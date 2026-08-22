# Kalaza Leads

An enquiry-to-conversion CRM Android app for **Kalaza Care**, an elder-care NGO in Pune. Companion to [Kalaza Care](https://github.com/Anhad23mahajan/kalaza-care) — where that app handles residents already in the facility, this one handles everyone *before* they become a resident: the families calling and messaging to enquire about care for a parent or relative.

> **Status:** in active development. This repo is the fresh foundation — the Android app itself is being scaffolded from the [Kalaza Care](https://github.com/Anhad23mahajan/kalaza-care) skeleton (auth, Supabase wiring, design system, navigation patterns) and built on top.

## Why it exists

The supervisor's own words, decoded: *"the primary task is to take the follow-up on the enquiry"*. The NGO gets enquiries by WhatsApp, phone call, and walk-in — and without a system, some slip through the cracks. This app makes sure they don't.

## What it does

- **Multi-channel intake** — every enquiry (WhatsApp / call / walk-in) lands in one place with the family's context, the elderly person's medical history, budget, room preference, and how they heard about the NGO.
- **Follow-up-due list** as the home screen — the killer feature. A phone push notification tells staff *"you have 3 follow-ups due today"* so no lead is forgotten.
- **Contact log per lead** — every call, message, and visit recorded with outcome (positive / negative / no answer / call back later) and notes. Doubles as an audit trail and a reminder engine.
- **One-tap WhatsApp** — the app drafts the right message for the right lead at the right time; staffer taps a `wa.me` deep link that opens WhatsApp with everything pre-filled, and hits send. Zero API cost, zero ban risk, fully within WhatsApp's rules.
- **Lead → resident handoff** — the moment a family converts, their record hands off into Kalaza Care's resident data (both apps share the same Supabase project).

## Tech stack

**Client:** Kotlin, Jetpack Compose (Material 3), MVVM + `StateFlow`
**Backend:** Supabase — Postgres, Auth, Realtime
**Serverless layer:** Supabase Edge Functions (Deno/TypeScript) for the Claude API calls (message drafting, WhatsApp-text parsing, lead-history summarization) and scheduled follow-up reminders — the Claude API key stays server-side, never in the APK.
**Messaging:** `wa.me` deep links for MVP; WhatsApp Business Cloud API for Phase 2 automation.

## Where AI earns its place

Three specific uses, not bolted on:

1. **Drafting** the personalized thank-you / follow-up / feedback message from a lead's context.
2. **Parsing** a pasted raw WhatsApp enquiry or call notes into the structured form fields — the key mitigation for the "typing on a phone is annoying" data-entry problem.
3. **Summarizing** a lead's entire contact history into a single line.

All three run through the Claude API from the serverless layer.

## Repository layout

Full concept, data model, and phasing plan live in [`docs/PROJECT_SPEC.md`](docs/PROJECT_SPEC.md). Android app source lands here as it's scaffolded.
