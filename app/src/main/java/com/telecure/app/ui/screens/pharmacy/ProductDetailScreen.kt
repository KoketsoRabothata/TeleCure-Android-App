package com.telecure.app.ui.screens.pharmacy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.telecure.app.navigation.Screen
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextSecondary
import com.telecure.app.ui.viewmodel.PharmacyViewModel
import com.telecure.app.ui.viewmodel.pharmacyViewModel

@Composable
fun ProductDetailScreen(navController: NavController, medicineId: String, vm: PharmacyViewModel = pharmacyViewModel()) {
    LaunchedEffect(medicineId) { vm.loadMedicine(medicineId) }
    val med = vm.selectedMedicine
    var qty by remember { mutableStateOf(1) }
    var added by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("Product Details", navController)
        if (med == null) {
            LoadingIndicator()
        } else {
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
                AsyncImage(
                    model = med.imageUrl, contentDescription = med.name, contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(16.dp)).background(Color.LightGray)
                )
                Spacer(Modifier.height(16.dp))
                Text(med.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("${med.strength} • ${med.manufacturer}", color = TextSecondary)
                Spacer(Modifier.height(8.dp))
                Text("R${med.price}", style = MaterialTheme.typography.titleLarge, color = TealPrimary, fontWeight = FontWeight.Bold)
                if (med.prescriptionRequired) {
                    Spacer(Modifier.height(6.dp))
                    AssistChip(onClick = {}, label = { Text("Prescription required") })
                }
                Spacer(Modifier.height(16.dp))
                Text("Description", fontWeight = FontWeight.SemiBold)
                Text(med.description.ifBlank { "No description available." }, color = TextSecondary, modifier = Modifier.padding(top = 4.dp))
                Spacer(Modifier.height(12.dp))
                Text("Dosage", fontWeight = FontWeight.SemiBold)
                Text(med.dosage.ifBlank { "As directed by physician." }, color = TextSecondary, modifier = Modifier.padding(top = 4.dp))

                Spacer(Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Quantity", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    IconButton(onClick = { if (qty > 1) qty-- }) { Icon(Icons.Filled.Remove, contentDescription = "Decrease") }
                    Text("$qty", style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = { qty++ }) { Icon(Icons.Filled.Add, contentDescription = "Increase") }
                }
                Spacer(Modifier.height(90.dp))
            }

            Surface(shadowElevation = 8.dp, color = Color.White) {
                Column(Modifier.padding(16.dp)) {
                    if (added) Text("Added to cart!", color = TealPrimary, modifier = Modifier.padding(bottom = 8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(onClick = { vm.addToCart(med, qty); added = true }, modifier = Modifier.weight(1f).height(50.dp)) {
                            Text("Add to Cart")
                        }
                        Button(
                            onClick = { vm.addToCart(med, qty); navController.navigate(Screen.Cart.route) },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            modifier = Modifier.weight(1f).height(50.dp)
                        ) { Text("Buy Now") }
                    }
                }
            }
        }
    }
}
