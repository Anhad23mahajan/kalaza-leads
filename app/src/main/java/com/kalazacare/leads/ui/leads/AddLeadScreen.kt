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

private val CONTACT_CHANNELS = listOf("phone_call", "whatsapp", "walk_in", "website", "email", "instagram_dm")
private val CONTACT_CHANNEL_LABELS = mapOf(
    "phone_call" to "Phone Call", "whatsapp" to "WhatsApp", "walk_in" to "Walk-in",
    "website" to "Website", "email" to "Email", "instagram_dm" to "Instagram DM",
)

private val HOW_HEARD = listOf(
    "google_search", "google_maps", "instagram", "facebook",
    "referral_friend_family", "referral_hospital", "referral_doctor",
    "passing_by", "newspaper", "other",
)
private val HOW_HEARD_LABELS = mapOf(
    "google_search" to "Google Search", "google_maps" to "Google Maps",
    "instagram" to "Instagram", "facebook" to "Facebook",
    "referral_friend_family" to "Referral — Friend/Family", "referral_hospital" to "Referral — Hospital",
    "referral_doctor" to "Referral — Doctor", "passing_by" to "Passing By",
    "newspaper" to "Newspaper", "other" to "Other",
)

private val RELATIONS = listOf(
    "son", "daughter", "spouse", "sibling", "grandchild",
    "nephew_niece", "friend", "self", "hospital_staff", "other",
)
private val RELATION_LABELS = mapOf(
    "son" to "Son", "daughter" to "Daughter", "spouse" to "Spouse", "sibling" to "Sibling",
    "grandchild" to "Grandchild", "nephew_niece" to "Nephew/Niece", "friend" to "Friend",
    "self" to "Self", "hospital_staff" to "Hospital Staff", "other" to "Other",
)

private val GENDERS = listOf("male", "female", "other")
private val GENDER_LABELS = mapOf("male" to "Male", "female" to "Female", "other" to "Other")

private val CONDITIONS = listOf(
    "alzheimers", "dementia", "parkinsons", "cancer", "post_stroke",
    "post_operative", "post_transplant", "bedridden", "diabetes",
    "cardiac", "mobility_impaired", "other",
)
private val CONDITION_LABELS = mapOf(
    "alzheimers" to "Alzheimer's", "dementia" to "Dementia", "parkinsons" to "Parkinson's",
    "cancer" to "Cancer", "post_stroke" to "Post-Stroke", "post_operative" to "Post-Operative",
    "post_transplant" to "Post-Transplant", "bedridden" to "Bedridden", "diabetes" to "Diabetes",
    "cardiac" to "Cardiac", "mobility_impaired" to "Mobility Impaired", "other" to "Other",
)

private val SERVICES = listOf(
    "assisted_living", "palliative_care", "post_transplant_care", "cancer_care",
    "medical_recovery", "dementia_care", "respite_care", "day_care",
)
private val SERVICE_LABELS = mapOf(
    "assisted_living" to "Assisted Living", "palliative_care" to "Palliative Care",
    "post_transplant_care" to "Post-Transplant Care", "cancer_care" to "Cancer Care",
    "medical_recovery" to "Medical Recovery", "dementia_care" to "Dementia Care",
    "respite_care" to "Respite Care", "day_care" to "Day Care",
)

private val ACCOMMODATIONS = listOf(
    "single_room", "double_sharing", "triple_sharing", "full_flat", "dormitory", "not_sure",
)
private val ACCOMMODATION_LABELS = mapOf(
    "single_room" to "Single Room", "double_sharing" to "Double Sharing",
    "triple_sharing" to "Triple Sharing", "full_flat" to "Full Flat",
    "dormitory" to "Dormitory", "not_sure" to "Not Sure",
)

private val AMENITIES = listOf(
    "ac", "lift", "attached_bathroom", "ground_floor",
    "female_attendant", "private_nurse", "veg_food", "other",
)
private val AMENITY_LABELS = mapOf(
    "ac" to "AC", "lift" to "Lift", "attached_bathroom" to "Attached Bathroom",
    "ground_floor" to "Ground Floor", "female_attendant" to "Female Attendant",
    "private_nurse" to "Private Nurse", "veg_food" to "Veg Food", "other" to "Other",
)

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
