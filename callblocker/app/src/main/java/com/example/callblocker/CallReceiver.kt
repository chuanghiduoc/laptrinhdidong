package com.example.callblocker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.widget.Toast
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build
import androidx.core.app.NotificationCompat
import java.util.Date

class CallReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "blocked_calls_channel"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                val phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

                if (phoneNumber != null) {
                    // Kiểm tra xem số điện thoại có trong danh sách chặn không
                    val sharedPrefs = context.getSharedPreferences("BlockedNumbers", Context.MODE_PRIVATE)
                    val blockedNumbers = sharedPrefs.getStringSet("numbers", setOf()) ?: setOf()

                    if (blockedNumbers.contains(phoneNumber)) {
                        // Thông báo cuộc gọi bị chặn
                        showBlockedCallNotification(context, phoneNumber)

                        // Trên các thiết bị cũ hơn hoặc với quyền đặc biệt, chúng ta có thể sử dụng:
                         endCall(context)
                    }
                }
            }
        }
    }

    private fun showBlockedCallNotification(context: Context, phoneNumber: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Tạo notification channel cho Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Cuộc gọi bị chặn",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Intent để mở ứng dụng khi nhấn vào thông báo
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Tạo thông báo
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_block)
            .setContentTitle("Cuộc gọi bị chặn")
            .setContentText("Số điện thoại: $phoneNumber")
            .setSubText("Thời gian: ${Date()}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Hiển thị thông báo
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    // Phương thức này chỉ hoạt động trên một số thiết bị cũ hoặc với quyền đặc biệt

    private fun endCall(context: Context): Boolean {
        try {
            val telephonyService = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val telephonyClass = Class.forName(telephonyService.javaClass.name)
            val telephonyObject = telephonyClass.getDeclaredMethod("getITelephony").apply {
                isAccessible = true
            }.invoke(telephonyService)

            val telephonyInterface = Class.forName(telephonyObject.javaClass.name)
            val endCallMethod = telephonyInterface.getDeclaredMethod("endCall")
            endCallMethod.invoke(telephonyObject)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

}
