package com.telecure.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telecure.app.data.model.HealthTip
import com.telecure.app.data.repository.HealthTipRepository
import kotlinx.coroutines.launch

class HealthTipsViewModel : ViewModel() {
    private val repo = HealthTipRepository()

    var tips by mutableStateOf<List<HealthTip>>(emptyList()); private set
    var isLoading by mutableStateOf(true); private set
    var selectedCategory by mutableStateOf("All"); private set
    var bookmarked by mutableStateOf<Set<String>>(emptySet()); private set
    var selectedTip by mutableStateOf<HealthTip?>(null); private set

    val categories = listOf("All", "Nutrition", "Fitness", "Mental Health", "Chronic Care", "Parenting")

    fun loadTip(id: String) {
        viewModelScope.launch { selectedTip = repo.getTip(id) }
    }

    fun load() {
        viewModelScope.launch {
            isLoading = true
            tips = repo.getTips()
            isLoading = false
        }
    }

    fun filter(category: String) {
        selectedCategory = category
        viewModelScope.launch { tips = repo.getTips(category) }
    }

    fun toggleBookmark(id: String) {
        bookmarked = if (bookmarked.contains(id)) bookmarked - id else bookmarked + id
    }
}
