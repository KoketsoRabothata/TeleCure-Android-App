package com.telecure.app.ui.screens.symptomchecker

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.telecure.app.navigation.Screen
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextPrimary
import com.telecure.app.ui.theme.TextSecondary

private enum class Urgency { EMERGENCY, SEE_TODAY, ROUTINE, SELF_CARE }

private data class SymptomCategory(val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String, val specialty: String)

private val categories = listOf(
    SymptomCategory(Icons.Filled.Thermostat, "Fever", "General Physician"),
    SymptomCategory(Icons.Filled.Air, "Cough / Cold", "General Physician"),
    SymptomCategory(Icons.Filled.Psychology, "Headache", "General Physician"),
    SymptomCategory(Icons.Filled.Sick, "Stomach Pain", "General Physician"),
    SymptomCategory(Icons.Filled.Face, "Skin Issue", "Dermatologist"),
    SymptomCategory(Icons.Filled.Healing, "Injury", "General Physician"),
    SymptomCategory(Icons.Filled.Favorite, "Chest Pain", "Cardiologist"),
    SymptomCategory(Icons.Filled.SelfImprovement, "Mental Health", "Psychiatrist"),
    SymptomCategory(Icons.AutoMirrored.Filled.HelpOutline, "Something Else", "General Physician")
)

/**
 * This is a simple, static, rule-based guide — not a diagnosis. It always errs toward
 * recommending professional care rather than reassuring the person to stay home.
 */
@Composable
fun SymptomCheckerScreen(navController: NavController) {
    var step by remember { mutableStateOf(0) } // 0 = pick category, 1 = questions, 2 = result
    var selectedCategory by remember { mutableStateOf<SymptomCategory?>(null) }
    var answers by remember { mutableStateOf(listOf<Boolean>()) }
    val context = LocalContext.current

    fun reset() { step = 0; selectedCategory = null; answers = emptyList() }

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("Symptom Checker", navController)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp)) content@ {
            when (step) {
                0 -> {
                    Text("What's your main symptom today?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text("This is a general guide, not a diagnosis.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(16.dp))
                    categories.forEach { category ->
                        Surface(
                            shape = RoundedCornerShape(14.dp), color = Color(0xFFF7FBFA),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp).clickable {
                                selectedCategory = category; answers = emptyList(); step = 1
                            }
                        ) {
                            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(category.icon, contentDescription = null, tint = TealPrimary)
                                Spacer(Modifier.width(12.dp))
                                Text(category.label, fontWeight = FontWeight.Medium, color = TextPrimary)
                            }
                        }
                    }
                }
                1 -> {
                    val category = selectedCategory ?: return@content
                    val questions = questionsFor(category.label)
                    val currentIndex = answers.size
                    if (currentIndex < questions.size) {
                        Text(category.label, color = TealPrimary, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(questions[currentIndex], style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(20.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(onClick = { answers = answers + true }, colors = ButtonDefaults.buttonColors(containerColor = TealPrimary), modifier = Modifier.weight(1f)) { Text("Yes") }
                            OutlinedButton(onClick = { answers = answers + false }, modifier = Modifier.weight(1f)) { Text("No") }
                        }
                    } else {
                        LaunchedEffect(answers) { step = 2 }
                    }
                }
                2 -> {
                    val category = selectedCategory ?: return@content
                    val urgency = urgencyFor(category.label, answers)
                    ResultView(category, urgency, navController, context, onStartOver = { reset() })
                }
            }
        }
    }
}

private fun questionsFor(category: String): List<String> = when (category) {
    "Fever" -> listOf("Is your temperature above 39°C (102°F)?", "Has it lasted more than 3 days?")
    "Cough / Cold" -> listOf("Do you have difficulty breathing or chest tightness?", "Has it lasted more than 2 weeks?")
    "Headache" -> listOf("Is this the worst headache of your life, or with vision changes or confusion?", "Has it lasted more than 3 days or is it getting worse?")
    "Stomach Pain" -> listOf("Is the pain severe, or are you vomiting blood, or is your abdomen rigid?", "Has it lasted more than 2 days, or do you also have a fever?")
    "Skin Issue" -> listOf("Is the rash spreading quickly along with fever or difficulty breathing?")
    "Injury" -> listOf("Do you suspect a broken bone, heavy bleeding, or a head injury with confusion?")
    "Chest Pain" -> emptyList() // always emergency-track, see urgencyFor
    "Mental Health" -> listOf("Are you having thoughts of harming yourself or someone else?", "Is this significantly affecting your daily life?")
    else -> emptyList()
}

private fun urgencyFor(category: String, answers: List<Boolean>): Urgency = when (category) {
    "Chest Pain" -> Urgency.EMERGENCY
    "Fever" -> if (answers.getOrElse(0) { false }) Urgency.SEE_TODAY else if (answers.getOrElse(1) { false }) Urgency.ROUTINE else Urgency.SELF_CARE
    "Cough / Cold" -> if (answers.getOrElse(0) { false }) Urgency.EMERGENCY else if (answers.getOrElse(1) { false }) Urgency.ROUTINE else Urgency.SELF_CARE
    "Headache" -> if (answers.getOrElse(0) { false }) Urgency.EMERGENCY else if (answers.getOrElse(1) { false }) Urgency.SEE_TODAY else Urgency.SELF_CARE
    "Stomach Pain" -> if (answers.getOrElse(0) { false }) Urgency.EMERGENCY else if (answers.getOrElse(1) { false }) Urgency.SEE_TODAY else Urgency.SELF_CARE
    "Skin Issue" -> if (answers.getOrElse(0) { false }) Urgency.EMERGENCY else Urgency.ROUTINE
    "Injury" -> if (answers.getOrElse(0) { false }) Urgency.EMERGENCY else Urgency.SEE_TODAY
    "Mental Health" -> if (answers.getOrElse(0) { false }) Urgency.EMERGENCY else if (answers.getOrElse(1) { false }) Urgency.SEE_TODAY else Urgency.ROUTINE
    else -> Urgency.ROUTINE
}

@Composable
private fun ResultView(category: SymptomCategory, urgency: Urgency, navController: NavController, context: android.content.Context, onStartOver: () -> Unit) {
    val (title, message, color) = when (urgency) {
        Urgency.EMERGENCY -> Triple(
            "Seek emergency care now",
            if (category.label == "Mental Health")
                "If you or someone you know is in immediate danger, please call 10177 or go to your nearest emergency room now.\n\nYou can also reach the SADAG Suicide Crisis Line on 0800 567 567, available 24/7, or SMS 31393."
            else
                "Based on your answers, this needs urgent attention. Please call emergency services or go to your nearest emergency room now.",
            Color(0xFFE5484D)
        )
        Urgency.SEE_TODAY -> Triple("See a doctor today", "It's best to have this looked at by a doctor as soon as possible today.", Color(0xFFF5A623))
        Urgency.ROUTINE -> Triple("Book a routine consultation", "This looks manageable, but a check-up with a ${category.specialty} is a good idea.", TealPrimary)
        Urgency.SELF_CARE -> Triple("Self-care should help", "Your symptoms sound mild. Rest, fluids, and monitoring are usually enough — but book a consultation if things don't improve.", Color(0xFF2E9E5B))
    }

    Icon(
        if (urgency == Urgency.EMERGENCY) Icons.Filled.Warning else Icons.Filled.CheckCircle,
        contentDescription = null, tint = color, modifier = Modifier.size(56.dp)
    )
    Spacer(Modifier.height(12.dp))
    Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = color)
    Spacer(Modifier.height(8.dp))
    Text(message, color = TextSecondary)
    Spacer(Modifier.height(24.dp))

    if (urgency == Urgency.EMERGENCY) {
        Button(
            onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:10177"))) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5484D)),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Icon(Icons.Filled.Call, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Call 10177 Now")
        }
        Spacer(Modifier.height(12.dp))
    }

    OutlinedButton(onClick = { navController.navigate(Screen.DoctorList.route) }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
        Text("Find a ${category.specialty}")
    }
    Spacer(Modifier.height(12.dp))
    TextButton(onClick = onStartOver) { Text("Start Over") }

    Spacer(Modifier.height(12.dp))
    Text(
        "This tool gives general guidance only and does not replace professional medical advice.",
        style = MaterialTheme.typography.bodySmall, color = Color.Gray
    )
}
