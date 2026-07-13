package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WorkoutScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.util.NotificationHelper

class MainActivity : ComponentActivity() {

  // Launcher to request runtime notification permission (Android 13+)
  private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { isGranted: Boolean ->
    if (isGranted) {
      // Permission granted, schedule reminder if enabled
    }
  }

  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Request notification permission if needed on Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(
          this,
          Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
    }

    // Pre-create notification channels on start
    NotificationHelper.createNotificationChannel(this)

    setContent {
      val viewModel: MainViewModel = viewModel()
      val profile by viewModel.userProfile.collectAsState()

      // Dynamically respect user theme preferences stored in DB
      MyApplicationTheme(darkTheme = profile.isDarkTheme) {
        // Enforce Persian RTL layouts system-wide
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
          
          val currentScreen = viewModel.currentScreen

          Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
              if (currentScreen != "WORKOUT") {
                CenterAlignedTopAppBar(
                  title = {
                    Text(
                      text = "تمرینیار هنگبرد",
                      fontWeight = FontWeight.ExtraBold,
                      style = MaterialTheme.typography.titleLarge
                    )
                  },
                  colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                  )
                )
              }
            },
            bottomBar = {
              if (currentScreen != "WORKOUT") {
                NavigationBar(
                  tonalElevation = 8.dp
                ) {
                  // HOME Tab
                  NavigationBarItem(
                    selected = currentScreen == "HOME",
                    onClick = { viewModel.currentScreen = "HOME" },
                    label = { Text("خانه") },
                    icon = {
                      Icon(
                        imageVector = if (currentScreen == "HOME") Icons.Default.Home else Icons.Outlined.Home,
                        contentDescription = "خانه"
                      )
                    },
                    modifier = Modifier.testTag("nav_home")
                  )

                  // WORKOUT Tab
                  NavigationBarItem(
                    selected = currentScreen == "WORKOUT_MAIN_TAB", // Custom state to access workout tab directly
                    onClick = {
                      // Instantly load workout screen with current selected day/week
                      viewModel.startWorkout(viewModel.selectedWeek, viewModel.selectedDayIndex)
                    },
                    label = { Text("تمرین امروز") },
                    icon = {
                      Icon(
                        imageVector = if (currentScreen == "WORKOUT") Icons.Default.FitnessCenter else Icons.Outlined.FitnessCenter,
                        contentDescription = "تمرین"
                      )
                    },
                    modifier = Modifier.testTag("nav_workout")
                  )

                  // PROGRESS Tab
                  NavigationBarItem(
                    selected = currentScreen == "PROGRESS",
                    onClick = { viewModel.currentScreen = "PROGRESS" },
                    label = { Text("پیشرفت") },
                    icon = {
                      Icon(
                        imageVector = if (currentScreen == "PROGRESS") Icons.Default.TrendingUp else Icons.Outlined.TrendingUp,
                        contentDescription = "پیشرفت"
                      )
                    },
                    modifier = Modifier.testTag("nav_progress")
                  )

                  // SETTINGS Tab
                  NavigationBarItem(
                    selected = currentScreen == "SETTINGS",
                    onClick = { viewModel.currentScreen = "SETTINGS" },
                    label = { Text("تنظیمات") },
                    icon = {
                      Icon(
                        imageVector = if (currentScreen == "SETTINGS") Icons.Default.Settings else Icons.Outlined.Settings,
                        contentDescription = "تنظیمات"
                      )
                    },
                    modifier = Modifier.testTag("nav_settings")
                  )
                }
              }
            }
          ) { innerPadding ->
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
            ) {
              when (currentScreen) {
                "HOME" -> HomeScreen(viewModel = viewModel)
                "WORKOUT" -> WorkoutScreen(viewModel = viewModel)
                "PROGRESS" -> ProgressScreen(viewModel = viewModel)
                "SETTINGS" -> SettingsScreen(viewModel = viewModel)
                else -> HomeScreen(viewModel = viewModel)
              }
            }
          }
        }
      }
    }
  }
}
