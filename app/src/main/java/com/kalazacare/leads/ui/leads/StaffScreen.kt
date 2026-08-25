package com.kalazacare.leads.ui.leads

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kalazacare.leads.data.model.NewStaffRequest
import com.kalazacare.leads.data.model.StaffMember
import com.kalazacare.leads.data.model.UpdateStaffRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffScreen(
    viewModel: StaffViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    var editTarget by remember { mutableStateOf<StaffMember?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Staff") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add staff")
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading && state.staff.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.staff.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("No staff added yet.", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Tap + to add the first one.",
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
                        items(state.staff) { staff ->
                            StaffCard(
                                staff = staff,
                                onClick = { editTarget = staff },
                                onToggleActive = { isActive ->
                                    val id = staff.id ?: return@StaffCard
                                    viewModel.updateStaff(
                                        id,
                                        UpdateStaffRequest(
                                            name = staff.name,
                                            phone = staff.phone,
                                            role = staff.role,
                                            isActive = isActive,
                                        ),
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        StaffEditDialog(
            staff = null,
            onDismiss = { showAddDialog = false },
            onSave = { name, phone, role ->
                viewModel.addStaff(NewStaffRequest(name = name, phone = phone, role = role))
                showAddDialog = false
            },
        )
    }

    editTarget?.let { staff ->
        StaffEditDialog(
            staff = staff,
            onDismiss = { editTarget = null },
            onSave = { name, phone, role ->
                val id = staff.id ?: return@StaffEditDialog
                viewModel.updateStaff(
                    id,
                    UpdateStaffRequest(name = name, phone = phone, role = role, isActive = staff.isActive),
                )
                editTarget = null
            },
        )
    }
}

@Composable
private fun StaffCard(
    staff: StaffMember,
    onClick: () -> Unit,
    onToggleActive: (Boolean) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = staff.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 2.dp),
                )
                Text(
                    text = STAFF_ROLE_LABELS[staff.role] ?: staff.role,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                if (!staff.phone.isNullOrBlank()) {
                    Text(
                        text = staff.phone,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Switch(checked = staff.isActive, onCheckedChange = onToggleActive)
                TextButton(onClick = onClick) { Text("Edit") }
            }
        }
    }
}

@Composable
private fun StaffEditDialog(
    staff: StaffMember?,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String?, role: String) -> Unit,
) {
    var name by remember { mutableStateOf(staff?.name ?: "") }
    var phone by remember { mutableStateOf(staff?.phone ?: "") }
    var role by remember { mutableStateOf(staff?.role ?: "coordinator") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (staff == null) "Add Staff" else "Edit Staff") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Spacer(Modifier.padding(top = 10.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                )
                Spacer(Modifier.padding(top = 10.dp))
                EnumDropdown("Role", STAFF_ROLES, STAFF_ROLE_LABELS, role, { if (it != null) role = it })
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name.trim(), phone.trim().ifBlank { null }, role) },
                enabled = name.isNotBlank(),
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
