package com.telecure.app.ui.screens.pharmacistchat

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.data.repository.ChatRepository
import com.telecure.app.navigation.Screen
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.components.PHARMACIST_NAME
import com.telecure.app.ui.components.PHARMACIST_UID

@Composable
fun PharmacistChatScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val chatId = ChatRepository().ensureThread(uid, PHARMACIST_UID, PHARMACIST_NAME, "")
            navController.navigate(Screen.ChatDetail.build(chatId, PHARMACIST_NAME)) {
                popUpTo(Screen.PharmacistChat.route) { inclusive = true }
            }
        }
    }
    LoadingIndicator(modifier = Modifier.fillMaxSize())
}
