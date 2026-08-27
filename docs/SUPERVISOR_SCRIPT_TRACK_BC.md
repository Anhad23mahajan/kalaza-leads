# Explaining Track B/C to the Supervisor — Anhad's Script + Prep Notes (v2)

**v2 — rewritten after proper research into Meta's actual requirements.**
The first version of this document estimated the WhatsApp automation would
cost roughly ₹450/year and implied Anhad could set it up entirely alone.
That was wrong on one important point: **keeping the existing WhatsApp
number (instead of switching to a new one) cannot be self-set-up — Meta's
own documentation requires going through a registered partner for that
specific piece.** This version corrects that, with real numbers and a real
decision to put in front of the supervisor. See
`docs/TRACK_B_META_ONBOARDING_PLAN.md` for the full research and sourcing
behind everything in this document.

This is for you (Anhad), not something to hand over as-is. It has two
parts: **Part A is what to actually say** to your supervisor, in his
language, no jargon. **Part B is the background you need to know** so you
can answer whatever he asks back, without having to check your phone
mid-conversation.

Have this conversation only after walking him through
`docs/SUPERVISOR_DEMO_SCRIPT_TRACK_A.md` — he needs to see the finished
staff app first, so he understands the next ask is a genuinely different,
bigger thing, not "why isn't the first thing done yet."

---

## PART A — What to say

### 1. Reconnect to what he actually asked for

*"You told me, more than once, that the real point of this whole project
is the automatic reply — that when someone messages us on WhatsApp asking
about palliative care or room availability, they should get an answer
right away, and if they reply again, we should reply back again
automatically. I've now properly researched exactly how that works, what
it needs, and what it realistically costs — including a couple of things
that turned out more complicated than I first thought. I want to walk you
through all of it honestly."*

### 2. Answer his own question directly

He asked: *"How would our application know that our enquiry has been done
on WhatsApp?"* That's the right question, and the honest answer is:

*"Our app on the phone can never see WhatsApp messages — no app can,
WhatsApp doesn't allow it, for anyone. The only real way is that WhatsApp
itself, through Meta (the company that owns WhatsApp), officially forwards
every message we get to a system we control, and that system replies
back. That's not a workaround — it's the standard, official way any
business does this. Setting that up is what Track B is."*

### 3. The number question — this is the first real decision he needs to make

*"There are two ways to do this, and I need your call on which one:"*

- **Option 1 — keep our current number.** Staff keep using WhatsApp
  Business on the phone exactly like today — same number, same chat
  history, same contacts. The automatic system runs alongside it, and any
  staff member can still jump into any conversation and take over any
  time. *This is the one I'd recommend, unless you have a reason not to.*
- **Option 2 — a brand-new, second number just for the bot.** Simpler to
  set up on my end and slightly cheaper (explained below), but means the
  NGO now runs two numbers — the old one for normal use, a new one just
  for the automatic replies. Every poster, Google listing, and social bio
  would need the new number, or people keep messaging the old one and
  never reach the bot.

### 4. The honest cost conversation — corrected from what I said before

*"I need to walk back something I told you earlier. I said this would
cost about ₹450 a year — that part's still true for the actual
message-sending cost. But I've since found there's a second cost I hadn't
accounted for, and I want to be upfront about it rather than surprise you
later:"*

- **Meta's own charges are genuinely tiny**: replying to someone within 24
  hours of them messaging is completely free, unlimited. Follow-up
  messages sent later cost about **12 paise each** (plus tax). At our
  volume, that's realistically **₹550-650 a year, including tax.** Not a
  budget conversation.
- **But** — *if we keep our current number* (Option 1 above), Meta
  requires that setup to go through a registered partner company, and the
  straightforward way to do that is to pay one of these companies a
  monthly fee — realistically **₹1,500 to ₹2,500 a month**, so roughly
  **₹18,000 to ₹30,000 a year.** That's the honest number if we want this
  done quickly and easily.
- **There is a free alternative**: I can try applying to Meta directly, to
  become that "registered partner" myself instead of paying a company for
  it. It costs nothing but my own time, and if it works, we're back to
  the original ~₹550-650/year total. The catch is Meta doesn't guarantee
  how long that application takes, or that it gets approved at all for a
  first-time applicant like me. *"I want to try the free route first,
  and give myself about two to three weeks on it — if it's not moving by
  then, I'll switch to paying a company instead so we're not stuck
  waiting indefinitely."*

*"So: worst case, this costs about ₹20-30k a year, which is still small
for what it does. Best case, if my direct application works, it's closer
to ₹600 a year. Either way it's not going to break the budget — I just
didn't want to promise you the smaller number without telling you the
other possibility."*

### 5. What it actually needs, and from whom

*"Here's everything that has to happen before the automatic replies can
go live. Most of it is on your side, not mine — I genuinely cannot do
these myself:"*

1. **Which WhatsApp number are we using**, and is it already on the
   WhatsApp Business app, or just regular WhatsApp? (Ties to the Option
   1/2 decision above.)
2. **Does the NGO have a Meta Business account already**, and who has
   admin access? If it doesn't exist, someone needs to create it and it
   should be an NGO-controlled account, not mine personally.
3. **Registration documents** — specifically, whichever of these Kalaza
   Care has: the trust deed or society registration certificate, a GST
   registration certificate or Udyam/MSME certificate, and one document
   proving our address (a utility bill or property tax receipt works).
   *These all need to show our name and address exactly matching what
   we'll put into the Meta account — even small mismatches cause
   rejection, so it's worth getting them right the first time.*
4. **A payment method** — a card, added to that Meta account. Even though
   day-to-day usage cost is tiny, Meta requires one on file before
   anything works. Should be something the NGO controls long-term, not a
   personal card.
5. **If we go with paying a company (Option 1, paid route)**: your
   approval on which company and its monthly cost, once I get a couple of
   quotes.
6. **The single biggest one: someone has to write down the actual answers**
   to the ~20 questions families ask most — what palliative care includes,
   room types, pricing, equipment, emergency backup, and so on. I can build
   the system that sends these answers instantly, but I cannot write them —
   I don't know what's actually true about the facility, and it would be
   genuinely dangerous to guess. If the bot tells a family we have
   equipment we don't, and they make a decision based on that, that's a
   real harm, not just an embarrassing bug.
7. **What languages** do people actually message in — just English, or
   Hindi and Marathi too? This matters because each language needs its own
   approved set of answers.

*"Of those seven, only my direct-Meta-application attempt is something I
can do alone. Everything else needs you or someone at the NGO."*

### 6. What you need from him, concretely, this week

*"Can you get me answers to those seven things? And specifically — who's
going to sit down and write the 20 answers, and by when? That one is the
thing most likely to make this take three months instead of three weeks,
so the sooner someone starts, the sooner this is real. Also — can you get
me the registration documents (or tell me they don't exist / need
renewing), so business verification can start?"*

### 7. Be honest about the timeline

*"Once accounts and documents are sorted, actually building the
automatic-reply system is maybe 6-8 weeks of my own coding work. But most
of the real delay isn't that — it's however long the paperwork and the 20
answers take, and my own attempt to avoid the monthly company fee adds
some uncertainty too. Realistically, this whole piece is a few months, not
weeks. I'd rather tell you that now than have you expect it next week and
be disappointed."*

### 8. Give him something free and immediate

*"There's one thing we can do this week, for free, with no approval
needed from Meta at all: WhatsApp Business's own app already has a
built-in greeting message and an away message. I can help you turn those
on today — so the moment this conversation ends, anyone who messages us
for the first time already gets an instant reply, while the real system
gets built behind the scenes. It's not the full thing, but it's a real,
visible win right away."*

### 9. Close

*"So: staff app — done, in your hands already. Automatic WhatsApp
replies — needs a number decision from you, some documents, someone
assigned to write the 20 answers, and either a company fee or some patience
while I try the free route. All of that can start now, in parallel with
me continuing to improve the app. Sound fair?"*

---

## PART B — Background, for you (in case he pushes back or asks follow-ups)

### If he asks: "Why did the cost estimate change?"

Be straightforward: the first estimate only counted what Meta itself
charges per message, which is genuinely small. What got missed is that
keeping the existing number requires going through a partner company for
the technical setup, and most of those companies charge a monthly fee for
that — this isn't Meta charging more, it's a separate company's service
fee for making the setup easy. Owning the mistake and explaining the fix
builds more trust than pretending the first number was always right.

### If he asks: "Why can't you just build it without all that?"

Because there's no way to intercept WhatsApp messages except through
Meta's official channel — that's a hard technical wall, not a shortcut
Anhad is avoiding. Separately, keeping the existing number specifically
(rather than starting a fresh number) is the piece that needs a partner —
a fresh number could be self-set-up with no company involved at all, but
that brings back the "two numbers" problem Option 1 was meant to avoid.
And separately from either of those: an AI or app that invents facts about
medical equipment or care capability is a real liability, not just a bug —
so the 20-answers piece isn't red tape, it's a genuine safety requirement.

### If he asks: "Why not just use one of those ready-made WhatsApp bot
### services instead of building our own?"

They exist (AiSensy, Wati, Interakt, DoubleTick — roughly ₹1,500–2,500+/month, which is actually close to what the "easy" path above already costs) and are honest to mention if
he asks. The case for building the actual reply logic ourselves, even if
we end up paying one of these companies just for the number-connection
piece: those tools don't model the specific things this NGO needs —
patient-vs-enquirer distinction, structured medical conditions, room-type
matching, and the not-converted-reason analytics he specifically asked
for, all of which are already built into the app today. Using one of
these companies is about the account-connection step, not necessarily the
whole bot.

### If he asks: "What if the bot says something wrong?"

The design rule, non-negotiable: the bot only ever answers from the
approved list of 20 answers. It never generates or guesses facts about the
facility, equipment, staffing, or pricing. Anything it doesn't have an
approved answer for gets a human handoff — "let me connect you with our
care coordinator" — never a guess. It also never negotiates price and
never gives medical advice.

### If he asks: "What about someone writing something really distressing,
### like about a dying parent?"

Those get routed straight to a human, immediately — not a canned reply.
A bot reply to something that emotional would hurt trust, not help it.
The bot only handles factual/logistical questions (location, cost, rooms,
brochure); anything emotional or medical-specific goes to a person.

### If he asks about language

Families in Pune write in Marathi, Hindi, and English, sometimes mixed in
one message. If this isn't handled, a large share of enquiries get a
broken experience. The system needs to detect the language and reply in
kind, and each language's approved answers need separate approval from
Meta — so this decision should be made early, not bolted on later.

### If he pushes for a faster timeline

Be honest rather than overpromise: at this NGO's volume (roughly 2-3
enquiries on a busy day, an estimated 200-400/year), there's no
enquiry-volume emergency forcing speed. The real conversion driver, per
his own theory, is follow-up discipline — and that (Track A) is already
done and live today. The bot adds speed and consistency on top, which
matters, but it isn't the only lever, and it shouldn't be rushed past
safety or past the free-setup attempt just to save a few weeks.

### If he asks why you're trying the "free" route instead of just paying a company

Because it's genuinely free to attempt (no fee just to apply), and if it
works, it saves the NGO ₹18,000-30,000 a year indefinitely, not just once.
The downside is only time, which is why it's time-boxed to 2-3 weeks
rather than left open-ended — if it stalls, switching to a paid company
is still available as a fallback with no wasted NGO money.

### Numbers to have ready if asked

- Meta's own message cost: **~₹550-650/year** at our volume (utility
  follow-up messages at ~13-14 paise each including tax; replies within 24
  hours are free, uncapped).
- Company/partner fee, if going the paid route: **~₹1,500-2,500/month**
  (~₹18,000-30,000/year) — this is what we're trying to avoid via the free
  direct-Meta application, time-boxed to 2-3 weeks.
- Document verification turnaround: typically **2-5 business days** once
  complete, matching documents are submitted — this is usually the fastest
  step, so document-gathering speed matters more than anything else.
- Messaging limits: new accounts get 250 unique contacts/day before full
  verification — at 2-3 enquiries/day, that's roughly 100x more headroom
  than needed, so this can be piloted well before full verification is
  even done.
- Realistic build time once unblocked: **6-8 weeks** of coding, on top of
  however long the NGO-side paperwork, the free-route application, and the
  20 answers take (historically the much longer part).

### The one line to remember if the conversation stalls

*The tracker (Track A) already works and doesn't need any of this. The
bot is additive, not a blocker to anything currently running.*
