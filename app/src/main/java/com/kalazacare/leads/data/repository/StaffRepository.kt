package com.kalazacare.leads.data.repository

import com.kalazacare.leads.data.model.NewStaffRequest
import com.kalazacare.leads.data.model.StaffMember
import com.kalazacare.leads.data.model.UpdateStaffRequest

interface StaffRepository {
    suspend fun getStaff(): Result<List<StaffMember>>
    suspend fun addStaff(newStaff: NewStaffRequest): Result<StaffMember>
    suspend fun updateStaff(id: String, update: UpdateStaffRequest): Result<StaffMember>
}
