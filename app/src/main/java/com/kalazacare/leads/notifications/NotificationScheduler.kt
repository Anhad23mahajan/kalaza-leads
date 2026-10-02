package com.kalazacare.leads.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.time.LocalDateTime
import java.time.ZoneId

private const val TAG = "NotificationScheduler"
private const val REQUEST_CODE = 1001

/**
 * Schedules the next follow-up check as a single exact alarm, not a periodic job.
 *
 * This replaced a `WorkManager` periodic-work approach (24h interval) that Android does not
 * run at a fixed clock time by design -- it batches/defers background jobs to save battery,
 * and the drift compounds run over run (worse on aggressive Indian OEM battery managers).
 * This fires a one-shot exact alarm for the supervisor-configured time; the alarm's own
 * receiver immediately re-schedules the *next* one (self-rescheduling), so there is always
 * exactly one alarm pending. See docs/ROADMAP.md §3.
 *
 * Honest limitation: even an exact alarm can still be delayed if the phone's battery saver
 * hasn't whitelisted this app -- see SettingsScreen for the one-time toggle that helps with
 * that. "Far more reliable than the old approach," not "guaranteed to the minute."
 */
object NotificationScheduler {

    /** Call whenever the configured time changes, on app startup, and after each alarm fires. */
    fun scheduleNext(context: Context) {
        val (hour, minute) = NotificationPreferences.getReminderTime(context)
        val next = nextOccurrence(hour, minute)
        val triggerAtMillis = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val pendingIntent = alarmPendingIntent(context)

        val canScheduleExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            alarmManager.canScheduleExactAlarms()

        if (canScheduleExact) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } else {
            Log.w(TAG, "scheduleNext: exact-alarm permission not granted, falling back to inexact")
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    /** Whether the app can currently schedule exact alarms (always true below Android 12). */
    fun canScheduleExactAlarms(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        return alarmManager.canScheduleExactAlarms()
    }

    private fun alarmPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, FollowUpAlarmReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun nextOccurrence(hour: Int, minute: Int): LocalDateTime {
        val now = LocalDateTime.now()
        var next = now.toLocalDate().atTime(hour, minute)
        if (!now.isBefore(next)) {
            next = next.plusDays(1)
        }
        return next
    }
}
