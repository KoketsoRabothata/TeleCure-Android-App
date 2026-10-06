package com.telecure.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.telecure.app.data.model.Dependent
import kotlinx.coroutines.tasks.await

class DependentRepository {
    private val db = FirebaseFirestore.getInstance()
    private fun col(uid: String) = db.collection("users").document(uid).collection("dependents")

    suspend fun getDependents(uid: String): List<Dependent> = try {
        col(uid).get().await().toObjects(Dependent::class.java)
    } catch (e: Exception) { emptyList() }

    suspend fun addDependent(uid: String, dependent: Dependent): Result<Unit> = try {
        val ref = col(uid).document()
        col(uid).document(ref.id).set(dependent.copy(id = ref.id)).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteDependent(uid: String, dependentId: String): Result<Unit> = try {
        col(uid).document(dependentId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
