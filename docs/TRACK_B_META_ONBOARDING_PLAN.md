# Track B — Meta / WhatsApp Onboarding: Full Research & Step-by-Step Plan

For Anhad. Deeply researched (August 2026, sources linked throughout) so you
can walk into the supervisor conversation and answer almost anything he
throws at you. This **supersedes** the Track B summary in
`docs/MASTER_PLAN_V2.md` §8/Part 2.4 wherever the two disagree — this
document has more current pricing and one important correction (see §1).

**Format**: every step says what we're doing, why, exactly what's needed,
and where/how to actually get it — not just "do this."

---

## 0. What Track B actually is

Track B is the process of getting Kalaza Care's WhatsApp number officially
connected to Meta's WhatsApp Business Platform (the "Cloud API"), so that a
piece of software we build (Track D) can receive incoming messages and send
replies automatically. It is **entirely account setup and paperwork** — no
code gets written in Track B itself. It has to finish (along with Track C)
before Track D can start.

---

## 1. The one decision that shapes everything: keep the number, or use a new one

This is the first thing to lock down, because it changes every step after
it — including cost.

### Option 1 — Coexistence: keep the existing number

The existing WhatsApp Business app keeps working on the phone exactly as
today (same number, same chat history, same contacts), while our
automation runs on the same number in parallel. Staff can still open
WhatsApp Business on the phone and jump into any conversation, at any time
— including ones the bot started.

**Important correction to what was assumed earlier**: this is **not**
something we can just self-serve directly through a plain Meta developer
account. Meta's own documentation is explicit that Coexistence onboarding
requires the connecting developer to be a registered **Tech Provider or
Solution Partner** — a status a plain, direct Cloud API setup does not
have. [[source]](https://developers.facebook.com/documentation/business-messaging/whatsapp/embedded-signup/onboarding-business-app-users/)
This is corroborated independently by at least two BSPs' own
documentation. [[source]](https://clientify.com/en/blog/communication/whatsapp-coexistence)

There are two ways to satisfy that requirement — see §3 Step 5 for the
full comparison. Short version: either (a) pay a commercial BSP a monthly
platform fee to do the onboarding for us, which is fast and turnkey, or
(b) register as our **own** Tech Provider directly with Meta, which is
free but requires going through Meta's app-review process with no
guaranteed timeline.

**Requirements**: WhatsApp Business app version 2.24.17+ already installed
and active on that number; the app must be opened at least once every 13
days to stay eligible. [[source]](https://www.ycloud.com/blog/whatsapp-business-app-coexistence-meta-update)
India is on the supported country list.

**What's lost under Coexistence** (worth telling the supervisor plainly):
group chat sync, disappearing messages, view-once media, live location,
and broadcast lists (become read-only) stop working on that number.
One-to-one chats, calls, the business profile, and the catalog keep
working normally. All linked companion devices (WhatsApp Web/Desktop) get
unlinked and need re-linking. [[source]](https://developers.facebook.com/documentation/business-messaging/whatsapp/embedded-signup/onboarding-business-app-users/)

### Option 2 — A brand-new number, dedicated to the bot

A second WhatsApp number, used only by the automation, fully self-served
through Meta's direct Cloud API setup — **no Tech Provider/BSP status
needed at all** for this path, since Coexistence is specifically what
triggers that requirement. [[source]](https://developers.facebook.com/documentation/business-messaging/whatsapp/solution-providers/overview)
This is simpler and has zero extra recurring fee beyond Meta's own
per-message cost.

**Trade-off**: the NGO now runs two numbers — the old one for normal human
WhatsApp use, a new one for the bot. Every printed material, Google
listing, and social bio needs the new number, or families keep messaging
the old one and never reach the bot. This was the exact problem Coexistence
was invented to avoid.

### Recommendation

Start down the **Coexistence** path (Option 1) with the **self-Tech-Provider
attempt first** (§3 Step 5b) since it's free to try and costs only time.
Keep a BSP quote in your back pocket as the fallback if that stalls — this
matches the Master Plan's original risk mitigation.

---

## 2. What it actually costs — corrected numbers

The Master Plan's original ~₹450/year estimate only counted Meta's own
per-message charges. It didn't account for a possible BSP platform fee,
because at the time it wasn't confirmed whether one would be needed. Now
that Coexistence's Tech-Provider requirement is confirmed, here's the
fuller picture:

**Meta's own message charges** (India, effective **1 July 2026**, per
Meta's official rate card):
- Replying to someone within 24 hours of them messaging us: **free,
  unlimited**, no cap.
- Utility/authentication template (e.g. a day-2 follow-up): **₹0.115**
  per delivered message. [[source]](https://www.engagelab.com/blog/whatsapp-business-api-pricing)
- Marketing-category template: **₹0.8631** per delivered message. [[source]](https://www.engagelab.com/blog/whatsapp-business-api-pricing)
- **18% GST applies on top of all of the above.** [[source]](https://www.engagelab.com/blog/whatsapp-business-api-pricing)

At the volume the Master Plan estimated (≈400 enquiries/year, ~3
follow-ups each, mostly utility category), Meta's own charges alone come
to roughly **₹550–650/year including GST** — still trivial.

**If a commercial BSP is used for onboarding** (Option 1a): realistic
entry-level plans for small businesses in India run **₹1,500–2,500/month**
(≈₹18,000–30,000/year) — e.g. AiSensy from ~₹1,500/month, Interakt around
₹2,142/month, Wati from ~₹2,499/month. [[source]](https://aisensy.com/aisensy-vs-interakt-vs-wati)
That's a real, ongoing line item — not "basically free" the way the
original framing suggested. Confirm current pricing directly with whichever
BSP you shortlist; pricing pages change.

**If self-registering as a Tech Provider** (Option 1b) or using a new
number (Option 2): **no platform fee at all** — only Meta's own per-message
cost above, so back to the original ~₹550–650/year estimate.

**Bottom line to tell the supervisor**: *"The actual usage cost is trivial
either way — well under ₹1,000 a year. The real cost decision is whether
we pay a company (~₹18,000–30,000/year) to make the account setup easy and
fast, or spend more of my own time trying to do that setup ourselves for
free. I'd like to try the free route first, with the paid option as a
fallback if it stalls."*

---

## 3. Step-by-step plan

### Step 1 — Decide the number strategy

**What**: Confirm with the supervisor whether we're doing Coexistence
(keep the current number) or a new dedicated number.
**Why**: Every later step depends on this — verification, the phone
number flow, and whether a Tech Provider/BSP is even needed.
**What's needed**: A decision from the supervisor, informed by §1 above.
**Where/how**: This is a conversation, not a form — see
`docs/SUPERVISOR_SCRIPT_TRACK_BC.md`. Recommend Coexistence unless he has
a strong reason not to.

### Step 2 — Set up a Meta Business Portfolio

**What**: A "Business Portfolio" (formerly called Business Manager) is
Meta's container for a business's ad accounts, pages, and — relevantly —
its WhatsApp Business Account (WABA). Everything else in this plan happens
inside one of these.
**Why**: Nothing in the WhatsApp Business Platform can be set up without
one; it's the account Meta actually verifies (§ Step 3), not a personal
Facebook profile.
**What's needed**: Someone with a personal Facebook account willing to be
the initial admin (this can be the supervisor, or whoever currently has
authority to represent the NGO to Meta). The NGO's legal business name and
address, matching what'll be on the verification documents exactly.
**Where/how**: [business.facebook.com](https://business.facebook.com) →
create a new Business Portfolio → enter the NGO's legal name and details.
Takes a few minutes; free. **This is the very first blocking question to
resolve with the supervisor**: does this already exist for Kalaza Care, and
if so, who has admin access to it? If it doesn't exist, decide who creates
it and holds admin rights (should probably be a real NGO-controlled
account, not Anhad's personal one, for long-term ownership reasons).

### Step 3 — Business verification

**What**: Meta confirms Kalaza Care is a real, legitimate organization by
checking submitted documents against the Business Portfolio's stated name
and address.
**Why**: Required before Meta will let the account send real (non-test)
messages at any meaningful scale, and before a display name (vs. a raw
phone number) shows to recipients. [[source]](https://saleshiker.com/blog/meta-business-verification-document-requirements-phone-number/)
**What's needed** (India, non-company entities like trusts/societies):
- The trust deed or society registration certificate (the entity's
  founding/constitutional document).
- A GST registration certificate **or** Udyam (MSME) registration
  certificate.
- An address-proof document in the entity's name: a utility bill,
  property tax receipt, lease deed, or the GST certificate itself if it
  shows the address. [[source]](https://support.wati.io/en/articles/11463208-meta-business-verification-required-documents-by-country)

  **Every document must show the exact same legal name and address** as
  what's entered in the Business Portfolio — mismatches are the single
  most common rejection reason. [[source]](https://saleshiker.com/blog/meta-business-verification-document-requirements-phone-number/)
**Where/how**: Inside the Business Portfolio → Business Settings → Security
Center → Start Verification. Upload the documents there. Typical turnaround
is **2–5 business days** for complete, matching documents. [[source]](https://docs.360dialog.com/docs/resources/meta-business-verification)
**This is squarely NGO/supervisor work** — Anhad cannot supply these
documents; someone at Kalaza Care has to locate and upload them.

### Step 4 — Add a payment method

**What**: A credit/debit card added to the Business Portfolio's payment
settings.
**Why**: Meta requires one on file before any WhatsApp messaging (even
test messages beyond the free tier) works, regardless of how small the
actual usage cost ends up being.
**What's needed**: A card the NGO is authorized to use for this — doesn't
need to be a business card specifically, but should be something the NGO
controls long-term, not a personal card that later becomes inaccessible.
**Where/how**: Business Settings → Payment Methods, inside the same
Business Portfolio. A finance-authorized person at the NGO needs to do
this or approve it.

### Step 5 — Get Coexistence-eligible: choose BSP-assisted or self-Tech-Provider

*(Skip this step entirely if Option 2 — a new number — was chosen in Step
1; go straight to a plain [Cloud API Get Started](https://developers.facebook.com/documentation/business-messaging/whatsapp/get-started) setup instead, no
Tech Provider needed.)*

#### Step 5a — BSP-assisted (the fast, paid path)

**What**: Sign up with a commercial WhatsApp Business Solution Provider
(BSP) — e.g. AiSensy, Interakt, Wati, or 360dialog — who is already a
Meta-approved Tech Provider, and let their "Embedded Signup" flow connect
our existing number with Coexistence enabled.
**Why**: BSPs exist specifically to make this fast — usually same-day to a
few days, with support if something goes wrong.
**What's needed**: The Business Portfolio (Step 2) and verification (Step
3) done first; then just pick a BSP and follow their signup flow, which
walks through connecting the existing WhatsApp Business app number,
confirming a code sent to that number, and choosing whether to share chat
history.
**Where/how**: **Before committing, confirm the specific BSP explicitly
supports Coexistence** (not all plans/providers do) — check their current
docs directly, e.g. [360dialog's coexistence page](https://docs.360dialog.com/docs/resources/phone-numbers/coexistence).
Get a live quote from 2–3 BSPs since pricing shifts often (§2 has rough
figures, not firm quotes).

#### Step 5b — Self-registered Tech Provider (the free, slower path) — try this first

**What**: Anhad registers his own Meta Developer app as a "Tech Provider"
directly with Meta, instead of paying a BSP to be the intermediary. A Tech
Provider connects a WhatsApp Business Account via the same Embedded Signup
mechanism a BSP uses, but keeps billing directly between the NGO and Meta
— no markup, no monthly platform fee. [[source]](https://whautomate.com/whatsapp-tech-provider-vs-bsp)
**Why**: This is the only way to get the "practically free" cost profile
the Master Plan originally hoped for, while still keeping Coexistence.
**What's needed**:
1. A Meta Developer account and a new Meta app, set up for WhatsApp
   messaging (same starting point as the direct Cloud API guide). [[source]](https://developers.facebook.com/documentation/business-messaging/whatsapp/get-started)
2. Business verification for *this* developer entity (can likely be the
   same Business Portfolio/verification as Step 3, needs confirming live).
3. Submitting for **App Review**, requesting Advanced Access to the
   `whatsapp_business_messaging` and `whatsapp_business_management`
   permissions — this is what actually grants Tech Provider capability. [[source]](https://www.infobip.com/docs/whatsapp/tech-provider-program/setup-and-integration)
4. Once approved, creating a "Partner Solution" inside the developer
   dashboard, which is what makes the Embedded Signup flow available to
   present to Kalaza Care's own account. [[source]](https://www.infobip.com/docs/whatsapp/tech-provider-program/setup-and-integration)
**Where/how**: All inside [developers.facebook.com](https://developers.facebook.com) — see the
["Become a Tech Provider"](https://developers.facebook.com/documentation/business-messaging/whatsapp/solution-providers/get-started-for-tech-providers) guide.
**Honest caveat (flagged below in §6 too)**: App Review for these specific
permissions is a real review process with no guaranteed approval or
timeline — Meta's documentation and guides describe the mechanics clearly,
but don't promise how long approval takes for a first-time, single-client
applicant. **Time-box this**: give it a genuine attempt, but if it stalls
past 2–3 weeks with no movement, fall back to Step 5a (a BSP) rather than
letting it block the whole project.

### Step 6 — Draft and submit message templates

**What**: Any message sent *outside* the 24-hour reply window (the day-2
follow-up, a visit reminder, etc.) has to use a pre-approved "template" —
free-form text only works while replying inside that 24-hour window.
**Why**: This is how Meta prevents spam; every outbound template gets
reviewed against its declared category before it can be used.
**What's needed**: Draft text for each planned automated message (Master
Plan Part 6.4's S2/S3/S5 sequences are a good starting list — follow-up
#1, follow-up #2, post-visit feedback ask), each correctly categorized:
- **Utility** — for messages that follow up on something the user did
  (an enquiry, a visit) — this is what our follow-up/feedback messages
  should be, and it's the cheapest, easiest-approved category.
- **Marketing** — broader outreach/re-engagement messages (e.g. the
  Master Plan's dormant-lead re-engagement) — costs more, use sparingly.
- **Authentication** — OTPs/login codes only; not relevant here. [[source]](https://support.wati.io/en/articles/11463465-whatsapp-template-categories-explained-utility-authentication-and-marketing)

  Getting the category wrong is the single most common rejection reason —
  Meta checks the actual content against the declared category and can
  reclassify or reject it. [[source]](https://gurusup.com/blog/whatsapp-api-message-templates)
**Where/how**: Inside WhatsApp Manager (part of the Business Portfolio) or
the chosen BSP's template editor. Most utility templates get automated
approval within minutes; if flagged for human review, up to 24 hours. [[source]](https://gurusup.com/blog/whatsapp-api-message-templates)
**If multiple languages are needed** (per Master Plan §2.8, Marathi/Hindi
alongside English), each language variant is a **separate template
submission** requiring its own approval.

### Step 7 — Hand off to Track D

Once the account is verified, a number is connected (Coexistent or new),
and initial templates are approved, Track B is functionally done. The
remaining pieces — a webhook endpoint receiving messages, a permanent
access token, the actual reply logic — are Track D (coding), not Track B.
No further supervisor/NGO action needed to *start* that work, though
Track C's answers still gate what the bot can actually say.

---

## 4. Who does what — clean checklist

**Supervisor / NGO must do:**
- Decide: Coexistence or a new number (Step 1)
- Confirm/create the Meta Business Portfolio and say who holds admin
  access (Step 2)
- Locate and provide: trust deed/society registration certificate, GST or
  Udyam certificate, an address-proof document (Step 3)
- Add a payment method the NGO controls long-term (Step 4)
- If going the BSP route: approve which BSP and its monthly cost (Step 5a)
- Approve the wording of message templates before submission (Step 6)

**Anhad can do without waiting on anyone:**
- Attempt self-Tech-Provider registration (Step 5b) — free to try
- Draft template wording for review (Step 6)
- Everything in Track D once B/C are ready

---

## 5. Realistic timeline

| Step | Typical duration | Depends on |
|---|---|---|
| Business Portfolio creation | Minutes | Someone with FB access doing it |
| Business verification | 2–5 business days | **How fast the NGO produces documents** — the real variable |
| Payment method | Minutes | Finance sign-off |
| BSP signup (if chosen) | Hours to a few days | Which BSP, their onboarding queue |
| Self-Tech-Provider App Review (if attempted) | Unconfirmed — budget 1–3 weeks, time-boxed | Meta's review queue; no guarantee |
| Template approval | Minutes–24 hours per template | Category correctness |

**Total, if the NGO moves fast on documents**: plausibly **1–3 weeks** for
a BSP-assisted path, or a few weeks longer if attempting self-Tech-Provider
first. The single biggest lever on speed is how quickly Step 3's documents
get produced — same as the Master Plan's original risk register flagged.

---

## 6. Open doubts — things this research could not fully pin down

Flagging these honestly rather than asserting false certainty:

1. **Exact App Review approval odds/timeline for a small, single-client
   Tech Provider applicant (Step 5b)** — official docs describe the
   mechanics but not outcomes for a case like ours. Treat it as a
   time-boxed experiment, not a guaranteed path.
2. **Which specific BSPs currently support Coexistence** vs. only fresh
   number onboarding — confirm live with each BSP's current docs before
   picking one; one search result suggested at least some plans/providers
   don't support it, which needs a direct check.
3. **BSP pricing** in §2 is from public pricing pages at time of writing
   and shifts often — get live quotes before deciding.
4. **Whether the same Business Portfolio/verification can serve both the
   NGO's general Meta presence and the Tech-Provider app** if Step 5b is
   attempted, or whether a second verification is needed — needs checking
   live in the Meta interface once you get there.

If any of these turn out differently once you're actually in the Meta
dashboard, that's expected — this document is the best pre-work research
can do; the live flow will confirm the rest.

---

## Sources

- [Onboard WhatsApp Business app users (Coexistence) — Meta for Developers](https://developers.facebook.com/documentation/business-messaging/whatsapp/embedded-signup/onboarding-business-app-users/)
- [WhatsApp Cloud API Get Started — Meta for Developers](https://developers.facebook.com/documentation/business-messaging/whatsapp/get-started)
- [Solution Partner overview — Meta for Developers](https://developers.facebook.com/documentation/business-messaging/whatsapp/solution-providers/overview)
- [Become a Tech Provider — Meta for Developers](https://developers.facebook.com/documentation/business-messaging/whatsapp/solution-providers/get-started-for-tech-providers)
- [Template categorization — Meta for Developers](https://developers.facebook.com/documentation/business-messaging/whatsapp/templates/template-categorization)
- [WhatsApp Coexistence — YCloud](https://www.ycloud.com/blog/whatsapp-business-app-coexistence-meta-update)
- [WhatsApp Coexistence — Clientify](https://clientify.com/en/blog/communication/whatsapp-coexistence)
- [Meta Business Verification required documents by country — Wati](https://support.wati.io/en/articles/11463208-meta-business-verification-required-documents-by-country)
- [Meta Business Verification — 360dialog docs](https://docs.360dialog.com/docs/resources/meta-business-verification)
- [Meta Verification Documents guide](https://saleshiker.com/blog/meta-business-verification-document-requirements-phone-number/)
- [WhatsApp Business API Pricing 2026 — EngageLab](https://www.engagelab.com/blog/whatsapp-business-api-pricing)
- [Tech Provider Program setup — Infobip docs](https://www.infobip.com/docs/whatsapp/tech-provider-program/setup-and-integration)
- [Tech Provider vs BSP — Whautomate](https://whautomate.com/whatsapp-tech-provider-vs-bsp)
- [360dialog pricing](https://docs.360dialog.com/partner/get-started/pricing)
- [AiSensy vs Interakt vs Wati 2026 comparison](https://aisensy.com/aisensy-vs-interakt-vs-wati)
- [WhatsApp template categories explained — Wati](https://support.wati.io/en/articles/11463465-whatsapp-template-categories-explained-utility-authentication-and-marketing)
- [WhatsApp API Message Templates Guide 2026 — Gurusup](https://gurusup.com/blog/whatsapp-api-message-templates)
