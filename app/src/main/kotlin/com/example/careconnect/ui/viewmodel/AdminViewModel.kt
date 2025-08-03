package com.example.careconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.data.model.User
import com.example.careconnect.data.repository.CareRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(private val repository: CareRepository) : ViewModel() {
    private val _userProfile = MutableStateFlow<User?>(null)
    // ✅ THIS IS THE FIXED LINE
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getCurrentUserProfile().collect { user ->
                _userProfile.value = user
            }
        }
    }

    fun signOut() {
        repository.signOut()
    }
}

