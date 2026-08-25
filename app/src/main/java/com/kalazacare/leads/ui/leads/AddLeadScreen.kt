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
import com.kalazacare.leads.data.model.NewLeadRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLeadScreen(
    viewModel: LeadsViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    var contactChannel by remember { mutableStateOf<String?>(null) }
    var howHeard by remember { mutableStateOf<String?>(null) }

    var enquirerName by remember { mutableStateOf("") }
    var countryCode by remember { mutableStateOf("+91") }
    var enquirerPhone by remember { mutableStateOf("") }
    var enquirerRelation by remember { mutableStateOf<String?>(null) }
    var enquirerLocation by remember { mutableStateOf("") }

    var patientName by remember { mutableStateOf("") }
    var patientAge by remember { mutableStateOf("") }
    var patientGender by remember { mutableStateOf<String?>(null) }
    var patientConditions by remember { mutableStateOf(listOf<String>()) }
    var currentCondition by remember { mutableStateOf("") }
    var medicalHistory by remember { mutableStateOf("") }

    var serviceWanted by remember { mutableStateOf(listOf<String>()) }
    var accommodationType by remember { mutableStateOf<String?>(null) }
    var budgetMin by remember { mutableStateOf("") }
    var budgetMax by remember { mutableStateOf("") }
    var amenitiesRequested by remember { mutableStateOf(listOf<String>()) }
    var specialRequirements by remember { mutableStateOf("") }
    var queries by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf("") }

    var plannedVisitDate by remember { mutableStateOf<String?>(null) }
    var nextFollowUpDate by remember { mutableStateOf<String?>(null) }

    fun toggle(list: List<String>, value: String) =
        if (value in list) list - value else list + value

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("New Enquiry") })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
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
                    viewModel.addLead(
                        NewLeadRequest(
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
                            plannedVisitDate = plannedVisitDate,
                            nextFollowUpDate = nextFollowUpDate,
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

            Spacer(Modifier.padding(bottom = 24.dp))
        }
    }
}
