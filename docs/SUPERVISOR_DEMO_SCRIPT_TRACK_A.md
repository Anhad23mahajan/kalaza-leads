# Kalaza Leads — What's Been Built (for the demo conversation)

A walkthrough script for showing the app to the supervisor. Plain language,
no tech jargon — this is about what the app *does*, not how it's built.
Go through it feature by feature, demo each one live on the phone if
possible, and pause for his reaction/opinion at each ⭐ point.

After this, there's a separate conversation about the WhatsApp auto-reply
piece (Track B/C) — see `docs/SUPERVISOR_SCRIPT_TRACK_BC.md`. Don't mix the
two; this one is about proving the staff app is finished and working.

---

## Opening line

*"I want to show you the staff app end to end — everything's built and
I've been testing it on my own phone for real. This is completely separate
from the WhatsApp auto-reply piece we'll talk about after — this part
doesn't need Meta, doesn't need any paperwork from you, it's just ready."*

---

## 1. Logging in

Staff log in with just their name and a password — no email needed. Only
people you've added can get in.

---

## 2. Adding a new enquiry

Show the Add Enquiry form. Walk through it field by field and tie it back
to what he asked for after the review:

- Phone number now only accepts exactly 10 digits, with a country code
  picker next to it (his request #1).
- "How did they reach out" is now two separate questions — how they *heard*
  about Kalaza Care (Google, Instagram, referral, etc.) and how they
  *contacted* us (call, WhatsApp, walk-in) — including Google Search,
  Call, and Hospital as options, since "call happens after Google search"
  (his own words, and now the app captures both facts) (request #2).
- Services now include palliative care, assisted living, cancer care,
  post-transplant care, medical recovery, dementia care, and more — 8
  options instead of 2 (request #3).
- "Where are you from" — location field (request #4).
- The form no longer feels janky to scroll through (request #5).
- Relation to patient is now a dropdown (son/daughter/spouse/etc.), not
  free text (request #8).
- Room type — single/double/sharing/full flat (request #9).
- Medical history field (request #10).
- Visit date — "when are you gonna visit" (request #11).
- What the patient actually has — Alzheimer's, cancer, etc., as a proper
  checklist, not free text (request #12).
- Follow-up date (request #13).

⭐ *"That's 11 of the 13 things you asked for after the demo. Does this
cover what your team actually needs when someone calls in?"*

---

## 3. Editing an enquiry after saving

Tap any enquiry, edit anything, change its status, save (request #6 — this
was the very first thing you asked for after seeing the old version).

---

## 4. The contact log

Every time staff call, WhatsApp, or visit a family, they log it — what
happened, whether it was positive/negative/no answer, and any notes. It
builds a timeline on that person's record. This is the proof that
follow-up actually happened, and it's what makes the reports later
possible.

⭐ *You said yourself: "if we don't properly reply back, their converting
chances become less... we need the follow-up, we need to call them." This
is what makes sure that never silently doesn't happen.*

---

## 5. The separate lists you asked for

Instead of one big list, there are now 7 tabs, each with a live count:
**Follow-ups Due, All, Active, Converted, Not Converted, Dormant, Backup.**
That's your "those converting / those not / those who just enquire and do
nothing / a backup list" ask, built exactly as separate lists.

⭐ *"Does this match how you pictured splitting the leads, or would you
group them differently?"*

---

## 6. One-tap WhatsApp messages

On any enquiry, staff can tap "Send WhatsApp" and it opens WhatsApp with a
ready-written message already filled in — a thank-you, a follow-up nudge,
or a post-visit feedback ask — personalized with the person's name and the
service they asked about. Staff can edit it before sending, but they don't
have to type from scratch every time. This works today, on any phone,
completely free — it's not the automatic version you asked about (that's
the Track B/C conversation), but it removes the "what do I even say"
friction right now.

---

## 7. Exporting to Excel

Any of those 7 lists can be exported and shared — tap the export icon, it
generates the file and opens WhatsApp/email/whatever you want to send it
through. Opens straight in Excel or Google Sheets. This is your "Excel
data file saved and shared" request.

---

## 8. Staff and who's following up with whom

There's now a staff list, and every enquiry can be assigned to a specific
person — your "follow-up person" column from your own Excel. You can add
staff, and mark someone inactive if they leave without deleting their
history.

---

## 9. Reports — the one to pay attention to

This is the most important screen to walk him through slowly:

- **Overall numbers**: how many enquiries, how many converted, what
  percentage, and how many days it typically takes someone to convert
  (your "after how many days are they converting" question).
- **Where people come from**, and which sources actually convert best —
  not just volume, but *quality* of each source.
- **Which services people ask for most**, and how many of those turn into
  admissions.
- **Who on the team is converting the most enquiries.**
- **Why people said no** — budget, chose another facility, wanted an
  amenity you don't have, and so on — ranked.
- **⭐ The "why we're losing families" report.** This is the one to slow
  down on: it lists, in their own words, what families said they wanted
  that we don't currently offer or couldn't confirm. After a few months of
  real use, this becomes hard evidence — not "I think people want X" but
  "14 families specifically said they needed a lift and we don't have
  one." That's the kind of thing that can justify a real decision, not a
  guess.
- **What budgets people are actually asking for**, compared to what we
  charge.

⭐ *"This is built to answer the exact questions you asked in the review —
how many convert, why they don't, what they actually want. Once real data
builds up over a few months, this becomes genuinely useful for decisions,
not just record-keeping. What else would you want to see here?"*

---

## 10. Follow-up reminders

The app now reminds staff on their phone when a follow-up is due, so
nobody has to remember to check manually. It's not instant — it checks
once a day — but it means a follow-up date set two days ago won't just
silently get forgotten.

---

## Closing line

*"That's the entire staff app — it's done, it's live, and it needs nothing
further from you or the NGO to keep working. Everything from here is about
the next piece — the actual automatic WhatsApp replies — and that part
does need some things from your side. Can we talk through that now?"*

**Then, if he's ready, move to `docs/SUPERVISOR_SCRIPT_TRACK_BC.md`.**
