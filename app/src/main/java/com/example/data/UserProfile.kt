package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "سنگ‌نورد",
    val age: Int = 25,
    val height: Float = 175f,
    val weight: Float = 68f,
    val reminderEnabled: Boolean = true,
    val reminderHour: Int = 18,
    val reminderMinute: Int = 0,
    val isDarkTheme: Boolean = true,
    val maxHangTime: Int = 0,
    val maxPullUps: Int = 0
)
