package com.telecure.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.telecure.app.data.model.HealthTip
import kotlinx.coroutines.tasks.await

class HealthTipRepository {
    private val db = FirebaseFirestore.getInstance()
    private val col = db.collection("health_tips")

    suspend fun getTips(category: String? = null): List<HealthTip> = try {
        val all = col.get().await().toObjects(HealthTip::class.java)
        if (category.isNullOrBlank() || category == "All") all else all.filter { it.category == category }
    } catch (e: Exception) { emptyList() }

    suspend fun getTip(id: String): HealthTip? = try {
        col.document(id).get().await().toObject(HealthTip::class.java)
    } catch (e: Exception) { null }
}
