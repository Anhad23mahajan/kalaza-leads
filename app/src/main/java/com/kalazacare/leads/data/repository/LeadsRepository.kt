package com.kalazacare.leads.data.repository

import com.kalazacare.leads.data.model.Lead
import com.kalazacare.leads.data.model.NewLeadRequest

interface LeadsRepository {
    suspend fun getLeads(): Result<List<Lead>>
    suspend fun addLead(newLead: NewLeadRequest): Result<Lead>
}
