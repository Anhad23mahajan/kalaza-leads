# Kalaza Leads — Complete Handoff Guide

**Read this first if you are a new person or a new Claude Code instance picking this project up with no prior context.**
Written 2026-09-21. It is meant to be enough, on its own, to continue the project on a different machine and a different Claude account.

> ⚠️ **This repository is PUBLIC.** This guide therefore contains **no** passwords, tokens, API keys, phone numbers or private personal details, and neither must anything you add. Secrets travel by other means — see §17.

---

## Contents

0. [How to use this document](#0-how-to-use-this-document)
1. [The 60-second summary](#1-the-60-second-summary)
2. [Purpose, people and context](#2-purpose-people-and-context)
3. [Timeline — how we got here](#3-timeline--how-we-got-here)
4. [What the supervisor asked for (requirements record)](#4-what-the-supervisor-asked-for-requirements-record)
5. [Architecture](#5-architecture)
6. [Repository map](#6-repository-map)
7. [Database: schema, security model, rebuilding from scratch](#7-database-schema-security-model-rebuilding-from-scratch)
8. [The Android app: features and how they work](#8-the-android-app-features-and-how-they-work)
9. [Intake pipeline: Google Form → Apps Script → Supabase](#9-intake-pipeline-google-form--apps-script--supabase)
10. [Building, running, and the environment](#10-building-running-and-the-environment)
11. [Git and GitHub workflow](#11-git-and-github-workflow)
12. [Working with Anhad: style, preferences, standing rules](#12-working-with-anhad-style-preferences-standing-rules)
13. [Decision log — what was decided, reversed, and why](#13-decision-log--what-was-decided-reversed-and-why)
14. [Lessons and gotchas (troubleshooting index)](#14-lessons-and-gotchas-troubleshooting-index)
15. [Track B / C / D — the WhatsApp automation](#15-track-b--c--d--the-whatsapp-automation)
16. [Open items and next steps](#16-open-items-and-next-steps)
17. [Security, privacy, and working from someone else's account](#17-security-privacy-and-working-from-someone-elses-account)
18. [What is NOT in GitHub](#18-what-is-not-in-github)
19. [Glossary](#19-glossary)

---

## 0. How to use this document

**Reading order for a new Claude Code instance**
1. This file, §1 then §12 (how to behave) then §17 (what not to do with secrets).
2. `docs/ROADMAP.md` — what to do next.
3. `docs/PROGRESS.md` — the running engineering log (newest facts about the build).
4. Then only what the task needs: `docs/GOOGLE_FORM_INTAKE_SPEC.md` (form), `docs/AUTOMATION_DESIGN.md` (bot design), `docs/TRACK_B_PAPERWORK_PLAYBOOK.md` (Meta onboarding), `docs/TRACK_A_TECHNICAL_SUMMARY.md` (feature-by-feature build notes).

**Where facts live (so you don't duplicate them)**

| Topic | Authoritative place |
|---|---|
| Current status / what's next | `docs/ROADMAP.md` |
| Engineering log, environment gotchas | `docs/PROGRESS.md` |
| Everything, from the start, incl. history and *why* | **this file** |
| The database schema | `docs/sql/001…006` (run in order) and `Lead.kt` |
| Google Form questions and bridge setup | `docs/GOOGLE_FORM_INTAKE_SPEC.md`, `tools/google-form-bridge/KalazaFormBridge.gs` |
| WhatsApp bot rules, sequences, tables | `docs/AUTOMATION_DESIGN.md` |
| Meta / WhatsApp onboarding decision + steps | `docs/TRACK_B_PAPERWORK_PLAYBOOK.md` |
| Track B meeting prep (glance sheet / rehearsal script) | `docs/TRACK_B_MEETING_CHEATSHEET.md`, `docs/TRACK_B_SUPERVISOR_MEETING_SCRIPT.md` |
| Track A feature-by-feature build notes | `docs/TRACK_A_TECHNICAL_SUMMARY.md` |
| Plain-language script for demoing Track A to the supervisor | `docs/SUPERVISOR_DEMO_SCRIPT_TRACK_A.md` |

**Recovering deleted documents.** Some older docs were deliberately deleted as superseded. They still exist in git history. Commit `ffe8913` still contains all three:
`git show ffe8913:docs/MASTER_PLAN_V2.md` (787 lines — the original three-track plan, supervisor quotes, cost analysis),
`git show ffe8913:docs/PROJECT_SPEC.md` (the v1 spec),
`git show ffe8913:docs/TRACK_B_META_ONBOARDING_PLAN.md` (the original Meta research with source links).

**Your first 30 minutes (checklist)**
1. `git clone` the repo, `git log --oneline -5`, and read `CLAUDE.md`, then this file's sections 1, 12 and 17.
2. Ask Anhad: *What happened in the Track B supervisor meeting? Which of these do you have on this machine: `local.properties` (Supabase URL + anon key), an Android phone, Android Studio? What do you want to work on today?* (Section 15 and section 16 explain why these matter.)
3. Don't assume the build works from your shell: try `gradlew` once; if it fails with a loopback error, ask the human to run it (section 10.4).
4. Before changing anything, run `git status` and read `docs/PROGRESS.md` (newest facts) and `docs/ROADMAP.md`.
5. When you finish something: update `docs/PROGRESS.md` (and `ROADMAP.md`), and follow the commit/push routine in section 11.

**Keep this document alive.** When something material changes, update the relevant section and the "Last verified" line below, and keep `docs/PROGRESS.md` and `docs/ROADMAP.md` in step. A stale handoff is worse than none.

Last verified against the repo: **2026-09-21** (main in sync with GitHub). External facts (Meta WhatsApp pricing/limits, Supabase free-tier terms, BSP pricing) last checked by live web search: **2026-09-22** — see the inline notes in §7 and §15 for what changed and the sources.

---

## 1. The 60-second summary

- **What it is:** *Kalaza Leads* — an Android app (Kotlin, Jetpack Compose, Supabase) that helps an elder-care NGO in Pune (**Kalaza Care**) never lose an enquiry from a family asking about care for a parent. It tracks every enquiry, schedules follow-ups, keeps a contact log, and reports on conversion.
- **Who built it:** Anhad Mahajan, solo, as a portfolio/coursework project for a real client (the NGO supervisor is the requester).
- **State (2026-09-21):**
  - **Track A — the Android CRM: 100% built and tested on a real phone.** Login, leads list (7 tabs), lead detail/edit, contact log, follow-up notifications, staff roster + assignment, CSV export, reports, one-tap WhatsApp (`wa.me`).
  - **Security fix shipped:** self-signup is gated by an active-staff roster.
  - **Intake moved to a Google Form** feeding the same database through a Google Apps Script bridge. The in-app "New Enquiry" screen was **deleted on purpose**. Verified end-to-end.
  - **Track B (Meta/WhatsApp Business onboarding), Track C (NGO-written content), Track D (the auto-reply bot): not started.** They depend on decisions and paperwork only the NGO can supply.
- **The supervisor's real priority is the WhatsApp auto-reply bot** (Track D). The CRM was the unblocked part, so it was built first.
- **What's next:** see `docs/ROADMAP.md`. In one line: tidy loose ends → get the Track B decision from the supervisor → collect NGO content → build the bot in thin slices.
- **The Supabase project and the Google Form/Sheet/Script belong to Anhad's personal accounts.** You cannot reach them without Anhad. See §17–§18.

---

## 2. Purpose, people and context

**The client and the problem.** *Kalaza Care* is an elder-care NGO in Pune (medical/palliative/assisted-living care). Families call, WhatsApp or walk in to ask about care for a parent. Without a system some of those enquiries slip through. In the supervisor's words: *"the primary task is to take the follow-up on the enquiry."* Kalaza Leads exists so **no enquiry is forgotten, every contact is provable, and the NGO learns why families do or don't join.**

**The people (by role — no private details on purpose):**
- **Anhad** — the developer and owner. A student building this solo as both a real deliverable for the NGO and a portfolio project. Runs all Gradle builds himself, tests on his own Android phone, and makes all product decisions. Blunt, terse, wants exact next steps (see §12).
- **The NGO supervisor** — the client/requester. Non-technical; thinks in outcomes ("the auto-reply should get back to them"). He redefined the project after seeing the first demo (§4). He decides the WhatsApp number strategy and who writes the answers. He declined to share his Excel of past enquiries.
- **The NGO's care team** — the only people who know the facility's facts. Track C depends on them writing ~20 answers.
- **Teammates from the original Kalaza Care project** — four others; each works on their *own separate* NGO project and is **not** involved here. One of them (a friend) maintains the original Kalaza Care app and holds *its* Supabase credentials. Another friend's Claude Pro account was once considered for running this project in Claude Code cloud (see §13 — abandoned, but relevant again now that work may move to a friend's machine).
- **You (the new person / Claude)** — working from Anhad's repo without his accounts. Read §12 and §17 before touching anything.

**The relationship to Kalaza Care (the other app).** Kalaza Care is the earlier 5-person Android app (Kotlin/Compose + Supabase/Firebase) for residents already in the facility — medication tracking, shifts, RBAC, escalation alerts, audit log — in production use, separate repo `Anhad23mahajan/kalaza-care`. Kalaza Leads handles everyone *before* they become a resident. The original idea was that, on conversion, a lead hands off into Care's resident data; that handoff was **deferred** and the two share no database (§13). Kalaza Leads' scaffold (Gradle version catalog, theme structure, Supabase wiring pattern) was forked from Kalaza Care's.

**Original design reasoning (from the first concept document, Aug 22)** — kept because it explains decisions you may be tempted to revisit:
- *Why a CRM and not "a chatbot".* The supervisor's shiny idea was WhatsApp auto-reply (chatbots, airport-style review messages). The value he actually needs is **discipline**: capture every enquiry, never miss a follow-up, know each outcome, prove contact happened. The first plan therefore made the follow-up tracker Phase 1 and automation Phase 2.
- *Why WhatsApp can't simply be automated.* The green/Business WhatsApp app can't be legitimately automated; libraries like `whatsapp-web.js`/Baileys violate the Terms of Service and get numbers banned. The legitimate route is the WhatsApp Business Cloud API, which needs pre-approved templates for messages outside 24 hours, costs money per message, needs a number and Meta business verification. Hence **`wa.me` deep links** as the free MVP bridge: the app decides who/when and drafts the text, staff tap send in WhatsApp itself (~90% of the value, ₹0, zero ban risk).
- *Why Android and not web.* The first recommendation was Flask + Supabase on a hosted server (a CRM is a "desk tool"). Two facts flipped it: it's **solo** (so team-skill arguments vanish) and **reusing the Kalaza Care skeleton** is a big accelerant; also the killer feature — "3 follow-ups due today" — is best as a phone notification, and `wa.me` is smoother on the phone. Acknowledged weaknesses of Android: typing long medical notes on a phone is unpleasant (which is partly why intake later moved to a Google Form), and reports look cramped (mitigated by CSV export).
- *Deployment correction.* An early message wrongly assumed Kalaza Care used Railway; Railway belonged to a different project. The web-hosting discussion (Render / PythonAnywhere) became moot once Android was chosen: only the thin serverless layer (Supabase Edge Functions) needs hosting.
- *Security principles fixed on day one:* the Claude API key must never be in the APK (all AI calls go through Edge Functions); no secrets in a shared Claude cloud environment's env-var box; scheduled jobs and the WhatsApp bot run server-side. Consent/sensitivity: the app stores medical history and budgets of elderly people — collect only what's needed, get consent before messaging, keep access staff-only.
- *Three places AI was meant to earn its place (still future):* drafting personalised messages, parsing a pasted raw WhatsApp enquiry into fields, summarising a lead's history — all via Edge Functions. See `docs/AUTOMATION_DESIGN.md` for the current, stricter role of AI (matching, translating approved answers, extracting fields — never inventing facts).

**Naming/packaging.** Repo `kalaza-leads`; Android package `com.kalazacare.leads` ("one product family, two apps"); teal theme deliberately distinct from Care's red so both apps can sit on one phone.

---

## 3. Timeline — how we got here

Dates are 2026. Commit hashes are anchors you can `git show`. (Sessions were long and several ran out of context and were continued; the running log `docs/PROGRESS.md` and these docs are what carried memory between them.)

**Pre-history.** By July, *Kalaza Care* (the original 5-person team app) was built and in production use at the NGO. Its supervisor is the same person who later asked for Kalaza Leads.

**Aug 20–21 — portfolio context (background only).** In a separate stretch of work Anhad's GitHub was cleaned up (assignment repos deleted; Kalaza Care published; a profile README added) and his resume rebuilt. Relevant here only because it established the **PAT push routine** (§11) and the rule that *Claude never types passwords* — Anhad logs into sites himself in the browser pane.

**Aug 22 — the idea and the repo.**
- The supervisor's scattered raw notes were turned into a concept document (decoded as "an enquiry-to-conversion follow-up CRM"; the requirements list is in §4). That document's reasoning is summarised in §2.
- Repo `Anhad23mahajan/kalaza-leads` created (public) with README + `docs/PROJECT_SPEC.md` (since deleted; commit `252cc4f`); Android scaffold forked from Kalaza Care pushed (`33ab0a1`).
- **Plan A (Claude Code cloud on a friend's Pro account) was started, then abandoned** the same afternoon: the repo picker only showed another person's repos and waiting for an invite "wastes time". Anhad's decision, 16:53: *"let's continue step by step here only… slow and steady wins the game. every step executed perfectly."* First run of the placeholder LoginScreen on his phone that evening.

**Aug 23 — Supabase and the long login saga.**
- A **standalone** Supabase project was created (Mumbai; the friend who holds Kalaza Care's credentials was unavailable, so sharing that project was dropped — reversible). `leads` table (18 columns) created with RLS + explicit grants.
- Getting login working took a very long day, with seven stacked problems: JDK 26 vs Gradle; Gradle can't run inside the agent; Windows build-folder locks; the `.internal` email TLD rejected; "Confirm email" blocking accounts; **Android Studio silently reinstalling a stale APK when the code didn't compile** (which made "the button does nothing" look like a runtime bug); and a Compose logout bug (§14). A temporary *mock* auth was used mid-way, and the assistant wrongly described it to Anhad as "real Supabase integration" — an overclaim he caught (§12). Login finally worked ~09:01; logout ~09:58.
- The project was copied out of OneDrive to **`C:\Dev\kalaza-leads`** (canonical build location from here on). First `docs/PROGRESS.md`. Add-Lead form and list screen added because Anhad needed something visible to demo to the NGO.

**Aug 25 — the supervisor review, the Master Plan, and Track A.**
- After a review meeting the supervisor **redefined the project** (§4): the WhatsApp auto-reply is the product; he gave 13 UI change requests and a list of desired lists/reports. A "Master Plan v2" (three systems; Tracks A–D) was written and committed. Realistic timeline: 4–6 months.
- That same long day: **A1** (v2 schema migration + form overhaul), **A2** (detail/edit), **A3** (contact log), **A4 part 1 + A5** (Follow-ups Due list, then 7 tabs), **A7** (`wa.me`), **A6** (CSV export), **A8** (staff + assignment), **A9** (reports) — each verified on the phone before being logged. Pushes ended around `84662f5`.
- The supervisor **declined to share his Excel** of past enquiries; the ask was dropped permanently.
- The context window hit ~90%; a paste-in prompt forced the new session to prove it had read the docs.
- **A4 part 2** (local WorkManager notification) was built but not committed until verified.

**Aug 27 — Track A complete; Track B research.**
- The notification fired the *next evening* rather than 9 am → Doze; accepted as best-effort. **Track A declared 100% complete** (`7613402`).
- Three docs written: a technical summary (for groupmates), a plain-language demo script for the supervisor, and a prep script for the Meta conversation.
- **Deep Track B research** found the Master Plan's "₹450/year" estimate incomplete: Meta's message fees are tiny, but keeping the existing number via "Coexistence" requires a Tech Provider/BSP (₹18–30k/year if paid). Anhad said the supervisor will not accept that, so a fallback ladder was researched, ending with a **brand-new dedicated number** as the cost-safe default. After a "brutal honest" analysis (a new number removes the BSP/Tech-Provider requirement but **not** the Meta Business verification, payment method, template approval or content work), the docs were deleted and rebuilt several times until the definitive `TRACK_B_PAPERWORK_PLAYBOOK.md` (`9abbc2e`).
- Then ~3 weeks with no Kalaza work (Anhad was on other projects).

**Sep 16 — recap, meeting prep, security fix.**
- Anhad had forgotten the details and needed a recap before a supervisor meeting expected Sep 17. Produced a one-page cheat sheet and a word-for-word meeting script (`574c6f2`), plus a 10-step order for demoing Track A on the phone. Anhad said he was ~90% sure the supervisor would pick the new-number plan (*an expectation, not a recorded outcome*).
- Anhad noticed he could **log in with any junk name and password**. Root cause: `login()` silently created an account for any unknown credentials, so anyone with the APK could read every family's data. **Fixed** by gating signup on the active-staff roster (`005_staff_name_check_rpc.sql`; built and verified on the phone Sep 16, committed Sep 17 as `1d30128`). While testing, a wrong-password error surfaced as a raw dump → replaced by a clear "Incorrect password" message; he deleted a stuck auth user in the Supabase dashboard to reset. Also fixed: logout button 75% visible (top bar overflow) → 4 compact icons + a logout confirmation dialog.

**Sep 17 — the Google Form pivot, built end to end.**
- Anhad's own flowchart of intake (form-first) was reviewed; the assistant pushed back three times (a cold form reply may scare anxious families; a form can't answer questions; phone/walk-in enquiries would go untracked if the in-app form is deleted). Anhad overruled each: the form is for *everyone*, the chatbot's job is answering, "problem 1 can be dealt later". A proposal to merge the form link into the existing Thank-You `wa.me` template was **withdrawn** by Anhad ("i didn't think of it properly") — the Thank-You stays a plain courtesy message; link delivery was parked.
- **"nothing… let's start the work… Make no mistakes."** The assistant built the entire 23-question Google Form live in the browser pane (Anhad logged in himself), verified it field by field, linked a response Sheet, and published it. The Apps Script bridge and the anon-insert migration (`006`) were written and Anhad ran `006` in Supabase. The Google popup/new-tab limitation meant **Anhad had to do the Apps Script steps in his own browser**, where he hit three mistakes (§9). After fixing them, a real test submission landed in the app with every field — including checkbox arrays — correct.
- Anhad ordered the **in-app "New Enquiry" screen deleted** ("we will see what to do with whatsapp part later"); done and verified on the phone. A second submission also landed.
- Docs cleanup with "make your own judgement calls": `PROJECT_SPEC.md` and the first Meta research doc deleted; README rewritten; stale sections fixed. Pushes: `ce89dff`, `ffe8913`, `3f9a2c1`.

**Sep 18 — roadmap.** Anhad asked for a detailed plan → `docs/ROADMAP.md`. He then said the Master Plan had "a lot too unnecessary things… it's all a little confusing" → it was cut from ~790 to 138 lines and renamed `AUTOMATION_DESIGN.md` (`0c8e172`). Docs went from 2,877 to ~1,718 lines.

**Sep 21 — this handoff.** Anhad may need to continue on a friend's machine and Claude account with no context; the repo was audited for completeness and this guide and a root `CLAUDE.md` were written.

---

---

## 4. What the supervisor asked for (requirements record)

Preserved from the original planning documents so the *reasons* behind features survive. Quotes are the supervisor's own words as relayed by Anhad.

### 4.1 The dominant theme: automatic replies
He came back to this six-plus times. It is, in his mind, *the product*:
- *"we first need to understand the most basic requirement of the whole application: the auto-reply should get back to them"*
- *"basically the entire point of making our application is that whatever we are providing... everything should go as automatic replies."*
- *"when she replies something to our automated message, then we should again reply her back through our automated message"* → he wants **two-way conversation**, not a one-shot canned reply.
- *"the type of questions they have, then the type of answers/auto-replies they should get"*
- *"how would our application come to know that our enquiry has been done on whatsapp?"* — the right technical question; the honest answer is in §15 (an app can never read WhatsApp; Meta's official Business Platform can forward messages to our server).
- *"meta api we have right... that takes money or what?"* — cost worry (answer: small; see Track B docs).

### 4.2 Service-specific answer packs
If they ask about **palliative care** the reply should be a dedicated section — e.g. *"we have a full flat needed for a palliative care patient... we have this machine, we have this Oxygen Cylinder... we also have a backup if any emergency occurs."* Answers must cover room types (full flat / single / private / sharing), budget responses, facilities, equipment, emergency backup.

### 4.3 Content to send
*"whatever details u have about the centre, just send that to me — social media links, instagram pages, google... or website, or a video"*; posters/videos per service; price-list sharing is tracked as its own Excel column.

### 4.4 The follow-up engine
- Follow-up timing should **honour what the person said**: *"if they say that 'i will tell u in 2-3 days', then there should a message that should go to them automatically after 2-3 days"*.
- Follow-up wording: *"are u free now? what have u decided? what's your plan? can i call u after 2 pm?"*
- Staff-side reminders too: *"after 2 days, automatically our application should have notification that we are supposed to reply to her"*.
- Post-visit: after 2–3 days an automatic *"you visited, how did you like it? what's your feedback?"*.
- A sequenced set: thank-you first, then a formatted follow-up, then reactive replies.

### 4.5 Separate lists he explicitly asked for
Converting / not converting / "just enquire and do nothing else" / a backup list / a list of **what they didn't like** (*"after 2-3 months we will come to know exactly what they want"*) / a list of **positive feedback** (*"the staff member was polite, hygiene was good"* → *"what our advantages are and what are disadvantages"*). → These became the 7 tabs (§8).

### 4.6 Analytics he asked for
How many enquiries and how many converted; **time-to-convert** (*"convert basically means when they start to live in the facility after the talks"*); **why they didn't convert** (budget / want AC / want lift / service we don't offer); *"a list that because of this this reasons, they didn't choose Kalaza Care"*; **unmet demand** (*"if what they want isn't there, then they want that in the facility"*). → The Reports screen (§8), especially the "Unmet demand" section.

### 4.7 Volume (matters for justification)
*"sometimes we get 2-3 enquiries a day"*; *"6 months we get around 100 calls approx"*; *"a potential customer will have max to max 20 questions, 1-2 up or down"*. Most plausible reading: roughly 200–400 enquiries a year, 2–3 on busy days. **Low volume.** So the case for automation is *speed, consistency, never forgetting* — not "drowning in messages". Corollary Anhad recorded: at this volume the **follow-up discipline (the CRM) probably wins more admissions than the bot**, though the bot is what the supervisor asked for.

### 4.8 His conversion theory (correct, worth quoting back)
> *"if someone comes to us, makes an enquiry, but we don't properly reply back, then their converting chances become less... so to convert them we need the follow-up, we need to call them, the replies need to be good, whatever enquiries they have we need to send them proper links, posters... because of social media, they get to know about our NGO, they read our google reviews... so whatever data we send to them, their converting chances increase."*

### 4.9 The 13 concrete UI change requests after the first demo
1. Phone: exactly 10 digits, plus a **country-code** selector. 2. "How did they reach out": add **Google Search, Call, Hospital** (*"call happens after google search"*) → split into `how_heard` vs `contact_channel`. 3. Service types: add **transplant, cancer, medical recovery** beside assisted living/palliative (4–5+ options). 4. **Location** ("where are you from"). 5. Fix janky **scrolling** of the long form. 6. **Edit after saving.** 7. **Excel file saved and shared** (built as CSV export). 8. **Relation to patient** as a dropdown. 9. **Room type** (single/double/sharing…). 10. **Medical history** field. 11. **Visit date** ("when are you gonna visit"). 12. **What the patient actually has** (Alzheimer's, cancer, … as a structured checklist). 13. **Follow-up dates.** All 13 were addressed except two that were only partly covered in the first pass; all were finished by the end of Track A.

### 4.10 His existing Excel (reference, not a mandate)
Columns as he listed them: enquiry date · enquirer · required for? · location · age of patient · contacted through / referred through · primary contact · his/her number · type of accommodation (single/double/sharing) · price list shared (Y/N) · date of visit in future · **"to confirm"** (meaning never clarified) · queries · comments · current condition · follow-up person name · follow-up 1 date · follow-up 2 date · enquiry date (listed twice) · **Final Remarks: why didn't they convert?** He said *"now i don't want to fill this exact. u can do your own thoughts."* The schema in §7 is the result. **He declined to share the actual Excel file** (his official records; data-protection concerns) — dropped as a blocker, don't chase.

---

## 5. Architecture

```
                         FAMILY / ENQUIRER
              ┌──────────────┬──────────────────────┐
     phone / walk-in / WhatsApp        (future) WhatsApp bot
              │                                    │
              ▼                                    ▼
   Google Form (23 questions)        Meta WhatsApp Cloud API   ← NOT BUILT (Track B/D)
              │                                    │ webhook
              ▼                                    ▼
   Google Sheet (responses)          Supabase Edge Functions   ← NOT BUILT (Track D)
              │  onFormSubmit trigger              │
              ▼                                    │
   Apps Script bridge  ──HTTPS POST (anon key)──►  │
              │                                    │
              ▼                                    ▼
        ┌──────────────────────────────────────────────┐
        │  SUPABASE  (Postgres + Auth + RLS)           │
        │  tables: leads, contact_activities, staff    │
        │  fn: is_active_staff_name(text)              │
        └──────────────────────────────────────────────┘
                              ▲
                              │  supabase-kt (Auth + Postgrest), signed-in staff only
                    ┌────────────────────┐
                    │  ANDROID APP       │  Kotlin + Compose, MVVM
                    │  (Kalaza Leads)    │  staff read/edit the same tables
                    └────────────────────┘
```

**The single most important design rule:** the Android app and the (future) WhatsApp bot **never talk to each other**. Both talk to the same database. That makes them feel like one product while being buildable and blockable independently.

**App pattern:** `Repository` (talks to Supabase) → `ViewModel` (exposes a `StateFlow` of a state data class) → Composable `Screen` (collects state, renders, calls ViewModel). `MainActivity.onCreate` constructs every repository and ViewModel once and passes them down. **No DI framework and no `NavHost`** — a private `enum class Screen { LOGIN, LEADS, LEAD_DETAIL, STAFF, REPORTS }` held in `mutableStateOf` and a `when` choose what to render. Deliberately simple for a solo project at this size.

**Two separate apps exist for the same NGO** (don't confuse them): *Kalaza Care* (the older team project: residents, medication, shifts — in production use; separate repo `Anhad23mahajan/kalaza-care`) and *Kalaza Leads* (this one: enquiries **before** admission). They share a stack but not code or database. The Leads app uses its own **standalone** Supabase project (see §13), and a different theme colour (teal vs Care's red) so staff can tell them apart on one phone.

---

## 6. Repository map

Canonical remote: `https://github.com/Anhad23mahajan/kalaza-leads` (public, branch `main`). Package `com.kalazacare.leads`.

```
README.md                         project overview + reading pointers
CLAUDE.md                         auto-loaded rules for Claude Code (points here)
build.gradle.kts / settings.gradle.kts / gradle.properties
gradle/libs.versions.toml         all dependency versions (single source)
gradle/wrapper/*, gradlew(.bat)   Gradle 8.11.1 wrapper (jar is tracked)
local.properties.example          template — copy to local.properties (gitignored)
app/build.gradle.kts              reads local.properties → BuildConfig.SUPABASE_URL / _ANON_KEY
app/src/main/AndroidManifest.xml  INTERNET, POST_NOTIFICATIONS, FileProvider
app/src/main/res/                 launcher icons, strings, colors, themes, xml/file_paths.xml
docs/                             all documentation (see §0)
docs/sql/001…006                  database migrations, run in order
tools/google-form-bridge/KalazaFormBridge.gs   Apps Script source (pasted into Google, not built)
```

**Kotlin sources** (`app/src/main/java/com/kalazacare/leads/`):

| Path | Responsibility |
|---|---|
| `KalazaLeadsApp.kt` | Application class: touches the Supabase client at startup (so bad config fails loudly) and schedules the daily reminder worker |
| `data/remote/SupabaseClients.kt` | Singleton `SupabaseClient` (installs Auth, Postgrest, Realtime — Realtime is unused) built from `BuildConfig` |
| `data/model/Lead.kt` | `Lead` (read model) and `UpdateLeadRequest` (edit payload) — **the current column list is authoritative here** |
| `data/model/ContactActivity.kt`, `StaffMember.kt` | Models for `contact_activities` and `staff` |
| `data/repository/*Repository.kt` + `Supabase*Repository.kt` | Interface + Supabase implementation for Auth, Leads, ContactActivities, Staff |
| `notifications/` | `FollowUpReminderWorker` (WorkManager job), `NotificationHelper` (builds/shows the notification, checks permission), `NotificationScheduler` (24 h periodic, targets ~9 am) |
| `ui/MainActivity.kt` | Composition root + screen enum + notification-permission request |
| `ui/login/` | `LoginScreen`, `LoginViewModel` |
| `ui/leads/LeadsScreen.kt` | The home screen: 7 segmented tabs with live counts, lead cards, top-bar actions (share/export, staff, reports, logout with confirmation) |
| `ui/leads/LeadsViewModel.kt` | Loads leads, holds selection, `updateLead` |
| `ui/leads/LeadDetailScreen.kt` | Full detail/edit form + status pipeline + assignment dropdown + contact log + WhatsApp drafts |
| `ui/leads/ContactLogSection.kt`, `ActivitiesViewModel.kt` | The contact-activity timeline and logging dialog |
| `ui/leads/FormComponents.kt` | Reusable inputs: `EnumDropdown`, `MultiSelectChips` (wrapping), `DateField` (Material3 date picker, ISO `yyyy-MM-dd`) |
| `ui/leads/LeadFormOptions.kt` | **Shared enum lists + display labels** (must stay in sync with the SQL check constraints and with the Apps Script maps) |
| `ui/leads/LeadExport.kt` | CSV export of the currently open tab + share sheet via `FileProvider` |
| `ui/leads/ReportsAnalytics.kt`, `ReportsScreen.kt` | Pure analytics functions and the Reports UI |
| `ui/leads/StaffScreen.kt`, `StaffViewModel.kt` | Staff roster CRUD, active/inactive toggle |
| `ui/leads/WhatsAppHelper.kt` | The three static message templates and the `wa.me` deep link builder |
| `ui/theme/` | Teal Material 3 theme (`Color/Shape/Theme/Type`) |

**Note on file names:** `LeadFormOptions.kt` and `FormComponents.kt` still carry "form" names from when there was an in-app Add Enquiry form; they are still used by the detail/edit screen.

---

## 7. Database: schema, security model, rebuilding from scratch

### 7.1 The live project
- **Supabase project ref `niqhlkdyaklnngcanxld`** → API URL `https://niqhlkdyaklnngcanxld.supabase.co` (⚠️ *not* the `supabase.com/dashboard/...` URL — someone pasted that once by mistake). Region: South Asia (Mumbai, `ap-south-1`). **Free plan.** Project is labelled `main` / PRODUCTION. Owner: Anhad's personal account (org "Anhad23mahajan's Org").
- Keys live on the dashboard at **Project Settings → API Keys**. The **anon / publishable key** is what the app and the Apps Script use. **The service-role / secret key must never be used in the app, the script, the repo, or any chat.**
- **Auth settings that matter:** "Confirm email" must be **OFF** (staff log in with synthesized emails that have no inbox); password minimum is Supabase's default of 6 characters.
- **"Automatically expose new tables" is OFF** for this project. Consequence: **RLS policies alone are not enough** — every table also needs an explicit `GRANT` to the role, or PostgREST answers `permission denied for table …` even when a policy would pass. Every migration includes its grants.
- ⚠️ **Free-tier caveat, verified by live web search 2026-09-22 (re-check the dashboard, as these terms can change):** a Free-plan project pauses after **7 days with no database activity** (dashboard visits and cached API reads don't count — it has to be an actual query reaching Postgres). A paused project's data is intact and can be restored from the dashboard for up to **1 year** before deletion. The Free plan has **zero days of backup retention** — there is no snapshot system running at all, unlike Pro/Team. **Paid plans cannot be paused.** Decide before real data goes in (see §16): either accept the pause risk with a periodic manual export, or upgrade.

### 7.2 Tables
**`leads`** — one row per enquiry (v2 schema, `002_leads_v2_migration.sql`). Column groups:
- *Identity/intake:* `id`, `created_at`, `updated_at` (kept current by a trigger), `enquiry_date`, `contact_channel` (phone_call | whatsapp | walk_in | website | email | instagram_dm), `how_heard` (google_search | google_maps | instagram | facebook | referral_friend_family | referral_hospital | referral_doctor | passing_by | newspaper | other), `how_heard_detail`. *`how_heard` and `contact_channel` are deliberately two fields — the supervisor's insight: someone finds you on Google, then phones; conflating them destroys attribution analytics.*
- *Enquirer:* `enquirer_name`, `enquirer_country_code` (default +91), `enquirer_phone` (10 digits), `enquirer_relation` (son … other), `enquirer_location`.
- *Patient:* `patient_name`, `patient_age`, `patient_gender`, `patient_conditions text[]` (alzheimers, dementia, parkinsons, cancer, post_stroke, post_operative, post_transplant, bedridden, diabetes, cardiac, mobility_impaired, other), `patient_condition_notes`, `current_condition`, `medical_history`.
- *Requirement:* `service_wanted text[]` (assisted_living, palliative_care, post_transplant_care, cancer_care, medical_recovery, dementia_care, respite_care, day_care), `accommodation_type` (single_room, double_sharing, triple_sharing, full_flat, dormitory, not_sure), `budget_min`, `budget_max`, `budget_notes`, `amenities_requested text[]` (ac, lift, attached_bathroom, ground_floor, female_attendant, private_nurse, veg_food, other), `special_requirements`, `queries`, `comments`.
- *Pipeline:* `status` (**NEW, CONTACTED, INFO_SENT, VISIT_SCHEDULED, VISITED, CONSIDERING, CONVERTED, NOT_CONVERTED, DORMANT, BACKUP**), `assigned_staff_id` (FK → `staff.id`), `next_follow_up_date`, `follow_up_count`, `price_list_shared(+_at)`, `info_packs_sent text[]`, `planned_visit_date`, `actual_visit_date`, `converted_at`, `days_to_convert` (**generated column** = `converted_at − enquiry_date`).
- *Outcome:* `not_converted_reason` (budget_too_high, chose_another_facility, location_too_far, amenity_missing, service_not_offered, family_decided_home_care, patient_passed_away, decision_postponed, unreachable_no_response, unhappy_after_visit, other), `not_converted_detail`, `feedback_positive_themes text[]`, `feedback_negative_themes text[]`, `final_remarks`.
- *Compliance:* `consent_given`, `opted_out`, `preferred_language` (en | hi | mr). (Present in the table; the app does not expose them yet.)

**`contact_activities`** — the "proof" log: `lead_id` (FK, cascade delete), `occurred_at`, `type` (call | whatsapp | visit | email | sms), `direction` (inbound | outbound), `outcome` (positive | negative | no_answer | callback_requested | not_reachable), `callback_on`, `notes`, `staff_id`, `is_automated`.

**`staff`** — roster: `name`, `phone`, `role` (admin | coordinator | viewer — **informational only, not enforced anywhere**), `is_active`. Deliberately *not* tied 1:1 to `auth.users`; a staff member may not have an app login.

### 7.3 Security model (read carefully — it has a trap)
1. **RLS on every table.** Policies for `authenticated` say only `auth.uid() is not null` — i.e. **any logged-in account is trusted staff** (accepted MVP simplification).
2. **Signup is gated** (fixed 2026-09-17 after a real hole was found — previously *anyone with the APK* could create an account and read every family's medical/financial data). `SupabaseAuthRepository.login()`: tries sign-in first; if that fails it calls the `is_active_staff_name(check_name)` RPC (SECURITY DEFINER, returns only a boolean, callable without a session, case/whitespace-insensitive) and only if the name matches an **active** `staff` row does it attempt sign-up; a "user already exists" error is then reported as "Incorrect password". Otherwise: *"Name not recognized as active staff … Ask your admin to add you under Staff first."*
3. **Anonymous insert exception** (`006`): the `anon` role may **INSERT** into `leads` (`with check (true)`) so the Google Form bridge can write without a login. `anon` still cannot select/update/delete. Accepted risk: anyone holding the anon key (it's inside every APK) can insert junk rows — not read/edit/delete. Mitigations if abuse ever appears: shared-secret field checked in the bridge, or move the insert behind an Edge Function with the service key.
4. **Login mechanics:** staff type a **name** + password; the app synthesizes the email `{name lowercased, spaces→underscores}@kalazaleads.app` (must be a real-looking TLD — `.internal`/`.local`/`.test` are rejected by Supabase's validator with `email_address_invalid`; `.app` works).
5. **Known weakness - unclaimed staff names.** The signup gate is *knowing an active staff name*, with no secret. If a staff row exists whose person has not logged in yet, anyone holding the APK who types that name plus any password creates that account first. Mitigation today: each staff member logs in once right after being added. Possible hardening: an admin-issued invite code or an approval step (section 16).

### 7.4 Rebuilding the database from nothing
Do this if the Supabase project is ever lost or you want a fresh copy.
1. Create a Supabase project. In **Authentication → Sign In / Providers**, turn **Confirm email OFF**. In project settings turn **"Automatically expose new tables" OFF** (or simply keep the grants in the SQL).
2. In the SQL Editor run, **in this order**: `001_leads_table.sql` → `002_leads_v2_migration.sql` (drops and recreates `leads`, so it is safe only on an empty/throwaway table) → `003_contact_activities.sql` → `004_staff_table.sql` → `005_staff_name_check_rpc.sql` → `006_google_form_anon_insert.sql`.
3. **Bootstrap the first staff member — mandatory.** On a fresh database `staff` is empty, so the signup gate rejects *everyone*, including you. Run once in the SQL editor:
   ```sql
   insert into public.staff (name, role, is_active) values ('YourName', 'admin', true);
   ```
   Then open the app, log in with that exact name and a new password (≥ 6 chars); the account is created on first login. Afterwards add further staff from the app's Staff screen.
4. Copy the Project URL and anon key into `local.properties` (§10) and, for the Form bridge, into the Apps Script's Script Properties (§9).

---

## 8. The Android app: features and how they work

All verified end-to-end on a real Android phone. Deep build notes: `docs/TRACK_A_TECHNICAL_SUMMARY.md`.

- **Login.** Name + password (§7.3). Logout has a confirmation dialog. `LoginViewModel.logout()` resets its state synchronously — see the Compose gotcha in §14.
- **Leads screen (A5).** Seven scrollable tabs, each with a live count, all client-side filters over the already-fetched list: **Follow-ups Due** (follow-up date ≤ today and status not terminal, sorted by date), **All**, **Active** (NEW…CONSIDERING), **Converted**, **Not Converted** (cards show the reason), **Dormant**, **Backup**. Terminal statuses = CONVERTED, NOT_CONVERTED, DORMANT.
- **Lead detail / edit (A2).** Every field editable, status with conditional not-converted reason/detail, planned vs actual visit date, final remarks, an **Assigned to** dropdown (active staff only). Saves via `UpdateLeadRequest`.
- **Contact log (A3).** Log a call/WhatsApp/visit/email/SMS with direction, outcome, optional callback date and notes; timeline shown newest first. This is the supervisor's "proof it happened" feature.
- **Follow-up notifications (A4).** Local, not server push: WorkManager runs every 24 h (first run targets ~9 am), queries leads whose follow-up is due, filters terminal statuses, and shows one device notification if any exist; tapping opens the app. It awaits Supabase auth initialisation and no-ops if nobody is logged in. Needs the Android 13+ `POST_NOTIFICATIONS` permission (requested at launch). **Known, accepted limitation:** Doze can delay it by hours (observed: fired same-day evening). Exact alarms were rejected as not worth the complexity. True server push needs Firebase Cloud Messaging and a backend (Track D).
- **`wa.me` messaging (A7).** Three static, personalised drafts (Thank You, Follow-up, Visit Feedback) opened in WhatsApp via a deep link; staff review and press send. Zero cost, zero Meta dependency, no ban risk. Not AI-generated.
- **CSV export (A6).** Share icon exports whichever tab is open (respecting its filter), with human-readable labels, via the Android share sheet. **CSV, not .xlsx, on purpose** — Apache POI has known Android problems and would bloat the APK; CSV opens in Excel/Sheets/WhatsApp.
- **Staff (A8).** Add/edit roster entries, toggle active/inactive; drives assignment and the signup gate.
- **Reports (A9).** All computed client-side (`ReportsAnalytics.kt`, pure functions): overview (total, converted, conversion rate, median days-to-convert), pipeline funnel, breakdowns by source / service (a lead counts toward every service it listed) / assigned staff, not-converted reasons ranked, **Unmet demand** (free-text detail for `amenity_missing`/`service_not_offered` — the "why we lose families" report), budget distribution. Bars are plain `Box` fractions, no chart library.
- **Intentionally absent:** any in-app way to *create* a lead. New leads arrive only through the Google Form (§9). Deleted 2026-09-17 with `AddLeadScreen.kt` and the `addLead`/`NewLeadRequest` plumbing.

---

## 9. Intake pipeline: Google Form → Apps Script → Supabase

**Why:** the enquirer fills in their own details (regardless of channel — WhatsApp, phone call or walk-in), so staff don't re-type everything on a phone. Everything lands in the same `leads` table; the rest of the app is unchanged.

**Pieces (all under Anhad's personal Google account):**
- **The Form** — 23 questions, exact titles/types/options in `docs/GOOGLE_FORM_INTAKE_SPEC.md` §1. Responder link: `https://docs.google.com/forms/d/e/1FAIpQLSfH3qQxdCvq4Iw0-kS6tuef2hx4Y-q8bT7Jef_y99A9yhSp0A/viewform`. Edit link: `https://docs.google.com/forms/d/1C6eQ1uoamNoquSEXbMa-SMoif6ThfYmHjzQuvJ2b0So/edit` (only openable by the owner). Published.
- **The response Sheet** — created from the Form's Responses tab. Contains **real family data once in use** → ownership/sharing matter (§16).
- **The Apps Script** — *container-bound* to that Sheet (Sheet → Extensions → Apps Script), source in `tools/google-form-bridge/KalazaFormBridge.gs`. Reads two **Script Properties**: `SUPABASE_URL`, `SUPABASE_ANON_KEY`.
- **The trigger** — an *installable* `onFormSubmit` trigger for `handleFormSubmit`, created by running `setupTrigger()` once from the editor (it removes old `handleFormSubmit` triggers first, so re-running is safe).

**How the script works:** `handleFormSubmit(e)` reads `e.namedValues` keyed by the **literal question title**, so **the title text is the contract** — rename a question and that field silently stops mapping. Dropdown/checkbox answers are normalised (lowercase, punctuation stripped, so curly vs straight apostrophes don't matter) and looked up in maps mirroring `LeadFormOptions.kt`; multi-select answers arrive comma-joined and are split into arrays; age/budget parsed to numbers; visit date reformatted to `yyyy-MM-dd`; country code defaults to `+91`. If name or phone is blank the row is skipped (logged). It POSTs to `<SUPABASE_URL>/rest/v1/leads` with the anon key (`Prefer: return=minimal`). Failures are only logged to Apps Script **Executions** — the Sheet row still exists, so nothing is lost, but there is no alert (§16).
**Deliberately not on the form:** staff-only pipeline fields (`next_follow_up_date`, `actual_visit_date`, `status`, `assigned_staff_id`, `final_remarks`, …) — an enquirer can't know them. Possible later additions: a consent checkbox and preferred-language dropdown (`GOOGLE_FORM_INTAKE_SPEC.md` §6).

**Setup / re-setup checklist (what actually went wrong the first time):**
1. Paste the **contents** of `KalazaFormBridge.gs` into the editor (once someone pasted the *file path* as text instead of the file).
2. Project Settings → Script Properties: `SUPABASE_URL` must be `https://<ref>.supabase.co`, **not** the dashboard URL; `SUPABASE_ANON_KEY` from Supabase → Project Settings → API Keys. Save.
3. In the editor's function dropdown choose **`setupTrigger`** (not `normalize`, which is what the dropdown may still show), Run, and approve the permissions ("Google hasn't verified this app" → Advanced → Go to project → Allow). If you run the wrong function nothing breaks but **no trigger exists** and form submissions do nothing — check the **Triggers** page for a row "From spreadsheet – On form submit".
4. Submit a test response ("TEST …"), then check **Executions** for a `handleFormSubmit` row of Type *Trigger*, Status *Completed*, and check the app's Leads → All tab (pull to refresh). Verify the checkbox fields arrived as clean lists. **No app rebuild is needed** — it's only data.
5. Delete the test rows from Supabase afterwards.

**Recreating the Form:** `GOOGLE_FORM_INTAKE_SPEC.md` §1–§2. When the Form was built (via browser automation with Anhad's own login) the following were learned — see §14: renders lag clicks; question-type dropdowns need a hover click then a select click; "Add question" inserts after the *focused* question; Google auto-detects some types from titles; response-validation widgets are custom, not native selects. (Order of questions doesn't matter to the bridge — titles do.)

**Delivering the form link to enquirers** was deliberately left open. The cheapest idea (recorded in `ROADMAP.md`) is the WhatsApp Business *app's* free features: put the link in the greeting message and a `/form` quick reply, and print a QR code for the front desk.

---

## 10. Building, running, and the environment

### 10.1 What you need (any machine)
- **Android Studio** recent enough for **AGP 8.9.1** (verify the version when installing), with the Android SDK. The build targets `compileSdk 36`, `targetSdk 35`, `minSdk 26`, Java/Kotlin JVM target 17.
- **A JDK 17 or newer to run Gradle** — Android Studio's bundled JBR (JDK 21) is the safe choice. **Do not use a JDK that's too new**: Gradle 8.11.1 fails instantly on JDK 26 with a cryptic one-line message ("26.0.1").
- **Pinned versions** (see `gradle/libs.versions.toml`): Kotlin 2.3.20, Compose BOM 2024.12.01, Material 3 + extended icons, supabase-kt BOM 3.5.0 (Auth, Postgrest, Realtime), Ktor 3.0.3, kotlinx-serialization 1.7.3, WorkManager 2.10.0, Lifecycle 2.8.7, Navigation-Compose 2.8.5 (declared but unused).
- **A Supabase project** and its URL + anon key (§7). Without them the app compiles but can't sign in.
- **An Android phone** with USB debugging (or an emulator) to test.

### 10.2 First-time setup
```
git clone https://github.com/Anhad23mahajan/kalaza-leads.git
cd kalaza-leads
cp local.properties.example local.properties        # Windows: copy
```
Edit `local.properties` (gitignored — **never commit it**):
```
sdk.dir=<path to your Android SDK>          # Android Studio usually writes this
SUPABASE_URL=https://<project-ref>.supabase.co
SUPABASE_ANON_KEY=<the anon / publishable key>
```
`app/build.gradle.kts` reads these into `BuildConfig.SUPABASE_URL` / `BuildConfig.SUPABASE_ANON_KEY`. Until they're filled in they're empty strings and Supabase calls fail loudly at first use (deliberate).

### 10.3 Build and install (Windows/PowerShell, as used on Anhad's machine)
```powershell
cd C:\Dev\kalaza-leads
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew.bat assembleDebug --no-daemon
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" install -r app\build\outputs\apk\debug\app-debug.apk
```
On macOS/Linux use `./gradlew assembleDebug` and `adb install -r app/build/outputs/apk/debug/app-debug.apk`. The APK lands at `app/build/outputs/apk/debug/app-debug.apk`. It is a **debug** build wired to the *live* Supabase project — anyone who installs it and is on the staff roster sees real data (there is no release signing config and no Play Store path).

**Wait for the build to finish before installing** — installing early installs the previous APK (this caused a false "my change isn't there" once).

### 10.4 Anhad-machine specifics vs. general rules
These bit hard on his laptop (NBCC-managed Windows 11, OneDrive-synced Desktop). Some are machine-specific; check before assuming they apply to yours.
1. **`JAVA_HOME` defaults to JDK 26** there → set it to the Android Studio JBR in every fresh terminal (it doesn't persist).
2. **Gradle can't run inside Claude's own Bash/PowerShell tool** there — `Unable to establish loopback connection` / `SocketException: Invalid argument: connect`. Cause: a JDK NIO/AF_UNIX problem when the JVM is a subprocess of an agent layer. Nothing fixed it (IPv4 flags, selector provider, other JDKs, disabling a VPN). **Every build in this project's history was the human running `gradlew` and pasting the output.** *On a new machine, try `gradlew` once from the agent's shell; if it fails the same way, don't burn time — ask the human to run it.*
3. **`Unable to delete directory …\app\build\…`** happens constantly there. It's a transient lock (Windows Search Indexer is the suspect; proven not to be OneDrive — a manual delete of the same folder succeeds seconds later). Working fix: a delete-wait-retry loop, run in the human's own terminal:
   ```powershell
   for ($i = 1; $i -le 5; $i++) {
       if (Test-Path "app\build") { Remove-Item -Recurse -Force "app\build" -ErrorAction SilentlyContinue }
       Start-Sleep -Seconds 2
       .\gradlew.bat assembleDebug --no-daemon
       if ($LASTEXITCODE -eq 0) { break }
   }
   ```
   `clean` itself triggers this error, so skip it and delete `app\build` manually instead.
4. **The canonical folder is `C:\Dev\kalaza-leads`**, not the OneDrive Desktop copy. That old copy still exists, is stale, and has **nothing unique** (verified: every commit in it is already on GitHub). Don't edit it.
5. **PowerShell opens in the home directory** — `.\gradlew.bat : not recognized` means you forgot to `cd` to the project.
6. **`adb` isn't on PATH** — use the full path shown above.
7. **`adb install` → "device unauthorized"** → the phone hasn't trusted the computer: unlock it and tap **Allow** on the USB-debugging prompt (tick "Always allow"). If two `adb` servers fight ("no devices"), `adb kill-server` and retry.
8. **Android Studio first sync "Invalid argument: connect"** → try Settings → HTTP Proxy → **No proxy**, restart Studio; if it persists delete `~/.gradle` and retry (which of the two fixed it was never pinned down).

### 10.5 Verify a build actually reached the phone
**Android Studio silently keeps running the old APK when the current code fails to compile.** On Aug 23 this produced a very long false hunt ("the button does nothing"). Before debugging behaviour after a code change:
```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" shell dumpsys package com.kalazacare.leads | Select-String lastUpdateTime
```
Compare to the current time. If it's old, the code never got there — go find the compile error. Also: `37 actionable tasks: 37 up-to-date` after an edit means Gradle skipped recompiling. Custom `Log.d` tags were unreliable on that phone; `dumpsys … lastUpdateTime` and `adb shell uiautomator dump` (grep `text="…"` in the pulled XML) are what actually worked.

### 10.6 What does **not** need a rebuild
Anything that only changes data or the pipeline: SQL migrations, Supabase settings, the Google Form, the Apps Script. The Form → Supabase → app path was tested end to end without touching the APK.

### 10.7 Known warnings
`Icons.Filled.ArrowBack` is deprecated at `ReportsScreen.kt` and `StaffScreen.kt` (should be `Icons.AutoMirrored.Filled.ArrowBack`). `libandroidx.graphics.path.so cannot be stripped` is harmless. CRLF/LF warnings from git on Windows are harmless.

---

## 11. Git and GitHub workflow

- **Repo:** `github.com/Anhad23mahajan/kalaza-leads`, public, branch `main` only, no PRs/branches used. Commits are made as `Anhad Mahajan` (his personal Gmail; read it from history with `git log -1 --format='%an <%ae>'`). On a machine that isn't his, set the identity **repo-locally** (`git config user.name ...`, `git config user.email ...`, *not* `--global`). The developer's commits historically appear under two author names (`AnhadMahajan3` and `Anhad23mahajan` — the same person); ignore.
- **No standing credentials anywhere.** Every push uses a **short-lived, single-repo, fine-grained PAT** the human creates on demand:
  1. Human opens `github.com/settings/personal-access-tokens/new`, scopes it to *only* `kalaza-leads`, permission **Contents: Read and write**, shortest expiration, and pastes the token into chat.
  2. The assistant runs one push with the token in the remote URL (`https://x-access-token:<TOKEN>@github.com/Anhad23mahajan/kalaza-leads.git`), then **immediately** resets the remote to plain `https://github.com/Anhad23mahajan/kalaza-leads.git` and confirms with `git remote -v`.
  3. The assistant tells the human to **revoke the token** at `github.com/settings/personal-access-tokens`.
  Each new push needs a new token — that friction is accepted. **Never write a token into a file, commit, or doc.** (Roughly two dozen tokens have been pasted into chat over the project's life; whether every one was revoked was never confirmed — see §16/§17.)
- **The routine per feature:** build (human runs Gradle) → human installs and tests on the phone → assistant updates `docs/PROGRESS.md` (and `ROADMAP.md` if status changed) → commit **code and docs as separate commits** where practical → push → revoke token. Nothing is marked "done" or pushed as done until it has been seen working on a real device.
- **Commit messages** explain *why*, and end with the required co-author trailer for the assistant in use.
- **Before any destructive git command** (`reset --hard`, `clean`, `checkout .`, force-push): run `git status` first and stash/commit anything present. Never force-push `main`. When a hook fails, fix it and make a **new** commit; don't amend.
- **Staging:** add files by name. `local.properties` is gitignored (`git add -A -- ':!local.properties'` prints a harmless warning and exits 1 but still stages everything else — check `git status`).
- **Windows long paths:** if `git add` says "Filename too long", `git config core.longpaths true`.
- **Surprises are not yours to overwrite.** On Sep 17 the assistant found uncommitted changes it hadn't made (the staff-roster security fix, done in another session) and *stopped to ask* three questions (different session? tested on device? SQL already run?) before committing them as their own commit. Do the same: investigate unexpected diffs; ask; don't discard.
- **Two local clones existed** (OneDrive copy and `C:\Dev`); both point at the same remote. Build only from `C:\Dev\kalaza-leads`.

---

## 12. Working with Anhad: style, preferences, standing rules

*Learned across the whole project. Following these is the difference between a smooth session and a frustrating one.*

**How he communicates.** Terse, casual, sometimes profane when something is genuinely broken ("writing bullshit name and password and I can login"). Commands like *"continue"*, *"YES"*, *"make no mistakes"*, *"make your own judgement calls"*, *"brutal, honest, well-researched"*. He pastes screenshots with "see" or "I got stuck on Step-2". He answers "what now?" after each success. He is a capable student, not a professional engineer: he follows precise steps well and gets stuck on unfamiliar UI navigation.

**What he wants from you**
1. **Exact next steps, not menus.** Give a recommendation, then **one** clear question. *"Tell me instructions of exactly the next steps"* is a literal request. Give direct URLs (e.g. the Supabase `…/sql/new` link, the PAT page) rather than "find the sidebar item" — he once couldn't find the SQL editor. Describe icons by position and function ("the 5th icon, a list with an arrow"). Include the `cd` and `JAVA_HOME` lines in every command block.
2. **You drive what you can.** He expects the assistant to do browser work itself (as it did for GitHub and the Google Form) and to hand off cleanly when a tool limit blocks it ("you must do this part in your own browser, because…").
3. **Honesty over reassurance.** He values pushback and being told trade-offs; he also rewards admitting a mistake. He *overrules* advice sometimes (he did on the Google Form design) — voice the concern once, clearly, then follow his decision.
4. **Nothing is "done" until seen working on his phone.** Don't claim success from a compile. Don't summarise features as working that he can't see (the Aug 23 mock-auth overclaim — *"when I log in I see only 'Leads / Follow-ups due will show here / Logout'"* — was the low point).
5. **Don't descope or declare completeness early.** From a related project, recorded as a standing preference: check the *whole* scope before saying "complete"; a failed first attempt is not proof something is impossible; an audit must cover everything and say what it didn't cover; when he says "fix everything", do it and report honestly afterwards.
6. **Keep the docs true.** He asked several times whether GitHub is updated "at every small step". Update `PROGRESS.md` as work lands; keep this guide and `ROADMAP.md` current.
7. **Plain language for non-technical audiences.** The supervisor gets jargon-free scripts with "⭐ ask his opinion" prompts. (Family-facing explanations were deliberately kept out of the public repo.)
8. **Delegation.** When he says "whatever you believe is best" he means it; decide, explain briefly, proceed. But he wants to be told about anything hard to reverse, visible to others, or costly.

**Standing rules**
- **The human runs all Gradle/adb commands** and pastes back output (§10.4).
- **The assistant never types credentials.** For sign-in pages the human logs in himself in the browser pane; the assistant checks he's on the *right account* (`Anhad23mahajan`, not a friend's).
- **No publishing/sending/deleting on his behalf without asking** — messages, public content, permanent deletions, form submissions, purchases. (The Google Form and repo docs were built at his direction.)
- **Verify APIs against reality before writing code that depends on them.** The previous assistant inspected the cached library jars (`auth-kt-api.jar`, `postgrest-kt-api.jar`) rather than guessing Supabase-kt signatures, and called it worth the cost given the slow build loop.
- **He defers non-urgent items** (test-lead cleanup, form additions, WhatsApp link delivery) and expects them *tracked*, not forgotten — they live in §16 and `ROADMAP.md`.

**Mistakes the previous assistant made — don't repeat them**
| Mistake | Lesson |
|---|---|
| Told him "real Supabase integration… auth working end-to-end" while a temporary mock was in place | Never describe a stopgap as the real thing |
| Assumed OneDrive locks were the build problem; moved the project | Prove root cause before restructuring |
| Trusted "Studio ran it" as proof the code reached the phone | Check `lastUpdateTime` (§10.5) |
| Batched many browser actions and typed into stale coordinates | Slow down; use element refs (§14) |
| Proposed merging the form link into the Thank-You template without checking there's no lead yet at that moment | Trace the whole flow before proposing |
| Left docs describing removed features (in-app form) | Search the repo for dead references after removing anything |

---

---

## 13. Decision log — what was decided, reversed, and why

Read this before "improving" something that looks odd — it may have been a deliberate trade-off.

| When | Decision | Why | Status |
|---|---|---|---|
| Aug 22 | Native **Android** (Kotlin/Compose), not a Flask web app | Solo; reuse Kalaza Care skeleton; push notifications and `wa.me` are best on a phone | Final (weakness: typing on phones → later the Google Form) |
| Aug 22 | **Supabase** (Postgres + Auth), no separate server for the app | Same stack as Kalaza Care; free; RLS | Final |
| Aug 22 | `wa.me` deep links first, WhatsApp Cloud API later | Free, no ban risk, ~90% of value | `wa.me` built (A7); API = Track D |
| Aug 22 | Cloud-hosted dev on a friend's Claude Pro account → **abandoned** | Waiting for repo access wasted time; "slow and steady wins the game" | Reversed same day; now relevant again (§17) |
| Aug 23 | **Standalone Supabase project**, not shared with Kalaza Care | The friend holding Care's credentials was unavailable; waiting would block everything | Reversible; the lead→resident handoff is therefore **deferred** |
| Aug 23 | Supabase settings: Mumbai region; "Automatically expose new tables" **OFF**; RLS on | Latency to Pune; tighter defaults | Consequence: every table needs explicit `GRANT` |
| Aug 23 | Login by **name**; synthesized email `…@kalazaleads.app`; "Confirm email" **OFF** | Mirrors Kalaza Care's name login; synthetic emails have no inbox | Final. `.internal/.local/.test` TLDs are rejected by Supabase |
| Aug 23 | Teal theme, `com.kalazacare.leads` | Distinguish from Care's red on one phone | Final |
| Aug 23 | Move build to `C:\Dev\kalaza-leads` | Suspected OneDrive locks | Root cause later shown to be transient locks anywhere; folder kept |
| Aug 25 | Build **Track A before B/C/D** | Zero external blockers; supervisor still gets value | Done |
| Aug 25 | **A1 (schema) before any new screen** | Avoid rebuilding fields twice | Done |
| Aug 25 | Built **A7 before A6** | Lower risk, higher value | Done |
| Aug 25 | **Split `how_heard` from `contact_channel`** | Supervisor: "call happens after google search"; one field would destroy attribution analytics | Final |
| Aug 25 | **CSV, not .xlsx**, for export | Apache POI is painful on Android (desugaring, missing AWT/XML classes, APK bloat); any dependency failure only shows after a full build round-trip; CSV opens in Excel/Sheets/WhatsApp | Final unless asked |
| Aug 25 | `staff` table **not** tied to `auth.users`; `assigned_staff_id` FK repointed to `staff(id)` | Not every staff member has a login; safe as the column was null everywhere | Final |
| Aug 25 | Reports computed **client-side** from already-loaded data | No new backend calls at this volume | Final |
| Aug 25 | Drop the request for the supervisor's Excel | He declined (official records; data-protection concerns) | Closed — don't re-ask |
| Aug 27 | **Local WorkManager notifications**, not FCM push | FCM needs a server (Track D); "follow-up due" is a fact about DB data, so a daily local check is legitimate at 1–3 enquiries/day | Final for now |
| Aug 27 | Accept notification-timing imprecision; **no exact alarms** | Exact alarms need an Android 12+ permission flow and OEM battery managers can still throttle | Revisit only if it matters |
| Aug 27 | Track B: recommend a **new dedicated number** as cost-safe default | Coexistence needs a Tech Provider/BSP (₹18–30k/yr if paid); supervisor won't accept that | Supervisor's call — outcome unknown (§15) |
| Aug 27 | Repeatedly deleted/rebuilt Track B docs as understanding improved | Kept only the definitive playbook + cheat sheet + script | Done |
| Sep 16 | **Gate signup by active-staff roster**; do *not* re-check the roster on every sign-in of an existing account | Closes the "anyone with the APK" hole; per-sign-in check risked locking Anhad out of his own test account | Further hardening possible |
| Sep 16 | No forgot-password flow | Emails are fake; recover by deleting the auth user in Supabase and logging in again | Final |
| Sep 17 | **Google Form intake replaces the in-app enquiry screen** | Enquirer types their own details; uniform across WhatsApp/call/walk-in; avoids phone-typing pain | Done. Assistant's objections (cold form, no Q&A, untracked phone enquiries) were overruled by Anhad |
| Sep 17 | Merge form link into the Thank-You template → **withdrawn** | Anhad: "i didn't think of it properly"; Thank-You stays a plain courtesy; also no lead exists yet at that moment | Delivery of the link parked (§9, §16) |
| Sep 17 | Anon **INSERT-only** RLS exception for the bridge | Bridge has no login; select/update/delete stay authenticated | Accepted spam risk, noted in `006` |
| Sep 17 | Skipped age/budget number-range validation on the form (for time) | Bridge tolerates it by parse-and-null | Optional nice-to-have (the spec's §1 table still lists it; the built form lacks it) |
| Sep 17 | Staff-only fields **not** on the form | An enquirer can't know follow-up dates, status, remarks | Final. Consent + language are possible later additions |
| Sep 17 | Delete the in-app screen **immediately** (assistant had proposed waiting) | Anhad: decide the WhatsApp delivery later | Done |
| Sep 17 | Doc cleanup: delete `PROJECT_SPEC.md`, first Meta research doc | Fully superseded | Done |
| Sep 18 | Trim Master Plan → `AUTOMATION_DESIGN.md`; add `ROADMAP.md` | "it's all a little confusing" | Done |
| — | **Not built on purpose:** FCM, exact alarms, xlsx, role enforcement, per-sign-in roster check, forgot-password, lead→resident handoff, Cloud API/webhook, AI features | See rows above / Track D | Open or rejected as stated |

---

## 14. Lessons and gotchas (troubleshooting index)

**Symptom → cause → fix.** Find your symptom, apply the fix. Longer stories are in §10 and `docs/PROGRESS.md` §4.

| Symptom | Cause | Fix |
|---|---|---|
| Gradle fails instantly with `26.0.1` | System JDK too new | `JAVA_HOME` → Android Studio JBR (§10) |
| `Unable to establish loopback connection` | Gradle run from an agent subprocess | Human runs Gradle (§10.4) |
| `Unable to delete directory …\app\build\…` | Transient Windows file lock | Retry loop; skip `clean` (§10.4) |
| "Button does nothing", no logs, after code changes | Compile failed; Studio kept the old APK | `dumpsys … lastUpdateTime` (§10.5) |
| `.\gradlew.bat : not recognized` | Wrong directory | `cd` to the project |
| `adb` not found | Not on PATH | Use full path |
| `adb: device unauthorized` | Phone hasn't trusted this PC | Unlock phone, tap Allow; `adb kill-server` if needed |
| Signup error `email_address_invalid` | Reserved TLD (`.internal/.local/.test`) | Use `@kalazaleads.app` |
| Signup "succeeds" but the account never works | "Confirm email" is ON | Turn it OFF in Supabase Auth |
| `permission denied for table …` although a policy exists | New tables aren't auto-exposed | Add `GRANT` to the role (all migrations already do) |
| Login says name "not recognized as active staff" | Roster gate | Add/activate the name on the Staff screen (or bootstrap SQL on a fresh DB, §7.4) |
| "Incorrect password for …" and it's forgotten | No reset flow | Supabase → Authentication → Users → delete that user; log in again with a new password |
| Password rejected | Supabase minimum is 6 characters | Use ≥ 6 |
| Logout bounces straight back to Leads | `LoginViewModel` still had `isLoggedIn = true` and navigation was a bare `if` | Trigger navigation in `LaunchedEffect(state.isLoggedIn)` **and** reset the ViewModel state **synchronously** in `logout()` (not inside `viewModelScope.launch`) — both are needed |
| Compose: reading a `StateFlow.value` doesn't update UI | Bypasses recomposition tracking | `collectAsState()` |
| Compile error about experimental Material3 API (`FilterChip`, `TopAppBar`) | Needs opt-in | `@OptIn(ExperimentalMaterial3Api::class)` |
| `ExposedDropdownMenu` "Unresolved reference" / anchor-type mismatch | It's a member of `ExposedDropdownMenuBoxScope` here; needs the correct `MenuAnchorType` | Call via the implicit receiver, don't import or qualify it |
| Chips scroll sideways instead of wrapping | `LazyRow` | `FlowRow` (needs an experimental opt-in) |
| Supabase Kotlin: `signInWith(email=…, password=…)` won't compile | API takes a provider + builder | `client.auth.signInWith(Email) { email = …; password = … }` (`…providers.builtin.Email`); `signOut()` is a plain member |
| Notification arrives at the wrong hour | WorkManager + Doze | Accepted (§8). Exact alarms = extra permission flow |
| `adb shell dumpsys jobscheduler` matches hundreds of jobs | The device's Google account contains "kalazacare" | Filter on the package `com.kalazacare.leads`, not the word |
| Apps Script: form submissions don't reach Supabase | No trigger exists (ran `normalize` instead of `setupTrigger`) | Run `setupTrigger`, check **Triggers** page (§9) |
| Apps Script inserts fail | `SUPABASE_URL` is the dashboard URL, or key missing | Use `https://<ref>.supabase.co`; anon key from **API Keys** page |
| Pasted code in Apps Script is a file path | Copied the path, not the contents | Paste the file *contents* |
| Form field silently empty in Supabase | Question title edited on the Form | Titles are the contract; restore or update the script |
| A test submission made before the trigger existed | Trigger wasn't there | It lives only in the Sheet; resubmit (there is no backfill script) |
| `git add` "Filename too long" | Windows MAX_PATH | `git config core.longpaths true` |

**Browser-automation lessons (Google Forms and other UIs)** — for when an agent drives the browser pane:
- Renders lag clicks; take a fresh screenshot before acting on what you see.
- **Coordinates drift** with scroll and re-render → prefer element refs (`find`/`read_page`); if you must click by pixel, re-screenshot each time. Text typed into stale coordinates overwrote question titles more than once; **triple-click to select, then retype**. Placeholder text is sometimes inserted *into* rather than replaced.
- Google Forms **type dropdown**: the first click on an item only *hovers* it; a second click on the same spot selects — but a second click at the same coordinate can also *close* the menu, so re-screenshot between. Arrow-key navigation didn't engage the menu.
- **"Add question" inserts after the focused question** (questions landed out of order when focus drifted); harmless here because the bridge matches titles, not positions.
- Google **guesses a question's type from its title** (sometimes Paragraph, sometimes Checkboxes) — verify each.
- Google's "Other" suggestion chip interferes with typing options; type manually.
- **Response-validation dropdowns are custom widgets**, not native `<select>`.
- **The browser tool refuses clicks that open new tabs/popups** ("new tabs open only from the user's own clicks, never from your input") — so anything needing "View in Sheets" / Extensions → Apps Script must be handed to the human. Read state with JavaScript instead (`window.location.href`, input values).
- **Supabase SQL editor (Monaco)** auto-closes brackets and virtualises the buffer: typing and Ctrl+A were unreliable and left stray `)`. What worked: click by ref, set content via the Monaco API (`editor.setValue`), click the real Run button — or just give the human a file/direct link. "Success. No rows returned" can be stale; verify on the Table Editor/Policies pages. Destructive statements show a confirmation dialog.
- **GitHub settings text inputs (React-controlled)** garble synthetic keystrokes; set the value through the native `HTMLInputElement.prototype.value` setter and dispatch `input`/`change` events.
- Sign-in pages: the human signs in. An expired session means asking him again.

**Claude-tool quirks on Anhad's Windows setup (from earlier sessions):** the Bash tool halves doubled backslashes (`\\` → `\`) and mangles non-ASCII in heredoc "old string" matches — write such files with the Write tool and match on ASCII; `Edit` needs a fresh `Read` after a script changed the file; a `Get-CimInstance … -like '*name*'` query matches the querying PowerShell itself (exclude `$PID` before `Stop-Process`).

---

## 15. Track B / C / D — the WhatsApp automation

**Status: not started.** It waits on decisions and paperwork only the NGO can supply. The plan and the honest analysis are in the docs below; this section is the orientation.

**Track meanings**
- **Track A** — the Android CRM: **done.**
- **Track B** — Meta/WhatsApp Business Platform onboarding: business account, verification documents, payment method, number decision, templates. NGO-side; Anhad advises.
- **Track C** — NGO-authored content: the ~20 FAQ answers, price list PDF, five service info packs, posters/videos/links, template wording, Hindi/Marathi versions. **The single biggest schedule risk** — Anhad can't write facility facts.
- **Track D** — the automation: webhook receiver, reply engine, scheduler, message logging (design in `docs/AUTOMATION_DESIGN.md`; slices D-0…D-6 in `docs/ROADMAP.md`). Gated on B and C.

**The core technical truth.** An app can never read WhatsApp. The only legitimate way is for the NGO's number to be enrolled in Meta's WhatsApp Business Platform (Cloud API): Meta forwards each incoming message to a webhook we host and we reply through the API. Non-official automation (whatsapp-web.js / Baileys) violates WhatsApp's terms and gets numbers banned — never use it on the NGO's number.

**The one decision needed from the supervisor** (`docs/TRACK_B_PAPERWORK_PLAYBOOK.md`; numbers re-verified by live web search 2026-09-22 — one material change found, see the callout after the table):

| | Option A — keep the existing number ("Coexistence") | Option B — a new dedicated number |
|---|---|---|
| Idea | Staff keep using the WhatsApp Business app on the same number while automation runs alongside | A second SIM/number used only by the bot |
| Needs a Tech Provider/BSP? | **Yes** (Meta requires the connecting developer to be one) | **No** |
| Needs Meta App Review? | Only on the free self-Tech-Provider route (no guaranteed timeline; time-box 2–3 weeks) | **No** |
| Ongoing cost | ~₹550–650/yr Meta fees if the self-Tech-Provider attempt succeeds; else ~₹18,000–30,000/yr for a paid BSP (AiSensy from ~₹1,500/mo, Interakt ~₹2,142/mo, Wati from ~₹2,499/mo) | ~₹550–650/yr Meta fees + a basic SIM plan |
| Trade-offs | Loses some features (group sync, disappearing messages, view-once, live location; broadcast lists read-only; WhatsApp for Windows unsupported; linked devices unlinked); needs Business app v2.24.17+ | Two numbers to run; the new number must be republished wherever the NGO shares its WhatsApp contact |

> ⚠️ **Material update (verified 2026-09-22): the "replies are free" framing expires 1 October 2026.** From that date Meta charges ₹0.115/message for service replies and for utility templates sent inside the 24-hour window (both previously free) — but every phone number still gets **1,000 free service messages/month**, resetting monthly. At this NGO's real volume (~200–400 enquiries/year) that allowance almost certainly still covers everything, so the ~₹550–650/yr estimate probably still holds — but say "the first ~1,000 replies a month are free, and our volume is nowhere near that" to the supervisor, not "replies are unlimited free." Also: Meta describes Tech Provider enrollment for Option A as *mandatory* now (not just recommended), and **Embedded Signup v2 (used to onboard existing numbers under Coexistence) is deprecated 8 October 2026** — Option A must be built against **v4** from then on. Details and sources: `TRACK_B_PAPERWORK_PLAYBOOK.md`'s top-of-file note and §8/§10.

The docs recommend **Option B as the cost-safe default**; the decision is the supervisor's. **A new number does NOT remove:** the Meta Business Portfolio, business verification (trust deed / society registration + GST or Udyam + address proof, name and address must match exactly; Meta's SLA is up to 14 business days, many clean submissions clear in 1–5), the payment method, message-template approval, the Track C content, or the Track D coding.

**Universal steps** (either option): Business Portfolio (owned by the **NGO**, not an individual) → business verification → payment method (an NGO-controlled card) → number path (A or B) → templates (Utility category for follow-ups; each language is a separate submission). Unverified accounts start with a 250-contacts/day allowance and show a raw number instead of the business name — the plan is still to verify. Meta pricing (India, verified 2026-09-22): utility/authentication/service ₹0.115/message, marketing ₹0.8631/message, first 1,000 service messages/month/number free — **re-check Meta's current rate card before quoting numbers**, as rates and allowances do change.

**Meeting outcome: UNKNOWN as of 2026-09-21.** A supervisor meeting was expected Sep 17. Anhad said beforehand he was ~90% sure the supervisor would choose the new-number plan — an expectation, not a recorded result. **Ask Anhad what happened and record it in `ROADMAP.md` and `PROGRESS.md`** — everything downstream branches on it. Meeting aids: `TRACK_B_MEETING_CHEATSHEET.md` (glance sheet; four asks — does a Business Portfolio exist and who's admin; trust/society and where are the documents; who gets a new SIM; who writes the 20 answers and by when) and `TRACK_B_SUPERVISOR_MEETING_SCRIPT.md` (rehearsal script).

**Free interim wins** (no API needed): WhatsApp Business *app* greeting message, away message and quick replies (e.g. `/form`, `/palliative`), plus a printed QR code for the intake form — the supervisor gets a visible "automatic reply" within days.

**Design rules for the bot** (non-negotiable, in `AUTOMATION_DESIGN.md`): answer only from an NGO-approved knowledge base; hand off to a human on no match or distress; never negotiate price or give medical advice; be honest it's a bot; per-language templates; honour opt-outs; log every message. **Open design question:** with the Google Form collecting details, should the bot just send the form link instead of asking qualifying questions one by one?

**Track D can start before Track B completes** on placeholder answers (slice D-0), and possibly against Meta's sandbox test number for D-1 (confirm its current limits in the developer console — unverified).

---

---

## 16. Open items and next steps

The prioritised plan is `docs/ROADMAP.md`. This is the complete list of loose ends as of 2026-09-21, so nothing depends on anyone's memory.

**Needs a decision or action from Anhad / the supervisor**
1. **Record the Track B meeting outcome** (§15) — everything in Track B/D branches on it.
2. **Hand over the Track C worksheet**: ~20 FAQ answers (list in `AUTOMATION_DESIGN.md` §5), price-list PDF, five service info packs, posters/links. Ask for ~5 answers a week with a date attached.
3. **Decide the languages** enquiries actually come in (English/Hindi/Marathi) — shapes the knowledge base and every template.
4. **Ownership of the Google Form / response Sheet / Apps Script.** They live in Anhad's *personal* Google account and will hold real families' medical/contact data. Move to (or co-own with) an NGO account and decide who may see the Sheet.
5. **Supabase free-tier decision** for real data — verified 2026-09-22: pauses after 7 days with zero DB queries (restorable up to 1 year, then deleted), and there is no backup system at all on the Free plan (0-day retention). Options: a scheduled export/backup script, keep the project 'warm' with a periodic real query, or upgrade to Pro (paid plans can't be paused).
6. **Deliver the form link to enquirers**: put it in the WhatsApp Business app's greeting message and a `/form` quick reply; print a QR code for the front desk.

**Housekeeping**
7. **Delete test leads** from Supabase (Table Editor): "hfgnb" (an old test), the second "TEST Anhad" submission, and any third test row — **verify the actual rows before deleting** (counts in chat were inconsistent). The *first* "TEST Anhad" submission exists **only in the response Sheet** (it predated the trigger) — delete it there too.
8. **Revoke every outstanding GitHub PAT** at `github.com/settings/personal-access-tokens`, and confirm none is left active. Many were pasted into chat over the project's life and revocation was never confirmed for all.
9. **Review Supabase Auth users** (Authentication → Users). Accounts created by the pre-fix "any name works" signup (before Sep 16) may still exist; delete any that aren't real staff. Also confirm whether the supervisor was added to the Staff roster (unconfirmed).
10. **Unclaimed-name weakness (security).** Signup is gated by *knowing an active staff name*, with no secret. If a staff row exists whose person hasn't logged in yet, anyone with the APK who types that name + any password **claims that account first**. Mitigation: have every staff member log in once right after being added; possible hardening — an admin-issued invite code, or an admin-approval step.
11. **Docs**: `docs/PROGRESS.md` and `docs/GOOGLE_FORM_INTAKE_SPEC.md` had a few stale statements (in-app form still described in places; the spec's §1 table lists number-range validations the built form lacks). These were corrected on 2026-09-21; if you find more, fix them.

**Optional / later**
12. Form additions: consent checkbox (`consent_given`), preferred-language dropdown (`preferred_language`), age/budget number validation. Steps: `GOOGLE_FORM_INTAKE_SPEC.md` §6 (+ add a `getAnswer` line in the bridge for each new question).
13. Bridge hardening: today a failed insert is only logged in Apps Script Executions (the Sheet row still exists). Add an email-on-failure and/or a "backfill from Sheet" script; optionally an anon-spam guard (shared secret checked in the bridge, or an Edge Function).
14. Enforce `staff.role` (currently informational); re-check the roster on every sign-in.
15. Replace deprecated `Icons.Filled.ArrowBack` (§10.7).
16. Unit tests for pure logic (`ReportsAnalytics.kt`, the tab filters, the bridge's `normalize`/`mapMulti`).
17. Exact-time notifications (AlarmManager) or real push (FCM) — only if the timing proves to matter or Track D exists.
18. Lead → resident handoff into Kalaza Care (deferred; the databases are separate).
19. Portfolio: a 2–3 minute demo recording of the whole loop, README screenshots, a project write-up.
20. **Track D** slices D-0…D-6 (`ROADMAP.md` §4) once B/C give a foothold.

---

## 17. Security, privacy, and working from someone else's account

**This repository is public — assume everything committed is world-readable forever (git history included).**

**Never put in the repo, in docs, in commit messages, or in issues:** GitHub tokens (PATs); the Supabase **service-role/secret** key (it must not be used anywhere in this project); the database password; passwords of any kind; Google credentials; real families' data (names, phones, medical details); personal contact details of individuals. `local.properties`, `.env*`, `*.key`, `*.keystore`, `google-services.json` are gitignored — keep it that way.

**Secret classes**

| Item | Class | Handling |
|---|---|---|
| GitHub PAT | secret, short-lived | Single-repo, Contents-only, shortest expiry; used once; stripped from the remote; revoked (§11) |
| Supabase service-role / secret key | **never use** | Not needed anywhere in this project |
| Supabase DB password | secret | Generated at project creation and not recorded anywhere; resettable in the dashboard |
| Supabase **anon / publishable key** | *semi-public* — it's compiled into every APK and RLS guards the data | Keep out of the repo anyway; lives in `local.properties` and the Apps Script's Script Properties |
| Supabase project URL / ref | identifier, already public in docs | Fine |
| Google Form responder link | public by design | Fine. The **edit** link and the Sheet/Script are owner-only |
| Google / Supabase / GitHub logins | secret | Only the human types them |

**Real families' data.** Once the Form is in use, the live database and the response Sheet contain real medical and financial details of elderly people (India's DPDP Act 2023 applies: collect only what's needed, get consent, honour opt-out, keep access staff-only). Use **fake data** ("TEST …") for every test, never paste real rows into chat or docs, and never export them to a shared machine.

**Working from a friend's machine / Claude account** (this is the situation this guide was written for)
1. **Everything you type into a Claude session is visible to that account's owner**, and a Claude Code cloud environment's *env-var box* is visible to anyone using the environment. Don't put secrets there. Keys belong in the gitignored `local.properties`.
2. **PATs pasted into that chat are visible to the friend.** Keep them single-repo and short-lived, and revoke right after the push.
3. **Log in to GitHub, Supabase and Google only in the browser**, as Anhad, and **sign out and clear the session afterwards** — browsers on shared machines keep cookies. (Earlier, a friend's GitHub session was active in a browser and had to be replaced with Anhad's own — check whose account is logged in before creating anything.)
4. **You do not need Anhad's live accounts to develop.** Everything can be rebuilt: create a *new* Supabase project and run the SQL (§7.4), point `local.properties` at it, and (if you need intake) recreate the Form from the spec (§9). That gives a safe sandbox with no real data. To touch the *live* Supabase project, Google Form/Sheet/Script or the GitHub repo, Anhad must do it himself or add you.
5. **The keys for the live project** must travel out-of-band (password manager or a direct private message) — never through the repo or these docs.
6. **Distributing the APK:** it is a debug build wired to the *live* project. Sideloading needs "Install unknown apps" and a Play Protect "Install anyway"; iPhones can't install it; a recipient can only get in if their name is on the active staff roster (since the Sep 16 fix).

---

## 18. What is NOT in GitHub

GitHub holds all source, all docs, the SQL migrations and the Apps Script. It does **not** hold the following — by design or by nature:

| Thing | Where it is | Notes |
|---|---|---|
| `local.properties` (Supabase URL + anon key + `sdk.dir`) | Anhad's `C:\Dev\kalaza-leads` | Gitignored on purpose. Recreate from `local.properties.example` + the Supabase **API Keys** page |
| The live Supabase project and its rows | Supabase cloud, Anhad's account | Schema is reproducible from `docs/sql/` (§7.4); the **data** is not backed up anywhere |
| The Google Form, response Sheet, Apps Script project, trigger | Anhad's Google account | Form spec + script are in the repo; the live objects and any responses are not |
| Supabase DB password | not recorded | Resettable |
| The original v1 concept document (`New_NGO_Project_Full_Context.md`) | Anhad's `Downloads` | Kept private (it names individuals). Its reasoning is summarised in §2/§13 |
| A copy of the original Master Plan (`Kalaza_Leads_Master_Plan_v2.md`) | Anhad's `Downloads` | Also recoverable from git: `git show ffe8913:docs/MASTER_PLAN_V2.md` |
| Kalaza Care (the other app) materials — source zips, audit HTML (`kalaza_audit.html` on the Desktop), guides, logbooks | Anhad's `Downloads` / `Desktop` | Different project; the audit contains security findings — **do not publish** |
| Family-facing / non-technical explanations | Anhad's files | Deliberately kept out of the public repo |
| Conversation history that produced all this (~1.2 MB of text across ~8 sessions, Aug 20–Sep 21) | Claude Code's local session folder on Anhad's machine (`~/.claude/projects/…`) | **Fully summarised in this guide.** Not needed to continue |
| Claude "memory" notes (preferences, environment gotchas) | Same local folder | Their content is folded into §10, §12, §14 |
| Built APKs | `app/build/…` (gitignored) | Rebuild any time |
| The stale OneDrive clone (`Desktop\kalaza-leads`) | Anhad's Desktop | Verified to contain **nothing** not on GitHub |

**Audit result (2026-09-21):** the canonical clone was in sync with `origin/main`, with no uncommitted or untracked files other than gitignored `local.properties`; there were no stashes and no other branches; the wrapper JAR and version catalog are tracked, so a fresh clone builds once `local.properties` exists.

---

## 19. Glossary

- **Track A / B / C / D** — A: the Android CRM (done). B: Meta/WhatsApp onboarding. C: NGO-written content. D: the WhatsApp automation engine.
- **Enquirer / patient** — the person contacting the NGO (often a son/daughter) vs. the elderly person the care is for. Tracked separately on purpose.
- **Lead** — one enquiry; a row in `leads`.
- **Converted** — the family started living at the facility (supervisor's definition). **Days-to-convert** = converted date − enquiry date.
- **Follow-up person** — the staff member assigned to a lead (`assigned_staff_id`).
- **Staff roster** — the `staff` table; drives assignment and gates signup.
- **RLS** — Postgres Row-Level Security. **Grant** — the base permission a role needs *in addition to* RLS.
- **Anon key / publishable key** — Supabase's public client key. **Service-role key** — the all-powerful one; never used here.
- **PostgREST** — the REST layer Supabase puts on Postgres (`/rest/v1/<table>`).
- **Apps Script / installable trigger** — Google's scripting platform; an *installable* `onFormSubmit` trigger runs `handleFormSubmit` for each response.
- **`wa.me` link** — a deep link that opens WhatsApp with a pre-filled message to a number; staff press send.
- **WhatsApp Business app vs. Business Platform (Cloud API)** — the free phone app vs. Meta's official API for automation.
- **Coexistence** — a Meta feature letting one number run the Business app and the Cloud API together; needs a Tech Provider/BSP.
- **BSP / Tech Provider / Solution Partner** — Meta-approved intermediaries who onboard businesses to the API (BSPs charge subscriptions).
- **WABA** — WhatsApp Business Account. **Template** — a pre-approved message required for business-initiated messages outside the 24-hour window. **24-hour window** — replies within 24 h of the user's message are free-form and free.
- **Business verification** — Meta's check of the organisation's legal documents (name/address must match exactly).
- **Edge Function** — Supabase's serverless Deno/TypeScript functions (planned for Track D).
- **WorkManager / Doze** — Android's background-job library and the battery mode that defers such jobs (why notifications aren't exact).
- **FCM** — Firebase Cloud Messaging, real server push (not used; needs a backend).
- **JBR** — JetBrains Runtime, the JDK bundled with Android Studio.
- **PAT** — GitHub personal access token (fine-grained, per-repo, short-lived here).
- **DPDP Act 2023** — India's data-protection law that applies to the families' medical and contact data.

---

*End of the handoff guide. If you changed something material, update the relevant section above, the "Last verified" line in §0, and `docs/PROGRESS.md` / `docs/ROADMAP.md`.*
