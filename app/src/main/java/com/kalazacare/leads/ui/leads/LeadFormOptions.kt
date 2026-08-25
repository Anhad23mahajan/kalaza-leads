package com.kalazacare.leads.ui.leads

// Shared enum option lists + display labels for both AddLeadScreen and LeadDetailScreen.
// Keep in sync with docs/sql/002_leads_v2_migration.sql's check constraints.

val CONTACT_CHANNELS = listOf("phone_call", "whatsapp", "walk_in", "website", "email", "instagram_dm")
val CONTACT_CHANNEL_LABELS = mapOf(
    "phone_call" to "Phone Call", "whatsapp" to "WhatsApp", "walk_in" to "Walk-in",
    "website" to "Website", "email" to "Email", "instagram_dm" to "Instagram DM",
)

val HOW_HEARD = listOf(
    "google_search", "google_maps", "instagram", "facebook",
    "referral_friend_family", "referral_hospital", "referral_doctor",
    "passing_by", "newspaper", "other",
)
val HOW_HEARD_LABELS = mapOf(
    "google_search" to "Google Search", "google_maps" to "Google Maps",
    "instagram" to "Instagram", "facebook" to "Facebook",
    "referral_friend_family" to "Referral — Friend/Family", "referral_hospital" to "Referral — Hospital",
    "referral_doctor" to "Referral — Doctor", "passing_by" to "Passing By",
    "newspaper" to "Newspaper", "other" to "Other",
)

val RELATIONS = listOf(
    "son", "daughter", "spouse", "sibling", "grandchild",
    "nephew_niece", "friend", "self", "hospital_staff", "other",
)
val RELATION_LABELS = mapOf(
    "son" to "Son", "daughter" to "Daughter", "spouse" to "Spouse", "sibling" to "Sibling",
    "grandchild" to "Grandchild", "nephew_niece" to "Nephew/Niece", "friend" to "Friend",
    "self" to "Self", "hospital_staff" to "Hospital Staff", "other" to "Other",
)

val GENDERS = listOf("male", "female", "other")
val GENDER_LABELS = mapOf("male" to "Male", "female" to "Female", "other" to "Other")

val CONDITIONS = listOf(
    "alzheimers", "dementia", "parkinsons", "cancer", "post_stroke",
    "post_operative", "post_transplant", "bedridden", "diabetes",
    "cardiac", "mobility_impaired", "other",
)
val CONDITION_LABELS = mapOf(
    "alzheimers" to "Alzheimer's", "dementia" to "Dementia", "parkinsons" to "Parkinson's",
    "cancer" to "Cancer", "post_stroke" to "Post-Stroke", "post_operative" to "Post-Operative",
    "post_transplant" to "Post-Transplant", "bedridden" to "Bedridden", "diabetes" to "Diabetes",
    "cardiac" to "Cardiac", "mobility_impaired" to "Mobility Impaired", "other" to "Other",
)

val SERVICES = listOf(
    "assisted_living", "palliative_care", "post_transplant_care", "cancer_care",
    "medical_recovery", "dementia_care", "respite_care", "day_care",
)
val SERVICE_LABELS = mapOf(
    "assisted_living" to "Assisted Living", "palliative_care" to "Palliative Care",
    "post_transplant_care" to "Post-Transplant Care", "cancer_care" to "Cancer Care",
    "medical_recovery" to "Medical Recovery", "dementia_care" to "Dementia Care",
    "respite_care" to "Respite Care", "day_care" to "Day Care",
)

val ACCOMMODATIONS = listOf(
    "single_room", "double_sharing", "triple_sharing", "full_flat", "dormitory", "not_sure",
)
val ACCOMMODATION_LABELS = mapOf(
    "single_room" to "Single Room", "double_sharing" to "Double Sharing",
    "triple_sharing" to "Triple Sharing", "full_flat" to "Full Flat",
    "dormitory" to "Dormitory", "not_sure" to "Not Sure",
)

val AMENITIES = listOf(
    "ac", "lift", "attached_bathroom", "ground_floor",
    "female_attendant", "private_nurse", "veg_food", "other",
)
val AMENITY_LABELS = mapOf(
    "ac" to "AC", "lift" to "Lift", "attached_bathroom" to "Attached Bathroom",
    "ground_floor" to "Ground Floor", "female_attendant" to "Female Attendant",
    "private_nurse" to "Private Nurse", "veg_food" to "Veg Food", "other" to "Other",
)

val STATUSES = listOf(
    "NEW", "CONTACTED", "INFO_SENT", "VISIT_SCHEDULED", "VISITED",
    "CONSIDERING", "CONVERTED", "NOT_CONVERTED", "DORMANT", "BACKUP",
)
val STATUS_LABELS = mapOf(
    "NEW" to "New", "CONTACTED" to "Contacted", "INFO_SENT" to "Info Sent",
    "VISIT_SCHEDULED" to "Visit Scheduled", "VISITED" to "Visited",
    "CONSIDERING" to "Considering", "CONVERTED" to "Converted",
    "NOT_CONVERTED" to "Not Converted", "DORMANT" to "Dormant", "BACKUP" to "Backup",
)

val NOT_CONVERTED_REASONS = listOf(
    "budget_too_high", "chose_another_facility", "location_too_far",
    "amenity_missing", "service_not_offered", "family_decided_home_care",
    "patient_passed_away", "decision_postponed", "unreachable_no_response",
    "unhappy_after_visit", "other",
)
val NOT_CONVERTED_REASON_LABELS = mapOf(
    "budget_too_high" to "Budget Too High", "chose_another_facility" to "Chose Another Facility",
    "location_too_far" to "Location Too Far", "amenity_missing" to "Amenity Missing",
    "service_not_offered" to "Service Not Offered", "family_decided_home_care" to "Family Chose Home Care",
    "patient_passed_away" to "Patient Passed Away", "decision_postponed" to "Decision Postponed",
    "unreachable_no_response" to "Unreachable / No Response", "unhappy_after_visit" to "Unhappy After Visit",
    "other" to "Other",
)

val COUNTRY_CODES = listOf("+91", "+1", "+44", "+971", "+61")

// Contact activity log (docs/sql/003_contact_activities.sql)
val ACTIVITY_TYPES = listOf("call", "whatsapp", "visit", "email", "sms")
val ACTIVITY_TYPE_LABELS = mapOf(
    "call" to "Call", "whatsapp" to "WhatsApp", "visit" to "Visit",
    "email" to "Email", "sms" to "SMS",
)

val ACTIVITY_DIRECTIONS = listOf("outbound", "inbound")
val ACTIVITY_DIRECTION_LABELS = mapOf("outbound" to "We contacted them", "inbound" to "They contacted us")

val ACTIVITY_OUTCOMES = listOf("positive", "negative", "no_answer", "callback_requested", "not_reachable")
val ACTIVITY_OUTCOME_LABELS = mapOf(
    "positive" to "Positive", "negative" to "Negative", "no_answer" to "No Answer",
    "callback_requested" to "Callback Requested", "not_reachable" to "Not Reachable",
)

// Staff roster (docs/sql/004_staff_table.sql)
val STAFF_ROLES = listOf("admin", "coordinator", "viewer")
val STAFF_ROLE_LABELS = mapOf("admin" to "Admin", "coordinator" to "Coordinator", "viewer" to "Viewer")
