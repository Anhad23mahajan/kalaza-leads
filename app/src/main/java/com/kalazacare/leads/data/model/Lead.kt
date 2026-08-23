package com.kalazacare.leads.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Lead(
    val id: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("assigned_staff_id") val assignedStaffId: String? = null,
    @SerialName("source_channel") val sourceChannel: String? = null,
    @SerialName("how_heard") val howHeard: String? = null,
    @SerialName("enquirer_name") val enquirerName: String,
    @SerialName("enquirer_phone") val enquirerPhone: String,
    @SerialName("enquirer_relation") val enquirerRelation: String? = null,
    @SerialName("patient_name") val patientName: String? = null,
    @SerialName("patient_age") val patientAge: Int? = null,
    val location: String? = null,
    @SerialName("service_wanted") val serviceWanted: String? = null,
    @SerialName("room_preference") val roomPreference: String? = null,
    val budget: Double? = null,
    @SerialName("medical_history") val medicalHistory: String? = null,
    @SerialName("specific_requirements") val specificRequirements: String? = null,
    val status: String = "NEW",
    @SerialName("next_follow_up_date") val nextFollowUpDate: String? = null,
)

/** Insert payload — omits server-assigned fields (id, created_at, status defaults itself). */
@Serializable
data class NewLeadRequest(
    @SerialName("source_channel") val sourceChannel: String? = null,
    @SerialName("enquirer_name") val enquirerName: String,
    @SerialName("enquirer_phone") val enquirerPhone: String,
    @SerialName("enquirer_relation") val enquirerRelation: String? = null,
    @SerialName("patient_name") val patientName: String? = null,
    @SerialName("patient_age") val patientAge: Int? = null,
    val location: String? = null,
    @SerialName("service_wanted") val serviceWanted: String? = null,
    val budget: Double? = null,
)
