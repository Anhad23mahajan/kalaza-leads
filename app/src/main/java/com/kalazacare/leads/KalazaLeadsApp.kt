package com.kalazacare.leads

import android.app.Application
import com.kalazacare.leads.data.remote.SupabaseClients
import com.kalazacare.leads.notifications.NotificationScheduler

/**
 * Application class. Repositories (see data/repository/) are constructed in
 * MainActivity.onCreate, not here — this class only does app-wide startup work.
 */
class KalazaLeadsApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // First, so a crash anywhere below still lands on our crash screen.
        CrashHandler.install(this)
        // Touching SupabaseClients.main here (rather than waiting for first repository
        // use) surfaces a misconfigured local.properties immediately on app start,
        // rather than on the first screen that happens to need data.
        SupabaseClients.main

        // Local (device-only) daily check for leads with a follow-up due. Re-arms the next
        // exact alarm on every app start (cheap and idempotent) as a backstop alongside the
        // boot receiver and the worker's own self-rescheduling. No-ops quietly if nobody's
        // logged in yet when the alarm actually fires -- see FollowUpReminderWorker.
        NotificationScheduler.scheduleNext(this)
    }
}
