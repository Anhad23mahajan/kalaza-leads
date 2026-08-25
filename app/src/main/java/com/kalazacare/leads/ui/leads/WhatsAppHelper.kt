package com.kalazacare.leads.ui.leads

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.kalazacare.leads.data.model.Lead
import java.net.URLEncoder

enum class WhatsAppTemplate(val label: String) {
    THANK_YOU("Thank You"),
    FOLLOW_UP("Follow-up"),
    VISIT_FEEDBACK("Visit Feedback"),
}

/**
 * Templated draft messages (Master Plan Part 6.4, S1/S2/S5). These are static
 * templates, not AI-generated -- real drafting/personalization is an Edge
 * Function + Claude API job for later (Track D), out of scope for the
 * client-only wa.me MVP bridge.
 */
fun buildWhatsAppMessage(template: WhatsAppTemplate, lead: Lead): String {
    val firstName = lead.enquirerName.trim().substringBefore(" ").ifBlank { "there" }
    val serviceLabel = lead.serviceWanted.firstOrNull()?.let { SERVICE_LABELS[it] }

    return when (template) {
        WhatsAppTemplate.THANK_YOU -> buildString {
            append("Hi $firstName, thank you for reaching out to Kalaza Care")
            if (serviceLabel != null) append(" about $serviceLabel")
            append(". We're happy to share more details, our price list, and answer any questions. When would be a good time to talk?")
        }
        WhatsAppTemplate.FOLLOW_UP -> buildString {
            append("Hi $firstName, following up on your enquiry with Kalaza Care")
            if (serviceLabel != null) append(" about $serviceLabel")
            append(". Have you had a chance to decide? We'd be happy to arrange a visit at a time that suits you.")
        }
        WhatsAppTemplate.VISIT_FEEDBACK -> {
            "Hi $firstName, thank you for visiting Kalaza Care. How was your experience? " +
                "Was our staff helpful? Anything we could improve?"
        }
    }
}

fun launchWhatsApp(context: Context, lead: Lead, message: String) {
    val digitsOnly = lead.enquirerPhone.filter { it.isDigit() }
    val countryDigits = lead.enquirerCountryCode.filter { it.isDigit() }
    val fullNumber = "$countryDigits$digitsOnly"
    val encodedMessage = URLEncoder.encode(message, "UTF-8").replace("+", "%20")
    val uri = Uri.parse("https://wa.me/$fullNumber?text=$encodedMessage")
    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
}
