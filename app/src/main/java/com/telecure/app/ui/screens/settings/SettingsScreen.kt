package com.telecure.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.telecure.app.ui.theme.TealDark
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextSecondary
import com.telecure.app.ui.viewmodel.AuthViewModel
import com.telecure.app.ui.viewmodel.ProfileViewModel

// Swap for your own hosted banner asset any time — this is just a placeholder stock photo.
private const val SETTINGS_BANNER_URL = "https://images.unsplash.com/photo-1580281657702-257584239a55?w=1000&q=80"

@Composable
fun SettingsScreen(navController: NavController, vm: ProfileViewModel = viewModel(), authVm: AuthViewModel = viewModel()) {
    LaunchedEffect(Unit) { vm.load() }
    var email by remember { mutableStateOf("") }
    val context = androidx.compose.ui.platform.LocalContext.current

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Box(Modifier.fillMaxWidth().height(120.dp)) {
            AsyncImage(model = SETTINGS_BANNER_URL, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(TealDark.copy(alpha = 0.65f), TealDark.copy(alpha = 0.35f)))))
            Row(Modifier.fillMaxWidth().padding(top = 44.dp, start = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("Settings & Support", color = Color.White, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleLarge)
            }
        }
        Column(Modifier.padding(horizontal = 20.dp).padding(top = 12.dp)) {
            SettingsRow(Icons.Filled.Lock, "Change Password") {
                OutlinedTextField(
                    value = email, onValueChange = { email = it },
                    label = { Text("Account email") }, modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = { authVm.sendPasswordReset(email) }, colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) {
                    Text("Send reset link")
                }
            }
            HorizontalDivider()
            SettingsRow(Icons.Filled.PhoneAndroid, "Device Management") {
                Text("Trusted devices (7-day feature). Sign-outs on other devices apply after 7 days of inactivity.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            }
            HorizontalDivider()
            SettingsRow(Icons.Filled.ContactPhone, "Emergency Contact") {
                var contactPhone by remember(vm.profile?.emergencyContactPhone) { mutableStateOf(vm.profile?.emergencyContactPhone.orEmpty()) }
                Text("Used when you share your location from the Emergency button on Home.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = contactPhone, onValueChange = { contactPhone = it },
                    label = { Text("Phone number") }, placeholder = { Text("e.g. 0821234567") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = { vm.updateEmergencyContact(contactPhone) }, colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) {
                    Text("Save")
                }
            }
            HorizontalDivider()
            SettingsRow(Icons.Filled.Language, "Language") {
                Text("Sets your preferred language for communication and doctor matching.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("English", "Afrikaans", "isiZulu").forEach { lang ->
                        FilterChip(
                            selected = vm.profile?.language == lang,
                            onClick = { vm.updateLanguage(lang) },
                            label = { Text(lang) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealPrimary, selectedLabelColor = Color.White)
                        )
                    }
                }
            }
            HorizontalDivider()
            SettingsRow(Icons.Filled.SupportAgent, "Contact Support") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { navController.navigate(com.telecure.app.navigation.Screen.SupportChat.route) }) { Text("Chat") }
                    OutlinedButton(onClick = {
                        val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO, android.net.Uri.parse("mailto:support@telecure.app"))
                        context.startActivity(intent)
                    }) { Text("Email") }
                    OutlinedButton(onClick = {
                        context.startActivity(android.content.Intent(android.content.Intent.ACTION_DIAL, android.net.Uri.parse("tel:0800000000")))
                    }) { Text("Call") }
                }
            }
            HorizontalDivider()
            SettingsRow(Icons.Filled.StarRate, "Rate the App") {
                Row { repeat(5) { Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFF5A623)) } }
            }
            HorizontalDivider()
            SettingsRow(Icons.Filled.Info, "About TeleCure") {
                Text("TeleCure v1.0.0 — Trusted care, anytime, anywhere.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun SettingsRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, content: @Composable ColumnScope.() -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(
            Modifier.fillMaxWidth().clickable(onClick = { expanded = !expanded }).padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = TealPrimary)
            Spacer(Modifier.width(14.dp))
            Text(title, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            Icon(if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null, tint = TextSecondary)
        }
        if (expanded) {
            Column(Modifier.padding(start = 38.dp, bottom = 8.dp)) { content() }
        }
    }
}
