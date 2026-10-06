package com.telecure.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.data.model.DoctorReview
import com.telecure.app.data.repository.ReviewRepository
import kotlinx.coroutines.launch

class ReviewViewModel : ViewModel() {
    private val repo = ReviewRepository()

    var reviews by mutableStateOf<List<DoctorReview>>(emptyList()); private set
    var isLoading by mutableStateOf(false); private set
    var submitted by mutableStateOf(false); private set
    var isSubmitting by mutableStateOf(false); private set

    fun loadReviews(doctorUid: String) {
        viewModelScope.launch {
            isLoading = true
            reviews = repo.getReviews(doctorUid)
            isLoading = false
        }
    }

    fun submitReview(doctorUid: String, appointmentId: String, rating: Int, comment: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val patientName = FirebaseAuth.getInstance().currentUser?.displayName.orEmpty()
        viewModelScope.launch {
            isSubmitting = true
            val review = DoctorReview(
                doctorUid = doctorUid, patientUid = uid, patientName = patientName,
                appointmentId = appointmentId, rating = rating, comment = comment
            )
            val result = repo.submitReview(review)
            isSubmitting = false
            result.onSuccess { submitted = true }
        }
    }
}
