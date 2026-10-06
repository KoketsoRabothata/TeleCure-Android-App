package com.telecure.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.telecure.app.data.model.LoyaltyEntry
import kotlinx.coroutines.tasks.await

class LoyaltyRepository {
    private val db = FirebaseFirestore.getInstance()
    private val col = db.collection("loyalty_entries")

    suspend fun getEntries(patientUid: String): List<LoyaltyEntry> = try {
        col.whereEqualTo("patientUid", patientUid)
            .orderBy("earnedAt", Query.Direction.DESCENDING)
            .get().await().toObjects(LoyaltyEntry::class.java)
    } catch (e: Exception) { emptyList() }

    suspend fun awardPoints(patientUid: String, points: Int, reason: String): Result<Unit> = try {
        val ref = col.document()
        ref.set(LoyaltyEntry(id = ref.id, patientUid = patientUid, points = points, reason = reason)).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
