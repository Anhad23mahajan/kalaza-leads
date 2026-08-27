# Track A — Technical Build Log

A sequential script of everything built in Track A (the Android CRM), for
group-mates / anyone technical picking this up. For full ongoing detail see
`docs/PROGRESS.md`; this file is the condensed, presentation-ready walkthrough
in build order. For the non-technical version (for the NGO supervisor), see
`docs/SUPERVISOR_DEMO_SCRIPT_TRACK_A.md`.

**Status: Track A is 100% complete** — auth through follow-up notifications,
all verified end-to-end on a real Android device.

---

## 0. Project & architecture

- **What it is**: Kalaza Leads, a staff-facing Android CRM for Kalaza Care, an
  elder-care NGO in Pune. Solo project, one developer.
- **Stack**: Kotlin + Jetpack Compose (Material 3), MVVM, package
  `com.kalazacare.leads`.
- **No DI framework, no NavHost.** `MainActivity` holds a private
  `enum class Screen { LOGIN, LEADS, ADD_LEAD, LEAD_DETAIL, STAFF, REPORTS }`
  as `mutableStateOf`, and a `when` picks which Composable to render.
  Deliberately simple for a solo project at this screen count.
- **Pattern per feature**: `Repository` (talks to Supabase) → `ViewModel`
  (exposes a `StateFlow<XState>` data class) → Composable `Screen` (collects
  state, renders, calls ViewModel methods). `MainActivity.onCreate` wires up
  every repository/ViewModel once and passes them down.
- **Backend**: Supabase (hosted Postgres). Plugins installed: `Auth`,
  `Postgrest` (all CRUD), `Realtime` (installed, unused so far — no
  live-sync feature has needed it yet).
- **RLS posture (deliberate MVP simplification)**: every table has
  `auth.uid() is not null` as its only policy — any signed-in account is
  trusted staff. No roles/permissions enforcement yet beyond the basic
  `staff.role` field added in A8 (not yet wired into access control, just
  informational/UI-level).
- **Theme**: teal, deliberately distinct from the older "Kalaza Care" app's
  red, since both install on the same staff phones.

---

## 1. Auth

Staff sign up / log in / log out. Supabase Auth is email/password under the
hood, but staff log in by **name only** — the app synthesizes an email
(`{name}@kalazaleads.app`) since a real inbox isn't needed (email
confirmation is off on this Supabase project). No roles yet; every account
is equally trusted.

---

## 2. A1 — Schema migration + Add Enquiry form

- `leads` table migrated to a v2 schema (`docs/sql/002_leads_v2_migration.sql`)
  per the supervisor's post-review requirements: split `contact_channel` /
  `how_heard` (call vs. how they found us are different facts), expanded
  service/condition/amenity option lists, budget range, visit/follow-up
  dates, etc.
- Add Enquiry form covers 11 of the supervisor's 13 explicit UI requests:
  country code + 10-digit phone validation, split contact-channel/how-heard,
  8 service types, structured patient conditions (multi-select), room type,
  relation-to-patient dropdown, medical history, visit/follow-up date
  pickers, multi-select chips that wrap instead of horizontal-scrolling.
- Reusable form components (`FormComponents.kt`): `EnumDropdown`,
  `MultiSelectChips`, `DateField` (Material3 `DatePickerDialog`, stores ISO
  `yyyy-MM-dd` strings).

---

## 3. A2 — Lead Detail / Edit screen

Tapping a lead card opens a full detail/edit screen: every Add-form field,
plus Status (with conditional not-converted reason/detail fields), planned
vs. actual visit date, final remarks. Saves via `UpdateLeadRequest`.

---

## 4. A3 — Contact Activity Log

New `contact_activities` table (`docs/sql/003_contact_activities.sql`),
FK'd to `leads.id`. A "Contact Log" section on Lead Detail lets staff log a
call/WhatsApp/visit/email/SMS with direction, outcome, optional callback
date, and notes; shows the full timeline for that lead. This is the "proof"
feature — evidence follow-up actually happened.

---

## 5. A4 — Follow-up reminders (two parts, built at different times)

**Part 1 — the list** (done alongside A5): a "Follow-ups Due" segmented tab,
purely client-side filtering (`nextFollowUpDate <= today && status not
terminal`), sorted by due date.

**Part 2 — local notifications** (done last, after A9): a `WorkManager`
periodic job checks Supabase daily (~9am target) for leads with a due
follow-up and fires a device notification if any exist.

- `notifications/FollowUpReminderWorker.kt` — a `CoroutineWorker`. Calls
  `SupabaseClients.main.auth.awaitInitialization()` before querying, to
  avoid racing the persisted-session restore on cold start; checks
  `sessionStatus.value is SessionStatus.Authenticated` and no-ops quietly
  if nobody's logged in. Queries `leads` with `lte("next_follow_up_date",
  today)`, filters out terminal statuses client-side, and calls
  `NotificationHelper.showFollowUpReminder` if count > 0.
- `notifications/NotificationHelper.kt` — builds the notification
  (`NotificationCompat`), guards `POST_NOTIFICATIONS` permission on API 33+,
  tapping it opens `MainActivity`.
- `notifications/NotificationScheduler.kt` — schedules a
  `PeriodicWorkRequest` (24h interval) via `WorkManager`, with an initial
  delay computed to target the next 9am. Called once from
  `KalazaLeadsApp.onCreate()`.
- `MainActivity` requests `POST_NOTIFICATIONS` at launch on API 33+
  (`ActivityResultContracts.RequestPermission`).
- **Deliberately not real push (FCM).** True server-push needs a backend
  that doesn't exist yet — that's Track D, gated on Track B/C. This is a
  client-only stand-in that satisfies the actual ask ("staff should get
  notified") without needing any of that.
- **Known limitation, accepted as-is**: `WorkManager` periodic work is
  best-effort, not an exact alarm — Android's Doze mode can defer the first
  daily run by several hours. Confirmed on device: fired same-day evening
  instead of ~9am. Staff still get notified same-day, which was judged
  good enough; upgrading to `AlarmManager` exact alarms (which would need a
  separate Android 12+ user-permission grant flow) was considered and
  explicitly deferred unless timing precision turns out to matter in
  practice.
- Before writing this, the exact Supabase Auth API (`awaitInitialization`,
  `SessionStatus` sealed subtypes, `PostgrestFilterBuilder.lte`) was
  verified by inspecting the actual cached library jars
  (`~/.gradle/caches/.../auth-kt-api.jar`, `postgrest-kt-api.jar`) rather
  than guessed — worth it given the tight edit-build-test loop (see §9).

---

## 6. A5 — Segmented pipeline views

Leads screen has 7 scrollable tabs with live counts: **Follow-ups Due, All,
Active (in-pipeline), Converted, Not Converted, Dormant, Backup** — matching
the separate lists the supervisor explicitly asked for. Not Converted cards
show the reason inline. Purely client-side filtering on data already
fetched (`LeadsScreen.kt`'s `SEGMENTS` list of `(label, filter fn)` pairs).

---

## 7. A7 — `wa.me` one-tap WhatsApp messaging

Built *ahead* of A6 (assessed as lower-risk, higher immediate value). "Send
WhatsApp" section on Lead Detail with three static templated draft messages
(Thank You, Follow-up, Visit Feedback), personalized with the lead's first
name and service. Opens WhatsApp via a `wa.me` deep link with the message
pre-filled — staff review/edit in WhatsApp itself before sending. Zero
cost, zero Meta dependency, works today. (`WhatsAppHelper.kt`)

---

## 8. A6 — CSV export + share

Share icon on the Leads screen top bar exports whichever segmented tab is
currently open (respects that tab's filter) to CSV, with human-readable
labels via the existing `*_LABELS` maps (not raw enum codes), then opens
the Android share sheet via a `FileProvider`.

- **Deliberately CSV, not true `.xlsx`.** Apache POI on Android has known
  incompatibilities (missing AWT/XML-stream classes, needs desugaring,
  bloats the APK) and any dependency issue would only surface after a full
  build-and-test round trip given this project's build constraints (§9).
  CSV opens natively in Excel, Sheets, WhatsApp, and email previews, and
  needs zero new Gradle dependencies.
- New: `LeadExport.kt`, `res/xml/file_paths.xml`; `AndroidManifest.xml`
  got a `FileProvider` entry.

---

## 9. A8 — Staff table + basic roles/assignment

- New `staff` table (`docs/sql/004_staff_table.sql`): name, phone, role
  (admin/coordinator/viewer), is_active.
- New Staff screen (people icon on Leads top bar): add/edit staff via a
  dialog, active/inactive toggle.
- Lead Detail gained an "Assigned to (follow-up person)" dropdown, scoped
  to active staff only — matches the supervisor's Excel "follow-up person"
  column.
- **Schema note**: `leads.assigned_staff_id` had a stray FK to
  `auth.users(id)` from the A1 migration, written before a staff table
  existed. The A8 migration repoints it to `staff(id)` — safe, since the
  assignment UI never shipped before this, so the column was null on every
  row. Assignment is to a roster entry, not necessarily someone with an
  app login.

---

## 10. A9 — Reports / analytics screen

Bar-chart icon on the Leads top bar. **All computed client-side from data
already loaded** (`ReportsAnalytics.kt` — pure functions, no new backend
calls):

- **Overview**: total enquiries, converted count, conversion rate (of
  decided leads), median days-to-convert (from the DB's generated
  `days_to_convert` column).
- **Pipeline funnel**: count per status, in pipeline order.
- **Breakdowns** (count + conversion rate each): by source (`how_heard`),
  by service requested (multi-membership — a lead counts toward every
  service it listed), by assigned staff (uses A8's staff roster).
- **Not-converted reasons, ranked.**
- **"Unmet demand"** — the Master Plan's flagged "why we lose families"
  report: lists the free-text `not_converted_detail` for every lead whose
  reason was `amenity_missing` or `service_not_offered`.
- **Budget distribution** — bucketed histogram from `budget_min`/`budget_max`.

UI: proportional bar rows (`Box` width fraction, no charting library) inside
`Card`-wrapped sections, built on `ReportsScreen.kt`.

---

## 11. Dev environment reality (worth knowing before touching this repo)

- **Canonical build location: `C:\Dev\kalaza-leads`**, not the OneDrive
  copy the project started in. Two local copies exist with the same GitHub
  remote — always confirm which one you're editing.
- **`JAVA_HOME` must be set to Android Studio's bundled JBR**
  (`C:\Program Files\Android\Android Studio\jbr`) before any `gradlew`
  command — system default is JDK 26, which Gradle 8.11.1 can't run.
- **Gradle cannot be run from inside an agent/automation subprocess** on
  this machine — it fails with a loopback socket `IOException`. Every build
  in this project's history has been the human running `gradlew` directly
  and pasting output back.
- **`Unable to delete directory` on `app\build\...`** happens often — a
  Windows Search Indexer race, not OneDrive. Fixed with a delete-and-retry
  loop around `gradlew.bat assembleDebug --no-daemon` (see
  `docs/PROGRESS.md` §4 for the exact script).
- **Every push uses a short-lived, single-repo-scoped GitHub PAT**, used
  once in the remote URL, then immediately stripped back to plain HTTPS —
  no standing credentials anywhere.

---

## 12. Full file/schema inventory

**SQL migrations** (`docs/sql/`): `001_leads_table.sql` (v1, superseded),
`002_leads_v2_migration.sql`, `003_contact_activities.sql`,
`004_staff_table.sql`.

**New Gradle dependency added in Track A**: `androidx.work:work-runtime-ktx`
(A4 part 2). Everything else (CSV export, staff CRUD, reports) shipped with
zero new dependencies.

**Key source additions by feature**: see `docs/PROGRESS.md` §1 for the
per-feature file list; this document's per-section notes above cover the
same ground in narrative form.

---

## What's next

Track A has no more unblocked work. Track B (Meta/WhatsApp onboarding) and
Track C (NGO-authored content) are supervisor/NGO-driven, not code — see
`docs/SUPERVISOR_SCRIPT_TRACK_BC.md`. Track D (the actual automation) is
gated on both.
