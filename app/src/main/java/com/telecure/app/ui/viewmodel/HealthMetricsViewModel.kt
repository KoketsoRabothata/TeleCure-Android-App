package com.telecure.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.data.model.HealthMetricEntry
import com.telecure.app.data.repository.HealthMetricRepository
import kotlinx.coroutines.launch

class HealthMetricsViewModel : ViewModel() {
    private val repo = HealthMetricRepository()

    var entries by mutableStateOf<List<HealthMetricEntry>>(emptyList()); private set
    var isLoading by mutableStateOf(true); private set
    var selectedType by mutableStateOf("WEIGHT"); private set

    fun load(type: String = selectedType) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        selectedType = type
        viewModelScope.launch {
            isLoading = true
            entries = repo.getEntries(uid, type)
            isLoading = false
        }
    }

    fun addEntry(value: String, note: String = "") {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        if (value.isBlank()) return
        viewModelScope.launch {
            repo.addEntry(HealthMetricEntry(patientUid = uid, type = selectedType, value = value, note = note))
            load()
        }
    }

    fun deleteEntry(id: String) {
        viewModelScope.launch {
            repo.deleteEntry(id)
            load()
        }
    }
}
