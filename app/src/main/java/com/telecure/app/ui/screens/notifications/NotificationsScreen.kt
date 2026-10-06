package com.telecure.app.ui.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.telecure.app.data.model.AppNotification
import com.telecure.app.ui.components.EmptyState
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextSecondary
import com.telecure.app.ui.viewmodel.NotificationsViewModel

private fun iconFor(type: String) = when (type) {
    "APPOINTMENT" -> Icons.Filled.EventAvailable
    "PRESCRIPTION" -> Icons.Filled.Description
    "ORDER" -> Icons.Filled.LocalShipping
    "HEALTH_TIP" -> Icons.AutoMirrored.Filled.Article
    else -> Icons.Filled.Notifications
}

@Composable
fun NotificationsScreen(_navController: NavController, vm: NotificationsViewModel = viewModel()) {
    LaunchedEffect(Unit) { vm.load() }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(20.dp, 16.dp, 20.dp, 0.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Notifications", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            TextButton(onClick = { vm.markAllRead() }) { Text("Mark all read") }
            TextButton(onClick = { vm.clearAll() }) { Text("Clear all") }
        }
        when {
            vm.isLoading -> LoadingIndicator()
            vm.notifications.isEmpty() -> EmptyState(Icons.Filled.NotificationsNone, "You're all caught up", "New notifications will appear here.")
            else -> LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(vm.notifications) { n -> NotificationRow(n, vm) }
            }
        }
    }
}

@Composable
private fun NotificationRow(n: AppNotification, vm: NotificationsViewModel) {
    Row(
        Modifier.fillMaxWidth().clickable { vm.markRead(n.id) }.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(40.dp).background(if (n.isRead) Color(0xFFEFEFEF) else Color(0xFFDFF4F1), CircleShape), contentAlignment = Alignment.Center) {
            Icon(iconFor(n.type), contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(n.title, fontWeight = if (n.isRead) FontWeight.Normal else FontWeight.Bold)
            Text(n.body, color = TextSecondary, style = MaterialTheme.typography.bodySmall, maxLines = 2)
        }
        if (!n.isRead) Box(Modifier.size(8.dp).background(TealPrimary, CircleShape))
    }
}