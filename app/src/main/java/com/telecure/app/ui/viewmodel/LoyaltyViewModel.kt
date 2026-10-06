package com.telecure.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.data.model.LoyaltyEntry
import com.telecure.app.data.repository.LoyaltyRepository
import kotlinx.coroutines.launch

class LoyaltyViewModel : ViewModel() {
    private val repo = LoyaltyRepository()

    var entries by mutableStateOf<List<LoyaltyEntry>>(emptyList()); private set
    var isLoading by mutableStateOf(true); private set
    val totalPoints: Int get() = entries.sumOf { it.points }

    fun load() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            isLoading = true
            entries = repo.getEntries(uid)
            isLoading = false
        }
    }
}
