package com.telecure.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.data.model.ChatMessage
import com.telecure.app.data.model.ChatThread
import com.telecure.app.data.repository.ChatRepository
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val repo = ChatRepository()

    var threads by mutableStateOf<List<ChatThread>>(emptyList()); private set
    var messages by mutableStateOf<List<ChatMessage>>(emptyList()); private set
    var isLoading by mutableStateOf(true); private set

    fun loadThreads() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            isLoading = true
            threads = repo.getThreads(uid)
            isLoading = false
        }
    }

    fun listen(chatId: String) {
        repo.listenMessages(chatId) { messages = it }
    }

    fun send(chatId: String, text: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            repo.sendMessage(chatId, ChatMessage(senderUid = uid, text = text))
        }
    }

    fun sendDocument(chatId: String, documentUrl: String, fileName: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            repo.sendMessage(chatId, ChatMessage(senderUid = uid, text = "📎 $fileName", documentUrl = documentUrl))
        }
    }

    fun uploadAndSendDocument(chatId: String, fileUri: android.net.Uri, fileName: String) {
        viewModelScope.launch {
            repo.uploadAttachment(chatId, fileUri).onSuccess { url ->
                sendDocument(chatId, url, fileName)
            }
        }
    }
}
