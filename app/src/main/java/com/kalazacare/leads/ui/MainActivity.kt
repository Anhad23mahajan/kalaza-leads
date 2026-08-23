package com.kalazacare.leads.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kalazacare.leads.data.remote.SupabaseClients
import com.kalazacare.leads.data.repository.SupabaseAuthRepository
import com.kalazacare.leads.data.repository.SupabaseLeadsRepository
import com.kalazacare.leads.ui.leads.AddLeadScreen
import com.kalazacare.leads.ui.leads.LeadsScreen
import com.kalazacare.leads.ui.leads.LeadsViewModel
import com.kalazacare.leads.ui.login.LoginScreen
import com.kalazacare.leads.ui.login.LoginViewModel
import com.kalazacare.leads.ui.theme.KalazaLeadsTheme

private enum class Screen { LOGIN, LEADS, ADD_LEAD }

class MainActivity : ComponentActivity() {
    private var currentScreen by mutableStateOf(Screen.LOGIN)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authRepository = SupabaseAuthRepository(SupabaseClients.main)
        val leadsRepository = SupabaseLeadsRepository(SupabaseClients.main)
        val loginViewModel = LoginViewModel(authRepository)
        val leadsViewModel = LeadsViewModel(leadsRepository)

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
                            onAddLead = { currentScreen = Screen.ADD_LEAD },
                            onLogout = {
                                loginViewModel.logout()
                                currentScreen = Screen.LOGIN
                            }
                        )
                        Screen.ADD_LEAD -> AddLeadScreen(
                            viewModel = leadsViewModel,
                            onBack = { currentScreen = Screen.LEADS },
                            onSaved = { currentScreen = Screen.LEADS },
                        )
                    }
                }
            }
        }
    }
}
