package com.telecure.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.telecure.app.data.model.MedicationReminder
import kotlinx.coroutines.tasks.await

class ReminderRepository {
    private val db = FirebaseFirestore.getInstance()
    private val col = db.collection("medication_reminders")

    suspend fun getReminders(patientUid: String): List<MedicationReminder> = try {
        col.whereEqualTo("patientUid", patientUid).get().await().toObjects(MedicationReminder::class.java)
    } catch (e: Exception) { emptyList() }

    suspend fun addReminder(reminder: MedicationReminder): Result<MedicationReminder> = try {
        val ref = col.document()
        val toSave = reminder.copy(id = ref.id)
        ref.set(toSave).await()
        Result.success(toSave)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun setActive(id: String, active: Boolean): Result<Unit> = try {
        col.document(id).update("active", active).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteReminder(id: String): Result<Unit> = try {
        col.document(id).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
