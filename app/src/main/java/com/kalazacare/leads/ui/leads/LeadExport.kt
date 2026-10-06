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

/** Plain numbers/phone-like text that may legitimately start with + or -, e.g. "+91" or "+91 98765 43210". */
private val PLAIN_NUMBER = Regex("^[+-]?[0-9][0-9 ().-]*$")

/**
 * Excel and Sheets run a cell that starts with = + - or @ as a formula. Lead text comes from a
 * public form, so a stranger could plant one (e.g. a name of `=HYPERLINK(...)`). A leading
 * apostrophe makes it plain text. Numbers like the "+91" country code are left alone.
 */
private fun neutraliseFormula(value: String): String {
    if (value.isEmpty()) return value
    val first = value[0]
    val risky = first == '=' || first == '@' || first == '\t' || first == '\r' ||
        ((first == '+' || first == '-') && !PLAIN_NUMBER.matches(value))
    return if (risky) "'$value" else value
}

private fun csvEscape(raw: String): String {
    val value = neutraliseFormula(raw)
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
 * Saves [leads] as a CSV straight into the phone's Downloads folder and returns the file name,
 * or null if it could not be written. Uses MediaStore (Android 10+, no permission needed).
 * On Android 8-9 writing to Downloads would need a storage permission, so those phones get the
 * share sheet instead (see [exportAndShareLeads]).
 *
 * A UTF-8 byte-order mark goes first: without it Excel reads the file as ANSI and garbles the
 * rupee sign and any Hindi/Marathi names.
 */
fun exportLeadsToDownloads(context: Context, leads: List<Lead>, segmentLabel: String): String? {
    val fileName = exportFileName(segmentLabel)
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
        exportAndShareLeads(context, leads, segmentLabel)
        return null
    }
    return try {
        val resolver = context.contentResolver
        val values = android.content.ContentValues().apply {
            put(android.provider.MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(android.provider.MediaStore.Downloads.MIME_TYPE, "text/csv")
            put(android.provider.MediaStore.Downloads.IS_PENDING, 1)
        }
        val uri = resolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: return null
        resolver.openOutputStream(uri)?.use { it.write(("\uFEFF" + buildLeadsCsv(leads)).toByteArray(Charsets.UTF_8)) }
            ?: return null
        values.clear()
        values.put(android.provider.MediaStore.Downloads.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
        fileName
    } catch (e: Exception) {
        android.util.Log.e("LeadExport", "Saving CSV to Downloads failed", e)
        null
    }
}

private fun exportFileName(segmentLabel: String): String {
    val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmm"))
    val safeSegment = segmentLabel.replace(Regex("[^A-Za-z0-9]+"), "_").trim('_')
    return "kalaza_leads_${safeSegment}_$timestamp.csv"
}

/**
 * Fallback for Android 8-9: writes [leads] to a CSV in the app's cache dir and opens the Android share
 * sheet so it can be sent to WhatsApp, email, Drive, etc. (roadmap A6 /
 * supervisor request #7 — "Excel data file saved and shared").
 */
fun exportAndShareLeads(context: Context, leads: List<Lead>, segmentLabel: String) {
    val exportsDir = File(context.cacheDir, "exports").apply { mkdirs() }
    val file = File(exportsDir, exportFileName(segmentLabel))
    file.writeText("\uFEFF" + buildLeadsCsv(leads))

    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, "Kalaza Leads — $segmentLabel export")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share leads export"))
}
