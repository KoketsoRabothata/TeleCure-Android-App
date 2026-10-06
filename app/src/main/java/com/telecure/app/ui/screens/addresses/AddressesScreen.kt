package com.telecure.app.ui.screens.addresses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.telecure.app.data.model.Address
import com.telecure.app.ui.components.EmptyState
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextPrimary
import com.telecure.app.ui.theme.TextSecondary
import com.telecure.app.ui.viewmodel.AddressesViewModel

@Composable
fun AddressesScreen(navController: NavController, vm: AddressesViewModel = viewModel()) {
    LaunchedEffect(Unit) { vm.load() }
    var showAddDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("Addresses", navController)
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.End) {
            Button(onClick = { showAddDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Add Address")
            }
        }
        Spacer(Modifier.height(12.dp))
        when {
            vm.isLoading -> LoadingIndicator()
            vm.addresses.isEmpty() -> EmptyState(Icons.Filled.LocationOn, "No addresses yet", "Add a delivery address to speed up pharmacy checkout.")
            else -> LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(vm.addresses) { addr -> AddressRow(addr, vm) }
            }
        }
    }

    if (showAddDialog) {
        AddAddressDialog(onDismiss = { showAddDialog = false }, onConfirm = { label, line1, city, postcode, isDefault ->
            vm.addAddress(label, line1, city, postcode, isDefault)
            showAddDialog = false
        })
    }
}

@Composable
private fun AddressRow(addr: Address, vm: AddressesViewModel) {
    Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFF7FBFA), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = TealPrimary)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(addr.label, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    if (addr.isDefault) {
                        Spacer(Modifier.width(8.dp))
                        AssistChip(onClick = {}, label = { Text("Default", style = MaterialTheme.typography.labelSmall) })
                    }
                }
                Text(listOf(addr.line1, addr.city, addr.postcode).filter { it.isNotBlank() }.joinToString(", "), color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            }
            if (!addr.isDefault) {
                TextButton(onClick = { vm.setDefault(addr.id) }) { Text("Set Default") }
            }
            IconButton(onClick = { vm.deleteAddress(addr.id) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Remove", tint = Color(0xFFE5484D))
            }
        }
    }
}

@Composable
private fun AddAddressDialog(onDismiss: () -> Unit, onConfirm: (String, String, String, String, Boolean) -> Unit) {
    var label by remember { mutableStateOf("Home") }
    var line1 by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var postcode by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Address") },
        text = {
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Home", "Work", "Other").forEach { l ->
                        FilterChip(selected = label == l, onClick = { label = l }, label = { Text(l) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealPrimary, selectedLabelColor = Color.White))
                    }
                }
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(line1, { line1 = it }, label = { Text("Street Address") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(city, { city = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(postcode, { postcode = it }, label = { Text("Postal Code") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isDefault, onCheckedChange = { isDefault = it }, colors = CheckboxDefaults.colors(checkedColor = TealPrimary))
                    Text("Set as default")
                }
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(label, line1, city, postcode, isDefault) }, enabled = line1.isNotBlank()) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
