package com.baotrongit.tlucontact.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.baotrongit.tlucontact.databinding.ActivityRegisterBinding
import com.baotrongit.tlucontact.utils.EmailValidationResult
import com.baotrongit.tlucontact.utils.ValidationUtils

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.registrationResult.observe(this) { result ->
            binding.progressBar.visibility = View.GONE

            result.onSuccess {
                Toast.makeText(
                    this,
                    "Đăng ký thành công! Vui lòng kiểm tra email để xác minh tài khoản.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }.onFailure { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupListeners() {
        binding.ivBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }

        binding.btnRegister.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val profileId = binding.etStudentId.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()


            // Kiểm tra hợp lệ trước khi gửi
            if (validateInputs(fullName, profileId, email, password, confirmPassword)) {
                binding.progressBar.visibility = View.VISIBLE
                viewModel.register(email, password, confirmPassword, fullName, profileId)
            }
        }



        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().trim()
                if (email.isNotEmpty()) {
                    when {
                        email.endsWith("@tlu.edu.vn") -> {
                            binding.tilStudentId.hint = "Mã cán bộ"
                        }
                        email.endsWith("@e.tlu.edu.vn") -> {
                            binding.tilStudentId.hint = "Mã sinh viên"
                        }
                        else -> {
                            binding.tilStudentId.hint = "Mã cán bộ/Mã sinh viên"
                        }
                    }
                }
            }
        })
    }

    private fun validateInputs(
        fullName: String,
        studentId: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        // Kiểm tra họ tên
        if (fullName.isBlank()) {
            binding.tilFullName.error = "Họ tên không được để trống"
            return false
        } else {
            binding.tilFullName.error = null
        }

        // Kiểm tra mã sinh viên/cán bộ
        if (studentId.isBlank()) {
            binding.tilStudentId.error = "Mã sinh viên/cán bộ không được để trống"
            return false
        } else {
            binding.tilStudentId.error = null
        }

        // Kiểm tra email
        val emailValidation = ValidationUtils.validateEmail(email)
        when (emailValidation) {
            EmailValidationResult.EMPTY -> {
                binding.tilEmail.error = "Email không được để trống"
                return false
            }
            EmailValidationResult.INVALID_FORMAT -> {
                binding.tilEmail.error = "Định dạng email không hợp lệ"
                return false
            }
            EmailValidationResult.INVALID_DOMAIN -> {
                binding.tilEmail.error = "Email phải thuộc domain @tlu.edu.vn hoặc @e.tlu.edu.vn"
                return false
            }
            else -> binding.tilEmail.error = null
        }

        // Kiểm tra mật khẩu
        if (password.isBlank()) {
            binding.tilPassword.error = "Mật khẩu không được để trống"
            return false
        } else if (password.length < 8) {
            binding.tilPassword.error = "Mật khẩu phải có ít nhất 8 ký tự"
            return false
        } else {
            val hasUppercase = password.any { it.isUpperCase() }
            val hasLowercase = password.any { it.isLowerCase() }
            val hasDigit = password.any { it.isDigit() }
            val hasSpecialChar = password.any { !it.isLetterOrDigit() }

            if (!hasUppercase || !hasLowercase || !hasDigit || !hasSpecialChar) {
                binding.tilPassword.error = "Mật khẩu phải có ít nhất 1 chữ hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt"
                return false
            } else {
                binding.tilPassword.error = null
            }
        }

        // Kiểm tra xác nhận mật khẩu
        if (confirmPassword != password) {
            binding.tilConfirmPassword.error = "Mật khẩu xác nhận không khớp"
            return false
        } else {
            binding.tilConfirmPassword.error = null
        }

        return true
    }
}
