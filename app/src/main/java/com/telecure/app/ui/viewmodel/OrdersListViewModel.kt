package com.telecure.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.data.model.PharmacyOrder
import com.telecure.app.data.repository.PharmacyRepository
import kotlinx.coroutines.launch

class OrdersListViewModel : ViewModel() {
    private val repo = PharmacyRepository()

    var orders by mutableStateOf<List<PharmacyOrder>>(emptyList()); private set
    var isLoading by mutableStateOf(true); private set

    fun load() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            isLoading = true
            orders = repo.getOrders(uid).sortedByDescending { it.createdAt }
            isLoading = false
        }
    }
}
