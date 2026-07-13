package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.WorkoutStep

@Composable
fun WorkoutScreen(viewModel: MainViewModel) {
    val steps = viewModel.workoutStepsList
    val currentIndex = viewModel.currentStepIndex
    val isTimerRunning = viewModel.isTimerRunning
    val timeLeft = viewModel.timeLeftSeconds
    val showCompletion = viewModel.showCompletionScreen

    if (showCompletion || (steps.isNotEmpty() && currentIndex >= steps.size) || steps.getOrNull(currentIndex) is WorkoutStep.Completed) {
        // Workout finished difficulty questionnaire screen
        WorkoutDifficultyQuestionnaire(
            onSelected = { difficulty ->
                viewModel.finishWorkoutAndRate(difficulty)
            },
            onCancel = {
                viewModel.currentScreen = "HOME"
            }
        )
    } else if (steps.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val currentStep = steps[currentIndex]

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.currentScreen = "HOME" },
                    modifier = Modifier.testTag("exit_workout")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "خروج")
                }
                Text(
                    text = "تمرین: هفته ${viewModel.selectedWeek} - روز ${viewModel.getDayName(viewModel.selectedDayIndex)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "${currentIndex + 1} از ${steps.size - 1}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Step Content Display
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (currentStep) {
                    is WorkoutStep.WarmupIntro -> {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsRun,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = currentStep.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = currentStep.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = { viewModel.skipStep() },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("شروع گرم کردن")
                                }
                            }
                        }
                    }

                    is WorkoutStep.WarmupExercise -> {
                        Text(
                            text = "مرحله گرم کردن",
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentStep.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentStep.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        // Huge Timer Circle
                        TimerCircle(timeLeft = timeLeft, total = currentStep.durationSeconds, isHang = false)
                    }

                    is WorkoutStep.HangboardSet -> {
                        val isHang = currentStep.isHanging
                        val stateText = if (isHang) "آویزان شوید! 🧗‍♂️" else "استراحت کنید ☕"
                        val stateColor = if (isHang) MaterialTheme.colorScheme.primary else Color(0xFF4CAF50)

                        Text(
                            text = "ست ${currentStep.setNumber} از ${currentStep.totalSets}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stateText,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = stateColor,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "گیره مورد استفاده: ${currentStep.holdSizeText}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        TimerCircle(timeLeft = timeLeft, total = currentStep.durationSeconds, isHang = isHang)
                    }

                    is WorkoutStep.PullupSet -> {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FitnessCenter,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "ست ${currentStep.setNumber} از ${currentStep.totalSets}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = currentStep.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = currentStep.repsText,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = { viewModel.skipStep() },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("انجام شد، ست بعدی")
                                }
                            }
                        }
                    }

                    is WorkoutStep.CooldownStretch -> {
                        Text(
                            text = "مرحله حرکات کششی",
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentStep.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentStep.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        TimerCircle(timeLeft = timeLeft, total = currentStep.durationSeconds, isHang = false)
                    }

                    else -> {}
                }
            }

            // Controls Block
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Exit / Reset
                IconButton(
                    onClick = { viewModel.currentScreen = "HOME" },
                    modifier = Modifier
                        .size(56.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "انصراف")
                }

                // Pause / Play
                if (currentStep is WorkoutStep.WarmupExercise || currentStep is WorkoutStep.HangboardSet || currentStep is WorkoutStep.CooldownStretch) {
                    FilledIconButton(
                        onClick = {
                            if (isTimerRunning) {
                                viewModel.pauseTimer()
                            } else {
                                viewModel.startTimer()
                            }
                        },
                        modifier = Modifier.size(72.dp).testTag("play_pause_button"),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "کنترل تایمر",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                // Skip / Next
                IconButton(
                    onClick = { viewModel.skipStep() },
                    modifier = Modifier
                        .size(56.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.SkipNext, contentDescription = "بعدی")
                }
            }
        }
    }
}

@Composable
fun TimerCircle(timeLeft: Int, total: Int, isHang: Boolean) {
    val progress = if (total > 0) timeLeft.toFloat() / total.toFloat() else 1f
    val strokeColor = if (isHang) MaterialTheme.colorScheme.primary else Color(0xFF4CAF50)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(240.dp)
    ) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            color = strokeColor,
            strokeWidth = 12.dp,
            trackColor = strokeColor.copy(alpha = 0.15f)
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$timeLeft",
                fontSize = 64.sp,
                fontWeight = FontWeight.Black,
                color = strokeColor
            )
            Text(
                text = "ثانیه",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun WorkoutDifficultyQuestionnaire(
    onSelected: (String) -> Unit,
    onCancel: () -> Unit
) {
    var selectedDifficulty by remember { mutableStateOf("NORMAL") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "تبریک 🎉",
            fontSize = 40.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "تمرین امروز را با موفقیت انجام دادی.",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "امروز تمرین چقدر سخت بود؟",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Difficulty Options
        val difficulties = listOf(
            Pair("EASY", "آسان 🟢"),
            Pair("NORMAL", "معمولی 🟡"),
            Pair("HARD", "سخت 🟠"),
            Pair("VERY_HARD", "خیلی سخت 🔴")
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            difficulties.forEach { item ->
                val isSelected = selectedDifficulty == item.first
                Card(
                    onClick = { selectedDifficulty = item.first },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = if (isSelected) borderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = item.second,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { onSelected(selectedDifficulty) },
            modifier = Modifier.fillMaxWidth().height(56.dp).testTag("submit_difficulty_button"),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("ثبت و بازگشت به خانه", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onCancel) {
            Text("انصراف", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun borderStroke(width: androidx.compose.ui.unit.Dp, color: Color) = androidx.compose.foundation.BorderStroke(width, color)
