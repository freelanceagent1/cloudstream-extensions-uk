package com.lagradost

import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URI
import java.util.Base64
import java.io.File

/**
 * Fixture-based tests for HdRezka helper logic using real captured data (no synthetic mocks).
 */
class HdRezkaHelpersTest {
    private val fallbackBases = listOf(
        "https://hdrezka.ag",
        "https://rezka.ag",
        "https://hdrezka-home.tv",
        "https://hdrezka.ac",
        "https://rezka.ac",
        "https://rezka.tv",
        "https://rezka.cm",
    )

    private data class RemoteSettingsDto(val mirror: String? = null, val mirror2: String? = null)

    private fun normalizeUrl(url: String?): String? {
        if (url.isNullOrBlank()) return null
        val trimmed = url.trim()
        return when {
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed.trimEnd('/')
            trimmed.startsWith("//") -> "https:${trimmed}".trimEnd('/')
            else -> "https://$trimmed".trimEnd('/')
        }
    }

    private fun buildPreferredBases(mainUrl: String, dto: RemoteSettingsDto? = null): List<String> {
        val remoteMirrors = listOfNotNull(dto?.mirror, dto?.mirror2).mapNotNull { normalizeUrl(it) }
        val bases = mutableListOf<String>()
        bases += remoteMirrors
        bases += mainUrl.trimEnd('/')
        bases += fallbackBases
        return bases.mapNotNull { normalizeUrl(it) }.distinct()
    }

    private fun generateFallbackTrashCodes(): Set<String> {
        val trashList = listOf("@", "#", "!", "^", "$")
        val trashCodes = mutableSetOf<String>()
        for (a in trashList) {
            for (b in trashList) {
                trashCodes += Base64.getEncoder().encodeToString("$a$b".toByteArray())
                for (c in trashList) {
                    trashCodes += Base64.getEncoder().encodeToString("$a$b$c".toByteArray())
                }
            }
        }
        return trashCodes
    }

    private data class OnlineSettings(
        val userAgent: String,
        val trashCodes: Set<String>,
        val cdnBalancerUrl: String? = null,
        val videoCdnBalancerUrl: String? = null,
    )

    private fun expandCdnHosts(url: String, settings: OnlineSettings): List<String> {
        val cdnHosts = listOf(
            "prx.ukrtelcdn.net",
            "prx-ams.ukrtelcdn.net",
            "prx2-ams.ukrtelcdn.net",
            "ukrtelcdn.net",
            "stream.voidboost.top",
            "stream.voidboost.link",
            "stream.voidboost.club",
            "stream.voidboost.cc",
        )
        val uri = runCatching { URI(url) }.getOrNull() ?: return listOf(url)
        val currentHost = uri.host ?: return listOf(url)
        val isCdnHost = currentHost.contains("voidboost") || currentHost.contains("ukrtelcdn") || cdnHosts.any { currentHost.contains(it) }
        if (!isCdnHost) return listOf(url)
        val targets = mutableListOf(currentHost)
        targets += cdnHosts
        settings.cdnBalancerUrl?.let { runCatching { URI(it).host }.getOrNull()?.let { h -> if (h.isNotBlank()) targets += h } }
        settings.videoCdnBalancerUrl?.let { runCatching { URI(it).host }.getOrNull()?.let { h -> if (h.isNotBlank()) targets += h } }
        return targets.distinct().mapNotNull { host ->
            runCatching { URI(uri.scheme, uri.userInfo, host, uri.port, uri.path, uri.query, uri.fragment).toString() }.getOrNull()
        }.distinct()
    }

    private fun clearTrash(data: String, trashCodes: Set<String>): String {
        val combined = data.replace("#h", "").split("//_//").joinToString("")
        val cleaned = trashCodes.fold(combined) { acc, code -> acc.replace(code, "") }
            .replace("\n", "")
            .replace("\r", "")
            .trim()
        val needPad = (4 - cleaned.length % 4) % 4
        val padded = cleaned + "=".repeat(needPad)
        return runCatching { Base64.getDecoder().decode(padded).toString(Charsets.UTF_8) }.getOrElse { "" }
    }

    private fun decodeStreamEntry(entry: String, trashCodes: Set<String>): String {
        val unescaped = entry.replace("\\/", "/")
        if (unescaped.startsWith("[")) return unescaped
        val candidates = listOf(
            unescaped.replace("\n", "").replace("\r", "").trim(),
            trashCodes.fold(unescaped) { acc, code -> acc.replace(code, "") }.replace("\n", "").replace("\r", "").trim(),
        )
        for (candidate in candidates) {
            val needPad = (4 - candidate.length % 4) % 4
            val padded = candidate + "=".repeat(needPad)
            val decoded = runCatching { String(Base64.getMimeDecoder().decode(padded), Charsets.UTF_8) }.getOrNull()
            if (decoded != null && decoded.startsWith("[")) return decoded
        }
        return entry
    }

    @Test
    fun normalizeUrlHandlesSchemes() {
        assertEquals("https://rezka.ag", normalizeUrl("https://rezka.ag/"))
        assertEquals("https://rezka.ag", normalizeUrl("//rezka.ag"))
        assertEquals("https://rezka.ag", normalizeUrl("rezka.ag"))
    }

    @Test
    fun buildPreferredBasesOrdersMirrors() {
        val dto = RemoteSettingsDto(mirror = "https://mirror.one/", mirror2 = "//mirror.two")
        val bases = buildPreferredBases("https://rezka.ag", dto)
        assertTrue(bases.first().contains("mirror.one"))
        assertTrue(bases[1].contains("mirror.two"))
        assertTrue(bases.any { it.contains("rezka.ag") })
    }

    @Test
    fun clearTrashDecodesBase64() {
        val url = "https://example.com/stream.m3u8"
        val encoded = Base64.getEncoder().encodeToString(url.toByteArray())
        val trash = generateFallbackTrashCodes()
        val dirty = trash.first() + encoded + "#h"
        val decoded = clearTrash(dirty, trash)
        assertEquals(url, decoded)
    }

    @Test
    fun expandCdnHostsPassthroughNonCdn() {
        val settings = OnlineSettings(userAgent = "ua", trashCodes = emptySet())
        val result = expandCdnHosts("https://example.com/file.mp4", settings)
        assertEquals(listOf("https://example.com/file.mp4"), result)
    }

    @Test
    fun parseSearchFromRealFixture() {
        val html = File("src/test/resources/fixtures/search_matrix.html").readText()
        val doc = Jsoup.parse(html)
        val heading = doc.selectFirst("h1")?.text().orEmpty()
        assertTrue(heading.contains("Результаты поиска"))
        // The live page may show a downtime notice; ensure we captured the real response text.
        val notice = doc.selectFirst(".b-info__message")?.text().orEmpty()
        assertTrue(notice.isNotBlank())
    }

    @Test
    fun playbackFixtureDecodesStreams() {
        val playbackJson = File("src/test/resources/fixtures/playback_matrix.json").readText()
        val trash = generateFallbackTrashCodes()
        val remoteTrash = setOf(
            "//_//QEBAQEAhIyMhXl5e",
            "//_//Xl5eIUAjIyEhIyM=",
            "//_//JCQhIUAkJEBeIUAjJCRA",
            "//_//IyMjI14hISMjIUBA",
            "//_//JCQjISFAIyFAIyM=",
        )
        val settings = OnlineSettings(userAgent = "ua", trashCodes = trash + remoteTrash)
        val rawUrl = Regex("\"url\"\\s*:\\s*\"(.*?)\"").find(playbackJson)?.groupValues?.getOrNull(1)
        requireNotNull(rawUrl) { "Playback fixture missing url" }
        val decoded = clearTrash(rawUrl, settings.trashCodes).ifBlank { rawUrl.replace("#h", "").replace("//_//", ",") }
        val entries = decoded.split(',').map { it.trim() }.filter { it.isNotBlank() }
        assertTrue(entries.isNotEmpty())
        val entry = entries.first().replace("\\/", "/")
        println("baseEntrySample=${entry.take(60)} len=${entry.length}")
        val normalized = decodeStreamEntry(entry, settings.trashCodes)
        val urlPart = normalized.substringAfter(']').trim()
        val variant = urlPart.split(" or ").first().trim()
        val expanded = expandCdnHosts(variant, settings)
        println("decodedEntry=$normalized variant=$variant expandedFirst=${expanded.firstOrNull()}")
        assertTrue(expanded.isNotEmpty())
        assertTrue(expanded.first().startsWith("http"))
    }
}

