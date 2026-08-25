# Kalaza Leads — Development Progress

Living log of what's actually been built, how it works, and everything a new session needs to know before touching this project. **Read this before doing anything else.**

**Scope note (2026-08-25):** `docs/MASTER_PLAN_V2.md` is now the authoritative source of truth for what this project *is* — it supersedes `docs/PROJECT_SPEC.md` wherever the two disagree (v1 spec is still fine for background). Read the Master Plan before planning any new work; the short version is that the supervisor redefined the project after a review — it's now three systems (Android CRM, a WhatsApp automation backend, and Meta's WhatsApp Cloud API integration), the leads schema needs a real migration, and most remaining blockers are NGO-side paperwork/content, not code. This file (`PROGRESS.md`) remains the engineering log of what's actually been built, how, and why — that hasn't changed.

Last updated: 2026-08-25.

---

## 1. Current status — what works right now

- **Auth**: staff can sign up / log in / log out. Verified working end-to-end on a real device.
- **Leads (A1 done)**: `leads` table migrated to the v2 schema (`docs/MASTER_PLAN_V2.md` Part 5 — split contact_channel/how_heard, expanded service/condition/amenity lists, budget range, visit/follow-up dates, etc. — see `docs/sql/002_leads_v2_migration.sql`). Add Enquiry form covers essentially all of it, including 11 of the 13 supervisor UI requests (country code + 10-digit validation, split contact-channel/how-heard, expanded services, structured patient conditions, room type, relation dropdown, medical history, visit/follow-up date pickers, multi-select chips that wrap instead of scrolling). Verified end-to-end on a real device.
- **Lead detail + edit (A2 done)**: tapping a lead card opens a full detail/edit screen — every add-form field plus Status (with conditional not-converted reason/detail), actual visit date, and final remarks. Verified end-to-end: edit, save, status reflects on the list card.
- **Contact activity log (A3 done)**: new `contact_activities` table (`docs/sql/003_contact_activities.sql`), tied to leads via `lead_id`. A "Contact Log" section on the Lead Detail screen (right after Status/Pipeline) lets staff log a call/WhatsApp/visit/email/SMS with direction, outcome, an optional callback date, and notes, and shows the timeline of everything logged so far. Verified end-to-end on device.
- **Follow-ups Due list (A4 part 1 done)**: see below, now folded into the segmented tabs. **Push notifications (A4 part 2) not built yet** — needs Firebase Cloud Messaging setup, a separate undertaking.
- **Segmented list views (A5 done)**: Leads screen has 7 scrollable tabs, each with a live count — Follow-ups Due, All, Active (in-pipeline), Converted, Not Converted, Dormant, Backup — matching the separate lists the supervisor explicitly asked for. Not Converted cards show the reason inline. Purely client-side filtering on data already fetched. Verified end-to-end on device.
- **`wa.me` one-tap WhatsApp messaging (A7 done)**: "Send WhatsApp" section on Lead Detail with three templated draft messages (Thank You, Follow-up, Visit Feedback), personalized with the lead's first name and service. Opens WhatsApp via a `wa.me` deep link with the message pre-filled; staff review/edit there before sending. Zero cost, no Meta dependency. Verified end-to-end on device (built ahead of A6 since it was assessed as more valuable/lower-risk).
- **Not built yet**: push notifications (A4 part 2), Excel export (A6), staff table (A8), reports/analytics (A9), and everything WhatsApp-automation-related (Track B/C/D in the Master Plan — none of it has started, and B/C need the supervisor/NGO to act, not Anhad).

If you're picking this up fresh: pull latest, open the project at `C:\Dev\kalaza-leads` (see §4 — **not** the OneDrive folder), build, install on the connected device, and you should be able to log in and add an enquiry immediately.

---

## 2. Architecture

Kotlin + Jetpack Compose (Material 3), MVVM. Package `com.kalazacare.leads`.

```
app/src/main/java/com/kalazacare/leads/
  KalazaLeadsApp.kt                    Application class
  data/
    model/Lead.kt                      Lead (read) + NewLeadRequest (insert payload)
    model/StaffMember.kt               unused placeholder, not wired up yet
    remote/SupabaseClients.kt          SupabaseClient singleton, reads URL/key from BuildConfig
    repository/
      AuthRepository.kt / SupabaseAuthRepository.kt
      LeadsRepository.kt / SupabaseLeadsRepository.kt
  ui/
    MainActivity.kt                    Screen state machine: LOGIN -> LEADS -> ADD_LEAD
    login/LoginScreen.kt, LoginViewModel.kt
    leads/LeadsScreen.kt, AddLeadScreen.kt, LeadsViewModel.kt
    theme/                             Teal Material3 theme (deliberately distinct from Kalaza Care's red)
```

**Pattern**: each feature is Repository (talks to Supabase) → ViewModel (StateFlow of a `*State` data class) → Composable Screen (collects state, renders, calls ViewModel methods). `MainActivity` creates all repositories/ViewModels once in `onCreate` and passes them down — no DI framework, deliberately simple for a solo student project.

**Navigation**: no NavHost. `MainActivity` holds a `private enum class Screen { LOGIN, LEADS, ADD_LEAD }` as a `mutableStateOf`, and a `when` picks which Composable to show. Fine for 3 screens; will need real navigation (Compose Navigation) once the screen count grows past ~5.

---

## 3. Supabase backend

- **Project**: `niqhlkdyaklnngcanxld.supabase.co`, org "Anhad23mahajan's Org", region South Asia (Mumbai). Created 2026-08-23 as a **standalone project**, not shared with Kalaza Care (that was considered — see §6 decisions log).
- **Credentials** live only in `local.properties` (gitignored) → injected into `BuildConfig.SUPABASE_URL` / `BuildConfig.SUPABASE_ANON_KEY` at build time. See `local.properties.example` for the exact keys needed. Never hardcode these.
- **`leads` table**: 18 columns per `docs/PROJECT_SPEC.md` §7 (source_channel, how_heard, enquirer_name/phone/relation, patient_name/age, location, service_wanted, room_preference, budget, medical_history, specific_requirements, status, next_follow_up_date). Schema SQL lives in `docs/sql/001_leads_table.sql` — that file is the source of truth, already applied to the live project.
- **RLS**: enabled, plus explicit `grant select/insert/update/delete ... to authenticated` (this project was created with "Automatically expose new tables" turned OFF for tighter default security, which means RLS policies alone are *not* enough — Postgres also needs the base grant, or PostgREST returns `permission denied for table leads` even with a passing policy). Every policy just checks `auth.uid() is not null` — there's no staff/roles table yet, so *any* signed-in account is trusted staff. Tighten this once a real roles table exists.
- **Auth settings** (Authentication → Sign In / Providers → User Signups in the Supabase dashboard): **"Confirm email" is OFF**. Staff log in by name; the app synthesizes an email `{name}@kalazaleads.app` (see `SupabaseAuthRepository.kt`) since Supabase Auth is email/password under the hood. Two hard-won constraints here:
  - The domain must be a real TLD — `.internal`/`.local`/`.test` etc. get rejected outright by Supabase's signup validator with `email_address_invalid`. `.app` works.
  - "Confirm email" must stay off, because these synthetic addresses have no real inbox — with it on, signup silently succeeds but the account is stuck permanently unconfirmed.
  - Password minimum is Supabase's default: **6 characters**.
- **Auth API shape** (io.github.jan-tennert.supabase, BOM 3.5.0): `signInWith`/`signUpWith` take a `Provider` object + builder lambda, not named args:
  ```kotlin
  client.auth.signInWith(Email) { email = "..."; password = "..." }
  ```
  `import io.github.jan.supabase.auth.providers.builtin.Email`. `signOut()` is a plain member of `client.auth`, no extra import.
- **Postgrest usage** (`SupabaseLeadsRepository.kt`): `client.postgrest.from("leads").select { order("created_at", Order.DESCENDING) }.decodeList<Lead>()` for reads, `.insert(newLead) { select() }.decodeSingle<Lead>()` for writes (the `select()` inside the insert builder is what makes it return the created row instead of nothing).

---

## 4. Local development environment — READ BEFORE BUILDING

This cost an entire debugging session. Don't repeat it.

- **Canonical project location is `C:\Dev\kalaza-leads`, NOT the OneDrive folder.** The project started at `Desktop\kalaza-leads` under the user's OneDrive sync (`OneDrive - NBCC (India) Limited`) and was copied out (`robocopy /E /XD "app\build" ".gradle"`, preserving `.git`) partway through, in an attempt to fix a build-lock issue that turned out to be unrelated to OneDrive (see next point) — but the move happened anyway and `C:\Dev\kalaza-leads` is now where building/testing actually happens. **Both copies still exist and both have git remotes to the same GitHub repo.** When making changes, decide which copy you're editing and make sure the fix actually reaches `C:\Dev\kalaza-leads` before testing (commit → push → pull in the other copy, or edit directly in whichever copy the user builds from).

- **System `JAVA_HOME` defaults to JDK 26**, which Gradle 8.11.1 can't run on (fails instantly with a cryptic one-line error, just the version number). Every terminal Gradle command needs:
  ```powershell
  $env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
  ```
  first (that path is Android Studio's bundled JDK 21).

- **Any Gradle/Java command run through Claude's own Bash or PowerShell tool fails** with `java.io.IOException: Unable to establish loopback connection` / `SocketException: Invalid argument: connect`. This is a JDK NIO bug (tries AF_UNIX domain sockets internally, which don't work when the JVM is a subprocess of an agent/automation layer — confirmed the same failure is reported elsewhere for Claude Desktop/MCP subprocesses). No JVM flag fixes it. **The only fix: the user runs the Gradle command themselves, directly, in their own terminal** (Android Studio's Terminal tab, or a plain PowerShell window). This is why every build in this project's history was done by the user pasting terminal output back, not by Claude running it directly.

- **`Unable to delete directory` / file-in-use errors on Gradle's generated `app\build\...` subdirectories** happen constantly, on both the OneDrive and non-OneDrive locations — this turned out to be **Windows Search Indexer** racing Gradle's rapid create-delete-recreate cycle on fresh build output, not OneDrive sync as originally suspected. A manual delete of the exact same stuck folder succeeds instantly moments later, confirming it's a transient lock, not a persistent block. Setting `NotContentIndexed` on the folder didn't reliably fix it either. **Working fix: retry loop**, run in the user's own terminal:
  ```powershell
  for ($i = 1; $i -le 5; $i++) {
      if (Test-Path "app\build") { Remove-Item -Recurse -Force "app\build" -ErrorAction SilentlyContinue }
      Start-Sleep -Seconds 2
      .\gradlew.bat assembleDebug --no-daemon
      if ($LASTEXITCODE -eq 0) { break }
  }
  ```
  Expect to need multiple attempts within the loop most of the time.

- **Android Studio silently keeps running the old APK when the current code fails to compile**, with no obvious signal to a non-expert. This caused a very long false trail ("the button does nothing") that was actually just a stale install. Always verify before assuming a runtime bug:
  ```powershell
  & "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" shell dumpsys package com.kalazacare.leads | Select-String "lastUpdateTime"
  ```
  Compare to current time. If it's not recent, the code change never actually reached the device — go find out why the build failed or wasn't installed, don't debug the running app.

- **`Gradle` reporting `X actionable tasks: X up-to-date` after a source change** means it skipped recompiling entirely (thinks nothing changed). If that happens right after an edit that should matter, force it: `.\gradlew.bat clean assembleDebug --no-daemon` (though `clean` itself is exactly what triggers the Search Indexer lock above, so expect to need the retry loop for `clean` too — or just delete `app\build` manually first and skip `clean`).

- **adb path**: `$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe` — not on PATH by default in these terminals.

---

## 5. Git / GitHub workflow

- Repo: `github.com/Anhad23mahajan/kalaza-leads`, owner's own account (not the friend/Aditya account originally considered — see §6).
- No standing credentials are stored anywhere. Every push uses a short-lived fine-grained GitHub PAT: user generates one scoped to just this repo (Contents: Read and write, shortest expiration) at `github.com/settings/personal-access-tokens/new`, pastes it into chat, it's used once in the remote URL (`https://x-access-token:TOKEN@github.com/...`), then immediately stripped from git config (`git remote set-url origin` back to the plain HTTPS URL) and the user revokes it on GitHub.
- Git identity: `Anhad Mahajan` / `anhadagammahajan@gmail.com` (set via `git config --global`).
- Because two local copies of the repo exist (§4), remember to sync both: commit+push from whichever copy was edited, then `git pull origin main` in the other one before building/testing there.

---

## 6. Key decisions and why

- **Standalone Supabase project, not shared with Kalaza Care.** The spec's original preference was sharing one project (clean lead→resident handoff). Went standalone instead because Harsh (who has Kalaza Care's credentials) wasn't available, and waiting would have blocked all progress. This is a **reversible** decision — migrating to Kalaza Care's project later is possible, just needs re-pointing `local.properties` and re-running the leads table SQL there.
- **Not migrating to Aditya Sharma's Claude Code cloud account.** Originally planned (see `docs/PROJECT_SPEC.md` framing), but the user chose to keep building step-by-step in this local session instead — "slow and steady wins the game." If a handoff to that account happens later, this file is exactly what should be read first there.
- **Teal theme, not Kalaza Care's red.** Deliberate — the two apps install side by side on the same phone; staff need to tell them apart at a glance.
- **No staff/roles table yet.** Every Supabase Auth account is currently treated as trusted staff (RLS just checks `auth.uid() is not null`). Fine for now since nothing else can create accounts through this app; revisit before any real deployment.
- **wa.me deep links for MVP messaging, not WhatsApp Business API.** Zero cost, zero ban risk, ~90% of the value. Full rationale in `docs/PROJECT_SPEC.md` §4. Not built yet as of this writing.

---

## 7. What to build next

**Superseded 2026-08-25** by `docs/MASTER_PLAN_V2.md` Part 8 (full roadmap) — read that for the authoritative plan. Short version, Track A (the only track with no NGO/Meta dependency, so the only one that can proceed unblocked right now):

1. ~~**A1 — schema migration + form overhaul.**~~ **Done 2026-08-25.** Migrated per Master Plan Part 5, form rebuilt with 11/13 supervisor UI requests.
2. ~~**A2 — Lead detail + edit screen.**~~ **Done 2026-08-25.** Tap a card, edit any field, change status, save.
3. ~~**A3 — Contact activity log.**~~ **Done 2026-08-25.** Log calls/WhatsApp/visits with outcome + notes, timeline shown on Lead Detail.
4. **A4 — Follow-up-due home screen** ~~+ push notifications~~. **List done 2026-08-25**, folded into A5's segmented tabs. **Push notifications still open** — needs Firebase Cloud Messaging setup.
5. ~~**A5 — Segmented list views.**~~ **Done 2026-08-25.** 7 tabs with live counts, matching the supervisor's requested lists exactly.
6. **A6 — Excel export.** **Next up.**
7. ~~**A7 — `wa.me` one-tap messaging.**~~ **Done 2026-08-25** — built ahead of A6 (assessed as lower-risk, more immediately valuable). Three templated messages, opens WhatsApp pre-filled.
8. **A8 — Staff table** + basic roles/assignment.
9. **A9 — Reports/analytics screen** — Master Plan Part 7 has the full list the supervisor asked for; the "why we lose families" (unmet-demand / not-converted-reason) report is flagged as the one to lead with when demoing.

Meanwhile, **Track B (Meta/WhatsApp onboarding) and Track C (NGO content — the ~20 FAQ answers, price list, posters)** need to start now too, in parallel, driven by the supervisor/NGO — not by Anhad — since they're the long pole per the Master Plan's risk register. Track D (the actual automation) is gated on both being done.
