package com.kalazacare.leads.data.repository

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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
        } else if (isActiveStaffName(staffName)) {
            Log.d(TAG, "Attempting signUp for $synthesizedEmail (matched active staff roster)")
            // Name matches an active staff.name row -> could be a first-time login (create
            // the account), or an existing account with the wrong password -- signIn's
            // generic "invalid credentials" error can't tell those apart, so try signUp
            // and read its specific error code to tell the user which one happened.
            val signUpResult = runCatching {
                client.auth.signUpWith(Email) {
                    this.email = synthesizedEmail
                    this.password = password
                }
            }
            if (signUpResult.isSuccess) {
                Log.d(TAG, "signUp completed, currentUser=${client.auth.currentUserOrNull()?.id}")
                Result.success(client.auth.currentUserOrNull()?.id ?: "")
            } else {
                val signUpError = signUpResult.exceptionOrNull()
                if (signUpError is RestException && signUpError.error == "user_already_exists") {
                    Log.d(TAG, "signUp hit an existing account -> wrong password for '$staffName'")
                    Result.failure(Exception("Incorrect password for \"$staffName\"."))
                } else {
                    Log.e(TAG, "signUp failed", signUpError)
                    Result.failure(Exception("Couldn't create account: ${signUpError?.message}"))
                }
            }
        } else {
            Log.d(TAG, "signUp blocked: '$staffName' is not on the active staff roster")
            Result.failure(
                Exception("Name not recognized as active staff, or the password is wrong. Ask your admin to add you under Staff first.")
            )
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

    /**
     * Checks the `is_active_staff_name` RPC (docs/sql/005_staff_name_check_rpc.sql)
     * — callable pre-auth, returns only a boolean, never the actual staff roster.
     */
    private suspend fun isActiveStaffName(name: String): Boolean = try {
        client.postgrest.rpc(
            "is_active_staff_name",
            buildJsonObject { put("check_name", name) },
        ).decodeAs<Boolean>()
    } catch (e: Exception) {
        Log.e(TAG, "isActiveStaffName check failed", e)
        false
    }
}
