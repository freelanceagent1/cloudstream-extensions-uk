# Noderr - Architectural Flowchart

**Purpose:** This document contains the Mermaid flowchart defining the architecture, components (NodeIDs), and their primary interactions for this project. This visual map is the source of truth for all implementable components tracked in `noderr_tracker.md`.

---

```mermaid
graph TD
    %% =================================================================
    %%  Legend - Defines the shapes and conventions used in this diagram
    %% =================================================================
    subgraph Legend
        direction LR
        L_IDConv(NodeID Convention: TYPE_Name)
        L_Proc([Process/Backend Logic])
        L_UI[/UI Component/]
        L_Decision{Decision Point}
        L_DB[(Database/Data Store)]
        L_ExtAPI{{External API}}
    end

    %% =================================================================
    %%  Cloudstream UA Providers - Existing + Missing for MVP
    %% =================================================================

    subgraph "Build & Distribution"
        direction LR
        INF_LocalJDKSetup([Process: Local JDK Setup]) %% Missing - needed for MVP
        CONFIG_GradleRoot([Process: Gradle Root Config])
        PIPE_CI_Build([Process: GitHub Actions Build])
        DATA_RepoManifest[(Repo Manifest)]
    end

    subgraph "Cloudstream Runtime"
        direction TB
        UI_CloudstreamApp[/Cloudstream App (Client)/]
        EXT_ContentSources{{Ukrainian Streaming Sites}}
    end

    subgraph "HdRezka Content & Playback"
        direction TB
        API_RemoteSettings([Process: API Remote Settings])
        API_CatalogHdRezka([Process: API Catalog (Sections)])
        API_SearchHdRezka([Process: API Search])
        API_TitleDetailHdRezka([Process: API Title Detail])
        API_PlaybackHdRezka([Process: API Playback Streams])
        SVC_RemoteSettingsCache([Process: Remote Settings Cache])
        SVC_MirrorSelection([Process: Mirror Selection w/ English Bias])
        SVC_PlaybackResolver([Process: Playback Resolver])
        SVC_SubtitleResolver([Process: Subtitle Resolver])
        UTIL_HdRezkaParsing([Process: Parsing Utils])
        UTIL_HdRezkaHeaders([Process: Header/UA Utils])
        DATA_UserPrefsHdRezka[(Data: User Prefs - Language/Translator)]
    end

    subgraph "Provider Modules"
        direction TB
        SVC_AnimeONProvider[Process: AnimeON Provider]
        SVC_AnimeUAProvider[Process: AnimeUA Provider]
        SVC_AnitubeinuaProvider[Process: Anitubeinua Provider]
        SVC_BambooUAProvider[Process: BambooUA Provider]
        SVC_CikavaIdeyaProvider[Process: CikavaIdeya Provider]
        SVC_EneyidaProvider[Process: Eneyida Provider]
        SVC_HdRezkaProvider[Process: HdRezka Provider]
        SVC_HentaiUkrProvider[Process: HentaiUkr Provider]
        SVC_KinoTronProvider[Process: KinoTron Provider]
        SVC_KinoVezhaProvider[Process: KinoVezha Provider]
        SVC_KlonTVProvider[Process: KlonTV Provider]
        SVC_SerialnoProvider[Process: Serialno Provider]
        SVC_TeleportalProvider[Process: Teleportal Provider]
        SVC_UAFlixProvider[Process: UAFlix Provider]
        SVC_UakinoProvider[Process: Uakino Provider]
        SVC_UASerialProvider[Process: UASerial Provider]
        SVC_UASerialsProProvider[Process: UASerialsPro Provider]
        SVC_UATuTFunProvider[Process: UATuTFun Provider]
        SVC_UFDubProvider[Process: UFDub Provider]
        SVC_UnimayProvider[Process: Unimay Provider]
    end

    TEST_ProviderSmokeSuite([Process: Provider Smoke Tests]) %% Missing - needed for MVP

    %% Flows
    INF_LocalJDKSetup --> CONFIG_GradleRoot
    CONFIG_GradleRoot --> PIPE_CI_Build
    PIPE_CI_Build --> DATA_RepoManifest
    DATA_RepoManifest --> UI_CloudstreamApp

    CONFIG_GradleRoot --> SVC_AnimeONProvider
    CONFIG_GradleRoot --> SVC_AnimeUAProvider
    CONFIG_GradleRoot --> SVC_AnitubeinuaProvider
    CONFIG_GradleRoot --> SVC_BambooUAProvider
    CONFIG_GradleRoot --> SVC_CikavaIdeyaProvider
    CONFIG_GradleRoot --> SVC_EneyidaProvider
    CONFIG_GradleRoot --> SVC_HdRezkaProvider
    CONFIG_GradleRoot --> SVC_HentaiUkrProvider
    CONFIG_GradleRoot --> SVC_KinoTronProvider
    CONFIG_GradleRoot --> SVC_KinoVezhaProvider
    CONFIG_GradleRoot --> SVC_KlonTVProvider
    CONFIG_GradleRoot --> SVC_SerialnoProvider
    CONFIG_GradleRoot --> SVC_TeleportalProvider
    CONFIG_GradleRoot --> SVC_UAFlixProvider
    CONFIG_GradleRoot --> SVC_UakinoProvider
    CONFIG_GradleRoot --> SVC_UASerialProvider
    CONFIG_GradleRoot --> SVC_UASerialsProProvider
    CONFIG_GradleRoot --> SVC_UATuTFunProvider
    CONFIG_GradleRoot --> SVC_UFDubProvider
    CONFIG_GradleRoot --> SVC_UnimayProvider

    UI_CloudstreamApp --> SVC_AnimeONProvider
    UI_CloudstreamApp --> SVC_AnimeUAProvider
    UI_CloudstreamApp --> SVC_AnitubeinuaProvider
    UI_CloudstreamApp --> SVC_BambooUAProvider
    UI_CloudstreamApp --> SVC_CikavaIdeyaProvider
    UI_CloudstreamApp --> SVC_EneyidaProvider
    UI_CloudstreamApp --> SVC_HdRezkaProvider
    UI_CloudstreamApp --> SVC_HentaiUkrProvider
    UI_CloudstreamApp --> SVC_KinoTronProvider
    UI_CloudstreamApp --> SVC_KinoVezhaProvider
    UI_CloudstreamApp --> SVC_KlonTVProvider
    UI_CloudstreamApp --> SVC_SerialnoProvider
    UI_CloudstreamApp --> SVC_TeleportalProvider
    UI_CloudstreamApp --> SVC_UAFlixProvider
    UI_CloudstreamApp --> SVC_UakinoProvider
    UI_CloudstreamApp --> SVC_UASerialProvider
    UI_CloudstreamApp --> SVC_UASerialsProProvider
    UI_CloudstreamApp --> SVC_UATuTFunProvider
    UI_CloudstreamApp --> SVC_UFDubProvider
    UI_CloudstreamApp --> SVC_UnimayProvider

    SVC_AnimeONProvider --> EXT_ContentSources
    SVC_AnimeUAProvider --> EXT_ContentSources
    SVC_AnitubeinuaProvider --> EXT_ContentSources
    SVC_BambooUAProvider --> EXT_ContentSources
    SVC_CikavaIdeyaProvider --> EXT_ContentSources
    SVC_EneyidaProvider --> EXT_ContentSources
    SVC_HdRezkaProvider --> EXT_ContentSources
    SVC_HentaiUkrProvider --> EXT_ContentSources
    SVC_KinoTronProvider --> EXT_ContentSources
    SVC_KinoVezhaProvider --> EXT_ContentSources
    SVC_KlonTVProvider --> EXT_ContentSources
    SVC_SerialnoProvider --> EXT_ContentSources
    SVC_TeleportalProvider --> EXT_ContentSources
    SVC_UAFlixProvider --> EXT_ContentSources
    SVC_UakinoProvider --> EXT_ContentSources
    SVC_UASerialProvider --> EXT_ContentSources
    SVC_UASerialsProProvider --> EXT_ContentSources
    SVC_UATuTFunProvider --> EXT_ContentSources
    SVC_UFDubProvider --> EXT_ContentSources
    SVC_UnimayProvider --> EXT_ContentSources

    %% HdRezka content/streams flow
    SVC_HdRezkaProvider --> SVC_RemoteSettingsCache
    SVC_RemoteSettingsCache --> API_RemoteSettings
    SVC_RemoteSettingsCache --> SVC_MirrorSelection
    SVC_MirrorSelection --> API_CatalogHdRezka
    SVC_MirrorSelection --> API_SearchHdRezka
    SVC_MirrorSelection --> API_TitleDetailHdRezka
    SVC_MirrorSelection --> API_PlaybackHdRezka
    DATA_UserPrefsHdRezka --> SVC_MirrorSelection
    DATA_UserPrefsHdRezka --> SVC_PlaybackResolver
    UTIL_HdRezkaHeaders --> API_CatalogHdRezka
    UTIL_HdRezkaHeaders --> API_SearchHdRezka
    UTIL_HdRezkaHeaders --> API_TitleDetailHdRezka
    UTIL_HdRezkaHeaders --> API_PlaybackHdRezka
    UTIL_HdRezkaParsing --> API_CatalogHdRezka
    UTIL_HdRezkaParsing --> API_TitleDetailHdRezka
    API_CatalogHdRezka --> SVC_PlaybackResolver
    API_SearchHdRezka --> SVC_PlaybackResolver
    API_TitleDetailHdRezka --> SVC_PlaybackResolver
    API_PlaybackHdRezka --> SVC_PlaybackResolver
    SVC_PlaybackResolver --> SVC_SubtitleResolver
    SVC_PlaybackResolver --> UI_CloudstreamApp
    SVC_SubtitleResolver --> UI_CloudstreamApp

    CONFIG_GradleRoot --> TEST_ProviderSmokeSuite
    TEST_ProviderSmokeSuite --> PIPE_CI_Build
    TEST_ProviderSmokeSuite --> UI_CloudstreamApp
```

---

Architecture updated to represent the current Cloudstream UA provider suite and missing MVP enablers (local JDK setup and provider smoke tests).
