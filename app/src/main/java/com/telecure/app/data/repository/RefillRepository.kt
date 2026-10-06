package com.telecure.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.telecure.app.data.model.RefillRequest

class RefillRepository {
    private val db = FirebaseFirestore.getInstance()
    private val col = db.collection("refill_requests")

    suspend fun getRequests(patientUid: String): List<RefillRequest> = try {
        col.whereEqualTo("patientUid", patientUid).get().await().toObjects(RefillRequest::class.java)
    } catch (e: Exception) { emptyList() }

    suspend fun requestRefill(request: RefillRequest): Result<Unit> = try {
        val ref = col.document()
        ref.set(request.copy(id = ref.id)).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
