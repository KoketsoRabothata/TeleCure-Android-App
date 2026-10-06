package com.telecure.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.telecure.app.navigation.Screen
import com.telecure.app.ui.components.EmergencySosDialog
import com.telecure.app.ui.theme.TealDark
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextPrimary
import com.telecure.app.ui.theme.TextSecondary
import com.telecure.app.ui.viewmodel.HomeViewModel
import java.time.LocalTime

private fun greeting(): String = when (LocalTime.now().hour) {
    in 0..11 -> "Good morning"
    in 12..16 -> "Good afternoon"
    else -> "Good evening"
}

@Composable
fun HomeScreen(navController: NavController, vm: HomeViewModel = viewModel()) {
    LaunchedEffect(Unit) { vm.load() }
    var showSosDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(greeting(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(
                    vm.profile?.fullName?.ifBlank { "Take care of your health today." } ?: "Take care of your health today.",
                    color = TextSecondary
                )
            }
            Box(
                Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFDFF4F1)).clickable(onClick = { navController.navigate(Screen.Profile.route) }),
                contentAlignment = Alignment.Center
            ) {
                if (vm.profile?.avatarUrl.isNullOrBlank()) {
                    Icon(Icons.Filled.Person, contentDescription = "Profile", tint = TealPrimary)
                } else {
                    AsyncImage(model = vm.profile?.avatarUrl, contentDescription = "Profile", modifier = Modifier.fillMaxSize().clip(CircleShape))
                }
            }
        }
        Spacer(Modifier.height(18.dp))

        OutlinedTextField(
            value = "", onValueChange = {},
            placeholder = { Text("Find a doctor or specialty") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth().clickable(onClick = { navController.navigate(Screen.DoctorList.route) }),
            enabled = false,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(disabledContainerColor = Color.White, disabledBorderColor = Color(0xFFE3EFED))
        )

        Spacer(Modifier.height(22.dp))
        Text("Quick Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickAction(Icons.Filled.EventAvailable, "Book\nConsultation", Modifier.weight(1f)) { navController.navigate(Screen.DoctorList.route) }
            QuickAction(Icons.Filled.VideoCall, "Video Call", Modifier.weight(1f)) { navController.navigate(Screen.DoctorList.route) }
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickAction(Icons.Filled.Medication, "Order\nMedicines", Modifier.weight(1f)) { navController.navigate(Screen.Pharmacy.route) }
            QuickAction(Icons.Filled.Folder, "My Records", Modifier.weight(1f)) { navController.navigate(Screen.Prescriptions.route) }
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickAction(Icons.Filled.HealthAndSafety, "Symptom\nChecker", Modifier.weight(1f)) { navController.navigate(Screen.SymptomChecker.route) }
            QuickAction(
                Icons.Filled.Warning, "Emergency", Modifier.weight(1f),
                gradientColors = listOf(Color(0xFFE5484D), Color(0xFFB22B2F))
            ) { showSosDialog = true }
        }

        Spacer(Modifier.height(26.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Upcoming Appointment", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            TextButton(onClick = { navController.navigate(Screen.MyAppointments.route) }) { Text("View all") }
        }
        val appt = vm.upcomingAppointment
        if (appt == null) {
            Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFFF1F8F7), modifier = Modifier.fillMaxWidth()) {
                Text("No upcoming appointments yet.", modifier = Modifier.padding(20.dp), color = TextSecondary)
            }
        } else {
            Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFFF1F8F7), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(model = appt.doctorPhotoUrl, contentDescription = null, modifier = Modifier.size(52.dp).clip(CircleShape).background(Color.LightGray))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(appt.doctorName, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text("${appt.date} • ${appt.time}", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                    }
                    Button(onClick = { navController.navigate(Screen.VideoConsultation.build(appt.id)) }, colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) {
                        Text("Join")
                    }
                }
            }
        }

        Spacer(Modifier.height(26.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Health Tips", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            TextButton(onClick = { navController.navigate(Screen.HealthTips.route) }) { Text("View all") }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(vm.healthTips) { tip ->
                Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFEFF7F6), modifier = Modifier.width(220.dp).clickable(onClick = { navController.navigate(Screen.HealthTips.route) })) {
                    Column(Modifier.padding(14.dp)) {
                        Text(tip.category, color = TealPrimary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(tip.title, fontWeight = FontWeight.SemiBold, maxLines = 2, color = TextPrimary)
                        Spacer(Modifier.height(4.dp))
                        Text(tip.excerpt, color = TextSecondary, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
                    }
                }
            }
        }
        Spacer(Modifier.height(80.dp))
    }

    if (showSosDialog) {
        EmergencySosDialog(
            emergencyContactPhone = vm.profile?.emergencyContactPhone.orEmpty(),
            onDismiss = { showSosDialog = false }
        )
    }
}

@Composable
private fun QuickAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(TealPrimary, TealDark),
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        modifier = modifier.height(96.dp).clickable(onClick = onClick)
    ) {
        Box(
            Modifier.fillMaxSize().background(Brush.linearGradient(gradientColors)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(icon, contentDescription = label, tint = Color.White)
                Spacer(Modifier.height(8.dp))
                Text(label, textAlign = androidx.compose.ui.text.style.TextAlign.Center, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    }
}
