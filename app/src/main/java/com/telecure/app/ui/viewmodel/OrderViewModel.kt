package com.telecure.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.telecure.app.data.model.OrderStatus
import com.telecure.app.data.model.PharmacyOrder
import com.telecure.app.data.repository.PharmacyRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {
    private val repo = PharmacyRepository()
    private var listener: ListenerRegistration? = null
    private var simulationJob: Job? = null

    var order by mutableStateOf<PharmacyOrder?>(null); private set

    fun track(orderId: String) {
        listener?.remove()
        listener = repo.listenOrder(orderId) { order = it }
    }

    /**
     * There's no real driver app behind this demo, so progress is simulated here and written
     * to Firestore — but the writes are genuine: the map marker, route line, and status
     * timeline all update live off the same Firestore listener a real driver app would push to.
     * Only runs once per order (skips if the order has already moved past CONFIRMED).
     */
    fun startDeliverySimulationIfNeeded(orderId: String) {
        if (simulationJob != null) return
        simulationJob = viewModelScope.launch {
            var attempt = order
            var tries = 0
            while (attempt == null && tries < 10) {
                delay(300)
                attempt = repo.getOrder(orderId)
                tries++
            }
            val start = attempt ?: return@launch
            if (start.status != OrderStatus.CONFIRMED.name) return@launch

            delay(4000)
            repo.updateOrderProgress(orderId, start.driverLat, start.driverLng, OrderStatus.PREPARING.name)

            delay(6000)
            repo.updateOrderProgress(orderId, start.driverLat, start.driverLng, OrderStatus.OUT_FOR_DELIVERY.name)

            val steps = 8
            repeat(steps) { i ->
                delay(1800)
                val t = (i + 1) / steps.toDouble()
                val lat = start.driverLat + (start.destinationLat - start.driverLat) * t
                val lng = start.driverLng + (start.destinationLng - start.driverLng) * t
                repo.updateOrderProgress(orderId, lat, lng, OrderStatus.OUT_FOR_DELIVERY.name)
            }

            delay(1000)
            repo.updateOrderProgress(orderId, start.destinationLat, start.destinationLng, OrderStatus.DELIVERED.name)
        }
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
        simulationJob?.cancel()
    }
}
