package com.kalazacare.leads.ui.leads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kalazacare.leads.data.model.Lead
import com.kalazacare.leads.data.model.NewLeadRequest
import com.kalazacare.leads.data.repository.LeadsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LeadsState(
    val isLoading: Boolean = false,
    val leads: List<Lead> = emptyList(),
    val errorMessage: String? = null,
)

class LeadsViewModel(private val repository: LeadsRepository) : ViewModel() {

    private val _state = MutableStateFlow(LeadsState())
    val state: StateFlow<LeadsState> = _state

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            repository.getLeads()
                .onSuccess { leads ->
                    _state.value = _state.value.copy(isLoading = false, leads = leads)
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load leads",
                    )
                }
        }
    }

    fun addLead(newLead: NewLeadRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            repository.addLead(newLead)
                .onSuccess {
                    refresh()
                    onSuccess()
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to add lead",
                    )
                }
        }
    }
}
