package com.telecure.app.ui.screens.auth

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.telecure.app.R
import com.telecure.app.ui.theme.TealDark
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.viewmodel.AuthViewModel

// Swap for your own hosted asset any time — this is just a placeholder stock photo.
private const val LOGIN_BACKGROUND_URL = "https://images.unsplash.com/photo-1631217868264-e5b90bb7e133?w=1000&q=80"

@Suppress("DEPRECATION")
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateSignUp: () -> Unit,
    vm: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    val context = LocalContext.current

    val googleSignInClient = remember {
        val webClientId = context.getString(R.string.google_web_client_id)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }
    val googleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).getResult(ApiException::class.java)
                account.idToken?.let { vm.signInWithGoogle(it) }
            } catch (e: ApiException) {
                // Cancelled or failed — the person can just try again.
            }
        }
    }

    LaunchedEffect(vm.success) { if (vm.success) onLoginSuccess() }

    Box(Modifier.fillMaxSize()) {
        AsyncImage(
            model = LOGIN_BACKGROUND_URL, contentDescription = null, contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(260.dp)
        )
        Box(
            Modifier.fillMaxWidth().height(260.dp).background(
                Brush.verticalGradient(listOf(TealDark.copy(alpha = 0.55f), Color.White), startY = 0f, endY = 700f)
            )
        )

        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(70.dp))
            Surface(shape = androidx.compose.foundation.shape.CircleShape, color = Color.White, tonalElevation = 4.dp) {
                Icon(Icons.Filled.LocalHospital, contentDescription = null, tint = TealPrimary, modifier = Modifier.padding(16.dp).size(36.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text("Welcome back", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Sign in to continue to TeleCure", color = Color.White.copy(alpha = 0.9f))
            Spacer(Modifier.height(24.dp))

            Surface(shape = RoundedCornerShape(24.dp), color = Color.White, shadowElevation = 6.dp, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(24.dp)) {
                    OutlinedTextField(
                        value = email, onValueChange = { email = it },
                        label = { Text("Email or Phone") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password, onValueChange = { password = it },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it }, colors = CheckboxDefaults.colors(checkedColor = TealPrimary))
                        Text("Remember me", modifier = Modifier.weight(1f))
                        TextButton(onClick = { vm.sendPasswordReset(email) }) { Text("Forgot Password?") }
                    }

                    vm.errorMessage?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                    }

                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { vm.signIn(email, password) },
                        enabled = !vm.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        if (vm.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        else Text("Sign In")
                    }

                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        HorizontalDivider(modifier = Modifier.weight(1f))
                        Text("  or  ", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        HorizontalDivider(modifier = Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { googleLauncher.launch(googleSignInClient.signInIntent) },
                        enabled = !vm.isLoading,
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Text("G", fontWeight = FontWeight.Bold, color = Color(0xFF4285F4))
                        Spacer(Modifier.width(10.dp))
                        Text("Continue with Google")
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Row {
                Text("Don't have an account? ")
                Text(
                    "Create Account", color = TealPrimary, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClick = onNavigateSignUp)
                )
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}