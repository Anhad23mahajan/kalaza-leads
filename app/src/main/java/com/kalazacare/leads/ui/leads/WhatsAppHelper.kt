package com.kalazacare.leads.ui.leads

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.kalazacare.leads.data.model.Lead
import java.net.URLEncoder

/** Groups templates in the "Send WhatsApp" list on Lead Detail, in display order. */
enum class WhatsAppCategory(val label: String) {
    REACHING_OUT("Reaching out"),
    CHECKING_IN("Checking in"),
    AFTER_A_VISIT("After a visit"),
    NOT_CONSIDERING("If they're not considering"),
}

/**
 * Wording below is placeholder, written to be usable as-is but expected to be swapped for the
 * supervisor's own phrasing once he gives it (docs/ROADMAP.md §4, open item). All templates are
 * shown regardless of the lead's pipeline stage, by the supervisor's own preference -- he wants
 * the freedom to pick any message based on the actual conversation, not have the app guess.
 */
enum class WhatsAppTemplate(val category: WhatsAppCategory, val label: String) {
    THANK_YOU(WhatsAppCategory.REACHING_OUT, "Thank you for reaching out"),
    FOLLOW_UP(WhatsAppCategory.CHECKING_IN, "General follow-up"),
    STILL_DECIDING(WhatsAppCategory.CHECKING_IN, "Still deciding? (they seemed interested)"),
    GONE_QUIET(WhatsAppCategory.CHECKING_IN, "Haven't heard back from them"),
    CONFIRM_VISIT(WhatsAppCategory.CHECKING_IN, "Confirm a scheduled visit"),
    VISIT_FEEDBACK(WhatsAppCategory.AFTER_A_VISIT, "How was your visit?"),
    WHAT_HAPPENED(WhatsAppCategory.NOT_CONSIDERING, "Ask what happened / the issue"),
}

/**
 * Static templates, not AI-generated -- these are reviewed and sent manually by the admin via
 * a `wa.me` deep link. (An earlier plan to draft these with an AI service as part of a WhatsApp
 * Business Platform bot was abandoned entirely -- see docs/HANDOFF.md §15 -- there is no
 * server-side component in this project.)
 */
fun buildWhatsAppMessage(template: WhatsAppTemplate, lead: Lead): String {
    val firstName = lead.enquirerName.trim().substringBefore(" ").ifBlank { "there" }
    val serviceLabel = lead.serviceWanted.firstOrNull()?.let { SERVICE_LABELS[it] }
    val forWhom = lead.patientName?.takeIf { it.isNotBlank() && it != lead.enquirerName }

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
        WhatsAppTemplate.STILL_DECIDING -> buildString {
            append("Hi $firstName, just checking in about Kalaza Care")
            if (serviceLabel != null) append(" and $serviceLabel")
            if (forWhom != null) append(" for $forWhom")
            append(". Would you like to schedule a visit, or is there anything else I can help with before you decide?")
        }
        WhatsAppTemplate.GONE_QUIET -> {
            "Hi $firstName, we haven't heard back since we last spoke about Kalaza Care. " +
                "No pressure at all. Just let us know if you'd like to continue the conversation, or if your plans have changed."
        }
        WhatsAppTemplate.CONFIRM_VISIT -> buildString {
            append("Hi $firstName, just confirming your visit to Kalaza Care")
            if (lead.plannedVisitDate != null) append(" on ${displayDate(lead.plannedVisitDate)}")
            append(". Let us know if that still works for you, or if you'd like to reschedule.")
        }
        WhatsAppTemplate.VISIT_FEEDBACK -> {
            "Hi $firstName, thank you for visiting Kalaza Care. How was your experience? " +
                "Was our staff helpful? Anything we could improve?"
        }
        WhatsAppTemplate.WHAT_HAPPENED -> {
            "Hi $firstName, thank you again for considering Kalaza Care. We understand you've decided not to move forward for now. " +
                "Would you mind sharing what made you decide against it? It really helps us improve."
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
