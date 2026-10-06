package com.telecure.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.data.model.UserProfile
import com.telecure.app.data.repository.AuthRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val repo = AuthRepository()

    var profile by mutableStateOf<UserProfile?>(null); private set
    var isLoading by mutableStateOf(true); private set

    fun load() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            isLoading = true
            profile = repo.getProfile(uid)
            isLoading = false
        }
    }

    fun updateBiometric(enabled: Boolean) {
        val p = profile ?: return
        val updated = p.copy(biometricEnabled = enabled)
        profile = updated
        viewModelScope.launch { repo.updateProfile(updated) }
    }

    fun updateLanguage(language: String) {
        val p = profile ?: return
        val updated = p.copy(language = language)
        profile = updated
        viewModelScope.launch { repo.updateProfile(updated) }
    }

    fun updateEmergencyContact(phone: String) {
        val p = profile ?: return
        val updated = p.copy(emergencyContactPhone = phone)
        profile = updated
        viewModelScope.launch { repo.updateProfile(updated) }
    }

    fun signOut() = repo.signOut()
}
