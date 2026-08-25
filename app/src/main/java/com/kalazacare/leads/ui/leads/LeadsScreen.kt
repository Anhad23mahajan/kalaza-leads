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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.unit.dp
import com.kalazacare.leads.data.model.Lead
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadsScreen(
    viewModel: LeadsViewModel,
    onAddLead: () -> Unit,
    onLeadClick: (Lead) -> Unit,
    onLogout: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val today = remember { LocalDate.now().toString() }

    val dueLeads = remember(state.leads, today) {
        state.leads
            .filter { it.nextFollowUpDate != null && it.nextFollowUpDate <= today && it.status !in TERMINAL_STATUSES }
            .sortedBy { it.nextFollowUpDate }
    }

    val visibleLeads = if (selectedTab == 0) dueLeads else state.leads

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Leads") },
                    actions = {
                        IconButton(onClick = onLogout) {
                            Text("Logout", modifier = Modifier.padding(end = 12.dp))
                        }
                    },
                )
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Follow-ups Due (${dueLeads.size})") },
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("All Leads (${state.leads.size})") },
                    )
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
                visibleLeads.isEmpty() && selectedTab == 0 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Nothing due right now.",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "Set a next follow-up date on a lead and it'll show up here.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
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
                            text = "No enquiries yet.",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "Tap + to add the first one.",
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

private val TERMINAL_STATUSES = setOf("CONVERTED", "NOT_CONVERTED", "DORMANT")

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
