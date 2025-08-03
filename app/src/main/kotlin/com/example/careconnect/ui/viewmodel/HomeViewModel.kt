package com.example.careconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.data.model.Appointment
import com.example.careconnect.data.model.Medication
import com.example.careconnect.data.model.User
import com.example.careconnect.data.repository.CareRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay // <-- ADD THIS IMPORT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class HomeScreenState(
    val isLoading: Boolean = true,
    val userProfile: User? = null,
    val upcomingAppointment: Appointment? = null,
    val nextMedication: Medication? = null,
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
            // ✅ THIS IS THE FIX: Add a small delay to allow Firebase Auth to initialize.
            delay(200) // 200 milliseconds is usually enough.

            val userProfileJob = async { repository.getCurrentUserProfile().firstOrNull() }
            val userProfile = userProfileJob.await()

            // Dummy data for now
            val dummyAppointment = Appointment("1", "Cardiologist Check-up", "Dr. Smith", "Heart Institute, Room 203", Date())
            val dummyMedication = Medication("m1", "Lisinopril", "10mg", Date())

            _uiState.update {
                it.copy(
                    isLoading = false,
                    userProfile = userProfile,
                    upcomingAppointment = dummyAppointment,
                    nextMedication = dummyMedication
                )
            }
        }
    }
}

