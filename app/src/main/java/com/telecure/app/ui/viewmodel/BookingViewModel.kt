package com.telecure.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.data.model.Appointment
import com.telecure.app.data.model.Doctor
import com.telecure.app.data.repository.AppointmentRepository
import com.telecure.app.data.repository.DoctorRepository
import kotlinx.coroutines.launch

class BookingViewModel : ViewModel() {
    private val doctorRepo = DoctorRepository()
    private val appointmentRepo = AppointmentRepository()

    var doctor by mutableStateOf<Doctor?>(null); private set
    var isBooking by mutableStateOf(false); private set
    var confirmationNumber by mutableStateOf<String?>(null); private set
    var errorMessage by mutableStateOf<String?>(null); private set

    fun loadDoctor(uid: String) {
        viewModelScope.launch { doctor = doctorRepo.getDoctor(uid) }
    }

    fun confirmBooking(doctorId: String, doctorName: String, doctorPhoto: String, specialty: String, fee: Double, date: String, time: String, type: String, reason: String, bookedForName: String = "") {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            isBooking = true
            val appt = Appointment(
                patientUid = uid, doctorUid = doctorId, doctorName = doctorName, doctorPhotoUrl = doctorPhoto,
                specialty = specialty, date = date, time = time, type = type, reasonForVisit = reason, fee = fee,
                bookedForName = bookedForName
            )
            val result = appointmentRepo.bookAppointment(appt)
            isBooking = false
            result.onSuccess { id ->
                confirmationNumber = "TC-" + id.take(8).uppercase()
            }.onFailure { errorMessage = it.message ?: "Booking failed." }
        }
    }
}
