package com.kalazacare.leads.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StaffMember(
    val id: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    val name: String,
    val phone: String? = null,
    val role: String = "coordinator",
    @SerialName("is_active") val isActive: Boolean = true,
)

@Serializable
data class NewStaffRequest(
    val name: String,
    val phone: String? = null,
    val role: String = "coordinator",
)

@Serializable
data class UpdateStaffRequest(
    val name: String,
    val phone: String? = null,
    val role: String,
    @SerialName("is_active") val isActive: Boolean,
)
