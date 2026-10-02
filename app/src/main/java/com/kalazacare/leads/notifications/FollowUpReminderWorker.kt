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
 * One-shot check for leads with a follow-up due, triggered by the exact alarm in
 * [NotificationScheduler] via [FollowUpAlarmReceiver]. Deliberately NOT a real push
 * notification -- that would need a server, and this project has none (the WhatsApp
 * automation idea that would have needed one was abandoned, see docs/HANDOFF.md §15).
 * This covers what's possible today: the app noticing its own data crossed a date.
 *
 * Always reschedules tomorrow's alarm before finishing (success or failure) -- otherwise
 * a single failed run would silently end the whole daily reminder chain.
 */
class FollowUpReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            checkAndNotify()
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "doWork failed", e)
            Result.failure()
        } finally {
            NotificationScheduler.scheduleNext(applicationContext)
        }
    }

    private suspend fun checkAndNotify() {
        val client = SupabaseClients.main
        client.auth.awaitInitialization()
        if (client.auth.sessionStatus.value !is SessionStatus.Authenticated) {
            Log.d(TAG, "checkAndNotify: no authenticated session, skipping")
            return
        }

        val today = LocalDate.now().toString()
        val dueLeads = client.postgrest.from("leads")
            .select {
                filter { lte("next_follow_up_date", today) }
            }
            .decodeList<Lead>()
            .filter { it.status !in TERMINAL_STATUSES }
            .sortedBy { it.nextFollowUpDate }

        Log.d(TAG, "checkAndNotify: ${dueLeads.size} follow-ups due")
        if (dueLeads.isNotEmpty()) {
            NotificationHelper.showFollowUpReminder(applicationContext, dueLeads)
        }
    }
}
