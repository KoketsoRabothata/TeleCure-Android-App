package com.telecure.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.telecure.app.data.model.HealthMetricEntry
import kotlinx.coroutines.tasks.await

class HealthMetricRepository {
    private val db = FirebaseFirestore.getInstance()
    private val col = db.collection("health_metrics")

    suspend fun getEntries(patientUid: String, type: String? = null): List<HealthMetricEntry> = try {
        val all = col.whereEqualTo("patientUid", patientUid)
            .orderBy("recordedAt", Query.Direction.DESCENDING)
            .get().await().toObjects(HealthMetricEntry::class.java)
        if (type == null) all else all.filter { it.type == type }
    } catch (e: Exception) { emptyList() }

    suspend fun addEntry(entry: HealthMetricEntry): Result<Unit> = try {
        val ref = col.document()
        ref.set(entry.copy(id = ref.id)).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteEntry(id: String): Result<Unit> = try {
        col.document(id).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
