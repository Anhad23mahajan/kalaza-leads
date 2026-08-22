package com.kalazacare.leads.ui.theme

import androidx.compose.ui.graphics.Color

// ── Kalaza Leads Brand Colors ────────────────────────────────────────────────
// Deliberately distinct from Kalaza Care's red/maroon — this is a companion app
// installed alongside it on the same phone, so staff need to tell them apart at
// a glance on the home screen.
val LeadsTeal        = Color(0xFF00695C)   // Primary brand teal — buttons, FABs, accents
val LeadsLightTeal   = Color(0xFF00897B)   // Hover / pressed teal
val LeadsDarkTeal    = Color(0xFF003D33)   // Status bar, top bar background
val LeadsAmber       = Color(0xFFE58A00)   // Follow-up-due accent (the killer feature — see spec)

// ── Surface Tones ─────────────────────────────────────────────────────────────
val White            = Color(0xFFFFFFFF)
val SurfaceVariant   = Color(0xFFF8F8F8)
val Outline          = Color(0xFFDFE0E0)

// ── Text ──────────────────────────────────────────────────────────────────────
val OnSurface        = Color(0xFF1A1A1A)
val OnSurfaceVariant = Color(0xFF69727D)

// ── Semantic Status Colors ────────────────────────────────────────────────────
val StatusSuccess    = Color(0xFF00D084)   // Converted
val StatusWarning    = Color(0xFFFCB900)   // Follow-up due soon
val StatusError      = Color(0xFFCF2E2E)   // Overdue / not interested
