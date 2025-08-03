package com.example.careconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.data.model.User
import com.example.careconnect.data.repository.CareRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CaregiversViewModel @Inject constructor(private val repository: CareRepository) : ViewModel() {

    // A placeholder for the current user's care circle ID
    private val careCircleId = "test_circle_1" // TODO: Get this from the logged-in user

    val caregivers: StateFlow<List<User>> = repository.getUsersInCareCircle(careCircleId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Trigger a fetch from the remote database when the ViewModel is created
        viewModelScope.launch {
            repository.fetchUsersFromRemote(careCircleId)
        }
    }
}

