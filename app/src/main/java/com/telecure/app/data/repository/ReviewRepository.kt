package com.telecure.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.telecure.app.data.model.Doctor
import com.telecure.app.data.model.DoctorReview
import kotlinx.coroutines.tasks.await

class ReviewRepository {
    private val db = FirebaseFirestore.getInstance()
    private val reviewsCol = db.collection("reviews")
    private val doctorsCol = db.collection("doctors")

    suspend fun getReviews(doctorUid: String): List<DoctorReview> = try {
        reviewsCol.whereEqualTo("doctorUid", doctorUid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get().await().toObjects(DoctorReview::class.java)
    } catch (e: Exception) { emptyList() }

    /** Writes the review, then recalculates the doctor's average rating and count in a transaction. */
    suspend fun submitReview(review: DoctorReview): Result<Unit> = try {
        val reviewRef = reviewsCol.document()
        val doctorRef = doctorsCol.document(review.doctorUid)

        db.runTransaction { txn ->
            val doctorSnap = txn.get(doctorRef)
            val doctor = doctorSnap.toObject(Doctor::class.java) ?: Doctor()
            val newCount = doctor.reviewCount + 1
            val newAverage = ((doctor.rating * doctor.reviewCount) + review.rating) / newCount

            txn.set(reviewRef, review.copy(id = reviewRef.id))
            txn.update(doctorRef, mapOf("rating" to newAverage, "reviewCount" to newCount))
        }.await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
