package com.kalazacare.leads.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.signInWith
import io.github.jan.supabase.auth.signOut
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.exceptions.RestException

/**
 * Supabase auth implementation. Staff log in by NAME, not email.
 * Synthesizes an email (staff_name@kalaza-leads.internal) for Supabase Auth.
 *
 * TODO (Phase 2): Replace with the security-definer RPC pattern from Kalaza Care
 * once that repo's auth setup is available. The RPC handles synthesis server-side.
 */
class SupabaseAuthRepository(private val client: SupabaseClient) : AuthRepository {

    override suspend fun login(staffName: String, password: String): Result<String> = try {
        val synthesizedEmail = "${staffName.lowercase().replace(" ", "_")}@kalaza-leads.internal"

        // Try to sign in. If it fails with "Invalid credentials", fall back to sign up.
        val signInResult = runCatching {
            client.auth.signInWith(
                email = synthesizedEmail,
                password = password
            )
        }

        if (signInResult.isSuccess) {
            Result.success(client.auth.currentUserOrNull()?.id ?: "")
        } else {
            // User doesn't exist; create the account
            client.auth.signUpWith(
                email = synthesizedEmail,
                password = password
            )
            Result.success(client.auth.currentUserOrNull()?.id ?: "")
        }
    } catch (e: RestException) {
        Result.failure(Exception("Auth failed: ${e.message}"))
    } catch (e: Exception) {
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
