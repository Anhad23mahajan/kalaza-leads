# Track B — Full Meeting Script for Tomorrow

A detailed, speak-it-out script for walking the supervisor through Track B,
step by step, in full. Use `docs/TRACK_B_MEETING_CHEATSHEET.md` as the
glance-at-a-glance version during the actual meeting; use this one to
prepare tonight so every step is second nature.

**Do this after** showing him Track A on the phone
(`docs/SUPERVISOR_DEMO_SCRIPT_TRACK_A.md`). This picks up right after that.

---

## Opening — bridge from Track A to Track B

*"So that's the staff app — done, working, nothing more needed from you
for that part. Now I want to walk you through the other piece — getting
WhatsApp to reply automatically. I've done proper research on exactly what
this needs, and I want to go through it slowly, step by step, so you have
the full picture before deciding anything."*

---

## The number decision

*"There are two ways to set this up, and this is the one real decision
that's yours to make — everything after this is just execution."*

*"Option one: keep our current WhatsApp number. Staff keep using it
exactly like today, and the automatic system runs alongside it on the same
number. The problem is, Meta requires going through a paid middleman
company to set this up specifically — realistically ₹1,500 to ₹2,500 a
month, so somewhere around ₹18,000 to ₹30,000 a year, just for that
connection. There's a free way around that too — I could apply to Meta
directly myself — but Meta doesn't guarantee that gets approved, or how
long it takes."*

*"Option two: use a second, brand-new number, just for the bot. Our
current number keeps working exactly as it does now, completely
untouched. This one can be set up for free, with no approval process at
all — it's guaranteed to work, cost-wise. The only downside isn't money,
it's that we'd be running two numbers instead of one, so anywhere we've
shared our WhatsApp contact — the website, Google listing, posters — would
need updating to the new number eventually."*

*"My honest recommendation is option two, specifically because it can't
go wrong on cost or get stuck waiting on an approval. But it's your call —
what do you think?"*

**[Assume he picks Option Two — the new number. If he picks Option One
instead, stop here and revisit `docs/TRACK_B_PAPERWORK_PLAYBOOK.md` §3 for
that path instead.]**

---

## Step 1 — The Meta Business Portfolio

*"First thing we need is what's called a Business Portfolio — it's not an
app, it's a website, business.facebook.com. Think of it as Kalaza Care's
official account with Meta — it's where everything else gets set up:
verification, payment, the WhatsApp connection, all of it."*

*"Two questions for you: has anyone ever set up a Facebook Page or run
Facebook or Instagram ads for Kalaza Care? Because if so, there's a real
chance a business account already exists that we don't know about. And if
one does exist — who currently has admin access to it?"*

*"If nothing exists yet, we'll create one — but whoever's personal
Facebook account creates it becomes the long-term admin, so I'd want that
to be you or someone at the NGO, not me personally, so the organization
keeps control of its own account."*

---

## Step 2 — Business verification

*"Once that account exists, Meta needs to confirm Kalaza Care is a real
organization before it lets us send real messages properly — this is
called business verification, and it needs three documents:"*

1. *"Whichever we have — a trust deed, if we're registered as a trust, or
   a society registration certificate, if we're a society. Which one are
   we, and do you know where that document is?"*
2. *"A GST registration certificate, or a Udyam — that's the MSME
   registration — certificate. Either one works."*
3. *"One document proving our address — a utility bill, a property tax
   receipt, anything like that. Actually, the GST certificate might
   already cover this if it shows our address, so this might not even be
   a separate document."*

*"One really important thing — whatever name and address we put into the
Business Portfolio in Step 1 has to match these documents exactly, word
for word. Not close enough — exactly. So it's worth pulling the exact
legal name straight off these documents rather than typing it from
memory, because a small mismatch is the most common reason this gets
rejected."*

*"Once submitted, this usually takes 2 to 5 business days. This step,
honestly, is the single biggest thing that determines how fast this whole
project moves — everything else afterward is fast."*

---

## Step 3 — Payment method

*"Meta requires a card on file before any of this works — not because it
costs much, the actual usage is genuinely tiny, under a thousand rupees a
year — but it's just a hard requirement regardless of size."*

*"Whose card should this be? I'd want it to be something the organization
controls long-term — not a personal card of mine or of any one staff
member, so we're not stuck depending on one person for this."*

---

## Step 4 — The new number

*"For the bot's own number, we need a phone number that's never been on
WhatsApp before at all — not on someone's personal WhatsApp, not on our
existing WhatsApp Business app. A brand-new SIM works fine, or an old
unused line if we have one lying around."*

*"Who should own this number, and can we get a basic SIM for it? It
doesn't need to sit in an actual phone being used day to day — once it's
registered to the system, nobody needs to physically use that SIM again."*

*"And one thing to flag now so we don't forget it later — once this is
live, we'll need to update our website, Google listing, and any posters
to the new number, or people will keep messaging the old one and never
reach the bot."*

---

## Step 5 — Connecting it (nothing needed from him)

*"Once the account, the documents, and the number are all ready, actually
connecting everything is on me — creating the developer app, registering
the number, generating the access credentials. No approval process, no
waiting on Meta, no cost beyond what we've already talked about. You
don't need to think about this part at all."*

---

## The parallel ask — Track C, starting now

*"Separately from all of this, and it can start today, completely
independent of the number decision — someone needs to start writing down
the actual answers to the roughly 20 questions families ask most. What
palliative care actually includes here. What room types exist. Real
pricing. What medical equipment is on-site. What happens in an emergency.
I can build the system that sends these instantly and automatically, but
I genuinely can't write them myself — I don't know what's actually true
about the facility, and guessing would be dangerous. If the system told a
family we have equipment we don't, and they made a real decision based on
that, that's a real harm, not just a bug."*

*"Honestly, this is the piece most likely to make this take months
instead of weeks — so the sooner someone starts, the sooner all of this
becomes real. Who could take this on, and can we set a rough deadline?"*

---

## Closing — the four things to actually walk away with today

*"So, to summarize what I need from you: one, does a Meta Business
Portfolio already exist for us, and who's admin? Two, are we a trust or a
society, and can you get me the registration document plus a GST or Udyam
certificate? Three, who's getting a SIM for the new number? And four —
the big one — who's writing the 20 answers, and by when?"*

*"Everything else after that is on me. None of this needs to happen
today, just needs owners assigned."*

---

## If he pushes back

**On cost**: *"Meta's own charges are tiny either way — under a thousand
rupees a year. The bigger number, ₹18-30k a year, only applies if we
choose to keep the old number instead — that's exactly why I'm
recommending the new number, it avoids that entirely."*

**On timeline**: *"Once everything's unblocked, the actual coding is
maybe 6 to 8 weeks. But the real timeline depends on how fast we get the
documents and the 20 answers — that's realistically months, not weeks,
and I'd rather tell you that now than promise faster and fall short."*

**On "why can't you just do all this yourself?"**: *"The documents have to
come from the NGO because they're proof of who we are — I can't produce
them. The 20 answers have to come from someone who actually knows the
facility's real capabilities — I'd be guessing, and guessing here is
genuinely dangerous, not just sloppy."*
