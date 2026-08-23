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
import com.kalazacare.leads.KalazaLeadsApp
import com.kalazacare.leads.data.repository.SupabaseAuthRepository
import com.kalazacare.leads.data.remote.SupabaseClients
import com.kalazacare.leads.ui.leads.LeadsScreen
import com.kalazacare.leads.ui.login.LoginScreen
import com.kalazacare.leads.ui.login.LoginViewModel
import com.kalazacare.leads.ui.theme.KalazaLeadsTheme

class MainActivity : ComponentActivity() {
    private var isLoggedIn by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authRepository = SupabaseAuthRepository(SupabaseClients.main)
        val loginViewModel = LoginViewModel(authRepository)

        setContent {
            KalazaLeadsTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (isLoggedIn) {
                        LeadsScreen(
                            onLogout = {
                                loginViewModel.logout()
                                isLoggedIn = false
                            }
                        )
                    } else {
                        LoginScreen(
                            viewModel = loginViewModel,
                            onLoginSuccess = {
                                isLoggedIn = true
                            }
                        )
                    }
                }
            }
        }
    }
}
