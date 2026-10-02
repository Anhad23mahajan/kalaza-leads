package com.kalazacare.leads.ui.leads

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kalazacare.leads.data.model.Lead

/**
 * Expandable, categorized list of wa.me quick-message templates -- replaces the old 3 fixed
 * buttons (docs/ROADMAP.md §4). Tapping a template still opens WhatsApp with the message
 * pre-filled for the supervisor to review/edit and send himself; nothing is sent automatically.
 */
@Composable
fun WhatsAppQuickMessages(lead: Lead) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Choose a message to send", style = MaterialTheme.typography.bodyLarge)
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse message list" else "Expand message list",
                )
            }
        }

        if (expanded) {
            WhatsAppCategory.entries.forEach { category ->
                val templates = WhatsAppTemplate.entries.filter { it.category == category }
                if (templates.isNotEmpty()) {
                    Text(
                        text = category.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 14.dp, bottom = 6.dp),
                    )
                    templates.forEach { template ->
                        OutlinedButton(
                            onClick = {
                                launchWhatsApp(context, lead, buildWhatsAppMessage(template, lead))
                                expanded = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                        ) {
                            Text(template.label)
                        }
                    }
                }
            }
        }
    }
}
