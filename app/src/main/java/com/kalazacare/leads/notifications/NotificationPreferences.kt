package com.kalazacare.leads.notifications

import android.content.Context

private const val PREFS_NAME = "follow_up_reminder_prefs"
private const val KEY_HOUR = "reminder_hour"
private const val KEY_MINUTE = "reminder_minute"
private const val DEFAULT_HOUR = 9
private const val DEFAULT_MINUTE = 0

/** The supervisor-configurable time of day the follow-up check runs. Defaults to 9:00 AM. */
object NotificationPreferences {

    fun getReminderTime(context: Context): Pair<Int, Int> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val hour = prefs.getInt(KEY_HOUR, DEFAULT_HOUR)
        val minute = prefs.getInt(KEY_MINUTE, DEFAULT_MINUTE)
        return hour to minute
    }

    fun setReminderTime(context: Context, hour: Int, minute: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_HOUR, hour)
            .putInt(KEY_MINUTE, minute)
            .apply()
    }
}
