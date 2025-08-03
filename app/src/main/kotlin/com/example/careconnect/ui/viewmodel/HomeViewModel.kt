package com.example.careconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.data.model.Appointment
import com.example.careconnect.data.model.JournalEntry // <-- ADD THIS IMPORT
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

data class HomeScreenState(
    val isLoading: Boolean = true,
    val userProfile: User? = null,
    val upcomingAppointment: Appointment? = null,
    val nextMedication: Medication? = null,
    val latestJournalEntry: JournalEntry? = null, // <-- ADD THIS PROPERTY
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: CareRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeScreenState())
    val uiState: StateFlow<HomeScreenState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            // Start fetching user profile and dashboard data concurrently
            val userProfileJob = async { repository.getCurrentUserProfile().firstOrNull() }
            val dashboardDataJob = async { repository.getDashboardData() }

            // Await for both to complete
            val userProfile = userProfileJob.await()
            val dashboardData = dashboardDataJob.await()

            // Update the UI state once with all the fetched data
            _uiState.update {
                it.copy(
                    isLoading = false,
                    userProfile = userProfile,
                    upcomingAppointment = dashboardData.upcomingAppointment,
                    nextMedication = dashboardData.nextMedication,
                    latestJournalEntry = dashboardData.latestJournalEntry // <-- SET THE NEW DATA
                )
            }
        }
    }
}

