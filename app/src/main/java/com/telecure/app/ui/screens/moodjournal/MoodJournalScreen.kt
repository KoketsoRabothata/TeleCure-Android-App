package com.telecure.app.ui.screens.moodjournal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.telecure.app.data.model.HealthMetricEntry
import com.telecure.app.ui.components.EmptyState
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextPrimary
import com.telecure.app.ui.theme.TextSecondary
import com.telecure.app.ui.viewmodel.HealthMetricsViewModel
import java.text.SimpleDateFormat
import java.util.Locale

private val moods = listOf("😄 Great", "🙂 Good", "😐 Okay", "😔 Low", "😞 Struggling")

@Composable
fun MoodJournalScreen(navController: NavController, vm: HealthMetricsViewModel = viewModel()) {
    LaunchedEffect(Unit) { vm.load("MOOD") }
    var selectedMood by remember { mutableStateOf("") }
    var journalText by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("Mood Journal", navController)
        Column(Modifier.padding(20.dp)) {
            Text("How are you feeling today?", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                moods.forEach { m ->
                    val selected = selectedMood == m
                    Surface(
                        shape = CircleShape,
                        color = if (selected) TealPrimary else Color(0xFFF7FBFA),
                        modifier = Modifier.clickable { selectedMood = m }
                    ) {
                        Text(m.substringBefore(" "), fontSize = MaterialTheme.typography.headlineSmall.fontSize, modifier = Modifier.padding(10.dp))
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = journalText, onValueChange = { journalText = it },
                placeholder = { Text("Write a few words about your day (optional)") },
                modifier = Modifier.fillMaxWidth().height(90.dp)
            )
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = {
                    if (selectedMood.isNotBlank()) {
                        vm.addEntry(selectedMood, journalText)
                        selectedMood = ""; journalText = ""
                    }
                },
                enabled = selectedMood.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save Entry") }
        }
        Spacer(Modifier.height(8.dp))
        when {
            vm.isLoading -> LoadingIndicator()
            vm.entries.isEmpty() -> EmptyState(Icons.Filled.SelfImprovement, "No journal entries yet", "Your mood history will build up here over time.")
            else -> LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(vm.entries) { entry -> JournalRow(entry) }
                item { Spacer(Modifier.height(20.dp)) }
            }
        }
    }
}

@Composable
private fun JournalRow(entry: HealthMetricEntry) {
    val dateStr = entry.recordedAt?.let { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(it) } ?: ""
    Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFF7FBFA), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(entry.value, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                if (dateStr.isNotBlank()) {
                    Spacer(Modifier.weight(1f))
                    Text(dateStr, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                }
            }
            if (entry.note.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(entry.note, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
            }
        }
    }
}
