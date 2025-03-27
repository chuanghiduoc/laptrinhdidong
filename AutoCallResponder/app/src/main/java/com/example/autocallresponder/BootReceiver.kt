package com.example.autocallresponder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Khởi động lại dịch vụ nếu được bật
            val sharedPreferences = context.getSharedPreferences("AutoCallResponderPrefs", Context.MODE_PRIVATE)
            val isEnabled = sharedPreferences.getBoolean("isEnabled", false)

            if (isEnabled) {
                // Ghi log
                val mainActivity = context.applicationContext as? MainActivity
                mainActivity?.addLogEntry("Dịch vụ đã được khởi động lại sau khi thiết bị khởi động")
            }
        }
    }
}
