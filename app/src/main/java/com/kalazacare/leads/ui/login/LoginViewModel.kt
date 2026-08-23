package com.kalazacare.leads.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kalazacare.leads.data.repository.AuthRepository
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

    fun login(staffName: String, password: String) {
        Log.d(TAG, "login() called with staffName=$staffName")
        if (staffName.isBlank() || password.isBlank()) {
            Log.d(TAG, "login() blocked: blank field")
            _state.value = _state.value.copy(errorMessage = "Name and password required")
            return
        }

        viewModelScope.launch {
            Log.d(TAG, "login() coroutine started, calling authRepository.login")
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            val result = authRepository.login(staffName, password)
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
                        errorMessage = error.message ?: "Login failed",
                    )
                }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}
