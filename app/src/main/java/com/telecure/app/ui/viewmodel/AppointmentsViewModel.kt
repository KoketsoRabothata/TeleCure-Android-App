package com.telecure.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.data.model.Appointment
import com.telecure.app.data.repository.AppointmentRepository
import kotlinx.coroutines.launch

class AppointmentsViewModel : ViewModel() {
    private val repo = AppointmentRepository()

    var appointments by mutableStateOf<List<Appointment>>(emptyList()); private set
    var isLoading by mutableStateOf(true); private set

    fun load() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            isLoading = true
            appointments = repo.getAppointments(uid)
            isLoading = false
        }
    }

    fun cancel(id: String) {
        viewModelScope.launch {
            repo.cancel(id)
            load()
        }
    }
}
