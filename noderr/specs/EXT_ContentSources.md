# EXT_ContentSources.md

## Purpose
External Ukrainian streaming/content sites scraped by the provider modules to supply media metadata and playable links.

## Current Implementation Status
✅ **EXTERNAL DEPENDENCY** - Live websites consumed at runtime; not controlled by this repository.

## Implementation Details
- **Location**: External; referenced within provider modules (hardcoded base URLs, mirrors, balancers).
- **Current interfaces**: HTTP endpoints/pages parsed via jsoup/NiceHttp; responses vary by site (HTML pages, AJAX endpoints, CDN URLs).
- **Dependencies**: Site availability, mirror domains, user-agent requirements, and CDN hosts.
- **Dependents**: All provider modules; Cloudstream client runtime.

## Core Logic & Functionality
- Serve HTML/JSON content that providers parse into `SearchResponse`, `LoadResponse`, and `ExtractorLink` objects.
- Provide CDN links (often m3u8/mp4) and subtitles where available.

## Current Quality Assessment
- **Completeness**: Multiple sources covered; uptime varies per site.
- **Code Quality**: N/A (external).
- **Test Coverage**: N/A; needs smoke checks in our code.
- **Documentation**: Source descriptions captured in provider Gradle metadata.

## Technical Debt & Improvement Areas
- Track mirror lists and user-agent changes per site.
- Add monitoring/smoke tests to detect upstream structure changes.
- Centralize mirror configuration where possible.

## Interface Definition
```text
HTTP endpoints per source (HTML/JSON/CDN), accessed via NiceHttp/jsoup.
```

## ARC Verification Criteria

### Functional Criteria
- [ ] Upstream sites reachable from client network.
- [ ] CDN links resolve to playable media.

### Input Validation Criteria
- [ ] Handle missing/altered HTML fields without crashes.

### Error Handling Criteria
- [ ] Providers implement fallbacks for mirror downtime.

### Quality Criteria
- [ ] Maintain up-to-date mirror/UA lists within providers.
- [ ] Avoid excessive requests that could trigger rate limits.
