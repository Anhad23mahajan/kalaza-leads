package com.kalazacare.leads.data.repository

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.exceptions.RestException

private const val TAG = "KalazaLeadsAuth"

/**
 * Single-admin auth. The admin logs in by NAME + password; the app synthesizes the
 * Supabase Auth email `{name lowercased, spaces -> underscores}@kalazaleads.app`.
 *
 * There is deliberately NO sign-up path in the app: the one admin account is created in
 * the Supabase dashboard (Authentication -> Users -> Add user) and "Allow new users to
 * sign up" is switched off, so nobody else can ever create an account.
 *
 * The domain must be a TLD Supabase's validator accepts (.internal/.local/.test are
 * rejected with email_address_invalid). No inbox is needed: "Confirm email" is off.
 */
class SupabaseAuthRepository(private val client: SupabaseClient) : AuthRepository {

    override suspend fun login(adminName: String, password: String): Result<String> = try {
        val syntheticEmail = "${adminName.trim().lowercase().replace(" ", "_")}@kalazaleads.app"
        Log.d(TAG, "Attempting signIn for $syntheticEmail")
        client.auth.signInWith(Email) {
            this.email = syntheticEmail
            this.password = password
        }
        Result.success(client.auth.currentUserOrNull()?.id ?: "")
    } catch (e: RestException) {
        Log.e(TAG, "signIn rejected", e)
        Result.failure(Exception("Incorrect name or password."))
    } catch (e: Exception) {
        Log.e(TAG, "signIn failed", e)
        Result.failure(e)
    }

    override suspend fun logout(): Result<Unit> = try {
        client.auth.signOut()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getCurrentUserId(): String? {
        return client.auth.currentUserOrNull()?.id
    }
}
