package com.example.firebaseauthdemo

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseauthdemo.databinding.ActivityDataBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDataBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Thiết lập toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Khởi tạo Firebase Auth
        auth = Firebase.auth

        // Hiển thị thông tin người dùng
        displayUserInfo()

        // Lấy dữ liệu từ Firebase Realtime Database
        fetchUserData()

        // Thiết lập sự kiện đăng xuất
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            finish()
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayUserInfo() {
        val user = auth.currentUser
        if (user != null) {
            binding.tvUserEmail.text = user.email
            binding.tvUserId.text = user.uid
        } else {
            finish()
            Toast.makeText(this, "Không có thông tin người dùng", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchUserData() {
        val user = auth.currentUser ?: return

        showProgress(true)

        val database = Firebase.database
        val userRef = database.getReference("users").child(user.uid)

        userRef.get().addOnCompleteListener { task ->
            showProgress(false)

            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot.exists()) {
                    val userData = snapshot.value as Map<*, *>?

                    // Hiển thị thông tin chi tiết ngay lập tức
                    val dataBuilder = StringBuilder()
                    userData?.forEach { (key, value) ->
                        dataBuilder.append("$key: $value\n")
                    }

                    binding.tvDatabaseData.text = dataBuilder.toString()

                    // Hiển thị thời gian đăng nhập cuối
                    val lastLogin = userData?.get("lastLogin") as? String
                    binding.tvLoginTime.text = lastLogin ?: "Chưa có thông tin"
                } else {
                    binding.tvDatabaseData.text = "Không có dữ liệu"
                }
            } else {
                binding.tvDatabaseData.text = "Lỗi: ${task.exception?.message}"
                Toast.makeText(this, "Lỗi tải dữ liệu: ${task.exception?.message}",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun showProgress(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
