package com.example.autocallresponder

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var statusTextView: TextView
    private lateinit var messageEditText: EditText
    private lateinit var enableServiceSwitch: Switch
    private lateinit var saveButton: Button
    private lateinit var logTextView: TextView

    private lateinit var sharedPreferences: SharedPreferences

    private val PERMISSIONS_REQUEST_CODE = 100
    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_CALL_LOG
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Khởi tạo các thành phần UI
        statusTextView = findViewById(R.id.statusTextView)
        messageEditText = findViewById(R.id.messageEditText)
        enableServiceSwitch = findViewById(R.id.enableServiceSwitch)
        saveButton = findViewById(R.id.saveButton)
        logTextView = findViewById(R.id.logTextView)

        // Khởi tạo SharedPreferences để lưu cài đặt
        sharedPreferences = getSharedPreferences("AutoCallResponderPrefs", MODE_PRIVATE)

        // Tải cài đặt đã lưu
        loadSettings()

        // Kiểm tra quyền
        checkPermissions()

        // Xử lý sự kiện nút Lưu
        saveButton.setOnClickListener {
            saveSettings()
            Toast.makeText(this, "Đã lưu cài đặt", Toast.LENGTH_SHORT).show()
            addLogEntry("Đã cập nhật cài đặt")
        }

    }

    private fun loadSettings() {
        val message = sharedPreferences.getString("message", "Xin lỗi, tôi đang bận. Tôi sẽ gọi lại cho bạn sau.")
        val isEnabled = sharedPreferences.getBoolean("isEnabled", false)
        val log = sharedPreferences.getString("log", "Chưa có hoạt động nào được ghi nhận.")

        messageEditText.setText(message)
        enableServiceSwitch.isChecked = isEnabled
        logTextView.text = log
    }

    private fun saveSettings() {
        val message = messageEditText.text.toString()
        val isEnabled = enableServiceSwitch.isChecked

        val editor = sharedPreferences.edit()
        editor.putString("message", message)
        editor.putBoolean("isEnabled", isEnabled)
        editor.apply()
    }

    private fun checkPermissions() {
        val permissionsToRequest = ArrayList<String>()

        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSIONS_REQUEST_CODE
            )
            statusTextView.text = "Đang chờ cấp quyền..."
        } else {
            statusTextView.text = "Dịch vụ đã sẵn sàng"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            var allGranted = true

            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false
                    break
                }
            }

            if (allGranted) {
                statusTextView.text = "Dịch vụ đã sẵn sàng"
                addLogEntry("Đã được cấp tất cả quyền cần thiết")
            } else {
                statusTextView.text = "Thiếu quyền - dịch vụ không thể hoạt động"
                addLogEntry("Thiếu quyền cần thiết")
                Toast.makeText(
                    this,
                    "Bạn cần cấp tất cả quyền để ứng dụng hoạt động",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun addLogEntry(entry: String) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val timestamp = dateFormat.format(Date())

        val currentLog = logTextView.text.toString()
        val newLog = if (currentLog == "Chưa có hoạt động nào được ghi nhận.") {
            "[$timestamp] $entry"
        } else {
            "[$timestamp] $entry\n$currentLog"
        }

        logTextView.text = newLog

        // Lưu log vào SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("log", newLog)
        editor.apply()
    }
}
