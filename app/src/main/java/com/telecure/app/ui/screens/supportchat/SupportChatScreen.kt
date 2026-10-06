package com.telecure.app.ui.screens.supportchat

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.data.repository.ChatRepository
import com.telecure.app.navigation.Screen
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.components.SUPPORT_NAME
import com.telecure.app.ui.components.SUPPORT_UID

@Composable
fun SupportChatScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val chatId = ChatRepository().ensureThread(uid, SUPPORT_UID, SUPPORT_NAME, "")
            navController.navigate(Screen.ChatDetail.build(chatId, SUPPORT_NAME)) {
                popUpTo(Screen.SupportChat.route) { inclusive = true }
            }
        }
    }
    LoadingIndicator(modifier = Modifier.fillMaxSize())
}
