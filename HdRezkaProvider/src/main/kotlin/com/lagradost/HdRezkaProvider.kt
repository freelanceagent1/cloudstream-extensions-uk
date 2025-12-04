package com.lagradost

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.AppUtils.parseJson
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.ExtractorLinkType
import com.lagradost.cloudstream3.utils.Qualities
import com.lagradost.cloudstream3.utils.newExtractorLink
import okhttp3.FormBody
import org.jsoup.nodes.Element
import java.net.URI
import java.util.Locale
import kotlin.math.min

class HdRezkaProvider : MainAPI() {
    override var mainUrl = "https://rezka.ag"
    override var name = "HdRezka"
    override val hasMainPage = true
    override var lang = "en"
    override val hasQuickSearch = true
    override val hasDownloadSupport = true
    override val supportedTypes = setOf(
        TvType.Movie,
        TvType.TvSeries,
        TvType.Anime,
        TvType.Cartoon,
        TvType.AsianDrama,
    )

    private val defaultUserAgent =
        "Mozilla/5.0 (Linux; Android 13; SM-G991B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"

    private val cdnHosts = listOf(
        "prx.ukrtelcdn.net",
        "prx-ams.ukrtelcdn.net",
        "prx2-ams.ukrtelcdn.net",
        "ukrtelcdn.net",
        "stream.voidboost.top",
        "stream.voidboost.link",
        "stream.voidboost.club",
        "stream.voidboost.cc",
    )

    private data class RemoteSettingsDto(
        val code: String? = null,
        val default_useragent: String? = null,
        val mirror: String? = null,
        val mirror2: String? = null,
        val trashCodes: List<String>? = null,
        val cdnBalancerUrl: String? = null,
        val videoCdnBalancerUrl: String? = null,
        val videoCdnMediaSelector: String? = null,
        val videoCdnDomain: String? = null,
    )

    private data class OnlineSettings(
        val appCode: String? = null,
        val userAgent: String,
        val trashCodes: Set<String>,
        val cdnBalancerUrl: String? = null,
        val videoCdnBalancerUrl: String? = null,
        val videoCdnMediaSelector: String? = null,
        val videoCdnDomain: String? = null,
    )

    private val remoteSettingsUrl = "https://hdrezka.nerdpol.ovh/topda_versiont.json"

    @Volatile
    private var cachedSettings: OnlineSettings? = null

    @Volatile
    private var appliedRemoteMainUrl = false

    override val mainPage = mainPageOf(
        "/films/" to "Movies",
        "/series/" to "Series",
        "/series/korean/" to "Korean Drama",
        "/cartoons/" to "Cartoons",
        "/anime/" to "Anime",
        "/show/" to "TV Shows",
        "/films/best/" to "Top Movies",
        "/series/best/" to "Top Series",
    )

    private fun generateFallbackTrashCodes(): Set<String> {
        val trashList = listOf("@", "#", "!", "^", "$")
        val trashCodes = mutableSetOf<String>()
        for (a in trashList) {
            for (b in trashList) {
                trashCodes += java.util.Base64.getEncoder().encodeToString("$a$b".toByteArray())
                for (c in trashList) {
                    trashCodes += java.util.Base64.getEncoder().encodeToString("$a$b$c".toByteArray())
                }
            }
        }
        return trashCodes
    }

    private fun applyRemoteMainUrl(dto: RemoteSettingsDto?) {
        if (appliedRemoteMainUrl) return
        val mirror = listOfNotNull(dto?.mirror, dto?.mirror2)
            .firstOrNull { !it.isNullOrBlank() }
            ?.trimEnd('/')
        if (!mirror.isNullOrBlank() && mirror != mainUrl) {
            mainUrl = mirror
        }
        appliedRemoteMainUrl = true
    }

    private suspend fun getSettings(): OnlineSettings {
        cachedSettings?.let { return it }
        val dto = runCatching { parseJson<RemoteSettingsDto>(app.get(remoteSettingsUrl).text) }.getOrNull()
        applyRemoteMainUrl(dto)

        val uaTemplate = dto?.default_useragent
        val resolvedUa = runCatching {
            if (uaTemplate?.contains("%s") == true) {
                String.format(Locale.US, uaTemplate, "Android", "Android")
            } else uaTemplate
        }.getOrNull()?.takeIf { it.isNotBlank() } ?: defaultUserAgent

        val trash = mutableSetOf<String>()
        trash += generateFallbackTrashCodes()
        dto?.trashCodes?.let { trash += it.filter { code -> code.isNotBlank() } }

        val settings = OnlineSettings(
            appCode = dto?.code,
            userAgent = resolvedUa,
            trashCodes = trash,
            cdnBalancerUrl = dto?.cdnBalancerUrl,
            videoCdnBalancerUrl = dto?.videoCdnBalancerUrl,
            videoCdnMediaSelector = dto?.videoCdnMediaSelector,
            videoCdnDomain = dto?.videoCdnDomain,
        )
        cachedSettings = settings
        return settings
    }

    private suspend fun baseHeaders(referer: String? = null, origin: String? = null): Map<String, String> {
        val settings = getSettings()
        return buildMap {
            put("User-Agent", settings.userAgent)
            put("X-Hdrezka-Android-App", "1")
            settings.appCode?.let { put("X-Hdrezka-Android-App-Version", it) }
            put("Accept", "*/*")
            put("Accept-Language", "en-US,en;q=0.9")
            referer?.let { put("Referer", it) }
            origin?.let { put("Origin", it) }
        }
    }

    private fun cdnHostCandidates(settings: OnlineSettings): List<String> {
        val hosts = mutableListOf<String>()
        hosts += cdnHosts
        settings.cdnBalancerUrl?.let { runCatching { URI(it).host }.getOrNull()?.let { h -> if (h.isNotBlank()) hosts += h } }
        settings.videoCdnBalancerUrl?.let { runCatching { URI(it).host }.getOrNull()?.let { h -> if (h.isNotBlank()) hosts += h } }
        return hosts.distinct()
    }

    private fun expandCdnHosts(url: String, settings: OnlineSettings): List<String> {
        val uri = runCatching { URI(url) }.getOrNull() ?: return listOf(url)
        val currentHost = uri.host ?: return listOf(url)
        val isCdnHost =
            currentHost.contains("voidboost") || currentHost.contains("ukrtelcdn") || cdnHosts.any { currentHost.contains(it) }
        if (!isCdnHost) return listOf(url)
        val targets = mutableListOf(currentHost)
        targets += cdnHostCandidates(settings)
        return targets.distinct().mapNotNull { host ->
            runCatching { URI(uri.scheme, uri.userInfo, host, uri.port, uri.path, uri.query, uri.fragment).toString() }.getOrNull()
        }.distinct()
    }

    private fun Element.posterFromImg(): String? {
        val imgEl = selectFirst("img") ?: return null
        val candidates = listOf("data-original", "data-src", "data-lazy", "data-preview", "src", "data-srcset")
        for (attr in candidates) {
            val raw = imgEl.attr(attr)
            val value = raw.substringBefore(" ").substringBefore(",").trim()
            if (value.isNotBlank()) return fixUrlNull(value)
        }
        return null
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val link = selectFirst(".b-content__inline_item-link a") ?: return null
        val href = fixUrl(link.attr("href"))
        val title = link.text().trim()
        val img = posterFromImg()
        val type = when {
            href.contains("/anime/") -> TvType.Anime
            href.contains("/cartoons/") -> TvType.Cartoon
            href.contains("/series/") -> TvType.TvSeries
            else -> TvType.Movie
        }
        return newMovieSearchResponse(title, href, type) {
            this.posterUrl = fixUrlNull(img)
        }
    }

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val url = if (request.data.endsWith("/")) "${mainUrl}${request.data}page/$page/" else "${mainUrl}${request.data}?page=$page"
        val doc = app.get(url, headers = baseHeaders(referer = mainUrl, origin = mainUrl)).document
        val items = doc.select(".b-content__inline_item").mapNotNull { it.toSearchResult() }
        return newHomePageResponse(request.name, items, hasNext = items.isNotEmpty())
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val url = "$mainUrl/search/?do=search&subaction=search&q=${query.trim()}"
        val doc = app.get(url, headers = baseHeaders(referer = mainUrl, origin = mainUrl)).document
        return doc.select(".b-content__inline_item").mapNotNull { it.toSearchResult() }
    }

    override suspend fun quickSearch(query: String): List<SearchResponse> = search(query)

    override suspend fun load(url: String): LoadResponse {
        val doc = app.get(url, headers = baseHeaders(referer = url, origin = mainUrl)).document
        val title = doc.selectFirst("h1")?.text()?.trim().orEmpty()
        val poster = doc.selectFirst("meta[property=og:image]")?.attr("content")
            ?: doc.selectFirst(".b-post__infopic img")?.let { img ->
                val raw = listOf("data-original", "data-src", "data-lazy", "data-preview", "src", "data-srcset")
                    .firstNotNullOfOrNull { attr -> img.attr(attr).substringBefore(" ").substringBefore(",").trim().takeIf { it.isNotBlank() } }
                raw
            }
        val description = doc.selectFirst("meta[name=description]")?.attr("content")
        val year = doc.selectFirst("meta[property=og:updated_time]")?.attr("content")?.take(4)?.toIntOrNull()
        val type = when {
            url.contains("/anime/") -> TvType.Anime
            url.contains("/cartoons/") -> TvType.Cartoon
            url.contains("/series/") -> TvType.TvSeries
            else -> TvType.Movie
        }

        val html = doc.html()
        val embeddedStreams =
            Regex("initCDN(?:Series|Movies)Events\\([^)]*?\\{\\\"id\\\":\\\"cdnplayer\\\",\\\"streams\\\":\\\"(.*?)\\\"", RegexOption.DOT_MATCHES_ALL)
                .find(html)?.groupValues?.getOrNull(1)
        val translatorId = Regex("initCDN(?:Series|Movies)Events\\((\\d+),\\s*(\\d+)")
            .find(html)?.groupValues?.getOrNull(2)?.toIntOrNull()
            ?: doc.selectFirst(".b-translator__item.active")?.attr("data-translator_id")?.toIntOrNull()
            ?: doc.selectFirst(".b-translator__item")?.attr("data-translator_id")?.toIntOrNull()
        val itemId = Regex("initCDN(?:Series|Movies)Events\\((\\d+),").find(html)?.groupValues?.getOrNull(1)?.toIntOrNull()
            ?: doc.selectFirst("#ctrl_favs")?.attr("data-id")?.toIntOrNull()
            ?: Regex("data-id=['\"](\\d+)['\"]").find(html)?.groupValues?.getOrNull(1)?.toIntOrNull()

        val safeItemId = itemId ?: 0

        val isShow = type == TvType.TvSeries || type == TvType.Anime || url.contains("/series/")

        return if (isShow) {
            val episodes = mutableListOf<Episode>()
            val eps = doc.select(".b-simple_episode__item")
            eps.forEach { el ->
                val seasonId = el.attr("data-season_id").toIntOrNull() ?: 1
                val episodeId = el.attr("data-episode_id").toIntOrNull() ?: 1
                val name = el.text().trim()
                episodes.add(
                    newEpisode(url) {
                        this.name = name
                        this.season = seasonId
                        this.episode = episodeId
                        this.data = listOf(
                            safeItemId,
                            translatorId ?: 0,
                            seasonId,
                            episodeId,
                            false,
                            url, // keep referer for CDN call
                        ).joinToString("|")
                    }
                )
            }
            newTvSeriesLoadResponse(title, url, type, episodes) {
                posterUrl = fixUrlNull(poster)
                plot = description
                this.year = year
            }
        } else {
            newMovieLoadResponse(
                title,
                url,
                type,
                if (embeddedStreams != null) {
                    listOf("embed", embeddedStreams, url).joinToString("|")
                } else {
                    listOf(
                        safeItemId,
                        translatorId ?: 0,
                        0,
                        0,
                        true,
                        url, // keep referer for CDN call
                    ).joinToString("|")
                }
            ) {
                posterUrl = fixUrlNull(poster)
                plot = description
                this.year = year
            }
        }
    }

    private fun clearTrash(data: String, trashCodes: Set<String>): String {
        val combined = data.replace("#h", "").split("//_//").joinToString("")
        val cleaned = trashCodes.fold(combined) { acc, code -> acc.replace(code, "") }
            .replace("\n", "")
            .replace("\r", "")
            .trim()
        val needPad = (4 - cleaned.length % 4) % 4
        val padded = cleaned + "=".repeat(needPad)
        return runCatching { java.util.Base64.getDecoder().decode(padded).toString(Charsets.UTF_8) }.getOrElse { "" }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val settings = getSettings()
        val parts = data.split("|")
        if (parts.firstOrNull() == "embed") {
            val streamsRaw = parts.getOrNull(1) ?: return false
            val refererUrl = parts.getOrNull(2) ?: mainUrl
            val decoded = clearTrash(streamsRaw, settings.trashCodes).ifBlank {
                streamsRaw.replace("#h", "").replace("//_//", ",")
            }
            val entries = decoded.split(',').map { it.trim() }.filter { it.isNotBlank() }
            if (entries.isEmpty()) return false
            entries.forEach { entry ->
                val quality = Regex("\\[(\\d+)p").find(entry)?.groupValues?.getOrNull(1)?.toIntOrNull()
                val urlPart = entry.substringAfter(']').trim()
                val variants = urlPart.split(" or ").map { it.trim() }
                variants.forEach { variant ->
                    val isHls = variant.contains(":hls:")
                    if (isHls) {
                        val vparts = variant.split(":hls:")
                        val baseRaw = vparts.getOrNull(0)?.trim().orEmpty()
                        val tail = vparts.getOrNull(1)?.trim().orEmpty()
                        val bases = expandCdnHosts(baseRaw, settings)
                        bases.forEach { base ->
                            callback(
                                newExtractorLink(
                                    this.name,
                                    this.name,
                                    base,
                                    ExtractorLinkType.VIDEO
                                ) {
                                    referer = refererUrl
                                    this.quality = quality ?: Qualities.Unknown.value
                                }
                            )
                            if (tail.isNotBlank()) {
                                val hlsBase = if (base.endsWith(".mp4")) base.removeSuffix(".mp4") else base
                                val hlsUrl = listOf(hlsBase, tail.removePrefix("/")).joinToString("/")
                                callback(
                                    newExtractorLink(
                                        this.name,
                                        this.name,
                                        hlsUrl,
                                        ExtractorLinkType.M3U8
                                    ) {
                                        referer = refererUrl
                                        this.quality = quality ?: Qualities.Unknown.value
                                    }
                                )
                            }
                        }
                    } else {
                        expandCdnHosts(variant, settings).forEach { url ->
                            callback(
                                newExtractorLink(
                                    this.name,
                                    this.name,
                                    url,
                                    if (url.contains(".m3u8")) ExtractorLinkType.M3U8 else ExtractorLinkType.VIDEO
                                ) {
                                    referer = refererUrl
                                    this.quality = quality ?: Qualities.Unknown.value
                                }
                            )
                        }
                    }
                }
            }
            return true
        }

        val itemId = parts.getOrNull(0)?.toIntOrNull() ?: return false
        val translatorId = parts.getOrNull(1)?.toIntOrNull() ?: 0
        val seasonId = parts.getOrNull(2)?.toIntOrNull() ?: 0
        val episodeId = parts.getOrNull(3)?.toIntOrNull() ?: 0
        val isMovie = parts.getOrNull(4)?.toBooleanStrictOrNull() ?: true
        val refererUrl = parts.getOrNull(5) ?: mainUrl

        val action = if (isMovie) "get_movie" else "get_stream"
        val base = runCatching {
            val uri = URI(refererUrl)
            "${uri.scheme}://${uri.host}"
        }.getOrNull() ?: mainUrl
        val endpoint = if (isMovie) "$base/ajax/get_cdn_movies/" else "$base/ajax/get_cdn_series/"
        val bodyBuilder = FormBody.Builder()
            .add("id", itemId.toString())
            .add("translator_id", translatorId.toString())
            .add("is_camrip", "0")
            .add("is_ads", "0")
            .add("is_director", "0")
            .add("favs", "0")
            .add("action", action)
        if (!isMovie) {
            bodyBuilder.add("season", seasonId.toString())
            bodyBuilder.add("episode", episodeId.toString())
        }

        val json = app.post(
            endpoint,
            requestBody = bodyBuilder.build(),
            headers = baseHeaders(referer = refererUrl, origin = base) + mapOf(
                "X-Requested-With" to "XMLHttpRequest",
            )
        ).text

        val parsed = parseJson<HdRezkaCdnResponse>(json)
        val rawUrl = parsed.url ?: return false
        val decoded = clearTrash(rawUrl, settings.trashCodes)
        val candidateList = if (decoded.isNotBlank()) decoded else rawUrl.replace("#h", "").replace("//_//", ",")
        val entries = candidateList.split(',').map { it.trim() }.filter { it.isNotBlank() }
        if (entries.isEmpty()) return false
        for (entry in entries) {
            val quality = Regex("\\[(\\d+)p").find(entry)?.groupValues?.getOrNull(1)?.toIntOrNull()
            val urlPart = entry.substringAfter(']').trim()
            val variants = urlPart.split(" or ").map { it.trim() }
            variants.forEach { variant ->
                val isHls = variant.contains(":hls:")
                if (isHls) {
                    val parts = variant.split(":hls:")
                    val baseRaw = parts.getOrNull(0)?.trim().orEmpty()
                    val tail = parts.getOrNull(1)?.trim().orEmpty()
                    val bases = expandCdnHosts(baseRaw, settings)
                    bases.forEach { base ->
                        if (base.isNotBlank() && tail.isNotBlank()) {
                            val hlsBase = if (base.endsWith(".mp4")) base.removeSuffix(".mp4") else base
                            val hlsUrl = listOf(hlsBase, tail.removePrefix("/")).joinToString("/")
                            callback(
                                newExtractorLink(
                                    this.name,
                                    this.name,
                                    base,
                                    ExtractorLinkType.VIDEO
                                ) {
                                    referer = refererUrl
                                    this.quality = quality ?: Qualities.Unknown.value
                                }
                            )
                            callback(
                                newExtractorLink(
                                    this.name,
                                    this.name,
                                    hlsUrl,
                                    ExtractorLinkType.M3U8
                                ) {
                                    referer = refererUrl
                                    this.quality = quality ?: Qualities.Unknown.value
                                }
                            )
                        }
                    }
                } else {
                    expandCdnHosts(variant, settings).forEach { url ->
                        callback(
                            newExtractorLink(
                                this.name,
                                this.name,
                                url,
                                if (url.contains(".m3u8")) ExtractorLinkType.M3U8 else ExtractorLinkType.VIDEO
                            ) {
                                referer = refererUrl
                                this.quality = quality ?: Qualities.Unknown.value
                            }
                        )
                    }
                }
            }
        }

        parsed.subtitle?.takeIf { it.isNotBlank() }?.let { sub ->
            subtitleCallback(SubtitleFile("Unknown", sub))
        }
        return true
    }
}

private data class HdRezkaCdnResponse(
    val success: Boolean?,
    val message: String?,
    val premium_content: Int?,
    val url: String?,
    val subtitle: String? = null,
)
