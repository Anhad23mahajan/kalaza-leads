package com.kalazacare.leads.data.repository

import android.util.Log
import com.kalazacare.leads.data.model.Lead
import com.kalazacare.leads.data.model.NewLeadRequest
import com.kalazacare.leads.data.model.UpdateLeadRequest
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

private const val TAG = "KalazaLeadsRepo"

class SupabaseLeadsRepository(private val client: SupabaseClient) : LeadsRepository {

    override suspend fun getLeads(): Result<List<Lead>> = try {
        val leads = client.postgrest.from("leads")
            .select {
                order("created_at", Order.DESCENDING)
            }
            .decodeList<Lead>()
        Log.d(TAG, "getLeads: fetched ${leads.size} leads")
        Result.success(leads)
    } catch (e: Exception) {
        Log.e(TAG, "getLeads failed", e)
        Result.failure(e)
    }

    override suspend fun addLead(newLead: NewLeadRequest): Result<Lead> = try {
        val inserted = client.postgrest.from("leads")
            .insert(newLead) {
                select()
            }
            .decodeSingle<Lead>()
        Log.d(TAG, "addLead: created lead ${inserted.id}")
        Result.success(inserted)
    } catch (e: Exception) {
        Log.e(TAG, "addLead failed", e)
        Result.failure(e)
    }

    override suspend fun updateLead(id: String, update: UpdateLeadRequest): Result<Lead> = try {
        val updated = client.postgrest.from("leads")
            .update(update) {
                filter {
                    eq("id", id)
                }
                select()
            }
            .decodeSingle<Lead>()
        Log.d(TAG, "updateLead: updated lead $id")
        Result.success(updated)
    } catch (e: Exception) {
        Log.e(TAG, "updateLead failed", e)
        Result.failure(e)
    }
}
