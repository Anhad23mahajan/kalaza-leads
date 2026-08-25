package com.kalazacare.leads.ui.leads

import com.kalazacare.leads.data.model.Lead
import com.kalazacare.leads.data.model.StaffMember

/**
 * Pure, client-side analytics over leads already loaded into memory (Master
 * Plan Part 7 / roadmap A9). No new backend calls -- everything here is
 * computed from data the app already fetched for the Leads screen.
 */

data class ReportOverview(
    val total: Int,
    val converted: Int,
    val notConverted: Int,
    val conversionRateOfTotal: Double,
    val conversionRateOfDecided: Double?,
    val medianDaysToConvert: Int?,
)

data class BreakdownRow(val label: String, val count: Int, val convertedCount: Int) {
    val conversionRate: Double get() = if (count == 0) 0.0 else convertedCount.toDouble() / count
}

fun computeOverview(leads: List<Lead>): ReportOverview {
    val converted = leads.count { it.status == "CONVERTED" }
    val notConverted = leads.count { it.status == "NOT_CONVERTED" }
    val decided = converted + notConverted
    val daysToConvert = leads.mapNotNull { if (it.status == "CONVERTED") it.daysToConvert else null }.sorted()
    val median = if (daysToConvert.isEmpty()) {
        null
    } else if (daysToConvert.size % 2 == 1) {
        daysToConvert[daysToConvert.size / 2]
    } else {
        (daysToConvert[daysToConvert.size / 2 - 1] + daysToConvert[daysToConvert.size / 2]) / 2
    }
    return ReportOverview(
        total = leads.size,
        converted = converted,
        notConverted = notConverted,
        conversionRateOfTotal = if (leads.isEmpty()) 0.0 else converted.toDouble() / leads.size,
        conversionRateOfDecided = if (decided == 0) null else converted.toDouble() / decided,
        medianDaysToConvert = median,
    )
}

fun computeStatusFunnel(leads: List<Lead>): List<Pair<String, Int>> =
    STATUSES.map { status -> (STATUS_LABELS[status] ?: status) to leads.count { it.status == status } }

fun computeBySource(leads: List<Lead>): List<BreakdownRow> =
    leads.groupBy { it.howHeard?.let { h -> HOW_HEARD_LABELS[h] ?: h } ?: "Not specified" }
        .map { (label, group) -> BreakdownRow(label, group.size, group.count { it.status == "CONVERTED" }) }
        .sortedByDescending { it.count }

fun computeByService(leads: List<Lead>): List<BreakdownRow> {
    val rows = mutableMapOf<String, BreakdownRow>()
    for (lead in leads) {
        val services = lead.serviceWanted.ifEmpty { listOf("__none__") }
        for (service in services) {
            val label = if (service == "__none__") "Not specified" else SERVICE_LABELS[service] ?: service
            val existing = rows[label]
            val isConverted = lead.status == "CONVERTED"
            rows[label] = if (existing == null) {
                BreakdownRow(label, 1, if (isConverted) 1 else 0)
            } else {
                existing.copy(count = existing.count + 1, convertedCount = existing.convertedCount + if (isConverted) 1 else 0)
            }
        }
    }
    return rows.values.sortedByDescending { it.count }
}

fun computeByStaff(leads: List<Lead>, staff: List<StaffMember>): List<BreakdownRow> {
    val staffNames = staff.associate { it.id to it.name }
    return leads.groupBy { it.assignedStaffId?.let { id -> staffNames[id] } ?: "Unassigned" }
        .map { (label, group) -> BreakdownRow(label, group.size, group.count { it.status == "CONVERTED" }) }
        .sortedByDescending { it.count }
}

fun computeNotConvertedReasons(leads: List<Lead>): List<Pair<String, Int>> {
    val notConverted = leads.filter { it.status == "NOT_CONVERTED" }
    return notConverted.groupBy { it.notConvertedReason?.let { r -> NOT_CONVERTED_REASON_LABELS[r] ?: r } ?: "Not specified" }
        .map { (label, group) -> label to group.size }
        .sortedByDescending { it.second }
}

/** The "why we lose families" report -- Master Plan Part 7's flagged demo material. */
data class UnmetDemandEntry(val reasonLabel: String, val detail: String)

fun computeUnmetDemand(leads: List<Lead>): List<UnmetDemandEntry> =
    leads
        .filter { it.status == "NOT_CONVERTED" && it.notConvertedReason in setOf("amenity_missing", "service_not_offered") }
        .mapNotNull { lead ->
            val detail = lead.notConvertedDetail?.trim()
            if (detail.isNullOrBlank()) null else UnmetDemandEntry(
                reasonLabel = NOT_CONVERTED_REASON_LABELS[lead.notConvertedReason] ?: lead.notConvertedReason.orEmpty(),
                detail = detail,
            )
        }

private val BUDGET_BUCKETS = listOf(
    "Under ₹20k" to (0..19999),
    "₹20k–40k" to (20000..39999),
    "₹40k–60k" to (40000..59999),
    "₹60k–80k" to (60000..79999),
    "₹80k+" to (80000..Int.MAX_VALUE),
)

fun computeBudgetDistribution(leads: List<Lead>): List<Pair<String, Int>> {
    val withBudget = leads.mapNotNull { it.budgetMin?.toInt() ?: it.budgetMax?.toInt() }
    val bucketCounts = BUDGET_BUCKETS.map { (label, range) -> label to withBudget.count { it in range } }
    val notSpecified = leads.size - withBudget.size
    return bucketCounts + ("Not specified" to notSpecified)
}
