package com.kalazacare.leads.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContactActivity(
    val id: String? = null,
    @SerialName("lead_id") val leadId: String,
    @SerialName("occurred_at") val occurredAt: String? = null,
    val type: String,
    val direction: String,
    val outcome: String? = null,
    @SerialName("callback_on") val callbackOn: String? = null,
    val notes: String? = null,
    @SerialName("staff_id") val staffId: String? = null,
    @SerialName("is_automated") val isAutomated: Boolean = false,
)

/** Insert payload for logging a new contact activity. */
@Serializable
data class NewContactActivityRequest(
    @SerialName("lead_id") val leadId: String,
    val type: String,
    val direction: String,
    val outcome: String? = null,
    @SerialName("callback_on") val callbackOn: String? = null,
    val notes: String? = null,
)
