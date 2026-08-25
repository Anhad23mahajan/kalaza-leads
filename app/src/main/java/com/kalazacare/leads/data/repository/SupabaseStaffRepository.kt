package com.kalazacare.leads.data.repository

import android.util.Log
import com.kalazacare.leads.data.model.NewStaffRequest
import com.kalazacare.leads.data.model.StaffMember
import com.kalazacare.leads.data.model.UpdateStaffRequest
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

private const val TAG = "KalazaStaffRepo"

class SupabaseStaffRepository(private val client: SupabaseClient) : StaffRepository {

    override suspend fun getStaff(): Result<List<StaffMember>> = try {
        val staff = client.postgrest.from("staff")
            .select {
                order("name", Order.ASCENDING)
            }
            .decodeList<StaffMember>()
        Log.d(TAG, "getStaff: fetched ${staff.size} staff")
        Result.success(staff)
    } catch (e: Exception) {
        Log.e(TAG, "getStaff failed", e)
        Result.failure(e)
    }

    override suspend fun addStaff(newStaff: NewStaffRequest): Result<StaffMember> = try {
        val inserted = client.postgrest.from("staff")
            .insert(newStaff) {
                select()
            }
            .decodeSingle<StaffMember>()
        Log.d(TAG, "addStaff: created staff ${inserted.id}")
        Result.success(inserted)
    } catch (e: Exception) {
        Log.e(TAG, "addStaff failed", e)
        Result.failure(e)
    }

    override suspend fun updateStaff(id: String, update: UpdateStaffRequest): Result<StaffMember> = try {
        val updated = client.postgrest.from("staff")
            .update(update) {
                filter {
                    eq("id", id)
                }
                select()
            }
            .decodeSingle<StaffMember>()
        Log.d(TAG, "updateStaff: updated staff $id")
        Result.success(updated)
    } catch (e: Exception) {
        Log.e(TAG, "updateStaff failed", e)
        Result.failure(e)
    }
}
