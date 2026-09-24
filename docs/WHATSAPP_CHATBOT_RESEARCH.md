# WhatsApp Chatbots in Healthcare and Care Enterprises — Research

Researched 2026-09-24 by live web search and page fetches, to inform Track D (`docs/AUTOMATION_DESIGN.md`). Purpose: learn what established Indian healthcare/wellness brands actually do on WhatsApp, what fails, and what Meta's rules allow, so Kalaza's bot is designed on evidence rather than guesses.

## 0. Honesty about what this research can and cannot tell us

- **Dr Batra's: no public documentation of a WhatsApp chatbot was found.** Searches for their bot and for vendor case studies (Haptik, Yellow.ai, Gupshup, Interakt, WATI, Infobip) returned nothing specific. What *is* verifiable from their own booking page: booking is a **web form** (name, optional email, mobile, health concern → city → clinic → time slot → pay online), with a **phone line** for help and a mandatory "I accept T&C" checkbox. That page does not mention WhatsApp or any bot. Anything more would be speculation — the way to find out is to message their WhatsApp number as a customer and observe, or ask them.
- **Most of the "results" figures below come from vendor and agency blogs** (companies selling chatbots). Treat percentages as marketing claims, not measurements. Where a claim is a vendor's, it is labelled.
- Big hospital chains (Apollo, Max, Manipal) are a very different scale from a small NGO. They are useful for *patterns*, not as a template to copy.

## 1. What established Indian healthcare brands do on WhatsApp

| Organisation | What is documented | Source quality |
|---|---|---|
| **Manipal Hospitals — "MAI"** | An AI assistant that lives on WhatsApp ("say hi to MAI"). Symptom analysis, explains lab reports/prescriptions (accepts a photo), books doctor appointments, English + Hindi + several regional languages, text/voice/photo input. Says answers are "powered by the medical expertise of Manipal's doctors" (clinically reviewed content) and that data stays confidential. Human handoff is implied ("connects you with the right specialists") but the mechanism is not described. A disclaimer link exists but its text wasn't retrievable. | Their own page (primary) |
| **Apollo Hospitals** | WhatsApp symptom checker built on Infobip's chatbot platform (triage guidance); WhatsApp reminders for lab reports and appointments. | Secondary (vendor/aggregator pages) |
| **Max Healthcare** | WhatsApp FAQ answers; vaccination reminders. A vendor page claims a ~40% reduction in call-centre load. | Vendor claim — unverified |
| **Practo** | WhatsApp for appointment booking and reminders, reducing no-shows. | Secondary |
| **Dr Batra's** | See §0 — nothing bot-related found; web booking form + phone. | Primary (their page), negative finding |
| **Elder-care players (Samarth, Emoha, Antara)** | Samarth advertises WhatsApp integration designed for elderly users and a dedicated Care Manager; both Samarth and Emoha route prospective customers through contact forms for consultations. **No public evidence of an enquiry-follow-up chatbot** for elder care was found. | Their own sites; thin |

**Reading of the evidence:** the well-documented WhatsApp use in Indian healthcare is (1) **appointment booking + reminders**, (2) **FAQs**, (3) **report/symptom help** at the large-hospital tier. *Enquiry-to-admission follow-up for elder care* — Kalaza's actual problem — has almost no public precedent. That is a gap for the project (nothing to copy) and an opportunity (a real differentiator for the portfolio).

## 2. Meta's rules for AI on WhatsApp (verified 2026-09-24)

- Since **15 Jan 2026** (new users from 15 Oct 2025) Meta prohibits **general-purpose AI chatbots** on the WhatsApp Business Platform — open-ended, "ask me anything" assistants such as ChatGPT or Perplexity on WhatsApp.
- **Allowed:** structured, task-oriented bots — customer support, FAQs, lead qualification, bookings, notifications, ticket classification, escalation — as long as the bot is ancillary to a real business service, has an **escalation path to a human**, and runs on the organisation's **verified business number**.
- **Implication for Kalaza: the planned design is compliant by construction** — a knowledge-base-only FAQ/enquiry bot with human handoff (`AUTOMATION_DESIGN.md` §4). Do **not** let it drift into an open-ended chat assistant; that would breach Meta's terms and Kalaza's own safety rules.

## 3. Patterns that recur (good practice for Track D)

1. **Rule-based/scripted bots for patient-facing work.** An Indian clinic-marketing consultancy recommends scripted, non-generative bots for anything patient-facing, because scripted output *cannot* produce medical advice or outcome claims; generative AI only as a staff-side research aid until compliance matures. This matches Kalaza's "KB-only, never invent facts" rule.
2. **Trigger words that hand off to a human**: "human", "agent", "doctor", "manager", "help" put the bot in passive mode and alert staff; also hand off on repeated unanswered questions, complaints, and frustration signals (all caps, many exclamation marks). Nobody should be trapped in a loop.
3. **Handoff carries context**: staff receive a summary (name, interest, transcript), not a cold chat.
4. **Response-time SLA on humans**: e.g. alert a manager if a qualified lead has no human contact within ~5 minutes. For Kalaza (2–3 enquiries a day) this would be a staff push notification — the app already has the notification plumbing.
5. **Lead segmentation/tagging** at intake (service wanted, new vs existing) so staff see priority enquiries first — Kalaza's `service_wanted`, `status` and follow-up dates already do this.
6. **Reminders and re-opening the window**: after 24 hours the free-form window closes; templates with a simple "reply YES" re-opener bring people back. This maps to Kalaza's S2–S5 sequences.
7. **Source attribution** (UTM/CRM source logging) so the organisation learns what channels convert — Kalaza's `how_heard` / `contact_channel` split already does this.

## 4. Documented failure modes (and how Kalaza already stands)

| Failure mode | Kalaza status |
|---|---|
| Bot answers but never books / no link to the real system | Kalaza's bot will write to the same `leads` table staff use — designed in. |
| **Data-protection (DPDP Act 2023) leaks**: health data stored without a lawful basis; conversations reused for model training without consent | Consent fields exist (`consent_given`, `opted_out`) but the **Form has no consent checkbox yet** (open item). Also decide a **retention/deletion policy**; one source suggests ~90-day deletion automation — a design choice, not a legal requirement we verified. Never train on family conversations. |
| **Language-switch abandonment** (Hindi/Marathi mid-flow breaks the bot) | Known risk; the advice is to route to a human on the second language mismatch. Needs the languages decision. |
| **Post-hours lead loss** when the 24h window closes | Templates with re-openers (S2–S5). |
| Clinical advice from an unguarded LLM (regulatory risk in India for medical claims) | Bot never gives medical advice; distress → human. |
| No source attribution | Already captured. |

**A 6-question vendor test** (from the same consultancy) is worth reusing if a BSP/platform is ever chosen: does it write leads directly into our CRM with source/timestamp? where is data stored and how is DPDP deletion proven? does it capture and route 24/7? is human handoff mid-conversation without losing context? who owns escalation SLAs? does it report chat-to-booking conversion (not vanity metrics)?

## 5. WhatsApp Flows — a real alternative to the Google Form (later)

**What it is:** *WhatsApp Flows* are native, multi-screen forms that run **inside** the WhatsApp chat (dropdowns, radio buttons, date pickers, text fields, photo upload). **Available only through the official Cloud API** — i.e. only after Track B. Vendors claim completion rates of ~55–70% versus ~8–15% for equivalent web forms; **vendor claims, unverified**, but the direction (less friction than leaving the app for a link) is plausible. Meta bills Flows under the normal message categories with no separate per-flow fee (as reported by vendors — confirm in Meta's docs).

**Why it matters for Kalaza:** the Google Form is the right choice **now** (works today, free, no Meta dependency). Once the Cloud API is live, a Flow could **replace** the form: same 23 fields, filled inside WhatsApp, delivered to our webhook, inserted into the same `leads` table. Nothing about the database or the app would change — only the intake front door. Record this as a Track D option (`ROADMAP.md`).

## 6. Design implications for Kalaza (what to adopt)

1. Keep the bot **structured and KB-only** (already the design) — it is both safest and Meta-compliant.
2. Add **explicit human-trigger words** (English + Hindi + Marathi equivalents of "human/doctor/manager/help") and a **repeat-failure handoff**.
3. Send staff a **context summary** on handoff and fire a **"lead not contacted within N minutes" alert** through the existing notification system.
4. Treat **DPDP consent** as a first-class feature: consent checkbox on the Form, consent line in the bot's first message, opt-out honoured, a written retention policy, no training on conversations.
5. Decide **languages early**; on a second language mismatch, route to a human.
6. Plan a **WhatsApp Flow** as the eventual replacement for the Google Form.
7. Lean into the gap: elder-care **enquiry follow-up** has no public precedent — the enquiry → follow-up → reason-for-not-converting loop (the app's Reports "Unmet demand") is genuinely differentiated.

## 7. Suggested next research (not done)

- Message Dr Batra's (and Manipal's MAI) as a customer and document the conversation flow first-hand — far more reliable than blogs.
- Read Meta's own developer pages for Flows, message templates and the AI policy (this pass used secondary summaries for some points).
- Find one or two India senior-living operators and test their WhatsApp/website enquiry response time and follow-up behaviour.

## Sources

- [Dr Batra's — Book an appointment (their site)](https://www.drbatras.com/book-an-appointment)
- [Manipal Hospitals — MAI on WhatsApp](https://www.manipalhospitals.com/mai/)
- [WhatsApp Solutions for Healthcare Providers — ChatArchitect](https://www.chatarchitect.com/news/whatsapp-solutions-for-healthcare-providers-automating-patient-communication) (Apollo/Max/Practo examples; secondary)
- [AI Chatbots for Clinics in India: When They Work, When They Fail — Ichelon Consulting](https://ichelonconsulting.com/insights/ai-chatbots-clinics-when-work-fail-india)
- [WhatsApp Chatbot for Healthcare: 7 Use Cases — respond.io](https://respond.io/blog/whatsapp-chatbot-for-healthcare)
- [Not All Chatbots Are Banned: WhatsApp's 2026 AI Policy Explained — respond.io](https://respond.io/blog/whatsapp-general-purpose-chatbots-ban)
- [TechCrunch — WhatsApp changes its terms to bar general-purpose chatbots](https://techcrunch.com/2025/10/18/whatssapp-changes-its-terms-to-bar-general-purpose-chatbots-from-its-platform)
- [WhatsApp Flows: Use Cases, Setup & Examples (2026) — EngageLab](https://www.engagelab.com/blog/whatsapp-flows)
- [WhatsApp Flows for Lead Generation & Qualification (2026) — LeadSync](https://leadsync.me/blog/whatsapp-flows-lead-qualification/)
- [Samarth — elder care app](https://care.samarth.community/app-for-elderly-care/) · [Emoha](https://emoha.com/)
