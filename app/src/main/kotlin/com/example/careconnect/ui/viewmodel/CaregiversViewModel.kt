package com.example.careconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.data.model.User
import com.example.careconnect.data.repository.CareRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CaregiversScreenState(
    val caregivers: List<User> = emptyList(),
    val isLoading: Boolean = true,
    val userToEdit: User? = null,
    val isRoleDialogVisible: Boolean = false
)

@HiltViewModel
class CaregiversViewModel @Inject constructor(private val repository: CareRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CaregiversScreenState())
    val uiState: StateFlow<CaregiversScreenState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // First, get the current user to find their care circle ID
            repository.getCurrentUserProfile().filterNotNull().flatMapLatest { currentUser ->
                // Now use that ID to get all members of the circle
                repository.getUsersInCareCircle(currentUser.careCircleId)
            }.collect { caregiversList ->
                _uiState.update { it.copy(caregivers = caregiversList, isLoading = false) }
            }
        }
    }

    fun onEditRoleClick(user: User) {
        _uiState.update { it.copy(userToEdit = user, isRoleDialogVisible = true) }
    }

    fun onDialogDismiss() {
        _uiState.update { it.copy(isRoleDialogVisible = false, userToEdit = null) }
    }

    fun onRoleChange(userId: String, newRole: String) {
        viewModelScope.launch {
            repository.updateUserRole(userId, newRole)
            // Refresh the list after updating
            val currentCareCircleId = _uiState.value.caregivers.firstOrNull()?.careCircleId
            if (currentCareCircleId != null) {
                repository.getUsersInCareCircle(currentCareCircleId).firstOrNull()?.let { newList ->
                    _uiState.update { it.copy(caregivers = newList) }
                }
            }
            onDialogDismiss()
        }
    }
}

