package com.kalazacare.leads.ui.leads

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.kalazacare.leads.data.model.Lead
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.delay
import java.time.LocalDate

/** How often the list reloads by itself while it is on screen. */
private const val AUTO_REFRESH_MS = 60_000L

/** Case-insensitive match on the names, location and phone (digits only, so "98765 43210" finds it too). */
private fun Lead.matches(query: String): Boolean {
    val q = query.trim().lowercase()
    if (q.isEmpty()) return true
    val digits = q.filter { it.isDigit() }
    return enquirerName.lowercase().contains(q) ||
        patientName?.lowercase()?.contains(q) == true ||
        enquirerLocation?.lowercase()?.contains(q) == true ||
        (digits.length >= 3 && enquirerPhone.contains(digits))
}

private val ACTIVE_STATUSES = setOf("NEW", "CONTACTED", "INFO_SENT", "VISIT_SCHEDULED", "VISITED", "CONSIDERING")
private val TERMINAL_STATUSES = setOf("CONVERTED", "NOT_CONVERTED", "DORMANT")

private data class Segment(val label: String, val filter: (List<Lead>, String) -> List<Lead>)

private val SEGMENTS = listOf(
    Segment("All") { leads, _ -> leads },
    Segment("Follow-ups Due") { leads, today ->
        leads
            .filter { it.nextFollowUpDate != null && it.nextFollowUpDate <= today && it.status !in TERMINAL_STATUSES }
            .sortedBy { it.nextFollowUpDate }
    },
    Segment("Active") { leads, _ -> leads.filter { it.status in ACTIVE_STATUSES } },
    Segment("Converted") { leads, _ -> leads.filter { it.status == "CONVERTED" } },
    Segment("Not Converted") { leads, _ -> leads.filter { it.status == "NOT_CONVERTED" } },
    Segment("Dormant") { leads, _ -> leads.filter { it.status == "DORMANT" } },
    Segment("Backup") { leads, _ -> leads.filter { it.status == "BACKUP" } },
)

/** Index of the "Follow-ups Due" tab, for opening straight onto it from a notification. */
val FOLLOW_UPS_TAB_INDEX: Int = SEGMENTS.indexOfFirst { it.label == "Follow-ups Due" }

/** [selectedTab] lives in MainActivity so it survives opening a lead and coming back. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadsScreen(
    viewModel: LeadsViewModel,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onLeadClick: (Lead) -> Unit,
    onViewReports: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onLogout: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var searchOpen by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    // Re-read on every reload, so an app left open past midnight moves follow-ups to "due".
    val today = remember(state.leads) { LocalDate.now().toString() }

    val searchedLeads = remember(state.leads, query) { state.leads.filter { it.matches(query) } }
    val visibleLeads = remember(searchedLeads, today, selectedTab) {
        SEGMENTS[selectedTab].filter(searchedLeads, today)
    }

    // Back closes the search first, instead of leaving the app.
    BackHandler(enabled = searchOpen) {
        searchOpen = false
        query = ""
    }

    // Auto-refresh: reload when the app comes back to the foreground, and every minute while the
    // list is on screen, so a new Google Form enquiry shows up without restarting the app.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        var firstResume = true
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // The first resume is the screen opening; MainActivity has just loaded the list.
                if (firstResume) firstResume = false else viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(Unit) {
        while (true) {
            delay(AUTO_REFRESH_MS)
            if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                viewModel.refresh()
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Leads") },
                    actions = {
                        IconButton(onClick = {
                            searchOpen = !searchOpen
                            if (!searchOpen) query = ""
                        }) {
                            Icon(
                                if (searchOpen) Icons.Filled.Close else Icons.Filled.Search,
                                contentDescription = if (searchOpen) "Close search" else "Search leads",
                            )
                        }
                        IconButton(onClick = { viewModel.refresh() }, enabled = !state.isLoading) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                        }
                        IconButton(
                            onClick = {
                                val label = SEGMENTS[selectedTab].label
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                    val saved = exportLeadsToDownloads(context, visibleLeads, label)
                                    viewModel.showMessage(
                                        if (saved != null) "Saved to Downloads: $saved"
                                        else "Couldn't save the file. Please try again.",
                                    )
                                } else {
                                    exportLeadsToDownloads(context, visibleLeads, label)
                                }
                            },
                            enabled = visibleLeads.isNotEmpty(),
                        ) {
                            Icon(Icons.Filled.Download, contentDescription = "Download ${SEGMENTS[selectedTab].label} as CSV")
                        }
                        IconButton(onClick = onViewReports) {
                            Icon(Icons.Filled.Assessment, contentDescription = "Reports")
                        }
                        IconButton(onClick = onOpenSettings) {
                            Icon(Icons.Filled.Settings, contentDescription = "Settings")
                        }
                        IconButton(onClick = { showLogoutConfirm = true }) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                        }
                    },
                )
                if (searchOpen) {
                    val focusRequester = remember { FocusRequester() }
                    LaunchedEffect(Unit) { focusRequester.requestFocus() }
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Search name, patient, phone or place") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .focusRequester(focusRequester),
                    )
                }
                ScrollableTabRow(selectedTabIndex = selectedTab) {
                    SEGMENTS.forEachIndexed { index, segment ->
                        val count = remember(searchedLeads, today) { segment.filter(searchedLeads, today).size }
                        Tab(
                            selected = selectedTab == index,
                            onClick = { onTabSelected(index) },
                            text = { Text("${segment.label} ($count)") },
                        )
                    }
                }
                // A reload with a list already showing: a thin bar instead of blanking the screen.
                if (state.isLoading && state.leads.isNotEmpty()) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                if (state.errorMessage != null && state.leads.isNotEmpty()) {
                    Text(
                        text = "Couldn't refresh. Showing the last loaded list.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
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
                // A failed load must not look like an empty list ("No leads") -- say so and offer a retry.
                state.errorMessage != null && state.leads.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = state.errorMessage!!,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                        )
                        Button(
                            onClick = { viewModel.refresh() },
                            modifier = Modifier.padding(top = 16.dp),
                        ) { Text("Retry") }
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
                            text = when {
                                query.isNotBlank() -> "No leads match \"${query.trim()}\"."
                                SEGMENTS[selectedTab].label == "Follow-ups Due" -> "Nothing due right now."
                                else -> "No leads in this list."
                            },
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = if (state.leads.isEmpty()) "New enquiries arrive from the Google Form." else " ",
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

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Log out?") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutConfirm = false
                    onLogout()
                }) { Text("Log Out") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) { Text("Cancel") }
            },
        )
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
            // Closed leads (converted / not converted / dormant) are never "due" -- same rule as the
            // Follow-ups Due tab and the daily reminder.
            if (lead.nextFollowUpDate != null && lead.status !in TERMINAL_STATUSES) {
                val isOverdue = lead.nextFollowUpDate < today
                val isDueToday = lead.nextFollowUpDate == today
                if (isOverdue || isDueToday) {
                    Text(
                        text = if (isOverdue) "Overdue — was due ${displayDate(lead.nextFollowUpDate)}" else "Due today",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isOverdue) Color(0xFFCF2E2E) else Color(0xFFE58A00),
                    )
                }
            }
        }
    }
}
