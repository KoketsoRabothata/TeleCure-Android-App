package com.telecure.app.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.data.model.MedicalAid
import com.telecure.app.data.repository.MedicalAidRepository
import kotlinx.coroutines.launch

class MedicalAidViewModel : ViewModel() {
    private val repo = MedicalAidRepository()

    var medicalAid by mutableStateOf<MedicalAid?>(null); private set
    var isLoading by mutableStateOf(true); private set
    var isSaving by mutableStateOf(false); private set

    fun load() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            isLoading = true
            medicalAid = repo.getMedicalAid(uid) ?: MedicalAid(patientUid = uid)
            isLoading = false
        }
    }

    fun save(provider: String, memberNumber: String, planName: String, mainMemberName: String, cardUri: Uri?) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            isSaving = true
            var cardUrl = medicalAid?.cardImageUrl.orEmpty()
            if (cardUri != null) {
                repo.uploadCard(uid, cardUri).onSuccess { cardUrl = it }
            }
            val updated = MedicalAid(uid, provider, memberNumber, planName, mainMemberName, cardUrl)
            repo.saveMedicalAid(updated)
            medicalAid = updated
            isSaving = false
        }
    }
}
