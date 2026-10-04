package com.kalazacare.leads.ui.leads

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.kalazacare.leads.notifications.NotificationPreferences
import com.kalazacare.leads.notifications.NotificationScheduler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var reminderTime by remember { mutableStateOf(NotificationPreferences.getReminderTime(context)) }
    var showTimeDialog by remember { mutableStateOf(false) }
    var exactAlarmsAllowed by remember { mutableStateOf(NotificationScheduler.canScheduleExactAlarms(context)) }
    val batteryManager = remember { context.getSystemService(PowerManager::class.java) }
    var batteryUnrestricted by remember {
        mutableStateOf(batteryManager?.isIgnoringBatteryOptimizations(context.packageName) ?: true)
    }

    // Re-check both permissions whenever the user comes back to this screen (e.g. after
    // granting one in system Settings and pressing back) -- a plain `remember` wouldn't
    // notice, since this screen stays composed across that app switch.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                exactAlarmsAllowed = NotificationScheduler.canScheduleExactAlarms(context)
                batteryUnrestricted = batteryManager?.isIgnoringBatteryOptimizations(context.packageName) ?: true
                // An alarm armed while the permission was missing stays inexact until it is
                // set again, so re-arm it now (cheap and idempotent).
                NotificationScheduler.scheduleNext(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Follow-up reminder", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Checks daily for leads whose follow-up is due, and notifies you with their names.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Reminds you daily at %02d:%02d".format(reminderTime.first, reminderTime.second),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = { showTimeDialog = true }) {
                        Text("Change time")
                    }
                }
            }

            if (!exactAlarmsAllowed) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Exact-time reminders are off", style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Without this permission, Android may delay the reminder by hours. Grant it for the time above to actually be honoured.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                            }
                        }) { Text("Allow exact alarms") }
                    }
                }
            }

            if (!batteryUnrestricted) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Battery optimization may delay reminders", style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Some phones (Xiaomi, Vivo, Oppo, Samsung) aggressively pause apps to save battery, which can still delay this reminder even with exact alarms allowed. Whitelisting the app helps.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = {
                            val intent = Intent(
                                Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                                Uri.parse("package:${context.packageName}"),
                            )
                            context.startActivity(intent)
                        }) { Text("Disable battery optimization for this app") }
                    }
                }
            }
        }
    }

    if (showTimeDialog) {
        val timePickerState = rememberTimePickerState(
            initialHour = reminderTime.first,
            initialMinute = reminderTime.second,
            is24Hour = true,
        )
        AlertDialog(
            onDismissRequest = { showTimeDialog = false },
            title = { Text("Reminder time") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    NotificationPreferences.setReminderTime(context, timePickerState.hour, timePickerState.minute)
                    NotificationScheduler.scheduleNext(context)
                    reminderTime = timePickerState.hour to timePickerState.minute
                    showTimeDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showTimeDialog = false }) { Text("Cancel") }
            },
        )
    }
}
