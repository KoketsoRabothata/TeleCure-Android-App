package com.telecure.app.ui.screens.records

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.telecure.app.navigation.Screen
import com.telecure.app.ui.components.EmptyState
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextPrimary
import com.telecure.app.ui.theme.TextSecondary
import com.telecure.app.ui.viewmodel.RecordsViewModel

private val tabTypes = listOf("Prescription", "e-MC", "Lab Report", "Referral")

@Composable
fun PrescriptionsScreen(navController: NavController, vm: RecordsViewModel = viewModel()) {
    var tab by remember { mutableStateOf(0) }
    LaunchedEffect(tab) { vm.loadPrescriptions(tabTypes[tab]) }

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("Prescriptions & e-MCs", navController, showBack = false)
        ScrollableTabRow(selectedTabIndex = tab, contentColor = TealPrimary, edgePadding = 12.dp) {
            tabTypes.forEachIndexed { i, label -> Tab(selected = tab == i, onClick = { tab = i }, text = { Text(label) }) }
        }
        when {
            vm.isLoading -> LoadingIndicator()
            vm.prescriptions.isEmpty() -> EmptyState(Icons.Filled.Description, "No documents yet", "Your ${tabTypes[tab].lowercase()}s will appear here after a consultation.")
            else -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(vm.prescriptions) { rx ->
                    Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFF7FBFA), modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(14.dp)) {
                            Row {
                                Column(Modifier.weight(1f)) {
                                    Text(rx.doctorName, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                    Text(rx.diagnosis.ifBlank { rx.type }, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = { /* Downloads pdfUrl via storage link */ }) { Text("Download PDF") }
                                OutlinedButton(onClick = { /* Shares pdfUrl */ }) { Text("Share") }
                                if (rx.medicines.isNotEmpty()) {
                                    Button(onClick = { navController.navigate(Screen.Pharmacy.route) }, colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) {
                                        Text("Order Medicines")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
