package com.telecure.app.ui.screens.paymentmethods

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.telecure.app.data.model.PaymentMethod
import com.telecure.app.ui.components.EmptyState
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextPrimary
import com.telecure.app.ui.viewmodel.PaymentMethodsViewModel

private val methodTypes = listOf("Card", "FPX", "e-Wallet")

@Composable
fun PaymentMethodsScreen(navController: NavController, vm: PaymentMethodsViewModel = viewModel()) {
    LaunchedEffect(Unit) { vm.load() }
    var showAddDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("Payment Methods", navController)
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.End) {
            Button(onClick = { showAddDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Add Method")
            }
        }
        Spacer(Modifier.height(12.dp))
        when {
            vm.isLoading -> LoadingIndicator()
            vm.methods.isEmpty() -> EmptyState(Icons.Filled.CreditCard, "No payment methods saved", "Add a card, FPX, or e-Wallet as a shortcut for future payments.")
            else -> LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(vm.methods) { method -> MethodRow(method, vm) }
            }
        }
    }

    if (showAddDialog) {
        AddMethodDialog(onDismiss = { showAddDialog = false }, onConfirm = { type, label, isDefault ->
            vm.addMethod(type, label, isDefault)
            showAddDialog = false
        })
    }
}

@Composable
private fun MethodRow(method: PaymentMethod, vm: PaymentMethodsViewModel) {
    Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFF7FBFA), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.CreditCard, contentDescription = null, tint = TealPrimary)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(method.label, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    if (method.isDefault) {
                        Spacer(Modifier.width(8.dp))
                        AssistChip(onClick = {}, label = { Text("Default", style = MaterialTheme.typography.labelSmall) })
                    }
                }
                Text(method.type, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
            if (!method.isDefault) {
                TextButton(onClick = { vm.setDefault(method.id) }) { Text("Set Default") }
            }
            IconButton(onClick = { vm.deleteMethod(method.id) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Remove", tint = Color(0xFFE5484D))
            }
        }
    }
}

@Composable
private fun AddMethodDialog(onDismiss: () -> Unit, onConfirm: (String, String, Boolean) -> Unit) {
    var type by remember { mutableStateOf(methodTypes.first()) }
    var label by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Payment Method") },
        text = {
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    methodTypes.forEach { t ->
                        FilterChip(selected = type == t, onClick = { type = t }, label = { Text(t) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealPrimary, selectedLabelColor = Color.White))
                    }
                }
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    label, { label = it },
                    label = { Text(if (type == "Card") "Card label" else "Account label") },
                    placeholder = { Text(if (type == "Card") "e.g. Visa ending 4417" else "e.g. My $type account") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isDefault, onCheckedChange = { isDefault = it }, colors = CheckboxDefaults.colors(checkedColor = TealPrimary))
                    Text("Set as default")
                }
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(type, label, isDefault) }, enabled = label.isNotBlank()) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
