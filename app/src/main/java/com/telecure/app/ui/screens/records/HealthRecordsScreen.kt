package com.telecure.app.ui.screens.records

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.telecure.app.ui.components.EmptyState
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextPrimary
import com.telecure.app.ui.theme.TextSecondary
import com.telecure.app.ui.viewmodel.RecordsViewModel
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HealthRecordsScreen(navController: NavController, vm: RecordsViewModel = viewModel()) {
    LaunchedEffect(Unit) { vm.loadRecords() }
    val cameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)
    val filePicker = rememberLauncherForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) vm.uploadRecord("Uploaded Document", "Other", uri)
    }
    val cameraScanner = rememberLauncherForActivityResult(androidx.activity.result.contract.ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val stream = ByteArrayOutputStream()
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, stream)
            vm.uploadScannedRecord("Scanned Document", "Other", stream.toByteArray())
        }
    }

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("Health Records", navController)
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = { if (cameraPermission.status.isGranted) cameraScanner.launch(null) else cameraPermission.launchPermissionRequest() },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp), tint = TealPrimary)
                Spacer(Modifier.width(6.dp))
                Text("Scan Document")
            }
            Button(onClick = { filePicker.launch("*/*") }, colors = ButtonDefaults.buttonColors(containerColor = TealPrimary), modifier = Modifier.weight(1f)) {
                Icon(Icons.Filled.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Upload")
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Lock, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Encrypted & securely stored", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Spacer(Modifier.height(12.dp))
        when {
            vm.isLoading -> LoadingIndicator()
            vm.records.isEmpty() -> EmptyState(Icons.Filled.Folder, "No records yet", "Scan or upload lab results, imaging, or vaccination records.")
            else -> LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(vm.records.groupBy { it.category }.entries.toList()) { (category, docs) ->
                    Text(category, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 6.dp))
                    docs.forEach { doc ->
                        Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFF7FBFA), modifier = Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Filled.InsertDriveFile, contentDescription = null, tint = TealPrimary)
                                Spacer(Modifier.width(10.dp))
                                Text(doc.title, modifier = Modifier.weight(1f), color = TextPrimary)
                                IconButton(onClick = { /* Opens doc.fileUrl / share sheet */ }) { Icon(Icons.Filled.Share, contentDescription = "Share") }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}
