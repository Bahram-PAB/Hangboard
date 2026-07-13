package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.util.NotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class WorkoutStep {
    data class WarmupIntro(val title: String, val description: String) : WorkoutStep()
    data class WarmupExercise(val name: String, val description: String, val durationSeconds: Int) : WorkoutStep()
    data class HangboardSet(
        val setNumber: Int,
        val totalSets: Int,
        val isHanging: Boolean, // true for hanging, false for resting
        val durationSeconds: Int,
        val holdSizeText: String
    ) : WorkoutStep()
    data class PullupSet(
        val setNumber: Int,
        val totalSets: Int,
        val name: String,
        val repsText: String
    ) : WorkoutStep()
    data class CooldownStretch(val name: String, val description: String, val durationSeconds: Int) : WorkoutStep()
    object Completed : WorkoutStep()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = WorkoutRepository(database.workoutDao())

    // UI state streams
    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfile()
        )

    val allSessions: StateFlow<List<WorkoutSession>> = repository.allSessions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current app navigation / screen state
    var currentScreen by mutableStateOf("HOME") // HOME, WORKOUT, PROGRESS, SETTINGS

    // Selected session for active workout (Defaults to today's workout)
    var selectedWeek by mutableStateOf(1)
    var selectedDayIndex by mutableStateOf(0) // 0 = Sat, 1 = Mon, 2 = Wed

    // State for Active Workout Screen
    var workoutStepsList = listOf<WorkoutStep>()
    var currentStepIndex by mutableStateOf(0)
    var timeLeftSeconds by mutableStateOf(0)
    var isTimerRunning by mutableStateOf(false)
    private var timerJob: Job? = null

    // Post-workout state
    var showCompletionScreen by mutableStateOf(false)

    init {
        viewModelScope.launch {
            // Initialize data with 36 pending sessions if empty
            repository.initializeIfEmpty()

            // Find current week and day based on last pending or last completed session
            repository.allSessions.collectLatest { sessions ->
                if (sessions.isNotEmpty()) {
                    val pending = sessions.sortedWith(compareBy({ it.week }, { it.dayIndex }))
                        .firstOrNull { it.status == "PENDING" }
                    if (pending != null) {
                        selectedWeek = pending.week
                        selectedDayIndex = pending.dayIndex
                    } else {
                        // All completed! Default to last
                        selectedWeek = 12
                        selectedDayIndex = 2
                    }
                }
            }
        }
    }

    // Prepare workout steps depending on the week and day
    fun startWorkout(week: Int, dayIndex: Int) {
        selectedWeek = week
        selectedDayIndex = dayIndex
        workoutStepsList = buildWorkoutSteps(week, dayIndex)
        currentStepIndex = 0
        showCompletionScreen = false
        isTimerRunning = false
        timerJob?.cancel()

        // Set initial timer
        val firstStep = workoutStepsList.firstOrNull()
        timeLeftSeconds = when (firstStep) {
            is WorkoutStep.WarmupExercise -> firstStep.durationSeconds
            is WorkoutStep.HangboardSet -> firstStep.durationSeconds
            is WorkoutStep.CooldownStretch -> firstStep.durationSeconds
            else -> 0
        }

        currentScreen = "WORKOUT"
    }

    private fun buildWorkoutSteps(week: Int, dayIndex: Int): List<WorkoutStep> {
        val steps = mutableListOf<WorkoutStep>()

        // 1. Warm-up Intro
        steps.add(
            WorkoutStep.WarmupIntro(
                title = "گرم کردن عمومی",
                description = "قبل از شروع تمرین اصلی، حتماً ۵ دقیقه بدن خود را گرم کنید تا از آسیب‌دیدگی تاندون‌ها و مفاصل جلوگیری شود."
            )
        )

        // 2. Warm-up Exercises (60 seconds each)
        val warmups = listOf(
            Pair("چرخش گردن", "حرکت ملایم گردن به صورت دایره‌ای به دو طرف"),
            Pair("چرخش شانه", "چرخش شانه‌ها به جلو و عقب برای روان‌سازی مفصل شانه"),
            Pair("چرخش مچ", "حرکت چرخشی مچ دست‌ها به هر دو جهت"),
            Pair("باز و بسته کردن انگشتان", "کشش و جمع کردن سریع انگشتان برای خون‌رسانی سریع"),
            Pair("آویزان شدن سبک", "آویزان شدن بسیار آرام از بزرگترین گیره با پاهای روی زمین")
        )
        for (w in warmups) {
            steps.add(WorkoutStep.WarmupExercise(w.first, w.second, 60))
        }

        // 3. Hangboard sets based on month
        val totalSets: Int
        val hangSeconds: Int
        val restSeconds: Int
        val holdSizeText: String
        val month = when (week) {
            in 1..4 -> 1
            in 5..8 -> 2
            else -> 3
        }

        if (month == 1) {
            totalSets = 6
            hangSeconds = 8
            restSeconds = 60
            holdSizeText = "گیره‌های بزرگ (بزرگتر از ۲۵ میلی‌متر)"
        } else if (month == 2) {
            totalSets = 7
            hangSeconds = 12
            restSeconds = 45
            holdSizeText = "گیره‌های متوسط (حدود ۱۵ تا ۲۰ میلی‌متر)"
        } else {
            totalSets = 8
            hangSeconds = 15
            restSeconds = 45
            holdSizeText = "گیره‌های کوچک (حدود ۱۰ تا ۱۲ میلی‌متر)"
        }

        for (set in 1..totalSets) {
            steps.add(WorkoutStep.HangboardSet(set, totalSets, true, hangSeconds, holdSizeText))
            if (set < totalSets) {
                steps.add(WorkoutStep.HangboardSet(set, totalSets, false, restSeconds, holdSizeText))
            }
        }

        // 4. Pull-up sets based on month and day
        if (month == 2) {
            // Day 0 (Sat): 2 sets, Day 2 (Wed): 3 sets
            if (dayIndex == 0) {
                for (s in 1..2) {
                    steps.add(WorkoutStep.PullupSet(s, 2, "بارفیکس نیمه", "۸ تا ۱۰ مرتبه"))
                }
            } else if (dayIndex == 2) {
                for (s in 1..3) {
                    steps.add(WorkoutStep.PullupSet(s, 3, "بارفیکس نیمه", "۸ تا ۱۰ مرتبه"))
                }
            }
        } else if (month == 3) {
            // Day 0 (Sat): 3 sets, Day 2 (Wed): 4 sets
            if (dayIndex == 0) {
                for (s in 1..3) {
                    steps.add(WorkoutStep.PullupSet(s, 3, "بارفیکس کامل", "۶ تا ۸ مرتبه"))
                }
            } else if (dayIndex == 2) {
                for (s in 1..4) {
                    steps.add(WorkoutStep.PullupSet(s, 4, "بارفیکس کامل", "۶ تا ۸ مرتبه"))
                }
            }
        }

        // 5. Cooldown/Stretch
        steps.add(
            WorkoutStep.WarmupIntro(
                title = "سرد کردن و حرکات کششی",
                description = "کشش انگشتان، ساعد و شانه‌ها برای بازیابی بهتر و افزایش انعطاف‌پذیری (۳ تا ۵ دقیقه)"
            )
        )
        val stretches = listOf(
            Pair("کشش انگشتان", "کشش ملایم تک تک انگشتان به عقب"),
            Pair("کشش ساعد", "کشش مچ دست‌ها به سمت پایین و عقب برای عضلات خم‌کننده انگشت"),
            Pair("کشش شانه", "کشش دست به صورت عرضی روی سینه")
        )
        for (s in stretches) {
            steps.add(WorkoutStep.CooldownStretch(s.first, s.second, 60))
        }

        steps.add(WorkoutStep.Completed)
        return steps
    }

    // Timer Controls
    fun startTimer() {
        if (isTimerRunning) return
        isTimerRunning = true
        timerJob = viewModelScope.launch {
            while (timeLeftSeconds > 0) {
                delay(1000)
                timeLeftSeconds--
            }
            isTimerRunning = false
            moveToNextStep()
        }
    }

    fun pauseTimer() {
        isTimerRunning = false
        timerJob?.cancel()
    }

    fun skipStep() {
        isTimerRunning = false
        timerJob?.cancel()
        moveToNextStep()
    }

    private fun moveToNextStep() {
        if (currentStepIndex < workoutStepsList.size - 1) {
            currentStepIndex++
            val nextStep = workoutStepsList[currentStepIndex]
            timeLeftSeconds = when (nextStep) {
                is WorkoutStep.WarmupExercise -> nextStep.durationSeconds
                is WorkoutStep.HangboardSet -> nextStep.durationSeconds
                is WorkoutStep.CooldownStretch -> nextStep.durationSeconds
                else -> 0
            }
            // Auto start timer for rest steps and hanging steps to keep workout fluid
            if (nextStep is WorkoutStep.HangboardSet || nextStep is WorkoutStep.WarmupExercise || nextStep is WorkoutStep.CooldownStretch) {
                startTimer()
            }
        } else {
            // Workout finished completely!
            showCompletionScreen = true
        }
    }

    fun finishWorkoutAndRate(difficulty: String) {
        viewModelScope.launch {
            // Save difficulty and complete status
            val sessionId = "W${selectedWeek}_D${selectedDayIndex}"
            val completedSession = WorkoutSession(
                id = sessionId,
                week = selectedWeek,
                dayIndex = selectedDayIndex,
                status = "COMPLETED",
                difficulty = difficulty,
                completedAt = System.currentTimeMillis()
            )
            repository.updateSession(completedSession)

            // Trigger notification
            NotificationHelper.showNotification(
                getApplication(),
                "تمرین انجام شد 🥳",
                "آفرین! تمرین امروز (${selectedWeek} هفته، جلسه ${getDayName(selectedDayIndex)}) با موفقیت کامل شد."
            )

            // Update records dynamically
            val profile = userProfile.value
            val month = when (selectedWeek) {
                in 1..4 -> 1
                in 5..8 -> 2
                else -> 3
            }
            val currentHangSeconds = if (month == 1) 8 else if (month == 2) 12 else 15
            val currentMaxHang = if (currentHangSeconds > profile.maxHangTime) currentHangSeconds else profile.maxHangTime

            val pullupReps = if (month == 2) 10 else if (month == 3) 8 else 0
            val currentMaxPullUps = if (pullupReps > profile.maxPullUps) pullupReps else profile.maxPullUps

            repository.updateProfile(
                profile.copy(
                    maxHangTime = currentMaxHang,
                    maxPullUps = currentMaxPullUps
                )
            )

            // Return to HOME
            currentScreen = "HOME"
            showCompletionScreen = false
        }
    }

    fun markAsMissed(week: Int, dayIndex: Int) {
        viewModelScope.launch {
            val sessionId = "W${week}_D${dayIndex}"
            val missedSession = WorkoutSession(
                id = sessionId,
                week = week,
                dayIndex = dayIndex,
                status = "MISSED",
                completedAt = System.currentTimeMillis()
            )
            repository.updateSession(missedSession)
        }
    }

    // Profile Settings Controls
    fun updateProfile(name: String, age: Int, height: Float, weight: Float, isDark: Boolean, reminder: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch {
            val updated = UserProfile(
                id = 1,
                name = name,
                age = age,
                height = height,
                weight = weight,
                isDarkTheme = isDark,
                reminderEnabled = reminder,
                reminderHour = hour,
                reminderMinute = minute,
                maxHangTime = userProfile.value.maxHangTime,
                maxPullUps = userProfile.value.maxPullUps
            )
            repository.updateProfile(updated)

            // Reschedule notification
            if (reminder) {
                NotificationHelper.scheduleDailyReminder(getApplication(), hour, minute)
            } else {
                NotificationHelper.cancelReminder(getApplication())
            }
        }
    }

    fun resetAllApp() {
        viewModelScope.launch {
            repository.resetAllData()
            selectedWeek = 1
            selectedDayIndex = 0
            currentScreen = "HOME"
        }
    }

    // Helpers
    fun getDayName(index: Int): String {
        return when (index) {
            0 -> "شنبه"
            1 -> "دوشنبه"
            2 -> "چهارشنبه"
            else -> ""
        }
    }
}
