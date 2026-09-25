# Google Form Intake — Field Spec & Setup

This replaces the in-app "New Enquiry" screen. The enquirer fills this form
(how the link reaches them is still open - see `docs/ROADMAP.md`), it lands in a Google Sheet, and a small script
(`tools/google-form-bridge/KalazaFormBridge.gs`) pushes each response into
the same `leads` table the app already reads from — so nothing changes on
the app side except that new leads now arrive already filled in, instead
of staff typing them.

**Status: done.** The form is built, published, the bridge (§3) is wired
up, §4 passed cleanly with a real test submission, and the in-app
"New Enquiry" screen (`AddLeadScreen.kt`) has been removed (2026-09-17) —
the Google Form is now the only way new leads enter the system.

---

## 0. Status — the form is already built

The form itself (all 22 questions below, §1) has already been built and
published under Anhad's Google account, and linked to a response
spreadsheet.

- **Responder link** (send this to enquirers, eventually via WhatsApp):
  `https://docs.google.com/forms/d/e/1FAIpQLSfH3qQxdCvq4Iw0-kS6tuef2hx4Y-q8bT7Jef_y99A9yhSp0A/viewform`
- **Edit link** (for making changes to the form itself):
  `https://docs.google.com/forms/d/1C6eQ1uoamNoquSEXbMa-SMoif6ThfYmHjzQuvJ2b0So/edit`
- Every question was verified against the table in §1 field-by-field after
  building — titles, types, and every option spelled exactly as specified.

**Nothing is left for the form itself.** Historically the bridge setup (section 3) and the test (section 4)
had to be done by the owner in their own Google account and browser, because an automated browser cannot open
the Sheets / Apps Script popup. Both are done; section 3 and section 4 record the steps and the results.
Section 1 and section 2 are kept below as the reference spec / rebuild recipe.

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
| 5 | `Your phone number (10 digits)` | Short answer, response validation: regex matches `^[0-9]{10}$` | — | **Yes** | `enquirer_phone` (country code is always `+91`, the DB/bridge default) |
| 6 | `Your relation to the patient` | Dropdown | Son / Daughter / Spouse / Sibling / Grandchild / Nephew or Niece / Friend / Other | No | `enquirer_relation` |
| 7 | `Where are you from (location)` | Short answer | — | No | `enquirer_location` |
| 8 | `Patient's name` | Short answer | — | No | `patient_name` |
| 9 | `Patient's age` | Short answer *(planned validation: number 0-120 - NOT applied on the built form)* | — | No | `patient_age` |
| 10 | `Patient's gender` | Dropdown | Male / Female / Other | No | `patient_gender` |
| 11 | `What does the patient have? (select all that apply)` | Checkboxes | Alzheimer's / Dementia / Parkinson's / Cancer / Post-Stroke / Post-Operative / Post-Transplant / Bedridden / Diabetes / Cardiac / Mobility Impaired / Other | No | `patient_conditions` |
| 12 | `Current condition / mobility` | Paragraph | — | No | `current_condition` |
| 13 | `Medical history` | Paragraph | — | No | `medical_history` |
| 14 | `Service(s) wanted (select all that apply)` | Checkboxes | Assisted Living / Palliative Care / Post-Transplant Care / Cancer Care / Medical Recovery / Dementia Care / Day Care | No | `service_wanted` |
| 15 | `Room type preferred` | Dropdown | Single Room / Double Sharing / Full Flat | No | `accommodation_type` |
| 16 | `Budget - minimum (Rs.)` | Short answer *(planned validation: number - NOT applied on the built form)* | — | No | `budget_min` |
| 17 | `Budget - maximum (Rs.)` | Short answer *(planned validation: number - NOT applied on the built form)* | — | No | `budget_max` |
| 18 | `Amenities requested (select all that apply)` | Checkboxes | AC / Lift / Attached Bathroom / Ground Floor / Female Attendant / Private Nurse / Veg Food / Other | No | `amenities_requested` |
| 19 | `Any special requirements` | Paragraph | — | No | `special_requirements` |
| 20 | `What would you like to ask us?` | Paragraph | — | No | `queries` |
| 21 | `Anything else you'd like to add` | Paragraph | — | No | `comments` |
| 22 | `Preferred visit date (if known)` | Date | — | No | `planned_visit_date` |

**Changed 2026-09-25 (the table above is the live form, in its live order):**
- The **Country code** question was deleted (always +91; the bridge already defaults to +91 when the answer is missing).
- Options removed: relation → *Self*, *Hospital Staff*; services → *Respite Care*; room type → *Triple Sharing*, *Dormitory*, *Not Sure*. The database check constraints, the bridge maps and `LeadFormOptions.kt` still accept those values (old rows stay valid); the form just no longer offers them.
- **Every question title now carries a number prefix** (`1. How did they first contact us?` … `22. Preferred visit date (if known)`). The numbers in the first column above are those live numbers. The bridge's `getAnswer` ignores a leading `N. ` and treats curly apostrophes as straight, so the *title text after the number* is still the contract. The numbering was applied by a one-off Apps Script (`FormApp`), because Google Forms has no built-in question numbering.
- **Order fixed (2026-09-25, second pass):** a one-off script moved the questions into the order above and renumbered them. Earlier the order had drifted ("Your name" before "If referral or other", "Medical history" before "Current condition", and Budget - maximum before minimum). The bridge matches titles, not positions, so it was never affected.

**Deliberately left off the form**: `next_follow_up_date` — that's an
internal scheduling field staff set later, not something to ask the
enquirer to decide for themselves.

**On the apostrophes** (Alzheimer's, Parkinson's): type them however feels
natural when building the form — the bridge script normalizes text before
matching (strips punctuation, lowercases), so it doesn't matter whether
Google's autocorrect turns a straight `'` into a curly `'` as you type.
This was deliberately built to not be a silent failure point.

---

## 2. Building the form — already done (§0)

*(Kept for reference / rebuilding if this form is ever lost.)*

1. Go to [forms.google.com](https://forms.google.com), create a new blank form.
2. Title it something like "Kalaza Care — Enquiry Details."
3. Add each question from the table above, in order, matching the title
   text and type exactly.
4. For checkbox/dropdown questions, add the listed options exactly as
   written (order doesn't matter, spelling does).
5. For question 6 (phone), question 10 (age), 17/18 (budget): open the
   "..." menu on that question → Response validation → set as noted in
   the table. *(Age/budget number validation was skipped for time —
   optional nice-to-have, not required for correctness.)*
6. Click the **Responses** tab → the green Sheets icon → "Create a new
   spreadsheet" — this is what the bridge script attaches to. Then
   **Publish** the form (top-right) — without this, it won't accept
   responses from anyone outside your own account.

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

## 4. Testing — done, 2026-09-17

1. Submitted a real test response to the Google Form ("TEST Anhad").
2. Opened the Kalaza Leads app, Leads screen, pulled to refresh — the test
   entry appeared in the "All" tab within seconds.
3. Opened it and confirmed every field landed correctly, including the
   checkbox fields (conditions/services/amenities) saving as a proper
   list, not garbled text.
4. Checked the Apps Script's **Executions** log — showed a Completed run
   for `handleFormSubmit`, no errors.
5. In-app "New Enquiry" screen removed the same day — see §0.

---

## 5. What's NOT covered here (on purpose)

- **How the form link actually reaches the enquirer** — still an open
  question, parked for later (see `docs/ROADMAP.md` section 2 and `docs/HANDOFF.md` section 9).
- **Anything about the WhatsApp chatbot / Q&A engine** — that's Track D,
  unrelated to this intake bridge.
- **Staff-only pipeline fields** — `next_follow_up_date`, `actual_visit_date`,
  `converted_at`, `status`, `follow_up_count`,
  `not_converted_reason`/`detail`, `feedback_*_themes`, `final_remarks`.
  These are set by staff as a lead progresses, not something an enquirer
  filling this form in has any way to answer — deliberately excluded, not
  an oversight (2026-09-17: confirmed with Anhad after end-to-end testing).

## 6. Possible future additions (not done, not urgent)

Raised 2026-09-17, explicitly deferred — revisit only if it comes up again:

- A **consent checkbox** (→ `consent_given`) — worth having on a form that
  collects medical/financial family data.
- **Preferred language** (→ `preferred_language`) — a quick dropdown could
  help staff route the enquiry appropriately.

If either gets added: update the question table in §1, `LeadFormOptions.kt`
stays untouched (these aren't multi-select/lookup fields), and
`KalazaFormBridge.gs`'s `payload` object in `handleFormSubmit` needs the
new `getAnswer(nv, '<exact question title>')` line.
