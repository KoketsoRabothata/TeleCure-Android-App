package com.telecure.app.ui.screens.health

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.telecure.app.ui.components.EmptyState
import com.telecure.app.ui.components.LoadingIndicator
import com.telecure.app.ui.components.TeleCureTopBar
import com.telecure.app.ui.theme.TealPrimary
import com.telecure.app.ui.theme.TextPrimary
import com.telecure.app.ui.theme.TextSecondary
import com.telecure.app.ui.viewmodel.HealthTipsViewModel

@Composable
fun HealthTipsScreen(navController: NavController, vm: HealthTipsViewModel = viewModel()) {
    LaunchedEffect(Unit) { vm.load() }

    Column(Modifier.fillMaxSize()) {
        TeleCureTopBar("Health Tips", navController)
        LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vm.categories) { category ->
                FilterChip(
                    selected = vm.selectedCategory == category,
                    onClick = { vm.filter(category) },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealPrimary, selectedLabelColor = Color.White)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        when {
            vm.isLoading -> LoadingIndicator()
            vm.tips.isEmpty() -> EmptyState(Icons.AutoMirrored.Filled.Article, "No articles yet", "Check back soon for health tips and guides.")
            else -> LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(vm.tips) { tip ->
                    Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFF7FBFA), modifier = Modifier.fillMaxWidth().clickable { navController.navigate(com.telecure.app.navigation.Screen.ContentDetail.build(tip.id)) }) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(tip.category, color = TealPrimary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                Text(tip.title, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text(tip.excerpt, color = TextSecondary, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
                            }
                            IconButton(onClick = { vm.toggleBookmark(tip.id) }) {
                                Icon(
                                    if (vm.bookmarked.contains(tip.id)) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                    contentDescription = "Bookmark", tint = TealPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
