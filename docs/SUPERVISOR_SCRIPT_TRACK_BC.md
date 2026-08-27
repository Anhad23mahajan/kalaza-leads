# Explaining Track B/C to the Supervisor — Anhad's Script + Prep Notes

This is for you (Anhad), not something to hand over as-is. It has two
parts: **Part A is what to actually say** to your supervisor, in his
language, no jargon. **Part B is the background you need to know** so you
can answer whatever he asks back, without having to check your phone
mid-conversation.

Have this conversation only after walking him through
`docs/SUPERVISOR_DEMO_SCRIPT_TRACK_A.md` — he needs to see the finished
staff app first, so he understands the next ask is a genuinely different,
bigger thing, not "why isn't the first thing done yet."

Full source material: `docs/MASTER_PLAN_V2.md` — this script is the
distilled, plain-language version of Parts 1–3 and 9–12 of that document.

---

## PART A — What to say

### 1. Reconnect to what he actually asked for

*"You told me, more than once, that the real point of this whole project
is the automatic reply — that when someone messages us on WhatsApp asking
about palliative care or room availability, they should get an answer
right away, and if they reply again, we should reply back again
automatically. I've thought that through properly, and I want to walk you
through exactly how it works, what it needs, and what's realistic."*

### 2. Answer his own question directly

He asked: *"How would our application know that our enquiry has been done
on WhatsApp?"* That's the right question, and the honest answer is:

*"Our app on the phone can never see WhatsApp messages — no app can,
WhatsApp doesn't allow it, for anyone. The only real way is that WhatsApp
itself, through Meta (the company that owns WhatsApp), officially forwards
every message we get to a system we control, and that system replies
back. That's not a workaround — it's the standard, official way any
business does this. Setting that up is what Track B is."*

### 3. Kill the two fears immediately

*"You had two worries, and I looked into both properly — they're both
non-issues:"*

- **"Will we lose our number?"** — *"No. Meta changed this — you keep
  using WhatsApp Business on your phone exactly like today, same number,
  same chats. The automatic system runs alongside it. Any staff member can
  still jump into any conversation any time and take over."*
- **"Does this cost money?"** — *"Barely anything. Replying to someone
  within 24 hours of them messaging us is completely free, no limit. Only
  the automatic follow-up messages a few days later cost anything, and
  that's about 15 paise each. At our volume, that's roughly ₹450 a year
  total. It's not a budget conversation."*

### 4. The honest part — what it actually needs, and from whom

*"Here's the part I want to be straight with you about. Six things need
to happen before the automatic replies can go live, and five of them are
on your side, not mine — I can't do them, only you or someone at the NGO
can:"*

1. **Which WhatsApp number are we actually using** for enquiries, and is
   it already on the WhatsApp Business app, or just regular WhatsApp?
2. **Does the NGO have a Facebook/Meta Business account**, and who has
   admin access to it? If it doesn't exist yet, someone needs to create it.
3. **Registration documents** — is Kalaza Care registered as a trust,
   society, or company? Meta needs to see that paperwork to verify us.
4. **A payment method** (a card) needs to be added to that Meta account —
   even though the actual usage cost is tiny, Meta requires one on file.
5. **The single biggest one: someone has to write down the actual answers**
   to the ~20 questions families ask most — what palliative care includes,
   room types, pricing, equipment, emergency backup, and so on. I can build
   the system that sends these answers instantly, but I cannot write them —
   I don't know what's actually true about the facility, and it would be
   genuinely dangerous to guess. If the bot tells a family we have
   equipment we don't, and they make a decision based on that, that's a
   real harm, not just an embarrassing bug.
6. **What languages** do people actually message in — just English, or
   Hindi and Marathi too? This matters because each language needs its own
   approved set of answers.

*"None of those six are things I can do by writing code. They need you,
or someone at the NGO."*

### 5. What you need from him, concretely, this week

*"Can you get me answers to those six things? And specifically — who's
going to sit down and write the 20 answers, and by when? That one is the
thing most likely to make this take three months instead of three weeks,
so the sooner someone starts, the sooner this is real."*

### 6. Be honest about the timeline

*"Once those six things are sorted, building the actual automatic-reply
system is maybe 6–8 weeks of my own work. But most of the delay isn't
that — it's waiting on the paperwork and the answers. Realistically, this
whole piece is a few months, not weeks. I'd rather tell you that now than
have you expect it next week and be disappointed."*

### 7. Give him something free and immediate

*"There's one thing we can do this week, for free, with no approval
needed from Meta at all: WhatsApp Business's own app already has a
built-in greeting message and an away message. I can help you turn those
on today — so the moment this conversation ends, anyone who messages us
for the first time already gets an instant reply, while the real system
gets built behind the scenes. It's not the full thing, but it's a real,
visible win right away."*

### 8. Close

*"So: staff app — done, in your hands already. Automatic WhatsApp
replies — needs those six things from your side, starting now, running in
parallel while I keep improving the app. Sound fair?"*

---

## PART B — Background, for you (in case he pushes back or asks follow-ups)

### If he asks: "Why can't you just build it without all that?"

Because there's no way to intercept WhatsApp messages except through
Meta's official channel — that's a hard technical wall, not a shortcut
Anhad is avoiding. And separately, an AI or app that invents facts about
medical equipment or care capability is a real liability, not just a bug —
so the answers piece isn't red tape, it's a genuine safety requirement.

### If he asks: "Why not just use one of those ready-made WhatsApp bot
### services instead of building our own?"

They exist (AiSensy, Wati, Interakt, DoubleTick — roughly ₹1,000–5,000/month) and are honest to mention if
he asks. The case for building it ourselves: it's free of recurring cost
beyond the ~₹450/year Meta fee, and — more importantly — those tools don't
model the specific things this NGO needs: patient-vs-enquirer distinction,
structured medical conditions, room-type matching, and the not-converted
reason analytics he specifically asked for. If Meta onboarding stalls
badly, one of these services (via what's called a "BSP") is a fallback
worth knowing about, but don't lead with it.

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
kind, and each language's answers need separate approval from Meta — so
this decision should be made early, not bolted on later.

### If he pushes for a faster timeline

Be honest rather than overpromise: at this NGO's volume (roughly 2–3
enquiries on a busy day, an estimated 200–400/year), there's no
enquiry-volume emergency forcing speed. The real conversion driver, per
his own theory, is follow-up discipline — and that (Track A) is already
done and live today. The bot adds speed and consistency on top, which
matters, but it isn't the only lever, and it shouldn't be rushed past
safety.

### Numbers to have ready if asked

- Estimated cost: **~₹450/year** at this volume (mostly follow-up
  template messages at ~₹0.145 each; inbound replies within 24 hours are
  free, uncapped).
- Messaging limits: new unverified accounts get 250 unique contacts/day —
  at 2–3 enquiries/day, that's roughly 100× more headroom than needed, so
  the whole thing can be piloted before full business verification is even
  done.
- Realistic build time once unblocked: **6–8 weeks** of coding, on top of
  however long the NGO-side paperwork and content take (historically the
  much longer part).

### The one line to remember if the conversation stalls

*The tracker (Track A) already works and doesn't need any of this. The
bot is additive, not a blocker to anything currently running.*
