package com.example.firebaseauthdemo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseauthdemo.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo Firebase Auth
        auth = Firebase.auth

        // Kiểm tra nếu người dùng đã đăng nhập
        val currentUser = auth.currentUser
        if (currentUser != null) {
            binding.tvStatus.text = "Đã đăng nhập: ${currentUser.email}"
        }

        // Thiết lập sự kiện click cho các nút
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Xử lý sự kiện đăng nhập
        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateForm(email, password)) {
                showProgress(true)
                signIn(email, password)
            }
        }

        // Xử lý sự kiện đăng ký
        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateForm(email, password)) {
                showProgress(true)
                signUp(email, password)
            }
        }

        // Xử lý sự kiện hiển thị dữ liệu
        binding.btnShowData.setOnClickListener {
            if (auth.currentUser != null) {
                startActivity(Intent(this, DataActivity::class.java))
            } else {
                Toast.makeText(this, "Bạn cần đăng nhập trước!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        var valid = true

        if (email.isEmpty()) {
            binding.tilEmail.error = "Vui lòng nhập email"
            valid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Email không hợp lệ"
            valid = false
        } else {
            binding.tilEmail.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = "Vui lòng nhập mật khẩu"
            valid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Mật khẩu phải có ít nhất 6 ký tự"
            valid = false
        } else {
            binding.tilPassword.error = null
        }

        return valid
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                showProgress(false)
                if (task.isSuccessful) {
                    // Đăng nhập thành công
                    val user = auth.currentUser
                    binding.tvStatus.text = "Đã đăng nhập: ${user?.email}"
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

                    // Lưu thông tin đăng nhập vào Realtime Database
                    saveUserLoginInfo(user?.uid ?: "", user?.email ?: "")
                } else {
                    // Đăng nhập thất bại
                    binding.tvStatus.text = "Đăng nhập thất bại"
                    Toast.makeText(this, "Đăng nhập thất bại: ${task.exception?.message}",
                        Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                showProgress(false)
                if (task.isSuccessful) {
                    // Đăng ký thành công
                    val user = auth.currentUser
                    binding.tvStatus.text = "Đã đăng ký: ${user?.email}"
                    Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()

                    // Lưu thông tin người dùng vào Realtime Database
                    saveUserInfo(user?.uid ?: "", user?.email ?: "")
                } else {
                    // Đăng ký thất bại
                    binding.tvStatus.text = "Đăng ký thất bại"
                    Toast.makeText(this, "Đăng ký thất bại: ${task.exception?.message}",
                        Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun saveUserInfo(userId: String, email: String) {
        val database = Firebase.database
        val userRef = database.getReference("users").child(userId)

        val user = HashMap<String, Any>()
        user["email"] = email
        user["createdAt"] = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            .format(Date())

        userRef.setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Lưu thông tin người dùng thành công",
                    Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserLoginInfo(userId: String, email: String) {
        val database = Firebase.database
        val userRef = database.getReference("users").child(userId)

        val loginInfo = HashMap<String, Any>()
        loginInfo["lastLogin"] = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            .format(Date())
        loginInfo["email"] = email

        userRef.updateChildren(loginInfo)
            .addOnSuccessListener {
                Toast.makeText(this, "Cập nhật thông tin đăng nhập thành công",
                    Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showProgress(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
}
