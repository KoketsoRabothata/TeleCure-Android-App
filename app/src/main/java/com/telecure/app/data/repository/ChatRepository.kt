package com.telecure.app.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.telecure.app.data.model.ChatMessage
import com.telecure.app.data.model.ChatThread
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ChatRepository {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val threadsCol = db.collection("chat_threads")

    fun getChatId(uidA: String, uidB: String): String =
        listOf(uidA, uidB).sorted().joinToString("_")

    suspend fun getThreads(patientUid: String): List<ChatThread> = try {
        threadsCol.whereEqualTo("patientUid", patientUid).get().await().toObjects(ChatThread::class.java)
    } catch (e: Exception) { emptyList() }

    suspend fun ensureThread(patientUid: String, doctorUid: String, doctorName: String, doctorPhotoUrl: String): String {
        val chatId = getChatId(patientUid, doctorUid)
        val doc = threadsCol.document(chatId)
        val existing = doc.get().await()
        if (!existing.exists()) {
            doc.set(
                ChatThread(id = chatId, patientUid = patientUid, doctorUid = doctorUid, doctorName = doctorName, doctorPhotoUrl = doctorPhotoUrl)
            ).await()
        }
        return chatId
    }

    fun listenMessages(chatId: String, onUpdate: (List<ChatMessage>) -> Unit) =
        threadsCol.document(chatId).collection("messages")
            .orderBy("sentAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snap, _ ->
                onUpdate(snap?.toObjects(ChatMessage::class.java) ?: emptyList())
            }

    suspend fun sendMessage(chatId: String, message: ChatMessage): Result<Unit> = try {
        val msgRef = threadsCol.document(chatId).collection("messages").document()
        msgRef.set(message.copy(id = msgRef.id)).await()
        threadsCol.document(chatId).update("lastMessage", message.text).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun uploadAttachment(chatId: String, fileUri: Uri): Result<String> = try {
        val ref = storage.reference.child("chat_attachments/$chatId/${UUID.randomUUID()}")
        ref.putFile(fileUri).await()
        val url = ref.downloadUrl.await().toString()
        Result.success(url)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
