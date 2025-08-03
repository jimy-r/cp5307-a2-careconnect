// Defines the file's location within the data repository architecture.
package com.example.careconnect.data.repository

// Imports necessary libraries for logging, data access, models, and Firebase.
import android.util.Log
import com.example.careconnect.data.local.dao.UserDao
import com.example.careconnect.data.model.*
import com.example.careconnect.ui.viewmodel.ScheduleEvent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

// Defines a simple data class to hold the raw results of a daily schedule query.
data class DailySchedule(
    val appointments: List<Appointment>,
    val medications: List<Medication>
)

// Defines the contract (a set of public functions) for the app's data layer.
interface CareRepository {
    // Defines functions for managing user profiles and care circle members.
    // User Management
    fun getCurrentUserProfile(): Flow<User?>
    fun getUsersInCareCircle(careCircleId: String): Flow<List<User>>
    suspend fun fetchUsersFromRemote(careCircleId: String)
    suspend fun updateUserProfile(name: String, phone: String, address: String, role: String)
    suspend fun updateUserRole(userIdToUpdate: String, newRole: String)
    suspend fun deleteUserAccount()
    fun signOut()
    // Defines functions for fetching data needed by the main dashboard.
    // Dashboard
    suspend fun getDashboardData(): DashboardModel
    // Defines functions for fetching and creating schedule events.
    // Schedule
    fun getDailySchedule(date: Date): Flow<DailySchedule>
    suspend fun addAppointment(appointment: Appointment)
    suspend fun addMedication(medication: Medication)
    // Defines functions for real-time messaging.
    // Messaging
    fun getMessages(): Flow<List<Message>>
    suspend fun sendMessage(text: String)
    // Defines functions for the collaborative journal feature.
    // Journal
    fun getJournalEntries(): Flow<List<JournalEntry>>
    suspend fun addJournalEntry(note: String)
    // Defines a generic function for clearing local cached data.
    // Generic
    suspend fun clearLocalData()
}

// Defines a data class that holds all the information needed for the dashboard.
data class DashboardModel(
    val upcomingAppointment: Appointment?,
    val nextMedication: Medication?,
    val latestJournalEntry: JournalEntry?
)

// Marks this class as a Singleton, meaning only one instance will exist in the app.
@Singleton
// Defines the concrete implementation of the CareRepository interface.
class CareRepositoryImpl @Inject constructor(private val userDao: UserDao) : CareRepository {

    // Initialises shortcuts to Firebase Authentication and Firestore services.
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    // A private helper function to reliably get the current user's care circle ID.
    private suspend fun getCareCircleId(): String? {
        val userId = auth.currentUser?.uid ?: return null
        return try {
            firestore.collection("users").document(userId).get().await().getString("careCircleId")
        } catch (e: Exception) {
            Log.e("CareRepository", "Error getting careCircleId for user $userId", e)
            null
        }
    }

    // Fetches the profile of the currently logged-in user from Firestore.
    override fun getCurrentUserProfile(): Flow<User?> {
        Log.d("CareRepository", "Fetching profile for user ID: ${auth.currentUser?.uid}")
        val userId = auth.currentUser?.uid ?: return emptyFlow()
        return flow {
            val userDoc = firestore.collection("users").document(userId).get().await()
            emit(userDoc.toObject(User::class.java))
        }
    }

    // Fetches a list of all users who share the same careCircleId.
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

    // Placeholder for a potential future caching strategy.
    override suspend fun fetchUsersFromRemote(careCircleId: String) {}

    // Updates the current user's document in Firestore with new profile information.
    override suspend fun updateUserProfile(name: String, phone: String, address: String, role: String) {
        val userId = auth.currentUser?.uid ?: return
        try {
            val updatedData = mapOf("name" to name, "phone" to phone, "address" to address, "role" to role)
            firestore.collection("users").document(userId).update(updatedData).await()
        } catch (e: Exception) {
            Log.e("CareRepository", "Error updating user profile", e)
        }
    }

    // Updates a specific user's document in Firestore with a new role.
    override suspend fun updateUserRole(userIdToUpdate: String, newRole: String) {
        try {
            firestore.collection("users").document(userIdToUpdate).update("role", newRole).await()
        } catch (e: Exception) {
            Log.e("CareRepository", "Error updating user role", e)
        }
    }

    // Deletes the user's data from Firestore and then from Firebase Authentication.
    override suspend fun deleteUserAccount() {
        val user = auth.currentUser ?: return
        try {
            firestore.collection("users").document(user.uid).delete().await()
            user.delete().await()
        } catch (e: Exception) {
            Log.e("CareRepository", "Error deleting user account", e)
        }
    }

    // Signs the current user out of Firebase Authentication.
    override fun signOut() {
        auth.signOut()
    }

    // Fetches all necessary data for the dashboard concurrently and combines it.
    override suspend fun getDashboardData(): DashboardModel {
        val defaultModel = DashboardModel(null, null, null)
        try {
            val careCircleId = getCareCircleId() ?: return defaultModel
            val now = Date()

            return coroutineScope {
                val appointmentsJob = async {
                    firestore.collection("careCircles").document(careCircleId)
                        .collection("appointments").whereGreaterThanOrEqualTo("dateTime", now)
                        .orderBy("dateTime").limit(1).get().await()
                        .toObjects<Appointment>().firstOrNull()
                }
                val medicationsJob = async {
                    firestore.collection("careCircles").document(careCircleId)
                        .collection("medications").whereGreaterThanOrEqualTo("time", now)
                        .orderBy("time").limit(1).get().await()
                        .toObjects<Medication>().firstOrNull()
                }
                val journalJob = async {
                    firestore.collection("careCircles").document(careCircleId)
                        .collection("journal").orderBy("timestamp", Query.Direction.DESCENDING)
                        .limit(1).get().await().documents.firstOrNull()?.toObject(JournalEntry::class.java)
                }
                DashboardModel(appointmentsJob.await(), medicationsJob.await(), journalJob.await())
            }
        } catch (e: Exception) {
            Log.e("CareRepository", "Error getting dashboard data", e)
            return defaultModel
        }
    }

    // Creates real-time listeners for appointments and medications for a specific day.
    override fun getDailySchedule(date: Date): Flow<DailySchedule> {
        return flow {
            val careCircleId = getCareCircleId() ?: return@flow
            val startOfDay = Calendar.getInstance().apply { time = date; set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }.time
            val endOfDay = Calendar.getInstance().apply { time = date; set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59) }.time

            val appointmentsFlow = callbackFlow<List<Appointment>> {
                val listener = firestore.collection("careCircles").document(careCircleId)
                    .collection("appointments").whereGreaterThanOrEqualTo("dateTime", startOfDay).whereLessThanOrEqualTo("dateTime", endOfDay)
                    .addSnapshotListener { snapshot, _ -> trySend(snapshot?.toObjects() ?: emptyList()) }
                awaitClose { listener.remove() }
            }
            val medicationsFlow = callbackFlow<List<Medication>> {
                val listener = firestore.collection("careCircles").document(careCircleId)
                    .collection("medications").whereGreaterThanOrEqualTo("time", startOfDay).whereLessThanOrEqualTo("time", endOfDay)
                    .addSnapshotListener { snapshot, _ -> trySend(snapshot?.toObjects() ?: emptyList()) }
                awaitClose { listener.remove() }
            }

            combine(appointmentsFlow, medicationsFlow) { appointments, medications ->
                DailySchedule(appointments, medications)
            }.collect { emit(it) }
        }
    }

    // Adds a new appointment document to the appropriate sub-collection in Firestore.
    override suspend fun addAppointment(appointment: Appointment) {
        getCareCircleId()?.let { firestore.collection("careCircles").document(it).collection("appointments").add(appointment).await() }
    }

    // Adds a new medication document to the appropriate sub-collection in Firestore.
    override suspend fun addMedication(medication: Medication) {
        getCareCircleId()?.let { firestore.collection("careCircles").document(it).collection("medications").add(medication).await() }
    }

    // Creates a real-time listener for messages in the shared care circle.
    override fun getMessages(): Flow<List<Message>> {
        val userId = auth.currentUser?.uid ?: return emptyFlow()
        return flow {
            try {
                val userDoc = firestore.collection("users").document(userId).get().await()
                val careCircleId = userDoc.getString("careCircleId")
                if (careCircleId != null && careCircleId.isNotBlank()) {
                    val messagesFlow: Flow<List<Message>> = callbackFlow {
                        val listener = firestore.collection("careCircles").document(careCircleId)
                            .collection("messages").orderBy("timestamp", Query.Direction.ASCENDING)
                            .addSnapshotListener { snapshot, error ->
                                if (error != null) { close(error); return@addSnapshotListener }
                                if (snapshot != null) { trySend(snapshot.toObjects()) }
                            }
                        awaitClose { listener.remove() }
                    }
                    emitAll(messagesFlow)
                }
            } catch (e: Exception) {
                Log.e("CareRepository", "Could not fetch messages", e)
                emit(emptyList())
            }
        }
    }

    // Adds a new message document to the appropriate sub-collection in Firestore.
    override suspend fun sendMessage(text: String) {
        val user = auth.currentUser ?: return
        try {
            val userDoc = firestore.collection("users").document(user.uid).get().await()
            val userName = userDoc.getString("name") ?: "Unknown User"
            val careCircleId = userDoc.getString("careCircleId")
            if (careCircleId != null && careCircleId.isNotBlank()) {
                val newMessage = Message(senderId = user.uid, senderName = userName, text = text, timestamp = Date())
                firestore.collection("careCircles").document(careCircleId).collection("messages").add(newMessage).await()
            }
        } catch (e: Exception) {
            Log.e("CareRepository", "Error sending message", e)
        }
    }

    // Creates a real-time listener for journal entries in the shared care circle.
    override fun getJournalEntries(): Flow<List<JournalEntry>> {
        val userId = auth.currentUser?.uid ?: return emptyFlow()
        return flow {
            try {
                val userDoc = firestore.collection("users").document(userId).get().await()
                val careCircleId = userDoc.getString("careCircleId")
                if (careCircleId != null && careCircleId.isNotBlank()) {
                    val journalUpdatesFlow: Flow<List<JournalEntry>> = callbackFlow {
                        val listener = firestore.collection("careCircles").document(careCircleId)
                            .collection("journal").orderBy("timestamp", Query.Direction.DESCENDING)
                            .addSnapshotListener { snapshot, error ->
                                if (error != null) { close(error); return@addSnapshotListener }
                                if (snapshot != null) {
                                    trySend(snapshot.toObjects(JournalEntry::class.java))
                                }
                            }
                        awaitClose { listener.remove() }
                    }
                    emitAll(journalUpdatesFlow)
                }
            } catch (e: Exception) {
                Log.e("CareRepository", "Could not fetch journal entries", e)
                emit(emptyList())
            }
        }
    }

    // Adds a new journal entry document to the appropriate sub-collection in Firestore.
    override suspend fun addJournalEntry(note: String) {
        val user = auth.currentUser ?: return
        try {
            val userDoc = firestore.collection("users").document(user.uid).get().await()
            val userName = userDoc.getString("name") ?: "Unknown User"
            val careCircleId = userDoc.getString("careCircleId")
            if (careCircleId != null && careCircleId.isNotBlank()) {
                val newEntry = JournalEntry(authorName = userName, note = note, timestamp = Date())
                firestore.collection("careCircles").document(careCircleId).collection("journal").add(newEntry).await()
            }
        } catch (e: Exception) {
            Log.e("CareRepository", "Error adding journal entry", e)
        }
    }

    // Deletes all tables in the local Room database.
    override suspend fun clearLocalData() {
        userDao.clearAll()
    }
}

