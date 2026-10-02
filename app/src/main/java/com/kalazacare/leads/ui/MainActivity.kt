package com.kalazacare.leads.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kalazacare.leads.data.remote.SupabaseClients
import com.kalazacare.leads.data.repository.SupabaseAuthRepository
import com.kalazacare.leads.data.repository.SupabaseContactActivitiesRepository
import com.kalazacare.leads.data.repository.SupabaseLeadsRepository
import com.kalazacare.leads.ui.leads.ActivitiesViewModel
import com.kalazacare.leads.ui.leads.LeadDetailScreen
import com.kalazacare.leads.ui.leads.LeadsScreen
import com.kalazacare.leads.ui.leads.LeadsViewModel
import com.kalazacare.leads.ui.leads.ReportsScreen
import com.kalazacare.leads.ui.leads.SettingsScreen
import com.kalazacare.leads.ui.login.LoginScreen
import com.kalazacare.leads.ui.login.LoginViewModel
import com.kalazacare.leads.ui.theme.KalazaLeadsTheme

private enum class Screen { LOGIN, LEADS, LEAD_DETAIL, REPORTS, SETTINGS }

class MainActivity : ComponentActivity() {
    private var currentScreen by mutableStateOf(Screen.LOGIN)

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Follow-up reminder notifications (A4 part 2) need this on Android 13+.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        val authRepository = SupabaseAuthRepository(SupabaseClients.main)
        val leadsRepository = SupabaseLeadsRepository(SupabaseClients.main)
        val activitiesRepository = SupabaseContactActivitiesRepository(SupabaseClients.main)
        val loginViewModel = LoginViewModel(authRepository)
        val leadsViewModel = LeadsViewModel(leadsRepository)
        val activitiesViewModel = ActivitiesViewModel(activitiesRepository)

        setContent {
            KalazaLeadsTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (currentScreen) {
                        Screen.LOGIN -> LoginScreen(
                            viewModel = loginViewModel,
                            onLoginSuccess = {
                                leadsViewModel.refresh()
                                currentScreen = Screen.LEADS
                            }
                        )
                        Screen.LEADS -> LeadsScreen(
                            viewModel = leadsViewModel,
                            onLeadClick = { lead ->
                                leadsViewModel.selectLead(lead)
                                currentScreen = Screen.LEAD_DETAIL
                            },
                            onViewReports = { currentScreen = Screen.REPORTS },
                            onOpenSettings = { currentScreen = Screen.SETTINGS },
                            onLogout = {
                                loginViewModel.logout()
                                currentScreen = Screen.LOGIN
                            }
                        )
                        Screen.LEAD_DETAIL -> {
                            val leadsState by leadsViewModel.state.collectAsState()
                            val selected = leadsState.selectedLead
                            if (selected != null) {
                                LeadDetailScreen(
                                    lead = selected,
                                    viewModel = leadsViewModel,
                                    activitiesViewModel = activitiesViewModel,
                                    onBack = {
                                        leadsViewModel.clearSelection()
                                        currentScreen = Screen.LEADS
                                    },
                                    onSaved = { currentScreen = Screen.LEADS },
                                )
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
