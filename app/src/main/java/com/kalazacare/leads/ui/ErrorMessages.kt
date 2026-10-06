package com.kalazacare.leads.ui

import io.github.jan.supabase.exceptions.RestException
import java.io.IOException

/**
 * The technical cause chain behind a failure, for the "Show details" link under an error, so a
 * screenshot from any phone says *why* (DNS lookup failed, certificate/clock problem, timeout...).
 */
fun technicalDetails(error: Throwable): String =
    generateSequence(error) { it.cause }
        .take(5)
        .joinToString("\n\u2190 ") { "${it.javaClass.simpleName}: ${it.message ?: "(no message)"}" }

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
