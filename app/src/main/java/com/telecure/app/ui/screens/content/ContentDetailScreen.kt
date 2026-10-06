package com.telecure.app.ui.screens.content

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextSecondary
import com.telecure.app.ui.viewmodel.HealthTipsViewModel

@Composable
fun ContentDetailScreen(navController: NavController, tipId: String, vm: HealthTipsViewModel = viewModel()) {
    LaunchedEffect(tipId) { vm.loadTip(tipId) }
    val tip = vm.selectedTip
    val context = LocalContext.current

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("Article", navController)
        if (tip == null) {
            LoadingIndicator()
        } else {
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp)) {
                if (tip.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = tip.imageUrl, contentDescription = tip.title, contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(Modifier.height(16.dp))
                }
                Text(tip.category, color = TealPrimary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text(tip.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    IconButton(onClick = { vm.toggleBookmark(tip.id) }) {
                        Icon(
                            if (vm.bookmarked.contains(tip.id)) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = "Bookmark", tint = TealPrimary
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))

                if (tip.videoUrl.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(14.dp), color = Color(0xFFF7FBFA),
                        modifier = Modifier.fillMaxWidth().clickable { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(tip.videoUrl))) }
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Icon(Icons.Filled.PlayCircle, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(28.dp))
                            Spacer(Modifier.width(10.dp))
                            Text("Watch related video", color = TealPrimary, fontWeight = FontWeight.Medium)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                Text(
                    tip.content.ifBlank { tip.excerpt },
                    style = MaterialTheme.typography.bodyLarge, color = TextSecondary
                )
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}
