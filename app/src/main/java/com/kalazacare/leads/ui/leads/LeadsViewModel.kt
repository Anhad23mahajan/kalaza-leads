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
    /** Loading the list. Kept apart from [isSaving] so a background refresh never locks the edit screen. */
    val isLoading: Boolean = false,
    /** A save or delete is in flight. */
    val isSaving: Boolean = false,
    val leads: List<Lead> = emptyList(),
    val selectedLead: Lead? = null,
    /** Why the last list load failed (shown on the Leads screen). */
    val errorMessage: String? = null,
    /** One-shot message for a snackbar ("Changes saved", a save error, ...); cleared via [LeadsViewModel.messageShown]. */
    val userMessage: UserMessage? = null,
)

/** The id makes two identical messages in a row ("Changes saved" twice) still show twice. */
data class UserMessage(val text: String, val id: Long = System.nanoTime())

class LeadsViewModel(private val repository: LeadsRepository) : ViewModel() {

    private val _state = MutableStateFlow(LeadsState())
    val state: StateFlow<LeadsState> = _state

    // No refresh() in init: nobody is signed in when this is constructed, so it could only fail --
    // and a late failure could land after a successful load and leave a stale error message
    // showing. MainActivity calls refresh() once a session exists (login or restored session).

    /** [onDone] runs on the main thread after the load finishes, whether it succeeded or failed. */
    fun refresh(onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.getLeads()
                .onSuccess { leads ->
                    _state.value = _state.value.copy(isLoading = false, leads = leads, errorMessage = null)
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = userFacingMessage(error, "Couldn't load the leads. Please try again."),
                    )
                }
            onDone?.invoke()
        }
    }

    fun selectLead(lead: Lead) {
        _state.value = _state.value.copy(selectedLead = lead)
    }

    fun clearSelection() {
        _state.value = _state.value.copy(selectedLead = null)
    }

    fun showMessage(text: String) {
        _state.value = _state.value.copy(userMessage = UserMessage(text))
    }

    fun messageShown(id: Long) {
        if (_state.value.userMessage?.id == id) _state.value = _state.value.copy(userMessage = null)
    }

    fun updateLead(id: String, update: UpdateLeadRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            repository.updateLead(id, update)
                .onSuccess { saved ->
                    _state.value = _state.value.copy(
                        isSaving = false,
                        selectedLead = null,
                        // Show the saved version straight away; the refresh below confirms it.
                        leads = _state.value.leads.map { if (it.id == id) saved else it },
                        userMessage = UserMessage("Changes saved"),
                    )
                    refresh()
                    onSuccess()
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isSaving = false,
                        userMessage = UserMessage(userFacingMessage(error, "Couldn't save the changes. Please try again.")),
                    )
                }
        }
    }

    fun deleteLead(id: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            repository.deleteLead(id)
                .onSuccess {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        selectedLead = null,
                        leads = _state.value.leads.filterNot { it.id == id },
                        userMessage = UserMessage("Lead deleted"),
                    )
                    onSuccess()
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isSaving = false,
                        userMessage = UserMessage(userFacingMessage(error, error.message ?: "Couldn't delete the lead. Please try again.")),
                    )
                }
        }
    }
}
