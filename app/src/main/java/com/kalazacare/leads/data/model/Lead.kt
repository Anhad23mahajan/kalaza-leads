package com.kalazacare.leads.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Lead(
    val id: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,

    @SerialName("enquiry_date") val enquiryDate: String? = null,
    @SerialName("contact_channel") val contactChannel: String? = null,
    @SerialName("how_heard") val howHeard: String? = null,
    @SerialName("how_heard_detail") val howHeardDetail: String? = null,

    @SerialName("enquirer_name") val enquirerName: String,
    @SerialName("enquirer_country_code") val enquirerCountryCode: String = "+91",
    @SerialName("enquirer_phone") val enquirerPhone: String,
    @SerialName("enquirer_relation") val enquirerRelation: String? = null,
    @SerialName("enquirer_location") val enquirerLocation: String? = null,

    @SerialName("patient_name") val patientName: String? = null,
    @SerialName("patient_age") val patientAge: Int? = null,
    @SerialName("patient_gender") val patientGender: String? = null,
    @SerialName("patient_conditions") val patientConditions: List<String> = emptyList(),
    @SerialName("patient_condition_notes") val patientConditionNotes: String? = null,
    @SerialName("current_condition") val currentCondition: String? = null,
    @SerialName("medical_history") val medicalHistory: String? = null,

    @SerialName("service_wanted") val serviceWanted: List<String> = emptyList(),
    @SerialName("accommodation_type") val accommodationType: String? = null,
    @SerialName("budget_min") val budgetMin: Double? = null,
    @SerialName("budget_max") val budgetMax: Double? = null,
    @SerialName("budget_notes") val budgetNotes: String? = null,
    @SerialName("amenities_requested") val amenitiesRequested: List<String> = emptyList(),
    @SerialName("special_requirements") val specialRequirements: String? = null,
    val queries: String? = null,
    val comments: String? = null,

    val status: String = "NEW",
    @SerialName("assigned_staff_id") val assignedStaffId: String? = null,
    @SerialName("next_follow_up_date") val nextFollowUpDate: String? = null,
    @SerialName("follow_up_count") val followUpCount: Int = 0,
    @SerialName("price_list_shared") val priceListShared: Boolean = false,
    @SerialName("price_list_shared_at") val priceListSharedAt: String? = null,
    @SerialName("info_packs_sent") val infoPacksSent: List<String> = emptyList(),
    @SerialName("planned_visit_date") val plannedVisitDate: String? = null,
    @SerialName("actual_visit_date") val actualVisitDate: String? = null,
    @SerialName("converted_at") val convertedAt: String? = null,
    @SerialName("days_to_convert") val daysToConvert: Int? = null,

    @SerialName("not_converted_reason") val notConvertedReason: String? = null,
    @SerialName("not_converted_detail") val notConvertedDetail: String? = null,
    @SerialName("feedback_positive_themes") val feedbackPositiveThemes: List<String> = emptyList(),
    @SerialName("feedback_negative_themes") val feedbackNegativeThemes: List<String> = emptyList(),
    @SerialName("final_remarks") val finalRemarks: String? = null,

    @SerialName("consent_given") val consentGiven: Boolean = false,
    @SerialName("opted_out") val optedOut: Boolean = false,
    @SerialName("preferred_language") val preferredLanguage: String = "en",
)

/** Insert payload for the Add Enquiry form — omits server-assigned/pipeline-only fields. */
@Serializable
data class NewLeadRequest(
    @SerialName("enquiry_date") val enquiryDate: String? = null,
    @SerialName("contact_channel") val contactChannel: String? = null,
    @SerialName("how_heard") val howHeard: String? = null,
    @SerialName("how_heard_detail") val howHeardDetail: String? = null,

    @SerialName("enquirer_name") val enquirerName: String,
    @SerialName("enquirer_country_code") val enquirerCountryCode: String = "+91",
    @SerialName("enquirer_phone") val enquirerPhone: String,
    @SerialName("enquirer_relation") val enquirerRelation: String? = null,
    @SerialName("enquirer_location") val enquirerLocation: String? = null,

    @SerialName("patient_name") val patientName: String? = null,
    @SerialName("patient_age") val patientAge: Int? = null,
    @SerialName("patient_gender") val patientGender: String? = null,
    @SerialName("patient_conditions") val patientConditions: List<String> = emptyList(),
    @SerialName("current_condition") val currentCondition: String? = null,
    @SerialName("medical_history") val medicalHistory: String? = null,

    @SerialName("service_wanted") val serviceWanted: List<String> = emptyList(),
    @SerialName("accommodation_type") val accommodationType: String? = null,
    @SerialName("budget_min") val budgetMin: Double? = null,
    @SerialName("budget_max") val budgetMax: Double? = null,
    @SerialName("amenities_requested") val amenitiesRequested: List<String> = emptyList(),
    @SerialName("special_requirements") val specialRequirements: String? = null,
    val queries: String? = null,
    val comments: String? = null,

    @SerialName("planned_visit_date") val plannedVisitDate: String? = null,
    @SerialName("next_follow_up_date") val nextFollowUpDate: String? = null,
)

/** Update payload for the Lead Detail edit screen — everything a staffer can change post-save. */
@Serializable
data class UpdateLeadRequest(
    @SerialName("contact_channel") val contactChannel: String? = null,
    @SerialName("how_heard") val howHeard: String? = null,

    @SerialName("enquirer_name") val enquirerName: String,
    @SerialName("enquirer_country_code") val enquirerCountryCode: String = "+91",
    @SerialName("enquirer_phone") val enquirerPhone: String,
    @SerialName("enquirer_relation") val enquirerRelation: String? = null,
    @SerialName("enquirer_location") val enquirerLocation: String? = null,

    @SerialName("patient_name") val patientName: String? = null,
    @SerialName("patient_age") val patientAge: Int? = null,
    @SerialName("patient_gender") val patientGender: String? = null,
    @SerialName("patient_conditions") val patientConditions: List<String> = emptyList(),
    @SerialName("current_condition") val currentCondition: String? = null,
    @SerialName("medical_history") val medicalHistory: String? = null,

    @SerialName("service_wanted") val serviceWanted: List<String> = emptyList(),
    @SerialName("accommodation_type") val accommodationType: String? = null,
    @SerialName("budget_min") val budgetMin: Double? = null,
    @SerialName("budget_max") val budgetMax: Double? = null,
    @SerialName("amenities_requested") val amenitiesRequested: List<String> = emptyList(),
    @SerialName("special_requirements") val specialRequirements: String? = null,
    val queries: String? = null,
    val comments: String? = null,

    val status: String,
    @SerialName("planned_visit_date") val plannedVisitDate: String? = null,
    @SerialName("actual_visit_date") val actualVisitDate: String? = null,
    @SerialName("next_follow_up_date") val nextFollowUpDate: String? = null,

    @SerialName("not_converted_reason") val notConvertedReason: String? = null,
    @SerialName("not_converted_detail") val notConvertedDetail: String? = null,
    @SerialName("final_remarks") val finalRemarks: String? = null,
)
