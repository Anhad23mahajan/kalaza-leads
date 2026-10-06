package com.kalazacare.leads.ui.leads

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kalazacare.leads.data.model.Lead
import com.kalazacare.leads.data.model.UpdateLeadRequest
import java.time.LocalDate

/** Offered options plus a lead's current value, so a value no longer offered by the form stays visible and editable. */
private fun withCurrent(options: List<String>, current: String?): List<String> =
    if (current != null && current !in options) options + current else options

private fun withCurrent(options: List<String>, current: List<String>): List<String> =
    options + current.filter { it !in options }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadDetailScreen(
    lead: Lead,
    viewModel: LeadsViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    var contactChannel by remember { mutableStateOf(lead.contactChannel) }
    var howHeard by remember { mutableStateOf(lead.howHeard) }

    var enquirerName by remember { mutableStateOf(lead.enquirerName) }
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
    // "Converted on" feeds the days-to-convert report. Nothing ever set it before 2026-10-04, so
    // a lead that is already CONVERTED but has no date is pre-filled with today (editable, and
    // only saved if the admin presses Save).
    var convertedAt by remember {
        mutableStateOf(lead.convertedAt ?: if (lead.status == "CONVERTED") LocalDate.now().toString() else null)
    }

    var notConvertedReason by remember { mutableStateOf(lead.notConvertedReason) }
    var notConvertedDetail by remember { mutableStateOf(lead.notConvertedDetail ?: "") }
    var finalRemarks by remember { mutableStateOf(lead.finalRemarks ?: "") }

    fun toggle(list: List<String>, value: String) =
        if (value in list) list - value else list + value

    val titleText = lead.patientName?.takeIf { it.isNotBlank() }
        ?.let { "${lead.enquirerName} (for $it)" }
        ?: lead.enquirerName

    // Input checks: the fields only accept digits, but not sensible ranges.
    val ageValue = patientAge.toIntOrNull()
    val ageError = if (ageValue != null && ageValue > 120) "Age must be 120 or less" else null
    val minValue = budgetMin.toDoubleOrNull()
    val maxValue = budgetMax.toDoubleOrNull()
    val budgetError = if (minValue != null && maxValue != null && minValue > maxValue) {
        "Max must be at least the min"
    } else {
        null
    }

    fun buildRequest() = UpdateLeadRequest(
                contactChannel = contactChannel,
                howHeard = howHeard,
                enquirerName = enquirerName.trim(),
                enquirerCountryCode = lead.enquirerCountryCode,
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
                convertedAt = if (status == "CONVERTED") convertedAt else null,
                notConvertedReason = if (status == "NOT_CONVERTED") notConvertedReason else null,
                notConvertedDetail = if (status == "NOT_CONVERTED") notConvertedDetail.trim().ifBlank { null } else null,
                finalRemarks = finalRemarks.trim().ifBlank { null },
            )

    // What the screen would save if nothing were touched; anything different = unsaved edits.
    val initialRequest = remember { buildRequest() }
    val hasUnsavedChanges = buildRequest() != initialRequest

    val canSave = enquirerName.isNotBlank() && enquirerPhone.length == 10 &&
        ageError == null && budgetError == null && !state.isSaving

    var showDiscardConfirm by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    fun leave() {
        if (hasUnsavedChanges) showDiscardConfirm = true else onBack()
    }

    // Takes over from MainActivity's Back handling only while there is something to lose.
    BackHandler(enabled = hasUnsavedChanges && !state.isSaving) { showDiscardConfirm = true }

    fun saveChanges() {
        val leadId = lead.id ?: return
        viewModel.updateLead(leadId, buildRequest(), onSaved)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(titleText, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            navigationIcon = {
                IconButton(onClick = ::leave, enabled = !state.isSaving) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back / cancel")
                }
            },
            actions = {
                TextButton(onClick = ::saveChanges, enabled = canSave) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(end = 8.dp).size(18.dp),
                            strokeWidth = 2.dp,
                        )
                    }
                    Text(if (state.isSaving) "Saving..." else "Save")
                }
            },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                // Without these the keyboard covers the field being typed in, and the last items
                // (Delete) sit under the navigation bar -- the app draws edge to edge.
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Text("Pipeline", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.padding(top = 8.dp))
            EnumDropdown("Status", STATUSES, STATUS_LABELS, status, {
                if (it != null) {
                    status = it
                    if (it == "CONVERTED" && convertedAt == null) convertedAt = LocalDate.now().toString()
                }
            })

            if (status == "CONVERTED") {
                Spacer(Modifier.padding(top = 10.dp))
                DateField("Converted on (date they moved in)", convertedAt, { convertedAt = it })
            }

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

            OutlinedTextField(
                value = enquirerPhone,
                onValueChange = { enquirerPhone = it.filter { c -> c.isDigit() }.take(10) },
                label = { Text("Phone * (10 digits)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            )
            Spacer(Modifier.padding(top = 10.dp))

            EnumDropdown(
                "Relation to patient", withCurrent(RELATIONS_OFFERED, enquirerRelation), RELATION_LABELS,
                enquirerRelation, { enquirerRelation = it },
            )
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
                    onValueChange = { patientAge = it.filter { c -> c.isDigit() }.take(3) },
                    label = { Text("Age") },
                    isError = ageError != null,
                    supportingText = ageError?.let { { Text(it) } },
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

            MultiSelectChips("Service wanted", withCurrent(SERVICES_OFFERED, serviceWanted), SERVICE_LABELS, serviceWanted) {
                serviceWanted = toggle(serviceWanted, it)
            }
            Spacer(Modifier.padding(top = 14.dp))

            EnumDropdown(
                "Room type", withCurrent(ACCOMMODATIONS_OFFERED, accommodationType), ACCOMMODATION_LABELS,
                accommodationType, { accommodationType = it },
            )
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
                    isError = budgetError != null,
                    supportingText = budgetError?.let { { Text(it) } },
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
                label = { Text("Comments") },
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
            Text("Send WhatsApp", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.padding(top = 8.dp))
            Text(
                "Opens WhatsApp with a message pre-filled — review or edit it there before sending.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.padding(top = 10.dp))
            WhatsAppQuickMessages(lead)

            HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))
            Text("Outcome", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.padding(top = 8.dp))

            OutlinedTextField(
                value = finalRemarks,
                onValueChange = { finalRemarks = it },
                label = { Text("Final remarks") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))
            OutlinedButton(
                onClick = { showDeleteConfirm = true },
                enabled = !state.isSaving && lead.id != null,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            ) {
                Text("Delete this lead")
            }

            Spacer(Modifier.padding(bottom = 24.dp))
        }
    }

    if (showDiscardConfirm) {
        AlertDialog(
            onDismissRequest = { showDiscardConfirm = false },
            title = { Text("Discard changes?") },
            text = { Text("You have edits that aren't saved. Leave without saving them?") },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardConfirm = false
                    onBack()
                }) { Text("Discard") }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardConfirm = false }) { Text("Keep editing") }
            },
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete this lead?") },
            text = {
                Text("This permanently removes ${lead.enquirerName}'s enquiry and all its details. It can't be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        lead.id?.let { viewModel.deleteLead(it, onBack) }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            },
        )
    }
}
