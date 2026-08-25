package com.kalazacare.leads.ui.leads

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.kalazacare.leads.data.model.Lead
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val EXPORT_COLUMNS: List<Pair<String, (Lead) -> String>> = listOf(
    "Enquiry Date" to { it.enquiryDate.orEmpty() },
    "Status" to { STATUS_LABELS[it.status] ?: it.status },
    "Enquirer Name" to { it.enquirerName },
    "Phone" to { "${it.enquirerCountryCode} ${it.enquirerPhone}" },
    "Relation to Patient" to { it.enquirerRelation?.let { r -> RELATION_LABELS[r] ?: r }.orEmpty() },
    "Location" to { it.enquirerLocation.orEmpty() },
    "Patient Name" to { it.patientName.orEmpty() },
    "Patient Age" to { it.patientAge?.toString().orEmpty() },
    "Patient Gender" to { it.patientGender?.let { g -> GENDER_LABELS[g] ?: g }.orEmpty() },
    "Service Wanted" to { it.serviceWanted.joinToString("; ") { s -> SERVICE_LABELS[s] ?: s } },
    "Accommodation Type" to { it.accommodationType?.let { a -> ACCOMMODATION_LABELS[a] ?: a }.orEmpty() },
    "Contact Channel" to { it.contactChannel?.let { c -> CONTACT_CHANNEL_LABELS[c] ?: c }.orEmpty() },
    "How Heard" to { it.howHeard?.let { h -> HOW_HEARD_LABELS[h] ?: h }.orEmpty() },
    "How Heard Detail" to { it.howHeardDetail.orEmpty() },
    "Patient Conditions" to { it.patientConditions.joinToString("; ") { c -> CONDITION_LABELS[c] ?: c } },
    "Current Condition" to { it.currentCondition.orEmpty() },
    "Medical History" to { it.medicalHistory.orEmpty() },
    "Budget Min" to { it.budgetMin?.toString().orEmpty() },
    "Budget Max" to { it.budgetMax?.toString().orEmpty() },
    "Budget Notes" to { it.budgetNotes.orEmpty() },
    "Amenities Requested" to { it.amenitiesRequested.joinToString("; ") { a -> AMENITY_LABELS[a] ?: a } },
    "Special Requirements" to { it.specialRequirements.orEmpty() },
    "Queries" to { it.queries.orEmpty() },
    "Comments" to { it.comments.orEmpty() },
    "Price List Shared" to { if (it.priceListShared) "Yes" else "No" },
    "Planned Visit Date" to { it.plannedVisitDate.orEmpty() },
    "Actual Visit Date" to { it.actualVisitDate.orEmpty() },
    "Next Follow-up Date" to { it.nextFollowUpDate.orEmpty() },
    "Follow-up Count" to { it.followUpCount.toString() },
    "Converted At" to { it.convertedAt.orEmpty() },
    "Days to Convert" to { it.daysToConvert?.toString().orEmpty() },
    "Not Converted Reason" to { it.notConvertedReason?.let { r -> NOT_CONVERTED_REASON_LABELS[r] ?: r }.orEmpty() },
    "Not Converted Detail" to { it.notConvertedDetail.orEmpty() },
    "Final Remarks" to { it.finalRemarks.orEmpty() },
)

private fun csvEscape(value: String): String {
    return if (value.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) {
        "\"${value.replace("\"", "\"\"")}\""
    } else {
        value
    }
}

private fun buildLeadsCsv(leads: List<Lead>): String = buildString {
    append(EXPORT_COLUMNS.joinToString(",") { csvEscape(it.first) })
    append("\r\n")
    for (lead in leads) {
        append(EXPORT_COLUMNS.joinToString(",") { csvEscape(it.second(lead)) })
        append("\r\n")
    }
}

/**
 * Writes [leads] to a CSV in the app's cache dir and opens the Android share
 * sheet so staff can send it to WhatsApp, email, Drive, etc. (Master Plan A6 /
 * supervisor request #7 — "Excel data file saved and shared").
 */
fun exportAndShareLeads(context: Context, leads: List<Lead>, segmentLabel: String) {
    val exportsDir = File(context.cacheDir, "exports").apply { mkdirs() }
    val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmm"))
    val safeSegment = segmentLabel.replace(Regex("[^A-Za-z0-9]+"), "_").trim('_')
    val file = File(exportsDir, "kalaza_leads_${safeSegment}_$timestamp.csv")
    file.writeText(buildLeadsCsv(leads))

    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, "Kalaza Leads — $segmentLabel export")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share leads export"))
}
