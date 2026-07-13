package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WorkoutSession
import com.example.ui.MainViewModel
import com.example.ui.theme.GripDoneGreen
import com.example.ui.theme.GripMissedRed
import com.example.ui.theme.GripRestGray

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val sessions by viewModel.allSessions.collectAsState()

    // Calculations
    val completedCount = sessions.count { it.status == "COMPLETED" }
    val progressPercent = if (sessions.isNotEmpty()) (completedCount * 100) / sessions.size else 0

    // Today's details
    val todayWeek = viewModel.selectedWeek
    val todayDayIndex = viewModel.selectedDayIndex
    val todayDayName = viewModel.getDayName(todayDayIndex)

    val monthNum = when (todayWeek) {
        in 1..4 -> 1
        in 5..8 -> 2
        else -> 3
    }

    val workoutDetails = when (monthNum) {
        1 -> "۶ ست هنگبرد (۸ ثانیه آویزان، ۶۰ ثانیه استراحت) از گیره‌های بزرگ. بدون بارفیکس."
        2 -> {
            val pullupText = if (todayDayIndex == 0) "۲ ست بارفیکس نیمه" else if (todayDayIndex == 2) "۳ ست بارفیکس نیمه" else "بدون بارفیکس"
            "۷ ست هنگبرد (۱۲ ثانیه آویزان، ۴۵ ثانیه استراحت) از گیره‌های متوسط. بارفیکس: $pullupText."
        }
        else -> {
            val pullupText = if (todayDayIndex == 0) "۳ ست بارفیکس کامل" else if (todayDayIndex == 2) "۴ ست بارفیکس کامل" else "فقط هنگبرد"
            "۸ ست هنگبرد (۱۵ ثانیه آویزان، ۴۵ ثانیه استراحت) از گیره‌های کوچک. بارفیکس: $pullupText."
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // 1. Welcome Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "سلام ${profile.name} 👋",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "آماده‌ای برای قوی‌تر کردن انگشتات؟ 💪",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // 2. Program Progress Summary
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "پیشرفت کل دوره ۱۲ هفته‌ای",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$progressPercent%",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { progressPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "جلسات تکمیل شده: $completedCount از ۳۶",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "هفته فعلی: $todayWeek",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // 3. Today's Training Action
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "تمرین پيشنهادی امروز",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "هفته $todayWeek - روز $todayDayName",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = workoutDetails,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.startWorkout(todayWeek, todayDayIndex) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("start_workout_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "شروع تمرین امروز",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 4. Interactive Training Calendar Grid Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "تقویم برنامه ۱۲ هفته‌ای",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LegendItem("انجام شده", GripDoneGreen)
                    LegendItem("ناموفق", GripMissedRed)
                    LegendItem("باقی‌مانده", GripRestGray)
                }
            }
        }

        // 5. Training Calendar Grid List
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (w in 1..12) {
                    val isCurrentWeek = (w == todayWeek)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (isCurrentWeek) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(vertical = 6.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "هفته $w",
                            fontWeight = if (isCurrentWeek) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp,
                            modifier = Modifier.width(60.dp),
                            color = if (isCurrentWeek) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Render 3 Days (Sat, Mon, Wed)
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            for (d in 0..2) {
                                val session = sessions.find { it.week == w && it.dayIndex == d }
                                val dayName = when (d) {
                                    0 -> "شنب"
                                    1 -> "دوش"
                                    else -> "چها"
                                }

                                val statusColor = when (session?.status) {
                                    "COMPLETED" -> GripDoneGreen
                                    "MISSED" -> GripMissedRed
                                    else -> GripRestGray.copy(alpha = 0.3f)
                                }

                                val textColor = when (session?.status) {
                                    "COMPLETED", "MISSED" -> Color.White
                                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 4.dp)
                                        .height(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(statusColor)
                                        .clickable {
                                            viewModel.startWorkout(w, d)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$dayName (${session?.difficulty?.let { getShortDiff(it) } ?: ""})".trimEnd(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(text: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = text,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

private fun getShortDiff(diff: String): String {
    return when (diff) {
        "EASY" -> "🟢"
        "NORMAL" -> "🟡"
        "HARD" -> "🟠"
        "VERY_HARD" -> "🔴"
        else -> ""
    }
}
