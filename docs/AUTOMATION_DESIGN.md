# WhatsApp Automation — Design (Tracks C & D)

What the auto-reply system should do and the rules it must follow. **Nothing here
is built yet.** For what to do next and in what order, see `docs/ROADMAP.md`. For the
Meta/WhatsApp paperwork and costs, see `docs/TRACK_B_PAPERWORK_PLAYBOOK.md`.
(This replaces the old `MASTER_PLAN_V2.md`; the parts that were already built or
superseded were removed — they're still in git history.)

---

## 1. What the supervisor wants

The auto-reply **is** the product, in his eyes. In his words:
*"the auto-reply should get back to them when they ask about something"* and *"when she replies something to our automated message, then we should again reply her back."*

- **Two-way replies**, not a one-shot canned message.
- **Service-specific answer packs** (palliative care, room types, budget, facilities, equipment, emergency backup), with links/posters/videos attached.
- **Follow-ups that honour what the person said** — "I'll tell you in 2–3 days" → the message goes out in 2–3 days.
- **Post-visit feedback ask** a couple of days after a visit.
- **Staff notifications** when something needs a human (already partly built as local notifications in the app).

## 2. How it works, and why volume matters

- An Android app can never read WhatsApp. The only official way is Meta's WhatsApp Business Platform (Cloud API): Meta forwards each incoming message to our server (a webhook), and our server replies.
- **Volume is low** (roughly 200–400 enquiries a year, 2–3 on busy days). So the case for automation is **speed, consistency, and never forgetting a follow-up** — not "we're drowning". The tracker (Track A, done) probably wins more admissions than the bot; the bot is still what the supervisor asked for.

## 3. Architecture

```
FAMILY on WhatsApp ⇄ Meta WhatsApp Cloud API
                        │ inbound webhook        ▲ outbound send
                        ▼                        │
              SUPABASE EDGE FUNCTIONS
              webhook · router · FAQ match · handoff · scheduler
                        │
                        ▼
              SUPABASE POSTGRES  ◄──────────── Google Form → Sheet → Apps Script (built)
              leads · contact_activities · staff (built)
              wa_messages · faq_entries · content_assets ·
              message_templates · feedback_responses (to build)
                        ▲
                        │
              ANDROID APP (built) — staff read/edit the same tables
```

**Key rule: the Android app and the bot never talk to each other. Both talk to the same database.** That's what makes them feel like one application while being built independently.

## 4. Non-negotiable rules

**Never invent facts.**
1. The bot answers **only** from an NGO-approved knowledge base. It never generates facts about facilities, equipment, staffing or prices.
2. No confident match → *"Let me connect you with our care coordinator"* → human takes over. Never a guess.
3. Never negotiates price, never commits to admission, never gives medical advice.
4. Every outbound message is logged and reviewable.

**Distress goes to a human, immediately.** A message like *"my mother has stage 4 cancer and I don't know what to do"* must not get a canned reply. The bot handles factual/logistical questions (where, what cost, what rooms, send the brochure); anything emotional or medical-specific → "our care coordinator will call you shortly", flag the lead URGENT, notify staff, stop.

**Be honest it's a bot.** Opener like: *"Kalaza Care assistant here — I can share info instantly, and our team will follow up personally."*

**Languages.** Families write in English, Hindi and Marathi, often mixed. The knowledge base needs approved answers in each (translating *approved* English answers with AI is acceptable; inventing content is not). Meta templates are approved **per language**. Needs a decision from the supervisor early.

**Consent and opt-out.** One-line consent at first contact ("we'll save your details and follow up — reply STOP to opt out"). Honour opt-outs automatically (also protects the WhatsApp quality rating). Staff-only access; don't store more medical detail than needed (India's DPDP Act 2023).

## 5. Knowledge base

Only ~20 distinct questions are expected, so this needs **a well-built FAQ with good matching, not a general chatbot** — more reliable, cheaper and safer.

AI (Claude) is used for three narrow jobs only: **matching** a messy message to the right approved answer, **translating** approved answers, **extracting** structured fields from free text. Never to author facts.

**The ~20 questions (draft — the NGO must confirm and answer; nothing can be built usefully until they do):**
- *Location & logistics:* where is the centre · how do I visit · visiting hours · parking/transport
- *Cost:* charges · what's included · deposit · extra medical charges
- *Rooms:* room types · single vs sharing · AC · lift · attached bathroom
- *Care:* palliative · post-operative recovery · dementia/Alzheimer's · post-transplant · cancer · 24/7 nursing · doctor availability · emergencies
- *Equipment:* oxygen · medical equipment on site · hospital tie-ups
- *Food:* meals · special/medical diets
- *Trust:* photos · reviews · how long operating · staff qualifications
- *Admissions:* documents · how long it takes · trial/short stay

Also needed from the NGO: price list PDF, one info pack per service (5), posters/videos/social/Google-review links.

## 6. Conversation flow

```
Inbound message
  → known lead? no → create lead (status NEW, channel whatsapp)
  → log to wa_messages · detect language · check opt-out
  → first message ever? yes → greeting + bot disclosure + consent line + service menu
  → distress / emotional / medical-specific? yes → hand to human, flag URGENT, STOP
  → match against approved FAQ
       confident → send approved answer + attached assets
       no match  → "let me get our coordinator" + flag for staff, STOP
  → update lead, set next_follow_up_date (+2 days), notify staff
```

Design rule: **one question per message.** Nobody fills a 10-field form over WhatsApp.

> **Open design question:** the Google Form now collects the enquirer's details. So instead of the bot asking qualifying questions one by one (who is it for → age → location → budget…), it may simply send the form link. Decide when building the FAQ slice.

## 7. Message sequences

| # | When | What | Cost |
|---|---|---|---|
| S1 | Instant | Thank-you → service menu → info pack for the chosen service (text, price list PDF, photos, video, Google reviews link) | Free up to 1,000 service messages/month per number (see note below) |
| S2 | +2 days | Follow-up #1 (template): "have you had a chance to decide? happy to arrange a visit" | Template, paise |
| S3 | +5, +10 days | Softer follow-ups; after #3 → `DORMANT` | Template |
| S4 | Custom | If they say "I'll tell you in 3 days", the follow-up fires then | Template |
| S5 | Day before visit; +2 days after | Visit reminder; feedback ask with buttons (Good / Okay / Not good) → stored in `feedback_responses` and the positive/negative theme lists | Template |
| S6 | +60/+90 days | Re-engagement for `BACKUP` leads (marketing category — use sparingly) | Marketing template |

A reply reopens the 24-hour window. **Note (verified 2026-09-22): from 1 October 2026, replies inside that window are no longer unconditionally free** — Meta now gives 1,000 free service messages/month per phone number, then charges per message. At this NGO's volume that allowance should cover normal use; don't assume it's unlimited. Current rates and limits: see the Track B playbook.

**Evidence and policy (researched 2026-09-24, see `docs/WHATSAPP_CHATBOT_RESEARCH.md`):** Meta bans *general-purpose* AI chatbots on the WhatsApp Business Platform (from 15 Jan 2026) but allows structured FAQ/booking/lead-qualification bots with a human escalation path — this design is compliant as long as it stays KB-only and task-bound. Adopt: explicit human-trigger words (incl. Hindi/Marathi), repeat-failure handoff, context summary to staff, a "lead not contacted in N minutes" alert, and DPDP consent + a retention policy. Future option: replace the Google Form with a native **WhatsApp Flow** once the Cloud API is live.

## 8. Tables still to build

Already built: `leads`, `contact_activities`, `staff` (see `docs/sql/` and `Lead.kt`).

- **`wa_messages`** — every inbound/outbound message: lead_id, wa_message_id, direction, body, media_url, template_name, status, sent_at, handled_by (bot|human), language
- **`faq_entries`** — question variants, answers in en/hi/mr, service tags, attached asset ids, is_approved, approved_by, last_reviewed_at
- **`content_assets`** — title, type (pdf|image|video|link), url, service tags, language, is_active
- **`message_templates`** — meta_template_name, category (utility|marketing), language, body, variables, approval_status, purpose (followup_1 | followup_2 | post_visit_feedback | visit_reminder | reengagement)
- **`feedback_responses`** — lead_id, received_at, rating, staff behaviour / hygiene / food / facilities / value, free text, themes

## 9. Staff-side notifications (to add once the bot exists)

New WhatsApp enquiry · follow-up due today (already built, local) · **urgent/distress flag** · bot couldn't answer, human needed · new feedback received. (Server push would need Firebase Cloud Messaging.)

## 10. Risks that still matter

| Risk | Impact | Mitigation |
|---|---|---|
| NGO never delivers the ~20 answers | Fatal to Track D | Hand over a worksheet; ask for ~5/week; escalate early |
| Bot states a false facility capability | Severe | KB-only answers, human handoff on no-match |
| Bot replies coldly to a distressed family | Severe | Distress detection → immediate handoff |
| Meta onboarding stalls | Blocks D | See the Track B playbook (paths and fallbacks) |
| Language gap (Marathi/Hindi) | High | Decide languages early; per-language KB and templates |
| WhatsApp quality rating drops | High | Honour opt-outs, prefer utility over marketing, never bulk-blast |
| Scope creep at the next review | Schedule | Freeze scope per phase; log new asks separately |
| Coursework/exams | Schedule | Build buffer into any date given to the supervisor |
