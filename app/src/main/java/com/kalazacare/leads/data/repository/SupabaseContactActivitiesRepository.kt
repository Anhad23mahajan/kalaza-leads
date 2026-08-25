package com.kalazacare.leads.data.repository

import android.util.Log
import com.kalazacare.leads.data.model.ContactActivity
import com.kalazacare.leads.data.model.NewContactActivityRequest
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

private const val TAG = "KalazaActivitiesRepo"

class SupabaseContactActivitiesRepository(
    private val client: SupabaseClient,
) : ContactActivitiesRepository {

    override suspend fun getForLead(leadId: String): Result<List<ContactActivity>> = try {
        val activities = client.postgrest.from("contact_activities")
            .select {
                filter {
                    eq("lead_id", leadId)
                }
                order("occurred_at", Order.DESCENDING)
            }
            .decodeList<ContactActivity>()
        Log.d(TAG, "getForLead: fetched ${activities.size} activities for lead $leadId")
        Result.success(activities)
    } catch (e: Exception) {
        Log.e(TAG, "getForLead failed", e)
        Result.failure(e)
    }

    override suspend fun addActivity(newActivity: NewContactActivityRequest): Result<ContactActivity> = try {
        val inserted = client.postgrest.from("contact_activities")
            .insert(newActivity) {
                select()
            }
            .decodeSingle<ContactActivity>()
        Log.d(TAG, "addActivity: logged activity ${inserted.id} for lead ${inserted.leadId}")
        Result.success(inserted)
    } catch (e: Exception) {
        Log.e(TAG, "addActivity failed", e)
        Result.failure(e)
    }
}
