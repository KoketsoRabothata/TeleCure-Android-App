package com.telecure.app.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.telecure.app.data.model.UserProfile
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    val currentUid: String? get() = auth.currentUser?.uid
    fun isLoggedIn(): Boolean = auth.currentUser != null

    suspend fun signIn(email: String, password: String): Result<String> = try {
        val res = auth.signInWithEmailAndPassword(email, password).await()
        Result.success(res.user?.uid.orEmpty())
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun signUp(name: String, email: String, phone: String, password: String): Result<String> = try {
        val res = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = res.user?.uid.orEmpty()
        val profile = UserProfile(uid = uid, fullName = name, email = email, phone = phone)
        db.collection("users").document(uid).set(profile).await()
        Result.success(uid)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /** Signs in (or silently registers, on first use) with a Google ID token from GoogleSignIn. */
    suspend fun signInWithGoogle(idToken: String): Result<String> = try {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val res = auth.signInWithCredential(credential).await()
        val user = res.user
        val uid = user?.uid.orEmpty()

        // Create a users/{uid} profile document the first time this Google account signs in.
        val existing = db.collection("users").document(uid).get().await()
        if (!existing.exists()) {
            val profile = UserProfile(
                uid = uid,
                fullName = user?.displayName.orEmpty(),
                email = user?.email.orEmpty(),
                avatarUrl = user?.photoUrl?.toString().orEmpty()
            )
            db.collection("users").document(uid).set(profile).await()
        }
        Result.success(uid)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> = try {
        auth.sendPasswordResetEmail(email).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getProfile(uid: String): UserProfile? = try {
        db.collection("users").document(uid).get().await().toObject(UserProfile::class.java)
    } catch (e: Exception) { null }

    suspend fun updateProfile(profile: UserProfile): Result<Unit> = try {
        db.collection("users").document(profile.uid).set(profile).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /** Uploads a picked profile photo to Storage and returns its download URL. */
    suspend fun uploadAvatar(uid: String, imageUri: Uri): Result<String> = try {
        val ref = storage.reference.child("avatars/$uid.jpg")
        ref.putFile(imageUri).await()
        val url = ref.downloadUrl.await().toString()
        Result.success(url)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun signOut() = auth.signOut()
}
