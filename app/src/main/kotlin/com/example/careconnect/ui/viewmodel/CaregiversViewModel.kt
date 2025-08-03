// Defines the file's location within the ViewModel architecture of the app.
package com.example.careconnect.ui.viewmodel

// Imports necessary libraries for ViewModel creation, coroutines, and the data layer.
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.data.model.User
import com.example.careconnect.data.repository.CareRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// Defines the data structure for all the state information on the Caregivers/Contact List screen.
data class CaregiversScreenState(
    val caregivers: List<User> = emptyList(),
    val isLoading: Boolean = true,
    val userToEdit: User? = null,
    val isRoleDialogVisible: Boolean = false
)

// Marks this class as a ViewModel that can be injected by Hilt.
@HiltViewModel
// Defines the ViewModel for the Caregivers screen, injecting the data repository.
class CaregiversViewModel @Inject constructor(private val repository: CareRepository) : ViewModel() {

    // A private, mutable state flow that holds the current state of the screen.
    private val _uiState = MutableStateFlow(CaregiversScreenState())
    // An immutable, public-facing state flow that the UI can safely observe.
    val uiState: StateFlow<CaregiversScreenState> = _uiState.asStateFlow()

    // This block runs when the ViewModel is first created to fetch the initial data.
    init {
        // Launches a coroutine to load the list of caregivers from the repository.
        viewModelScope.launch {
            // A reactive chain that first gets the current user's profile to find their care circle ID.
            repository.getCurrentUserProfile().filterNotNull().flatMapLatest { currentUser ->
                // Then, it uses that ID to fetch the list of all users in that same care circle.
                repository.getUsersInCareCircle(currentUser.careCircleId)
                // Collects the resulting list of caregivers and updates the UI state.
            }.collect { caregiversList ->
                _uiState.update { it.copy(caregivers = caregiversList, isLoading = false) }
            }
        }
    }

    // A public function for the UI to call when the user wants to edit a caregiver's role.
    fun onEditRoleClick(user: User) {
        // Updates the state to show the role change dialog and remembers which user is being edited.
        _uiState.update { it.copy(userToEdit = user, isRoleDialogVisible = true) }
    }

    // A public function for the UI to call to dismiss the role change dialog.
    fun onDialogDismiss() {
        // Updates the state to hide the dialog and clear the user being edited.
        _uiState.update { it.copy(isRoleDialogVisible = false, userToEdit = null) }
    }

    // A public function to handle the logic of updating a user's role in the database.
    fun onRoleChange(userId: String, newRole: String) {
        // Launches a coroutine to perform the database update off the main thread.
        viewModelScope.launch {
            // Calls the repository to update the user's role in Firestore.
            repository.updateUserRole(userId, newRole)
            // Gets the current care circle ID to manually refresh the list of caregivers.
            val currentCareCircleId = _uiState.value.caregivers.firstOrNull()?.careCircleId
            // If a care circle ID exists, it re-fetches the updated list.
            if (currentCareCircleId != null) {
                repository.getUsersInCareCircle(currentCareCircleId).firstOrNull()?.let { newList ->
                    _uiState.update { it.copy(caregivers = newList) }
                }
            }
            // Hides the dialog after the update is complete.
            onDialogDismiss()
        }
    }
}

