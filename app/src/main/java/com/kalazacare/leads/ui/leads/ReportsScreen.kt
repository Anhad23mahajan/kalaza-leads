package com.kalazacare.leads.ui.leads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    leadsViewModel: LeadsViewModel,
    staffViewModel: StaffViewModel,
    onBack: () -> Unit,
) {
    val leadsState by leadsViewModel.state.collectAsState()
    val staffState by staffViewModel.state.collectAsState()
    val leads = leadsState.leads

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            if (leads.isEmpty()) {
                Text(
                    "No leads yet — reports will fill in once enquiries are added.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                return@Column
            }

            OverviewSection(computeOverview(leads))

            ReportSection("Pipeline funnel") {
                BreakdownList(computeStatusFunnel(leads))
            }

            ReportSection("By source") {
                BreakdownRowsList(computeBySource(leads))
            }

            ReportSection("By service requested") {
                BreakdownRowsList(computeByService(leads))
            }

            ReportSection("By assigned staff") {
                BreakdownRowsList(computeByStaff(leads, staffState.staff))
            }

            ReportSection("Why they didn't convert") {
                BreakdownList(computeNotConvertedReasons(leads))
            }

            ReportSection("Unmet demand — what we're missing") {
                val unmet = computeUnmetDemand(leads)
                if (unmet.isEmpty()) {
                    Text(
                        "No amenity/service-gap details logged yet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    unmet.forEach { entry ->
                        Column(modifier = Modifier.padding(bottom = 8.dp)) {
                            Text(entry.reasonLabel, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            Text(entry.detail, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            ReportSection("Budget distribution") {
                BreakdownList(computeBudgetDistribution(leads))
            }

            Spacer(Modifier.padding(bottom = 24.dp))
        }
    }
}

@Composable
private fun OverviewSection(overview: ReportOverview) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Overview", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.padding(top = 10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatTile("Total enquiries", overview.total.toString())
                StatTile("Converted", overview.converted.toString())
                StatTile(
                    "Conversion rate",
                    overview.conversionRateOfDecided?.let { "${(it * 100).roundToInt()}%" } ?: "—",
                )
                StatTile("Median days to convert", overview.medianDaysToConvert?.toString() ?: "—")
            }
        }
    }
    Spacer(Modifier.padding(top = 16.dp))
}

@Composable
private fun StatTile(label: String, value: String) {
    Column {
        Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ReportSection(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.padding(top = 10.dp))
            content()
        }
    }
}

@Composable
private fun BreakdownList(rows: List<Pair<String, Int>>) {
    val nonZero = rows.filter { it.second > 0 }
    if (nonZero.isEmpty()) {
        Text("No data yet.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        return
    }
    val max = nonZero.maxOf { it.second }
    nonZero.forEachIndexed { index, (label, count) ->
        BarRow(label = label, count = count, secondaryLabel = null, fraction = count.toFloat() / max)
        if (index != nonZero.lastIndex) HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
    }
}

@Composable
private fun BreakdownRowsList(rows: List<BreakdownRow>) {
    if (rows.isEmpty()) {
        Text("No data yet.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        return
    }
    val max = rows.maxOf { it.count }
    rows.forEachIndexed { index, row ->
        val conversionPct = (row.conversionRate * 100).roundToInt()
        BarRow(
            label = row.label,
            count = row.count,
            secondaryLabel = "$conversionPct% converted",
            fraction = row.count.toFloat() / max,
        )
        if (index != rows.lastIndex) HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
    }
}

@Composable
private fun BarRow(label: String, count: Int, secondaryLabel: String?, fraction: Float) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(
                if (secondaryLabel != null) "$count · $secondaryLabel" else count.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.padding(top = 4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction.coerceIn(0f, 1f))
                    .height(8.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp)),
            )
        }
    }
}
