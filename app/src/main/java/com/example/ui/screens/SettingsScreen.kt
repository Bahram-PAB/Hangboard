package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.MainViewModel

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val profile by viewModel.userProfile.collectAsState()

    // Temporary form states
    var nameState by remember(profile) { mutableStateOf(profile.name) }
    var ageState by remember(profile) { mutableStateOf(profile.age.toString()) }
    var heightState by remember(profile) { mutableStateOf(profile.height.toString()) }
    var weightState by remember(profile) { mutableStateOf(profile.weight.toString()) }
    var reminderEnabledState by remember(profile) { mutableStateOf(profile.reminderEnabled) }
    var reminderHourState by remember(profile) { mutableStateOf(profile.reminderHour.toFloat()) }
    var reminderMinuteState by remember(profile) { mutableStateOf(profile.reminderMinute.toFloat()) }
    var darkThemeState by remember(profile) { mutableStateOf(profile.isDarkTheme) }

    var showResetDialog by remember { mutableStateOf(false) }
    var showSavedMessage by remember { mutableStateOf(false) }

    val uriHandler = LocalUriHandler.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Title
        item {
            Text(
                text = "تنظیمات کاربری",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // 1. Profile Information Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "اطلاعات کاربری صخره‌نورد",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = nameState,
                        onValueChange = { nameState = it },
                        label = { Text("نام و نام خانوادگی") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("settings_name_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = ageState,
                            onValueChange = { ageState = it },
                            label = { Text("سن") },
                            leadingIcon = { Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = heightState,
                            onValueChange = { heightState = it },
                            label = { Text("قد (سانتی‌متر)") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Height, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = weightState,
                        onValueChange = { weightState = it },
                        label = { Text("وزن (کیلوگرم)") },
                        leadingIcon = { Icon(imageVector = Icons.Default.MonitorWeight, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // 2. Preferences Card (Notifications & Theme)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "تنظیمات یادآور و ظاهر برنامه",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Dark Theme Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "حالت تاریک (Dark Mode)")
                        }
                        Switch(
                            checked = darkThemeState,
                            onCheckedChange = { darkThemeState = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Notification Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "اعلان یادآوری روزانه")
                        }
                        Switch(
                            checked = reminderEnabledState,
                            onCheckedChange = { reminderEnabledState = it }
                        )
                    }

                    if (reminderEnabledState) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "ساعت یادآوری: ${reminderHourState.toInt().toString().padStart(2, '0')}:${reminderMinuteState.toInt().toString().padStart(2, '0')}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Sliders for hour & minute to keep it simple and visual
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "ساعت", fontSize = 12.sp, modifier = Modifier.width(40.dp))
                            Slider(
                                value = reminderHourState,
                                onValueChange = { reminderHourState = it },
                                valueRange = 0f..23f,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "دقیقه", fontSize = 12.sp, modifier = Modifier.width(40.dp))
                            Slider(
                                value = reminderMinuteState,
                                onValueChange = { reminderMinuteState = it },
                                valueRange = 0f..59f,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // 3. Save Button
        item {
            Button(
                onClick = {
                    val age = ageState.toIntOrNull() ?: 25
                    val height = heightState.toFloatOrNull() ?: 175f
                    val weight = weightState.toFloatOrNull() ?: 68f
                    viewModel.updateProfile(
                        name = nameState,
                        age = age,
                        height = height,
                        weight = weight,
                        isDark = darkThemeState,
                        reminder = reminderEnabledState,
                        hour = reminderHourState.toInt(),
                        minute = reminderMinuteState.toInt()
                    )
                    showSavedMessage = true
                },
                modifier = Modifier.fillMaxWidth().height(56.dp).testTag("save_settings_button"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("ذخیره تغییرات", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Confirmation Message
        item {
            if (showSavedMessage) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                        Text("تغییرات با موفقیت ذخیره شد.", color = Color(0xFF1B5E20), fontSize = 14.sp)
                    }
                }
            }
        }

        // 4. App Version & Social Links Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "تمرینیار هنگبرد",
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "نسخه برنامه: V0.2.1",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // GitHub Button
                        OutlinedButton(
                            onClick = { uriHandler.openUri("https://github.com/Bahram-PAB/Hangboard") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_github),
                                contentDescription = "گیت‌هاب",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("گیت‌هاب", fontWeight = FontWeight.Bold)
                        }
                        
                        // Telegram Button
                        OutlinedButton(
                            onClick = { uriHandler.openUri("https://t.me/gharibe_ir") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_telegram),
                                contentDescription = "تلگرام",
                                tint = Color(0xFF24A1DE),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("تلگرام", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
