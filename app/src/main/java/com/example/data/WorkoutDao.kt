package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileDirect(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)

    // Workout Sessions
    @Query("SELECT * FROM workout_session")
    fun getAllSessionsFlow(): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_session WHERE id = :id LIMIT 1")
    suspend fun getSessionById(id: String): WorkoutSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSession)

    @Query("DELETE FROM workout_session")
    suspend fun clearAllSessions()
}
