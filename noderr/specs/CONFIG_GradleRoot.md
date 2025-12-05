# CONFIG_GradleRoot.md

## Purpose
Central Gradle configuration for all Cloudstream provider modules, including plugin setup, shared dependencies, and Android build settings.

## Current Implementation Status
✅ **IMPLEMENTED** - Active in `build.gradle.kts` and `settings.gradle.kts`.

## Implementation Details
- **Location**: build.gradle.kts; settings.gradle.kts; gradle/libs.versions.toml
- **Current interfaces**: Gradle plugins `com.android.library`, `kotlin-android`, `com.lagradost.cloudstream3.gradle`; helper extension functions `cloudstream {}` and `android {}` applied to subprojects.
- **Dependencies**: cloudstream3 (pre-release), NiceHttp 0.4.11, jsoup 1.18.3, kotlin stdlib, kotlinx-coroutines-test, junit 4.13.2.
- **Dependents**: All provider modules; GitHub Actions build workflow; testReport aggregation task.

## Core Logic & Functionality
- Applies shared Android/Kotlin configuration (namespace, compileSdk 35, minSdk 21, JVM target 1.8) to every subproject with a `build.gradle.kts`.
- Configures Cloudstream metadata (repo URL, authors) and dependency resolution via version catalog.
- Registers `clean` and `testReport` tasks; sets `cloudstream` and implementation dependencies for providers.
- Handles task-based dependency selection (`useApk` logic) and compiler flags for Kotlin.

## Current Quality Assessment
- **Completeness**: Covers all modules via automatic inclusion; ready once JDK is available.
- **Code Quality**: Clear Gradle Kotlin DSL; leverages version catalog.
- **Test Coverage**: None (build logic only).
- **Documentation**: Implicit via Gradle scripts and comments.

## Technical Debt & Improvement Areas
- Add explicit Java toolchain/JDK enforcement to avoid local JAVA_HOME issues.
- Document common tasks (`make`, `makePluginsJson`) for developers.
- Consider enabling lint/static analysis tasks for providers.

## Interface Definition
```kotlin
// Gradle DSL in build.gradle.kts and settings.gradle.kts governing subprojects
```

## ARC Verification Criteria

### Functional Criteria
- [ ] Subprojects auto-include when a build.gradle.kts exists.
- [ ] Cloudstream plugin applies without errors after JDK is configured.

### Input Validation Criteria
- [ ] Repository URLs and author metadata resolve correctly.

### Error Handling Criteria
- [ ] Build fails fast with clear message if JDK/Android SDK missing.

### Quality Criteria
- [ ] Version catalog remains single source of truth for dependencies.
- [ ] Compiler flags remain consistent across modules.
