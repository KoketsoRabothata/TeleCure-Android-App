package com.telecure.app.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.telecure.app.ui.theme.TealPrimary

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EmergencySosDialog(emergencyContactPhone: String, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val locationPermission = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    var statusMessage by remember { mutableStateOf<String?>(null) }

    fun shareLocation() {
        if (!locationPermission.status.isGranted) {
            locationPermission.launchPermissionRequest()
            return
        }
        val client = LocationServices.getFusedLocationProviderClient(context)
        try {
            client.lastLocation.addOnSuccessListener { location ->
                if (location == null) {
                    statusMessage = "Couldn't get your location — make sure location services are on."
                    return@addOnSuccessListener
                }
                val mapsLink = "https://maps.google.com/?q=${location.latitude},${location.longitude}"
                val message = "I need help. My current location: $mapsLink"
                if (emergencyContactPhone.isNotBlank()) {
                    val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$emergencyContactPhone")).apply {
                        putExtra("sms_body", message)
                    }
                    context.startActivity(smsIntent)
                } else {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, message)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share your location"))
                }
            }
        } catch (e: SecurityException) {
            statusMessage = "Location permission is needed to share your location."
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFE5484D)) },
        title = { Text("Emergency") },
        text = {
            Column {
                Text("If this is a medical emergency, call for help immediately.")
                statusMessage?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = Color(0xFFE5484D), style = MaterialTheme.typography.bodySmall)
                }
                if (emergencyContactPhone.isBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Tip: add an emergency contact number in Profile so location shares go straight to them.",
                        style = MaterialTheme.typography.bodySmall, color = Color.Gray
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:10177"))) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5484D))
            ) {
                Icon(Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Call 10177")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { shareLocation() }) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp), tint = TealPrimary)
                Spacer(Modifier.width(6.dp))
                Text("Share Location")
            }
        }
    )
}
