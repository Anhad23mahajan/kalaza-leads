# Track B — The Full Paperwork & Decision Playbook

For Anhad, to prepare for (and then execute) the Track B conversation and
process with the NGO supervisor. This is the definitive version — it
replaces every earlier Track B/C script. Companion document:
`docs/TRACK_B_META_ONBOARDING_PLAN.md` has the original deep research with
full source links if you want to double-check anything here.

**What this document is for**: walking the supervisor through the decision
between two real options, then — once he decides — executing every step in
exact order, for either path, with nothing skipped.

**Honesty note before anything else**: everywhere this document states a
fact, it's backed by Meta's own documentation or a corroborating source
(linked in §10). Everywhere it's uncertain, it says so explicitly rather
than guessing. Meta's own interface can shift button names/menu locations
over time — the *sequence and requirements* below are solid; if a specific
click-path looks different when you're actually in it, the underlying step
is still correct.

---

## 0. The one-paragraph version

Kalaza Care's WhatsApp number needs to be officially connected to Meta's
WhatsApp Business Platform so a system we build can read incoming messages
and reply automatically. That connection requires an approved Meta
Business account (paperwork, not code) and a phone number to connect —
and there are two different ways to handle *which* phone number, with very
different cost and complexity consequences. Everything else (the NGO's
documents, the payment method, the message templates) is required no
matter which number path is picked.

---

## 1. THE decision: keep the existing number, or use a new one

This is the only decision that needs the supervisor's input before work
can proceed. Everything else in this document is execution, not a
judgment call.

| | **Option A — Coexistence (keep existing number)** | **Option B — New dedicated number** |
|---|---|---|
| **What it means** | Staff keep using WhatsApp Business on the phone exactly as today — same number, same chats, same contacts. The automation runs on the same number in parallel. | A second WhatsApp number, used only by the bot. The NGO's original number keeps working exactly as it does now, completely separate. |
| **Requires a Tech Provider/BSP?** | **Yes — confirmed, not optional.** Meta's own documentation requires the connecting developer to be a registered Tech Provider or Solution Partner to enable Coexistence. A plain, direct Cloud API setup cannot do this. | **No.** A fresh number is set up through Meta's plain, direct Cloud API path — no Tech Provider status needed at all. |
| **Requires Meta's "App Review" approval?** | Only if going the free self-Tech-Provider route (see §1a below) — and that specific approval has no guaranteed timeline or outcome. | **No.** Managing your own single WhatsApp account only needs "Standard Access," which doesn't go through Meta's App Review process at all. |
| **Realistic cost** | Free (~₹550–650/year, Meta's own message fees) *if* the self-Tech-Provider attempt succeeds. Otherwise ~₹18,000–30,000/year for a commercial BSP subscription. | **Guaranteed free** beyond Meta's own tiny per-message fee (~₹550–650/year) — there's no approval gate to fail, so there's no scenario where this costs more. |
| **What's lost/changed** | Group chat sync, disappearing messages, view-once media, live location, and broadcast lists stop working on that number. All linked companion devices (WhatsApp Web/Desktop) get unlinked and need re-linking. | Nothing changes on the existing number. The new number needs its own SIM/line and has to be publicized everywhere the NGO currently shares its WhatsApp contact — website, Google listing, posters, social bios. |
| **Certainty** | Cost and approval outcome both genuinely uncertain until attempted. | Certain — this path cannot fail on cost or approval, only on ordinary setup mistakes (fixable). |

**Anhad's honest recommendation**: Option B is the safer default given
budget is a hard constraint — it can't surprise us with a bill or a stalled
approval. Option A is nicer for the NGO's day-to-day experience (one
number, not two) but carries real risk on both cost and timeline. This is
genuinely the supervisor's call — present both fairly.

### 1a. If Option A is chosen: which sub-path?

Even within Option A, there are two ways to satisfy the Tech Provider
requirement:
- **A-free**: Anhad applies directly to Meta to become the registered Tech
  Provider himself. No fee, but Meta's approval process has no confirmed
  timeline for a small first-time applicant. Recommend time-boxing this to
  2–3 weeks before falling back.
- **A-paid**: Pay a commercial BSP (AiSensy from ~₹1,500/month, Interakt
  ~₹2,142/month, Wati from ~₹2,499/month are the common India options) to
  handle the connection for an ongoing fee.

---

## 2. Universal steps — needed no matter which option is chosen

Do these first, regardless of the Option A/B decision. Nothing in §3/§4
can start until these are done.

### Step 1 — Create (or locate) a Meta Business Portfolio

**What**: A Business Portfolio (Meta's current name for what used to be
called "Business Manager") is the container that holds Kalaza Care's
WhatsApp Business Account, ad accounts, and pages. Every other step in
this whole plan happens inside one of these.

**Why**: Nothing in the WhatsApp Business Platform can be set up outside a
Business Portfolio — it's what Meta actually verifies (Step 2), not a
personal Facebook profile.

**Exactly how**:
1. Go to [business.facebook.com](https://business.facebook.com).
2. Log in with a personal Facebook account (this needs to be an account
   that will hold long-term admin rights for the NGO — ideally not
   Anhad's personal account, so the NGO retains control if Anhad moves on).
3. If a Business Portfolio for Kalaza Care doesn't already exist: click
   "Create Account," enter the NGO's **exact legal name and address** —
   this needs to match the documents used in Step 2 exactly, or
   verification gets rejected.
4. If one already exists: find out who currently has admin access — this
   is one of the first questions to ask the supervisor.

**Who does this**: The supervisor, or whoever the NGO designates as the
long-term account owner. Free, takes minutes once the "who owns this"
question is settled.

**What happens next**: Move to Step 2 once the Business Portfolio exists
and Anhad has (or can get) access to help with the rest.

---

### Step 2 — Business verification

**What**: Meta checks submitted documents against the Business Portfolio's
stated name and address to confirm Kalaza Care is a real, legitimate
organization.

**Why**: Required before the account can send messages at meaningful
volume, and before a proper display name ("Kalaza Care") shows to message
recipients instead of a raw phone number. Also reduces the account's risk
of being flagged/restricted.

**Exactly what documents are needed** (India, non-company entities like
trusts/societies):
1. The **trust deed or society registration certificate** — the entity's
   founding/constitutional document. This is the core one; ask the
   supervisor directly whether Kalaza Care is registered as a trust or a
   society, and whether this document is on hand or needs to be located.
2. A **GST registration certificate** or a **Udyam (MSME) registration
   certificate** — either works.
3. An **address-proof document** in the entity's name: a utility bill, a
   property tax receipt, a lease deed, or the GST certificate itself if it
   shows the address.

**Critical accuracy requirement**: every document must show the exact same
legal name and address as what was entered into the Business Portfolio in
Step 1. Mismatches (even small ones — an abbreviation, a slightly
different address format) are the single most common rejection reason.
Double-check this before submitting.

**Exactly how**:
1. Inside the Business Portfolio → Business Settings → Security Center →
   "Start Verification."
2. Upload the documents from the list above.
3. Wait for review.

**How long**: Typically **2–5 business days** for complete, matching
documents. If rejected, Meta will usually say why (most often a name/
address mismatch) — fix and resubmit.

**Who does this**: The documents themselves can only come from the NGO —
Anhad cannot supply or fabricate them. Anhad can help with the actual
upload/submission if given access, but someone at Kalaza Care has to
physically locate the documents first. **This is the single biggest
timeline variable in all of Track B** — faster documents mean a faster
launch.

**What happens next**: Once verified, proceed to Step 3. (Technically it's
possible to start some setup before verification finishes, since
unverified accounts get a 250-contacts/day allowance — but the display
name won't show properly and the account carries more risk, so don't treat
this as a reason to skip verification.)

---

### Step 3 — Add a payment method

**What**: A credit/debit card added to the Business Portfolio's payment
settings.

**Why**: Meta requires a card on file before any WhatsApp messaging works
at all — even though actual usage cost is tiny (§1's cost table), this is
a hard requirement, not optional.

**Exactly how**: Business Settings → Payment Methods, inside the same
Business Portfolio. Add a card.

**Who does this**: Whoever at the NGO has finance authority for this — it
should be a card/account the NGO controls long-term, not something tied to
one person's personal card that could later become inaccessible.

**What happens next**: With Steps 1–3 done, the Option A/B fork begins.

---

## 3. If Option A was chosen (Coexistence — keep the existing number)

### Step 4A — Confirm the existing number qualifies

**What**: Check that the number is already active on the **WhatsApp
Business app** (not just regular WhatsApp), version 2.24.17 or newer.

**Why**: Coexistence connects to an *already-running* WhatsApp Business
app account — it doesn't work with plain WhatsApp or with a number that
isn't on the Business app yet.

**How**: On the phone that has WhatsApp Business installed, check
Settings → Help → App info for the version number. Update if needed
(free, via Play Store).

**If the number is currently on plain WhatsApp, not WhatsApp Business**:
it needs to be converted to the Business app first — this is a standard,
well-supported conversion that keeps the same number, but do it carefully
and make sure chat backups are current before converting, since it's a
real change to how that phone works day-to-day.

### Step 5A — Pick free or paid, and execute

**If A-free (self-Tech-Provider) — try this first:**

1. Go to [developers.facebook.com](https://developers.facebook.com), create a
   developer account if Anhad doesn't already have one.
2. Create a new Meta app, selecting the WhatsApp/business-messaging use
   case, connected to Kalaza Care's Business Portfolio from Step 1.
3. Complete business verification **for this developer app specifically**
   if prompted (may reuse the Step 2 verification, or require a separate
   pass — confirm live, this is one of the genuine open uncertainties).
4. Submit for **App Review**, specifically requesting Advanced Access to
   `whatsapp_business_messaging` and `whatsapp_business_management`. This
   is the actual approval gate — fill out whatever business-justification
   form Meta requires honestly (this is a real NGO's own account, not a
   reseller situation, which should work in our favor, but isn't
   guaranteed).
5. **Time-box this to 2–3 weeks.** If there's no meaningful movement by
   then, stop and switch to A-paid below rather than let it block the
   whole project indefinitely.
6. Once approved: create a "Partner Solution" in the developer dashboard,
   which unlocks the Embedded Signup flow to actually connect Kalaza
   Care's number (proceed to Step 6A below).

**If A-paid (commercial BSP):**

1. Get live quotes from 2–3 BSPs (AiSensy, Interakt, Wati are the common
   India options) — pricing shifts, don't rely on the numbers in this
   document without checking their current pricing pages.
2. **Before committing, explicitly confirm the chosen BSP's plan supports
   Coexistence** — not all of their plans do; ask directly or check their
   current documentation.
3. Get supervisor approval on the specific cost before signing up.
4. Follow that BSP's own signup flow — they'll walk through Embedded
   Signup themselves (this overlaps with Step 6A below, but the BSP
   handles the technical parts).

### Step 6A — Complete Embedded Signup (connecting the existing number)

Whoever is doing this (Anhad if self-Tech-Provider, or the BSP if paid)
walks the business owner through:
1. Selecting "connect an existing WhatsApp Business app account."
2. Entering the WhatsApp Business app phone number.
3. Receiving a verification code via a message from the official Meta
   Business Account.
4. Tapping "Connect to the Business Platform" inside the WhatsApp Business
   app itself.
5. Choosing whether to share existing chat history (optional).
6. Entering the verification code to complete the connection.

**Time-sensitive step right after this**: within **24 hours** of
completing this flow, the contacts and message-history sync has to be
triggered (a technical/Track-D-adjacent step) or the business has to
redo the whole Embedded Signup flow. Flag this so it isn't accidentally
left for days after the business owner completes their part.

**What's disabled going forward**: group chat sync, disappearing messages,
view-once media, live location messages, and broadcast lists (read-only).
One-to-one chats, calls, the business profile, and contacts sync keep
working normally. All companion devices (WhatsApp Web/Desktop) get
unlinked and need re-linking (excluding WhatsApp for Windows, which isn't
supported at all under Coexistence).

**What happens next**: Proceed to §5 (message templates), the last
universal step.

---

## 4. If Option B was chosen (a new dedicated number)

This path is meaningfully simpler — no Tech Provider status, no App
Review, no Embedded Signup complexity.

### Step 4B — Obtain the new number

**What**: A phone number not currently registered to any WhatsApp account
(regular or Business), capable of receiving an SMS or voice call for a
one-time verification code.

**Why**: WhatsApp requires proving control of a number via that code
before it can be registered to any WhatsApp account, API or otherwise.

**How**: A new SIM card, or an existing unused number the NGO controls.
Decide who owns/pays for this line long-term (small ongoing cost —
whatever a basic SIM plan costs, separate from everything else in this
document).

### Step 5B — Set up the Cloud API directly

1. Go to [developers.facebook.com](https://developers.facebook.com), create a
   developer account if needed.
2. Create a new Meta app → select the "Connect with customers through
   WhatsApp" use case → connect it to Kalaza Care's Business Portfolio
   from Step 1.
3. In the app's WhatsApp → API Setup section, register the new phone
   number: enter the number, receive and enter the verification code.
4. Save the resulting WhatsApp Business Account ID and phone number ID —
   Track D's code will need these.
5. Generate a temporary access token from the dashboard and send a test
   message to confirm the connection works.
6. Set up a webhook test endpoint to confirm message-status notifications
   arrive (this starts to overlap with Track D — a placeholder/test
   endpoint is enough for now, the real one gets built in Track D).
7. In Business Settings, create a System User, assign it the WhatsApp
   asset, and generate a **permanent** access token with
   `business_management`, `whatsapp_business_messaging`, and
   `whatsapp_business_management` permissions. **No App Review needed for
   this — it's standard access, since this is Kalaza Care's own single
   account,** not managing other businesses' accounts.

**What happens next**: Proceed to §5 (message templates), the last
universal step.

---

## 5. Universal (again) — draft and submit message templates

Needed regardless of which option was chosen, once the number is
connected either way.

**What**: Any message sent *outside* a 24-hour window since the user last
messaged has to use a pre-approved template — free-form replies only work
inside that 24-hour window.

**Why**: This is Meta's spam-prevention mechanism; every template gets
reviewed against its declared category before use.

**Categories** (getting this wrong is the most common rejection reason):
- **Utility** — follow-up on something the user did (an enquiry, a visit).
  This is what our planned follow-up/feedback messages should be —
  cheapest, easiest approved.
- **Marketing** — broader outreach (e.g. re-engaging a dormant lead) —
  costs more per message, use sparingly.
- **Authentication** — OTPs/login codes only, not relevant here.

**Exactly how**:
1. Inside WhatsApp Manager (part of the Business Portfolio) or the chosen
   BSP's template editor, draft the wording for each planned message —
   start with: enquiry follow-up #1, follow-up #2, post-visit feedback ask
   (see `docs/MASTER_PLAN_V2.md` Part 6.4 for suggested wording to adapt).
2. Assign each one the correct category.
3. Submit. Most utility templates get automated approval within minutes;
   if flagged for human review, up to 24 hours.
4. **If multiple languages are planned** (English/Hindi/Marathi, per
   Master Plan §2.8), each language variant is a **separate submission**
   needing its own approval — this needs a decision from the supervisor
   on which languages to support before this step can be considered done.

**Who does this**: Anhad drafts, but the supervisor (or whoever
understands what families actually need to hear) should approve wording
before submission — getting the tone right matters as much as getting the
category right.

---

## 6. What happens after Track B is done

Once a number is connected (either path) and initial templates are
approved, Track B is functionally complete. What's left — a real webhook
endpoint, the reply logic, the guardrails against the bot inventing facts
— is **Track D**, pure coding work, and doesn't need further supervisor
action to *start*. It does, however, still need Track C (the NGO's actual
answers to the ~20 common questions) to have any real content to send —
Track C runs in parallel and isn't covered in full detail in this
document yet (flagged for its own dedicated research pass, same treatment
this document gave Track B).

---

## 7. Master checklist — print-and-follow version

```
[ ] Step 1  — Create/locate Meta Business Portfolio, confirm admin owner
[ ] Step 2  — Gather documents (trust deed/society cert + GST/Udyam +
              address proof) → submit for Business Verification → wait
              2-5 business days
[ ] Step 3  — Add a payment method
[ ] DECISION POINT — Supervisor chooses Option A (keep number) or
    Option B (new number)

    IF OPTION A:
    [ ] Step 4A — Confirm number is on WhatsApp Business app 2.24.17+
    [ ] Step 5A — Decide free (self-Tech-Provider, time-boxed 2-3 weeks)
                  or paid (BSP, get quotes, confirm coexistence support)
    [ ] Step 6A — Complete Embedded Signup with the business owner;
                  trigger contact/history sync within 24 hours

    IF OPTION B:
    [ ] Step 4B — Obtain a new number (new SIM or unused existing line)
    [ ] Step 5B — Direct Cloud API setup: create Meta app, register
                  number, generate permanent access token (no App
                  Review needed)

[ ] Step 5  — Draft, categorize, and submit message templates (decide
              languages first if more than English is needed)
[ ] Handoff — Track D coding can begin; Track C content gathering
              should already be running in parallel
```

---

## 8. Cost summary (final)

| Path | Setup cost | Ongoing cost |
|---|---|---|
| Option B (new number) | ₹0 | ~₹550-650/year (Meta message fees) + cost of a basic SIM plan for the new line |
| Option A, self-Tech-Provider (if approved) | ₹0 | ~₹550-650/year |
| Option A, paid BSP | ₹0 (most BSPs) | ~₹18,000-30,000/year + Meta's message fees |

---

## 9. Timeline summary

| Step | Typical duration |
|---|---|
| Business Portfolio creation | Minutes |
| Business verification | 2-5 business days (fastest if documents are ready) |
| Payment method | Minutes |
| Option A: self-Tech-Provider approval | Unconfirmed, time-boxed to 2-3 weeks |
| Option A: BSP signup | Hours to a few days |
| Option B: direct Cloud API setup | Hours, once documents/verification are done |
| Template approval | Minutes to 24 hours per template |

**The single biggest lever on total speed, either path**: how fast the
NGO produces the verification documents in Step 2.

---

## 10. Open uncertainties — stated honestly, not guessed away

1. Whether the App Review submission in Step 5A needs a *separate*
   business verification pass for the developer app, or reuses Step 2's —
   needs confirming live in the Meta dashboard.
2. Real-world approval odds/timeline for a small, single-client
   self-Tech-Provider applicant — Meta's docs describe the mechanics, not
   outcomes for a case like this one.
3. Which specific BSPs currently support Coexistence on their cheaper
   plans versus only their higher tiers — confirm directly with whichever
   BSP is shortlisted before committing.
4. Exact current UI button names/locations in Business Manager/WhatsApp
   Manager — Meta changes these periodically; the steps above are
   conceptually accurate even if a label has shifted by the time you're
   in there.

---

## Sources

- [Onboard WhatsApp Business app users (Coexistence) — Meta for Developers](https://developers.facebook.com/documentation/business-messaging/whatsapp/embedded-signup/onboarding-business-app-users/)
- [WhatsApp Cloud API Get Started — Meta for Developers](https://developers.facebook.com/documentation/business-messaging/whatsapp/get-started)
- [Solution Partner overview — Meta for Developers](https://developers.facebook.com/documentation/business-messaging/whatsapp/solution-providers/overview)
- [Become a Tech Provider — Meta for Developers](https://developers.facebook.com/documentation/business-messaging/whatsapp/solution-providers/get-started-for-tech-providers)
- [Template categorization — Meta for Developers](https://developers.facebook.com/documentation/business-messaging/whatsapp/templates/template-categorization)
- [Meta Business Verification required documents by country — Wati](https://support.wati.io/en/articles/11463208-meta-business-verification-required-documents-by-country)
- [Meta Business Verification — 360dialog docs](https://docs.360dialog.com/docs/resources/meta-business-verification)
- [WhatsApp Business API Pricing 2026 — EngageLab](https://www.engagelab.com/blog/whatsapp-business-api-pricing)
- [WhatsApp API Pricing India 2026 — RichAutomate](https://richautomate.in/blog/whatsapp-business-api-without-monthly-fee-india-2026)
- [AiSensy vs Interakt vs Wati 2026 comparison](https://aisensy.com/aisensy-vs-interakt-vs-wati)
- Full original source list: `docs/TRACK_B_META_ONBOARDING_PLAN.md`
