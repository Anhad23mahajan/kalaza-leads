package com.kalazacare.leads.ui.leads

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kalazacare.leads.data.model.ContactActivity
import com.kalazacare.leads.data.model.NewContactActivityRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactLogSection(
    leadId: String,
    viewModel: ActivitiesViewModel,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(leadId) {
        viewModel.load(leadId)
    }

    var showForm by remember { mutableStateOf(false) }
    var type by remember { mutableStateOf<String?>(null) }
    var direction by remember { mutableStateOf<String?>(null) }
    var outcome by remember { mutableStateOf<String?>(null) }
    var callbackOn by remember { mutableStateOf<String?>(null) }
    var notes by remember { mutableStateOf("") }

    Column {
        Text("Contact Log", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.padding(top = 8.dp))

        if (!showForm) {
            Button(onClick = { showForm = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Log Contact")
            }
        } else {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    EnumDropdown("Type", ACTIVITY_TYPES, ACTIVITY_TYPE_LABELS, type, { type = it })
                    Spacer(Modifier.padding(top = 10.dp))
                    EnumDropdown("Direction", ACTIVITY_DIRECTIONS, ACTIVITY_DIRECTION_LABELS, direction, { direction = it })
                    Spacer(Modifier.padding(top = 10.dp))
                    EnumDropdown("Outcome", ACTIVITY_OUTCOMES, ACTIVITY_OUTCOME_LABELS, outcome, { outcome = it })

                    if (outcome == "callback_requested") {
                        Spacer(Modifier.padding(top = 10.dp))
                        DateField("Callback on", callbackOn, { callbackOn = it })
                    }

                    Spacer(Modifier.padding(top = 10.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                    )

                    Spacer(Modifier.padding(top = 14.dp))
                    Row {
                        val canLog = type != null && direction != null && !state.isLoading
                        Button(
                            onClick = {
                                viewModel.addActivity(
                                    NewContactActivityRequest(
                                        leadId = leadId,
                                        type = type!!,
                                        direction = direction!!,
                                        outcome = outcome,
                                        callbackOn = callbackOn,
                                        notes = notes.trim().ifBlank { null },
                                    )
                                )
                                showForm = false
                                type = null; direction = null; outcome = null; callbackOn = null; notes = ""
                            },
                            enabled = canLog,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Log")
                        }
                        Spacer(Modifier.padding(start = 8.dp))
                        TextButton(onClick = { showForm = false }) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }

        Spacer(Modifier.padding(top = 14.dp))

        if (state.isLoading && state.activities.isEmpty()) {
            CircularProgressIndicator()
        } else if (state.activities.isEmpty()) {
            Text(
                "No contact logged yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            state.activities.forEach { activity ->
                ActivityRow(activity)
                Spacer(Modifier.padding(top = 8.dp))
            }
        }

        if (state.errorMessage != null) {
            Text(
                state.errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun ActivityRow(activity: ContactActivity) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row {
                Text(
                    text = ACTIVITY_TYPE_LABELS[activity.type] ?: activity.type,
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = "  •  ${ACTIVITY_DIRECTION_LABELS[activity.direction] ?: activity.direction}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (activity.outcome != null) {
                Text(
                    text = ACTIVITY_OUTCOME_LABELS[activity.outcome] ?: activity.outcome,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            if (!activity.notes.isNullOrBlank()) {
                Spacer(Modifier.padding(top = 4.dp))
                Text(activity.notes, style = MaterialTheme.typography.bodyMedium)
            }
            if (activity.occurredAt != null) {
                Spacer(Modifier.padding(top = 4.dp))
                Text(
                    activity.occurredAt.take(16).replace("T", " "),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
