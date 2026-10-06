package com.telecure.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.telecure.app.data.model.Appointment
import com.telecure.app.data.model.AppointmentStatus
import kotlinx.coroutines.tasks.await

class AppointmentRepository {
    private val db = FirebaseFirestore.getInstance()
    private val col = db.collection("appointments")

    suspend fun bookAppointment(appointment: Appointment): Result<String> = try {
        val ref = col.document()
        val confirmation = "TC-" + ref.id.take(8).uppercase()
        val toSave = appointment.copy(id = ref.id, confirmationNumber = confirmation)
        ref.set(toSave).await()
        Result.success(ref.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getAppointments(patientUid: String): List<Appointment> = try {
        col.whereEqualTo("patientUid", patientUid).get().await().toObjects(Appointment::class.java)
    } catch (e: Exception) { emptyList() }

    suspend fun getAppointment(id: String): Appointment? = try {
        col.document(id).get().await().toObject(Appointment::class.java)
    } catch (e: Exception) { null }

    suspend fun updateStatus(id: String, status: AppointmentStatus): Result<Unit> = try {
        col.document(id).update("status", status.name).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun cancel(id: String) = updateStatus(id, AppointmentStatus.CANCELLED)
}
