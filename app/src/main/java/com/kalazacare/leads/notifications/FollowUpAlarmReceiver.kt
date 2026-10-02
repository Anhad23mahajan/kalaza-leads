package com.kalazacare.leads.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

/**
 * Fired by the exact alarm set in [NotificationScheduler]. A `BroadcastReceiver`'s `onReceive`
 * can't safely do the network call itself (strict time limit, no guarantee the process survives),
 * so this just hands off to a one-time `WorkManager` job, which does the actual Supabase check,
 * shows the notification, and -- critically -- schedules tomorrow's alarm before finishing.
 */
class FollowUpAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val request = OneTimeWorkRequestBuilder<FollowUpReminderWorker>().build()
        WorkManager.getInstance(context).enqueue(request)
    }
}
