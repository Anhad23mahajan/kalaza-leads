package com.kalazacare.leads.data.repository

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.exceptions.RestException

private const val TAG = "KalazaLeadsAuth"

/**
 * Supabase auth implementation. Staff log in by NAME, not email.
 * Synthesizes an email (staff_name@kalazaleads.app) for Supabase Auth.
 *
 * The domain must be a TLD Supabase's signup validator accepts — reserved/example
 * TLDs like .internal, .local, .test get rejected outright with email_address_invalid.
 * No real inbox is needed since "Confirm email" is turned off for this project.
 *
 * TODO (Phase 2): Replace with the security-definer RPC pattern from Kalaza Care
 * once that repo's auth setup is available. The RPC handles synthesis server-side.
 */
class SupabaseAuthRepository(private val client: SupabaseClient) : AuthRepository {

    override suspend fun login(staffName: String, password: String): Result<String> = try {
        val synthesizedEmail = "${staffName.lowercase().replace(" ", "_")}@kalazaleads.app"
        Log.d(TAG, "Attempting signIn for $synthesizedEmail")

        // Try to sign in. If it fails with "Invalid credentials", fall back to sign up.
        val signInResult = runCatching {
            client.auth.signInWith(Email) {
                this.email = synthesizedEmail
                this.password = password
            }
        }
        Log.d(TAG, "signIn result: success=${signInResult.isSuccess}, error=${signInResult.exceptionOrNull()}")

        if (signInResult.isSuccess) {
            Result.success(client.auth.currentUserOrNull()?.id ?: "")
        } else {
            Log.d(TAG, "Attempting signUp for $synthesizedEmail")
            // User doesn't exist; create the account
            client.auth.signUpWith(Email) {
                this.email = synthesizedEmail
                this.password = password
            }
            Log.d(TAG, "signUp completed, currentUser=${client.auth.currentUserOrNull()?.id}")
            Result.success(client.auth.currentUserOrNull()?.id ?: "")
        }
    } catch (e: RestException) {
        Log.e(TAG, "RestException during login", e)
        Result.failure(Exception("Auth failed: ${e.message}"))
    } catch (e: Exception) {
        Log.e(TAG, "Exception during login", e)
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
