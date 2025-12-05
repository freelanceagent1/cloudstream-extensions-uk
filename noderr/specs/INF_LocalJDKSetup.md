# INF_LocalJDKSetup.md

## Purpose
Prepare local development machines with a compatible JDK and JAVA_HOME so Gradle wrapper tasks can run for all provider modules.

## Current Implementation Status
✅ **IMPLEMENTED** - JDK 17 installed; JAVA_HOME set to ms-17.0.17 enabling Gradle wrapper.

## MVP Context
- **Required for Feature**: Local build/test of providers, dev feedback loop.
- **Priority**: High.
- **Blocking**: CONFIG_GradleRoot, all SVC_* providers, TEST_ProviderSmokeSuite.

## Implementation Details
- **Location**: Local developer setup (`environment_context.md`); JDK installed at `C:\Users\andre\.jdks\ms-17.0.17`.
- **Interfaces**: JAVA_HOME configured; `java` available in PATH; Gradle wrapper now runs.
- **Dependencies**: Microsoft OpenJDK 17.0.17; PowerShell session env vars.
- **Dependents**: Gradle wrapper (.\gradlew.bat), local test execution.

## Core Logic & Functionality
- Use installed JDK 17.
- Set JAVA_HOME and update PATH for the current shell.
- Verify `java -version` and `.\gradlew.bat --version` work.

## Implementation Requirements
- **Technology**: Windows 11 PowerShell scripts/commands.
- **Integration Points**: Gradle wrapper; potentially Android SDK tooling if needed.
- **Data Requirements**: None.
- **User Experience**: Clear one-time setup commands and verification steps.

## Interface Definition (As Implemented)
```powershell
# Use installed Microsoft OpenJDK 17
$Env:JAVA_HOME="C:\\Users\\andre\\.jdks\\ms-17.0.17"
$Env:Path = "$Env:JAVA_HOME\\bin;$Env:Path"
java -version
.\gradlew.bat --version
```

## ARC Verification Criteria

### Functional Criteria
- [ ] java -version returns expected version.
- [ ] .\gradlew.bat --version succeeds without JAVA_HOME errors.

### Input Validation Criteria
- [ ] JAVA_HOME path exists and points to installed JDK.

### Error Handling Criteria
- [ ] Clear guidance if gradle still cannot find java.

### Quality Criteria
- [ ] Documented in environment_context.md and README for future contributors.
- [ ] Reproducible setup steps for all developers.

## Implementation Notes
- Align JDK version with CI (17).
- Consider scripting env var updates for current session and permanent user profile.
- Re-run gradle tasks after setup to validate.
