package com.telecure.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.telecure.app.data.model.Doctor
import kotlinx.coroutines.tasks.await

class DoctorRepository {
    private val db = FirebaseFirestore.getInstance()
    private val col = db.collection("doctors")

    suspend fun getDoctors(specialty: String? = null): List<Doctor> = try {
        val query = if (!specialty.isNullOrBlank() && specialty != "All") {
            col.whereEqualTo("specialty", specialty)
        } else col
        query.get().await().toObjects(Doctor::class.java)
    } catch (e: Exception) { emptyList() }

    suspend fun searchDoctors(term: String): List<Doctor> = try {
        val all = col.get().await().toObjects(Doctor::class.java)
        if (term.isBlank()) all else all.filter {
            it.name.contains(term, true) || it.specialty.contains(term, true)
        }
    } catch (e: Exception) { emptyList() }

    suspend fun getDoctor(uid: String): Doctor? = try {
        col.document(uid).get().await().toObject(Doctor::class.java)
    } catch (e: Exception) { null }
}
