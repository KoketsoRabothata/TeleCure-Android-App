package com.telecure.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.telecure.app.navigation.Screen
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.theme.TealDark
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextSecondary
import com.telecure.app.ui.viewmodel.ProfileViewModel

private data class MenuEntry(val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String, val route: String?)

// Swap for your own hosted banner asset any time — this is just a placeholder stock photo.
private const val PROFILE_BANNER_URL = "https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=1000&q=80"

@Composable
fun ProfileScreen(navController: NavController, onSignedOut: () -> Unit, vm: ProfileViewModel = viewModel()) {
    LaunchedEffect(Unit) { vm.load() }
    val profile = vm.profile

    val menu = listOf(
        MenuEntry(Icons.AutoMirrored.Filled.EventNote, "My Appointments", Screen.MyAppointments.route),
        MenuEntry(Icons.Filled.Inventory2, "My Orders", Screen.MyOrders.route),
        MenuEntry(Icons.Filled.Folder, "Health Records", Screen.HealthRecords.route),
        MenuEntry(Icons.Filled.CreditCard, "Medical Aid", Screen.MedicalAid.route),
        MenuEntry(Icons.Filled.Science, "Lab Tests & Results", Screen.LabTests.route),
        MenuEntry(Icons.Filled.MonitorHeart, "Health Metrics", Screen.HealthMetrics.route),
        MenuEntry(Icons.Filled.Autorenew, "Prescription Refills", Screen.RefillRequests.route),
        MenuEntry(Icons.Filled.EmojiEvents, "Loyalty Points", Screen.Loyalty.route),
        MenuEntry(Icons.Filled.HourglassTop, "My Waitlist", Screen.Waitlist.route),
        MenuEntry(Icons.Filled.SelfImprovement, "Mood Journal", Screen.MoodJournal.route),
        MenuEntry(Icons.Filled.LocationOn, "Addresses", Screen.Addresses.route),
        MenuEntry(Icons.Filled.Payment, "Payment Methods", Screen.PaymentMethods.route),
        MenuEntry(Icons.Filled.FamilyRestroom, "Family Members / Dependents", Screen.Dependents.route),
        MenuEntry(Icons.Filled.Medication, "Medication Reminders", Screen.Reminders.route),
        MenuEntry(Icons.Filled.Notifications, "Notification Settings", Screen.Notifications.route),
        MenuEntry(Icons.Filled.Settings, "Settings & Support", Screen.Settings.route),
        MenuEntry(Icons.AutoMirrored.Filled.HelpOutline, "Help & Support / FAQ", Screen.HelpFaq.route),
        MenuEntry(Icons.Filled.PrivacyTip, "Privacy Policy / Terms", Screen.PrivacyTerms.route)
    )

    if (vm.isLoading || profile == null) {
        LoadingIndicator()
        return
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Box(Modifier.fillMaxWidth().height(180.dp)) {
            AsyncImage(
                model = PROFILE_BANNER_URL, contentDescription = null, contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(TealDark.copy(alpha = 0.55f), TealDark.copy(alpha = 0.15f)))
                )
            )
            Box(Modifier.align(Alignment.BottomStart).padding(start = 20.dp).offset(y = 36.dp)) {
                Box(
                    Modifier.size(88.dp).clip(CircleShape).background(Color.White).padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (profile.avatarUrl.isBlank()) {
                        Box(Modifier.fillMaxSize().clip(CircleShape).background(Color(0xFFDFF4F1)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(40.dp))
                        }
                    } else {
                        AsyncImage(model = profile.avatarUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(CircleShape))
                    }
                }
            }
        }

        Column(Modifier.padding(20.dp)) {
            Spacer(Modifier.height(44.dp))
            Text(profile.fullName.ifBlank { "Your Name" }, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(profile.phone.ifBlank { "Add a phone number" }, color = TextSecondary)
            Text(profile.email, color = TextSecondary)

            Spacer(Modifier.height(20.dp))
            menu.forEach { entry ->
                Row(
                    Modifier.fillMaxWidth().clickable(enabled = entry.route != null) { entry.route?.let { navController.navigate(it) } }.padding(vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(entry.icon, contentDescription = null, tint = TealPrimary)
                    Spacer(Modifier.width(14.dp))
                    Text(entry.label, modifier = Modifier.weight(1f))
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextSecondary)
                }
                HorizontalDivider()
            }

            Spacer(Modifier.height(16.dp))
            Surface(
                shape = RoundedCornerShape(14.dp), color = Color(0xFFFFF1F1),
                modifier = Modifier.fillMaxWidth().clickable { vm.signOut(); onSignedOut() }
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color(0xFFE5484D))
                    Spacer(Modifier.width(10.dp))
                    Text("Logout", color = Color(0xFFE5484D), fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(70.dp))
        }
    }
}
