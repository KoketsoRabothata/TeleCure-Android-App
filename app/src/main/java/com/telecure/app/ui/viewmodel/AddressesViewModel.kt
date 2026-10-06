package com.telecure.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.data.model.Address
import com.telecure.app.data.repository.AddressRepository
import kotlinx.coroutines.launch

class AddressesViewModel : ViewModel() {
    private val repo = AddressRepository()

    var addresses by mutableStateOf<List<Address>>(emptyList()); private set
    var isLoading by mutableStateOf(true); private set

    fun load() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            isLoading = true
            addresses = repo.getAddresses(uid)
            isLoading = false
        }
    }

    fun addAddress(label: String, line1: String, city: String, postcode: String, isDefault: Boolean) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        if (line1.isBlank()) return
        viewModelScope.launch {
            repo.addAddress(uid, Address(label = label.ifBlank { "Home" }, line1 = line1, city = city, postcode = postcode, isDefault = isDefault))
            load()
        }
    }

    fun setDefault(addressId: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch { repo.setDefault(uid, addressId); load() }
    }

    fun deleteAddress(addressId: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch { repo.deleteAddress(uid, addressId); load() }
    }
}
