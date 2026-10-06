package com.telecure.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.telecure.app.data.model.WaitlistEntry
import kotlinx.coroutines.tasks.await

class WaitlistRepository {
    private val db = FirebaseFirestore.getInstance()
    private val col = db.collection("waitlist")

    suspend fun getEntries(patientUid: String): List<WaitlistEntry> = try {
        col.whereEqualTo("patientUid", patientUid).get().await().toObjects(WaitlistEntry::class.java)
    } catch (e: Exception) { emptyList() }

    suspend fun join(entry: WaitlistEntry): Result<Unit> = try {
        val ref = col.document()
        ref.set(entry.copy(id = ref.id)).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun leave(id: String): Result<Unit> = try {
        col.document(id).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
