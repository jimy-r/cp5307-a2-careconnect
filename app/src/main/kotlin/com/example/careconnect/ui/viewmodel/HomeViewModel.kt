// Defines the file's location within the ViewModel architecture of the app.
package com.example.careconnect.ui.viewmodel

// Imports necessary libraries for ViewModel creation, coroutines, and the data layer.
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.data.model.Appointment
import com.example.careconnect.data.model.JournalEntry
import com.example.careconnect.data.model.Medication
import com.example.careconnect.data.model.User
import com.example.careconnect.data.repository.CareRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Defines the data structure for all the state information on the Home/Dashboard screen.
data class HomeScreenState(
    val isLoading: Boolean = true,
    val userProfile: User? = null,
    val upcomingAppointment: Appointment? = null,
    val nextMedication: Medication? = null,
    val latestJournalEntry: JournalEntry? = null,
    val error: String? = null
)

// Marks this class as a ViewModel that can be injected by Hilt.
@HiltViewModel
// Defines the ViewModel for the Home screen, injecting the data repository.
class HomeViewModel @Inject constructor(private val repository: CareRepository) : ViewModel() {
    // A private, mutable state flow that holds the current state of the screen.
    private val _uiState = MutableStateFlow(HomeScreenState())
    // An immutable, public-facing state flow that the UI can safely observe.
    val uiState: StateFlow<HomeScreenState> = _uiState.asStateFlow()

    // This block runs when the ViewModel is first created to fetch the initial data.
    init {
        loadDashboardData()
    }

    // A private function to orchestrate the fetching of all data needed for the dashboard.
    private fun loadDashboardData() {
        // Immediately updates the UI state to show a loading indicator.
        _uiState.update { it.copy(isLoading = true) }

        // Launches a coroutine to perform network and database operations off the main thread.
        viewModelScope.launch {
            // Concurrently starts a background job to fetch the user's profile.
            val userProfileJob = async { repository.getCurrentUserProfile().firstOrNull() }
            // Concurrently starts another background job to fetch the rest of the dashboard data.
            val dashboardDataJob = async { repository.getDashboardData() }

            // Pauses execution here until the user profile data has been returned.
            val userProfile = userProfileJob.await()
            // Pauses execution here until the dashboard data has been returned.
            val dashboardData = dashboardDataJob.await()

            // Updates the UI state a single time with all the fetched data, removing the loading indicator.
            _uiState.update {
                it.copy(
                    isLoading = false,
                    userProfile = userProfile,
                    upcomingAppointment = dashboardData.upcomingAppointment,
                    nextMedication = dashboardData.nextMedication,
                    latestJournalEntry = dashboardData.latestJournalEntry
                )
            }
        }
    }
}

