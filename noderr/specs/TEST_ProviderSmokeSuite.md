# TEST_ProviderSmokeSuite.md

## Purpose
Automated smoke tests to validate that each provider builds and basic search/load flows succeed, catching upstream site or parsing regressions before release.

## Current Implementation Status
⚪ **PLANNED** - No automated tests exist; required for MVP reliability.

## MVP Context
- **Required for Feature**: Stability of provider outputs before publishing.
- **Priority**: High.
- **Blocking**: Release confidence for all SVC_* providers and CI pipeline.

## Planned Implementation Details
- **Intended Location**: Module-specific tests under `<Provider>/src/test/java|kotlin/` plus optional shared test utilities.
- **Required Interfaces**: JUnit 4.13.2 (already declared), kotlinx-coroutines-test for async operations, ability to mock HTTP (or run against known fixtures).
- **Dependencies**: CONFIG_GradleRoot, INF_LocalJDKSetup, network fixtures/mocks, optionally recorded HTML.
- **Dependents**: PIPE_CI_Build (to run tests pre-publish), developer local workflows.

## Core Logic & Functionality Requirements
- Build provider modules and assert search returns non-empty results for a known query (mocked or fixture-based to avoid hitting live sites every run).
- Validate load/link parsing for a sample item (fixture-based) and ensure ExtractorLinks are emitted.
- Fail fast when parsing selectors or mirror logic changes.

## Implementation Requirements
- **Technology**: Kotlin + JUnit; consider MockWebServer or recorded HTML fixtures.
- **Integration Points**: Gradle `testReleaseUnitTest`; optionally dedicated `smoke` task.
- **Data Requirements**: Stored fixtures representing typical HTML/API responses per site.
- **User Experience**: One-command test execution locally and in CI.

## Interface Definition (Planned)
```kotlin
class HdRezkaSmokeTest {
    @Test
    fun `search returns results`() { /* fixture-based assertion */ }
    @Test
    fun `loadLinks emits streams`() { /* verify extractor links */ }
}
```

## ARC Verification Criteria

### Functional Criteria
- [ ] Tests run via gradle and fail on parsing/link-generation regressions.
- [ ] Coverage includes at least one happy-path search/load per provider.

### Input Validation Criteria
- [ ] Tests include malformed/empty responses to ensure graceful handling.

### Error Handling Criteria
- [ ] Failures provide actionable messages (which provider, which selector, which fixture).

### Quality Criteria
- [ ] Tests remain deterministic (fixture-based) to avoid flaky live calls.
- [ ] Suites run in CI before publishing artifacts.

## Implementation Notes
- Start with top-traffic providers (HdRezka, Eneyida, UAFlix) then expand.
- Consider shared fixtures/utilities to avoid duplication across modules.
- Gate CI publish on smoke test success.
