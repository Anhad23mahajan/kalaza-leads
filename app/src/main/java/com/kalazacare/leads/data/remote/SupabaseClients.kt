package com.kalazacare.leads.data.remote

import com.kalazacare.leads.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

/**
 * The anon key is safe to embed client-side once Row-Level Security is enabled on every
 * table (same pattern as Kalaza Care) — every request it makes is still subject to those
 * policies. It is intentionally NOT hardcoded here: it's injected at build time from
 * local.properties (gitignored) via BuildConfig fields set in app/build.gradle.kts.
 * See local.properties.example at the repo root and docs/PROJECT_SPEC.md section 9.
 *
 * Until local.properties is filled in, these are empty strings — createSupabaseClient
 * will fail loudly at first use rather than silently, which is preferable to a client
 * that looks configured but isn't.
 */
object SupabaseClients {
    val main: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY,
        ) {
            install(Auth)
            install(Postgrest)
            install(Realtime)
        }
    }
}
