# Kalaza Leads — Roadmap (what's done, what's next)

Rewritten 2026-10-02 after a major scope change. Everything below this point reflects the
**current, simplified project** — not the earlier WhatsApp-bot plan. If you see a reference
to "Track B/C/D" anywhere else in this repo, it describes a path that was **abandoned**
(see `docs/HANDOFF.md` §15 for why) — ignore it, this file is authoritative for "what's next."

---

## 1. The pivot (read this first)

For about six weeks the plan was: CRM first (Track A, done), then a Meta/WhatsApp Business
Platform auto-reply bot (Tracks B/C/D). On 2026-10-01 Anhad and the supervisor began the real
Meta setup (Business Portfolio, Developer App, phone-number registration) and hit a wall of
compounding problems — number-migration errors, unclear payment-method rules, business
verification paperwork, and a general sense that none of it was going to resolve quickly for
a solo student project. **On 2026-10-02, Anhad decided to drop the WhatsApp Business Platform
integration entirely.** No Meta account, no Cloud API, no auto-reply bot. That part of the
project is over, not paused.

**What replaces it — the project is now three simple things:**
1. **Google Form → Supabase intake.** Already built and working. No changes planned.
2. **Follow-up reminder notifications**, fixed and made supervisor-configurable.
3. **A redesigned WhatsApp quick-message feature** — a categorized list of pre-filled message
   templates the supervisor picks from and sends manually (replacing the old fixed 3 buttons).

Plus a one-time **end-of-project handoff**: moving the Google Form/Sheet/Script and the
Supabase project from Anhad's personal accounts to the NGO's own accounts, so the supervisor
is fully independent once Anhad is no longer actively maintaining it.

No Meta Business Portfolio, Developer App, or phone-number work is needed anywhere in this
plan. (The half-finished "Kalaza Care Assistant" Meta app created on 2026-10-01 was never
verified or billed — it can simply be ignored or deleted later; it isn't referenced by
anything in this repo.)

---

## 2. Where we are

| Area | State |
|---|---|
| **Android CRM** | Done, tested on a real device, on GitHub. Leads (7 tabs), detail/edit, CSV export, reports. (Contact log was removed 2026-10-02 — see §4.) |
| **Security** | Single admin: no signup in the app, signups disabled in Supabase (`docs/sql/007_single_admin.sql`). |
| **Intake** | Done. Google Form (24 questions) → Apps Script → Supabase `leads`. No changes planned. |
| **Follow-up notifications** | **Fixed and verified on device (2026-10-02).** Exact-alarm timing, supervisor-configurable via a Settings screen, shows real lead names. See §3. |
| **WhatsApp quick-messages** | **Redesigned and verified on device (2026-10-02).** 7 templates in 4 categories, expandable list. See §4. |
| **WhatsApp Business Platform / auto-reply bot** | **Abandoned 2026-10-02.** Not part of the project anymore. |
| **Final handoff to the NGO** | Not started. See §5. |
| **Usability pass (2026-10-05)** | Built: search, delete lead, "Changes saved" snackbar, discard-changes warning, refresh button + auto-refresh, age/budget checks, crash screen, 2×2 Reports overview. See §4b. |

---

## 3. Workstream 1 — Fix the follow-up notification system

**Status: timing fix, settings screen and lead names are done, verified on device 2026-10-02.**
See `docs/PROGRESS.md` §1 for the exact files changed and what was tested. Still open (lower
priority, not blocking): a longer patience-test across a day or two, and confirming the boot
receiver actually re-arms the alarm after a real phone restart.

**Correction (2026-10-04): one item below was not built when this section was marked done.**
"Tapping the notification jumps to the due lead" (item 3's last clause) was skipped on
2026-10-02 and not recorded. It was found by a friend's test report and is now built (tap opens
the lead when one is due, or the Follow-ups Due tab when several are) — **built, not yet verified
on the phone.** The same report led to the other fixes listed in §3b below.

### 3b. Fixes from the friend's test report (2026-10-04) — verified on the phone 2026-10-04 (Anhad: "all worked"; the optional live-form budget test was not separately confirmed)

| Finding | Status |
|---|---|
| Budgets typed with commas / "k" / "lakh" saved wrong (`20,000` → 20) | Fixed in the bridge and **live in Google Apps Script** (checked identical to the repo); a real form submission still to be run through it |
| "Median days to convert" always blank — nothing set `converted_at` | Fixed: editable "Converted on" date on Lead Detail when status is Converted |
| Exact reminders off by default on Android 14+; not re-armed after granting | Fixed: `USE_EXACT_ALARM`, and Settings re-arms on resume |
| Reminder lost if the phone is offline at that time | Fixed: waits for a network, retries up to 5 times |
| App opens on login every time; rotation resets it; Back exits the app | Fixed: saved login restored, portrait lock, Back handler |
| Tapping the notification doesn't open the lead | Fixed (see above) |
| Lint error in `themes.xml` | Fixed |
| CSV cells starting `=` `+` `-` `@` run as formulas in Excel | Fixed, keeping `+91`/phone numbers intact |
| Typed impossible date (31/02) rejects a submission | Not fixed on purpose: the form's date picker can't produce one |
| *(Found while checking, not in the report)* clearing a field and saving didn't clear it, because the update request omitted values equal to their defaults | Fixed: `UpdateLeadRequest` has no defaults now |

Not done on purpose: **no number validation on the form's budget questions** — the bridge now
reads `30k` / `1,00,000` / `1.5 lakh`, and strict validation would reject exactly what people type.

**Why it was broken:** the current implementation uses Android's `WorkManager` in periodic mode
(`NotificationScheduler.kt`), which Android deliberately does not run at a fixed clock time —
it batches/defers background jobs to save battery, and the drift compounds run over run. This
is worse on Indian Android OEM skins (Xiaomi/Vivo/Oppo/Samsung), which are more aggressive
about delaying background work than stock Android. This was always a known limitation
(`FollowUpReminderWorker.kt` even has a comment acknowledging it), but it's now a real problem
the supervisor is hitting daily.

**What to build:**
1. **Replace periodic WorkManager with a self-rescheduling exact alarm.** Each time the check
   runs, it immediately schedules the *next* one for the exact target time, using Android's
   exact-alarm API. Still needs the app whitelisted from battery optimization on the phone (a
   one-time setting) for real reliability — explain this honestly, don't oversell "guaranteed."
2. **A Settings screen** where the supervisor sets what time he wants the daily check to run.
   Persist with DataStore/SharedPreferences; the scheduler reads it. (Frequency/multiple-times-
   a-day can be added later if he asks for it — start with "one configurable time per day.")
3. **Show actual lead names/details in the notification**, not just a count. Currently
   `NotificationHelper.kt` shows "3 follow-ups due" with generic body text and a fixed
   notification ID (so a second run overwrites rather than stacks). Needs to list who's due
   (e.g. an `InboxStyle` notification with one line per lead) and tapping it should jump to
   that lead's detail screen when there's exactly one, or a filtered list when there's more
   than one.

## 4. Workstream 2 — Redesign the WhatsApp quick-message feature

**Status: the 7-template categorized list is done, verified on device 2026-10-02.** See
`docs/PROGRESS.md` §1 for the exact files.

**Also done the same day, off the back of seeing it on the phone, verified on device:** two more
changes to the same Lead Detail screen, both at the supervisor's request after testing the
message list above:
1. **The Contact Log section was removed entirely** (he found it unnecessary). Deleted
   `ContactLogSection.kt`, `ActivitiesViewModel.kt`, `ContactActivitiesRepository`/
   `SupabaseContactActivitiesRepository.kt`, `ContactActivity.kt`, and the dead `ACTIVITY_*`
   lists in `LeadFormOptions.kt`. The `contact_activities` table itself was left in Supabase,
   unused (same treatment as the old `staff` table).
2. **Save/Cancel moved from the bottom of the long scrolling form into the top app bar** — a
   back arrow (top-left) acts as Cancel/back, and a "Save" text action sits top-right, both
   reachable without scrolling to the bottom.

**Also done, verified:** the Leads screen's default/first tab was changed from "Follow-ups Due"
to "All," per the supervisor's preference for landing on the full list when opening the app.

**Previous state (now replaced):** `WhatsAppHelper.kt` defines exactly 3 static templates (Thank You,
Follow-up, Visit Feedback), rendered as 3 always-visible buttons in `LeadDetailScreen.kt`'s
"Send WhatsApp" section, regardless of the lead's pipeline stage.

**What the supervisor wants instead:** an expandable/dropdown list of message templates,
grouped by the actual conversation context — e.g. "they seem interested" (a follow-up nudge),
"they've gone quiet" (a check-in), "they're not considering" (a why/what-happened message),
plus keep Thank You and Visit Feedback. He picks the one that matches what actually happened
with that family, previews/edits it, and sends manually via WhatsApp — same "review before
sending" behavior as today, just a richer menu instead of 3 fixed icons. All templates should
probably stay visible regardless of stage (his call, confirmed 2026-10-02) rather than being
gated by status — he wants the freedom to pick any message at any time.

**What to build:**
1. Expand `WhatsAppTemplate` from 3 fixed entries into a larger, categorized set.
2. Replace the 3-button row in `LeadDetailScreen.kt` with an expandable list/dropdown UI.
3. **Open item:** the exact wording for the new template categories needs the supervisor's
   input eventually (similar in spirit to the old "~20 FAQ answers" ask, but much smaller —
   probably 6–10 short templates). Draft sensible placeholder wording to unblock building the
   UI now; swap in his real wording once he gives it. Doesn't need to block starting the work.

## 4b. Usability pass (2026-10-05)

Gaps found by comparing this app with Kalaza Care, chosen by Harsh (taking over from Anhad).
Deliberately **not** done: idle auto-logout, offline cache, version scheme, server push, roles/audit/Wi-Fi gate.

1. **Search** (magnifier in the Leads top bar): matches enquirer name, patient name, location, and phone digits (3+).
   Applies across all tabs; tab counts follow the search.
2. **Delete lead** (bottom of Lead Detail, red outline button) with a "can't be undone" confirmation. The delete
   asks Supabase to return the deleted row, because RLS silently ignores a delete it doesn't allow.
3. **Snackbar** after save ("Changes saved") and delete ("Lead deleted"); save/delete errors also show there
   (they used to sit at the very bottom of the long edit form, out of sight).
4. **Discard-changes warning** on Back / the back arrow when the form has unsaved edits.
5. **Refresh**: button in the top bar, plus automatic reload when the app returns to the foreground and every
   60 s while the list is on screen. A failed reload keeps the old list and shows a red "Couldn't refresh" strip.
6. **Input checks**: age at most 120; budget max must be at least the min. Save is disabled until fixed.
7. **Crash screen** (`CrashHandler` / `CrashActivity`, ported from Kalaza Care): plain message, Restart,
   Share details, technical details folded away; a crash within 5 s of another just closes (no loop).

8. **CSV download** (Harsh, 2026-10-05): the export button (download icon) now saves straight to the phone's
   **Downloads** folder via MediaStore (Android 10+, no permission) and confirms with a snackbar; Android 8-9 phones
   still get the share sheet. The file starts with a UTF-8 BOM so Excel shows ₹ and Hindi/Marathi names correctly.

Found and fixed while testing on the phone (Vivo, Android 14):
- "Due today" / "Overdue" was shown on closed leads (Not Converted, Dormant) while the Follow-ups Due tab said 0.
- Dates read "2 Oct 2026" instead of `2026-10-02` (cards, date fields, the confirm-visit WhatsApp message).
- A date could be set but never removed: the date picker now has **Clear**.
- On Lead Detail the keyboard covered the field being typed in, and the bottom of the form sat under the nav bar.
- The search box now takes focus when opened, and Back closes it instead of leaving the app.
- WhatsApp templates no longer contain "--".

Verified on the phone 2026-10-05: search (name, phone digits, patient, no-match), Back closes search, refresh button,
auto-refresh on launch / on return / every 60 s (logcat timestamps), CSV saved to Downloads (3 rows, 34 columns, BOM),
age > 120 and budget min > max block Save with inline errors, discard dialog (Keep editing / Discard), delete dialog
(opened and cancelled), save + "Changes saved" + clearing a field really clears it, Reports 2×2, Settings, WhatsApp list,
crash screen via `adb shell am crash` (details + Restart back into the app, still logged in).
Actual delete verified 2026-10-05: "TEST Round Three" deleted from the app, "Lead deleted" snackbar, list 3 → 2, and the row is gone in Supabase.

**Bridge budget fix (2026-10-05, repo only — the LIVE Apps Script still has the old line):** `parseAmount()` turned "25000rs" / "25000inr" / "40000pm" into 2500 / 2500 / 4000 (the regex backtracked a digit to satisfy its no-letter-after rule). Fixed with `(?!\d)` and a unit-only letter check; 25 budget cases pass offline. Until the live script is updated (see CLAUDE.md "Facts that bite" for the steps), the old behaviour is what families' submissions get.

Also: Reports overview is a 2×2 grid of tiles (the four stats in one row ran into each other), and list
loading vs saving are separate states, so a background refresh never disables the Save button.

## 5. Workstream 3 — End-of-project handoff to the NGO

Three separate ownership surfaces need to move before Anhad hands this off — don't conflate
them, they live on different services:

| # | What | Currently owned by | Needs to move to |
|---|---|---|---|
| A | **Google Form + response Sheet + Apps Script bridge** | Anhad's personal Gmail (`anhadmahajan36@gmail.com`) | An NGO-owned Google account — this is the one holding real family contact/medical data |
| B | **Supabase project** (database, API keys, dashboard) | Anhad's Supabase account | Supervisor/NGO's own Supabase account, added as full Owner |
| C | **The app's internal single-admin login** (just a row in the database — not an external account) | `anhad@kalazaleads.app` | A new login created for whichever name the supervisor will use (e.g. `somnath@kalazaleads.app`) — trivial, one-time, no external sign-up needed |

The GitHub repository stays under Anhad's own account (portfolio project) — the NGO never
needs source-code access, only the finished app and their own data. Flag this assumption
before acting on it if it's ever unclear.

**Also re-decide before handoff:** the Supabase free-tier pause risk (pauses after 7 days with
zero DB queries; zero-day backup retention on Free). With the supervisor using the app
regularly day-to-day, this is less likely to trigger than it was under the old plan, but it's
still worth a final explicit yes/no before real data depends on it, not an assumption.

---

## 6. Decisions still open

| # | Question | Who | Blocks |
|---|---|---|---|
| 1 | Exact wording for the new WhatsApp message-template categories | Supervisor | Workstream 2's final content (not the build) |
| 2 | Who owns the new NGO Google account for the Form/Sheet/Script | Supervisor/NGO | Workstream 3A |
| 3 | Does the Supabase free-tier pause risk matter given real daily app use? | Anhad | Workstream 3, final go-live |
| 4 | Does the supervisor want more than one notification time per day? | Supervisor | Workstream 1's settings screen scope (default: no, start with one) |

## 7. Known and accepted (not planned work)

- No WhatsApp Business Platform / Cloud API / Meta Business account anywhere in this project — dropped for good, not deferred.
- There is one admin account; RLS is `auth.uid() is not null` and signups are disabled.
- Anyone holding the anon key can insert junk leads via the form bridge (not read/edit/delete) — accepted risk, unchanged.
- `Realtime` is installed but unused.
- Exact-alarm notifications still cannot be *guaranteed* to the minute on all Android phones — battery optimization on some OEM skins can still delay them even when done correctly. Be honest about this with the supervisor; "far more reliable than today" is the right framing, not "guaranteed."

## 8. Suggested order, in one line

Fix follow-up notification timing + add the settings screen + show lead names → redesign the WhatsApp message list → test both on the phone → do the three-part ownership handoff (Google account, Supabase, admin login) once everything is verified working.
