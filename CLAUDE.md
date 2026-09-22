# CLAUDE.md — Kalaza Leads

**You are probably a fresh Claude instance with no memory of this project. Start here.**

1. Read **`docs/HANDOFF.md`** — the complete guide (history, architecture, database, build, workflow, decisions, gotchas, open items). Sections 1, 12 and 17 first.
2. Then `docs/ROADMAP.md` (what to do next) and `docs/PROGRESS.md` (newest engineering facts).
3. Before doing anything, ask the human what the outcome of the **Track B supervisor meeting** was and what they want to work on (details: HANDOFF §15–§16).

## What this is
An Android CRM (Kotlin, Jetpack Compose, MVVM, Supabase) for an elder-care NGO in Pune. Staff track enquiries, follow-ups and contacts. **Track A (the app) is finished and tested on a real phone.** New leads arrive through a **Google Form → Apps Script → Supabase** bridge; there is deliberately **no in-app "add enquiry" screen**. The WhatsApp auto-reply bot (Tracks B/C/D) has not started.

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
- Signup is **gated by the active staff roster** (`is_active_staff_name`). On a **fresh database** nobody can sign up until you insert the first staff row — see HANDOFF §7.4.
- Supabase project has "Automatically expose new tables" **off**: every table needs an explicit `GRANT` *and* a policy.
- Login is by **name**; the app synthesizes `name@kalazaleads.app` (Confirm-email is OFF; passwords ≥ 6 chars).
- The Google Form's **question titles are the contract** with `tools/google-form-bridge/KalazaFormBridge.gs`. Changing a title silently breaks that field. Enum lists must stay in sync across `LeadFormOptions.kt`, the SQL check constraints and the bridge maps.
- Build only from the canonical clone (on Anhad's machine `C:\Dev\kalaza-leads`, not the OneDrive copy).
- The bot must **never invent facts**, must hand distress and no-match to a human, and must honour opt-outs (`docs/AUTOMATION_DESIGN.md`).

## Layout
`app/` Android code (`com.kalazacare.leads`) · `docs/` all documentation · `docs/sql/001…006` migrations (run in order) · `tools/google-form-bridge/` Apps Script source · `gradle/libs.versions.toml` all versions.
