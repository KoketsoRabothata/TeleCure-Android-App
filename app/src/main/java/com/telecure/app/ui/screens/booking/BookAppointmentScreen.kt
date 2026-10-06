package com.telecure.app.ui.screens.booking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.telecure.app.navigation.Screen
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextPrimary
import com.telecure.app.ui.viewmodel.BookingViewModel
import com.telecure.app.ui.viewmodel.DependentsViewModel
import java.net.URLEncoder

private val dates = listOf("Mon 1", "Tue 2", "Wed 3", "Thu 4", "Fri 5", "Sat 6")
private val times = listOf("09:00 AM", "10:30 AM", "12:00 PM", "02:00 PM", "04:30 PM", "06:00 PM")
private val types = listOf("Video", "Chat", "In-clinic")
private const val SELF = "Myself"

@Composable
fun BookAppointmentScreen(
    navController: NavController, doctorId: String,
    vm: BookingViewModel = viewModel(), dependentsVm: DependentsViewModel = viewModel()
) {
    LaunchedEffect(doctorId) { vm.loadDoctor(doctorId) }
    LaunchedEffect(Unit) { dependentsVm.load() }
    val doctor = vm.doctor

    var selectedDate by remember { mutableStateOf(dates.first()) }
    var selectedTime by remember { mutableStateOf(times.first()) }
    var selectedType by remember { mutableStateOf(types.first()) }
    var reason by remember { mutableStateOf("") }
    var bookedFor by remember { mutableStateOf(SELF) }

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("Book Appointment", navController)
        if (doctor == null) {
            LoadingIndicator()
        } else {
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
                Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFEFF7F6), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Text(doctor.name, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(doctor.specialty, color = TealPrimary, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(Modifier.height(20.dp))
                Text("Booking For", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(selected = bookedFor == SELF, onClick = { bookedFor = SELF }, label = { Text(SELF) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealPrimary, selectedLabelColor = Color.White))
                    }
                    items(dependentsVm.dependents) { dep ->
                        FilterChip(selected = bookedFor == dep.name, onClick = { bookedFor = dep.name }, label = { Text(dep.name) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealPrimary, selectedLabelColor = Color.White))
                    }
                }

                Spacer(Modifier.height(20.dp))
                Text("Select Date", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(dates) { d ->
                        FilterChip(selected = selectedDate == d, onClick = { selectedDate = d }, label = { Text(d) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealPrimary, selectedLabelColor = Color.White))
                    }
                }

                Spacer(Modifier.height(20.dp))
                Text("Select Time", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                FlowRowSimple(times) { t ->
                    FilterChip(selected = selectedTime == t, onClick = { selectedTime = t }, label = { Text(t) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealPrimary, selectedLabelColor = Color.White))
                }

                Spacer(Modifier.height(20.dp))
                Text("Consultation Type", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    types.forEach { t ->
                        FilterChip(selected = selectedType == t, onClick = { selectedType = t }, label = { Text(t) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealPrimary, selectedLabelColor = Color.White))
                    }
                }

                Spacer(Modifier.height(20.dp))
                Text("Reason for Visit", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = reason, onValueChange = { reason = it },
                    placeholder = { Text("Briefly describe your symptoms or reason") },
                    modifier = Modifier.fillMaxWidth().height(100.dp)
                )

                Spacer(Modifier.height(16.dp))
                Text("Upload previous reports (optional)", color = TealPrimary, fontWeight = FontWeight.Medium)

                Spacer(Modifier.height(20.dp))
                Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFF7FBFA), modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Consultation Fee", color = TextPrimary)
                        Text("R${doctor.consultationFee.toInt()}", fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
                Spacer(Modifier.height(100.dp))
            }

            Surface(shadowElevation = 8.dp, color = Color.White) {
                Button(
                    onClick = {
                        val encodedReason = URLEncoder.encode(reason.ifBlank { "-" }, "UTF-8")
                        val encodedBookedFor = URLEncoder.encode(if (bookedFor == SELF) "-" else bookedFor, "UTF-8")
                        navController.navigate(Screen.Payment.build(doctorId, selectedDate, selectedTime, selectedType, encodedReason, encodedBookedFor))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(52.dp)
                ) { Text("Confirm & Pay") }
            }
        }
    }
}

// Simple flow-row substitute (avoids extra accompanist dependency)
@Composable
private fun FlowRowSimple(items: List<String>, content: @Composable (String) -> Unit) {
    val chunked = items.chunked(3)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        chunked.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { content(it) }
            }
        }
    }
}
