package com.example.careconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.data.repository.CareRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileScreenState(
    val currentName: String = "",
    val currentPhone: String = "",
    val currentAddress: String = "",
    val currentRole: String = "",
    val isLoading: Boolean = false,
    val isSaveSuccess: Boolean = false,
    val isDeleteSuccess: Boolean = false,
    val isConfirmDeleteDialogOpen: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: CareRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileScreenState())
    val uiState: StateFlow<ProfileScreenState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getCurrentUserProfile().collect { user ->
                _uiState.update {
                    it.copy(
                        currentName = user?.name ?: "",
                        currentPhone = user?.phone ?: "",
                        currentAddress = user?.address ?: "",
                        currentRole = user?.role ?: ""
                    )
                }
            }
        }
    }

    // --- Action Handlers ---
    fun onNameChange(newName: String) { _uiState.update { it.copy(currentName = newName) } }
    fun onPhoneChange(newPhone: String) { _uiState.update { it.copy(currentPhone = newPhone) } }
    fun onAddressChange(newAddress: String) { _uiState.update { it.copy(currentAddress = newAddress) } }
    fun onRoleChange(newRole: String) { _uiState.update { it.copy(currentRole = newRole) } }
    fun showDeleteDialog() { _uiState.update { it.copy(isConfirmDeleteDialogOpen = true) } }
    fun hideDeleteDialog() { _uiState.update { it.copy(isConfirmDeleteDialogOpen = false) } }

    fun saveProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val currentState = _uiState.value
            repository.updateUserProfile(
                name = currentState.currentName,
                phone = currentState.currentPhone,
                address = currentState.currentAddress,
                role = currentState.currentRole
            )
            _uiState.update { it.copy(isLoading = false, isSaveSuccess = true) }
        }
    }

    fun deleteProfile() {
        viewModelScope.launch {
            hideDeleteDialog()
            _uiState.update { it.copy(isLoading = true) }
            repository.deleteUserAccount()
            _uiState.update { it.copy(isLoading = false, isDeleteSuccess = true) }
        }
    }
}

