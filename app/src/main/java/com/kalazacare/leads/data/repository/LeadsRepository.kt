package com.kalazacare.leads.data.repository

import com.kalazacare.leads.data.model.Lead
import com.kalazacare.leads.data.model.UpdateLeadRequest

interface LeadsRepository {
    suspend fun getLeads(): Result<List<Lead>>
    suspend fun updateLead(id: String, update: UpdateLeadRequest): Result<Lead>
    suspend fun deleteLead(id: String): Result<Unit>
}
