# API_SearchHdRezka.md

## Purpose
Search endpoint handling queries, headers, and mirror selection.

## Current Implementation Status
? **PLANNED** - Implementation pending to align with APK data flows and English-pref behavior.

## Implementation Details
- **Location**: To be implemented within HdRezkaProvider module (Kotlin). Files: HdRezkaProvider.kt and supporting utils/services.
- **Current interfaces**: To define (see Interface Definition).
- **Dependencies**: HdRezka remote settings endpoint, mirrors, CDN hosts; jsoup/NiceHttp; Cloudstream APIs.
- **Dependents**: HdRezka provider UI flows (catalog/search/detail/playback), playback resolver, Cloudstream client.

## Core Logic & Functionality
- Define request shapes, headers, and params based on APK flows.
- Prefer English/localized mirrors when available; rotate fallbacks on failure.
- Produce parsed outputs (listings/details/streams) for Cloudstream consumption.

## Current Quality Assessment
- **Completeness**: Not yet implemented explicitly.
- **Code Quality**: N/A.
- **Test Coverage**: N/A; will require fixtures and smoke tests.
- **Documentation**: This spec.

## Technical Debt & Improvement Areas
- Add fixture-based tests to avoid flaky live calls.
- Harden mirror/host rotation and error handling.
- Ensure selector resilience to site HTML changes.

## Interface Definition
`	ext
// Define methods/endpoints/headers/parsing for API_SearchHdRezka
`

## ARC Verification Criteria

### Functional Criteria
- [ ] Requests succeed against selected mirrors; outputs parsed correctly.
- [ ] Streams/posters/metadata present where applicable.

### Input Validation Criteria
- [ ] Handle missing/empty responses gracefully.
- [ ] Validate required params (translator/season/episode) before requests.

### Error Handling Criteria
- [ ] Fallback to alternate mirrors on failure.
- [ ] Bubble actionable errors without crashing provider.

### Quality Criteria
- [ ] Prefer English/localized variants when available.
- [ ] Parsing and host expansion remain performant and maintainable.
