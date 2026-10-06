package com.telecure.app.ui.screens.pharmacy

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.telecure.app.ui.components.EmptyState
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextPrimary
import com.telecure.app.ui.theme.TextSecondary
import com.telecure.app.ui.viewmodel.PharmacyViewModel
import com.telecure.app.ui.viewmodel.pharmacyViewModel

@Composable
fun PharmacyScreen(navController: NavController, vm: PharmacyViewModel = pharmacyViewModel()) {
    LaunchedEffect(Unit) { vm.load() }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(20.dp, 16.dp, 20.dp, 0.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Pharmacy", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            IconButton(onClick = { navController.navigate(Screen.PharmacistChat.route) }) {
                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat with Pharmacist", tint = TealPrimary)
            }
            BadgedBox(badge = { if (vm.cartCount > 0) Badge { Text("${vm.cartCount}") } }) {
                IconButton(onClick = { navController.navigate(Screen.Cart.route) }) {
                    Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart", tint = TealPrimary)
                }
            }
        }
        Column(Modifier.padding(horizontal = 20.dp)) {
            OutlinedTextField(
                value = vm.searchTerm, onValueChange = { vm.search(it) },
                placeholder = { Text("Search medicines") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            Spacer(Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(vm.categories) { category ->
                    FilterChip(
                        selected = vm.selectedCategory == category,
                        onClick = { vm.filterByCategory(category) },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealPrimary, selectedLabelColor = Color.White)
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        when {
            vm.isLoading -> LoadingIndicator()
            vm.medicines.isEmpty() -> EmptyState(Icons.Filled.Medication, "No medicines found", "Try a different search or category.")
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vm.medicines) { med ->
                    Surface(
                        shape = RoundedCornerShape(16.dp), color = Color(0xFFF7FBFA),
                        modifier = Modifier.clickable { navController.navigate(Screen.ProductDetail.build(med.id)) }
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            AsyncImage(
                                model = med.imageUrl, contentDescription = med.name, contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth().height(90.dp).clip(RoundedCornerShape(10.dp)).background(Color.LightGray)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(med.name, fontWeight = FontWeight.SemiBold, maxLines = 1, color = TextPrimary)
                            Text(med.strength, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(6.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("R${med.price}", fontWeight = FontWeight.Bold, color = TextPrimary)
                                IconButton(onClick = { vm.addToCart(med, 1) }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Filled.ShoppingCart, contentDescription = "Add", tint = TealPrimary, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
