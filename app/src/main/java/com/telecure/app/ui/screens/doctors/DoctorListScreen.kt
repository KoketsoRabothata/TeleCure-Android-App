package com.telecure.app.ui.screens.doctors

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.telecure.app.ui.viewmodel.DoctorViewModel

@Composable
fun DoctorListScreen(navController: NavController, vm: DoctorViewModel = viewModel()) {
    LaunchedEffect(Unit) { vm.load() }

    Column(Modifier.fillMaxSize().padding(top = 8.dp)) {
        Column(Modifier.padding(horizontal = 20.dp)) {
            Text("Find a Doctor", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = vm.searchTerm, onValueChange = { vm.search(it) },
                placeholder = { Text("Search doctors or specialties") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(vm.specialties) { specialty ->
                    FilterChip(
                        selected = vm.selectedSpecialty == specialty,
                        onClick = { vm.filterBySpecialty(specialty) },
                        label = { Text(specialty) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealPrimary, selectedLabelColor = Color.White)
                    )
                }
            }
            if (vm.languages.size > 1) {
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(vm.languages) { language ->
                        FilterChip(
                            selected = vm.selectedLanguage == language,
                            onClick = { vm.filterByLanguage(language) },
                            label = { Text(if (language == "All") "Any language" else language) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealPrimary, selectedLabelColor = Color.White)
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        when {
            vm.isLoading -> LoadingIndicator()
            vm.doctors.isEmpty() -> EmptyState(Icons.Filled.SearchOff, "No doctors found", "Try a different specialty or search term.")
            else -> LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(vm.doctors) { doctor ->
                    Surface(
                        shape = RoundedCornerShape(16.dp), color = Color(0xFFF7FBFA),
                        modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Screen.DoctorDetail.build(doctor.uid)) }
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(model = doctor.photoUrl, contentDescription = doctor.name, modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.LightGray))
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(doctor.name, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text(doctor.specialty, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFF5A623), modifier = Modifier.size(16.dp))
                                    Text(" ${doctor.rating} (${doctor.reviewCount})  •  ${doctor.experienceYears} yrs", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                }
                                Text("Next: ${doctor.nextAvailableSlot.ifBlank { "See calendar" }}", style = MaterialTheme.typography.bodySmall, color = TealPrimary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("R${doctor.consultationFee.toInt()}", fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Spacer(Modifier.height(6.dp))
                                Button(
                                    onClick = { navController.navigate(Screen.BookAppointment.build(doctor.uid)) },
                                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) { Text("Book", style = MaterialTheme.typography.labelSmall) }
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(70.dp)) }
            }
        }
    }
}
