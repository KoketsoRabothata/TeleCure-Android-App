package com.telecure.app.ui.viewmodel

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.telecure.app.data.model.CartItem
import com.telecure.app.data.model.Medicine
import com.telecure.app.data.repository.PharmacyRepository
import kotlinx.coroutines.launch

/**
 * PharmacyScreen, ProductDetailScreen, and CartCheckoutScreen are separate navigation
 * destinations, so calling the default viewModel() in each one creates three unrelated
 * instances with three unrelated carts. This helper scopes PharmacyViewModel to the
 * Activity instead, so every screen that calls pharmacyViewModel() shares one cart.
 */
@Composable
fun pharmacyViewModel(): PharmacyViewModel {
    val activity = LocalContext.current as ComponentActivity
    return viewModel(activity)
}

class PharmacyViewModel : ViewModel() {
    private val repo = PharmacyRepository()

    var medicines by mutableStateOf<List<Medicine>>(emptyList()); private set
    var isLoading by mutableStateOf(true); private set
    var selectedCategory by mutableStateOf("All"); private set
    var searchTerm by mutableStateOf(""); private set
    var selectedMedicine by mutableStateOf<Medicine?>(null); private set
    var cart by mutableStateOf<List<CartItem>>(emptyList()); private set

    val categories = listOf("All", "Pain Relief", "Vitamins", "Chronic", "Cold & Flu", "Skin Care")
    val cartCount: Int get() = cart.sumOf { it.quantity }
    val cartTotal: Double get() = cart.sumOf { it.price * it.quantity }

    fun load() {
        viewModelScope.launch {
            isLoading = true
            medicines = repo.getMedicines()
            isLoading = false
        }
    }

    fun filterByCategory(category: String) {
        selectedCategory = category
        viewModelScope.launch { medicines = repo.getMedicines(category) }
    }

    fun search(term: String) {
        searchTerm = term
        viewModelScope.launch { medicines = repo.searchMedicines(term) }
    }

    fun loadMedicine(id: String) {
        viewModelScope.launch { selectedMedicine = repo.getMedicine(id) }
    }

    fun addToCart(medicine: Medicine, quantity: Int) {
        val existing = cart.find { it.medicineId == medicine.id }
        cart = if (existing != null) {
            cart.map { if (it.medicineId == medicine.id) it.copy(quantity = it.quantity + quantity) else it }
        } else {
            cart + CartItem(medicine.id, medicine.name, medicine.price, medicine.imageUrl, quantity, medicine.prescriptionRequired)
        }
    }

    fun updateQuantity(medicineId: String, quantity: Int) {
        cart = if (quantity <= 0) cart.filterNot { it.medicineId == medicineId }
        else cart.map { if (it.medicineId == medicineId) it.copy(quantity = quantity) else it }
    }

    fun removeFromCart(medicineId: String) {
        cart = cart.filterNot { it.medicineId == medicineId }
    }

    fun clearCart() { cart = emptyList() }
}
