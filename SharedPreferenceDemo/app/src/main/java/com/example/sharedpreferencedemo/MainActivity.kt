package com.example.sharedpreferencedemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSave: Button
    private lateinit var btnClear: Button
    private lateinit var btnShow: Button
    private lateinit var tvResult: TextView

    private lateinit var preferenceHelper: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Khởi tạo PreferenceHelper
        preferenceHelper = PreferenceHelper(this)

        // Ánh xạ các thành phần UI
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnSave = findViewById(R.id.btnSave)
        btnClear = findViewById(R.id.btnClear)
        btnShow = findViewById(R.id.btnShow)
        tvResult = findViewById(R.id.tvResult)

        // Thiết lập sự kiện cho nút Lưu
        btnSave.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Lưu thông tin vào SharedPreferences
            preferenceHelper.saveUserCredentials(username, password)
            Toast.makeText(this, "Đã lưu thông tin thành công", Toast.LENGTH_SHORT).show()

            // Xóa nội dung trong EditText
            etUsername.text.clear()
            etPassword.text.clear()
        }

        // Thiết lập sự kiện cho nút Xóa
        btnClear.setOnClickListener {
            preferenceHelper.clearUserCredentials()
            tvResult.text = ""
            Toast.makeText(this, "Đã xóa thông tin", Toast.LENGTH_SHORT).show()
        }

        // Thiết lập sự kiện cho nút Hiển thị
        btnShow.setOnClickListener {
            if (preferenceHelper.hasUserCredentials()) {
                val username = preferenceHelper.getUsername()
                val password = preferenceHelper.getPassword()
                tvResult.text = "Tên người dùng: $username\nMật khẩu: $password"
            } else {
                tvResult.text = "Không có thông tin người dùng"
                Toast.makeText(this, "Không có thông tin để hiển thị", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
