package com.kalazacare.leads

import android.app.Application
import com.kalazacare.leads.data.remote.SupabaseClients

/**
 * Application class. Repositories (LeadRepository, ContactActivityRepository, etc. —
 * see docs/PROJECT_SPEC.md section 7 for the data model) get wired up here as they're
 * built, following the same pattern Kalaza Care uses in its own Application class.
 */
class KalazaLeadsApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Touching SupabaseClients.main here (rather than waiting for first repository
        // use) surfaces a misconfigured local.properties immediately on app start,
        // rather than on the first screen that happens to need data.
        SupabaseClients.main
    }
}
