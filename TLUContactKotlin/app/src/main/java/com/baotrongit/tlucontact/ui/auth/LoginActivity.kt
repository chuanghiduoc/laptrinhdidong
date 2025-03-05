package com.baotrongit.tlucontact.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.baotrongit.tlucontact.MainActivity
import com.baotrongit.tlucontact.databinding.ActivityLoginBinding
import com.baotrongit.tlucontact.databinding.DialogForgotPasswordBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.loginResult.observe(this) { result ->
            binding.progressBar.visibility = View.GONE

            result.onSuccess {
                navigateToMainActivity()
            }.onFailure { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.currentUser.observe(this) { user ->
            if (user != null) {
                navigateToMainActivity()
            }
        }

        viewModel.resetPasswordResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(
                    this,
                    "Email đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra hộp thư của bạn.",
                    Toast.LENGTH_LONG
                ).show()
            }.onFailure { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
            }
        }

        // Quan sát dữ liệu profile người dùng
        viewModel.userProfile.observe(this) { result ->
            result.onSuccess { profileData ->
                // Lưu thông tin profile vào SharedPreferences để sử dụng sau này
                saveUserProfileToPreferences(profileData)
            }.onFailure { exception ->
                // Xử lý khi không lấy được thông tin profile
                // Thông thường không cần thông báo lỗi này cho người dùng
                // vì họ vẫn có thể sử dụng ứng dụng với thông tin cơ bản
            }
        }
    }

    private fun saveUserProfileToPreferences(profileData: Any) {
        try {
            val sharedPref = getSharedPreferences("user_profile", MODE_PRIVATE)
            with(sharedPref.edit()) {
                // Chuyển đổi profileData thành JSON hoặc chuỗi để lưu trữ
                // Trong trường hợp thực tế, bạn nên sử dụng thư viện Gson hoặc Moshi
                // để chuyển đổi object thành JSON
                putString("profile_data", profileData.toString())
                apply()
            }
        } catch (e: Exception) {
            // Xử lý ngoại lệ nếu có
        }
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            binding.progressBar.visibility = View.VISIBLE
            viewModel.login(email, password)
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    private fun showForgotPasswordDialog() {
        val dialogBinding = DialogForgotPasswordBinding.inflate(LayoutInflater.from(this))

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnSendEmail.setOnClickListener {
            val email = dialogBinding.etEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                viewModel.sendPasswordResetEmail(email)
                dialog.dismiss()
            } else {
                dialogBinding.tilEmail.error = "Vui lòng nhập email"
            }
        }

        dialog.show()
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
