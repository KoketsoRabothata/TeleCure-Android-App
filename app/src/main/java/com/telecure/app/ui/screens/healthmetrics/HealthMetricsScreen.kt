package com.telecure.app.ui.screens.healthmetrics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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

private val metricTabs = listOf("WEIGHT" to "Weight", "BLOOD_PRESSURE" to "Blood Pressure", "BLOOD_SUGAR" to "Blood Sugar", "MOOD" to "Mood")

@Composable
fun HealthMetricsScreen(navController: NavController, vm: HealthMetricsViewModel = viewModel()) {
    LaunchedEffect(Unit) { vm.load("WEIGHT") }
    var showAddDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("Health Metrics", navController)
        ScrollableTabRow(selectedTabIndex = metricTabs.indexOfFirst { it.first == vm.selectedType }, contentColor = TealPrimary, edgePadding = 12.dp) {
            metricTabs.forEach { (type, label) ->
                Tab(selected = vm.selectedType == type, onClick = { vm.load(type) }, text = { Text(label) })
            }
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp), horizontalArrangement = Arrangement.End) {
            Button(onClick = { showAddDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Log Entry")
            }
        }
        when {
            vm.isLoading -> LoadingIndicator()
            vm.entries.isEmpty() -> EmptyState(Icons.Filled.MonitorHeart, "No entries yet", "Log your first reading to start tracking trends.")
            else -> {
                if (vm.selectedType == "WEIGHT" || vm.selectedType == "BLOOD_SUGAR") {
                    TrendChart(vm.entries)
                }
                LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(vm.entries) { entry -> MetricRow(entry, vm) }
                }
            }
        }
    }

    if (showAddDialog) {
        AddMetricDialog(
            metricType = vm.selectedType,
            onDismiss = { showAddDialog = false },
            onConfirm = { value, note -> vm.addEntry(value, note); showAddDialog = false }
        )
    }
}

@Composable
private fun TrendChart(entries: List<HealthMetricEntry>) {
    val values = entries.mapNotNull { it.value.toFloatOrNull() }.reversed()
    if (values.size < 2) return
    val minVal = values.min()
    val maxVal = values.max()
    val range = (maxVal - minVal).takeIf { it > 0f } ?: 1f

    Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFF7FBFA), modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(140.dp)) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val stepX = size.width / (values.size - 1).coerceAtLeast(1)
            val points = values.mapIndexed { index, v ->
                val x = index * stepX
                val y = size.height - ((v - minVal) / range) * size.height
                Offset(x, y)
            }
            for (i in 0 until points.size - 1) {
                drawLine(color = TealPrimary, start = points[i], end = points[i + 1], strokeWidth = 5f)
            }
            points.forEach { drawCircle(color = TealPrimary, radius = 6f, center = it) }
        }
    }
    Spacer(Modifier.height(4.dp))
}

@Composable
private fun MetricRow(entry: HealthMetricEntry, vm: HealthMetricsViewModel) {
    val dateStr = entry.recordedAt?.let { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(it) } ?: ""
    Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFF7FBFA), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(entry.value, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                if (entry.note.isNotBlank()) Text(entry.note, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                if (dateStr.isNotBlank()) Text(dateStr, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = { vm.deleteEntry(entry.id) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color(0xFFE5484D))
            }
        }
    }
}

@Composable
private fun AddMetricDialog(metricType: String, onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var value by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val moodOptions = listOf("Great", "Good", "Okay", "Low", "Struggling")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log " + metricTabs.first { it.first == metricType }.second) },
        text = {
            Column {
                when (metricType) {
                    "MOOD" -> {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(moodOptions) { m ->
                                FilterChip(selected = value == m, onClick = { value = m }, label = { Text(m) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealPrimary, selectedLabelColor = Color.White))
                            }
                        }
                    }
                    "BLOOD_PRESSURE" -> OutlinedTextField(value, { value = it }, label = { Text("Systolic/Diastolic") }, placeholder = { Text("120/80") }, modifier = Modifier.fillMaxWidth())
                    "WEIGHT" -> OutlinedTextField(value, { value = it }, label = { Text("Weight (kg)") }, placeholder = { Text("72.5") }, modifier = Modifier.fillMaxWidth())
                    else -> OutlinedTextField(value, { value = it }, label = { Text("Blood Sugar (mmol/L)") }, placeholder = { Text("5.5") }, modifier = Modifier.fillMaxWidth())
                }
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(note, { note = it }, label = { Text("Note (optional)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(value, note) }, enabled = value.isNotBlank()) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
