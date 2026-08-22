package com.kalazacare.leads.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Placeholder login screen — scaffolding only, not wired to Supabase Auth yet.
 *
 * Kalaza Care logs staff in by NAME (not email) via a security-definer RPC that
 * resolves a synthesized auth email server-side, because pre-login there's no
 * auth.uid() yet for RLS to key off. The same pattern is the intended approach
 * here too, once real auth work starts — see the Kalaza Care source
 * (SupabaseAuthRepository.kt) for the exact RPC shape to replicate.
 *
 * TODO (not yet built):
 *  - Wire staffNameField + passwordField to a real AuthViewModel
 *  - Call the staff_login_lookup RPC (or an equivalent for this app) + Supabase Auth signIn
 *  - Navigate to the main lead-list screen on success
 *  - Surface real error states instead of the current no-op onLoginClick
 */
@Composable
fun LoginScreen(
    onLoginClick: (staffName: String, password: String) -> Unit = { _, _ -> },
) {
    var staffName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingValues(24.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Kalaza Leads",
            style = MaterialTheme.typography.headlineLarge,
        )
        Text(
            text = "Enquiry follow-up, sorted.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 32.dp))

        OutlinedTextField(
            value = staffName,
            onValueChange = { staffName = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 20.dp))

        Button(
            onClick = { onLoginClick(staffName, password) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Login")
        }
    }
}
