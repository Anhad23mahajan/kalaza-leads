package com.kalazacare.leads.notifications

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kalazacare.leads.data.model.Lead
import com.kalazacare.leads.data.remote.SupabaseClients
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.postgrest
import java.time.LocalDate

private const val TAG = "FollowUpReminderWorker"
private val TERMINAL_STATUSES = setOf("CONVERTED", "NOT_CONVERTED", "DORMANT")

/**
 * Periodic, client-only check for leads with a follow-up due (Master Plan
 * Part 6.5 / roadmap A4 part 2). Deliberately NOT a real push notification --
 * that needs a server, which doesn't exist until Track D. This covers what's
 * possible today: the app noticing its own data crossed a date.
 */
class FollowUpReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val client = SupabaseClients.main
        return try {
            client.auth.awaitInitialization()
            if (client.auth.sessionStatus.value !is SessionStatus.Authenticated) {
                Log.d(TAG, "doWork: no authenticated session, skipping")
                return Result.success()
            }

            val today = LocalDate.now().toString()
            val dueCount = client.postgrest.from("leads")
                .select {
                    filter { lte("next_follow_up_date", today) }
                }
                .decodeList<Lead>()
                .count { it.status !in TERMINAL_STATUSES }

            Log.d(TAG, "doWork: $dueCount follow-ups due")
            if (dueCount > 0) {
                NotificationHelper.showFollowUpReminder(applicationContext, dueCount)
            }
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "doWork failed", e)
            Result.retry()
        }
    }
}
