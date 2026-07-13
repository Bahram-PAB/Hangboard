package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.util.NotificationHelper

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationHelper.showNotification(
            context = context,
            title = "تمرینیار هنگبرد 💪",
            message = "وقت تمرین امروز رسید. بریم انگشتامون رو قوی‌تر کنیم!"
        )
    }
}
