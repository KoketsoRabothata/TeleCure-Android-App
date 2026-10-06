package com.telecure.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.telecure.app.navigation.Screen
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextSecondary

data class BottomTab(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

val bottomTabs = listOf(
    BottomTab(Screen.Home.route, "Home", Icons.Filled.Home),
    BottomTab(Screen.DoctorList.route, "Doctors", Icons.Filled.LocalHospital),
    BottomTab(Screen.Pharmacy.route, "Pharmacy", Icons.Filled.Medication),
    BottomTab(Screen.Prescriptions.route, "Records", Icons.Filled.Folder),
    BottomTab(Screen.Profile.route, "Profile", Icons.Filled.Person)
)

@Composable
fun TeleCureBottomBar(navController: NavController, currentRoute: String?) {
    NavigationBar {
        bottomTabs.forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab.route,
                onClick = {
                    if (currentRoute != tab.route) {
                        navController.navigate(tab.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = TealPrimary, indicatorColor = Color(0xFFDFF4F1))
            )
        }
    }
}

@Composable
fun EmptyState(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Composable
fun NoInternetState(onRetry: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Filled.WifiOff, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(12.dp))
        Text("No internet connection", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text("Check your connection and try again.", color = TextSecondary)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) { Text("Retry") }
    }
}

@Composable
fun SuccessState(title: String, subtitle: String, buttonText: String, onButtonClick: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = com.telecure.app.ui.theme.SuccessGreen, modifier = Modifier.size(72.dp))
        Spacer(Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(subtitle, color = TextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onButtonClick, colors = ButtonDefaults.buttonColors(containerColor = TealPrimary), modifier = Modifier.fillMaxWidth()) {
            Text(buttonText)
        }
    }
}

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = TealPrimary)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeleCureTopBar(title: String, navController: NavController, showBack: Boolean = true) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.SemiBold) },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}
