package com.telecure.app.ui.screens.booking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.telecure.app.navigation.Screen
import com.telecure.app.ui.components.SuccessState
import com.telecure.app.ui.components.prepTipsFor
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextSecondary

@Composable
fun BookingSuccessScreen(navController: NavController, confirmation: String, specialty: String = "") {
    Column(Modifier.fillMaxSize()) {
        SuccessState(
            title = "Payment Successful",
            subtitle = "Your appointment is confirmed.\nConfirmation #$confirmation",
            buttonText = "View My Appointments",
            onButtonClick = {
                navController.navigate(Screen.MyAppointments.route) {
                    popUpTo(Screen.Home.route)
                }
            }
        )
        if (specialty.isNotBlank() && specialty != "-") {
            Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFFF1F8F7), modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("How to prepare", fontWeight = FontWeight.SemiBold, color = TealPrimary)
                    Spacer(Modifier.height(8.dp))
                    prepTipsFor(specialty).forEach { tip ->
                        Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(bottom = 6.dp)) {
                            Icon(Icons.Filled.CheckCircleOutline, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(tip, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
