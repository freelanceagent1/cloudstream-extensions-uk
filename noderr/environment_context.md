# Universal Environment Context Profile (Completed)

```yaml
environment:
  type: "development"
  provider: "local-windows"
  lifecycle: "persistent"
  purpose: "Local Windows 11 workstation for Cloudstream plugin development and builds"
  environment_focus: "DEVELOPMENT - This profile documents the development workspace"

  platform_detection:
    identified_platform: "Local Windows 11 Pro for Workstations (no hosted env vars detected)"
    special_environment_vars: "None matching REPL/CLOUD/VIRTUAL/AWS/GCP/AZURE/HEROKU/VERCEL/NETLIFY/LOVABLE/CODESPACES"
    available_tools: "git, node v24.11.1, npm 10.9.4, python 3.14.0, pip 25.2, java 17.0.17 (ms-17), gradlew (wrapper)"
    missing_standard_tools: "python3 alias opens Microsoft Store; yarn, pnpm, go, rust, mvn, docker, docker-compose, kubectl, helm, terraform, ansible"

  orchestration_hints:
    auto_seed_on_reset: false
    strict_linting: false
    backup_before_migrations: false
    uses_workflow_system: false
    uses_container_orchestration: false
    uses_serverless_deployment: false
```

**Profile Name:** Windows11-Cloudstream-Gradle  
**Environment ID:** ENV-2025-12-04-win-local  
**Last Updated:** 2025-12-04T13:43:35.0790064-08:00  
**Validated By:** Codex (GPT-5)  
**Confidence Level:** High (local tools tested; Gradle works with JAVA_HOME set to ms-17.0.17)

---

## Purpose
This document is the operational reference for this **DEVELOPMENT** environment. All commands below were executed in PowerShell and recorded with their observed output. The project is a local Kotlin/Gradle multi-module (Cloudstream provider) workspace; there is no hosted preview URL.

---

## 1. Critical Platform Rules & Gotchas

```yaml
critical_donts:
  process_management:
    - "Do not assume background daemons; start gradle tasks manually after configuring JAVA_HOME."
    - "Do not rely on lsof/ss (not installed); use Get-NetTCPConnection for ports."
  package_management:
    - "Do not run gradle tasks before installing a JDK and setting JAVA_HOME."
    - "Do not use yarn/pnpm (not installed); npm is available for JS tooling only if needed."
    - "Do not attempt linux-only tooling without WSL; commands like uname/id are missing."
  network_configuration:
    - "Do not bind to platform ports expecting reverse proxies; this repo builds plugins, not services."
    - "Do not assume localhost web access; no dev server is part of this project."
    - "Do not use production URLs (none provided) for testing; testing is via local builds."
  file_system:
    - "Avoid writing outside the repo root; use relative paths under D:\\hdrezka."
    - "Respect NTFS permissions; sudo is not available."
  environment_confusion:
    - "This profile is for local development only; there is no production deployment in this repo."
    - "Do not document or use external deployment URLs as test targets."
  platform_specific:
    - "PowerShell is the default shell; adapt commands (grep/uname/id not present)."
    - "python3 alias invokes Microsoft Store; use python."
```

```yaml
debugging_gotchas:
  console_behavior:
    - "Gradle output appears in the PowerShell console; ensure JAVA_HOME is set or gradle exits early."
  development_workflow:
    - "Gradle tasks fail fast without JDK; install JDK 17+ and set JAVA_HOME."
  network_behavior:
    - "Use Get-NetTCPConnection for port checks; lsof/ss are unavailable."
  resource_management:
    - "Local disk free: ~104 GB on D: (Get-PSDrive)."
  environment_separation:
    - "No separate dev/prod runtimes; all work is local builds."
```

---

## 2. Environment Discovery & Validation

### 2.1 System Information
```bash
# Working directory
pwd
# Returns:
# D:\hdrezka

# Operating System Details
systeminfo | findstr /B /C:"OS Name" /C:"OS Version"
# Returns:
# OS Name:                       Microsoft Windows 11 Pro for Workstations
# OS Version:                    10.0.26200 N/A Build 26200

# Shell Information
$PSVersionTable.PSVersion
# Returns:
# Major  Minor  Build  Revision
# 5      1      26100  7019

# Current User
whoami
# Returns:
# main\andre

# Environment Type Confirmation
Write-Output "This profile documents: DEVELOPMENT environment"
# Returns: This profile documents: DEVELOPMENT environment
```

### 2.2 Platform-Specific Detection
```bash
# Environment variable scan for hosted platforms
Get-ChildItem Env: | Where-Object { $_.Name -match 'REPL|CLOUD|VIRTUAL|AWS|GCP|AZURE|HEROKU|VERCEL|NETLIFY|LOVABLE|CODEPEN|CODESANDBOX|GITPOD|GITHUB|CODESPACES' }
# Returns: no matching variables (local workstation)

# Platform-specific tools
foreach ($tool in 'replit','lovable','aws','docker','kubectl') { $cmd = Get-Command $tool -ErrorAction SilentlyContinue; if ($cmd) { \"✓ $tool detected at $($cmd.Source)\" } else { \"✗ $tool not available\" } }
# Returns:
# ✗ replit not available
# ✗ lovable not available
# ✗ aws not available
# ✗ docker not available
# ✗ kubectl not available

# Development vs Production indicators
# Local-only; no production deployment configured for this repository.
```

### 2.3 Available Commands Check
```bash
# Core utilities availability
$tools = 'git','node','npm','python3','python','pip','pip3','yarn','pnpm','go','rust','java','mvn','gradle','docker','docker-compose','kubectl','helm','terraform','ansible'; foreach ($tool in $tools) { $cmd = Get-Command $tool -ErrorAction SilentlyContinue; if ($cmd) { \"✓ {0}: {1}\" -f $tool, $cmd.Source } else { \"✗ {0}: not available\" -f $tool } }
# Returns:
# ✓ git: C:\Program Files\Git\cmd\git.exe
# ✓ node: C:\Program Files\nodejs\node.exe
# ✓ npm: C:\Program Files\nodejs\npm.ps1
# ✓ python3: C:\Users\andre\AppData\Local\Microsoft\WindowsApps\python3.exe (invokes MS Store; not usable)
# ✓ python: C:\Python314\python.exe
# ✓ pip: C:\Python314\Scripts\pip.exe
# ✓ pip3: C:\Python314\Scripts\pip3.exe
# ✗ yarn: not available
# ✗ pnpm: not available
# ✗ go: not available
# ✗ rust: not available
# ✗ java: not available
# ✗ mvn: not available
# ✗ gradle: not available (use project wrapper after setting JAVA_HOME)
# ✗ docker: not available
# ✗ docker-compose: not available
# ✗ kubectl: not available
# ✗ helm: not available
# ✗ terraform: not available
# ✗ ansible: not available

# Package managers and versions
npm --version
# Returns: 10.9.4
pip --version
# Returns: pip 25.2 from C:\Python314\Lib\site-packages\pip (python 3.14)
pip3 --version
# Returns: pip 25.2 from C:\Python314\Lib\site-packages\pip (python 3.14)
node -v
# Returns: v24.11.1
python --version
# Returns: Python 3.14.0
python3 --version
# Returns: Python was not found; opens Microsoft Store (do not use)

# Java / Gradle (after setting JAVA_HOME to ms-17.0.17)
$Env:JAVA_HOME="C:\\Users\\andre\\.jdks\\ms-17.0.17"; $Env:Path = "$Env:JAVA_HOME\\bin;" + $Env:Path; java -version
# Returns: openjdk version "17.0.17" 2025-10-21 LTS ...
$Env:JAVA_HOME="C:\\Users\\andre\\.jdks\\ms-17.0.17"; $Env:Path = "$Env:JAVA_HOME\\bin;" + $Env:Path; .\\gradlew.bat --version
# Returns: Gradle 8.12.1 (launcher JVM 17.0.17)
```

### 2.4 Environment Constraints
```bash
# Disk Space (D:)
Get-PSDrive -Name D
# Returns: Used ~361.56 GB, Free ~104.20 GB on D:

# Volume details
Get-Volume -DriveLetter D | Select-Object DriveLetter,FileSystemLabel,FileSystem,SizeRemaining,Size
# Returns: NTFS, SizeRemaining 111883419648, Size 500105736192

# Memory
Get-CimInstance Win32_OperatingSystem | Select-Object TotalVisibleMemorySize,FreePhysicalMemory
# Returns: TotalVisibleMemorySize 33251320 KB, FreePhysicalMemory 2753240 KB

# Process/File Limits
# ulimit not available on Windows; rely on OS defaults.

# Network Connectivity
(Invoke-WebRequest -UseBasicParsing https://api.ipify.org).Content
# Returns: 199.195.150.2
```

---

## 3. Platform-Specific Workflow Management

### 3.1 Process Management System
```bash
# Start/stop tasks
# Gradle tasks are run manually: .\gradlew.bat <task> (after JAVA_HOME is set to a JDK install).
# To stop hung tasks: Get-Process -Name "java","gradle" | Stop-Process -Force
# Port checks: Get-NetTCPConnection | Select-Object -First 5
```

### 3.2 Package Management System
```bash
# Kotlin/Gradle project
# Requires JDK (17+). Set JAVA_HOME before running gradle:
# $Env:JAVA_HOME="C:\\Users\\andre\\.jdks\\ms-17.0.17"; $Env:Path = "$Env:JAVA_HOME\\bin;" + $Env:Path
# Then run .\gradlew.bat <task>
# Example: .\gradlew.bat projects (works with ms-17.0.17)
# JS tooling (optional): npm install <pkg>
# Python tooling (optional): pip install <pkg>
```

### 3.3 Deployment System
```bash
# This repository builds Cloudstream provider plugins; there is no deployment target defined here.
# Distribution typically via GitHub releases or repo.json feed (see README).
```

---

## 4. File System Operations

### 4.1 Working Directory Structure
```bash
pwd
# Returns: D:\hdrezka (project root)

# Project root indicators: settings.gradle.kts, build.gradle.kts, multiple provider module folders (e.g., HdRezkaProvider).
```

### 4.2 Directory Listing
```bash
# Primary listing
ls -la

# Recursive view alternative
Get-ChildItem -Recurse -Depth 2 | Select-Object FullName
```

### 4.3 File Reading & Writing
```bash
# Read file
Get-Content <filepath>

# Read with line numbers
Get-Content -Path <filepath> | nl

# Write file
Set-Content -Path <filepath> -Value "<content>"
```

---

## 5. Network & Port Management

### 5.1 Network Configuration
```bash
ipconfig
# Returns: Interfaces including Ethernet (IPv4 192.168.1.222) and Zerotier (172.27.92.215)

nslookup google.com
# Returns: resolves via router.home.local (192.168.1.1)
```

### 5.2 Port Management
```bash
# Port listing
Get-NetTCPConnection | Select-Object -First 5
# Returns: sample bound ports (no active dev server)

# netstat (basic)
netstat -ano | Select-Object -First 5
# Returns: shows LISTENING entries; command exits with code 1 but outputs data on Windows
```

### 5.3 Application Access Configuration
```yaml
development_server:
  bind_host: "localhost"
  default_port: 0
  alternative_ports: none
  access_urls:
    local_dev_preview:
      url: "No running web server; testing via built plugin in Cloudstream app/emulator"
      description: "Build artifacts locally with gradle after JDK setup, then sideload into Cloudstream"
      how_to_access: "Run gradle make task per provider to produce plugin APK; load into app"
    public_deployed_app:
      url: "Not deployed"
      description: "No production deployment for this repository"
      warning: "⚠️ Changes here produce local builds only"
      how_to_deploy: "Publish artifacts manually (e.g., GitHub release/repo.json)"
  platform_url_examples:
    development_pattern: "N/A - library build, no dev URL"
    production_pattern: "N/A - plugin distribution only"
  platform_specific_config:
    websocket_url: "N/A"
    proxy_requirements: "None documented"
    firewall_rules: "Standard Windows firewall applies; allow gradle to download dependencies"
```

---

## 6. Version Control & Collaboration

### 6.1 Git Configuration
```bash
git config user.name
# Returns: freelanceagent1
git config user.email
# Returns: noreply@freelanceagent.net
```

### 6.2 Collaboration Tools
```bash
# Local git workflow; no cloud IDE collaboration in use.
```

---

## 7. Language & Runtime Management

### 7.1 Runtime Detection
```bash
node -v && npm -v
# Returns: v24.11.1 and 10.9.4

python --version && pip --version
# Returns: Python 3.14.0 / pip 25.2

python3 --version
# Returns: Launches Microsoft Store; avoid.
```

### 7.2 Package Management
```bash
# Kotlin/Gradle: install JDK and use .\gradlew.bat <task>
# Node: npm install <package>
# Python: pip install <package>
```

---

## 8. Database & Storage Operations

```yaml
database_system: "None (plugin project; no database)"
connection_method: "N/A"
orm_tool: "N/A"
connection_source: "N/A"

database_environment:
  development_db: "None"
  production_db: "None"
  data_isolation: "N/A"

platform_specific_database:
  provider: "N/A"
  access_method: "N/A"
  management_tools: "N/A"
```

---

## 9. Testing & Quality Assurance

### 9.1 Testing Framework
```bash
# Gradle JUnit tests per module
$Env:JAVA_HOME="C:\\Users\\andre\\.jdks\\ms-17.0.17"; $Env:Path = "$Env:JAVA_HOME\\bin;" + $Env:Path; .\\gradlew.bat testReleaseUnitTest
# Status: runnable after setting JAVA_HOME to ms-17.0.17
```

### 9.2 Build Process
```bash
# Build plugins (example per provider)
$Env:JAVA_HOME="C:\\Users\\andre\\.jdks\\ms-17.0.17"; $Env:Path = "$Env:JAVA_HOME\\bin;" + $Env:Path; .\\gradlew.bat HdRezkaProvider:make
# Status: runnable after setting JAVA_HOME to ms-17.0.17
```

---

## 10. Debugging & Monitoring

```yaml
log_system:
  application_logs: "Gradle console output in PowerShell"
  system_logs: "Windows Event Viewer (not used for build)"
  platform_logs: "None; local workstation"
  dev_vs_prod_logs:
    development: "Console output during gradle tasks"
    production: "Not applicable"
  real_time_monitoring: "tail-like monitoring via Get-Content -Wait for text files if needed"
  log_aggregation: "None"
```

```bash
# Debug mode activation
# Use gradle --stacktrace or --info once JDK is configured.

# Process monitoring
Get-Process | Select-Object -First 5
```

---

## 11. Security & Secrets Management

```yaml
secrets_management:
  method: "Environment variables or local config files; none required for current plugins"
  access_pattern: "Set via PowerShell `$Env:VAR=value` when needed"
  storage_location: "Local machine; not committed"
  dev_vs_prod_secrets:
    development: "None in use"
    production: "N/A"
    isolation: "complete"
  platform_specific:
    secret_ui: "None"
    secret_injection: "Manual export in shell"
    secret_rotation: "Manual"
```

```bash
# Security scanning
npm audit
# Not applicable unless JS deps added; primary codebase is Kotlin.
```

---

## 12. Platform-Specific Features & Limitations

```yaml
platform_features:
  multiplayer_editing: false
  real_time_collaboration: false
  integrated_terminal: true (PowerShell)
  preview_pane: false
  hot_reload: false
  built_in_deployment: false
  automatic_https: false
  custom_domains: false
  database_integration: false
  ai_assistance: false
  unique_features:
    - "Full local control; no platform-imposed limits beyond missing JDK"

platform_limitations:
  no_sudo_access: true
  limited_system_tools: true (no lsof/ss/uname/id)
  restricted_network_access: false (downloads allowed)
  ephemeral_storage: false
  process_limits: standard Windows
  memory_constraints: "~31.7 GB total; ~2.6 GB free at capture time"
  dev_prod_separation:
    separate_urls: false
    separate_databases: false
    separate_configs: false
  specific_restrictions:
    - "JAVA_HOME must be set to run gradle wrapper"
    - "python3 alias unusable; use python"
```

```bash
# Development workflow
# 1. Install JDK 17+ and set JAVA_HOME.
# 2. Run gradle tasks via .\gradlew.bat (e.g., HdRezkaProvider:make).
# 3. Sideload generated plugin into Cloudstream for testing.
# 4. Commit changes with git.

# Emergency procedures
# - Stop stuck processes: Get-Process java,gradle | Stop-Process -Force
# - Clean build outputs: Remove-Item -Recurse -Force **/build
```

---

## 13. Performance & Resource Monitoring

```bash
# CPU & Memory snapshot
Get-Process | Sort-Object CPU -Descending | Select-Object -First 5

# Network monitoring
netstat -s | Select-String -Pattern "error"
```

```bash
# Performance testing (library project)
# Not applicable; primary output is plugin artifacts, not a running service.
```

---

## 14. Error Recovery & Troubleshooting

```yaml
common_errors:
  dependency_issues:
    detection: "Gradle error: JAVA_HOME is not set"
    solution: "Install JDK and set JAVA_HOME"
  process_management:
    detection: "Gradle tasks hang or fail"
    solution: "Stop processes and rerun with --stacktrace"
  network_connectivity:
    detection: "Dependency downloads fail"
    solution: "Verify internet access and Windows firewall allowances"
  storage_issues:
    detection: "Build fails writing to build directory"
    solution: "Ensure NTFS permissions and free disk space (~104 GB free)"
  environment_confusion:
    detection: "Expecting web preview URL"
    solution: "This repo builds plugins; no web runtime"
```

```bash
# Full development environment reset
# Remove build artifacts:
Remove-Item -Recurse -Force **/build
# Re-open shell after setting JAVA_HOME

# Partial recovery
# Re-run gradle with --refresh-dependencies once JDK is configured
```

---

## 15. Quick Reference Card

```bash
# Key commands (after installing JDK and setting JAVA_HOME):
.\gradlew.bat HdRezkaProvider:make          # Build a provider plugin
.\gradlew.bat testReleaseUnitTest          # Run unit tests

# Git
git status
git add <files> && git commit -m "..."

# Environment reminders:
echo "Current environment: DEVELOPMENT"
echo "Preview URL: No web server; test via Cloudstream plugin install"
echo "Production URL: Not deployed"
```

---

## 16. Environment Verification

```bash
echo "=== FINAL ENVIRONMENT VERIFICATION ==="
echo "Environment type: DEVELOPMENT"
echo "Preview/test URL: No running web server; build plugins locally"
echo "Production URL (reference only): Not deployed"
echo ""
echo "✓ I will use local plugin builds for ALL testing"
echo "✓ I understand 'Not deployed' represents production"
echo "✓ This profile documents the DEVELOPMENT environment"
echo "=== VERIFICATION COMPLETE ==="
```

---

## Validation Checklist

- Completed: Environment type clearly marked as DEVELOPMENT
- Completed: Platform identified as local Windows workstation
- Completed: Commands tested; noted missing JDK requirement for gradle
- Completed: local_dev_preview and public_deployed_app documented (no web runtime; plugin build workflow)
- Completed: No placeholders remain
- Completed: Limitations and constraints documented
- Completed: Error recovery procedures noted
- Completed: Security considerations addressed
- Completed: Performance notes documented

---

Environment context complete: development focus, local Windows host, gradle wrapper pending JDK setup, no production URL, testing via locally built Cloudstream plugins.
