package com.kalazacare.leads.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kalazacare.leads.data.repository.AuthRepository
import com.kalazacare.leads.ui.userFacingMessage
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "KalazaLeadsAuth"

data class LoginState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null,
)

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    fun login(adminName: String, password: String) {
        Log.d(TAG, "login() called with adminName=$adminName")
        if (adminName.isBlank() || password.isBlank()) {
            Log.d(TAG, "login() blocked: blank field")
            _state.value = _state.value.copy(errorMessage = "Name and password required")
            return
        }

        viewModelScope.launch {
            Log.d(TAG, "login() coroutine started, calling authRepository.login")
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            val result = authRepository.login(adminName, password)
            Log.d(TAG, "authRepository.login returned: success=${result.isSuccess}, error=${result.exceptionOrNull()}")
            result
                .onSuccess { userId ->
                    Log.d(TAG, "login success, userId=$userId")
                    _state.value = LoginState(
                        isLoading = false,
                        isLoggedIn = true,
                        errorMessage = null,
                    )
                }
                .onFailure { error ->
                    Log.e(TAG, "login failed", error)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        // The repository already words a refused login; network/rate-limit errors are mapped here.
                        errorMessage = if (error is RestException || error is java.io.IOException) {
                            userFacingMessage(error, "Login failed. Please try again.")
                        } else {
                            error.message ?: "Login failed. Please try again."
                        },
                    )
                }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun logout() {
        Log.d(TAG, "logout() called")
        // Reset synchronously, before the caller flips MainActivity's own isLoggedIn
        // flag. If this reset happened inside the coroutine below instead, MainActivity
        // would recompose LoginScreen with this ViewModel's isLoggedIn still stuck at
        // true (the coroutine hasn't run yet), and its LaunchedEffect would bounce
        // straight back to LeadsScreen before the reset ever lands.
        _state.value = LoginState()
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
