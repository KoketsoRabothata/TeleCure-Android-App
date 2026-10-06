package com.telecure.app.ui.screens.legal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TextSecondary

@Composable
fun PrivacyTermsScreen(navController: NavController) {
    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("Privacy Policy / Terms", navController)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp)) {
            Text(
                "This is placeholder policy text for demo purposes. Replace it with policy reviewed by a qualified legal professional before real-world use.",
                color = TextSecondary, style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(16.dp))

            LegalSection("Privacy Policy", listOf(
                "TeleCure collects the information you provide directly, such as your name, contact details, health records, and appointment history, to provide telemedicine and pharmacy services.",
                "Your data is stored securely and access is limited to your own account, except where you explicitly share records with a doctor.",
                "We do not sell your personal or health information to third parties.",
                "You can request deletion of your account and associated data at any time by contacting support."
            ))
            Spacer(Modifier.height(20.dp))
            LegalSection("Terms of Use", listOf(
                "TeleCure connects patients with healthcare providers for consultations and medicine ordering. It is not a substitute for emergency care — call 10177 for emergencies.",
                "You are responsible for providing accurate information when booking appointments or ordering medicines.",
                "Payments made through the app are processed for the services you select; refund requests should go through Contact Support.",
                "We may update these terms from time to time. Continued use of the app means you accept the current terms."
            ))
            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
private fun LegalSection(title: String, points: List<String>) {
    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
    points.forEach { point ->
        Text("•  $point", color = TextSecondary, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))
    }
}
