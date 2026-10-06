package com.telecure.app.ui.screens.video

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
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
import com.telecure.app.data.model.Appointment
import com.telecure.app.data.model.AppointmentStatus
import com.telecure.app.data.repository.AppointmentRepository
import com.telecure.app.data.repository.LoyaltyRepository
import com.telecure.app.ui.components.SuccessState
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.viewmodel.ReviewViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VideoConsultationScreen(navController: NavController, appointmentId: String, reviewVm: ReviewViewModel = viewModel()) {
    var seconds by remember { mutableStateOf(0) }
    var muted by remember { mutableStateOf(false) }
    var cameraOn by remember { mutableStateOf(true) }
    var chatOpen by remember { mutableStateOf(false) }
    var callEnded by remember { mutableStateOf(false) }
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }
    var appointment by remember { mutableStateOf<Appointment?>(null) }

    val scope = rememberCoroutineScope()
    val appointmentRepo = remember { AppointmentRepository() }
    val loyaltyRepo = remember { LoyaltyRepository() }

    LaunchedEffect(appointmentId) { appointment = appointmentRepo.getAppointment(appointmentId) }

    LaunchedEffect(Unit) {
        while (true) { delay(1000); seconds++ }
    }

    if (callEnded) {
        Column(Modifier.fillMaxSize()) {
            SuccessState(
                title = "Consultation Completed",
                subtitle = "Thanks for using TeleCure. Rate your doctor below.",
                buttonText = if (reviewVm.submitted) "Done" else "Skip",
                onButtonClick = { navController.popBackStack() }
            )
            Column(Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    (1..5).forEach { i ->
                        Icon(
                            if (i <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = "Rate $i",
                            tint = Color(0xFFF5A623),
                            modifier = Modifier.size(32.dp).clip(CircleShape).clickable { rating = i }
                        )
                    }
                }
                if (!reviewVm.submitted) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = comment, onValueChange = { comment = it },
                        placeholder = { Text("Optional feedback about your consultation") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val doctorUid = appointment?.doctorUid ?: return@Button
                            reviewVm.submitReview(doctorUid, appointmentId, rating, comment)
                        },
                        enabled = rating > 0 && !reviewVm.isSubmitting && appointment != null,
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        if (reviewVm.isSubmitting) CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                        else Text("Submit Rating")
                    }
                } else {
                    Spacer(Modifier.height(8.dp))
                    Text("Thanks for your feedback!", color = TealPrimary, fontWeight = FontWeight.Medium)
                }
            }
        }
        return
    }

    Box(Modifier.fillMaxSize().background(Color(0xFF10201E))) {
        // Full-screen "doctor" video placeholder
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(140.dp))
        }

        // Top bar
        Row(
            Modifier.fillMaxWidth().padding(20.dp).align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
        ) {
            Text(appointment?.doctorName?.ifBlank { "Consultation" } ?: "Consultation", color = Color.White, fontWeight = FontWeight.SemiBold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.SignalCellular4Bar, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(String.format("%02d:%02d", seconds / 60, seconds % 60), color = Color.White)
            }
        }

        // Self-view PiP
        Box(
            Modifier.align(Alignment.TopEnd).padding(top = 70.dp, end = 20.dp)
                .size(90.dp, 130.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF2A3F3D)),
            contentAlignment = Alignment.Center
        ) {
            if (cameraOn) Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White.copy(alpha = 0.6f))
            else Icon(Icons.Filled.VideocamOff, contentDescription = null, tint = Color.White.copy(alpha = 0.6f))
        }

        // Bottom controls
        Row(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CallControl(if (muted) Icons.Filled.MicOff else Icons.Filled.Mic) { muted = !muted }
            CallControl(if (cameraOn) Icons.Filled.Videocam else Icons.Filled.VideocamOff) { cameraOn = !cameraOn }
            CallControl(Icons.Filled.CallEnd, background = Color(0xFFE5484D)) {
                callEnded = true
                scope.launch {
                    appointmentRepo.updateStatus(appointmentId, AppointmentStatus.COMPLETED)
                    val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                    if (uid != null) loyaltyRepo.awardPoints(uid, 20, "Completed consultation")
                }
            }
            CallControl(Icons.AutoMirrored.Filled.Chat) { chatOpen = !chatOpen }
            CallControl(Icons.Filled.Cameraswitch) { }
        }

        AnimatedVisibility(visible = chatOpen, modifier = Modifier.align(Alignment.BottomCenter)) {
            Surface(color = Color.White, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp), modifier = Modifier.fillMaxWidth().height(280.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("In-call chat", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    Text("Chat and document sharing during the call.", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun CallControl(icon: androidx.compose.ui.graphics.vector.ImageVector, background: Color = Color.White.copy(alpha = 0.15f), onClick: () -> Unit) {
    Box(
        Modifier.size(52.dp).clip(CircleShape).background(background)
            .then(Modifier),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) {
            Icon(icon, contentDescription = null, tint = Color.White)
        }
    }
}
