package com.baotrongit.tlucontact

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.baotrongit.tlucontact.databinding.ActivityMainBinding
import com.baotrongit.tlucontact.ui.auth.AuthViewModel
import com.baotrongit.tlucontact.ui.auth.LoginActivity
import com.baotrongit.tlucontact.ui.auth.ProfileActivity
import com.baotrongit.tlucontact.ui.directory.StaffDirectoryActivity
import com.baotrongit.tlucontact.ui.directory.StudentDirectoryActivity
import com.baotrongit.tlucontact.ui.directory.UnitsDirectoryActivity // Import activity danh bạ đơn vị
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.currentUser.observe(this) { user ->
            if (user == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return@observe
            }

            // Cập nhật UI với thông tin người dùng
            binding.tvWelcome.text = "Xin chào, ${user.fullName}"
            binding.tvUserType.text = when (user.userType) {
                com.baotrongit.tlucontact.data.model.UserType.STAFF -> "Cán bộ/Giảng viên"
                com.baotrongit.tlucontact.data.model.UserType.STUDENT -> "Sinh viên"
            }
            binding.tvEmail.text = user.email
            binding.tvStudentId.text = user.profileId

            // Hiển thị số điện thoại (nếu không rỗng)
            if (user.phoneNumber.isNotBlank()) {
                binding.tvPhoneNumber.text = user.phoneNumber
                binding.tvPhoneNumber.visibility = android.view.View.VISIBLE
            } else {
                binding.tvPhoneNumber.visibility = android.view.View.GONE
            }

            if (user.photoURL.isNotEmpty()) {
                Glide.with(this)
                    .load(user.photoURL)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(binding.ivUserAvatar)
            }
        }
    }

    private fun setupListeners() {
        binding.cardUnits.setOnClickListener {
            // Chuyển đến màn hình Danh bạ Đơn vị
            startActivity(Intent(this, UnitsDirectoryActivity::class.java)) // Khởi chạy UnitsDirectoryActivity
        }

        binding.cardStaff.setOnClickListener {
            // Chuyển đến màn hình Danh bạ CBGV
//            Toast.makeText(this, "Chức năng đang phát triển: Danh bạ CBGV", Toast.LENGTH_SHORT).show()
             startActivity(Intent(this, StaffDirectoryActivity::class.java))
        }

//        binding.cardStaff.setOnClickListener {
//            // Kiểm tra quyền truy cập
//            viewModel.currentUser.observe(this) { user ->
//                if (user == null || user.userType == com.baotrongit.tlucontact.data.model.UserType.STUDENT) {
//                    // Nếu là sinh viên hoặc không có người dùng đăng nhập
//                    Toast.makeText(this, "Bạn không có quyền truy cập vào danh bạ CBGV.", Toast.LENGTH_SHORT).show()
//                } else {
//                    // Chuyển đến màn hình Danh bạ CBGV
//                    startActivity(Intent(this, StaffDirectoryActivity::class.java))
//                }
//            }
//        }

        binding.cardStudents.setOnClickListener {
            // Chuyển đến màn hình Danh bạ Sinh viên
//            Toast.makeText(this, "Chức năng đang phát triển: Danh bạ Sinh viên", Toast.LENGTH_SHORT).show()
             startActivity(Intent(this, StudentDirectoryActivity::class.java))
        }

        binding.fabProfile.setOnClickListener {
            // Chuyển đến màn hình Cập nhật thông tin cá nhân
            startActivity(Intent(this, ProfileActivity::class.java)) // Khởi chạy ProfileActivity
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                viewModel.signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
