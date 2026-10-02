package com.kalazacare.leads.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.kalazacare.leads.data.model.Lead
import com.kalazacare.leads.ui.MainActivity

private const val CHANNEL_ID = "follow_up_reminders"
private const val NOTIFICATION_ID = 1001
private const val MAX_NAMES_SHOWN = 5

object NotificationHelper {

    fun ensureChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Follow-up Reminders",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Reminders for leads with a follow-up due today"
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    @SuppressLint("MissingPermission")
    fun showFollowUpReminder(context: Context, dueLeads: List<Lead>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        ensureChannel(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val dueCount = dueLeads.size
        val title = if (dueCount == 1) "1 follow-up due" else "$dueCount follow-ups due"

        val lines = dueLeads.take(MAX_NAMES_SHOWN).map { lead ->
            if (!lead.patientName.isNullOrBlank() && lead.patientName != lead.enquirerName) {
                "${lead.enquirerName} (for ${lead.patientName})"
            } else {
                lead.enquirerName
            }
        }.toMutableList()
        if (dueCount > MAX_NAMES_SHOWN) {
            lines += "+${dueCount - MAX_NAMES_SHOWN} more"
        }

        val inboxStyle = NotificationCompat.InboxStyle()
            .setBigContentTitle(title)
        lines.forEach { inboxStyle.addLine(it) }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(lines.joinToString(", "))
            .setStyle(inboxStyle)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }
}
