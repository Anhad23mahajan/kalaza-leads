package com.kalazacare.leads.ui.leads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kalazacare.leads.data.model.NewStaffRequest
import com.kalazacare.leads.data.model.StaffMember
import com.kalazacare.leads.data.model.UpdateStaffRequest
import com.kalazacare.leads.data.repository.StaffRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class StaffState(
    val isLoading: Boolean = false,
    val staff: List<StaffMember> = emptyList(),
    val errorMessage: String? = null,
)

class StaffViewModel(private val repository: StaffRepository) : ViewModel() {

    private val _state = MutableStateFlow(StaffState())
    val state: StateFlow<StaffState> = _state

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            repository.getStaff()
                .onSuccess { staff ->
                    _state.value = _state.value.copy(isLoading = false, staff = staff)
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load staff",
                    )
                }
        }
    }

    fun addStaff(newStaff: NewStaffRequest) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            repository.addStaff(newStaff)
                .onSuccess { refresh() }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to add staff",
                    )
                }
        }
    }

    fun updateStaff(id: String, update: UpdateStaffRequest) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            repository.updateStaff(id, update)
                .onSuccess { refresh() }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to update staff",
                    )
                }
        }
    }
}
