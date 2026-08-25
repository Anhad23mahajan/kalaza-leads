package com.kalazacare.leads.data.repository

import com.kalazacare.leads.data.model.ContactActivity
import com.kalazacare.leads.data.model.NewContactActivityRequest

interface ContactActivitiesRepository {
    suspend fun getForLead(leadId: String): Result<List<ContactActivity>>
    suspend fun addActivity(newActivity: NewContactActivityRequest): Result<ContactActivity>
}
