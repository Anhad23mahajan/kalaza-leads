package com.kalazacare.leads.ui.leads

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.kalazacare.leads.data.model.Lead
import com.kalazacare.leads.data.model.UpdateLeadRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadDetailScreen(
    lead: Lead,
    viewModel: LeadsViewModel,
    activitiesViewModel: ActivitiesViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    var contactChannel by remember { mutableStateOf(lead.contactChannel) }
    var howHeard by remember { mutableStateOf(lead.howHeard) }

    var enquirerName by remember { mutableStateOf(lead.enquirerName) }
    var countryCode by remember { mutableStateOf(lead.enquirerCountryCode) }
    var enquirerPhone by remember { mutableStateOf(lead.enquirerPhone) }
    var enquirerRelation by remember { mutableStateOf(lead.enquirerRelation) }
    var enquirerLocation by remember { mutableStateOf(lead.enquirerLocation ?: "") }

    var patientName by remember { mutableStateOf(lead.patientName ?: "") }
    var patientAge by remember { mutableStateOf(lead.patientAge?.toString() ?: "") }
    var patientGender by remember { mutableStateOf(lead.patientGender) }
    var patientConditions by remember { mutableStateOf(lead.patientConditions) }
    var currentCondition by remember { mutableStateOf(lead.currentCondition ?: "") }
    var medicalHistory by remember { mutableStateOf(lead.medicalHistory ?: "") }

    var serviceWanted by remember { mutableStateOf(lead.serviceWanted) }
    var accommodationType by remember { mutableStateOf(lead.accommodationType) }
    var budgetMin by remember { mutableStateOf(lead.budgetMin?.toInt()?.toString() ?: "") }
    var budgetMax by remember { mutableStateOf(lead.budgetMax?.toInt()?.toString() ?: "") }
    var amenitiesRequested by remember { mutableStateOf(lead.amenitiesRequested) }
    var specialRequirements by remember { mutableStateOf(lead.specialRequirements ?: "") }
    var queries by remember { mutableStateOf(lead.queries ?: "") }
    var comments by remember { mutableStateOf(lead.comments ?: "") }

    var status by remember { mutableStateOf(lead.status) }
    var plannedVisitDate by remember { mutableStateOf(lead.plannedVisitDate) }
    var actualVisitDate by remember { mutableStateOf(lead.actualVisitDate) }
    var nextFollowUpDate by remember { mutableStateOf(lead.nextFollowUpDate) }

    var notConvertedReason by remember { mutableStateOf(lead.notConvertedReason) }
    var notConvertedDetail by remember { mutableStateOf(lead.notConvertedDetail ?: "") }
    var finalRemarks by remember { mutableStateOf(lead.finalRemarks ?: "") }

    fun toggle(list: List<String>, value: String) =
        if (value in list) list - value else list + value

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text(lead.enquirerName) })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Text("Pipeline", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.padding(top = 8.dp))
            EnumDropdown("Status", STATUSES, STATUS_LABELS, status, { if (it != null) status = it })

            if (status == "NOT_CONVERTED") {
                Spacer(Modifier.padding(top = 10.dp))
                EnumDropdown(
                    "Reason", NOT_CONVERTED_REASONS, NOT_CONVERTED_REASON_LABELS,
                    notConvertedReason, { notConvertedReason = it },
                )
                Spacer(Modifier.padding(top = 10.dp))
                OutlinedTextField(
                    value = notConvertedDetail,
                    onValueChange = { notConvertedDetail = it },
                    label = { Text("Detail") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))
            lead.id?.let { leadId ->
                ContactLogSection(leadId = leadId, viewModel = activitiesViewModel)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))
            Text("How did they reach out?", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.padding(top = 8.dp))
            EnumDropdown("Contact channel", CONTACT_CHANNELS, CONTACT_CHANNEL_LABELS, contactChannel, { contactChannel = it })
            Spacer(Modifier.padding(top = 10.dp))
            EnumDropdown("How did they hear about us", HOW_HEARD, HOW_HEARD_LABELS, howHeard, { howHeard = it })

            HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))
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

            Row {
                EnumDropdown(
                    "Code", COUNTRY_CODES, COUNTRY_CODES.associateWith { it }, countryCode,
                    { if (it != null) countryCode = it },
                    modifier = Modifier.width(100.dp),
                )
                Spacer(Modifier.padding(start = 8.dp))
                OutlinedTextField(
                    value = enquirerPhone,
                    onValueChange = { enquirerPhone = it.filter { c -> c.isDigit() }.take(10) },
                    label = { Text("Phone * (10 digits)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                )
            }
            Spacer(Modifier.padding(top = 10.dp))

            EnumDropdown("Relation to patient", RELATIONS, RELATION_LABELS, enquirerRelation, { enquirerRelation = it })
            Spacer(Modifier.padding(top = 10.dp))

            OutlinedTextField(
                value = enquirerLocation,
                onValueChange = { enquirerLocation = it },
                label = { Text("Where are they from?") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))
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

            Row {
                OutlinedTextField(
                    value = patientAge,
                    onValueChange = { patientAge = it.filter { c -> c.isDigit() } },
                    label = { Text("Age") },
                    modifier = Modifier.width(120.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                Spacer(Modifier.padding(start = 8.dp))
                EnumDropdown(
                    "Gender", GENDERS, GENDER_LABELS, patientGender, { patientGender = it },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(Modifier.padding(top = 14.dp))

            MultiSelectChips("What does the patient have?", CONDITIONS, CONDITION_LABELS, patientConditions) {
                patientConditions = toggle(patientConditions, it)
            }
            Spacer(Modifier.padding(top = 14.dp))

            OutlinedTextField(
                value = currentCondition,
                onValueChange = { currentCondition = it },
                label = { Text("Current condition / mobility") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
            )
            Spacer(Modifier.padding(top = 10.dp))

            OutlinedTextField(
                value = medicalHistory,
                onValueChange = { medicalHistory = it },
                label = { Text("Medical history") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))
            Text("Requirement", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.padding(top = 8.dp))

            MultiSelectChips("Service wanted", SERVICES, SERVICE_LABELS, serviceWanted) {
                serviceWanted = toggle(serviceWanted, it)
            }
            Spacer(Modifier.padding(top = 14.dp))

            EnumDropdown("Room type", ACCOMMODATIONS, ACCOMMODATION_LABELS, accommodationType, { accommodationType = it })
            Spacer(Modifier.padding(top = 10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = budgetMin,
                    onValueChange = { budgetMin = it.filter { c -> c.isDigit() } },
                    label = { Text("Budget min (₹)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                Spacer(Modifier.padding(start = 8.dp))
                OutlinedTextField(
                    value = budgetMax,
                    onValueChange = { budgetMax = it.filter { c -> c.isDigit() } },
                    label = { Text("Budget max (₹)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
            Spacer(Modifier.padding(top = 14.dp))

            MultiSelectChips("Amenities requested", AMENITIES, AMENITY_LABELS, amenitiesRequested) {
                amenitiesRequested = toggle(amenitiesRequested, it)
            }
            Spacer(Modifier.padding(top = 14.dp))

            OutlinedTextField(
                value = specialRequirements,
                onValueChange = { specialRequirements = it },
                label = { Text("Special requirements") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))
            Text("Scheduling", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.padding(top = 8.dp))

            DateField("Planned visit date", plannedVisitDate, { plannedVisitDate = it })
            Spacer(Modifier.padding(top = 10.dp))
            DateField("Actual visit date", actualVisitDate, { actualVisitDate = it })
            Spacer(Modifier.padding(top = 10.dp))
            DateField("Next follow-up date", nextFollowUpDate, { nextFollowUpDate = it })

            HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))
            Text("Notes", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.padding(top = 8.dp))

            OutlinedTextField(
                value = queries,
                onValueChange = { queries = it },
                label = { Text("What did they actually ask?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
            )
            Spacer(Modifier.padding(top = 10.dp))

            OutlinedTextField(
                value = comments,
                onValueChange = { comments = it },
                label = { Text("Staff comments") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
            )
            Spacer(Modifier.padding(top = 10.dp))

            OutlinedTextField(
                value = finalRemarks,
                onValueChange = { finalRemarks = it },
                label = { Text("Final remarks") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
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

            val canSave = enquirerName.isNotBlank() && enquirerPhone.length == 10 && !state.isLoading

            Button(
                onClick = {
                    val leadId = lead.id ?: return@Button
                    viewModel.updateLead(
                        leadId,
                        UpdateLeadRequest(
                            contactChannel = contactChannel,
                            howHeard = howHeard,
                            enquirerName = enquirerName.trim(),
                            enquirerCountryCode = countryCode,
                            enquirerPhone = enquirerPhone.trim(),
                            enquirerRelation = enquirerRelation,
                            enquirerLocation = enquirerLocation.trim().ifBlank { null },
                            patientName = patientName.trim().ifBlank { null },
                            patientAge = patientAge.toIntOrNull(),
                            patientGender = patientGender,
                            patientConditions = patientConditions,
                            currentCondition = currentCondition.trim().ifBlank { null },
                            medicalHistory = medicalHistory.trim().ifBlank { null },
                            serviceWanted = serviceWanted,
                            accommodationType = accommodationType,
                            budgetMin = budgetMin.toDoubleOrNull(),
                            budgetMax = budgetMax.toDoubleOrNull(),
                            amenitiesRequested = amenitiesRequested,
                            specialRequirements = specialRequirements.trim().ifBlank { null },
                            queries = queries.trim().ifBlank { null },
                            comments = comments.trim().ifBlank { null },
                            status = status,
                            plannedVisitDate = plannedVisitDate,
                            actualVisitDate = actualVisitDate,
                            nextFollowUpDate = nextFollowUpDate,
                            notConvertedReason = if (status == "NOT_CONVERTED") notConvertedReason else null,
                            notConvertedDetail = if (status == "NOT_CONVERTED") notConvertedDetail.trim().ifBlank { null } else null,
                            finalRemarks = finalRemarks.trim().ifBlank { null },
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
                Text(if (state.isLoading) "Saving..." else "Save Changes")
            }

            Spacer(Modifier.padding(top = 8.dp))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
            ) {
                Text("Cancel")
            }

            Spacer(Modifier.padding(bottom = 24.dp))
        }
    }
}
