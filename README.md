# Kalaza Leads

An enquiry-to-conversion CRM Android app for **Kalaza Care**, an elder-care NGO in Pune. Companion to [Kalaza Care](https://github.com/Anhad23mahajan/kalaza-care) — where that app handles residents already in the facility, this one handles everyone *before* they become a resident: the families calling and messaging to enquire about care for a parent or relative.

> **Status:** the Android CRM is **fully built, tested end-to-end on a real device, and in use.** A planned WhatsApp Business Platform auto-reply bot was attempted and then **abandoned (2026-10-02)** after real-world setup problems (number-migration dead ends, unclear payment/verification requirements) made it impractical for a solo project. Current work: fixing the follow-up notification system and redesigning the WhatsApp quick-message templates — see `docs/ROADMAP.md`.

## Why it exists

The supervisor's own words, decoded: *"the primary task is to take the follow-up on the enquiry"*. The NGO gets enquiries by WhatsApp, phone call, and walk-in — and without a system, some slip through the cracks. This app makes sure they don't.

## What it does

- **Multi-channel intake via Google Form** — every enquiry (WhatsApp / call / walk-in) is filled in by the enquirer themselves on a Google Form, which lands in the same `leads` table through an Apps Script bridge, with the family's context, the elderly person's medical history, budget, room preference, and how they heard about the NGO.
- **Follow-up-due list** as a home-screen tab — a phone notification tells staff *"you have follow-ups due today"* so no lead is forgotten.
- **Contact log per lead** — every call, message, and visit recorded with outcome (positive / negative / no answer / call back later) and notes. Doubles as an audit trail and a reminder engine.
- **One-tap WhatsApp** — the app drafts the right message for the right lead at the right time; staff tap a `wa.me` deep link that opens WhatsApp with everything pre-filled, and hit send. Zero API cost, zero ban risk, fully within WhatsApp's rules.
- **Single admin** — the app is used by one person. There is no signup: the one admin account lives in Supabase and new signups are switched off.
- **CSV export + reports/analytics** — export any filtered list of leads to CSV, and a reports screen breaks down conversion rate, pipeline funnel, source/service/staff performance, and unmet demand (e.g. "families lost because we don't have a lift").

## Tech stack

**Client:** Kotlin, Jetpack Compose (Material 3), MVVM + `StateFlow`
**Backend:** Supabase — Postgres, Auth (RLS-gated; single admin account, signups disabled)
**Intake bridge:** Google Forms → Google Apps Script → Supabase REST API (anon, insert-only)
**Messaging:** `wa.me` deep links, reviewed and sent manually by the admin — a categorized list of templates is being built to replace the current 3 fixed buttons. (A WhatsApp Business Cloud API auto-reply bot was attempted and abandoned 2026-10-02 — see `docs/HANDOFF.md` §15.)

## Where to start reading

- [`docs/HANDOFF.md`](docs/HANDOFF.md) — **start here if you're new**: the complete guide (history, architecture, database, build, decisions, gotchas, open items).
- [`docs/ROADMAP.md`](docs/ROADMAP.md) — where the project stands and what's next, phase by phase (**authoritative current scope**).
- [`docs/PROGRESS.md`](docs/PROGRESS.md) — the engineering build log: what's been built, how, and why, kept up to date as work lands.
- [`docs/GOOGLE_FORM_INTAKE_SPEC.md`](docs/GOOGLE_FORM_INTAKE_SPEC.md) — the intake form's field spec and the Apps Script bridge setup.

## Repository layout

```
app/src/main/java/com/kalazacare/leads/
  KalazaLeadsApp.kt                    Application class
  data/
    model/                             Lead, UpdateLeadRequest, StaffMember
    remote/SupabaseClients.kt          Supabase client, wired to local.properties
    repository/                        Auth / Leads / Staff / ContactActivities repos
  ui/
    MainActivity.kt                    Screen state machine (no NavHost)
    login/                             Login screen + ViewModel
    leads/                             Leads list, detail/edit, staff, reports screens
    theme/                             Material 3 theme (teal, distinct from Kalaza Care's red)
tools/google-form-bridge/
  KalazaFormBridge.gs                  Apps Script bridge (Google Form -> Supabase)
```

Copy `local.properties.example` to `local.properties` and fill in a Supabase project's URL/anon key before building.
