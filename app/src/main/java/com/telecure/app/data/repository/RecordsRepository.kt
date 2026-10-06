package com.telecure.app.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.telecure.app.data.model.MedicalRecord
import kotlinx.coroutines.tasks.await
import java.util.UUID

class RecordsRepository {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val col = db.collection("medical_records")

    suspend fun getRecords(patientUid: String): List<MedicalRecord> = try {
        col.whereEqualTo("patientUid", patientUid).get().await().toObjects(MedicalRecord::class.java)
    } catch (e: Exception) { emptyList() }

    suspend fun uploadRecord(patientUid: String, title: String, category: String, fileUri: Uri): Result<Unit> = try {
        val path = "records/$patientUid/${UUID.randomUUID()}"
        val ref = storage.reference.child(path)
        ref.putFile(fileUri).await()
        val url = ref.downloadUrl.await().toString()
        val doc = col.document()
        doc.set(
            MedicalRecord(id = doc.id, patientUid = patientUid, title = title, category = category, fileUrl = url)
        ).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /** Uploads a document scanned via camera (delivered as an in-memory JPEG) without needing a FileProvider. */
    suspend fun uploadScannedRecord(patientUid: String, title: String, category: String, jpegBytes: ByteArray): Result<Unit> = try {
        val path = "records/$patientUid/${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child(path)
        ref.putBytes(jpegBytes).await()
        val url = ref.downloadUrl.await().toString()
        val doc = col.document()
        doc.set(
            MedicalRecord(id = doc.id, patientUid = patientUid, title = title, category = category, fileUrl = url)
        ).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun shareWithDoctor(recordId: String, doctorUid: String): Result<Unit> = try {
        col.document(recordId).update("sharedWithDoctorUid", doctorUid).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
