package com.telecure.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.data.model.RefillRequest
import com.telecure.app.data.repository.RefillRepository
import kotlinx.coroutines.launch

class RefillViewModel : ViewModel() {
    private val repo = RefillRepository()

    var requests by mutableStateOf<List<RefillRequest>>(emptyList()); private set
    var isLoading by mutableStateOf(true); private set

    fun load() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            isLoading = true
            requests = repo.getRequests(uid)
            isLoading = false
        }
    }

    fun requestRefill(doctorUid: String, doctorName: String, medicineName: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        if (medicineName.isBlank()) return
        viewModelScope.launch {
            repo.requestRefill(RefillRequest(patientUid = uid, doctorUid = doctorUid, doctorName = doctorName, medicineName = medicineName))
            load()
        }
    }
}
