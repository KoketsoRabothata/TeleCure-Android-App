package com.telecure.app.ui.screens.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventBusy
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
import com.telecure.app.data.model.Appointment
import com.telecure.app.navigation.Screen
import com.telecure.app.ui.components.EmptyState
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextPrimary
import com.telecure.app.ui.theme.TextSecondary
import com.telecure.app.ui.viewmodel.AppointmentsViewModel

@Composable
fun MyAppointmentsScreen(navController: NavController, vm: AppointmentsViewModel = viewModel()) {
    LaunchedEffect(Unit) { vm.load() }
    var tab by remember { mutableStateOf(0) }
    val tabs = listOf("Upcoming", "Past", "Cancelled")
    val filtered = when (tab) {
        0 -> vm.appointments.filter { it.status == "UPCOMING" }
        1 -> vm.appointments.filter { it.status == "COMPLETED" }
        else -> vm.appointments.filter { it.status == "CANCELLED" }
    }

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("My Appointments", navController, showBack = false)
        TabRow(selectedTabIndex = tab, contentColor = TealPrimary) {
            tabs.forEachIndexed { i, label -> Tab(selected = tab == i, onClick = { tab = i }, text = { Text(label) }) }
        }
        when {
            vm.isLoading -> LoadingIndicator()
            filtered.isEmpty() -> EmptyState(Icons.Filled.EventBusy, "No appointments", "Your ${tabs[tab].lowercase()} appointments will appear here.")
            else -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filtered) { appt -> AppointmentCard(appt, navController, vm) }
            }
        }
    }
}

@Composable
private fun AppointmentCard(appt: Appointment, navController: NavController, vm: AppointmentsViewModel) {
    Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFFF7FBFA), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(model = appt.doctorPhotoUrl, contentDescription = null, modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.LightGray))
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(appt.doctorName, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text("${appt.date} • ${appt.time}", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                    if (appt.bookedForName.isNotBlank()) {
                        Text("For: ${appt.bookedForName}", color = TealPrimary, style = MaterialTheme.typography.bodySmall)
                    }
                }
                AssistChip(onClick = {}, label = { Text(appt.status.lowercase().replaceFirstChar { it.uppercase() }) })
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (appt.status == "UPCOMING") {
                    Button(onClick = { navController.navigate(Screen.VideoConsultation.build(appt.id)) }, colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) {
                        Text("Join Video")
                    }
                    OutlinedButton(onClick = { navController.navigate(com.telecure.app.navigation.Screen.BookAppointment.build(appt.doctorUid)) }) { Text("Reschedule") }
                    OutlinedButton(onClick = { vm.cancel(appt.id) }) { Text("Cancel") }
                } else {
                    OutlinedButton(onClick = { navController.navigate(Screen.Prescriptions.route) }) { Text("View e-Prescription") }
                }
            }
        }
    }
}
