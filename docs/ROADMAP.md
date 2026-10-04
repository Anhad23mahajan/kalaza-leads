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

### 3b. Fixes from the friend's test report (2026-10-04) — built, NOT yet verified on a device

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
