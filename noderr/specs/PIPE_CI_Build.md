# PIPE_CI_Build.md

## Purpose
GitHub Actions workflow that builds all provider modules, generates plugins.json, and publishes artifacts to the `builds` branch for distribution.

## Current Implementation Status
✅ **IMPLEMENTED** - `.github/workflows/build.yml` present and configured.

## Implementation Details
- **Location**: .github/workflows/build.yml
- **Current interfaces**: Triggered on pushes to master/main (excluding *.md); steps include checkout (source and builds branch), clean old builds, setup JDK 17, setup Android SDK, run gradle `make` and `makePluginsJson`, copy outputs to builds branch, force-push amended commit.
- **Dependencies**: GitHub-hosted runners (ubuntu-latest), actions/checkout, actions/setup-java@v4, android-actions/setup-android@v2, project Gradle wrapper.
- **Dependents**: repo.json consumers; Cloudstream clients pulling plugins.json from builds branch.

## Core Logic & Functionality
- Uses two checkouts to combine source and artifacts branch.
- Ensures JDK and Android SDK are available in CI so gradle tasks succeed.
- Packages `.cs3` artifacts and plugins.json into builds branch and force-pushes them.

## Current Quality Assessment
- **Completeness**: CI path is defined end-to-end.
- **Code Quality**: Workflow is concise; uses caching for Gradle via setup-java.
- **Test Coverage**: No tests executed (only build). 
- **Documentation**: Minimal comments; build.yml is straightforward.

## Technical Debt & Improvement Areas
- Add smoke tests before packaging.
- Consider artifact retention instead of force-pushing if history is needed.
- Add branch protection or release tagging to track builds.

## Interface Definition
```yaml
# See .github/workflows/build.yml
# jobs.build.steps run gradle make/makePluginsJson and publish to builds branch
```

## ARC Verification Criteria

### Functional Criteria
- [ ] Workflow completes successfully on push to main/master.
- [ ] Gradle tasks produce .cs3 artifacts and plugins.json.

### Input Validation Criteria
- [ ] Fails clearly if secrets or SDKs are missing.

### Error Handling Criteria
- [ ] Build step errors surface in action logs with non-zero exit codes.

### Quality Criteria
- [ ] Future smoke tests inserted before publish step.
- [ ] JDK/Android SDK versions remain pinned and cached.
