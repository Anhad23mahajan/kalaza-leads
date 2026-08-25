package com.kalazacare.leads.data.repository

import com.kalazacare.leads.data.model.Lead
import com.kalazacare.leads.data.model.NewLeadRequest
import com.kalazacare.leads.data.model.UpdateLeadRequest

interface LeadsRepository {
    suspend fun getLeads(): Result<List<Lead>>
    suspend fun addLead(newLead: NewLeadRequest): Result<Lead>
    suspend fun updateLead(id: String, update: UpdateLeadRequest): Result<Lead>
}
