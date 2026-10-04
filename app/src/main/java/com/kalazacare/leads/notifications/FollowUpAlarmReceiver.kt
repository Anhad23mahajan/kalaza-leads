package com.kalazacare.leads.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

private const val UNIQUE_WORK_NAME = "follow_up_reminder_run"

/**
 * Fired by the exact alarm set in [NotificationScheduler]. A `BroadcastReceiver`'s `onReceive`
 * can't safely do the network call itself (strict time limit, no guarantee the process survives),
 * so this just hands off to a one-time `WorkManager` job, which does the actual Supabase check,
 * shows the notification, and -- critically -- schedules tomorrow's alarm before finishing.
 *
 * The job needs a network connection: if the phone is offline at reminder time WorkManager holds
 * it until connectivity returns, so the reminder arrives late instead of being lost. It is
 * unique + REPLACE so a stale job still waiting from yesterday can't pile up behind today's.
 */
class FollowUpAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val request = OneTimeWorkRequestBuilder<FollowUpReminderWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build(),
            )
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.REPLACE, request)
    }
}
