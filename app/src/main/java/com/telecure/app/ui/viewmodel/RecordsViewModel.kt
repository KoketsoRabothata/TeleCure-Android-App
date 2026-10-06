package com.telecure.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.data.model.MedicalRecord
import com.telecure.app.data.model.Prescription
import com.telecure.app.data.repository.PrescriptionRepository
import com.telecure.app.data.repository.RecordsRepository
import kotlinx.coroutines.launch

class RecordsViewModel : ViewModel() {
    private val prescriptionRepo = PrescriptionRepository()
    private val recordsRepo = RecordsRepository()

    var prescriptions by mutableStateOf<List<Prescription>>(emptyList()); private set
    var records by mutableStateOf<List<MedicalRecord>>(emptyList()); private set
    var isLoading by mutableStateOf(true); private set

    fun loadPrescriptions(type: String? = null) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            isLoading = true
            prescriptions = prescriptionRepo.getPrescriptions(uid, type)
            isLoading = false
        }
    }

    fun loadRecords() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            isLoading = true
            records = recordsRepo.getRecords(uid)
            isLoading = false
        }
    }

    fun uploadRecord(title: String, category: String, uri: android.net.Uri) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            recordsRepo.uploadRecord(uid, title, category, uri)
            loadRecords()
        }
    }

    fun uploadScannedRecord(title: String, category: String, jpegBytes: ByteArray) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            recordsRepo.uploadScannedRecord(uid, title, category, jpegBytes)
            loadRecords()
        }
    }
}
