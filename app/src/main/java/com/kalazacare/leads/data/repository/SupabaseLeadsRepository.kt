package com.kalazacare.leads.data.repository

import android.util.Log
import com.kalazacare.leads.data.model.Lead
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
        // Info, not debug: some phones (Vivo) drop debug-level app logs entirely.
        Log.i(TAG, "getLeads: fetched ${leads.size} leads")
        Result.success(leads)
    } catch (e: Exception) {
        Log.e(TAG, "getLeads failed", e)
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

    override suspend fun deleteLead(id: String): Result<Unit> = try {
        // select() returns the deleted rows: RLS silently filters a delete it doesn't allow
        // (0 rows, no error), so "nothing came back" has to be treated as a failure.
        val deleted = client.postgrest.from("leads")
            .delete {
                filter { eq("id", id) }
                select()
            }
            .decodeList<Lead>()
        if (deleted.isEmpty()) {
            Log.e(TAG, "deleteLead: no row deleted for $id")
            Result.failure(IllegalStateException("This lead was not found. It may already have been deleted."))
        } else {
            Log.d(TAG, "deleteLead: deleted lead $id")
            Result.success(Unit)
        }
    } catch (e: Exception) {
        Log.e(TAG, "deleteLead failed", e)
        Result.failure(e)
    }
}
