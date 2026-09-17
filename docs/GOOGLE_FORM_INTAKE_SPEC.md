# Google Form Intake — Field Spec & Setup

This replaces the in-app "New Enquiry" screen. The enquirer fills this form
(sent via WhatsApp), it lands in a Google Sheet, and a small script
(`tools/google-form-bridge/KalazaFormBridge.gs`) pushes each response into
the same `leads` table the app already reads from — so nothing changes on
the app side except that new leads now arrive already filled in, instead
of staff typing them.

**Do not remove the in-app "New Enquiry" screen yet.** Build this, test it
end-to-end (§4 below), and only then come back to remove that screen —
otherwise there'd be a gap where nobody can add a new lead at all.

---

## 1. Every question, exact wording and type

Build these in Google Forms **in this exact order**, with **this exact
question title text** — the bridge script matches on the literal title, so
a typo here breaks that field silently.

| # | Question title (type it exactly) | Type | Options (exact text) | Required? | → Database field |
|---|---|---|---|---|---|
| 1 | `How did they first contact us?` | Dropdown | Phone Call / WhatsApp / Walk-in / Website / Email / Instagram DM | No | `contact_channel` |
| 2 | `How did you hear about Kalaza Care?` | Dropdown | Google Search / Google Maps / Instagram / Facebook / Referral - Friend or Family / Referral - Hospital / Referral - Doctor / Passing By / Newspaper / Other | No | `how_heard` |
| 3 | `If referral or other, please give details` | Short answer | — | No | `how_heard_detail` |
| 4 | `Your name` | Short answer | — | **Yes** | `enquirer_name` |
| 5 | `Country code` | Dropdown | +91 / +1 / +44 / +971 / +61 | No (defaults to +91 if skipped) | `enquirer_country_code` |
| 6 | `Your phone number (10 digits)` | Short answer, response validation: regex matches `^[0-9]{10}$` | — | **Yes** | `enquirer_phone` |
| 7 | `Your relation to the patient` | Dropdown | Son / Daughter / Spouse / Sibling / Grandchild / Nephew or Niece / Friend / Self / Hospital Staff / Other | No | `enquirer_relation` |
| 8 | `Where are you from (location)` | Short answer | — | No | `enquirer_location` |
| 9 | `Patient's name` | Short answer | — | No | `patient_name` |
| 10 | `Patient's age` | Short answer, response validation: number, between 0 and 120 | — | No | `patient_age` |
| 11 | `Patient's gender` | Dropdown | Male / Female / Other | No | `patient_gender` |
| 12 | `What does the patient have? (select all that apply)` | Checkboxes | Alzheimer's / Dementia / Parkinson's / Cancer / Post-Stroke / Post-Operative / Post-Transplant / Bedridden / Diabetes / Cardiac / Mobility Impaired / Other | No | `patient_conditions` |
| 13 | `Current condition / mobility` | Paragraph | — | No | `current_condition` |
| 14 | `Medical history` | Paragraph | — | No | `medical_history` |
| 15 | `Service(s) wanted (select all that apply)` | Checkboxes | Assisted Living / Palliative Care / Post-Transplant Care / Cancer Care / Medical Recovery / Dementia Care / Respite Care / Day Care | No | `service_wanted` |
| 16 | `Room type preferred` | Dropdown | Single Room / Double Sharing / Triple Sharing / Full Flat / Dormitory / Not Sure | No | `accommodation_type` |
| 17 | `Budget - minimum (Rs.)` | Short answer, response validation: number | — | No | `budget_min` |
| 18 | `Budget - maximum (Rs.)` | Short answer, response validation: number | — | No | `budget_max` |
| 19 | `Amenities requested (select all that apply)` | Checkboxes | AC / Lift / Attached Bathroom / Ground Floor / Female Attendant / Private Nurse / Veg Food / Other | No | `amenities_requested` |
| 20 | `Any special requirements` | Paragraph | — | No | `special_requirements` |
| 21 | `What would you like to ask us?` | Paragraph | — | No | `queries` |
| 22 | `Anything else you'd like to add` | Paragraph | — | No | `comments` |
| 23 | `Preferred visit date (if known)` | Date | — | No | `planned_visit_date` |

**Deliberately left off the form**: `next_follow_up_date` — that's an
internal scheduling field staff set later, not something to ask the
enquirer to decide for themselves.

**On the apostrophes** (Alzheimer's, Parkinson's): type them however feels
natural when building the form — the bridge script normalizes text before
matching (strips punctuation, lowercases), so it doesn't matter whether
Google's autocorrect turns a straight `'` into a curly `'` as you type.
This was deliberately built to not be a silent failure point.

---

## 2. Building the form

1. Go to [forms.google.com](https://forms.google.com), create a new blank form.
2. Title it something like "Kalaza Care — Enquiry Details."
3. Add each question from the table above, in order, matching the title
   text and type exactly.
4. For checkbox/dropdown questions, add the listed options exactly as
   written (order doesn't matter, spelling does).
5. For question 6 (phone), question 10 (age), 17/18 (budget): open the
   "..." menu on that question → Response validation → set as noted in
   the table.
6. Once built, click the **Responses** tab → the green Sheets icon →
   "Create a new spreadsheet" — this is what the bridge script attaches to.

---

## 3. The database bridge (Apps Script)

The script lives at `tools/google-form-bridge/KalazaFormBridge.gs` in this
repo — see that file directly for the code. Setup:

1. Open the response Spreadsheet from step 6 above.
2. Extensions → Apps Script.
3. Delete the placeholder code, paste in the contents of
   `KalazaFormBridge.gs`.
4. Project Settings (gear icon) → Script Properties → add two properties:
   - `SUPABASE_URL` → the project URL (same one from `local.properties`,
     e.g. `https://niqhlkdyaklnngcanxld.supabase.co`)
   - `SUPABASE_ANON_KEY` → the anon key (same one from `local.properties`)

   *(Kept as Script Properties rather than pasted into the code itself —
   not because the anon key is secret, it's already inside the built APK,
   but so the script file in this repo doesn't need editing per-environment.)*
5. Back in the script editor, run the `setupTrigger` function once (top
   menu, select it from the function dropdown, click Run). This is what
   makes the bridge fire automatically on every future form submission.
   The first run will ask for permissions — approve them (this script
   only touches this one spreadsheet and makes web requests, nothing else).

---

## 4. Testing — do this before touching the in-app form

1. Submit a real test response to the Google Form (use a fake name/number
   you'll recognize, e.g. "TEST Anhad").
2. Open the Kalaza Leads app, go to the Leads screen, pull to refresh (or
   just reopen the app) — the test entry should appear in the "All" tab
   within a few seconds of submitting.
3. Open it and confirm every field landed correctly — especially the
   checkbox fields (conditions/services/amenities) actually saved as a
   proper list, not garbled text.
4. Check the Apps Script's **Executions** log (in the script editor, left
   sidebar) for that run — should show a successful (200) response, not
   an error.
5. Only once this works cleanly, come back and remove the in-app "New
   Enquiry" screen — that's a separate, later step, not part of this one.

---

## 5. What's NOT covered here (on purpose)

- **How the form link actually reaches the enquirer** — still an open
  question, parked for later (see chat history).
- **Removing the in-app form** — deliberately a separate, later step.
- **Anything about the WhatsApp chatbot / Q&A engine** — that's Track D,
  unrelated to this intake bridge.
