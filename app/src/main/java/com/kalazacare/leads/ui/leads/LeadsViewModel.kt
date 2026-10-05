package com.kalazacare.leads.ui.leads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kalazacare.leads.data.model.Lead
import com.kalazacare.leads.data.model.UpdateLeadRequest
import com.kalazacare.leads.data.repository.LeadsRepository
import com.kalazacare.leads.ui.userFacingMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LeadsState(
    val isLoading: Boolean = false,
    val leads: List<Lead> = emptyList(),
    val selectedLead: Lead? = null,
    val errorMessage: String? = null,
)

class LeadsViewModel(private val repository: LeadsRepository) : ViewModel() {

    private val _state = MutableStateFlow(LeadsState())
    val state: StateFlow<LeadsState> = _state

    // No refresh() in init: nobody is signed in when this is constructed, so it could only fail --
    // and a late failure could land after a successful load and leave a stale error message
    // showing. MainActivity calls refresh() once a session exists (login or restored session).

    /** [onDone] runs on the main thread after the load finishes, whether it succeeded or failed. */
    fun refresh(onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            repository.getLeads()
                .onSuccess { leads ->
                    _state.value = _state.value.copy(isLoading = false, leads = leads, errorMessage = null)
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = userFacingMessage(error, "Couldn't load the leads. Pull down to try again."),
                    )
                }
            onDone?.invoke()
        }
    }

    fun selectLead(lead: Lead) {
        _state.value = _state.value.copy(selectedLead = lead, errorMessage = null)
    }

    fun clearSelection() {
        _state.value = _state.value.copy(selectedLead = null)
    }

    fun updateLead(id: String, update: UpdateLeadRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            repository.updateLead(id, update)
                .onSuccess {
                    _state.value = _state.value.copy(isLoading = false, selectedLead = null)
                    refresh()
                    onSuccess()
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = userFacingMessage(error, "Couldn't save the changes. Please try again."),
                    )
                }
        }
    }
}
