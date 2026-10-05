package com.kalazacare.leads

import android.app.Application
import android.content.Intent
import android.util.Log
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

/**
 * Process-wide uncaught-exception handler: a crash ends on our own [CrashActivity] (a plain
 * explanation, a Restart button, and the details to share with whoever maintains the app)
 * instead of the app silently vanishing or the phone maker's own crash dialog.
 * Same approach as the Kalaza Care app.
 */
object CrashHandler {
    fun install(app: Application) {
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            try {
                Log.e("KalazaLeadsCrash", "Uncaught exception", throwable)
                // Two crashes within a few seconds (e.g. the crash screen itself failing) would loop
                // forever if we relaunched every time: the second one just closes the app.
                val prefs = app.getSharedPreferences("crash_guard", android.content.Context.MODE_PRIVATE)
                val now = System.currentTimeMillis()
                val crashLoop = now - prefs.getLong("last_crash_at", 0L) < 5_000L
                prefs.edit().putLong("last_crash_at", now).commit()
                if (crashLoop) {
                    android.os.Process.killProcess(android.os.Process.myPid())
                    exitProcess(1)
                }
                val writer = StringWriter()
                throwable.printStackTrace(PrintWriter(writer))
                val intent = Intent(app, CrashActivity::class.java).apply {
                    putExtra(CrashActivity.EXTRA_DETAILS, writer.toString())
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                app.startActivity(intent)
            } catch (_: Throwable) {
                // Nothing more we can do; the process ends below either way.
            }
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(1)
        }
    }
}
