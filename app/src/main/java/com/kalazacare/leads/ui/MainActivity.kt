package com.kalazacare.leads.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.kalazacare.leads.ui.login.LoginScreen
import com.kalazacare.leads.ui.theme.KalazaLeadsTheme

/**
 * Hosts LoginScreen directly for now — no NavHost yet. Once there's more than one
 * screen, this should follow Kalaza Care's pattern (a KalazaLeadsNavHost + Routes
 * object) rather than growing branchy logic in here.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KalazaLeadsTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LoginScreen()
                }
            }
        }
    }
}
