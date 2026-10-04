# Kalaza Leads — Development Progress

Living log of what's actually been built, how it works, and everything a new session needs to know before touching this project. **Read this before doing anything else.**

**Scope note (2026-10-02, supersedes the one below):** the WhatsApp Business Platform auto-reply bot (Tracks B/C/D, and the `AUTOMATION_DESIGN.md` doc referenced in the original note below) was **abandoned entirely** after real-world Meta setup hit compounding blockers. `docs/ROADMAP.md` was rewritten and is the current source of truth: form intake (done), fixing the follow-up notification system, redesigning the WhatsApp quick-message templates, and an eventual ownership handoff to the NGO. Full story: `docs/HANDOFF.md` §15.

**Scope note (2026-08-25, historical — the plan it describes no longer applies, kept for context):** `docs/ROADMAP.md` is the source of truth for what's done and what's next, and `docs/AUTOMATION_DESIGN.md` for how the WhatsApp system should work (the older `PROJECT_SPEC.md` and `MASTER_PLAN_V2.md` were deleted as superseded; they remain in git history). Read the roadmap before planning any new work; the short version is that the supervisor redefined the project after a review — it's now three systems (Android CRM, a WhatsApp automation backend, and Meta's WhatsApp Cloud API integration), the leads schema needed a real migration, and most remaining blockers are NGO-side paperwork/content, not code. This file (`PROGRESS.md`) remains the engineering log of what's actually been built, how, and why — that hasn't changed.

Last updated: 2026-10-02 (major pivot: WhatsApp Business Platform auto-reply bot abandoned entirely; current work is fixing follow-up notifications and redesigning the WhatsApp quick-message templates — see `docs/ROADMAP.md`). Before that: 2026-09-24 (single-admin simplification: staff roster, roles, assignment and the signup gate removed); before that 2026-09-21 (complete handoff guide added: `docs/HANDOFF.md`; before that: Google Form intake bridge — built, published, and tested end-to-end).

---

## 1. Current status — what works right now

- **Auth**: the admin logs in / out by name + password. There is **no signup** (single admin since 2026-09-24; earlier versions allowed signup gated by a staff roster). Verified working end-to-end on a real device.
- **Leads (A1 done)**: `leads` table migrated to the v2 schema (split contact_channel/how_heard, expanded service/condition/amenity lists, budget range, visit/follow-up dates, etc. — see `docs/sql/002_leads_v2_migration.sql`). Originally entered via an in-app Add Enquiry form covering 11 of the 13 supervisor UI requests; that screen was **removed 2026-09-17** and replaced by the Google Form intake (see the Google Form bullet below).
- **Lead detail + edit (A2 done; reworked 2026-09-25 - reordered to follow the form, country-code picker removed, pickers match the form's options, Cancel is now outlined; not yet device-verified)**: tapping a lead card opens a full detail/edit screen — every add-form field plus Status (with conditional not-converted reason/detail), actual visit date, and final remarks. Verified end-to-end: edit, save, status reflects on the list card.
- **Contact activity log (A3 done)**: new `contact_activities` table (`docs/sql/003_contact_activities.sql`), tied to leads via `lead_id`. A "Contact Log" section on the Lead Detail screen (right after Status/Pipeline) lets staff log a call/WhatsApp/visit/email/SMS with direction, outcome, an optional callback date, and notes, and shows the timeline of everything logged so far. Verified end-to-end on device.
- **Follow-ups Due list (A4 part 1 done)**: folded into the segmented tabs (see A5). Notifications (A4 part 2) are covered further down.
- **Segmented list views (A5 done; tab order changed 2026-10-02, verified on device)**: Leads screen has 7 scrollable tabs, each with a live count — now ordered **All, Follow-ups Due, Active (in-pipeline), Converted, Not Converted, Dormant, Backup** (the supervisor wanted "All" as the first thing he sees on opening the app, not "Follow-ups Due"). The default selected tab is still index 0, which now resolves to "All" — confirmed on device. The empty-state message ("Nothing due right now." vs "No leads in this list.") was keyed off a hardcoded `selectedTab == 0`, which would have broken with the reorder — fixed to check `SEGMENTS[selectedTab].label == "Follow-ups Due"` instead. Not Converted cards show the reason inline. Purely client-side filtering on data already fetched.
- **`wa.me` WhatsApp quick-messages — redesigned 2026-10-02, NOT YET VERIFIED ON DEVICE.** The original 3 fixed buttons (Thank You, Follow-up, Visit Feedback -- struck through below) were replaced per the supervisor's ask for a richer menu:
  - `ui/leads/WhatsAppHelper.kt`: `WhatsAppTemplate` now has **7 entries** grouped into 4 `WhatsAppCategory` values -- Reaching out (Thank You), Checking in (Follow-up, Still deciding?, Haven't heard back, Confirm a scheduled visit), After a visit (Visit feedback), If they're not considering (Ask what happened). Wording is placeholder, written to be usable as-is -- swap for the supervisor's own phrasing once he gives it (open item, not blocking).
  - `ui/leads/WhatsAppQuickMessages.kt` (new): the expandable UI -- a collapsed card ("Choose a message to send" + chevron), tap to expand into the categories/templates list. Replaces the flat `WhatsAppTemplate.entries.forEach { Button(...) }` loop in `LeadDetailScreen.kt`.
  - All templates stay visible regardless of lead pipeline stage -- the supervisor's explicit preference, no status-based gating.
  - Still opens `wa.me` with the message pre-filled; staff review/edit in WhatsApp itself before sending, same as before. Zero cost, no Meta dependency (and no Meta integration anywhere in this project now -- see docs/HANDOFF.md §15).
  - **Not yet built/installed on the phone.** Verify: the collapsed/expanded toggle, all 7 templates appear under the right category headers, and a couple of them open WhatsApp with sensible text (check the `plannedVisitDate` one specifically -- it only inserts a date if the lead has one set).
  - ~~Original (struck through, superseded): three templated draft messages (Thank You, Follow-up, Visit Feedback) as fixed buttons. Verified end-to-end on device 2026-08-25.~~
- **Contact log — REMOVED 2026-10-02, verified on device.** The supervisor saw it on the rebuilt Lead Detail screen and asked for it gone as unnecessary. Deleted: `ui/leads/ContactLogSection.kt`, `ui/leads/ActivitiesViewModel.kt`, `data/repository/ContactActivitiesRepository.kt` + `SupabaseContactActivitiesRepository.kt`, `data/model/ContactActivity.kt`, and the now-dead `ACTIVITY_*` enum lists in `LeadFormOptions.kt`. `MainActivity.kt` no longer constructs an `ActivitiesViewModel`/repository. The `contact_activities` table and its migration (`003_contact_activities.sql`) were left in place in Supabase, unused — same treatment as the superseded `staff` table from `004`/`005`.
- **Lead Detail Save/Cancel moved into the top app bar — done 2026-10-02, verified on device.** Previously two full-width buttons at the very bottom of a long scrolling form (Save Changes, then Cancel) — now a back-arrow navigation icon (top-left, acts as Cancel/back) and a "Save" text action (top-right) on the existing `TopAppBar`, both reachable without scrolling. Same `canSave`/`saveChanges` logic as before, just relocated.
- **CSV export + share (A6 done)**: share icon on the Leads screen top bar exports whichever segmented tab is currently open (respects the same filter as the tab, e.g. "Follow-ups Due" exports just those) to a CSV in the app's cache dir, with human-readable labels (status/service/condition/etc. via the existing `*_LABELS` maps, not raw enum codes), then opens the Android share sheet via a new `FileProvider`. Deliberately CSV, not true `.xlsx` — avoids Apache POI's known Android incompatibilities (missing AWT/XML-stream classes, APK bloat) for a format Excel/Sheets/WhatsApp/email all open natively. New files: `ui/leads/LeadExport.kt`, `res/xml/file_paths.xml`; `AndroidManifest.xml` got a `FileProvider` entry. No new Gradle dependencies. Verified end-to-end on device.
- **Staff table + assignment (A8) — REMOVED 2026-09-24.** The app is used by exactly one admin, so the Staff screen, the `staff` table, `leads.assigned_staff_id`, the "Assigned to" dropdown and the by-staff report were deleted (`docs/sql/007_single_admin.sql` drops the DB objects; the code changes are in the app). Originally built 2026-08-25.
- **Reports/analytics (A9 done)**: bar-chart icon on the Leads screen top bar opens a Reports screen — overview (total, converted, conversion rate, median days-to-convert), pipeline funnel, breakdowns by source/service (count + conversion rate each), not-converted reasons ranked, an "Unmet demand" section (the supervisor's "why we lose families" report — lists `not_converted_detail` text for `amenity_missing`/`service_not_offered` leads), and budget distribution. All computed client-side from data already loaded (`ReportsAnalytics.kt`, pure functions) — no new backend calls. Verified end-to-end on device with test data.
- **Fixes from a friend's test report — 2026-10-04. Code BUILT BUT NOT YET RUN ON A DEVICE; the bridge change is LIVE in Google Apps Script.** A friend's Claude tested commit `2292017` and reported 10 bugs; each was re-checked against the current code (8 real, 2 skipped) — summary in `docs/HANDOFF.md` §3 (Oct 4). What changed:
  - **Budget parsing (bridge, live):** new `parseAmount()` replaces `parseFloat` — `20,000`→20000, `1,00,000`→100000, `30k`, `1.5 lakh`, `Rs. 25000`. 32 offline checks pass (`bridge_test4.js`, scratch) and the earlier bridge tests still pass. The live Apps Script was patched in the editor, checked to be **byte-identical to the repo file (sha16 `9e8d2bd235db4c08`, 11293 chars) after a full reload**, and the form-submit trigger was confirmed still installed. No real form submission has been run through it yet.
  - **`converted_at` (app):** editable "Converted on (date they moved in)" on Lead Detail when status is Converted, pre-filled with today; saved via `UpdateLeadRequest.convertedAt`. Makes "Median days to convert" work. Existing converted leads show today's date pre-filled until saved.
  - **`UpdateLeadRequest` has no default values now (app):** kotlinx's default JSON omits properties equal to their default, so clearing a field or un-ticking the last chip was never sent. Found by inspecting supabase-kt (no `encodeDefaults` anywhere in the jars), not reproduced on a device.
  - **Reminders (app):** `USE_EXACT_ALARM` added and `SCHEDULE_EXACT_ALARM` capped at API 32; Settings re-arms the alarm on resume; the check runs as unique work with a network constraint and `Result.retry()` (up to 5 retries) instead of failing silently when offline.
  - **App behaviour (app):** saved login restored at launch (spinner while checking); system Back returns to the list instead of closing the app; activity locked to portrait; the selected Leads tab now survives opening a lead; tapping the notification opens the lead (one due) or the Follow-ups Due tab (several) after a fresh load; `LeadsViewModel` no longer refreshes at construction (it ran before login and could leave a stale error), and `refresh()` takes an optional completion callback.
  - **Small:** CSV export neutralises cells starting `=`, `@`, `+`, `-` (but keeps `+91` and phone numbers); `windowLayoutInDisplayCutoutMode` moved to `values-v27/themes.xml` (lint error).
  - **Independent read-only review of this batch (2026-10-04):** no compile errors found (imports, the 29 named arguments, supabase-kt/androidx signatures checked against the real jars). It did find a bug in the new notification routing, **fixed** (`key(selected.id)` around `LeadDetailScreen`, so tapping a notification for lead B while lead A is open can't make Save write A's values over B). Two more review findings were then **fixed too** (same untested batch): if the saved login had expired and the phone was offline at launch (status `RefreshFailure`), the login form showed and never moved on when the connection returned — `MainActivity` now waits for `Authenticated` and goes in by itself unless the admin logged in by hand; and a slow notification-triggered refresh could switch your screen after you had already navigated — it now only navigates if you are still where you tapped. **Still open, known, left alone on purpose:** a configuration change other than rotation (dark-mode toggle, font size, language) recreates the activity and returns to the Leads list, dropping an open edit — rare, and fixing it needs `rememberSaveable` for ~25 form fields.
  - **Verify on the phone after building:** (1) log in, close the app, reopen — it should go straight in; (2) press Back on a lead — returns to the list; (3) open a lead, change status to Converted, Save, then Reports — "Median days to convert" shows a number; (4) clear a Comments note and Save — it stays cleared after reopening; (5) set the reminder a minute ahead with one lead due — the notification names them and tapping opens that lead; (6) submit a form test with budgets `20,000` and `1,00,000` — the lead shows 20000 / 100000 (delete that test lead afterwards).
- **Follow-up reminder notifications — reworked 2026-10-02, verified on device 2026-10-02.** Build installed, follow-up date set to today, reminder time set a few minutes out in the new Settings screen, notification fired with the correct lead name(s). Confirmed working. The original `WorkManager` periodic-check design (below, struck through) turned out to actually matter in practice — the supervisor hit random firing times daily once the feature was in real use — so it was replaced:
  - `notifications/NotificationScheduler.kt`: now schedules a single **exact alarm** (`AlarmManager.setExactAndAllowWhileIdle`) for a supervisor-configurable time (`NotificationPreferences.kt`, default 9:00, `SharedPreferences`-backed), instead of a 24h periodic `WorkManager` job.
  - `notifications/FollowUpAlarmReceiver.kt` (new): the alarm's `BroadcastReceiver`; hands off to a one-time `WorkManager` job (can't safely do network I/O directly in `onReceive`).
  - `notifications/FollowUpReminderWorker.kt`: now fetches the actual due leads (not just a count) and — critically — **always reschedules tomorrow's alarm in a `finally` block**, success or failure, so one bad run can't silently kill the whole daily chain.
  - `notifications/NotificationHelper.kt`: now takes the list of due leads and builds an `InboxStyle` notification listing each enquirer's (and patient's, if different) name, capped at 5 lines + "+N more" — not just a bare count.
  - `notifications/BootReceiver.kt` (new): re-arms the alarm after a device reboot (exact alarms don't survive one).
  - `ui/leads/SettingsScreen.kt` (new), reachable via a gear icon on the Leads screen top bar: lets the supervisor pick the reminder time (Material3 `TimePicker` in a dialog), and surfaces two Android reliability caveats with one-tap fixes — the Android 12+ exact-alarm permission, and battery-optimization whitelisting (common on Xiaomi/Vivo/Oppo/Samsung). Both checks re-run via a `DisposableEffect` + `LifecycleEventObserver` on `ON_RESUME`, so returning from the system Settings app updates the UI.
  - New manifest permissions: `SCHEDULE_EXACT_ALARM`, `RECEIVE_BOOT_COMPLETED`, `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`; two new receivers registered (`FollowUpAlarmReceiver`, `BootReceiver`).
  - **Honest framing, not oversold**: even an exact alarm can still be delayed if the phone's battery optimization hasn't been disabled for the app — this is "far more reliable than before," not "guaranteed to the minute." The Settings screen says this plainly.
  - **Verified 2026-10-02**: time picker + real alarm firing + correct lead name in the notification, all confirmed on device. **Still open, lower priority:** a longer-running patience-test (does it still fire correctly after a day or two, and does it survive a phone reboot — the boot receiver is untested in practice).
  - ~~Original (struck through, superseded): `WorkManager` periodic check, scheduled daily targeting ~9am, counted leads and fired a notification with just a count. Known, accepted limitation was that WorkManager doesn't guarantee exact timing. Verified end-to-end on device 2026-08-27.~~
- **Track A (the Android CRM) is now fully feature-complete.** Remaining: everything WhatsApp-automation-related (Track B/C/D, see `docs/ROADMAP.md` — none of it has started, and B/C need the supervisor/NGO to act, not Anhad).
- **Security fix (2026-09-17): self-signup gated by the active staff roster — SUPERSEDED 2026-09-24 by the single-admin model (no signup path in the app at all; signups disabled in Supabase; see 007).** Login previously fell back to creating a brand-new account for *any* name/password that failed sign-in — meaning anyone with the APK could get in and see every family's medical/financial data, since every RLS policy just checks "is someone logged in," not "are they staff." Fixed via a new `is_active_staff_name` SQL function (`docs/sql/005_staff_name_check_rpc.sql`, SECURITY DEFINER, returns only a boolean, callable pre-auth) — signup is now blocked unless the typed name matches an active row in `staff`. Also added a logout confirmation dialog. Built and verified on device in a prior session; already deployed to Supabase.
- **Google Form intake bridge (done 2026-09-17; simplified 2026-09-25: Country code question removed, several options removed, all questions numbered, two optional date questions added (follow-up date, already-visited date; 24 in total; 7 marked required), and the bridge now reads real Date values instead of parsing regional date text - see docs/GOOGLE_FORM_INTAKE_SPEC.md), which replaced the in-app "New Enquiry" screen.** The enquirer now fills in their own details via a Google Form (delivery mechanism — how the link reaches them over WhatsApp — still undecided, parked) rather than staff re-typing everything into the app; applies to phone/walk-in enquiries too via the same form. `docs/GOOGLE_FORM_INTAKE_SPEC.md` has the exact 23-question spec; `tools/google-form-bridge/KalazaFormBridge.gs` is the Apps Script bridge that inserts form responses into the same `leads` table; `docs/sql/006_google_form_anon_insert.sql` grants the narrow anon-insert-only RLS exception this needs (already run in Supabase). Form built and published, bridge script pasted in, Script Properties set, `setupTrigger()` run, and a real test submission ("TEST Anhad") verified end-to-end: landed in the app's Leads "All" tab with every field — including the checkbox multi-selects (conditions/service/amenities) — saved correctly as arrays, and the Apps Script Executions log showed a clean Completed run. **The in-app "New Enquiry" screen has now been removed (2026-09-17)** — `AddLeadScreen.kt` deleted, plus the now-dead `addLead`/`NewLeadRequest` plumbing across `LeadsViewModel`, `LeadsRepository`/`SupabaseLeadsRepository`, and `Lead.kt`, and the FAB + `Screen.ADD_LEAD` wiring in `LeadsScreen.kt`/`MainActivity.kt`. The Google Form is now the only way new leads enter the system. `LeadFormOptions.kt`'s shared enum/label lists are untouched — still used by `LeadDetailScreen` (edit) and CSV export labels.

If you're picking this up fresh: pull latest, open the project at `C:\Dev\kalaza-leads` (see §4 — **not** the OneDrive folder), build, install on the connected device, and you should be able to log in and see leads (the single admin account must exist in Supabase Auth, see `docs/HANDOFF.md` section 7.4). New leads arrive via the Google Form, not in the app.

---

## 2. Architecture

Kotlin + Jetpack Compose (Material 3), MVVM. Package `com.kalazacare.leads`.

```
app/src/main/java/com/kalazacare/leads/
  KalazaLeadsApp.kt                    Application class
  data/
    model/Lead.kt                      Lead (read) + UpdateLeadRequest (edit payload)
    remote/SupabaseClients.kt          SupabaseClient singleton, reads URL/key from BuildConfig
    repository/
      AuthRepository.kt / SupabaseAuthRepository.kt
      LeadsRepository.kt / SupabaseLeadsRepository.kt
  ui/
    MainActivity.kt                    Screen state machine: LOGIN / LEADS / LEAD_DETAIL / STAFF / REPORTS
    login/LoginScreen.kt, LoginViewModel.kt
    leads/LeadsScreen.kt, LeadsViewModel.kt
    theme/                             Teal Material3 theme (deliberately distinct from Kalaza Care's red)
```

**Pattern**: each feature is Repository (talks to Supabase) → ViewModel (StateFlow of a `*State` data class) → Composable Screen (collects state, renders, calls ViewModel methods). `MainActivity` creates all repositories/ViewModels once in `onCreate` and passes them down — no DI framework, deliberately simple for a solo student project.

**Navigation**: no NavHost. `MainActivity` holds a `private enum class Screen { LOGIN, LEADS, LEAD_DETAIL, STAFF, REPORTS }` as a `mutableStateOf`, and a `when` picks which Composable to show. Fine at this size; consider Compose Navigation if the screen count grows. (This tree is abridged - the complete, current file map is in `docs/HANDOFF.md` section 6.)

---

## 3. Supabase backend

- **Project**: `niqhlkdyaklnngcanxld.supabase.co`, org "Anhad23mahajan's Org", region South Asia (Mumbai). Created 2026-08-23 as a **standalone project**, not shared with Kalaza Care (that was considered — see §6 decisions log).
- **Credentials** live only in `local.properties` (gitignored) → injected into `BuildConfig.SUPABASE_URL` / `BuildConfig.SUPABASE_ANON_KEY` at build time. See `local.properties.example` for the exact keys needed. Never hardcode these.
- **`leads` table**: originally 18 columns (`docs/sql/001_leads_table.sql`), migrated 2026-08-25 to the wider v2 schema per `docs/sql/002_leads_v2_migration.sql` — see `Lead.kt` for the current, authoritative column list.
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

- Repo: `github.com/Anhad23mahajan/kalaza-leads`, owner's own account (not a friend's account, which was originally considered — see §6).
- No standing credentials are stored anywhere. Every push uses a short-lived fine-grained GitHub PAT: user generates one scoped to just this repo (Contents: Read and write, shortest expiration) at `github.com/settings/personal-access-tokens/new`, pastes it into chat, it's used once in the remote URL (`https://x-access-token:TOKEN@github.com/...`), then immediately stripped from git config (`git remote set-url origin` back to the plain HTTPS URL) and the user revokes it on GitHub.
- Git identity: `Anhad Mahajan` / `anhadagammahajan@gmail.com` (set via `git config --global`).
- Because two local copies of the repo exist (§4), remember to sync both: commit+push from whichever copy was edited, then `git pull origin main` in the other one before building/testing there.

---

## 6. Key decisions and why

- **Standalone Supabase project, not shared with Kalaza Care.** The spec's original preference was sharing one project (clean lead→resident handoff). Went standalone instead because Harsh (who has Kalaza Care's credentials) wasn't available, and waiting would have blocked all progress. This is a **reversible** decision — migrating to Kalaza Care's project later is possible, just needs re-pointing `local.properties` and re-running the leads table SQL there.
- **Not migrating to a friend's Claude Code cloud account.** Originally considered, but the user chose to keep building step-by-step in this local session instead — "slow and steady wins the game." If a handoff to that account happens later, this file is exactly what should be read first there.
- **Teal theme, not Kalaza Care's red.** Deliberate — the two apps install side by side on the same phone; staff need to tell them apart at a glance.
- **(Historical - superseded 2026-09-24: single admin.) No staff/roles table yet.** Every Supabase Auth account is currently treated as trusted staff (RLS just checks `auth.uid() is not null`). Fine for now since nothing else can create accounts through this app; revisit before any real deployment.
- **wa.me deep links for MVP messaging, not WhatsApp Business API.** Zero cost, zero ban risk, ~90% of the value. Built and shipped 2026-08-25 (A7, see §1 above).

---

## 7. What to build next

**Superseded 2026-09-18** by `docs/ROADMAP.md` — read that for the current plan and order of work. The list below is the historical Track A checklist, all done:

1. ~~**A1 — schema migration + form overhaul.**~~ **Done 2026-08-25.** Migrated to the v2 schema, form rebuilt with 11/13 supervisor UI requests.
2. ~~**A2 — Lead detail + edit screen.**~~ **Done 2026-08-25.** Tap a card, edit any field, change status, save.
3. ~~**A3 — Contact activity log.**~~ **Done 2026-08-25.** Log calls/WhatsApp/visits with outcome + notes, timeline shown on Lead Detail.
4. ~~**A4 — Follow-up-due home screen** + push notifications.~~ **Done.** List folded into A5's segmented tabs (2026-08-25). Notifications done 2026-08-27 as local `WorkManager` daily checks, not true FCM server push (no backend exists yet — that's Track D); see the known-limitation note above on timing precision.
5. ~~**A5 — Segmented list views.**~~ **Done 2026-08-25.** 7 tabs with live counts, matching the supervisor's requested lists exactly.
6. ~~**A6 — Excel export.**~~ **Done 2026-08-25.** CSV export + share from any segmented tab, human-readable labels, no new dependencies.
7. ~~**A7 — `wa.me` one-tap messaging.**~~ **Done 2026-08-25** — built ahead of A6 (assessed as lower-risk, more immediately valuable). Three templated messages, opens WhatsApp pre-filled.
8. ~~**A8 — Staff table** + basic roles/assignment.~~ **Done 2026-08-25; removed 2026-09-24 (single admin).** Staff CRUD screen, active/inactive toggle, lead assignment dropdown.
9. ~~**A9 — Reports/analytics screen.**~~ **Done 2026-08-25.** Overview, funnel, source/service/staff breakdowns, not-converted reasons, unmet demand, budget distribution.

**Track A is complete and is now simply "the app."** Tracks B/C/D described below this point were the plan as of Sep 2026 — **all abandoned 2026-10-02** after real-world Meta setup (Business Portfolio, Developer App, phone-number registration) hit compounding blockers. See `docs/HANDOFF.md` §15 for the full story. **Current work (per `docs/ROADMAP.md`, rewritten 2026-10-02):** fix the follow-up notification system (wrong timing, no lead details shown), redesign the WhatsApp quick-message feature (3 fixed buttons → a categorized expandable list), then a final ownership handoff of the Google Form/Sheet/Script and the Supabase project to the NGO.

~~Meanwhile, **Track B (Meta/WhatsApp onboarding) and Track C (NGO content — the ~20 FAQ answers, price list, posters)** need to start now too, in parallel, driven by the supervisor/NGO — not by Anhad — since they're the long pole per `docs/AUTOMATION_DESIGN.md`'s risk table. Track D (the actual automation) is gated on both being done.~~ (Historical — this paragraph described the abandoned plan; `AUTOMATION_DESIGN.md` itself was deleted 2026-10-02.)
