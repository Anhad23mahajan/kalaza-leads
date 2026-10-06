package com.kalazacare.leads

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kalazacare.leads.ui.MainActivity
import com.kalazacare.leads.ui.theme.KalazaLeadsTheme

/** Shown by [CrashHandler] after a crash. Plain words first; the technical details are folded away. */
class CrashActivity : ComponentActivity() {
    companion object {
        const val EXTRA_DETAILS = "crash_details"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val details = intent.getStringExtra(EXTRA_DETAILS) ?: "No details were recorded."
        setContent {
            KalazaLeadsTheme {
                CrashScreen(
                    details = details,
                    onRestart = {
                        startActivity(
                            Intent(this, MainActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK),
                        )
                        finish()
                    },
                    onShare = {
                        val send = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Kalaza Leads crash report")
                            putExtra(Intent.EXTRA_TEXT, details)
                        }
                        startActivity(Intent.createChooser(send, "Share crash details"))
                    },
                )
            }
        }
    }
}

@Composable
private fun CrashScreen(details: String, onRestart: () -> Unit, onShare: () -> Unit) {
    var showDetails by remember { mutableStateOf(false) }
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(24.dp),
        ) {
            Text("Something went wrong", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text(
                "Kalaza Leads had to close. Your saved leads are safe. Tap Restart to continue; " +
                    "if this keeps happening, share the details with whoever looks after the app.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(20.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onRestart, modifier = Modifier.fillMaxWidth()) { Text("Restart") }
                OutlinedButton(onClick = onShare, modifier = Modifier.fillMaxWidth()) { Text("Share details") }
            }
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = { showDetails = !showDetails }) {
                Text(if (showDetails) "Hide technical details" else "Show technical details")
            }
            if (showDetails) {
                Text(
                    details,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                )
            }
        }
    }
}
