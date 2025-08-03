package com.example.careconnect.data.repository

import android.util.Log
import com.example.careconnect.data.local.dao.UserDao
import com.example.careconnect.data.model.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

interface CareRepository {
    // User Management
    fun getCurrentUserProfile(): Flow<User?>
    fun getUsersInCareCircle(careCircleId: String): Flow<List<User>>
    suspend fun fetchUsersFromRemote(careCircleId: String)
    suspend fun updateUserProfile(name: String, phone: String, address: String, role: String)
    suspend fun updateUserRole(userIdToUpdate: String, newRole: String)
    suspend fun deleteUserAccount()
    fun signOut()

    // Dashboard
    suspend fun getDashboardData(): DashboardModel

    // Schedule
    suspend fun getScheduleForDate(date: Date): List<Any>

    // Messaging
    fun getMessages(): Flow<List<Message>>
    suspend fun sendMessage(text: String)

    // Journal
    fun getJournalEntries(): Flow<List<JournalEntry>>
    suspend fun addJournalEntry(note: String)

    // Generic
    suspend fun clearLocalData()
}

data class DashboardModel(
    val upcomingAppointment: Appointment?,
    val nextMedication: Medication?
)

@Singleton
class CareRepositoryImpl @Inject constructor(private val userDao: UserDao) : CareRepository {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    override fun getCurrentUserProfile(): Flow<User?> {
        Log.d("CareRepository", "Fetching profile for user ID: ${auth.currentUser?.uid}")
        val userId = auth.currentUser?.uid ?: return emptyFlow()

        // ✅ THE FIX IS HERE: The try-catch block has been removed from this flow builder.
        // The ViewModel, using .firstOrNull(), will safely handle any potential exceptions
        // without violating Flow principles.
        return flow {
            val userDoc = firestore.collection("users").document(userId).get().await()
            emit(userDoc.toObject(User::class.java))
        }
    }

    override fun getUsersInCareCircle(careCircleId: String): Flow<List<User>> {
        if (careCircleId.isBlank()) {
            return emptyFlow()
        }
        return flow {
            try {
                val snapshot = firestore.collection("users")
                    .whereEqualTo("careCircleId", careCircleId)
                    .get().await()
                emit(snapshot.toObjects(User::class.java))
            } catch (e: Exception) {
                Log.e("CareRepository", "Error getting users in care circle", e)
                emit(emptyList())
            }
        }
    }

    override suspend fun fetchUsersFromRemote(careCircleId: String) {
        // Implementation can be added here if needed for specific caching strategies
    }

    override suspend fun updateUserProfile(name: String, phone: String, address: String, role: String) {
        val userId = auth.currentUser?.uid ?: return
        try {
            val updatedData = mapOf(
                "name" to name,
                "phone" to phone,
                "address" to address,
                "role" to role
            )
            firestore.collection("users").document(userId).update(updatedData).await()
        } catch (e: Exception) {
            Log.e("CareRepository", "Error updating user profile", e)
        }
    }

    override suspend fun updateUserRole(userIdToUpdate: String, newRole: String) {
        try {
            firestore.collection("users").document(userIdToUpdate).update("role", newRole).await()
        } catch (e: Exception) {
            Log.e("CareRepository", "Error updating user role", e)
        }
    }

    override suspend fun deleteUserAccount() {
        val user = auth.currentUser ?: return
        try {
            // First, delete the Firestore document containing user details.
            firestore.collection("users").document(user.uid).delete().await()
            // Second, delete the user from Firebase Authentication.
            user.delete().await()
        } catch (e: Exception) {
            Log.e("CareRepository", "Error deleting user account", e)
            // Note: Deleting an Auth user can sometimes fail if they haven't logged
            // in recently. For a production app, you would handle this by prompting
            // the user to re-authenticate before deleting.
        }
    }

    override fun signOut() {
        auth.signOut()
    }

    // --- Existing Placeholder Implementations ---

    override suspend fun getDashboardData(): DashboardModel {
        return DashboardModel(null, null)
    }

    override suspend fun getScheduleForDate(date: Date): List<Any> {
        return emptyList()
    }

    override fun getMessages(): Flow<List<Message>> {
        return emptyFlow()
    }

    override suspend fun sendMessage(text: String) {
        // No-op for now
    }

    override fun getJournalEntries(): Flow<List<JournalEntry>> {
        return emptyFlow()
    }

    override suspend fun addJournalEntry(note: String) {
        // No-op for now
    }

    override suspend fun clearLocalData() {
        userDao.clearAll()
    }
}

