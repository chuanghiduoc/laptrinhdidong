package com.baotrongit.tlucontact.ui.auth

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.baotrongit.tlucontact.databinding.ActivityProfileBinding
import com.bumptech.glide.Glide

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        loadUserData()
        setupSpinner()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.currentUser.observe(this) { user ->
            if (user != null) {
                binding.etFullName.setText(user.fullName)
                binding.etPhoneNumber.setText(user.phoneNumber)
                binding.etAddress.setText(user.address)
                binding.etPosition.setText(user.position)
                binding.etPhotoUrl.setText(user.photoURL)
                if (user.photoURL.isNotEmpty()) {
                    loadProfileImage(user.photoURL)
                }
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadProfileImage(url: String) {
        if (url.isNotEmpty()) {
            Glide.with(this)
                .load(url)
                .placeholder(com.baotrongit.tlucontact.R.drawable.ic_person)
                .error(com.baotrongit.tlucontact.R.drawable.ic_person)
                .into(binding.ivProfileImage)
        }
    }

    private fun loadUserData() {
        // Gọi phương thức trong ViewModel để lấy thông tin người dùng
        viewModel.refreshUserData()
    }

    private fun setupSpinner() {
        // Ví dụ danh sách lớp có thể được thay thế bằng danh sách từ Firestore
        val classOptions = arrayOf("Lớp 1", "Lớp 2", "Lớp 3") // Thay thế bằng danh sách lớp thực tế
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, classOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerClass.adapter = adapter

        // Thiết lập giá trị chọn cho spinner nếu có
        // Có thể lấy từ user hoặc để trống nếu chưa có
    }

    private fun updateUserProfile() {
        val fullName = binding.etFullName.text.toString().trim()
        val phoneNumber = binding.etPhoneNumber.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val classId = binding.spinnerClass.selectedItem.toString() // Lấy lớp từ spinner
        val position = binding.etPosition.text.toString().trim()
        val photoUrl = binding.etPhotoUrl.text.toString().trim()

        // Gọi phương thức trong ViewModel để cập nhật thông tin
        viewModel.updateUserProfile(fullName, phoneNumber, address, classId, position, photoUrl, this)
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            updateUserProfile()
        }

        binding.ivBackButton.setOnClickListener {
            onBackPressed()
        }

        binding.tvChangePhoto.setOnClickListener {
            // Khi người dùng nhấp vào "Thay đổi ảnh đại diện", tập trung vào trường URL
            binding.etPhotoUrl.requestFocus()
        }

        // Thêm listener cho trường URL ảnh để tải và hiển thị ảnh khi người dùng nhập xong
        binding.etPhotoUrl.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val url = binding.etPhotoUrl.text.toString().trim()
                if (url.isNotEmpty()) {
                    loadProfileImage(url)
                }
            }
        }
    }
}