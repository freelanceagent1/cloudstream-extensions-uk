# SVC_UASerialsProProvider.md

## Purpose
UASerialsPro - ??????? ??????????? ????? ?????? Cloudstream provider module delivering Ukrainian-localized streams for this source.

## Current Implementation Status
? **IMPLEMENTED** - Kotlin `MainAPI` provider module present.

## Implementation Details
- **Location**: UASerialsProProvider\src\main\kotlin\com\lagradost\UASerialsProProvider.kt; UASerialsProProvider\src\main\kotlin\com\lagradost\UASerialsProProviderPlugin.kt; UASerialsProProvider/build.gradle.kts
- **Current interfaces**: Cloudstream `MainAPI` overrides (mainPage/search/load/loadLinks) and plugin registration class.
- **Dependencies**: cloudstream3 (pre-release), NiceHttp, jsoup, Kotlin stdlib, kotlinx-coroutines-test (tests only).
- **Dependents**: Cloudstream client when plugin installed; CI build (`build.yml`) for packaging.

## Core Logic & Functionality
- Scrapes the source site for listings, search results, and stream links.
- Emits `SearchResponse`/`LoadResponse` objects and `ExtractorLink` entries with CDN/quality handling.
- Uses provider metadata (language, tvTypes, description) from `build.gradle.kts`.

## Current Quality Assessment
- **Completeness**: Implementation exists and matches source scope; no automated verification.
- **Code Quality**: Appears consistent with Cloudstream patterns; site-specific parsing may need upkeep.
- **Test Coverage**: No tests present.
- **Documentation**: Description present in module Gradle script; minimal inline comments.

## Technical Debt & Improvement Areas
- Add smoke tests for search/load and a basic link-resolution check.
- Monitor source site changes (HTML structure, mirror domains, user-agent requirements).
- Validate error handling and fallback strategies.

## Interface Definition
```kotlin
// MainAPI overrides for search/load/loadLinks; Cloudstream plugin entrypoint class.
```

## ARC Verification Criteria

### Functional Criteria
- [ ] Build module successfully with Gradle after JAVA_HOME is set.
- [ ] Search and load flows return playable links in Cloudstream.

### Input Validation Criteria
- [ ] Handle empty/invalid queries gracefully.
- [ ] Guard against null/blank fields in parsed HTML.

### Error Handling Criteria
- [ ] Survive upstream outages or mirror changes without crashing the app.
- [ ] Provide fallback mirrors where available.

### Quality Criteria
- [ ] Basic performance remains acceptable (fast parse, minimal calls).
- [ ] Maintainable selectors and clear metadata constants.
