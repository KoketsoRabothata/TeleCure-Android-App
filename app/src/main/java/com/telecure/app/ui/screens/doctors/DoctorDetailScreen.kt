package com.telecure.app.ui.screens.doctors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextPrimary
import com.telecure.app.ui.theme.TextSecondary
import com.telecure.app.ui.viewmodel.DoctorViewModel
import com.telecure.app.ui.viewmodel.ReviewViewModel
import com.telecure.app.ui.viewmodel.WaitlistViewModel

@Composable
fun DoctorDetailScreen(navController: NavController, doctorId: String, vm: DoctorViewModel = viewModel(), reviewVm: ReviewViewModel = viewModel(), waitlistVm: WaitlistViewModel = viewModel()) {
    var showWaitlistDialog by remember { mutableStateOf(false) }
    LaunchedEffect(doctorId) { vm.loadDoctor(doctorId); reviewVm.loadReviews(doctorId) }
    val doctor = vm.selectedDoctor

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("Doctor Profile", navController)
        if (doctor == null) {
            LoadingIndicator()
        } else {
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        AsyncImage(model = doctor.photoUrl, contentDescription = doctor.name, modifier = Modifier.size(88.dp).clip(CircleShape).background(Color.LightGray))
                        if (doctor.verified) {
                            Icon(
                                Icons.Filled.Verified, contentDescription = "Verified", tint = TealPrimary,
                                modifier = Modifier.align(Alignment.BottomEnd).size(22.dp).background(Color.White, CircleShape)
                            )
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(doctor.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(doctor.specialty, color = TealPrimary)
                        Text("${doctor.experienceYears} yrs • ${doctor.languages.joinToString()}", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                        if (doctor.nationality.isNotBlank()) Text(doctor.nationality, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                }

                Spacer(Modifier.height(20.dp))
                Text("About", fontWeight = FontWeight.SemiBold)
                Text(doctor.about.ifBlank { "No bio provided yet." }, color = TextSecondary, modifier = Modifier.padding(top = 4.dp))

                Spacer(Modifier.height(16.dp))
                Text("Education", fontWeight = FontWeight.SemiBold)
                Text(doctor.education.ifBlank { "—" }, color = TextSecondary, modifier = Modifier.padding(top = 4.dp))

                Spacer(Modifier.height(16.dp))
                Text("Clinic Affiliation", fontWeight = FontWeight.SemiBold)
                Text(doctor.clinicAffiliation.ifBlank { "—" }, color = TextSecondary, modifier = Modifier.padding(top = 4.dp))

                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFF5A623))
                    Text(" ${doctor.rating} rating  •  ${doctor.reviewCount} reviews", fontWeight = FontWeight.Medium)
                }

                Spacer(Modifier.height(16.dp))
                Text("Available Slots", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("Today 2:00 PM", "Today 4:30 PM", "Tomorrow 9:00 AM", "Tomorrow 11:00 AM")) { slot ->
                        Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFFEFF7F6)) {
                            Text(slot, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Patient Reviews", fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(8.dp))
                if (reviewVm.reviews.isEmpty()) {
                    Text("No reviews yet.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                } else {
                    reviewVm.reviews.take(5).forEach { review ->
                        Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFF7FBFA), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                            Column(Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    repeat(5) { i ->
                                        Icon(
                                            if (i < review.rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                                            contentDescription = null, tint = Color(0xFFF5A623), modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    if (review.patientName.isNotBlank()) {
                                        Spacer(Modifier.width(8.dp))
                                        Text(review.patientName, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    }
                                }
                                if (review.comment.isNotBlank()) {
                                    Spacer(Modifier.height(4.dp))
                                    Text(review.comment, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(90.dp))
            }

            Surface(shadowElevation = 8.dp, color = Color.White) {
                Column(Modifier.padding(16.dp)) {
                    Button(
                        onClick = { navController.navigate(Screen.BookAppointment.build(doctor.uid)) },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) { Text("Book Appointment") }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { navController.navigate(Screen.BookAppointment.build(doctor.uid)) },
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Icon(Icons.Filled.VideoCall, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Video Consult Now")
                    }
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = { showWaitlistDialog = true }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Filled.NotificationsActive, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("No slots work for you? Join the waitlist")
                    }
                }
            }
        }
    }

    if (showWaitlistDialog) {
        var preferredDate by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showWaitlistDialog = false },
            title = { Text("Join Waitlist") },
            text = {
                Column {
                    Text("We'll notify you if an earlier slot opens up with ${doctor?.name ?: "this doctor"}.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(preferredDate, { preferredDate = it }, label = { Text("Preferred date") }, placeholder = { Text("e.g. This week") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    doctor?.let { waitlistVm.join(it.uid, it.name, preferredDate) }
                    showWaitlistDialog = false
                }) { Text("Join") }
            },
            dismissButton = { TextButton(onClick = { showWaitlistDialog = false }) { Text("Cancel") } }
        )
    }
}
