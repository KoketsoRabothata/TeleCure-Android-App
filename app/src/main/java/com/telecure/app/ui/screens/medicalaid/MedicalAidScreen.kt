package com.telecure.app.ui.screens.medicalaid

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
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
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextSecondary
import com.telecure.app.ui.viewmodel.MedicalAidViewModel

private val providers = listOf("Discovery Health", "Momentum Health", "Bonitas", "GEMS", "Fedhealth", "Medihelp", "Other")

@Composable
fun MedicalAidScreen(navController: NavController, vm: MedicalAidViewModel = viewModel()) {
    LaunchedEffect(Unit) { vm.load() }
    val medicalAid = vm.medicalAid

    var provider by remember(medicalAid) { mutableStateOf(medicalAid?.provider.orEmpty().ifBlank { providers.first() }) }
    var memberNumber by remember(medicalAid) { mutableStateOf(medicalAid?.memberNumber.orEmpty()) }
    var planName by remember(medicalAid) { mutableStateOf(medicalAid?.planName.orEmpty()) }
    var mainMemberName by remember(medicalAid) { mutableStateOf(medicalAid?.mainMemberName.orEmpty()) }
    var cardUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> if (uri != null) cardUri = uri }

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("Medical Aid", navController)
        if (vm.isLoading) {
            LoadingIndicator()
        } else {
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp)) {
                Text(
                    "Add your medical aid so we can show estimated co-payments before you book or order.",
                    color = TextSecondary, style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))

                Text("Provider", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(providers) { p ->
                        FilterChip(selected = provider == p, onClick = { provider = p }, label = { Text(p) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealPrimary, selectedLabelColor = Color.White))
                    }
                }

                Spacer(Modifier.height(16.dp))
                OutlinedTextField(memberNumber, { memberNumber = it }, label = { Text("Member Number") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(planName, { planName = it }, label = { Text("Plan Name") }, placeholder = { Text("e.g. Classic Saver") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(mainMemberName, { mainMemberName = it }, label = { Text("Main Member Name") }, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(20.dp))
                Text("Membership Card", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(14.dp), color = Color(0xFFF7FBFA),
                    modifier = Modifier.fillMaxWidth().height(140.dp)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val displayUrl = cardUri?.toString() ?: medicalAid?.cardImageUrl
                        if (displayUrl.isNullOrBlank()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.CreditCard, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(32.dp))
                                Spacer(Modifier.height(6.dp))
                                TextButton(onClick = { photoPicker.launch("image/*") }) { Text("Upload card photo") }
                            }
                        } else {
                            AsyncImage(model = displayUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp)))
                        }
                    }
                }
                if (!medicalAid?.cardImageUrl.isNullOrBlank() || cardUri != null) {
                    TextButton(onClick = { photoPicker.launch("image/*") }) { Text("Replace photo") }
                }

                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { vm.save(provider, memberNumber, planName, mainMemberName, cardUri) },
                    enabled = !vm.isSaving,
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    if (vm.isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    else Text("Save Medical Aid Details")
                }
            }
        }
    }
}
