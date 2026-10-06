package com.telecure.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.data.model.PaymentMethod
import com.telecure.app.data.repository.PaymentMethodRepository
import kotlinx.coroutines.launch

class PaymentMethodsViewModel : ViewModel() {
    private val repo = PaymentMethodRepository()

    var methods by mutableStateOf<List<PaymentMethod>>(emptyList()); private set
    var isLoading by mutableStateOf(true); private set

    fun load() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            isLoading = true
            methods = repo.getMethods(uid)
            isLoading = false
        }
    }

    fun addMethod(type: String, label: String, isDefault: Boolean) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        if (label.isBlank()) return
        viewModelScope.launch {
            repo.addMethod(uid, PaymentMethod(type = type, label = label, isDefault = isDefault))
            load()
        }
    }

    fun setDefault(methodId: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch { repo.setDefault(uid, methodId); load() }
    }

    fun deleteMethod(methodId: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch { repo.deleteMethod(uid, methodId); load() }
    }
}
