package com.telecure.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.telecure.app.data.model.PaymentMethod
import kotlinx.coroutines.tasks.await

class PaymentMethodRepository {
    private val db = FirebaseFirestore.getInstance()
    private fun col(uid: String) = db.collection("users").document(uid).collection("payment_methods")

    suspend fun getMethods(uid: String): List<PaymentMethod> = try {
        col(uid).get().await().toObjects(PaymentMethod::class.java)
    } catch (e: Exception) { emptyList() }

    suspend fun addMethod(uid: String, method: PaymentMethod): Result<Unit> = try {
        val ref = col(uid).document()
        if (method.isDefault) clearDefaults(uid)
        ref.set(method.copy(id = ref.id)).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun setDefault(uid: String, methodId: String): Result<Unit> = try {
        clearDefaults(uid)
        col(uid).document(methodId).update("isDefault", true).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteMethod(uid: String, methodId: String): Result<Unit> = try {
        col(uid).document(methodId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    private suspend fun clearDefaults(uid: String) {
        val existing = col(uid).get().await().toObjects(PaymentMethod::class.java)
        existing.filter { it.isDefault }.forEach { col(uid).document(it.id).update("isDefault", false).await() }
    }
}
