package com.telecure.app.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telecure.app.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repo = AuthRepository()

    var isLoading by mutableStateOf(false); private set
    var errorMessage by mutableStateOf<String?>(null); private set
    var success by mutableStateOf(false); private set

    fun signIn(email: String, password: String) {
        errorMessage = null
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || password.isBlank()) {
            errorMessage = "Please enter your email and password."
            return
        }
        viewModelScope.launch {
            isLoading = true
            val result = repo.signIn(trimmedEmail, password)
            isLoading = false
            result.onSuccess { success = true }.onFailure { errorMessage = it.message ?: "Sign in failed." }
        }
    }

    fun signInWithGoogle(idToken: String) {
        errorMessage = null
        viewModelScope.launch {
            isLoading = true
            val result = repo.signInWithGoogle(idToken)
            isLoading = false
            result.onSuccess { success = true }.onFailure { errorMessage = it.message ?: "Google sign-in failed." }
        }
    }

    fun signUp(name: String, email: String, phone: String, password: String, confirm: String, avatarUri: Uri?) {
        errorMessage = null
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        val trimmedPhone = phone.trim()
        if (trimmedName.isBlank() || trimmedEmail.isBlank() || password.isBlank()) {
            errorMessage = "Please fill in all required fields."
            return
        }
        if (password != confirm) {
            errorMessage = "Passwords do not match."
            return
        }
        viewModelScope.launch {
            isLoading = true
            val result = repo.signUp(trimmedName, trimmedEmail, trimmedPhone, password)
            result.onSuccess { uid ->
                if (avatarUri != null) {
                    val uploadResult = repo.uploadAvatar(uid, avatarUri)
                    uploadResult.onSuccess { url ->
                        val profile = repo.getProfile(uid)
                        if (profile != null) repo.updateProfile(profile.copy(avatarUrl = url))
                    }
                }
                isLoading = false
                success = true
            }.onFailure {
                isLoading = false
                errorMessage = it.message ?: "Sign up failed."
            }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch { repo.sendPasswordReset(email) }
    }

    fun resetState() { success = false; errorMessage = null }
}
