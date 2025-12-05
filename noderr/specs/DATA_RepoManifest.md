# DATA_RepoManifest.md

## Purpose
Repository manifest pointing Cloudstream clients to the hosted plugin feed (`builds` branch). Defines repository name/description and plugin list location.

## Current Implementation Status
✅ **IMPLEMENTED** - `repo.json` present at repo root.

## Implementation Details
- **Location**: repo.json
- **Current interfaces**: JSON fields `name`, `description`, `manifestVersion`, `pluginLists` (points to `https://raw.githubusercontent.com/freelanceagent1/cloudstream-extensions-uk/master/builds/plugins.json`).
- **Dependencies**: CI pipeline to keep `builds/plugins.json` updated; Cloudstream client expects this manifest format.
- **Dependents**: Cloudstream app when adding repository URL; any consumers pulling plugin list.

## Core Logic & Functionality
- Provides metadata for the plugin repository and the URL to the generated plugins.json list.
- Serves as entrypoint for clients to discover provider modules.

## Current Quality Assessment
- **Completeness**: Manifest fields populated and valid.
- **Code Quality**: Simple JSON; relies on external plugins.json staying current.
- **Test Coverage**: None.
- **Documentation**: README includes repository URL usage.

## Technical Debt & Improvement Areas
- Keep pluginLists URL aligned with actual builds branch owner/org if it changes.
- Consider mirroring or backup URL for resilience.
- Automate validation that plugins.json exists after CI runs.

## Interface Definition
```json
{
  "name": "CakesTwix Providers Repository",
  "description": "Cloudstream Ukraine Plugin Repository",
  "manifestVersion": 1,
  "pluginLists": [
    "https://raw.githubusercontent.com/freelanceagent1/cloudstream-extensions-uk/master/builds/plugins.json"
  ]
}
```

## ARC Verification Criteria

### Functional Criteria
- [ ] Clients can fetch plugins.json using the pluginLists URL.
- [ ] ManifestVersion matches Cloudstream expectations.

### Input Validation Criteria
- [ ] URL remains reachable (HTTP 200) after builds.

### Error Handling Criteria
- [ ] Fail fast if plugins.json missing in CI.

### Quality Criteria
- [ ] Manifest stays in sync with CI output location and branch ownership.
