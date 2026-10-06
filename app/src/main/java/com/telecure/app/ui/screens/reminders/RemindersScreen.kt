package com.telecure.app.ui.screens.reminders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.telecure.app.data.model.MedicationReminder
import com.telecure.app.ui.components.EmptyState
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextPrimary
import com.telecure.app.ui.theme.TextSecondary
import com.telecure.app.ui.viewmodel.RemindersViewModel

@Composable
fun RemindersScreen(navController: NavController, vm: RemindersViewModel = viewModel()) {
    LaunchedEffect(Unit) { vm.load() }
    var showAddDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("Medication Reminders", navController)
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.End) {
            Button(onClick = { showAddDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Add Reminder")
            }
        }
        Spacer(Modifier.height(12.dp))
        when {
            vm.isLoading -> LoadingIndicator()
            vm.reminders.isEmpty() -> EmptyState(Icons.Filled.Medication, "No reminders yet", "Add a reminder so you never miss a dose.")
            else -> LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(vm.reminders) { reminder -> ReminderRow(reminder, vm) }
            }
        }
    }

    if (showAddDialog) {
        AddReminderDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, dosage, times ->
                vm.addReminder(name, dosage, times)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun ReminderRow(reminder: MedicationReminder, vm: RemindersViewModel) {
    Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFF7FBFA), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Medication, contentDescription = null, tint = TealPrimary)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(reminder.medicineName, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text(
                    listOfNotNull(reminder.dosage.ifBlank { null }, reminder.times.joinToString(", ")).joinToString(" • "),
                    color = TextSecondary, style = MaterialTheme.typography.bodySmall
                )
            }
            Switch(checked = reminder.active, onCheckedChange = { vm.toggleActive(reminder) }, colors = SwitchDefaults.colors(checkedThumbColor = TealPrimary))
            IconButton(onClick = { vm.deleteReminder(reminder) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color(0xFFE5484D))
            }
        }
    }
}

@Composable
private fun AddReminderDialog(onDismiss: () -> Unit, onConfirm: (String, String, List<String>) -> Unit) {
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var timesText by remember { mutableStateOf("08:00, 20:00") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Medication Reminder") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Medicine Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = dosage, onValueChange = { dosage = it }, label = { Text("Dosage (optional)") }, placeholder = { Text("e.g. 1 tablet") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = timesText, onValueChange = { timesText = it },
                    label = { Text("Times (24h, comma-separated)") }, placeholder = { Text("08:00, 20:00") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Format: HH:MM, separated by commas", style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val times = timesText.split(",").map { it.trim() }.filter { Regex("^([01]\\d|2[0-3]):[0-5]\\d$").matches(it) }
                    if (times.isNotEmpty()) onConfirm(name, dosage, times)
                },
                enabled = name.isNotBlank()
            ) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
