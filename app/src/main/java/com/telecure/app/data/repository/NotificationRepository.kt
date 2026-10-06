package com.telecure.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.telecure.app.data.model.AppNotification
import kotlinx.coroutines.tasks.await

class NotificationRepository {
    private val db = FirebaseFirestore.getInstance()
    private val col = db.collection("notifications")

    suspend fun getNotifications(userUid: String): List<AppNotification> = try {
        col.whereEqualTo("userUid", userUid).get().await().toObjects(AppNotification::class.java)
    } catch (e: Exception) { emptyList() }

    suspend fun markAsRead(id: String) = try {
        col.document(id).update("isRead", true).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun markAllRead(userUid: String) = try {
        val docs = col.whereEqualTo("userUid", userUid).get().await()
        docs.documents.forEach { it.reference.update("isRead", true) }
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun clearAll(userUid: String) = try {
        val docs = col.whereEqualTo("userUid", userUid).get().await()
        docs.documents.forEach { it.reference.delete() }
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }
}
