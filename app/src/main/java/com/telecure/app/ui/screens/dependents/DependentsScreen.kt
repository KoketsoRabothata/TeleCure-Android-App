package com.telecure.app.ui.screens.dependents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.telecure.app.ui.components.EmptyState
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextPrimary
import com.telecure.app.ui.theme.TextSecondary
import com.telecure.app.ui.viewmodel.DependentsViewModel

private val relationships = listOf("Spouse", "Child", "Parent", "Sibling", "Other")

@Composable
fun DependentsScreen(navController: NavController, vm: DependentsViewModel = viewModel()) {
    LaunchedEffect(Unit) { vm.load() }
    var showAddDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("Family Members", navController)
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.End) {
            Button(onClick = { showAddDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Add Family Member")
            }
        }
        Spacer(Modifier.height(12.dp))
        when {
            vm.isLoading -> LoadingIndicator()
            vm.dependents.isEmpty() -> EmptyState(
                Icons.Filled.FamilyRestroom, "No family members yet",
                "Add a spouse, child, or parent to book appointments and order medicines for them."
            )
            else -> LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(vm.dependents) { dep ->
                    Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFF7FBFA), modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = TealPrimary)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(dep.name, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text(
                                    listOf(dep.relationship, dep.dateOfBirth).filter { it.isNotBlank() }.joinToString(" • "),
                                    color = TextSecondary, style = MaterialTheme.typography.bodySmall
                                )
                            }
                            IconButton(onClick = { vm.deleteDependent(dep.id) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Remove", tint = Color(0xFFE5484D))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddDependentDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, relationship, dob ->
                vm.addDependent(name, relationship, dob)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddDependentDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf(relationships.first()) }
    var dob by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Family Member") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                Text("Relationship", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    relationships.take(3).forEach { r ->
                        FilterChip(selected = relationship == r, onClick = { relationship = r }, label = { Text(r) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealPrimary, selectedLabelColor = Color.White))
                    }
                }
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    relationships.drop(3).forEach { r ->
                        FilterChip(selected = relationship == r, onClick = { relationship = r }, label = { Text(r) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealPrimary, selectedLabelColor = Color.White))
                    }
                }
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = dob, onValueChange = { dob = it }, label = { Text("Date of Birth (optional)") }, placeholder = { Text("YYYY-MM-DD") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name, relationship, dob) }, enabled = name.isNotBlank()) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
