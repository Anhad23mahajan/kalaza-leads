# Kalaza Leads — Master Plan v2
## Post-Supervisor-Review: Full Analysis, Revised Scope, and Plan of Action

**Owner:** Anhad Mahajan (solo project)
**Client:** Elder-care NGO, Pune
**Supersedes:** `New_NGO_Project_Full_Context.md` (v1) — that document is still valid for background, but where the two conflict, **this one wins**.
**Date:** 2026-08-25
**Status:** Planning document. Nothing in Track B/C/D has been started yet.

**Correction note (2026-08-27):** Part 2.4's cost estimate (~₹450/year) and
the assumption that Coexistence could be self-set-up directly have both
been superseded by deeper research — see `docs/TRACK_B_META_ONBOARDING_PLAN.md`
for the corrected numbers and process, and `docs/SUPERVISOR_SCRIPT_TRACK_BC.md`
for the plain-language version. Short version: Meta's own message costs are
still tiny (~₹550-650/year), but Coexistence specifically requires going
through a registered Tech Provider/BSP, which typically means either a
~₹1,500-2,500/month company fee, or a free-but-uncertain-timeline direct
Meta application. The rest of this document (architecture, data model,
roadmap phasing) is still accurate.

---

# PART 0 — READ THIS FIRST: The One-Paragraph Truth

The supervisor has **redefined the project**. In v1, the app was a follow-up tracker, and WhatsApp automation was a "Phase 2, maybe." In this review he said, in his own words: *"we first need to understand the most basic requirement of the whole application: the auto-reply should get back to them"* and *"basically the entire point of making our application is that whatever we are providing... everything should go as automatic replies."* **The WhatsApp auto-reply is no longer a nice-to-have. In his mind, it IS the product.** Everything built so far (auth, lead list, add-lead form) is roughly **15% of what is now scoped**, and the new scope is not one system but **three**: a staff Android app, a server-side automation engine, and a WhatsApp integration layer. This is now realistically a **4–6 month project**, not a few weeks. The good news is genuinely good: the two things I warned would be blockers in v1 — losing the NGO's WhatsApp number, and cost — have both turned out to be **non-problems in 2026**. The real blockers are different, and they are **not technical**: they are NGO paperwork and NGO-authored content, neither of which Anhad can do alone.

---

# PART 1 — WHAT THE SUPERVISOR SAID, ORGANIZED

His input came as scattered snippets. Sorted into themes:

## 1.1 The auto-reply demand (the dominant theme — he returned to it 6+ times)

- *"how would our application come to know that our enquiry has been done on whatsapp?"* — **he is asking the exact right technical question**, and it deserves a straight answer (Part 3).
- *"how do you get automatic replies on whatsapp through different AI chatbots? can our application do something like this?"*
- *"meta api we have right... that takes money or what?"*
- *"the auto-reply should get back to them when they ask about something"*
- *"when she replies something to our automated message, then we should again reply her back through our automated message"* — **he wants two-way conversation, not just a one-shot canned reply.**
- *"the type of questions they have, then the type of answers/auto-replies they should get"*

## 1.2 Service-specific answer packs

- If they ask about **palliative care**, the reply should be a dedicated palliative section: *"we have a full flat needed for a palliative care patient, this this are the facilities, this this are the things we have, we have this machine, we have this Oxygen Cylinder, we also have a machine 24/7, we also have a backup if any emergency occurs."*
- Answers must cover: **room types** (full flat / single / private / sharing), **budget responses**, **facilities**, **equipment**, **emergency backup**.

## 1.3 Content to be sent out

- *"whatever details u have about the centre, just send that to me — social media links, instagram pages, google... or website like, or a video u want"*
- *"if they enquire about palliative care, then... particular links, posters, videos... whatever, we can share that to them"*
- Price list sharing is tracked as its own column in his Excel.

## 1.4 The follow-up engine

- *"if they say that 'i will tell u in 2-3 days', then there should a message that should go to them automatically after 2-3 days"* — **follow-up timing should honour what the person actually said**, not just a fixed rule.
- Follow-up message content: *"are u free now? what have u decided? what's your plan? can i call u after 2 pm?"*
- *"after 2 days, automatically our application should have notification that we are supposed to reply to her"* — **he wants staff-side notifications too**, not only outbound messages.
- Post-visit: *"if she has visited today, then automatically after 2-3 days, the auto-message should go to her that u visited so how did you like it? did u like it or not? what's your feedback?"*
- Sequenced set: *"the whole after setup — the set of messages that should go to her"* — thank-you first, then a formatted follow-up, then reactive replies.

## 1.5 Lists and segmentation

He explicitly asked for **separate lists**:
- Those **converting** / coming to us
- Those **not** converting
- Those who **just enquire and do nothing else**
- A **backup list**
- A list of **what they didn't like** (*"after 2-3 months we will come to know exactly what they want"*)
- A list of **positive feedback** (*"the staff member was polite, hygiene was good"*) → *"what our advantages are and what are disadvantages"*

## 1.6 Analytics he asked for

- How many enquiries, how many converting
- **Time-to-convert**: *"if they ARE converting then after how many days are they converting"* — and he defined convert precisely: *"convert basically means when they start to live in the facility after the talks."*
- **Why they didn't convert**: budget / over-budget / want AC / want lift / service we don't offer
- *"a list that because of this this reasons, they didn't choose Kalaza Care"*
- Unmet demand: *"if what they want isn't there, then they want that in the facility"*

## 1.7 Volume (important — see the analysis in Part 2.3)

- *"sometimes we get 2-3 enquiries a day"*
- *"6 months we get around 100 calls approx"*
- *"a potential customer will have max to max 20 questions, 1-2 up or down"*

## 1.8 His conversion theory (worth quoting back to him — it's correct)

> *"if someone comes to us, makes an enquiry, but we don't properly reply back, then their converting chances become less... so to convert them we need the follow-up, we need to call them, the replies need to be good, whatever enquiries they have we need to send them proper links, posters... because of social media, they get to know about our NGO, they read our google reviews... so whatever data we send to them, their converting chances increase."*

## 1.9 The 13 concrete UI change requests (after seeing the demo)

1. Phone number — 10-digit validation, no more; plus **country code** selector (India, US, etc.)
2. "How did they reach out" — add **Google Search**, **Call**, **Hospital** — *"because call happens after google search"*
3. Service types — add **transplant patients, cancer services, medical recovery** alongside assisted living and palliative care; needs **4–5+ options**
4. **Location** — "where are you from"
5. **Scroll behaviour** needs improving (form too long / janky)
6. **Edit option after an enquiry is saved** ← already #1 on the existing build list
7. **Excel data file saved and shared**
8. **Relation to patient** — dropdown options, not free text
9. **Type of enquiry / room type** — single / double / sharing etc.
10. **Medical history** field
11. **Visit date** — "if you have called, when are you gonna visit"
12. **What the patient actually has** — Alzheimer's, cancer, etc. (structured)
13. **Follow-up dates**

## 1.10 His existing Excel file — the real-world schema (gold dust)

He maintains this today. Columns, as he listed them:

> enquiry date · enquirer · required for? · location · age of patient · contacted through / referred through · primary contact · his/her number · type of accommodation required (single/double/sharing) · price list shared (Y/N) · date of visit in future · to confirm · queries · comments · current condition · follow-up person name · follow-up 1 date · follow-up 2 date · enquiry date [listed twice — needs clarification] · **Final Remarks: why didn't they convert? reason not to come. what was the issue?**

He explicitly said: *"now i don't want to fill this exact. u can do your own thoughts."* — so this is a **reference, not a mandate**. But it is the single most valuable input in the entire review, because it is what actually works in practice today.

---

# PART 2 — THE HONEST ANALYSIS

## 2.1 The fundamental reframe

There are **two different products** being described, and they have been merged:

| | **Product A: The CRM** | **Product B: The Auto-Reply Bot** |
|---|---|---|
| What it is | Staff tool for tracking enquiries and follow-ups | Automated conversation system on WhatsApp |
| Who uses it | NGO staff | The enquiring families |
| Where it runs | Android phone | A server, 24/7 |
| Built so far | ~15% | 0% |
| Blockers | None — Anhad can build it | Meta onboarding + NGO content |
| Value | Discipline, records, analytics | Speed, consistency, availability |

**They share one thing: the database.** That is the unification, and it is the key architectural insight (Part 4).

The supervisor thinks of these as one thing called "the application." He is not wrong that they should feel like one thing to him — but they are built, deployed, and blocked completely differently, and **the plan has to treat them as separate tracks or it will stall.**

## 2.2 Answering his actual question directly

> *"how would our application come to know that our enquiry has been done on whatsapp?"*

**Straight answer: as things stand, it cannot. Not ever. Not with any amount of coding.**

An Android app has no way to read WhatsApp messages. WhatsApp is end-to-end encrypted and sandboxed; no third-party app can see its contents. Reading notifications is a hack that is fragile, incomplete, and would not work reliably.

The **only legitimate way** for any software to see WhatsApp messages is if the NGO's number is enrolled in **Meta's WhatsApp Business Platform (Cloud API)**. Then Meta itself forwards every incoming message to a server the NGO controls (a "webhook"), and that server can reply.

So the honest framing for the supervisor: **"The app can't see WhatsApp — but we can make WhatsApp talk to our system, officially, through Meta. That's a setup the NGO has to authorise, and it's mostly paperwork, not code."**

## 2.3 The volume reality — and why it changes the justification

His two numbers don't agree:
- 2–3 per day → ~900/year
- ~100 per 6 months → ~200/year

Most likely reading: **2–3 on busy days, ~200–400 enquiries per year overall.** This needs confirming, but either way:

**This is low volume.** Under ~1 enquiry per day on average. A single human can physically handle that.

**This matters, and it must be said plainly:** the case for automation here is **not** "we're drowning in messages." It's:

1. **Speed** — an enquiry at 11pm on a Sunday gets an instant, complete reply instead of one on Tuesday morning. His own theory says this drives conversion.
2. **Consistency** — every single enquirer gets the price list, the right info pack, the videos, the Google review link. Not "whatever the staff member remembered to send."
3. **Never forgetting** — the follow-up happens on day 2 whether or not anyone remembered.

**Brutal corollary:** at ~1 enquiry/day, the **follow-up discipline system (Track A) probably drives more conversions than the chatbot does.** The supervisor is over-weighting the bot. Both are worth building — but if only one gets finished, **it should be the tracker**, and that also happens to be the one with no external blockers. This is a strong argument for the phasing in Part 8.

## 2.4 The two v1 fears that turned out to be WRONG (good news)

### ❌ FEAR 1: "They'll lose their WhatsApp number from the normal app." — **NO LONGER TRUE**

In v1 I warned that enrolling a number in the Cloud API meant deregistering it from the WhatsApp Business app, forcing the NGO to run two numbers. <cite index="17-1">Meta's Coexistence feature now allows a business to run the WhatsApp Business app and the Cloud API on the same number simultaneously.</cite> <cite index="18-1">It preserves contacts, chat history, and app functionality</cite>, and <cite index="22-1">messages sent or received on either side appear on both, so a human agent replying from the app and an automated flow replying via the API work against the same conversation thread.</cite>

Critically for us: <cite index="20-1">Coexistence is now broadly supported including India</cite>, having <cite index="19-1">expanded from a limited beta to globally available</cite>.

**Meaning: the NGO keeps its existing number, its chat history, and its normal WhatsApp workflow — and gets automation on top.** This single fact removes the biggest objection the supervisor could have had.

Known trade-offs to be aware of: <cite index="23-1">disappearing messages, view-once media, and live location sharing are not supported on the API side, and throughput is lower than Cloud API alone (though still far beyond this NGO's needs)</cite>. <cite index="14-1">Companion/linked devices get unlinked on onboarding and only supported ones can be re-linked; WhatsApp for Windows is not supported, and messages from unsupported companion devices won't trigger webhooks. The Business App must not be uninstalled afterwards, as that disconnects the number.</cite>

⚠️ **One thing to verify at setup time:** most coexistence onboarding is documented via Meta Tech Partners / BSPs. Whether a fully direct (non-BSP) coexistence signup is available needs checking in the live Meta flow. **Fallback if not: use a separate new number for the bot** (worse, but workable).

### ❌ FEAR 2: "Automation will cost real money." — **ESSENTIALLY FALSE AT THIS VOLUME**

The pricing model works out almost perfectly for this exact use case:

<cite index="7-1">Service messages — your replies to a user-initiated conversation, sent inside the 24-hour window — are free worldwide with no monthly cap. The old "first 1,000 free service conversations per month" cap was removed on 1 November 2024.</cite> <cite index="4-1">When a user messages you, a 24-hour support window opens and each new message from them resets it; during that window you can respond freely with any free-form message at no charge, and even utility templates delivered inside the window are not charged.</cite>

For messages sent **outside** that window (i.e. the day-2 follow-up), <cite index="6-1">Meta has charged per delivered template message rather than per 24-hour conversation window since 1 July 2025</cite>. Indian rates: <cite index="4-1">₹0.145 per utility/authentication template and ₹1.09 per marketing template (BSP-inclusive pricing)</cite>; Meta's own base marketing rate is <cite index="2-1">₹0.8631, revised upward by roughly 10% in 2026 from ₹0.7846, while utility and authentication rates stayed flat</cite>.

**Cost model for this NGO** (assume 400 enquiries/year, 3 outbound follow-ups each, all utility category):

| Item | Volume/yr | Rate | Cost/yr |
|---|---|---|---|
| Inbound auto-replies (service window) | Unlimited | **Free** | **₹0** |
| Follow-up templates (utility) | 1,200 | ₹0.145 | **₹174** |
| Feedback templates (utility) | 400 | ₹0.145 | **₹58** |
| Buffer / some marketing-category msgs | 200 | ₹1.09 | **₹218** |
| **TOTAL** | | | **≈ ₹450 / year** |

**Under ₹500 a year.** That is not a budget conversation — that is a rounding error for an NGO. Tell the supervisor this plainly; it removes his stated worry (*"that takes money or what?"*).

⚠️ Two caveats: a payment method must still be added to the Meta account before any messaging works (<cite index="10-1">a payment method is required after phone number verification in order to send or receive messages</cite>), and one source notes <cite index="18-1">Meta will update WhatsApp API message pricing again starting 1 July 2026</cite> — rates should be re-checked before final budgeting.

### ✅ BONUS GOOD NEWS: the messaging limit is a non-issue

<cite index="30-1">New unverified accounts start at 250 unique contacts per day, rising to 1,000 / 10,000 / 100,000 after Business Verification, and service messages (replies within the 24-hour window) don't count toward these limits at all.</cite>

At 2–3 enquiries a day, **250/day is ~100× more headroom than needed.** The NGO can pilot the whole system *before* completing Business Verification.

Verification is still worth doing eventually, for two reasons: <cite index="31-1">display name review is only triggered once the account reaches a 2,000/day limit — until then users see the raw phone number instead of the business name</cite>, and <cite index="24-1">unverified accounts carry a higher risk of bans</cite>. <cite index="32-1">Verification requires legal business documents through Meta Business Manager and typically takes 2–5 business days</cite>, and <cite index="24-1">the legal business name in the documents must match the Business Info settings exactly or verification is rejected</cite>.

## 2.5 The REAL blockers (none of them are code)

| # | Blocker | Who must resolve | Realistic time | Can Anhad do it? |
|---|---|---|---|---|
| B-1 | Meta Business Portfolio + admin access to the NGO's Facebook account | Supervisor / NGO | Days–weeks | ❌ No |
| B-2 | NGO legal documents (registration certificate, etc.) | NGO admin | Days–weeks | ❌ No |
| B-3 | Payment method added to Meta account | NGO finance | Days | ❌ No |
| B-4 | Decision on which phone number | Supervisor | Hours (once informed) | ❌ No |
| B-5 | **The ~20 answers written and approved** | Supervisor / care team | **Weeks** | ❌ No |
| B-6 | Price list PDF, posters, videos, social links collected | NGO | Weeks | ❌ No |
| B-7 | Template wording approved by Meta | Anhad submits, Meta reviews | Hours–days | ⚠️ Partly |

**Read that table again.** Six of the seven blockers require someone other than Anhad to act. **The most common way this project fails is not bad code — it is waiting three months for someone to write the palliative-care answers.**

Practical consequence: **Track B and Track C must start tomorrow, in parallel with coding.** They are the long pole.

## 2.6 The guardrail problem — the bot must not improvise

The supervisor wants the bot to answer questions about oxygen cylinders, 24/7 machines, emergency backup, and palliative-care capability.

**If an AI hallucinates a capability the facility does not have, and a family moves a dying parent in on the strength of that message, the harm is real and the reputational damage to the NGO is severe.** This is not a theoretical risk — it is the single most likely way this system does damage.

Hard rules, non-negotiable:
1. The bot answers **only** from an NGO-approved knowledge base. It never generates facts about facilities, medical equipment, staffing, or pricing.
2. Any question it cannot match to an approved answer → **"Let me connect you with our care coordinator"** → human handoff. Never a guess.
3. The bot **never negotiates price** or commits to admission.
4. The bot **never gives medical advice.**
5. Every outbound message is logged and reviewable.

## 2.7 The emotional-escalation problem

Someone will write: *"my mother has stage 4 cancer and I don't know what to do."*

A canned bot reply to that message is worse than no reply. Frame it in the supervisor's own terms — **it will lower conversion, not raise it.** These families are making one of the hardest decisions of their lives.

Design rule: the bot handles **factual and logistical** questions (where are you, what does it cost, what rooms, what facilities, send me the brochure). Anything **emotional, medical-specific, or distressed** routes to a human immediately, and — thanks to coexistence — that human just picks up the same chat in the WhatsApp Business app.

**The bot should also never pretend to be a person.** A short, honest opener ("Kalaza Care assistant here — I can share info instantly, and our team will follow up personally") sets the right expectation.

## 2.8 The language problem (nearly missed — this is important)

**Pune. Families will write in Marathi, Hindi, and English — often mixed in one message.**

If the bot only handles English, a large share of enquiries get a broken experience. Implications:
- The knowledge base needs answers in **at least English + Hindi + Marathi** (or an AI translation layer over approved English answers, which is acceptable because it's translating approved content, not inventing it).
- **Templates must be submitted to Meta per language** — each language is a separate approved template.
- Language detection on inbound, then reply in kind.

This is a real scope item and it needs a decision from the supervisor early.

## 2.9 Consent and data protection

The system will store medical conditions, ages, and budgets for elderly people, and send them automated messages. Under India's **DPDP Act 2023**, this warrants:
- A one-line consent capture at first contact ("we'll save your details and follow up — reply STOP to opt out")
- **Opt-out honoured automatically** (also protects the WhatsApp quality rating — <cite index="8-1">if a user is delivered a message and then blocks you or reports spam, you still paid for it and your quality rating suffers</cite>)
- Staff-only access, no sharing outside the NGO
- Not storing more medical detail than is operationally needed

Cheap to do now, expensive to retrofit.

## 2.10 What already exists off the shelf — full disclosure

Honesty demands this be stated: tools like **AiSensy, Wati, Interakt, DoubleTick** already provide WhatsApp chatbot + broadcast + basic CRM for roughly **₹1,000–5,000/month**, and <cite index="26-1">most Indian businesses connect through a BSP rather than the direct Cloud API, because BSPs handle much of the technical setup and provide a dashboard</cite>.

**Why building it is still the right call here:**
- It's an academic project — the point is to build.
- Off-the-shelf CRMs won't model elder-care specifics: patient-vs-enquirer split, condition tracking (Alzheimer's/cancer/post-transplant), room-type matching, or the not-converted-reason analytics the supervisor specifically asked for.
- Recurring cost forever vs. ~₹450/year of Meta fees.
- Direct Cloud API + Supabase Edge Functions costs nothing to host.

**Where a BSP is still worth keeping in the back pocket:** if Meta onboarding (especially coexistence) stalls badly, a BSP can unblock it. Document this as the escape hatch; don't lead with it.

## 2.11 Scope reality check — brutal

| | v1 scope | v2 scope |
|---|---|---|
| Systems | 1 (Android app) | 3 (app + automation backend + WhatsApp layer) |
| External dependencies | 0 | 7 (Part 2.5) |
| Content the NGO must author | ~0 | 20 Q&As, 5 info packs, price list, posters, videos, template copy — **in up to 3 languages** |
| Realistic solo timeline | ~6–8 weeks | **~4–6 months** |
| Done so far | | **~15%** |

**Anhad is one first-year student with a full coursework load.** That is not a reason to shrink the ambition — but it *is* a reason to phase ruthlessly and to be honest with the supervisor about timelines up front, rather than over-promising and disappointing later.

---

# PART 3 — HOW IT ACTUALLY WORKS (the explanation for the supervisor)

Plain-language version, ready to use:

> **Q: How will the app know someone messaged us on WhatsApp?**
>
> The app itself never will — no app can read WhatsApp. But WhatsApp can be made to *tell* our system. Meta runs an official service for exactly this. Once our number is enrolled, every message someone sends us is instantly forwarded to our system, and our system can reply straight back through WhatsApp.
>
> **Do we lose our number from normal WhatsApp?** No — this used to be true, but Meta changed it. We keep using WhatsApp Business on the phone exactly as we do now, with all our chats. The automation just runs alongside it, on the same number. Staff can jump into any conversation at any time.
>
> **Does it cost money?** Replying to someone who messaged us in the last 24 hours is completely free, unlimited. Only messages we send days later — the follow-ups — cost anything, and that's about 15 paise each. At our volume that's a few hundred rupees a year.
>
> **What do we need to do?** This is the part that isn't coding. We need the NGO's Meta/Facebook business account, our registration documents, and a card on file. And most importantly — we need to write down the answers to the questions people actually ask. The system can send them instantly, but someone from the care team has to tell it what's true.

---

# PART 4 — SYSTEM ARCHITECTURE

```
        FAMILY ON WHATSAPP
               │
               ▼
    ┌──────────────────────┐
    │  Meta WhatsApp       │
    │  Cloud API           │  ← NGO's existing number, coexistence mode
    └──────────────────────┘
         │            ▲
   inbound webhook    │ outbound send
         ▼            │
    ┌─────────────────────────────────┐
    │  SUPABASE EDGE FUNCTIONS        │
    │  • webhook receiver             │
    │  • intent routing               │
    │  • knowledge-base answering     │
    │  • Claude API (guarded)         │
    │  • human-handoff detector       │
    │  • scheduled follow-up sender   │
    └─────────────────────────────────┘
                  │
                  ▼
    ┌─────────────────────────────────┐
    │  SUPABASE POSTGRES              │
    │  leads · contact_activities ·   │
    │  wa_messages · faq_entries ·    │
    │  content_assets · templates ·   │
    │  feedback · not_converted       │
    └─────────────────────────────────┘
                  ▲
                  │
    ┌─────────────────────────────────┐        ┌──────────────────┐
    │  ANDROID APP (Kalaza Leads)     │        │ WhatsApp Business│
    │  staff-facing                   │        │ App on staff     │
    │  • enquiry capture              │        │ phone            │
    │  • follow-up-due home           │        │ (same number,    │
    │  • lead detail + edit           │        │  coexistence —   │
    │  • activity log                 │        │  humans take over│
    │  • segmented lists              │        │  any chat)       │
    │  • Excel export                 │        └──────────────────┘
    │  • reports                      │
    │  • push notifications           │
    └─────────────────────────────────┘
```

**The single most important line in this diagram:** the Android app and the WhatsApp bot **never talk to each other**. They both talk to the same database. That's what makes them feel like "one application" to the supervisor while being independently buildable.

---

# PART 5 — REVISED DATA MODEL

## 5.1 `leads` — needs migration from the current 18 columns

The current table was built to v1 spec. It needs restructuring. **Key change: `how_heard` and `contact_channel` must be split into two fields** — the supervisor identified this himself (*"add google search and call... because call happens after google search"*). Someone finds you on Google, then phones. Those are two different facts and conflating them destroys the attribution analytics he wants.

```
IDENTITY & INTAKE
  id, created_at, updated_at
  enquiry_date              date      -- explicit; may differ from created_at
  contact_channel           enum      -- phone_call | whatsapp | walk_in | website | email | instagram_dm
  how_heard                 enum      -- google_search | google_maps | instagram | facebook |
                                      --   referral_friend_family | referral_hospital | referral_doctor |
                                      --   passing_by | newspaper | other
  how_heard_detail          text      -- e.g. referrer name, which hospital

ENQUIRER (the person contacting — usually the son/daughter)
  enquirer_name             text
  enquirer_country_code     text      -- default +91
  enquirer_phone            text      -- 10 digits validated for IN
  enquirer_relation         enum      -- son | daughter | spouse | sibling | grandchild |
                                      --   nephew_niece | friend | self | hospital_staff | other
  enquirer_location         text      -- "where are you from"

PATIENT (the elderly person care is for)
  patient_name              text
  patient_age               int
  patient_gender            enum
  patient_conditions        text[]    -- multi: alzheimers | dementia | parkinsons | cancer |
                                      --   post_stroke | post_operative | post_transplant |
                                      --   bedridden | diabetes | cardiac | mobility_impaired | other
  patient_condition_notes   text
  current_condition         text      -- mobility / dependency level, free text
  medical_history           text

REQUIREMENT
  service_wanted            text[]    -- assisted_living | palliative_care | post_transplant_care |
                                      --   cancer_care | medical_recovery | dementia_care |
                                      --   respite_care | day_care
  accommodation_type        enum      -- single_room | double_sharing | triple_sharing |
                                      --   full_flat | dormitory | not_sure
  budget_min, budget_max    int
  budget_notes              text
  amenities_requested       text[]    -- ac | lift | attached_bathroom | ground_floor |
                                      --   female_attendant | private_nurse | veg_food | other
  special_requirements      text
  queries                   text      -- what they actually asked
  comments                  text      -- free staff notes

PIPELINE
  status                    enum      -- see 5.2
  assigned_staff_id         uuid      -- his "follow-up person from name"
  next_follow_up_date       date
  follow_up_count           int
  price_list_shared         bool
  price_list_shared_at      timestamptz
  info_packs_sent           text[]
  planned_visit_date        date
  actual_visit_date         date
  converted_at              date      -- the day they moved in
  days_to_convert           int       -- computed: converted_at - enquiry_date

OUTCOME
  not_converted_reason      enum      -- see 5.3
  not_converted_detail      text
  feedback_positive_themes  text[]
  feedback_negative_themes  text[]
  final_remarks             text      -- his Excel's last column

COMPLIANCE
  consent_given             bool
  opted_out                 bool
  preferred_language        enum      -- en | hi | mr
```

## 5.2 `status` — the pipeline, mapping directly to his lists

| Status | His words |
|---|---|
| `NEW` | enquiry received, nobody's touched it |
| `CONTACTED` | first reply/call done |
| `INFO_SENT` | packs/price list shared |
| `VISIT_SCHEDULED` | *"date of visit in future"* |
| `VISITED` | they came |
| `CONSIDERING` | *"i will tell u in 2-3 days"* |
| `CONVERTED` | *"when they start to live in the facility"* |
| `NOT_CONVERTED` | *"one who's not"* — requires a reason |
| `DORMANT` | *"many that just enquires but doesn't do anything else"* |
| `BACKUP` | *"their backup list"* — interested, but not now |

## 5.3 `not_converted_reason` — this powers his most-wanted report

`budget_too_high` · `chose_another_facility` · `location_too_far` · `amenity_missing` (→ which) · `service_not_offered` (→ which) · `family_decided_home_care` · `patient_passed_away` · `decision_postponed` · `unreachable_no_response` · `unhappy_after_visit` (→ theme) · `other`

Every one of those is a row in the report he asked for: *"because of this this reasons, they didn't choose Kalaza Care."*

## 5.4 New tables

```
contact_activities      -- the "proof" log (already planned in v1)
  lead_id, occurred_at, type (call|whatsapp|visit|email|sms),
  direction (inbound|outbound), outcome (positive|negative|no_answer|
  callback_requested|not_reachable), callback_on, notes, staff_id,
  is_automated (bool)   -- distinguishes bot messages from human ones

wa_messages             -- every inbound/outbound WhatsApp message
  lead_id, wa_message_id, direction, body, media_url, template_name,
  status (queued|sent|delivered|read|failed), cost_paise, sent_at,
  handled_by (bot|human), language

faq_entries             -- the ~20 questions, NGO-approved
  question_variants[], answer_en, answer_hi, answer_mr,
  service_tags[], attached_asset_ids[], is_approved, approved_by,
  last_reviewed_at

content_assets          -- links, posters, videos, price list
  title, type (pdf|image|video|link), url, service_tags[],
  language, is_active

message_templates       -- Meta-approved outbound templates
  meta_template_name, category (utility|marketing), language,
  body_text, variables[], approval_status, purpose
  (purpose: followup_1 | followup_2 | post_visit_feedback |
   visit_reminder | reengagement)

feedback_responses
  lead_id, received_at, rating, staff_behaviour, hygiene, food,
  facilities, value_for_money, free_text, sentiment, themes[]

staff                   -- currently missing; needed for assignment + roles
  name, phone, role (admin|coordinator|viewer), is_active
```

## 5.5 Mapping his Excel → the schema (show him this table; it proves he was heard)

| His Excel column | Field |
|---|---|
| enquiry date | `enquiry_date` |
| enquirer | `enquirer_name` |
| required for? | `patient_name` + `enquirer_relation` |
| location | `enquirer_location` |
| age of patient | `patient_age` |
| contacted through / referred through | **split** → `contact_channel` + `how_heard` + `how_heard_detail` |
| primary contact / number | `enquirer_phone` + `enquirer_country_code` |
| type of accommodation | `accommodation_type` |
| price list shared | `price_list_shared` + timestamp |
| date of visit in future | `planned_visit_date` |
| to confirm | ⚠️ **ask him what this means** |
| queries | `queries` |
| comments | `comments` |
| current condition | `current_condition` + `patient_conditions[]` |
| follow-up person | `assigned_staff_id` |
| follow-up 1 / 2 date | `contact_activities` rows + `follow_up_count` |
| final remarks / why not converted | `not_converted_reason` + `not_converted_detail` + `final_remarks` |

---

# PART 6 — THE AUTO-REPLY BOT DESIGN

## 6.1 Why ~20 questions is great news

His estimate — *"max to max 20 questions, 1-2 up or down"* — means **this does not need a general-purpose AI.** It needs a well-built FAQ with good matching. That is dramatically more reliable, cheaper, and safer than an open-ended chatbot, and it makes the guardrails in 2.6 easy to enforce.

AI (Claude) is used for three narrow jobs only:
1. **Matching** a messy real-world message to the right approved answer
2. **Translating** approved answers between English / Hindi / Marathi
3. **Extracting** structured lead fields from free-text conversation

It is **never** used to author facts about the facility.

## 6.2 The likely 20 questions (draft — the NGO must confirm and answer)

Location & logistics: where is the centre · how do I visit · what are visiting hours · is there parking/transport
Cost: what are your charges · what's included · is there a deposit · are there extra medical charges
Rooms: what room types · single vs sharing · is AC available · is there a lift · attached bathroom
Care: do you do palliative care · post-operative recovery · dementia/Alzheimer's care · post-transplant care · cancer care · is there 24/7 nursing · is a doctor available · what if there's an emergency
Equipment: oxygen availability · medical equipment on site · hospital tie-ups
Food: meal arrangements · special/medical diets
Trust: can I see photos · reviews · how long have you operated · staff qualifications
Admissions: what documents · how long does admission take · trial/short stay

**Action: this list goes to the supervisor as a worksheet. He fills in the answers. Nothing bot-related can be built until he does.**

## 6.3 Conversation flow

```
Inbound message arrives
        │
        ▼
Is sender a known lead? ──no──► create lead (status=NEW, channel=whatsapp)
        │ yes
        ▼
Log to wa_messages · detect language · check opt-out
        │
        ▼
Is this the FIRST message ever?
   yes ──► Greeting + honest bot disclosure + consent line
           + service menu (quick-reply buttons):
             Assisted Living | Palliative Care | Cancer Care
             Post-op Recovery | Transplant Care | Something else
        │
        ▼
Distress / emotional / medical-specific language detected?
   yes ──► "Our care coordinator will call you shortly"
           + flag lead URGENT + push-notify staff → STOP (human takes over)
        │ no
        ▼
Match against faq_entries
   confident match ──► send approved answer + attached assets
   no match        ──► "Let me get our coordinator to answer that properly"
                        + flag for staff → STOP
        │
        ▼
Ask ONE qualifying question (progressive, never a form dump):
   who is it for → their age/condition → location → when needed → budget range
        │
        ▼
Update lead fields · set next_follow_up_date (+2 days)
· notify staff in Android app
```

**Design rule: one question per message.** Nobody answers a 10-field form over WhatsApp.

## 6.4 Message sequences

**S1 — Instant enquiry response** (free, inside window)
`Thank you for contacting Kalaza Care` → service menu → info pack for chosen service (text + price list PDF + photos + video + Google reviews link + Instagram) → progressive qualifying questions.

**S2 — Follow-up #1** (+2 days, utility template, ₹0.145)
> *Hello {name}, you recently enquired with Kalaza Care about {service} for {patient_relation}. Have you had a chance to decide? We'd be happy to arrange a visit at a time that suits you.*

Their reply reopens the free window → conversation continues free.

**S3 — Follow-up #2** (+5 days) and **#3** (+10 days), softer each time. After #3 → `DORMANT`.

**S4 — Honour their stated timeline.** If they say *"I'll tell you in 3 days"* → the bot (or staff) sets the date, and the follow-up fires then. This is his explicit ask and it's a differentiator vs. dumb fixed sequences.

**S5 — Visit reminder** (day before) and **post-visit feedback** (+2 days):
> *Thank you for visiting Kalaza Care. How was your experience? Was our staff helpful? Anything we could improve?*
Buttons: 😊 Good / 😐 Okay / 😞 Not good → each opens the free window for detail → written into `feedback_responses` and the positive/negative theme lists.

**S6 — Re-engagement** for `BACKUP` leads at +60/+90 days (marketing category, ₹1.09 — use sparingly).

## 6.5 Staff-side notifications (his explicit ask)

> *"after 2 days, automatically our application should have notification that we are supposed to reply to her"*

Android push notifications for: new WhatsApp enquiry · follow-ups due today · **urgent/distress flag** · bot couldn't answer, human needed · new feedback received.

---

# PART 7 — ANALYTICS HE ASKED FOR

| # | Report | His words |
|---|---|---|
| 1 | Enquiry volume over time, by channel | *"how many enquiries we have had"* |
| 2 | Source attribution — volume **and conversion rate** per source | *"how did they come to know about it?"* |
| 3 | Overall conversion rate; by service; by source; by staff | *"how many are converting"* |
| 4 | **Time-to-convert** (median days enquiry → move-in) | *"after how many days are they converting"* |
| 5 | Funnel drop-off by stage | implied |
| 6 | **Not-converted reasons, ranked** | *"why didn't they choose us? what's the issue?"* |
| 7 | **Unmet demand** — requested services/amenities not offered | *"if what they want isn't there, then they want that in the facility"* |
| 8 | Positive feedback themes = **advantages list** | *"staff member was polite, hygiene was good"* |
| 9 | Negative feedback themes = **disadvantages list** | *"what our advantages are and what are disadvantages"* |
| 10 | Follow-up discipline: % done on time, avg first-response time | implied by his conversion theory |
| 11 | Budget distribution vs. actual pricing | *"do they not like the budget? is it over-budget?"* |

**Report #7 is the sleeper hit.** After 3 months it tells the NGO, with evidence, *"14 families walked away because we don't have a lift."* That's a business case, not a feature request. Lead with this when demoing to him.

---

# PART 8 — ROADMAP

Four parallel tracks. **A is code with no blockers. B and C are NGO-dependent and must start immediately. D needs B and C done.**

## TRACK A — The Android app (Anhad, unblocked, start now)

| ID | Item | Est. |
|---|---|---|
| A1 | Schema migration + **form overhaul** (all 13 change requests: country code + 10-digit validation, split how_heard/contact_channel, expanded services, conditions multi-select, room type, location, medical history, visit date, relation dropdown, scroll fix) | 1.5 wk |
| A2 | **Lead detail + edit screen** (request #6) | 1 wk |
| A3 | **Contact activity log** — the "proof" feature | 1 wk |
| A4 | ~~**Follow-up-due home screen** + push notifications~~ — **Done** (list 2026-08-25, notifications 2026-08-27 as local `WorkManager` checks, not true server push) | 1.5 wk |
| A5 | Segmented list views (his 4+ lists) + status transitions + not-converted reason capture | 1 wk |
| A6 | ~~**Excel export + share**~~ (request #7) — **Done 2026-08-25**, as CSV (opens in Excel/Sheets) via the Android share sheet | 0.5 wk |
| A7 | `wa.me` one-tap messaging (interim, works before any API) | 0.5 wk |
| A8 | ~~Staff table + assignment + basic roles~~ — **Done 2026-08-25** | 0.5 wk |
| A9 | ~~Reports/analytics screen~~ — **Done 2026-08-25** | 1.5 wk |

**≈ 9 weeks of solo evening/weekend work.** At the end of Track A the NGO has a fully working CRM — **with zero dependency on Meta.**

## TRACK B — Meta onboarding (NGO must drive, start tomorrow)

B1 Decide number strategy (recommend: **coexistence on existing number**) → B2 Meta Business Portfolio + admin access → B3 gather NGO legal docs → B4 add payment method → B5 embedded signup / coexistence onboarding → B6 submit Business Verification → B7 draft + submit templates (per language)

**Elapsed: 2–6 weeks, mostly waiting. Anhad's involvement: advisory only.**

## TRACK C — Content (NGO authors, Anhad structures)

C1 the 20 Q&As answered and approved → C2 price list as shareable PDF → C3 five service info packs → C4 posters/videos/social/Google-review links collected → C5 template copy approved → C6 Hindi/Marathi versions

**Elapsed: 3–8 weeks depending entirely on NGO responsiveness. This is the single biggest schedule risk in the project.**

## TRACK D — Automation (gated on B + C)

D1 webhook Edge Function → D2 outbound send function → D3 conversation router + FAQ matching → D4 guardrails + human handoff → D5 scheduler for S2/S3/S5 → D6 two-way sync into `leads` + `wa_messages` → D7 AI layer (matching, translation, extraction) → D8 opt-out handling

**≈ 6–8 weeks once unblocked.**

## Phased delivery (what the supervisor sees)

| Phase | Contents | When | What he sees |
|---|---|---|---|
| **0 — Quick wins** | A1, A2 + **WhatsApp Business App built-in greeting/away/quick-replies configured** | ~2 wks | Fixed form, editable enquiries, and **auto-greeting live on WhatsApp already** |
| **1 — The CRM** | A3–A8 | ~6 wks | Nothing gets forgotten. Full follow-up discipline + Excel export |
| **2 — Bot listens & replies** | D1–D4, D6 | ~10 wks (if B+C done) | Real auto-replies with info packs |
| **3 — Scheduled outbound** | D5, D7, D8 | ~14 wks | Automatic follow-ups & feedback requests |
| **4 — Insight** | A9 + full reports | ~16 wks | The "why we lose families" report |

**Total: ~4 months minimum, realistically 5–6 with coursework.** Say this out loud to him. Do not let him believe it's weeks.

## 8.1 The Phase-0 freebie — do this THIS WEEK

Before any API exists, the **WhatsApp Business app already has** free built-in automation:
- **Greeting message** — auto-sends to anyone messaging for the first time
- **Away message** — auto-replies outside business hours
- **Quick replies** — staff type `/palliative` and a full prepared answer drops in
- **Labels** — organise chats by status

Setting these up costs nothing and takes an afternoon. **The supervisor gets to see "automatic replies working on WhatsApp" within days**, which buys enormous goodwill and credibility while the real system is built. Strongly recommended.

---

# PART 9 — RISK REGISTER

| Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|
| **NGO never delivers the 20 answers** | **High** | **Fatal to Track D** | Hand over a filled-in worksheet template; ask for 5 answers/week; escalate early |
| Meta onboarding stalls (docs, account access) | Medium | Blocks D | Start now; BSP as escape hatch; 250/day cap means pilot can run unverified |
| Coexistence unavailable via direct signup | Medium | Annoying | Fall back to BSP-assisted onboarding, or a separate bot number |
| Scope creep at next review | **High** | Schedule slip | Freeze scope per phase; log new asks to a Phase-5 list |
| Bot states a false facility capability | Low | **Severe** | KB-only answers, no generative facts, human handoff on no-match |
| Bot replies coldly to a distressed family | Medium | **Severe** | Distress detection → immediate human handoff |
| Language gap (Marathi/Hindi) | **High** if ignored | High | Decide early; multi-language KB + per-language templates |
| Coursework collision (exams) | Certain | Schedule | Build the buffer into the timeline now, not later |
| Staff don't adopt the app | Medium | High | The AI paste-parser; keep the form fast; Phase-0 wins |
| WhatsApp quality rating drops → restrictions | Low | High | Honour opt-outs, utility over marketing, never bulk-blast |
| Single-developer bus factor | Certain | High | The `PROGRESS.md` discipline already in place — keep it |

---

# PART 10 — QUESTIONS FOR THE SUPERVISOR

**Blocking (need answers before Track B/D can move):**
1. Which WhatsApp number is used for enquiries? Is it on the **WhatsApp Business app** or normal WhatsApp?
2. Who has **admin access to the NGO's Facebook / Meta Business account**? Does one exist?
3. Is the NGO registered as a trust / society / Section 8 company, and are the **registration documents** available?
4. Can the NGO add a **payment method (card)** to a Meta account? Who authorises that?
5. **Who will write and approve the 20 answers**, and by when?
6. **What languages** do enquiries actually come in — English only, or Hindi/Marathi too?

**Important:**
7. Real volume — is it 2–3/day, or ~100 per 6 months? (They imply very different numbers.)
8. Confirm the **full service list** — assisted living, palliative, cancer, transplant, medical recovery… anything else?
9. Confirm **room types** and what "full flat" includes.
10. What amenities exist **today** — AC, lift, oxygen, 24/7 nursing, doctor on call, hospital tie-up?
11. Is there an existing **price list PDF**? Posters? Videos? Where do they live?
12. How many **staff** will use the app, and do they need different permission levels?
13. **"To confirm"** — what does that column in the Excel mean?
14. Can we get his **existing Excel file** to import as historical data? (Instant value, and real test data.)

**Governance:**
15. What should families be told about **how their data is stored**?
16. Who approves outbound message wording before it goes to Meta?

---

# PART 11 — WHAT TO TELL HIM (the honest conversation)

Suggested framing, in his language:

> **"Yes — everything you asked for is possible, and the two things you were worried about turned out fine.**
>
> **On the auto-reply:** the app on the phone can never read WhatsApp — no app can. But Meta has an official service that forwards our messages to our system so it can reply automatically. That's the piece we need to set up.
>
> **You will NOT lose your number.** Meta changed this recently. We keep using WhatsApp Business exactly as we do now, same number, same chats — the automation just runs alongside it. Staff can take over any conversation any time.
>
> **The cost is about ₹450 a year.** Replying to someone within 24 hours of them messaging us is free and unlimited. Only the day-2 follow-ups cost anything, roughly 15 paise each.
>
> **Here's the honest part.** This is now three systems, not one — the staff app, the automation engine, and the WhatsApp connection. It's about a 4–6 month build alongside my coursework, not a few weeks. And **six of the seven things blocking it aren't code — they're on the NGO side.** The biggest one: I can't write the answers to what facilities we have. Someone from the care team has to. If I get those 20 answers in two weeks, we move fast. If they take three months, the bot waits three months.
>
> **So here's my plan.** I build the tracker first — the part that makes sure nobody gets forgotten — because that has no dependencies and, honestly, at 2–3 enquiries a day I think it wins us more admissions than the bot will. Meanwhile you start the Meta paperwork and the care team writes the answers. When both are ready, I connect them.
>
> **And this week, for free:** I can switch on WhatsApp's own built-in greeting and away messages, so anyone who messages us gets an instant reply starting immediately, while everything else is being built."

---

# PART 12 — IMMEDIATE NEXT ACTIONS

**Anhad, this week:**
1. Take the Part 10 questions to the supervisor — **especially the six blocking ones**
2. Hand over the 20-question worksheet (Part 6.2) for the care team to fill
3. Ask for the existing **Excel file** — import it as seed data
4. Set up **WhatsApp Business app greeting/away/quick-replies** with him (Part 8.1) — visible win in days
5. Start **A1**: schema migration + form overhaul (the 13 change requests)

**Supervisor, this week:**
6. Confirm the number strategy and locate Meta/Facebook account access
7. Start gathering NGO registration documents
8. Assign someone to write the 20 answers, with a deadline

**Update `PROGRESS.md` and `docs/PROJECT_SPEC.md`** to point at this document as the current source of truth for scope.

---

*End of Master Plan v2. Pricing, coexistence, and messaging-limit facts in Part 2.4 were verified against 2026 sources at time of writing; re-check Meta's official rate card before final budgeting, particularly in light of the pricing update reported for 1 July 2026.*
