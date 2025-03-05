package com.baotrongit.tlucontact

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.baotrongit.tlucontact.databinding.ActivityMainBinding
import com.baotrongit.tlucontact.ui.auth.AuthViewModel
import com.baotrongit.tlucontact.ui.auth.LoginActivity
import com.baotrongit.tlucontact.ui.auth.ProfileActivity
import com.baotrongit.tlucontact.ui.directory.StaffDirectoryActivity
import com.baotrongit.tlucontact.ui.directory.StudentDirectoryActivity
import com.baotrongit.tlucontact.ui.directory.UnitsDirectoryActivity
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!viewModel.isUserLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupObservers()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()

        // Refresh user data when returning to MainActivity
        viewModel.refreshUserData()
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
            startActivity(Intent(this, UnitsDirectoryActivity::class.java))
        }

//        binding.cardStaff.setOnClickListener {
//            startActivity(Intent(this, StaffDirectoryActivity::class.java))
//        }
        binding.cardStaff.setOnClickListener {
            val currentUser = viewModel.currentUser.value

            if (currentUser == null || currentUser.userType == com.baotrongit.tlucontact.data.model.UserType.STUDENT) {
                Toast.makeText(this, "Bạn không có quyền truy cập vào danh bạ CBGV.", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, StaffDirectoryActivity::class.java))
            }
        }

        binding.cardStudents.setOnClickListener {
            startActivity(Intent(this, StudentDirectoryActivity::class.java))
        }

        binding.fabProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.btnSettings.setOnClickListener { view ->
            showSettingsMenu(view)
        }
    }

    private fun showSettingsMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.settings_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                R.id.action_settings -> {
                    Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.action_logout -> {
                    viewModel.signOut()
                    true
                }
                else -> false
            }
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            popup.gravity = Gravity.END
//        }

        popup.show()
    }
}

//        binding.cardStaff.setOnClickListener {
//            // Kiểm tra quyền truy cập
//binding.cardStaff.setOnClickListener {
//    // Get the current user value directly from the LiveData
//    val currentUser = viewModel.currentUser.value
//
//    if (currentUser == null || currentUser.userType == com.baotrongit.tlucontact.data.model.UserType.STUDENT) {
//        // If user is a student or not logged in
//        Toast.makeText(this, "Bạn không có quyền truy cập vào danh bạ CBGV.", Toast.LENGTH_SHORT).show()
//    } else {
//        // Navigate to Staff Directory
//        startActivity(Intent(this, StaffDirectoryActivity::class.java))
//    }
//}
