# Kalaza Leads — Project Specification

The full concept, requirements, architecture, and data model for the app. Working reference — expected to evolve as decisions land.

---

## 1. Where this sits

[Kalaza Care](https://github.com/Anhad23mahajan/kalaza-care) handles people who are already **residents** at the NGO's facility — medication tracking, staff shifts, role-based access, escalation alerts, audit logging. In production use now.

**Kalaza Leads** handles everyone **before** they become a resident — the families calling and messaging to enquire about care for their elderly parent or relative. Two apps, one NGO, shared Supabase backend, and a clean handoff seam at the moment a family converts from lead to resident.

---

## 2. What the supervisor asked for

The idea came from the NGO supervisor himself. Stripped of phrasing, one specific thing:

> **An enquiry-to-conversion follow-up system — a lightweight CRM built for an elder-care NGO.**

He stated the goal twice, plainly: *"the primary task is to take the follow-up on the enquiry."* Everything orbits that single goal.

The concrete asks he raised:

1. **Multi-channel intake** — enquiries arrive by WhatsApp, phone call, or walk-in. All of them need to land in **one place**.
2. **First-response SLA** — every enquiry gets a reply within 2–3 days. The system must flag anything approaching that deadline.
3. **Rich enquiry data** — see §4 for the full field list.
4. **A contact log that acts as proof** — for any lead, one view showing which date we called, which date we messaged, what she asked. Simultaneously an audit trail and a reminder engine.
5. **Multi-round follow-up** — if call 1 isn't a "yes," schedule call 2, and keep going. No giving up after one attempt.
6. **Post-stay thank-you + feedback** — after a family visits or stays 2–3 days: *"Thank you for visiting Kalaza Care, how was the staff, what can we improve?"* Automated, or a one-tap option from the staff side.
7. **Reports** — how many enquiries, from where (source breakdown), converting at what rate.

The chatbot / auto-reply idea he gestured at is the shiny thing he pictures; the actual value he needs is **discipline** — capture every enquiry, never miss a follow-up, know the outcome of each contact, and prove that contact happened.

---

## 3. Data captured per enquiry

The supervisor listed a lot; this is the consolidated field list.

- **Source channel** — WhatsApp / phone call / walk-in
- **How they heard about the NGO** — social media / Google / referral (marketing attribution)
- **Enquirer** — name, phone, relation to the elderly person (daughter / son / relative)
- **Patient** — the elderly person the care is actually for: name, age (two different people from the enquirer; must be tracked separately)
- **Location** — where they are / belong to
- **Service wanted** — assisted living / palliative care / other
- **Room preference** — single / double / sharing
- **Budget** — the supervisor explicitly flagged this as **primary**
- **Medical history / notes** on the elderly person
- **Specific requirements** — e.g. lift, AC, dietary
- **Status** — New → Contacted → Visited → Converted / Not interested
- **Assigned staff**
- **Next follow-up date**

---

## 4. The WhatsApp reality (must be understood before building)

The fully-automated WhatsApp chatbot the supervisor pictures is the single hard, regulated, costs-money part of this project. Communicated honestly:

- The green WhatsApp app the NGO currently uses **cannot be legitimately automated.** Libraries that attempt it (e.g. `whatsapp-web.js`, `Baileys`) violate WhatsApp's Terms of Service and regularly get phone numbers banned. The NGO's real enquiry number cannot be used as the guinea pig.
- The legitimate route is the **WhatsApp Business Cloud API** (Meta's official API). To message someone who has not texted you in the last 24 hours — which is exactly the follow-up case, days later — requires a **pre-approved template message**. Template messages cost money per message, need a **dedicated phone number** (cannot be a number already active on the normal WhatsApp app), and require **Meta business verification**.

**Conclusion:** true automation is a Phase 2 thing with real setup and a small ongoing cost. It is not the MVP.

### The MVP bridge — semi-automated `wa.me`

The workaround that delivers ~90% of the value for zero cost, today, with zero ban risk and fully within WhatsApp's rules:

> The app decides **who** to contact, **when**, and **drafts what to say** — then shows a "Send on WhatsApp" button that opens WhatsApp with the message pre-filled to that person's number (using a `wa.me` deep link). The staffer just taps send.

Zero cost, zero API, zero ban risk. It is "semi-automated" — a human hits send — but the hard work (remembering who/when, drafting the message, tracking the outcome) is done by the system. Countless small businesses run exactly this way. Graduates to full API automation later once the pipeline is proven and the NGO is willing to fund the dedicated number + per-message template costs.

On mobile, tapping "Send on WhatsApp" opens the actual WhatsApp app on the same phone, message pre-filled. This is one of several points favouring an Android client over web.

---

## 5. Phasing

### Phase 1 — MVP (buildable now, ₹0 running cost)

- Every enquiry is captured with all §3 fields.
- Each lead has a follow-up schedule + a contact log.
- Home screen is a **"Follow-ups due today / overdue" list** — the killer feature — delivered via mobile push notification.
- Outcome tracking per contact: positive / negative / no answer / call back later.
- One-tap `wa.me` messaging with pre-drafted text.
- Basic reports: enquiries this week, source breakdown, conversion rate.

### Phase 2 — Automation

- WhatsApp Business Cloud API for genuinely automatic thank-you + feedback + follow-up nudges.
- Scheduled jobs running server-side.
- Feedback form responses flowing back into the system.

**Framing for the supervisor:** present Phase 1 as the whole thing working end-to-end, then Phase 2 as *"and now it sends by itself."*

---

## 6. Architecture

> **A separate Android app, same stack as Kalaza (Kotlin / Jetpack Compose + Supabase), forking the Kalaza scaffolding — with a small serverless backend for the AI and scheduled reminders.**

**Client:** Native Android, Kotlin + Jetpack Compose. Forks the Kalaza skeleton (auth, Supabase wiring, design system, navigation) as a starting point, then builds enquiry features on top.

**Backend / data:** Supabase — Postgres (data) + Auth. Ideally the **same Supabase project/org** as Kalaza, so both apps live in one backend and a converted lead can hand off into Kalaza Care's resident data.

**Serverless layer:** Supabase Edge Functions (+ scheduled/cron jobs) for:
- Calling the **Claude API** (message drafting, paste-parsing, summarization) — API key stays server-side, never in the APK.
- **Scheduled follow-up reminders.**
- **Phase 2** WhatsApp Business API automation.

**Distribution:** Android APK, installs alongside Kalaza Care on the same phone.

**Reports for the supervisor:** a simple Supabase-served web report page or CSV/PDF export, so he gets his numbers on a laptop without building a whole second frontend.

### Two known weaknesses, mitigated head-on

1. **Phone data entry is real friction.** A CRM is typing — medical history, budget, requirements, source, room preference, notes. Typing long fields on a phone is unpleasant. Concrete risk: staff under-fill the form because it's tedious, and the supervisor's #1 requirement ("all the data comes to us") quietly dies. **Mitigation:** lean hard on the AI paste-parser — one box where staff dump the raw WhatsApp message or a few scribbled call notes, and Claude fills the structured fields. Build this early, not as a nice-to-have.
2. **Supervisor's reports look cramped on a phone.** He'll want "how many enquiries this week, conversion rate, source breakdown" on a laptop. **Mitigation:** the small web report page / export above.

---

## 7. Data model

Two core tables do most of the work, plus small supporting tables.

### `leads`
One row per enquiry.
- `id`, `created_at`, `assigned_staff_id`
- `source_channel` — enum: WHATSAPP / CALL / WALK_IN
- `how_heard` — enum: SOCIAL_MEDIA / GOOGLE / REFERRAL / OTHER
- `enquirer_name`, `enquirer_phone`, `enquirer_relation`
- `patient_name`, `patient_age`
- `location`
- `service_wanted` — enum: ASSISTED_LIVING / PALLIATIVE / OTHER
- `room_preference` — enum: SINGLE / DOUBLE / SHARING
- `budget` (numeric)
- `medical_history` (text)
- `specific_requirements` (text)
- `status` — enum: NEW / CONTACTED / VISITED / CONVERTED / NOT_INTERESTED
- `next_follow_up_date`

### `contact_activities`
The "proof" log. Many rows per lead.
- `id`, `lead_id`, `staff_id`, `timestamp`
- `type` — enum: CALL / WHATSAPP / VISIT
- `outcome` — enum: POSITIVE / NEGATIVE / NO_ANSWER / CALL_BACK_LATER
- `notes` (text)
- `call_back_on` (nullable date)

### `message_templates`
Reusable templates for thank-you / follow-up / feedback messages, referenced when drafting outgoing WhatsApp messages via `wa.me`.

### `feedback_responses`
Post-stay feedback captured from families.

All tables get Postgres Row-Level Security; policies follow the same `is_active_staff()` / `is_super_admin()` pattern as Kalaza Care.

---

## 8. Where AI earns its place

Not bolted on — three real, non-gimmicky uses:

1. **Drafting** — generate the personalized thank-you / follow-up / feedback message from the lead's context.
2. **Parsing** — turn a pasted raw WhatsApp enquiry (or messy call notes) into the structured form fields. This is also the key mitigation for phone data-entry pain (§6).
3. **Summarizing** — condense a lead's entire contact history into a single line.

All three call the Claude API from the serverless layer, never from the app directly.

---

## 9. Security

- **The Claude API key never goes in the APK.** Android apps decompile trivially; anyone could extract an embedded key. All AI calls go through the serverless layer (Supabase Edge Functions); the app only talks to that.
- **Secrets go in a repo-level `.env` (gitignored) or in the serverless layer's own secret storage.** Never in any shared / visible environment-variable dialog on a hosted development environment.
- **Scheduled jobs and Phase 2 WhatsApp automation run server-side**, not on a phone.

### Sensitive-data note

The app stores medical history and budget information about elderly people and their families. That is sensitive data. Design principles:
- Collect only what's needed.
- Get consent before messaging people.
- Access is staff-only, gated by RLS.
- Communicate this stance clearly to the supervisor — for an NGO, family trust matters a great deal.

---

## 10. Relationship to Kalaza Care

**Separate:**
- Its own Android Studio project.
- Its own GitHub repo (this one).
- Its own APK that installs alongside Kalaza Care (both can sit on the same phone at once).
- Its own screens and features.
- The two apps never depend on each other's code.

**Shared, deliberately:**
- Same tech stack — Kotlin / Compose + Supabase.
- Ideally the same Supabase project/org, so both apps live in one backend. This is the clean integration seam: this app tracks a family through **enquiry → follow-up → conversion**, and the moment they convert to an actual resident, that record hands off into Kalaza Care's resident data. One database, two apps talking to different tables.

**"Forking the scaffolding" means:** copy the skeleton (auth setup, Supabase wiring, design system, navigation patterns) from Kalaza into this project as a starting point, then build the enquiry features on top. Reusing groundwork, not sharing a codebase.

**Why standalone and not a module inside Kalaza:**
- The supervisor framed it as a new project.
- Keeps a clean standalone deliverable (better for grading and portfolio: *"I built two apps for the NGO"*).
- Avoids mixing resident-care ops (Kalaza) with pre-resident lead tracking (this) — different jobs, different users.

---

## 11. Open items

1. **Confirm the concept with the supervisor** — take this spec back to him and verify understanding is right before building. Seeing his scattered thoughts turned into a clear system should land well.
2. **Same Supabase org or a new one?** — reuse Kalaza's project (enables the lead-to-resident handoff) or spin up a fresh one.
3. **Phase 2 go/no-go on WhatsApp Business API** — depends on the NGO being willing to fund a dedicated number + per-message template costs. Not needed for MVP.
