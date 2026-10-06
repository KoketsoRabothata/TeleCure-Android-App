package com.telecure.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.data.model.WaitlistEntry
import com.telecure.app.data.repository.WaitlistRepository
import kotlinx.coroutines.launch

class WaitlistViewModel : ViewModel() {
    private val repo = WaitlistRepository()

    var entries by mutableStateOf<List<WaitlistEntry>>(emptyList()); private set
    var isLoading by mutableStateOf(true); private set
    var joined by mutableStateOf(false); private set

    fun load() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            isLoading = true
            entries = repo.getEntries(uid)
            isLoading = false
        }
    }

    fun join(doctorUid: String, doctorName: String, preferredDate: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            val result = repo.join(WaitlistEntry(patientUid = uid, doctorUid = doctorUid, doctorName = doctorName, preferredDate = preferredDate))
            result.onSuccess { joined = true }
            load()
        }
    }

    fun leave(id: String) {
        viewModelScope.launch {
            repo.leave(id)
            load()
        }
    }

    fun resetJoined() { joined = false }
}
