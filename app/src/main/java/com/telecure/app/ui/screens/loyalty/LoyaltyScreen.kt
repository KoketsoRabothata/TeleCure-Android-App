package com.telecure.app.ui.screens.loyalty

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.telecure.app.data.model.LoyaltyEntry
import com.telecure.app.ui.components.EmptyState
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TealDark
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextPrimary
import com.telecure.app.ui.theme.TextSecondary
import com.telecure.app.ui.viewmodel.LoyaltyViewModel
import java.text.SimpleDateFormat
import java.util.Locale

private data class Tier(val name: String, val threshold: Int)
private val tiers = listOf(Tier("Bronze", 0), Tier("Silver", 100), Tier("Gold", 300), Tier("Platinum", 600))

@Composable
fun LoyaltyScreen(navController: NavController, vm: LoyaltyViewModel = viewModel()) {
    LaunchedEffect(Unit) { vm.load() }
    val currentTier = tiers.lastOrNull { vm.totalPoints >= it.threshold } ?: tiers.first()
    val nextTier = tiers.firstOrNull { it.threshold > vm.totalPoints }

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("Loyalty Points", navController)
        if (vm.isLoading) {
            LoadingIndicator()
        } else {
            Surface(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                Box(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(TealPrimary, TealDark))).padding(24.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("${vm.totalPoints} points", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("${currentTier.name} Tier", color = Color.White.copy(alpha = 0.9f))
                        if (nextTier != null) {
                            Spacer(Modifier.height(8.dp))
                            Text("${nextTier.threshold - vm.totalPoints} points to ${nextTier.name}", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            Text("Earn 20 points per completed consultation, 10 per pharmacy order.", color = TextSecondary, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(12.dp))
            if (vm.entries.isEmpty()) {
                EmptyState(Icons.Filled.Stars, "No points yet", "Complete a consultation or place a pharmacy order to start earning.")
            } else {
                LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(vm.entries) { entry -> LoyaltyRow(entry) }
                }
            }
        }
    }
}

@Composable
private fun LoyaltyRow(entry: LoyaltyEntry) {
    val dateStr = entry.earnedAt?.let { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it) } ?: ""
    Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFF7FBFA), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(entry.reason, fontWeight = FontWeight.Medium, color = TextPrimary)
                if (dateStr.isNotBlank()) Text(dateStr, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            }
            Text("+${entry.points}", color = TealPrimary, fontWeight = FontWeight.Bold)
        }
    }
}
