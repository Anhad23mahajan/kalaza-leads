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

/** Makes titles comparable: drops a leading question number ("12. ") and treats curly apostrophes as straight. */
function stripQuestionNumber(title) {
  return String(title || '').replace(/[\u2018\u2019]/g, "'").replace(/^\s*\d+\s*[.)]\s*/, '').trim();
}

function getAnswer(namedValues, title) {
  var wanted = stripQuestionNumber(title);
  for (var key in namedValues) {
    if (stripQuestionNumber(key) === wanted) {
      var values = namedValues[key];
      if (!values || values.length === 0) return '';
      return values[0];
    }
  }
  return '';
}

function pad2(n) {
  return String(n).padStart(2, '0');
}

/** Parses date TEXT defensively (last resort). d/m/yyyy is read day-first (India). Returns 'yyyy-mm-dd' or null. */
function parseDateString(raw) {
  var s = String(raw || '').trim();
  if (!s) return null;
  if (/^\d{4}-\d{2}-\d{2}$/.test(s)) return s;
  var m = s.match(/^(\d{1,2})[\/.\-](\d{1,2})[\/.\-](\d{4})$/);
  if (!m) return null;
  var day = parseInt(m[1], 10), month = parseInt(m[2], 10), year = parseInt(m[3], 10);
  if (month < 1 || month > 12 || day < 1 || day > 31) return null;
  return year + '-' + pad2(month) + '-' + pad2(day);
}

/**
 * Reads a Date-question answer. Prefers the real Date value from the response row, which is immune
 * to the spreadsheet's regional date format (30/09/2026 vs 9/30/2026); falls back to parsing text.
 */
function getDateAnswer(e, title) {
  var wanted = stripQuestionNumber(title);
  try {
    if (e.range) {
      var sheet = e.range.getSheet();
      var cols = sheet.getLastColumn();
      var headers = sheet.getRange(1, 1, 1, cols).getValues()[0];
      var row = sheet.getRange(e.range.getRow(), 1, 1, cols).getValues()[0];
      for (var i = 0; i < headers.length; i++) {
        if (stripQuestionNumber(headers[i]) === wanted) {
          var v = row[i];
          if (Object.prototype.toString.call(v) === '[object Date]' && !isNaN(v.getTime())) {
            return Utilities.formatDate(v, sheet.getParent().getSpreadsheetTimeZone(), 'yyyy-MM-dd');
          }
          return parseDateString(v);
        }
      }
    }
  } catch (err) {
    logError('getDateAnswer failed for ' + title + ': ' + err, null);
  }
  return parseDateString(getAnswer(e.namedValues, title));
}

function logError(message, context) {
  console.error(message, JSON.stringify(context));
}

/**
 * Reads an amount a family typed into a free-text box: "20000", "20,000", "1,00,000" (Indian
 * grouping), "Rs. 25000", "30k", "1.5 lakh", "2 lac", "1 crore". Returns a number, or null when no
 * usable number is found. If a range is typed ("20000-30000") the first number is used.
 * (parseFloat alone turned "20,000" into 20 and "1,00,000" into 1.)
 */
function parseAmount(raw) {
  if (raw === null || raw === undefined) return null;
  var s = String(raw).toLowerCase().replace(/,/g, '');
  // (?!\d) stops the number being cut short; the unit carries its own (?![a-z]) so "25000rs" is 25000, not 2500.
  var m = s.match(/(\d+(?:\.\d+)?)(?!\d)\s*(?:(thousand|lakhs?|lacs?|crores?|cr|k|l)(?![a-z]))?/);
  if (!m) return null;
  var value = parseFloat(m[1]);
  if (isNaN(value)) return null;
  var unit = m[2] || '';
  if (unit === 'k' || unit === 'thousand') value *= 1000;
  else if (unit === 'l' || /^lakh|^lac/.test(unit)) value *= 100000;
  else if (unit === 'cr' || /^crore/.test(unit)) value *= 10000000;
  return value;
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

    var budgetMin = parseAmount(getAnswer(nv, 'Budget - minimum (Rs.)'));
    var budgetMax = parseAmount(getAnswer(nv, 'Budget - maximum (Rs.)'));

    var plannedVisitDate = getDateAnswer(e, 'Preferred visit date (if known)');
    var nextFollowUpDate = getDateAnswer(e, 'When would you like us to follow up with you?');
    var actualVisitDate = getDateAnswer(e, 'If you have already visited us, on which date?');

    var payload = {
      // Renamed 2026-09-25 from 'How did they first contact us?'; the old title is kept as a fallback.
      contact_channel: mapSingle(CONTACT_CHANNEL_MAP, getAnswer(nv, 'How did you first contact us?') || getAnswer(nv, 'How did they first contact us?')),
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
      next_follow_up_date: nextFollowUpDate,
      actual_visit_date: actualVisitDate,
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
