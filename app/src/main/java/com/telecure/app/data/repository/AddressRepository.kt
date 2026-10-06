package com.telecure.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.telecure.app.data.model.Address
import kotlinx.coroutines.tasks.await

class AddressRepository {
    private val db = FirebaseFirestore.getInstance()
    private fun col(uid: String) = db.collection("users").document(uid).collection("addresses")

    suspend fun getAddresses(uid: String): List<Address> = try {
        col(uid).get().await().toObjects(Address::class.java)
    } catch (e: Exception) { emptyList() }

    suspend fun addAddress(uid: String, address: Address): Result<Unit> = try {
        val ref = col(uid).document()
        if (address.isDefault) clearDefaults(uid)
        ref.set(address.copy(id = ref.id)).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun setDefault(uid: String, addressId: String): Result<Unit> = try {
        clearDefaults(uid)
        col(uid).document(addressId).update("isDefault", true).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteAddress(uid: String, addressId: String): Result<Unit> = try {
        col(uid).document(addressId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    private suspend fun clearDefaults(uid: String) {
        val existing = col(uid).get().await().toObjects(Address::class.java)
        existing.filter { it.isDefault }.forEach { col(uid).document(it.id).update("isDefault", false).await() }
    }
}
