package com.kalazacare.leads.ui

import io.github.jan.supabase.exceptions.RestException
import java.io.IOException

/**
 * Turns a failure into a sentence the admin can act on. Raw library messages ("HTTP request to
 * https://... failed with message: Unable to resolve host ...") must never reach the screen; the
 * full exception is still logged by the caller.
 *
 * Network failures in supabase-kt (HttpRequestException, timeouts, no connection) are all
 * IOExceptions; server-side refusals are RestExceptions carrying the HTTP status.
 */
fun userFacingMessage(error: Throwable, fallback: String): String = when {
    error is IOException ->
        "Can't reach the server. Check the internet connection and try again."
    error is RestException && error.statusCode == 429 ->
        "Too many attempts. Wait a minute and try again."
    error is RestException && (error.statusCode == 401 || error.statusCode == 403) ->
        "Your login has expired. Log out and log in again."
    else -> fallback
}
