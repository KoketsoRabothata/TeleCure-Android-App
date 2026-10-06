package com.telecure.app.ui.screens.pharmacy

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.data.model.Address
import com.telecure.app.data.model.PharmacyOrder
import com.telecure.app.data.repository.LoyaltyRepository
import com.telecure.app.data.repository.PharmacyRepository
import com.telecure.app.navigation.Screen
import com.telecure.app.ui.components.CardDetailsForm
import com.telecure.app.ui.components.CardDetailsState
import com.telecure.app.ui.components.EmptyState
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.components.isValid
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.viewmodel.PharmacyViewModel
import com.telecure.app.ui.viewmodel.pharmacyViewModel
import kotlinx.coroutines.launch

@Composable
fun CartCheckoutScreen(navController: NavController, vm: PharmacyViewModel = pharmacyViewModel()) {
    val scope = rememberCoroutineScope()
    val repo = remember { PharmacyRepository() }
    val loyaltyRepo = remember { LoyaltyRepository() }
    var addressLine by remember { mutableStateOf("14 Rivonia Road") }
    var city by remember { mutableStateOf("Sandton, Johannesburg") }
    var deliveryOption by remember { mutableStateOf("Standard") }
    var paymentMethod by remember { mutableStateOf("Card") }
    var isPlacingOrder by remember { mutableStateOf(false) }

    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }
    val cardState = remember(cardNumber, cardExpiry, cardCvv, cardHolder) {
        CardDetailsState().apply { number = cardNumber; expiry = cardExpiry; cvv = cardCvv; holderName = cardHolder }
    }
    val canPlaceOrder = paymentMethod != "Card" || cardState.isValid()

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("Cart & Checkout", navController)
        if (vm.cart.isEmpty()) {
            EmptyState(Icons.Filled.RemoveShoppingCart, "Your cart is empty", "Add medicines from the Pharmacy to get started.")
        } else {
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
                Text("Items", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                vm.cart.forEach { item ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(item.name, fontWeight = FontWeight.Medium)
                            Text("R${item.price} each", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { vm.updateQuantity(item.medicineId, item.quantity - 1) }) { Icon(Icons.Filled.Remove, contentDescription = null) }
                        Text("${item.quantity}")
                        IconButton(onClick = { vm.updateQuantity(item.medicineId, item.quantity + 1) }) { Icon(Icons.Filled.Add, contentDescription = null) }
                        IconButton(onClick = { vm.removeFromCart(item.medicineId) }) { Icon(Icons.Filled.Delete, contentDescription = "Remove", tint = Color(0xFFE5484D)) }
                    }
                    HorizontalDivider()
                }
                if (vm.cart.any { it.prescriptionRequired }) {
                    Spacer(Modifier.height(12.dp))
                    Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFFFF6E5), modifier = Modifier.fillMaxWidth()) {
                        Text("One or more items require a prescription upload.", modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall)
                    }
                }

                Spacer(Modifier.height(20.dp))
                Text("Delivery Address", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(addressLine, { addressLine = it }, label = { Text("Address Line") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(city, { city = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(20.dp))
                Text("Delivery Options", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Standard", "Express").forEach { opt ->
                        FilterChip(selected = deliveryOption == opt, onClick = { deliveryOption = opt }, label = { Text(opt) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealPrimary, selectedLabelColor = Color.White))
                    }
                }

                Spacer(Modifier.height(20.dp))
                Text("Payment Method", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                listOf("Card", "FPX", "e-Wallet").forEach { method ->
                    Row(
                        Modifier.fillMaxWidth().selectable(selected = paymentMethod == method, onClick = { paymentMethod = method }).padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = paymentMethod == method, onClick = { paymentMethod = method }, colors = RadioButtonDefaults.colors(selectedColor = TealPrimary))
                        Text(method)
                    }
                }
                AnimatedVisibility(visible = paymentMethod == "Card") {
                    Column {
                        Spacer(Modifier.height(4.dp))
                        CardDetailsForm(
                            number = cardNumber, onNumberChange = { cardNumber = it },
                            expiry = cardExpiry, onExpiryChange = { cardExpiry = it },
                            cvv = cardCvv, onCvvChange = { cardCvv = it },
                            holderName = cardHolder, onHolderNameChange = { cardHolder = it }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", fontWeight = FontWeight.SemiBold)
                    Text("R${"%.2f".format(vm.cartTotal)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.height(90.dp))
            }

            Surface(shadowElevation = 8.dp, color = Color.White) {
                Column(Modifier.padding(16.dp)) {
                    if (paymentMethod == "Card" && !cardState.isValid() && (cardNumber.isNotBlank() || cardExpiry.isNotBlank() || cardCvv.isNotBlank() || cardHolder.isNotBlank())) {
                        Text("Please complete all card details correctly.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    Button(
                        onClick = {
                            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button
                            isPlacingOrder = true
                            scope.launch {
                                val order = PharmacyOrder(
                                    patientUid = uid,
                                    items = vm.cart,
                                    deliveryAddress = Address(line1 = addressLine, city = city, lat = -26.1076, lng = 28.0567),
                                    deliveryOption = deliveryOption,
                                    total = vm.cartTotal,
                                    driverLat = -26.0940, driverLng = 28.0480,
                                    destinationLat = -26.1076, destinationLng = 28.0567
                                )
                                val result = repo.placeOrder(order)
                                isPlacingOrder = false
                                result.onSuccess { id ->
                                    vm.clearCart()
                                    loyaltyRepo.awardPoints(uid, 10, "Pharmacy order")
                                    navController.navigate(Screen.OrderTracking.build(id)) { popUpTo(Screen.Pharmacy.route) }
                                }
                            }
                        },
                        enabled = !isPlacingOrder && canPlaceOrder,
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        if (isPlacingOrder) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        else Text("Place Order")
                    }
                }
            }
        }
    }
}
