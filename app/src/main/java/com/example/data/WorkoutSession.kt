package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_session")
data class WorkoutSession(
    @PrimaryKey val id: String, // format: "W{week}_D{dayIndex}" (e.g., "W1_D1")
    val week: Int, // 1 to 12
    val dayIndex: Int, // 0 = Saturday (شنبه), 1 = Monday (دوشنبه), 2 = Wednesday (چهارشنبه)
    val status: String, // "PENDING", "COMPLETED", "MISSED"
    val difficulty: String? = null, // "EASY", "NORMAL", "HARD", "VERY_HARD"
    val completedAt: Long? = null // timestamp of completion
)
