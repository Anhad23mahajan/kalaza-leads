package com.kalazacare.leads.data.model

import kotlinx.serialization.Serializable

@Serializable
data class StaffMember(
    val id: String,
    val name: String,
    val email: String,
)
