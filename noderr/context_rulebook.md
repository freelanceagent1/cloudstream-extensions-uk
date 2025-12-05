# Context-Documentation Rulebook

## Purpose
Define the standing rule to document APK-derived findings (endpoints, headers, mirrors, parsing, language preferences) so all Noderr artifacts stay in sync.

## Rule
1. When analyzing APKs or provider binaries, record all observed endpoints, headers, params, mirrors, language/region variants, parsing rules, and stream/subtitle flows.
2. Surface findings in:
   - Specs: Update/create specs for affected NodeIDs with concrete endpoints/headers/parsing details.
   - Tracker: Add/update NodeIDs and statuses with new dependencies/notes.
   - Architecture: Ensure nodes/edges reflect new flows or components.
   - Log: Add Decision/IssueUpdate entries summarizing discoveries and rules applied.
3. Prefer language-localized/English-friendly mirrors whenever available; avoid non-localized content where feasible.
4. Keep fixture-based tests aligned with documented flows to prevent drift.
5. Mandatory: Do not mock data in place of real flows—implement real endpoints/parsing/streams and use real captured fixtures where testing requires offline runs.

## Scope
Applies to all provider modules, especially HdRezka, when integrating full app functionality into Cloudstream extensions.
