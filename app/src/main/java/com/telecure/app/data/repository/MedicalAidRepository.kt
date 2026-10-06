package com.telecure.app.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.telecure.app.data.model.MedicalAid
import kotlinx.coroutines.tasks.await

class MedicalAidRepository {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val col = db.collection("medical_aid")

    suspend fun getMedicalAid(uid: String): MedicalAid? = try {
        col.document(uid).get().await().toObject(MedicalAid::class.java)
    } catch (e: Exception) { null }

    suspend fun saveMedicalAid(medicalAid: MedicalAid): Result<Unit> = try {
        col.document(medicalAid.patientUid).set(medicalAid).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun uploadCard(uid: String, imageUri: Uri): Result<String> = try {
        val ref = storage.reference.child("medical_aid_cards/$uid.jpg")
        ref.putFile(imageUri).await()
        val url = ref.downloadUrl.await().toString()
        Result.success(url)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
