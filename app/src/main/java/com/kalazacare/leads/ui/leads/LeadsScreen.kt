package com.kalazacare.leads.ui.leads

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kalazacare.leads.data.model.Lead
import java.time.LocalDate

private val ACTIVE_STATUSES = setOf("NEW", "CONTACTED", "INFO_SENT", "VISIT_SCHEDULED", "VISITED", "CONSIDERING")
private val TERMINAL_STATUSES = setOf("CONVERTED", "NOT_CONVERTED", "DORMANT")

private data class Segment(val label: String, val filter: (List<Lead>, String) -> List<Lead>)

private val SEGMENTS = listOf(
    Segment("Follow-ups Due") { leads, today ->
        leads
            .filter { it.nextFollowUpDate != null && it.nextFollowUpDate <= today && it.status !in TERMINAL_STATUSES }
            .sortedBy { it.nextFollowUpDate }
    },
    Segment("All") { leads, _ -> leads },
    Segment("Active") { leads, _ -> leads.filter { it.status in ACTIVE_STATUSES } },
    Segment("Converted") { leads, _ -> leads.filter { it.status == "CONVERTED" } },
    Segment("Not Converted") { leads, _ -> leads.filter { it.status == "NOT_CONVERTED" } },
    Segment("Dormant") { leads, _ -> leads.filter { it.status == "DORMANT" } },
    Segment("Backup") { leads, _ -> leads.filter { it.status == "BACKUP" } },
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadsScreen(
    viewModel: LeadsViewModel,
    onAddLead: () -> Unit,
    onLeadClick: (Lead) -> Unit,
    onManageStaff: () -> Unit = {},
    onViewReports: () -> Unit = {},
    onLogout: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val today = remember { LocalDate.now().toString() }

    val visibleLeads = remember(state.leads, today, selectedTab) {
        SEGMENTS[selectedTab].filter(state.leads, today)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Leads") },
                    actions = {
                        IconButton(
                            onClick = {
                                exportAndShareLeads(context, visibleLeads, SEGMENTS[selectedTab].label)
                            },
                            enabled = visibleLeads.isNotEmpty(),
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = "Export ${SEGMENTS[selectedTab].label} to CSV")
                        }
                        IconButton(onClick = onManageStaff) {
                            Icon(Icons.Filled.People, contentDescription = "Manage staff")
                        }
                        IconButton(onClick = onViewReports) {
                            Icon(Icons.Filled.Assessment, contentDescription = "Reports")
                        }
                        IconButton(onClick = onLogout) {
                            Text("Logout", modifier = Modifier.padding(end = 12.dp))
                        }
                    },
                )
                ScrollableTabRow(selectedTabIndex = selectedTab) {
                    SEGMENTS.forEachIndexed { index, segment ->
                        val count = remember(state.leads, today) { segment.filter(state.leads, today).size }
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text("${segment.label} ($count)") },
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddLead) {
                Icon(Icons.Filled.Add, contentDescription = "Add lead")
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when {
                state.isLoading && state.leads.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                visibleLeads.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = if (selectedTab == 0) "Nothing due right now." else "No leads in this list.",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = if (state.leads.isEmpty()) "Tap + to add the first one." else " ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(visibleLeads) { lead ->
                            LeadCard(lead, today = today, onClick = { onLeadClick(lead) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeadCard(lead: Lead, today: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = lead.enquirerName,
                style = MaterialTheme.typography.titleMedium,
            )
            if (!lead.patientName.isNullOrBlank()) {
                Text(
                    text = "For: ${lead.patientName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = "${lead.enquirerCountryCode} ${lead.enquirerPhone}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
            Text(
                text = STATUS_LABELS[lead.status] ?: lead.status,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            if (lead.status == "NOT_CONVERTED" && lead.notConvertedReason != null) {
                Text(
                    text = "Reason: ${NOT_CONVERTED_REASON_LABELS[lead.notConvertedReason] ?: lead.notConvertedReason}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (lead.nextFollowUpDate != null) {
                val isOverdue = lead.nextFollowUpDate < today
                val isDueToday = lead.nextFollowUpDate == today
                if (isOverdue || isDueToday) {
                    Text(
                        text = if (isOverdue) "Overdue — was due ${lead.nextFollowUpDate}" else "Due today",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isOverdue) Color(0xFFCF2E2E) else Color(0xFFE58A00),
                    )
                }
            }
        }
    }
}
