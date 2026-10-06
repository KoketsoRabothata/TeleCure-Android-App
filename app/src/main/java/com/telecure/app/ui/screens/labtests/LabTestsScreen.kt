package com.telecure.app.ui.screens.labtests

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.telecure.app.data.model.LabTest
import com.telecure.app.ui.components.EmptyState
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextPrimary
import com.telecure.app.ui.theme.TextSecondary
import com.telecure.app.ui.viewmodel.LabTestViewModel

private fun statusLabel(status: String) = status.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
private fun statusColor(status: String) = when (status) {
    "RESULTS_READY" -> Color(0xFF2E9E5B)
    "PROCESSING" -> TealPrimary
    "SAMPLE_COLLECTED" -> Color(0xFFF5A623)
    else -> Color.Gray
}

@Composable
fun LabTestsScreen(navController: NavController, vm: LabTestViewModel = viewModel()) {
    LaunchedEffect(Unit) { vm.load() }
    var tab by remember { mutableStateOf(0) }

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("Lab Tests & Results", navController)
        TabRow(selectedTabIndex = tab, contentColor = TealPrimary) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Book a Test") })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("My Tests") })
        }
        when {
            vm.isLoading -> LoadingIndicator()
            tab == 0 -> LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(vm.availableTests) { (name, price) ->
                    Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFF7FBFA), modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Science, contentDescription = null, tint = TealPrimary)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(name, fontWeight = FontWeight.Medium, color = TextPrimary)
                                Text("R${price.toInt()}", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                            }
                            Button(
                                onClick = { vm.bookTest(name, price) },
                                enabled = !vm.isBooking,
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) { Text("Book") }
                        }
                    }
                }
            }
            vm.labTests.isEmpty() -> EmptyState(Icons.Filled.Science, "No tests booked", "Book a common test from the 'Book a Test' tab.")
            else -> LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(vm.labTests) { test -> LabTestRow(test) }
            }
        }
    }
}

@Composable
private fun LabTestRow(test: LabTest) {
    Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFF7FBFA), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(test.testName, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                AssistChip(
                    onClick = {}, label = { Text(statusLabel(test.status)) },
                    colors = AssistChipDefaults.assistChipColors(labelColor = statusColor(test.status))
                )
            }
            if (test.resultSummary.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(test.resultSummary, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
            }
            if (test.requestedByDoctorName.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text("Requested by ${test.requestedByDoctorName}", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
