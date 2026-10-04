package com.kalazacare.leads.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kalazacare.leads.data.remote.SupabaseClients
import com.kalazacare.leads.data.repository.SupabaseAuthRepository
import com.kalazacare.leads.data.repository.SupabaseLeadsRepository
import com.kalazacare.leads.notifications.NotificationHelper
import com.kalazacare.leads.ui.leads.FOLLOW_UPS_TAB_INDEX
import com.kalazacare.leads.ui.leads.LeadDetailScreen
import com.kalazacare.leads.ui.leads.LeadsScreen
import com.kalazacare.leads.ui.leads.LeadsViewModel
import com.kalazacare.leads.ui.leads.ReportsScreen
import com.kalazacare.leads.ui.leads.SettingsScreen
import com.kalazacare.leads.ui.login.LoginScreen
import com.kalazacare.leads.ui.login.LoginViewModel
import com.kalazacare.leads.ui.theme.KalazaLeadsTheme
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus

private enum class Screen { LOGIN, LEADS, LEAD_DETAIL, REPORTS, SETTINGS }

/** Where a tapped follow-up notification wants to land once the admin is signed in. */
private sealed interface PendingOpen {
    data class Lead(val id: String) : PendingOpen
    data object FollowUps : PendingOpen
}

class MainActivity : ComponentActivity() {
    private var currentScreen by mutableStateOf(Screen.LOGIN)

    // Held here (not inside LeadsScreen) so the chosen tab survives opening a lead and coming back.
    private var leadsTab by mutableIntStateOf(0)

    // True until we know whether a saved session exists, so the login form doesn't flash up
    // for a signed-in admin.
    private var checkingSession by mutableStateOf(true)

    private var pendingOpen: PendingOpen? = null

    private lateinit var leadsViewModel: LeadsViewModel

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Follow-up reminder notifications need this on Android 13+.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        val authRepository = SupabaseAuthRepository(SupabaseClients.main)
        val leadsRepository = SupabaseLeadsRepository(SupabaseClients.main)
        val loginViewModel = LoginViewModel(authRepository)
        leadsViewModel = LeadsViewModel(leadsRepository)

        // Only on a fresh start: after a recreation Android re-delivers the original intent,
        // which would otherwise reopen the same lead.
        if (savedInstanceState == null) readOpenRequest(intent)

        setContent {
            KalazaLeadsTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // The Supabase session is stored on the phone, so an admin who has not logged
                    // out goes straight in (this is also what a tapped notification relies on).
                    LaunchedEffect(Unit) {
                        val auth = SupabaseClients.main.auth
                        auth.awaitInitialization()
                        if (auth.sessionStatus.value is SessionStatus.Authenticated) {
                            enterApp()
                        } else {
                            checkingSession = false
                        }
                    }

                    // Without this, the system Back gesture closes the whole app from any screen.
                    BackHandler(
                        enabled = currentScreen == Screen.LEAD_DETAIL ||
                            currentScreen == Screen.REPORTS ||
                            currentScreen == Screen.SETTINGS,
                    ) {
                        if (currentScreen == Screen.LEAD_DETAIL) leadsViewModel.clearSelection()
                        currentScreen = Screen.LEADS
                    }

                    if (checkingSession) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        when (currentScreen) {
                            Screen.LOGIN -> LoginScreen(
                                viewModel = loginViewModel,
                                onLoginSuccess = { enterApp() },
                            )
                            Screen.LEADS -> LeadsScreen(
                                viewModel = leadsViewModel,
                                selectedTab = leadsTab,
                                onTabSelected = { leadsTab = it },
                                onLeadClick = { lead ->
                                    leadsViewModel.selectLead(lead)
                                    currentScreen = Screen.LEAD_DETAIL
                                },
                                onViewReports = { currentScreen = Screen.REPORTS },
                                onOpenSettings = { currentScreen = Screen.SETTINGS },
                                onLogout = {
                                    loginViewModel.logout()
                                    leadsTab = 0
                                    currentScreen = Screen.LOGIN
                                },
                            )
                            Screen.LEAD_DETAIL -> {
                                val leadsState by leadsViewModel.state.collectAsState()
                                val selected = leadsState.selectedLead
                                if (selected != null) {
                                    // The form fields are remembered without a key, so when a tapped
                                    // notification switches lead A to lead B while this screen is
                                    // open, the screen must be rebuilt -- otherwise it would keep A's
                                    // values and Save would write them over B.
                                    key(selected.id) {
                                        LeadDetailScreen(
                                            lead = selected,
                                            viewModel = leadsViewModel,
                                            onBack = {
                                                leadsViewModel.clearSelection()
                                                currentScreen = Screen.LEADS
                                            },
                                            onSaved = { currentScreen = Screen.LEADS },
                                        )
                                    }
                                } else {
                                    currentScreen = Screen.LEADS
                                }
                            }
                            Screen.REPORTS -> ReportsScreen(
                                leadsViewModel = leadsViewModel,
                                onBack = { currentScreen = Screen.LEADS },
                            )
                            Screen.SETTINGS -> SettingsScreen(
                                onBack = { currentScreen = Screen.LEADS },
                            )
                        }
                    }
                }
            }
        }
    }

    // launchMode is singleTask, so tapping a notification while the app is already running
    // arrives here instead of creating a new activity.
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        readOpenRequest(intent)
        val signedIn = SupabaseClients.main.auth.sessionStatus.value is SessionStatus.Authenticated
        if (pendingOpen != null && !checkingSession && signedIn) {
            enterApp()
        }
        // Otherwise the request stays pending: the session check at start-up, or the next
        // successful login, picks it up through enterApp().
    }

    private fun readOpenRequest(intent: Intent?) {
        val leadId = intent?.getStringExtra(NotificationHelper.EXTRA_OPEN_LEAD_ID)
        val followUps = intent?.getBooleanExtra(NotificationHelper.EXTRA_OPEN_FOLLOW_UPS, false) == true
        pendingOpen = when {
            leadId != null -> PendingOpen.Lead(leadId)
            followUps -> PendingOpen.FollowUps
            else -> pendingOpen
        }
        intent?.removeExtra(NotificationHelper.EXTRA_OPEN_LEAD_ID)
        intent?.removeExtra(NotificationHelper.EXTRA_OPEN_FOLLOW_UPS)
    }

    /** Called once a session exists (restored, or just logged in): load the leads and navigate. */
    private fun enterApp() {
        val open = pendingOpen
        pendingOpen = null

        if (open == null) {
            leadsViewModel.refresh()
            currentScreen = Screen.LEADS
            checkingSession = false
            return
        }

        // Coming from a notification: load fresh data first, so the lead that opens is current
        // (saving an old copy would overwrite newer edits).
        leadsViewModel.refresh {
            val target = (open as? PendingOpen.Lead)
                ?.let { request -> leadsViewModel.state.value.leads.firstOrNull { it.id == request.id } }
            if (target != null) {
                leadsViewModel.selectLead(target)
                currentScreen = Screen.LEAD_DETAIL
            } else {
                leadsTab = FOLLOW_UPS_TAB_INDEX
                currentScreen = Screen.LEADS
            }
            checkingSession = false
        }
    }
}
