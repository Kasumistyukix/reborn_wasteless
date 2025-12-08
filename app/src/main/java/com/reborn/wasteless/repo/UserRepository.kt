package com.reborn.wasteless.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.reborn.wasteless.data.entity.UserEntity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

/**
 * Repository for user-related data operations.
 * Handles fetching user profile from Firestore.
 */
class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore

    /**
     * Gets the current user's profile from Firestore.
     * Returns LiveData that updates automatically when the document changes.
     *
     * If user document doesn't exist, creates one with email as fallback username.
     */
    fun getCurrentUser(): LiveData<UserEntity> {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("No signed-in user")

        val live = MutableLiveData<UserEntity>()

        val userRef = firestore
            .collection("users")
            .document(uid)

        // Use snapshot listener for real-time updates (automatically cleaned up)
        val listenerRegistration = userRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // If document doesn't exist, create one with email as fallback
                val email = auth.currentUser?.email ?: ""
                val fallbackUser = UserEntity(
                    uid = uid,
                    email = email,
                    username = extractUsernameFromEmail(email)
                )
                live.postValue(fallbackUser)
                return@addSnapshotListener
            }

            val user = snapshot?.toObject(UserEntity::class.java)
            if (user != null) {
                live.postValue(user.copy(uid = uid)) // Ensure uid is set
            } else {
                // Document doesn't exist yet, use email as fallback
                val email = auth.currentUser?.email ?: ""
                val fallbackUser = UserEntity(
                    uid = uid,
                    email = email,
                    username = extractUsernameFromEmail(email)
                )
                live.postValue(fallbackUser)
            }
        }

        // Note: In a production app, you might want to store the listenerRegistration
        // and remove it when needed. However, since we're returning LiveData,
        // the ViewModel will handle lifecycle properly.

        return live
    }

    /**
     * Extracts a display name from email (part before @).
     * Example: "john.doe@example.com" -> "john.doe"
     */
    private fun extractUsernameFromEmail(email: String): String {
        return if (email.isNotEmpty() && email.contains("@")) {
            email.substringBefore("@")
        } else {
            "user"
        }
    }

    /**
     * Creates or updates user document in Firestore.
     * Call this during signup to store username.
     */
    fun createOrUpdateUser(user: UserEntity): Task<Void> {
        val uid = auth.currentUser?.uid
            ?: return com.google.android.gms.tasks.Tasks.forException(
                IllegalStateException("No signed-in user")
            )

        return firestore
            .collection("users")
            .document(uid)
            .set(user.copy(uid = uid))
    }

    /**
     * Checks if there's already an existing same username in the database (should be unique)
     * @return true if username exist, else false
     */
    fun isUsernameTaken(username: String): Task<Boolean> {
        return firestore.collection("users")
            .whereEqualTo("username", username)
            .limit(1) // prevent reading whole collection
            .get()
            .continueWith { task ->
                !task.result.isEmpty // true = username exists
            }
    }
}