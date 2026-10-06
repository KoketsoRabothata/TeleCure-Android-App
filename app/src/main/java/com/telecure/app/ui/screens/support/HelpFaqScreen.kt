package com.telecure.app.ui.screens.support

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextSecondary

private data class Faq(val question: String, val answer: String)

private val faqs = listOf(
    Faq("How do I book a video consultation?", "Go to Doctors, pick a doctor, choose a date, time, and \"Video\" as the consultation type, then confirm and pay."),
    Faq("How do I join my video call?", "On the Home screen or My Appointments, tap \"Join\" on your upcoming appointment."),
    Faq("How do I track a pharmacy order?", "After checkout, you're taken straight to Order Tracking. You can also find past orders under Profile → My Orders."),
    Faq("How do I add a family member?", "Go to Profile → Family Members / Dependents, then tap \"Add Family Member\". You can then book appointments on their behalf."),
    Faq("How do medication reminders work?", "Add a reminder under Profile → Medication Reminders with the medicine name and times. You'll get a notification with a \"Mark as Taken\" button."),
    Faq("How do I cancel or reschedule an appointment?", "Go to My Appointments, find the appointment, and use the Cancel or Reschedule buttons."),
    Faq("Is my data secure?", "Health records, prescriptions, and personal details are stored securely and access is restricted to your account only."),
    Faq("Who do I contact for urgent help?", "For medical emergencies, use the red Emergency button on Home to call 10177 directly. For app issues, use Settings → Contact Support.")
)

@Composable
fun HelpFaqScreen(navController: NavController) {
    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("Help & Support / FAQ", navController)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            faqs.forEach { faq -> FaqRow(faq) }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun FaqRow(faq: Faq) {
    var expanded by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(
            Modifier.fillMaxWidth().clickable { expanded = !expanded }.padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(faq.question, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            Icon(if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null, tint = TealPrimary)
        }
        if (expanded) {
            Text(faq.answer, color = TextSecondary, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))
        }
        HorizontalDivider()
    }
}
