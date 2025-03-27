package com.example.autocallresponder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log

class PhoneCallReceiver : BroadcastReceiver() {

    companion object {
        private val TAG = "PhoneCallReceiver"
        private val callStartTimeMap = HashMap<String, Long>()
        private val answeredCalls = HashSet<String>()
    }

    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        val phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

        // Lấy trạng thái dịch vụ từ SharedPreferences
        val sharedPreferences = context.getSharedPreferences("AutoCallResponderPrefs", Context.MODE_PRIVATE)
        val isEnabled = sharedPreferences.getBoolean("isEnabled", false)

        // Nếu dịch vụ không được bật, không làm gì cả
        if (!isEnabled) return

        when (state) {
            TelephonyManager.EXTRA_STATE_RINGING -> {
                if (phoneNumber != null) {
                    // Cuộc gọi đến, lưu thời điểm bắt đầu
                    callStartTimeMap[phoneNumber] = System.currentTimeMillis()

                    // Ghi log
                    saveLog(context, "Cuộc gọi đến từ $phoneNumber")
                }
            }

            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                if (phoneNumber != null) {
                    // Cuộc gọi được trả lời
                    answeredCalls.add(phoneNumber)

                    // Ghi log
                    saveLog(context, "Đã trả lời cuộc gọi từ $phoneNumber")
                }
            }

            TelephonyManager.EXTRA_STATE_IDLE -> {
                // Xử lý cuộc gọi kết thúc
                if (phoneNumber != null) {
                    handleCallEnded(context, phoneNumber)
                } else {
                    // Khi IDLE mà không có số điện thoại, xử lý tất cả các cuộc gọi đang chờ
                    handleAllPendingCalls(context)
                }
            }
        }
    }

    private fun handleCallEnded(context: Context, phoneNumber: String) {
        val startTime = callStartTimeMap[phoneNumber]

        if (startTime != null) {
            val callDuration = System.currentTimeMillis() - startTime

            // Nếu cuộc gọi không được trả lời
            if (!answeredCalls.contains(phoneNumber)) {
                // Gửi tin nhắn SMS
                sendSMS(context, phoneNumber)
            }

            // Xóa số điện thoại khỏi các collections
            callStartTimeMap.remove(phoneNumber)
            answeredCalls.remove(phoneNumber)

            // Ghi log
            saveLog(context, "Cuộc gọi từ $phoneNumber kết thúc sau $callDuration ms")
        }
    }

    private fun handleAllPendingCalls(context: Context) {
        for (number in callStartTimeMap.keys.toSet()) {
            val startTime = callStartTimeMap[number]
            if (startTime != null) {
                val callDuration = System.currentTimeMillis() - startTime

                // Nếu cuộc gọi không được trả lời
                if (!answeredCalls.contains(number)) {
                    // Gửi tin nhắn SMS
                    sendSMS(context, number)
                }

                // Xóa số điện thoại khỏi các collections
                callStartTimeMap.remove(number)
                answeredCalls.remove(number)

                // Ghi log
                saveLog(context, "Cuộc gọi từ $number kết thúc sau $callDuration ms")
            }
        }
    }

    private fun saveLog(context: Context, entry: String) {
        try {
            val sharedPreferences = context.getSharedPreferences("AutoCallResponderPrefs", Context.MODE_PRIVATE)
            val currentLog = sharedPreferences.getString("log", "Chưa có hoạt động nào được ghi nhận.") ?: ""
            val timestamp = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault())
                .format(java.util.Date())

            val newLog = if (currentLog == "Chưa có hoạt động nào được ghi nhận.") {
                "[$timestamp] $entry"
            } else {
                "[$timestamp] $entry\n$currentLog"
            }

            sharedPreferences.edit().putString("log", newLog).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving log", e)
        }
    }

    private fun sendSMS(context: Context, phoneNumber: String) {
        try {
            // Lấy tin nhắn từ SharedPreferences
            val sharedPreferences = context.getSharedPreferences("AutoCallResponderPrefs", Context.MODE_PRIVATE)
            val message = sharedPreferences.getString("message",
                "Xin lỗi, tôi đang bận. Tôi sẽ gọi lại cho bạn sau.")

            // Sử dụng Handler để delay việc gửi SMS một chút
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    // Gửi SMS
                    val smsManager = SmsManager.getDefault()
                    smsManager.sendTextMessage(phoneNumber, null, message, null, null)

                    // Ghi log
                    saveLog(context, "Đã gửi tin nhắn đến $phoneNumber")
                } catch (e: Exception) {
                    saveLog(context, "Lỗi khi gửi tin nhắn đến $phoneNumber: ${e.message}")
                }
            }, 500) // Delay 0.5 giây

        } catch (e: Exception) {
            // Xử lý lỗi
            saveLog(context, "Lỗi khi chuẩn bị gửi tin nhắn đến $phoneNumber: ${e.message}")
        }
    }
}
