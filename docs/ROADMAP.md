# Kalaza Leads — Roadmap (what's done, what's next)

Written 2026-09-18, after Track A shipped and the intake moved to a Google Form.
This is the **"what do we do next"** doc. For how the auto-reply system should work (rules, sequences, tables) see
`docs/AUTOMATION_DESIGN.md`; for the engineering log see `docs/PROGRESS.md`.

---

## 1. Where we are

| Area | State |
|---|---|
| **Track A — Android CRM** | Done, tested on a real device, on GitHub. Leads (7 tabs), detail/edit, contact log, follow-up notifications, staff roster + assignment, CSV export, reports, `wa.me` one-tap messages. |
| **Security** | Done. Signup gated by the active-staff roster (`is_active_staff_name`). RLS = "any logged-in user is staff". |
| **Intake** | Done. Google Form (23 fields) → Apps Script → Supabase `leads`. In-app "New Enquiry" screen removed. Verified end-to-end 2026-09-17. |
| **Track B — Meta/WhatsApp onboarding** | **Not started.** Prep docs are ready (`TRACK_B_*`). Needs a decision + paperwork from the supervisor/NGO. |
| **Track C — NGO content** (FAQ answers, price list, packs) | **Not started.** NGO-authored. |
| **Track D — WhatsApp automation** | **Not started.** This is what the supervisor considers "the product". |

**The honest read:** the CRM is finished. Everything left that matters to the
supervisor (auto-replies) depends on things only he/the NGO can supply — a
number decision, Meta paperwork, and written answers. So the plan below is:
finish the small loose ends, unblock B/C, and use the waiting time productively
instead of idling.

---

## 2. Phase 1 — Loose ends (this week, all in Anhad's hands)

1. **Delete the test leads** ("hfgnb", the "TEST Anhad" rows) from Supabase Table Editor. They're junk in the real table.
2. **Solve "how does the form link reach the enquirer" with zero API cost.** This was parked; there's a free answer inside the WhatsApp Business *app*:
   - Put the form link in the **greeting message** (auto-sent to first-time messagers) and in a **quick reply** (e.g. `/form`) for staff to fire on calls/walk-ins.
   - Print a **QR code** of the responder link for the front desk.
   - It costs nothing, and it gives the supervisor a visible auto-reply within days.
3. **Move form ownership off the personal Gmail.** The Form, its response Sheet (full of family medical/contact data), and the Apps Script all live in `anhadagammahajan@gmail.com`. Before real use: create/borrow an NGO-owned Google account and transfer ownership (or at least add it as co-owner). Also decide who can see the response Sheet.
4. **Supabase free-tier check.** Free projects pause after a period of inactivity and have no automated backups (verify current terms in the dashboard). Decide: is this OK for real data? If not — periodic CSV export to a safe place, or an upgrade — and record the decision.
5. **Optional form additions** (deferred, low priority): consent checkbox → `consent_given`, preferred language → `preferred_language`. Steps are in `GOOGLE_FORM_INTAKE_SPEC.md` §6.

## 3. Phase 2 — Unblock Track B and C (needs the supervisor)

**Gate: the Track B conversation.** Use `TRACK_B_MEETING_CHEATSHEET.md` (glance sheet) and `TRACK_B_SUPERVISOR_MEETING_SCRIPT.md` (rehearsal). Its outcome picks the path in `TRACK_B_PAPERWORK_PLAYBOOK.md`:

- **Path 1 — Coexistence** (keep the current number; needs a BSP / Tech Provider, has a monthly fee).
- **Path 2 — New dedicated number** (cost-safe default; no BSP requirement).
- **Path 3 — Stall** (supervisor won't/can't act yet) → do Phase 4 work only.

> **Record the outcome here and in `PROGRESS.md` right after the meeting.** Everything in Phase 3+ branches on it.

In parallel, hand over the **Track C worksheet**: the ~20 Q&A answers (list in `AUTOMATION_DESIGN.md` §5), price list PDF, five service info packs, posters/links. This is the single biggest schedule risk in the project. Ask for ~5 answers a week, not all at once, and put a date on it.

Track B execution (whoever owns the NGO's Meta account does it; Anhad advises): Business Portfolio → admin access → legal docs → payment method → number onboarding → business verification → template submission.

## 3b. Also unblocked right now

- Ask (again, or accept the "no") about the supervisor's Excel history — dropped as a blocker earlier, don't chase.
- Hindi/Marathi: get a decision on which languages enquiries actually come in. It shapes the knowledge base and every template.

---

## 4. Phase 3 — Track D, the automation (start when B/C give a foothold)

Order of work (re-sequenced into thin slices so something works early; design in `AUTOMATION_DESIGN.md`):

| Slice | What | Needs |
|---|---|---|
| **D-0** | Design the knowledge-base format + reply-router logic on paper; write it against **placeholder answers** | Nothing external |
| **D-1** | Webhook receiver (Supabase Edge Function) + outbound send function | A WhatsApp number (Meta's sandbox test number may be enough to start — confirm current limits in the developer console) |
| **D-2** | FAQ matching: incoming question → KB answer, **KB-only, no generated facts**; no-match → human handoff | Real answers from Track C to be useful, placeholders to build |
| **D-3** | Guardrails: distress detection → immediate human handoff, opt-out handling | — |
| **D-4** | Two-way sync: log every message into a `wa_messages` table + link to `leads` | — |
| **D-5** | Scheduled outbound: honour "I'll tell you in 2–3 days", post-visit feedback ask | Approved templates (Track B) |
| **D-6** | AI layer (translation / extraction), only after D-1…D-5 are solid | — |

Design rules carried over (they're the reason the risk register looks the way it does): never let the bot invent a facility capability; always hand off on no-match; staff can take over any thread at any time; honour opt-outs.

**Realistic timing:** Original estimate was ~6–8 weeks once unblocked. Coursework/exams will stretch it — build the buffer into any date you tell the supervisor.

---

## 5. Phase 4 — Parallel, always available (doesn't depend on anyone)

Useful while B/C are waiting, and directly serves the portfolio goal:

- **Demo package:** a 2–3 minute screen recording of the full loop (form → lead appears → follow-up → export/report), plus screenshots for the README.
- **Hardening of what exists:**
  - Bridge failure visibility — today a failed Supabase insert is only logged in Apps Script (the row still lives in the Sheet). Add an email-on-failure or a "backfill from sheet" script.
  - Anon-insert spam guard (shared-secret field checked in the bridge, or move the insert behind an Edge Function) — noted as an accepted risk in `006_google_form_anon_insert.sql`, revisit if abuse appears.
  - Roles: `staff.role` exists but isn't enforced anywhere. Only matters if viewers must be read-only.
  - A few unit tests for the pure logic (`ReportsAnalytics.kt`, segment filters, the bridge's `normalize`/`mapMulti`).
- **Resume/portfolio write-up** of the project — the story is strong: real NGO client, real device testing, security fix found and closed, architecture pivot to Google Forms.

---

## 6. Decisions still open

| # | Question | Who | Blocks |
|---|---|---|---|
| 1 | Coexistence vs new number vs stall | Supervisor | All of Track B/D |
| 2 | Who owns the Google Form / Sheet / Script long-term | Anhad + supervisor | Real-data use of the form |
| 3 | Who writes/approves the ~20 answers, by when | Supervisor | Track D usefulness |
| 4 | Which languages | Supervisor | KB + templates |
| 5 | Supabase free tier vs paid/backup | Anhad | Real-data use |
| 6 | Does anything more get added to the Google Form (consent, language)? | Anhad | Nothing |

## 7. Known and accepted (not planned work)

- Follow-up notifications fire same-day but not at an exact time (WorkManager/Doze). Exact alarms deemed not worth the complexity.
- Every signed-in account is trusted staff (RLS is `auth.uid() is not null`); mitigated by roster-gated signup.
- Anyone holding the anon key can insert junk leads (not read/edit/delete).
- `Realtime` is installed but unused.

## 8. Suggested order, in one line

Delete test data → free form-link delivery via WhatsApp greeting/quick-reply/QR → move form ownership → **have the Track B meeting and record the outcome** → hand over the Track C worksheet → build Track D slices D-0…D-4 as far as the outcome allows → keep the demo/portfolio work going alongside.
