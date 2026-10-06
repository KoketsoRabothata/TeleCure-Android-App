package com.telecure.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.data.model.LabTest
import com.telecure.app.data.repository.LabTestRepository
import kotlinx.coroutines.launch

class LabTestViewModel : ViewModel() {
    private val repo = LabTestRepository()

    var labTests by mutableStateOf<List<LabTest>>(emptyList()); private set
    var isLoading by mutableStateOf(true); private set
    var isBooking by mutableStateOf(false); private set

    val availableTests = repo.availableTests()

    fun load() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            isLoading = true
            labTests = repo.getLabTests(uid)
            isLoading = false
        }
    }

    fun bookTest(testName: String, price: Double) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            isBooking = true
            repo.bookTest(LabTest(patientUid = uid, testName = testName, price = price))
            isBooking = false
            load()
        }
    }
}
