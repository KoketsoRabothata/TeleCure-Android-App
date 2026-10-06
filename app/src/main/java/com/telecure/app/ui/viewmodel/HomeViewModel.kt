package com.telecure.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.data.model.Appointment
import com.telecure.app.data.model.HealthTip
import com.telecure.app.data.model.UserProfile
import com.telecure.app.data.repository.AppointmentRepository
import com.telecure.app.data.repository.AuthRepository
import com.telecure.app.data.repository.HealthTipRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val authRepo = AuthRepository()
    private val appointmentRepo = AppointmentRepository()
    private val tipRepo = HealthTipRepository()

    var profile by mutableStateOf<UserProfile?>(null); private set
    var upcomingAppointment by mutableStateOf<Appointment?>(null); private set
    var healthTips by mutableStateOf<List<HealthTip>>(emptyList()); private set
    var isLoading by mutableStateOf(true); private set

    fun load() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            isLoading = true
            profile = authRepo.getProfile(uid)
            upcomingAppointment = appointmentRepo.getAppointments(uid)
                .filter { it.status == "UPCOMING" }
                .minByOrNull { it.date + it.time }
            healthTips = tipRepo.getTips()
            isLoading = false
        }
    }
}
