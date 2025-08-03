package com.example.careconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.data.model.Appointment
import com.example.careconnect.data.model.Medication
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class HomeScreenState(
    val isLoading: Boolean = true,
    val upcomingAppointment: Appointment? = null,
    val nextMedication: Medication? = null,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(HomeScreenState())
    val uiState: StateFlow<HomeScreenState> = _uiState

    val currentUser = Firebase.auth.currentUser

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = HomeScreenState(isLoading = true)
            // TODO: Replace this with a call to repository.getDashboardData()
            kotlinx.coroutines.delay(1000) // Simulate network delay
            _uiState.value = HomeScreenState(
                isLoading = false,
                upcomingAppointment = Appointment("1", "Cardiologist Check-up", "Dr. Smith", "Heart Institute, Room 203", Date()),
                nextMedication = Medication("m1", "Lisinopril", "10mg", Date())
            )
        }
    }
}

