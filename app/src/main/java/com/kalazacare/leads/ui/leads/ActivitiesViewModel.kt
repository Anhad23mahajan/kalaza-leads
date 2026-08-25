package com.kalazacare.leads.ui.leads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kalazacare.leads.data.model.ContactActivity
import com.kalazacare.leads.data.model.NewContactActivityRequest
import com.kalazacare.leads.data.repository.ContactActivitiesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ActivitiesState(
    val isLoading: Boolean = false,
    val activities: List<ContactActivity> = emptyList(),
    val errorMessage: String? = null,
)

class ActivitiesViewModel(private val repository: ContactActivitiesRepository) : ViewModel() {

    private val _state = MutableStateFlow(ActivitiesState())
    val state: StateFlow<ActivitiesState> = _state

    fun load(leadId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            repository.getForLead(leadId)
                .onSuccess { activities ->
                    _state.value = _state.value.copy(isLoading = false, activities = activities)
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load contact log",
                    )
                }
        }
    }

    fun addActivity(newActivity: NewContactActivityRequest) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            repository.addActivity(newActivity)
                .onSuccess {
                    load(newActivity.leadId)
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to log contact",
                    )
                }
        }
    }
}
