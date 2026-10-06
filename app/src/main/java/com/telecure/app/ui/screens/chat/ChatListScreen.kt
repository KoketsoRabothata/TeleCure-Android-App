package com.telecure.app.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.telecure.app.navigation.Screen
import com.telecure.app.ui.components.EmptyState
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TextSecondary
import com.telecure.app.ui.viewmodel.ChatViewModel

@Composable
fun ChatListScreen(navController: NavController, vm: ChatViewModel = viewModel()) {
    LaunchedEffect(Unit) { vm.loadThreads() }

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("Messages", navController, showBack = false)
        when {
            vm.isLoading -> LoadingIndicator()
            vm.threads.isEmpty() -> EmptyState(Icons.Filled.ChatBubbleOutline, "No conversations yet", "Message a doctor from their profile to start a chat.")
            else -> LazyColumn {
                items(vm.threads) { thread ->
                    Row(
                        Modifier.fillMaxWidth().clickable { navController.navigate(Screen.ChatDetail.build(thread.id, thread.doctorName)) }.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(model = thread.doctorPhotoUrl, contentDescription = null, modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.LightGray))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(thread.doctorName, fontWeight = FontWeight.SemiBold)
                            Text(thread.lastMessage.ifBlank { "Say hello!" }, color = TextSecondary, maxLines = 1)
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}
