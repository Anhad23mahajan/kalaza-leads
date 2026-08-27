package com.kalazacare.leads.notifications

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

private const val WORK_NAME = "follow_up_reminder_check"
private const val REMINDER_HOUR = 9

object NotificationScheduler {

    fun schedule(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<FollowUpReminderWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setInitialDelay(millisUntilNextReminderTime(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request,
        )
    }

    private fun millisUntilNextReminderTime(): Long {
        val now = LocalDateTime.now()
        var next = now.toLocalDate().atTime(REMINDER_HOUR, 0)
        if (!now.isBefore(next)) {
            next = next.plusDays(1)
        }
        return Duration.between(now, next).toMillis()
    }
}
