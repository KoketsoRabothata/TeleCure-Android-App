package com.telecure.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * State holder for card details, hoisted by the caller so the entered values survive
 * navigation/recomposition and can be validated before allowing payment to proceed.
 */
class CardDetailsState {
    var number: String = ""
    var expiry: String = ""
    var cvv: String = ""
    var holderName: String = ""
}

/** True once every card field looks well-formed enough to submit. */
fun CardDetailsState.isValid(): Boolean {
    val digitsOnly = number.filter { it.isDigit() }
    val expiryOk = Regex("^(0[1-9]|1[0-2])/\\d{2}$").matches(expiry)
    return digitsOnly.length in 15..16 && expiryOk && cvv.length in 3..4 && holderName.isNotBlank()
}

private fun formatCardNumber(input: String): String =
    input.filter { it.isDigit() }.take(16).chunked(4).joinToString(" ")

private fun formatExpiry(input: String): String {
    val digits = input.filter { it.isDigit() }.take(4)
    return if (digits.length > 2) "${digits.take(2)}/${digits.drop(2)}" else digits
}

@Composable
fun CardDetailsForm(
    number: String, onNumberChange: (String) -> Unit,
    expiry: String, onExpiryChange: (String) -> Unit,
    cvv: String, onCvvChange: (String) -> Unit,
    holderName: String, onHolderNameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = number,
            onValueChange = { onNumberChange(formatCardNumber(it)) },
            label = { Text("Card Number") },
            placeholder = { Text("1234 5678 9012 3456") },
            leadingIcon = { Icon(Icons.Filled.CreditCard, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = expiry,
                onValueChange = { onExpiryChange(formatExpiry(it)) },
                label = { Text("MM/YY") },
                placeholder = { Text("08/28") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = cvv,
                onValueChange = { onCvvChange(it.filter { c -> c.isDigit() }.take(4)) },
                label = { Text("CVV") },
                placeholder = { Text("123") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = holderName,
            onValueChange = onHolderNameChange,
            label = { Text("Cardholder Name") },
            placeholder = { Text("As shown on card") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
