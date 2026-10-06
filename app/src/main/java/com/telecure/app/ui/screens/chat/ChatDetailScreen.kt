package com.telecure.app.ui.screens.chat

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.navigation.Screen
import com.telecure.app.ui.components.FIXED_CONTACT_UIDS
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.viewmodel.ChatViewModel

@Composable
fun ChatDetailScreen(navController: NavController, chatId: String, doctorName: String, vm: ChatViewModel = viewModel()) {
    LaunchedEffect(chatId) { vm.listen(chatId) }
    val myUid = FirebaseAuth.getInstance().currentUser?.uid
    val otherUid = remember(chatId, myUid) { chatId.split("_").firstOrNull { it != myUid }.orEmpty() }
    val isRealDoctor = otherUid.isNotBlank() && otherUid !in FIXED_CONTACT_UIDS
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    val attachmentPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val name = uri.lastPathSegment ?: "Document"
            vm.uploadAndSendDocument(chatId, uri, name)
        }
    }

    LaunchedEffect(vm.messages.size) {
        if (vm.messages.isNotEmpty()) listState.animateScrollToItem(vm.messages.size - 1)
    }

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar(doctorName, navController)
        LazyColumn(state = listState, modifier = Modifier.weight(1f).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vm.messages) { msg ->
                val mine = msg.senderUid == myUid
                Row(Modifier.fillMaxWidth(), horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start) {
                    Surface(
                        color = if (mine) TealPrimary else Color(0xFFF0F0F0),
                        shape = RoundedCornerShape(14.dp),
                        modifier = if (msg.documentUrl.isNotBlank())
                            Modifier.clickable { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(msg.documentUrl))) }
                        else Modifier
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (msg.documentUrl.isNotBlank()) {
                                Icon(Icons.Filled.Description, contentDescription = null, tint = if (mine) Color.White else TealPrimary, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                            }
                            Text(msg.text, color = if (mine) Color.White else Color.Black)
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            if (isRealDoctor) {
                IconButton(onClick = { navController.navigate(Screen.DoctorDetail.build(otherUid)) }) {
                    Icon(Icons.Filled.VideoCall, contentDescription = "Request Video Call", tint = TealPrimary)
                }
            }
            IconButton(onClick = { attachmentPicker.launch("*/*") }) {
                Icon(Icons.Filled.AttachFile, contentDescription = "Share Report", tint = TealPrimary)
            }
            OutlinedTextField(
                value = input, onValueChange = { input = it },
                placeholder = { Text("Type a message") },
                modifier = Modifier.weight(1f), singleLine = true
            )
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = { vm.send(chatId, input); input = "" }) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = TealPrimary)
            }
        }
    }
}
