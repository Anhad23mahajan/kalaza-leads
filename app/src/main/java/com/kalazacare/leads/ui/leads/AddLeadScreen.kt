package com.kalazacare.leads.ui.leads

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kalazacare.leads.data.model.NewLeadRequest

private val SOURCE_CHANNELS = listOf("WHATSAPP", "CALL", "WALK_IN")
private val SOURCE_LABELS = mapOf("WHATSAPP" to "WhatsApp", "CALL" to "Call", "WALK_IN" to "Walk-in")

private val SERVICES = listOf("ASSISTED_LIVING", "PALLIATIVE", "OTHER")
private val SERVICE_LABELS = mapOf(
    "ASSISTED_LIVING" to "Assisted Living",
    "PALLIATIVE" to "Palliative",
    "OTHER" to "Other",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLeadScreen(
    viewModel: LeadsViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    var enquirerName by remember { mutableStateOf("") }
    var enquirerPhone by remember { mutableStateOf("") }
    var enquirerRelation by remember { mutableStateOf("") }
    var patientName by remember { mutableStateOf("") }
    var patientAge by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var sourceChannel by remember { mutableStateOf<String?>(null) }
    var serviceWanted by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("New Enquiry") },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Text("Enquirer", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.padding(top = 8.dp))

            OutlinedTextField(
                value = enquirerName,
                onValueChange = { enquirerName = it },
                label = { Text("Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Spacer(Modifier.padding(top = 10.dp))

            OutlinedTextField(
                value = enquirerPhone,
                onValueChange = { enquirerPhone = it },
                label = { Text("Phone *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            )
            Spacer(Modifier.padding(top = 10.dp))

            OutlinedTextField(
                value = enquirerRelation,
                onValueChange = { enquirerRelation = it },
                label = { Text("Relation to patient (e.g. daughter)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(Modifier.padding(top = 24.dp))
            Text("Patient", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.padding(top = 8.dp))

            OutlinedTextField(
                value = patientName,
                onValueChange = { patientName = it },
                label = { Text("Patient name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Spacer(Modifier.padding(top = 10.dp))

            OutlinedTextField(
                value = patientAge,
                onValueChange = { patientAge = it.filter { c -> c.isDigit() } },
                label = { Text("Patient age") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            Spacer(Modifier.padding(top = 24.dp))
            Text("Enquiry details", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.padding(top = 8.dp))

            Text("How did they reach out?", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.padding(top = 4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SOURCE_CHANNELS.forEach { channel ->
                    FilterChip(
                        selected = sourceChannel == channel,
                        onClick = { sourceChannel = if (sourceChannel == channel) null else channel },
                        label = { Text(SOURCE_LABELS[channel] ?: channel) },
                    )
                }
            }
            Spacer(Modifier.padding(top = 16.dp))

            Text("Service wanted", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.padding(top = 4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SERVICES.forEach { service ->
                    FilterChip(
                        selected = serviceWanted == service,
                        onClick = { serviceWanted = if (serviceWanted == service) null else service },
                        label = { Text(SERVICE_LABELS[service] ?: service) },
                    )
                }
            }
            Spacer(Modifier.padding(top = 16.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Spacer(Modifier.padding(top = 10.dp))

            OutlinedTextField(
                value = budget,
                onValueChange = { budget = it.filter { c -> c.isDigit() } },
                label = { Text("Budget (₹/month)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            if (state.errorMessage != null) {
                Spacer(Modifier.padding(top = 12.dp))
                Text(
                    text = state.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(Modifier.padding(top = 24.dp))

            val canSave = enquirerName.isNotBlank() && enquirerPhone.isNotBlank() && !state.isLoading

            Button(
                onClick = {
                    viewModel.addLead(
                        NewLeadRequest(
                            sourceChannel = sourceChannel,
                            enquirerName = enquirerName.trim(),
                            enquirerPhone = enquirerPhone.trim(),
                            enquirerRelation = enquirerRelation.trim().ifBlank { null },
                            patientName = patientName.trim().ifBlank { null },
                            patientAge = patientAge.toIntOrNull(),
                            location = location.trim().ifBlank { null },
                            serviceWanted = serviceWanted,
                            budget = budget.toDoubleOrNull(),
                        ),
                        onSaved,
                    )
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                }
                Text(if (state.isLoading) "Saving..." else "Save Enquiry")
            }

            Spacer(Modifier.padding(top = 8.dp))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
            ) {
                Text("Cancel")
            }
        }
    }
}
