# CLAUDE.md — Kalaza Leads

**You are probably a fresh Claude instance with no memory of this project. Start here.**

> ⚠️ **Major pivot, 2026-10-02:** the WhatsApp Business Platform auto-reply bot (Tracks B/C/D)
> was **abandoned entirely**, not paused. Ignore any older context, doc, or memory suggesting
> it's still the plan. Current scope is three things: the Google Form intake (done), fixing the
> follow-up notification system, and redesigning the WhatsApp quick-message buttons. Plus an
> eventual ownership handoff to the NGO. `docs/ROADMAP.md` is authoritative for current scope.

1. Read **`docs/HANDOFF.md`** — the complete guide (history, architecture, database, build, workflow, decisions, gotchas, open items). Sections 1, 12 and 17 first — and the pivot banner at the top of §0.
2. Then `docs/ROADMAP.md` (what to do next — **authoritative current scope**) and `docs/PROGRESS.md` (newest engineering facts).
3. Before doing anything, ask the human which of the three current workstreams they want to work on (ROADMAP.md §3–§5: fix notifications, redesign WhatsApp messages, or the NGO handoff).

## What this is
An Android CRM (Kotlin, Jetpack Compose, MVVM, Supabase) for an elder-care NGO in Pune. The single admin tracks enquiries, follow-ups and contacts. **The app is finished and tested on a real phone.** New leads arrive through a **Google Form → Apps Script → Supabase** bridge; there is deliberately **no in-app "add enquiry" screen**. **The WhatsApp auto-reply bot (Tracks B/C/D) was abandoned 2026-10-02** after real-world Meta setup hit compounding blockers — see `docs/HANDOFF.md` §15. The project is now simpler: form intake (done) + follow-up notifications (being fixed) + WhatsApp quick-message templates (being redesigned) + a final handoff of the Google/Supabase accounts to the NGO.

## Rules that prevent mistakes
1. **The repo is PUBLIC.** Never commit or write into any doc: tokens, keys, passwords, the Supabase service-role key, real families' data, or personal contact details. `local.properties` is gitignored — keep it that way. Use fake data ("TEST …") for tests.
2. **Git pushes use a short-lived, single-repo, fine-grained GitHub PAT that the human pastes into chat.** Push once with `https://x-access-token:<TOKEN>@github.com/Anhad23mahajan/kalaza-leads.git`, then immediately reset the remote to `https://github.com/Anhad23mahajan/kalaza-leads.git`, confirm with `git remote -v`, and tell the human to revoke the token. Never store a token in any file. Never force-push. Run `git status` before any destructive git command.
3. **The human runs Gradle and adb** on Anhad's machine (Gradle failed inside the agent's shell with a loopback error). Try once on a new machine; if it fails, ask the human. Always give the `cd` and `JAVA_HOME` lines. Windows: `$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"`.
4. **Never type passwords or log in for the human.** For Google/Supabase/GitHub sign-in the human signs in themselves in the browser pane; check it is *their* account.
5. **"Done" means seen working on the human's phone**, not "it compiled". After code changes verify the build reached the device (`adb shell dumpsys package com.kalazacare.leads | grep lastUpdateTime`) — Android Studio silently reinstalls a stale APK when the code doesn't compile.
6. **Never describe a stopgap as the real thing, never declare work complete without checking the whole scope, and say what you did not verify.**
7. **Give exact next steps and one clear question**, not menus. Direct URLs, click paths, full commands. The human is blunt and terse; follow decisions after voicing a concern once.
8. **Ask before** anything hard to reverse or visible to others: publishing, sending messages, deleting data, submitting forms, changing shared settings.
9. **Verify library APIs against the real jars/code** (supabase-kt signatures changed between versions) before writing code that depends on them.
10. **Keep docs true.** After a feature or decision, update `docs/PROGRESS.md`, `docs/ROADMAP.md` and, if material, `docs/HANDOFF.md`. After removing anything, search the repo for dead references.

## Facts that bite
- **Single-admin app** (2026-09-24): no signup, roster, roles or assignment. The one admin account is created in the Supabase dashboard and "Allow new users to sign up" is OFF (`docs/sql/007_single_admin.sql`). On a fresh database: run 001…007, then Authentication → Users → Add user, then disable signups (HANDOFF section 7.4).
- Supabase project has "Automatically expose new tables" **off**: every table needs an explicit `GRANT` *and* a policy.
- Login is by **name**; the app synthesizes `name@kalazaleads.app` (Confirm-email is OFF; passwords ≥ 6 chars).
- The Google Form's **question titles are the contract** with `tools/google-form-bridge/KalazaFormBridge.gs`. Changing a title silently breaks that field (a leading question number like `12. ` and curly apostrophes are ignored since 2026-09-25). Enum lists must stay in sync across `LeadFormOptions.kt`, the SQL check constraints and the bridge maps.
- Build only from the canonical clone (on Anhad's machine `C:\Dev\kalaza-leads`, not the OneDrive copy).
- **`UpdateLeadRequest` (in `Lead.kt`) must keep NO default values.** supabase-kt encodes JSON with kotlinx's default (`encodeDefaults = false`), which silently drops any property equal to its default — so a default `null`/`emptyList()` means clearing a field never reaches the database. Every property is passed explicitly at the one call site (`LeadDetailScreen.kt`).
- The app **restores the saved login at launch**, locks to **portrait**, and handles the Back gesture itself (`MainActivity.kt`); a tapped follow-up notification carries `EXTRA_OPEN_LEAD_ID` / `EXTRA_OPEN_FOLLOW_UPS` (`NotificationHelper.kt`).
- The bridge reads budgets with `parseAmount()` (handles `20,000`, `1,00,000`, `30k`, `lakh`). **Don't add strict number validation to the form's budget questions** — it would reject what people type. After editing `KalazaFormBridge.gs`, the *live* Apps Script must be updated too (browser pane: Monaco `setValue`, save, reload, compare the LF-normalised SHA-256 to the repo file) — the repo file alone changes nothing. (Live script last synced 2026-10-05, sha16 `de5aab7d77c7e597`; at the NGO handoff move *this* version.)
- **No WhatsApp bot, no Meta account, no Cloud API anywhere in this project** — dropped for good 2026-10-02. If a doc, memory, or earlier conversation implies otherwise, it's describing history, not current scope.
- Follow-up notifications currently fire at unpredictable times and show only a count, not names — this is a known, currently-being-fixed problem, not a mystery to re-diagnose from scratch (`docs/ROADMAP.md` §3).
- The WhatsApp quick-message feature (`WhatsAppHelper.kt`) is being expanded from 3 fixed buttons to a categorized, expandable list (`docs/ROADMAP.md` §4) — all templates stay visible regardless of lead pipeline stage (the supervisor's explicit preference, don't add status-gating).

## Layout
`app/` Android code (`com.kalazacare.leads`) · `docs/` all documentation · `docs/sql/001…007` migrations (run in order) · `tools/google-form-bridge/` Apps Script source · `gradle/libs.versions.toml` all versions.
