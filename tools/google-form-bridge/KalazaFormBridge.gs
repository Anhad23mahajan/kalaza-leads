/**
 * Kalaza Leads — Google Form → Supabase bridge.
 *
 * Setup: docs/GOOGLE_FORM_INTAKE_SPEC.md has the full instructions.
 * Short version: paste this into the Apps Script editor attached to the
 * form's response spreadsheet, set SUPABASE_URL / SUPABASE_ANON_KEY as
 * Script Properties, then run setupTrigger() once.
 *
 * Field mappings and question titles here MUST match
 * docs/GOOGLE_FORM_INTAKE_SPEC.md §1 and app/.../LeadFormOptions.kt
 * exactly -- these are the source of truth, kept in sync by hand.
 */

var CONFIG = {
  SUPABASE_URL: PropertiesService.getScriptProperties().getProperty('SUPABASE_URL'),
  SUPABASE_ANON_KEY: PropertiesService.getScriptProperties().getProperty('SUPABASE_ANON_KEY'),
};

var COUNTRY_CODES = ['+91', '+1', '+44', '+971', '+61'];

function normalize(text) {
  return String(text || '').toLowerCase().replace(/[^a-z0-9]/g, '');
}

function buildLookup(pairs) {
  var map = {};
  pairs.forEach(function (pair) {
    map[normalize(pair[0])] = pair[1];
  });
  return map;
}

var CONTACT_CHANNEL_MAP = buildLookup([
  ['Phone Call', 'phone_call'],
  ['WhatsApp', 'whatsapp'],
  ['Walk-in', 'walk_in'],
  ['Website', 'website'],
  ['Email', 'email'],
  ['Instagram DM', 'instagram_dm'],
]);

var HOW_HEARD_MAP = buildLookup([
  ['Google Search', 'google_search'],
  ['Google Maps', 'google_maps'],
  ['Instagram', 'instagram'],
  ['Facebook', 'facebook'],
  ['Referral - Friend or Family', 'referral_friend_family'],
  ['Referral - Hospital', 'referral_hospital'],
  ['Referral - Doctor', 'referral_doctor'],
  ['Passing By', 'passing_by'],
  ['Newspaper', 'newspaper'],
  ['Other', 'other'],
]);

var RELATION_MAP = buildLookup([
  ['Son', 'son'],
  ['Daughter', 'daughter'],
  ['Spouse', 'spouse'],
  ['Sibling', 'sibling'],
  ['Grandchild', 'grandchild'],
  ['Nephew or Niece', 'nephew_niece'],
  ['Friend', 'friend'],
  ['Self', 'self'],
  ['Hospital Staff', 'hospital_staff'],
  ['Other', 'other'],
]);

var GENDER_MAP = buildLookup([
  ['Male', 'male'],
  ['Female', 'female'],
  ['Other', 'other'],
]);

var CONDITIONS_MAP = buildLookup([
  ["Alzheimer's", 'alzheimers'],
  ['Dementia', 'dementia'],
  ["Parkinson's", 'parkinsons'],
  ['Cancer', 'cancer'],
  ['Post-Stroke', 'post_stroke'],
  ['Post-Operative', 'post_operative'],
  ['Post-Transplant', 'post_transplant'],
  ['Bedridden', 'bedridden'],
  ['Diabetes', 'diabetes'],
  ['Cardiac', 'cardiac'],
  ['Mobility Impaired', 'mobility_impaired'],
  ['Other', 'other'],
]);

var SERVICE_MAP = buildLookup([
  ['Assisted Living', 'assisted_living'],
  ['Palliative Care', 'palliative_care'],
  ['Post-Transplant Care', 'post_transplant_care'],
  ['Cancer Care', 'cancer_care'],
  ['Medical Recovery', 'medical_recovery'],
  ['Dementia Care', 'dementia_care'],
  ['Respite Care', 'respite_care'],
  ['Day Care', 'day_care'],
]);

var ACCOMMODATION_MAP = buildLookup([
  ['Single Room', 'single_room'],
  ['Double Sharing', 'double_sharing'],
  ['Triple Sharing', 'triple_sharing'],
  ['Full Flat', 'full_flat'],
  ['Dormitory', 'dormitory'],
  ['Not Sure', 'not_sure'],
]);

var AMENITY_MAP = buildLookup([
  ['AC', 'ac'],
  ['Lift', 'lift'],
  ['Attached Bathroom', 'attached_bathroom'],
  ['Ground Floor', 'ground_floor'],
  ['Female Attendant', 'female_attendant'],
  ['Private Nurse', 'private_nurse'],
  ['Veg Food', 'veg_food'],
  ['Other', 'other'],
]);

function mapSingle(lookup, rawValue) {
  if (!rawValue) return null;
  var key = normalize(rawValue);
  return lookup[key] || null;
}

function mapMulti(lookup, rawCombined) {
  if (!rawCombined) return [];
  return String(rawCombined)
    .split(',')
    .map(function (part) { return part.trim(); })
    .filter(function (part) { return part.length > 0; })
    .map(function (part) { return mapSingle(lookup, part); })
    .filter(function (value) { return value !== null; });
}

function getAnswer(namedValues, title) {
  var values = namedValues[title];
  if (!values || values.length === 0) return '';
  return values[0];
}

function formatDate(rawDate) {
  var parsed = new Date(rawDate);
  if (isNaN(parsed.getTime())) return null;
  var yyyy = parsed.getFullYear();
  var mm = String(parsed.getMonth() + 1).padStart(2, '0');
  var dd = String(parsed.getDate()).padStart(2, '0');
  return yyyy + '-' + mm + '-' + dd;
}

function logError(message, context) {
  console.error(message, JSON.stringify(context));
}

/** Installable trigger target -- do not rename without updating setupTrigger(). */
function handleFormSubmit(e) {
  try {
    var nv = e.namedValues;

    var countryCodeRaw = getAnswer(nv, 'Country code');
    var countryCode = COUNTRY_CODES.indexOf(countryCodeRaw) !== -1 ? countryCodeRaw : '+91';

    var ageRaw = getAnswer(nv, "Patient's age");
    var age = ageRaw ? parseInt(ageRaw, 10) : null;
    if (age !== null && isNaN(age)) age = null;

    var budgetMinRaw = getAnswer(nv, 'Budget - minimum (Rs.)');
    var budgetMin = budgetMinRaw ? parseFloat(budgetMinRaw) : null;
    if (budgetMin !== null && isNaN(budgetMin)) budgetMin = null;

    var budgetMaxRaw = getAnswer(nv, 'Budget - maximum (Rs.)');
    var budgetMax = budgetMaxRaw ? parseFloat(budgetMaxRaw) : null;
    if (budgetMax !== null && isNaN(budgetMax)) budgetMax = null;

    var visitDateRaw = getAnswer(nv, 'Preferred visit date (if known)');
    var plannedVisitDate = visitDateRaw ? formatDate(visitDateRaw) : null;

    var payload = {
      contact_channel: mapSingle(CONTACT_CHANNEL_MAP, getAnswer(nv, 'How did they first contact us?')),
      how_heard: mapSingle(HOW_HEARD_MAP, getAnswer(nv, 'How did you hear about Kalaza Care?')),
      how_heard_detail: getAnswer(nv, 'If referral or other, please give details') || null,

      enquirer_name: getAnswer(nv, 'Your name'),
      enquirer_country_code: countryCode,
      enquirer_phone: getAnswer(nv, 'Your phone number (10 digits)'),
      enquirer_relation: mapSingle(RELATION_MAP, getAnswer(nv, 'Your relation to the patient')),
      enquirer_location: getAnswer(nv, 'Where are you from (location)') || null,

      patient_name: getAnswer(nv, "Patient's name") || null,
      patient_age: age,
      patient_gender: mapSingle(GENDER_MAP, getAnswer(nv, "Patient's gender")),
      patient_conditions: mapMulti(CONDITIONS_MAP, getAnswer(nv, 'What does the patient have? (select all that apply)')),
      current_condition: getAnswer(nv, 'Current condition / mobility') || null,
      medical_history: getAnswer(nv, 'Medical history') || null,

      service_wanted: mapMulti(SERVICE_MAP, getAnswer(nv, 'Service(s) wanted (select all that apply)')),
      accommodation_type: mapSingle(ACCOMMODATION_MAP, getAnswer(nv, 'Room type preferred')),
      budget_min: budgetMin,
      budget_max: budgetMax,
      amenities_requested: mapMulti(AMENITY_MAP, getAnswer(nv, 'Amenities requested (select all that apply)')),
      special_requirements: getAnswer(nv, 'Any special requirements') || null,
      queries: getAnswer(nv, 'What would you like to ask us?') || null,
      comments: getAnswer(nv, "Anything else you'd like to add") || null,

      planned_visit_date: plannedVisitDate,
    };

    if (!payload.enquirer_name || !payload.enquirer_phone) {
      logError('Missing required field(s) -- enquirer_name or enquirer_phone blank. Skipped insert.', payload);
      return;
    }

    insertLead(payload);
  } catch (err) {
    logError('handleFormSubmit threw: ' + err, e);
  }
}

function insertLead(payload) {
  var url = CONFIG.SUPABASE_URL + '/rest/v1/leads';
  var options = {
    method: 'post',
    contentType: 'application/json',
    headers: {
      apikey: CONFIG.SUPABASE_ANON_KEY,
      Authorization: 'Bearer ' + CONFIG.SUPABASE_ANON_KEY,
      Prefer: 'return=minimal',
    },
    payload: JSON.stringify(payload),
    muteHttpExceptions: true,
  };

  var response = UrlFetchApp.fetch(url, options);
  var code = response.getResponseCode();
  if (code < 200 || code >= 300) {
    logError('Supabase insert failed (HTTP ' + code + '): ' + response.getContentText(), payload);
  }
}

/** Run this once, manually, from the Apps Script editor to wire up the trigger. */
function setupTrigger() {
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  ScriptApp.getProjectTriggers().forEach(function (t) {
    if (t.getHandlerFunction() === 'handleFormSubmit') {
      ScriptApp.deleteTrigger(t);
    }
  });
  ScriptApp.newTrigger('handleFormSubmit')
    .forSpreadsheet(ss)
    .onFormSubmit()
    .create();
}
