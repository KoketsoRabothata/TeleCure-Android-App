package com.telecure.app.ui.screens.booking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.telecure.app.navigation.Screen
import com.telecure.app.ui.components.CardDetailsForm
import com.telecure.app.ui.components.CardDetailsState
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.components.isValid
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextPrimary
import com.telecure.app.ui.viewmodel.BookingViewModel
import java.net.URLDecoder

@Composable
fun PaymentScreen(
    navController: NavController,
    doctorId: String, date: String, time: String, type: String, reasonEncoded: String, bookedForEncoded: String,
    vm: BookingViewModel = viewModel()
) {
    LaunchedEffect(doctorId) { vm.loadDoctor(doctorId) }
    val doctor = vm.doctor
    val reason = remember(reasonEncoded) { URLDecoder.decode(reasonEncoded, "UTF-8").let { if (it == "-") "" else it } }
    val bookedFor = remember(bookedForEncoded) { URLDecoder.decode(bookedForEncoded, "UTF-8").let { if (it == "-") "" else it } }
    var paymentMethod by remember { mutableStateOf("Card") }
    var promoCode by remember { mutableStateOf("") }

    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }
    val cardState = remember(cardNumber, cardExpiry, cardCvv, cardHolder) {
        CardDetailsState().apply { number = cardNumber; expiry = cardExpiry; cvv = cardCvv; holderName = cardHolder }
    }
    val canPay = paymentMethod != "Card" || cardState.isValid()

    LaunchedEffect(vm.confirmationNumber) {
        vm.confirmationNumber?.let { navController.navigate(Screen.BookingSuccess.build(it, doctor?.specialty.orEmpty())) { popUpTo(Screen.Home.route) } }
    }

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("Payment", navController)
        if (doctor != null) {
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
                Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFEFF7F6), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Text("Appointment Summary", fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Spacer(Modifier.height(6.dp))
                        Text("${doctor.name} — ${doctor.specialty}", color = TextPrimary)
                        Text("$date at $time • $type", color = TextPrimary)
                        if (bookedFor.isNotBlank()) Text("For: $bookedFor", color = TextPrimary)
                        if (reason.isNotBlank()) Text("Reason: $reason", color = TextPrimary)
                    }
                }

                Spacer(Modifier.height(20.dp))
                Text("Payment Method", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                listOf("Card", "FPX", "e-Wallet").forEach { method ->
                    Row(
                        Modifier.fillMaxWidth().selectable(selected = paymentMethod == method, onClick = { paymentMethod = method }).padding(vertical = 8.dp),
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

                Spacer(Modifier.height(16.dp))
                OutlinedTextField(value = promoCode, onValueChange = { promoCode = it }, label = { Text("Promo Code") }, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(20.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", fontWeight = FontWeight.SemiBold)
                    Text("R${doctor.consultationFee.toInt()}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }

                vm.errorMessage?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = MaterialTheme.colorScheme.error)
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
                            vm.confirmBooking(
                                doctorId = doctor.uid, doctorName = doctor.name, doctorPhoto = doctor.photoUrl,
                                specialty = doctor.specialty, fee = doctor.consultationFee,
                                date = date, time = time, type = type, reason = reason, bookedForName = bookedFor
                            )
                        },
                        enabled = !vm.isBooking && canPay,
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        if (vm.isBooking) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        else Text("Pay Securely")
                    }
                }
            }
        }
    }
}
