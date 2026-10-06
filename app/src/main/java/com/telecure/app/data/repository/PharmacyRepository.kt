package com.telecure.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.telecure.app.data.model.Medicine
import com.telecure.app.data.model.PharmacyOrder
import kotlinx.coroutines.tasks.await

class PharmacyRepository {
    private val db = FirebaseFirestore.getInstance()
    private val medicinesCol = db.collection("medicines")
    private val ordersCol = db.collection("orders")

    suspend fun getMedicines(category: String? = null): List<Medicine> = try {
        val query = if (!category.isNullOrBlank() && category != "All") {
            medicinesCol.whereEqualTo("category", category)
        } else medicinesCol
        query.get().await().toObjects(Medicine::class.java)
    } catch (e: Exception) { emptyList() }

    suspend fun searchMedicines(term: String): List<Medicine> = try {
        val all = medicinesCol.get().await().toObjects(Medicine::class.java)
        if (term.isBlank()) all else all.filter { it.name.contains(term, true) }
    } catch (e: Exception) { emptyList() }

    suspend fun getMedicine(id: String): Medicine? = try {
        medicinesCol.document(id).get().await().toObject(Medicine::class.java)
    } catch (e: Exception) { null }

    suspend fun placeOrder(order: PharmacyOrder): Result<String> = try {
        val ref = ordersCol.document()
        ref.set(order.copy(id = ref.id)).await()
        Result.success(ref.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getOrders(patientUid: String): List<PharmacyOrder> = try {
        ordersCol.whereEqualTo("patientUid", patientUid).get().await().toObjects(PharmacyOrder::class.java)
    } catch (e: Exception) { emptyList() }

    suspend fun getOrder(id: String): PharmacyOrder? = try {
        ordersCol.document(id).get().await().toObject(PharmacyOrder::class.java)
    } catch (e: Exception) { null }

    fun listenOrder(id: String, onUpdate: (PharmacyOrder?) -> Unit) =
        ordersCol.document(id).addSnapshotListener { snap, _ ->
            onUpdate(snap?.toObject(PharmacyOrder::class.java))
        }

    /** Updates the rider's live position and order status — drives the map/timeline in real time. */
    suspend fun updateOrderProgress(orderId: String, driverLat: Double, driverLng: Double, status: String): Result<Unit> = try {
        ordersCol.document(orderId).update(
            mapOf("driverLat" to driverLat, "driverLng" to driverLng, "status" to status)
        ).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
