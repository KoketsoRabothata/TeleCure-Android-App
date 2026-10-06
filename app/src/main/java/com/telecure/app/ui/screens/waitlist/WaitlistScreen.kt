package com.telecure.app.ui.screens.waitlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.telecure.app.ui.components.EmptyState
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextPrimary
import com.telecure.app.ui.theme.TextSecondary
import com.telecure.app.ui.viewmodel.WaitlistViewModel

@Composable
fun WaitlistScreen(navController: NavController, vm: WaitlistViewModel = viewModel()) {
    LaunchedEffect(Unit) { vm.load() }

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("My Waitlist", navController)
        when {
            vm.isLoading -> LoadingIndicator()
            vm.entries.isEmpty() -> EmptyState(Icons.Filled.HourglassTop, "Not on any waitlists", "Join a doctor's waitlist from their profile if no slot works for you.")
            else -> LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(vm.entries) { entry ->
                    Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFF7FBFA), modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.HourglassTop, contentDescription = null, tint = TealPrimary)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(entry.doctorName, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text(
                                    if (entry.preferredDate.isNotBlank()) "Preferred: ${entry.preferredDate}" else "Any upcoming slot",
                                    color = TextSecondary, style = MaterialTheme.typography.bodySmall
                                )
                            }
                            IconButton(onClick = { vm.leave(entry.id) }) {
                                Icon(Icons.Filled.Close, contentDescription = "Leave waitlist", tint = Color(0xFFE5484D))
                            }
                        }
                    }
                }
            }
        }
    }
}
