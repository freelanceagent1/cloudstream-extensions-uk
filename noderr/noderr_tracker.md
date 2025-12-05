# Noderr - Status Map

**Purpose:** Tracks development status of all NodeIDs defined in `noderr_architecture.md`. Updated alongside specs to reflect implementation reality.

---
**Progress:** 3% (1/39 verified)
---

| Status | WorkGroupID | Node ID | Label | Dependencies | Logical Grouping | Spec Link | Classification | Notes / Issues |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 🟢 `[VERIFIED]` | | `INF_LocalJDKSetup` | Local JDK/JAVA_HOME setup | - | Infrastructure | [Spec](noderr/specs/INF_LocalJDKSetup.md) | Critical | JDK 17 configured (ms-17.0.17); Gradle wrapper working |
| ⚪️ `[TODO]` | | `CONFIG_GradleRoot` | Gradle root config | INF_LocalJDKSetup | Build System | [Spec](noderr/specs/CONFIG_GradleRoot.md) | Standard | Shared build config ready; blocked on local JDK to run |
| ⚪️ `[TODO]` | | `PIPE_CI_Build` | GitHub Actions build/publish | CONFIG_GradleRoot | Build System | [Spec](noderr/specs/PIPE_CI_Build.md) | Standard | CI builds configured; add smoke tests before publish |
| ⚪️ `[TODO]` | | `DATA_RepoManifest` | Repository manifest | PIPE_CI_Build | Distribution | [Spec](noderr/specs/DATA_RepoManifest.md) | Standard | Points to builds/plugins.json; ensure URL stays current |
| ⚪️ `[TODO]` | | `TEST_ProviderSmokeSuite` | Provider smoke tests | INF_LocalJDKSetup, CONFIG_GradleRoot, PIPE_CI_Build | Quality | [Spec](noderr/specs/TEST_ProviderSmokeSuite.md) | Critical | Missing automated tests for provider flows |
| ⚪️ `[TODO]` | | `EXT_ContentSources` | External content sites | - | External | [Spec](noderr/specs/EXT_ContentSources.md) | Complex | Availability and HTML structure may change; monitor mirrors |
| ⚪️ `[TODO]` | | `UI_CloudstreamApp` | Cloudstream client app | DATA_RepoManifest | Client | [Spec](noderr/specs/UI_CloudstreamApp.md) | Standard | External upstream app; ensure repo compatibility |
| ⚪️ `[TODO]` | | `API_RemoteSettings` | Remote settings endpoint | CONFIG_GradleRoot | HdRezka Core | [Spec](noderr/specs/API_RemoteSettings.md) | Standard | Fetch UA/appCode/mirrors/trash codes with English bias |
| ⚪️ `[TODO]` | | `SVC_RemoteSettingsCache` | Remote settings cache | API_RemoteSettings | HdRezka Core | [Spec](noderr/specs/SVC_RemoteSettingsCache.md) | Standard | Cache/refresh remote settings for downstream calls |
| ⚪️ `[TODO]` | | `UTIL_HdRezkaHeaders` | Header/UA utils | SVC_RemoteSettingsCache | HdRezka Core | [Spec](noderr/specs/UTIL_HdRezkaHeaders.md) | Standard | Build headers per mirror/request |
| ⚪️ `[TODO]` | | `UTIL_HdRezkaParsing` | Parsing utils | - | HdRezka Core | [Spec](noderr/specs/UTIL_HdRezkaParsing.md) | Standard | HTML parsing/selectors for listings/detail |
| ⚪️ `[TODO]` | | `DATA_UserPrefsHdRezka` | User prefs (language/translator) | - | HdRezka Core | [Spec](noderr/specs/DATA_UserPrefsHdRezka.md) | Standard | Optional preferences for mirror/translator |
| ⚪️ `[TODO]` | | `SVC_MirrorSelection` | Mirror selection (English-pref) | SVC_RemoteSettingsCache, DATA_UserPrefsHdRezka | HdRezka Core | [Spec](noderr/specs/SVC_MirrorSelection.md) | Complex | Choose/rotate mirrors with English preference |
| ⚪️ `[TODO]` | | `API_CatalogHdRezka` | Catalog sections | SVC_MirrorSelection, UTIL_HdRezkaHeaders | HdRezka Core | [Spec](noderr/specs/API_CatalogHdRezka.md) | Complex | Fetch listings per section with fallbacks |
| ⚪️ `[TODO]` | | `API_SearchHdRezka` | Search endpoint | SVC_MirrorSelection, UTIL_HdRezkaHeaders | HdRezka Core | [Spec](noderr/specs/API_SearchHdRezka.md) | Standard | Query search with mirrors |
| ⚪️ `[TODO]` | | `API_TitleDetailHdRezka` | Title detail | SVC_MirrorSelection, UTIL_HdRezkaHeaders, UTIL_HdRezkaParsing | HdRezka Core | [Spec](noderr/specs/API_TitleDetailHdRezka.md) | Complex | Parse metadata/posters/episodes/translators |
| ⚪️ `[TODO]` | | `API_PlaybackHdRezka` | Playback streams | SVC_MirrorSelection, UTIL_HdRezkaHeaders | HdRezka Core | [Spec](noderr/specs/API_PlaybackHdRezka.md) | Critical | cdn_movies/cdn_series POSTs, subtitles |
| ⚪️ `[TODO]` | | `SVC_PlaybackResolver` | Playback resolver | API_PlaybackHdRezka, API_TitleDetailHdRezka, UTIL_HdRezkaParsing, DATA_UserPrefsHdRezka | HdRezka Core | [Spec](noderr/specs/SVC_PlaybackResolver.md) | Critical | Decode trash codes, expand CDN hosts, build links |
| ⚪️ `[TODO]` | | `SVC_SubtitleResolver` | Subtitle resolver | API_PlaybackHdRezka, API_TitleDetailHdRezka | HdRezka Core | [Spec](noderr/specs/SVC_SubtitleResolver.md) | Standard | Attach subtitles to streams |
| ⚪️ `[TODO]` | | `SVC_AnimeONProvider` | AnimeON provider | CONFIG_GradleRoot, INF_LocalJDKSetup | Providers | [Spec](noderr/specs/SVC_AnimeONProvider.md) | Complex | Implemented; unverified locally due to JDK gap |
| ⚪️ `[TODO]` | | `SVC_AnimeUAProvider` | AnimeUA provider | CONFIG_GradleRoot, INF_LocalJDKSetup | Providers | [Spec](noderr/specs/SVC_AnimeUAProvider.md) | Complex | Implemented; needs build/test |
| ⚪️ `[TODO]` | | `SVC_AnitubeinuaProvider` | Anitubeinua provider | CONFIG_GradleRoot, INF_LocalJDKSetup | Providers | [Spec](noderr/specs/SVC_AnitubeinuaProvider.md) | Complex | Implemented; needs build/test |
| ⚪️ `[TODO]` | | `SVC_BambooUAProvider` | BambooUA provider | CONFIG_GradleRoot, INF_LocalJDKSetup | Providers | [Spec](noderr/specs/SVC_BambooUAProvider.md) | Complex | Implemented; needs build/test |
| ⚪️ `[TODO]` | | `SVC_CikavaIdeyaProvider` | CikavaIdeya provider | CONFIG_GradleRoot, INF_LocalJDKSetup | Providers | [Spec](noderr/specs/SVC_CikavaIdeyaProvider.md) | Standard | Implemented; needs build/test |
| ⚪️ `[TODO]` | | `SVC_EneyidaProvider` | Eneyida provider | CONFIG_GradleRoot, INF_LocalJDKSetup | Providers | [Spec](noderr/specs/SVC_EneyidaProvider.md) | Complex | Implemented; needs build/test |
| ⚪️ `[TODO]` | | `SVC_HdRezkaProvider` | HdRezka provider | CONFIG_GradleRoot, INF_LocalJDKSetup | Providers | [Spec](noderr/specs/SVC_HdRezkaProvider.md) | Complex | Implemented; needs build/test |
| ⚪️ `[TODO]` | | `SVC_HentaiUkrProvider` | HentaiUkr provider | CONFIG_GradleRoot, INF_LocalJDKSetup | Providers | [Spec](noderr/specs/SVC_HentaiUkrProvider.md) | Standard | Implemented; needs build/test |
| ⚪️ `[TODO]` | | `SVC_KinoTronProvider` | KinoTron provider | CONFIG_GradleRoot, INF_LocalJDKSetup | Providers | [Spec](noderr/specs/SVC_KinoTronProvider.md) | Standard | Implemented; needs build/test |
| ⚪️ `[TODO]` | | `SVC_KinoVezhaProvider` | KinoVezha provider | CONFIG_GradleRoot, INF_LocalJDKSetup | Providers | [Spec](noderr/specs/SVC_KinoVezhaProvider.md) | Complex | Implemented; needs build/test |
| ⚪️ `[TODO]` | | `SVC_KlonTVProvider` | KlonTV provider | CONFIG_GradleRoot, INF_LocalJDKSetup | Providers | [Spec](noderr/specs/SVC_KlonTVProvider.md) | Standard | Implemented; needs build/test |
| ⚪️ `[TODO]` | | `SVC_SerialnoProvider` | Serialno provider | CONFIG_GradleRoot, INF_LocalJDKSetup | Providers | [Spec](noderr/specs/SVC_SerialnoProvider.md) | Standard | Implemented; needs build/test |
| ⚪️ `[TODO]` | | `SVC_TeleportalProvider` | Teleportal provider | CONFIG_GradleRoot, INF_LocalJDKSetup | Providers | [Spec](noderr/specs/SVC_TeleportalProvider.md) | Standard | Implemented; needs build/test |
| ⚪️ `[TODO]` | | `SVC_UAFlixProvider` | UAFlix provider | CONFIG_GradleRoot, INF_LocalJDKSetup | Providers | [Spec](noderr/specs/SVC_UAFlixProvider.md) | Complex | Implemented; needs build/test |
| ⚪️ `[TODO]` | | `SVC_UakinoProvider` | Uakino provider | CONFIG_GradleRoot, INF_LocalJDKSetup | Providers | [Spec](noderr/specs/SVC_UakinoProvider.md) | Complex | Implemented; needs build/test |
| ⚪️ `[TODO]` | | `SVC_UASerialProvider` | UASerial provider | CONFIG_GradleRoot, INF_LocalJDKSetup | Providers | [Spec](noderr/specs/SVC_UASerialProvider.md) | Standard | Implemented; needs build/test |
| ⚪️ `[TODO]` | | `SVC_UASerialsProProvider` | UASerialsPro provider | CONFIG_GradleRoot, INF_LocalJDKSetup | Providers | [Spec](noderr/specs/SVC_UASerialsProProvider.md) | Standard | Implemented; needs build/test |
| ⚪️ `[TODO]` | | `SVC_UATuTFunProvider` | UATuTFun provider | CONFIG_GradleRoot, INF_LocalJDKSetup | Providers | [Spec](noderr/specs/SVC_UATuTFunProvider.md) | Standard | Implemented; needs build/test |
| ⚪️ `[TODO]` | | `SVC_UFDubProvider` | UFDub provider | CONFIG_GradleRoot, INF_LocalJDKSetup | Providers | [Spec](noderr/specs/SVC_UFDubProvider.md) | Standard | Implemented; needs build/test |
| ⚪️ `[TODO]` | | `SVC_UnimayProvider` | Unimay provider | CONFIG_GradleRoot, INF_LocalJDKSetup | Providers | [Spec](noderr/specs/SVC_UnimayProvider.md) | Standard | Implemented; needs build/test |

---
### Legend for Status:

* ⚪️ **`[TODO]`**: Task is defined and ready once dependencies met.
* 📝 **`[NEEDS_SPEC]`**: Node identified but missing spec (none outstanding).
* 🟡 **`[WIP]`**: Work in progress.
* 🟢 **`[VERIFIED]`**: Complete and validated (tests/docs).
* ❗ **`[ISSUE]`**: Blocker or missing prerequisite.
