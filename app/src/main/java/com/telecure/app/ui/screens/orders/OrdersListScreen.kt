package com.telecure.app.ui.screens.orders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.telecure.app.data.model.PharmacyOrder
import com.telecure.app.navigation.Screen
import com.telecure.app.ui.components.EmptyState
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextPrimary
import com.telecure.app.ui.theme.TextSecondary
import com.telecure.app.ui.viewmodel.OrdersListViewModel

private fun statusLabel(status: String) = status.replace("_", " ")
    .lowercase().replaceFirstChar { it.uppercase() }

@Composable
fun OrdersListScreen(navController: NavController, vm: OrdersListViewModel = viewModel()) {
    LaunchedEffect(Unit) { vm.load() }

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("My Orders", navController)
        when {
            vm.isLoading -> LoadingIndicator()
            vm.orders.isEmpty() -> EmptyState(Icons.Filled.Inventory2, "No orders yet", "Medicines you order from the Pharmacy will show up here.")
            else -> LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(vm.orders) { order -> OrderRow(order, navController) }
            }
        }
    }
}

@Composable
private fun OrderRow(order: PharmacyOrder, navController: NavController) {
    Surface(
        shape = RoundedCornerShape(14.dp), color = Color(0xFFF7FBFA),
        modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Screen.OrderTracking.build(order.id)) }
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Order #${order.id.take(8).uppercase()}", fontWeight = FontWeight.SemiBold, color = TextPrimary)
                AssistChip(onClick = {}, label = { Text(statusLabel(order.status)) })
            }
            Spacer(Modifier.height(4.dp))
            Text("${order.items.size} item${if (order.items.size == 1) "" else "s"} • R${"%.2f".format(order.total)}", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
            Text(order.deliveryAddress.line1.ifBlank { "" }, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
        }
    }
}
