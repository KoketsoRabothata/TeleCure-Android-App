package com.telecure.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.data.model.AppNotification
import com.telecure.app.data.repository.NotificationRepository
import kotlinx.coroutines.launch

class NotificationsViewModel : ViewModel() {
    private val repo = NotificationRepository()

    var notifications by mutableStateOf<List<AppNotification>>(emptyList()); private set
    var isLoading by mutableStateOf(true); private set

    fun load() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            isLoading = true
            notifications = repo.getNotifications(uid).sortedByDescending { it.createdAt }
            isLoading = false
        }
    }

    fun markRead(id: String) {
        viewModelScope.launch { repo.markAsRead(id); load() }
    }

    fun markAllRead() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch { repo.markAllRead(uid); load() }
    }

    fun clearAll() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch { repo.clearAll(uid); load() }
    }
}
