package com.telecure.app.ui.screens.refills

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.data.model.Appointment
import com.telecure.app.data.model.RefillRequest
import com.telecure.app.data.repository.AppointmentRepository
import com.telecure.app.ui.components.EmptyState
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextPrimary
import com.telecure.app.ui.theme.TextSecondary
import com.telecure.app.ui.viewmodel.RefillViewModel

private fun statusColor(status: String) = when (status) {
    "APPROVED" -> Color(0xFF2E9E5B)
    "DECLINED" -> Color(0xFFE5484D)
    else -> Color(0xFFF5A623)
}

@Composable
fun RefillRequestsScreen(navController: NavController, vm: RefillViewModel = viewModel()) {
    LaunchedEffect(Unit) { vm.load() }
    var pastDoctors by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val all = AppointmentRepository().getAppointments(uid)
            pastDoctors = all.distinctBy { it.doctorUid }
        }
    }

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("Prescription Refills", navController)
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.End) {
            Button(onClick = { showAddDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = TealPrimary), enabled = pastDoctors.isNotEmpty()) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Request Refill")
            }
        }
        Spacer(Modifier.height(8.dp))
        when {
            vm.isLoading -> LoadingIndicator()
            vm.requests.isEmpty() -> EmptyState(Icons.Filled.Autorenew, "No refill requests yet", "Request a renewal for your chronic medication from a doctor you've seen before.")
            else -> LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(vm.requests) { req ->
                    Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFF7FBFA), modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(14.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(req.medicineName, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                AssistChip(onClick = {}, label = { Text(req.status.lowercase().replaceFirstChar { it.uppercase() }) },
                                    colors = AssistChipDefaults.assistChipColors(labelColor = statusColor(req.status)))
                            }
                            Text("Dr. ${req.doctorName}", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                            if (req.doctorNote.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(req.doctorNote, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        RequestRefillDialog(
            doctors = pastDoctors,
            onDismiss = { showAddDialog = false },
            onConfirm = { doctorUid, doctorName, medicineName ->
                vm.requestRefill(doctorUid, doctorName, medicineName)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun RequestRefillDialog(doctors: List<Appointment>, onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var selectedDoctor by remember { mutableStateOf(doctors.firstOrNull()) }
    var medicineName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Request Prescription Refill") },
        text = {
            Column {
                Text("Doctor", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Spacer(Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(doctors) { appt ->
                        FilterChip(
                            selected = selectedDoctor?.doctorUid == appt.doctorUid,
                            onClick = { selectedDoctor = appt },
                            label = { Text(appt.doctorName) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealPrimary, selectedLabelColor = Color.White)
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(medicineName, { medicineName = it }, label = { Text("Medicine Name") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(
                onClick = { selectedDoctor?.let { onConfirm(it.doctorUid, it.doctorName, medicineName) } },
                enabled = medicineName.isNotBlank() && selectedDoctor != null
            ) { Text("Send Request") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
