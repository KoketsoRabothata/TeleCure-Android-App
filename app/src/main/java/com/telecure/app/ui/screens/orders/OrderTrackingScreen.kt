package com.telecure.app.ui.screens.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.telecure.app.data.model.OrderStatus
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextSecondary
import com.telecure.app.ui.viewmodel.OrderViewModel

private val statusSteps = listOf(
    OrderStatus.CONFIRMED to "Confirmed",
    OrderStatus.PREPARING to "Preparing",
    OrderStatus.OUT_FOR_DELIVERY to "Out for Delivery",
    OrderStatus.DELIVERED to "Delivered"
)

@Composable
fun OrderTrackingScreen(navController: NavController, orderId: String, vm: OrderViewModel = viewModel()) {
    LaunchedEffect(orderId) {
        vm.track(orderId)
        vm.startDeliverySimulationIfNeeded(orderId)
    }
    val order = vm.order

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("Track Order", navController)
        if (order == null) {
            LoadingIndicator()
        } else {
            val currentIndex = statusSteps.indexOfFirst { it.first.name == order.status }.coerceAtLeast(0)
            val driverPos = LatLng(order.driverLat, order.driverLng)
            val destPos = LatLng(order.destinationLat, order.destinationLng)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(driverPos, 13f)
            }

            // Map preview
            Box(Modifier.fillMaxWidth().height(240.dp)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(
                        state = MarkerState(position = driverPos),
                        title = "Delivery rider",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    )
                    Marker(
                        state = MarkerState(position = destPos),
                        title = "Delivery address",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                    )
                    Polyline(points = listOf(driverPos, destPos), color = TealPrimary, width = 6f)
                }
            }

            Column(Modifier.weight(1f).padding(20.dp)) {
                Text("Order #${order.id.take(8).uppercase()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("Delivering to ${order.deliveryAddress.line1}, ${order.deliveryAddress.city}", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.height(24.dp))
                statusSteps.forEachIndexed { index, (_, label) ->
                    val done = index <= currentIndex
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.dp)) {
                        Icon(
                            if (done) Icons.Filled.CheckCircle else Icons.Filled.Circle,
                            contentDescription = null,
                            tint = if (done) TealPrimary else Color.LightGray,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(label, fontWeight = if (index == currentIndex) FontWeight.Bold else FontWeight.Normal, color = if (done) Color.Black else TextSecondary)
                    }
                }

                Spacer(Modifier.height(24.dp))
                Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFF7FBFA), modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.SupportAgent, contentDescription = null, tint = TealPrimary)
                        Spacer(Modifier.width(10.dp))
                        Text("Need help with your order?", modifier = Modifier.weight(1f))
                        TextButton(onClick = { /* Opens support chat hook */ }) { Text("Chat Support") }
                    }
                }
            }
        }
    }
}