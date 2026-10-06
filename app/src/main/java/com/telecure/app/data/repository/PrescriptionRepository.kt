package com.telecure.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.telecure.app.data.model.Prescription
import kotlinx.coroutines.tasks.await

class PrescriptionRepository {
    private val db = FirebaseFirestore.getInstance()
    private val col = db.collection("prescriptions")

    suspend fun getPrescriptions(patientUid: String, type: String? = null): List<Prescription> = try {
        val all = col.whereEqualTo("patientUid", patientUid).get().await().toObjects(Prescription::class.java)
        if (type == null) all else all.filter { it.type == type }
    } catch (e: Exception) { emptyList() }
}
