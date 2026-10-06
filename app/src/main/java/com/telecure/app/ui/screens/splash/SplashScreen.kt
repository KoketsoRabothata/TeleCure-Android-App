package com.telecure.app.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.navigation.Screen
import com.telecure.app.ui.theme.TealDark
import com.telecure.app.ui.theme.TealPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigate: (String) -> Unit) {
    val scale = remember { Animatable(0.7f) }
    LaunchedEffect(Unit) {
        scale.animateTo(1f, animationSpec = tween(600, easing = FastOutSlowInEasing))
        delay(1400)
        val loggedIn = FirebaseAuth.getInstance().currentUser != null
        onNavigate(if (loggedIn) Screen.Home.route else Screen.Onboarding.route)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(TealPrimary, Color.White))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.scale(scale.value)) {
            Surface(shape = androidx.compose.foundation.shape.CircleShape, color = Color.White, tonalElevation = 4.dp) {
                Icon(
                    Icons.Filled.LocalHospital,
                    contentDescription = "TeleCure",
                    tint = TealDark,
                    modifier = Modifier.padding(20.dp).size(56.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            Text("TeleCure", color = Color.White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("Trusted care, anytime, anywhere", color = Color.White.copy(alpha = 0.9f))
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = 40.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(28.dp), strokeWidth = 3.dp)
        }
    }
}
