package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class WorkoutRepository(private val workoutDao: WorkoutDao) {

    val userProfile: Flow<UserProfile?> = workoutDao.getUserProfileFlow()
    val allSessions: Flow<List<WorkoutSession>> = workoutDao.getAllSessionsFlow()

    suspend fun getProfile(): UserProfile {
        var profile = workoutDao.getUserProfileDirect()
        if (profile == null) {
            profile = UserProfile()
            workoutDao.insertUserProfile(profile)
        }
        return profile
    }

    suspend fun updateProfile(profile: UserProfile) {
        workoutDao.insertUserProfile(profile)
    }

    suspend fun updateSession(session: WorkoutSession) {
        workoutDao.insertSession(session)
    }

    suspend fun initializeIfEmpty() {
        val currentProfile = workoutDao.getUserProfileDirect()
        if (currentProfile == null) {
            workoutDao.insertUserProfile(UserProfile())
        }

        // Check if sessions are initialized
        val sessions = workoutDao.getAllSessionsFlow().firstOrNull() ?: emptyList()
        if (sessions.isEmpty()) {
            // Generate all 36 sessions (12 weeks * 3 days)
            for (w in 1..12) {
                for (d in 0..2) {
                    val id = "W${w}_D${d}"
                    workoutDao.insertSession(
                        WorkoutSession(
                            id = id,
                            week = w,
                            dayIndex = d,
                            status = "PENDING"
                        )
                    )
                }
            }
        }
    }

    suspend fun resetAllData() {
        workoutDao.clearAllSessions()
        workoutDao.insertUserProfile(UserProfile())
        // Generate again
        for (w in 1..12) {
            for (d in 0..2) {
                val id = "W${w}_D${d}"
                workoutDao.insertSession(
                    WorkoutSession(
                        id = id,
                        week = w,
                        dayIndex = d,
                        status = "PENDING"
                    )
                )
            }
        }
    }
}
