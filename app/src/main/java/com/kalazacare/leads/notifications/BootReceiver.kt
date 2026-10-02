package com.kalazacare.leads.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Exact alarms don't survive a reboot -- re-arm the next one as soon as the device comes back up. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            NotificationScheduler.scheduleNext(context)
        }
    }
}
