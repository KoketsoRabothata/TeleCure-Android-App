package com.telecure.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.telecure.app.data.model.LabTest
import kotlinx.coroutines.tasks.await

private val commonTests = listOf(
    "Full Blood Count" to 250.0,
    "HIV Test" to 150.0,
    "COVID-19 PCR" to 350.0,
    "Cholesterol Panel" to 300.0,
    "Blood Glucose (Diabetes Screen)" to 180.0,
    "Liver Function Test" to 320.0,
    "Kidney Function Test" to 320.0,
    "Thyroid Function Test" to 450.0
)

class LabTestRepository {
    private val db = FirebaseFirestore.getInstance()
    private val col = db.collection("lab_tests")

    fun availableTests(): List<Pair<String, Double>> = commonTests

    suspend fun getLabTests(patientUid: String): List<LabTest> = try {
        col.whereEqualTo("patientUid", patientUid).get().await().toObjects(LabTest::class.java)
    } catch (e: Exception) { emptyList() }

    suspend fun bookTest(labTest: LabTest): Result<Unit> = try {
        val ref = col.document()
        ref.set(labTest.copy(id = ref.id)).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
