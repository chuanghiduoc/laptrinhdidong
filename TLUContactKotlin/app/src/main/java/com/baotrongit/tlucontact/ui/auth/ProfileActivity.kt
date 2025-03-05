package com.baotrongit.tlucontact.ui.auth

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.baotrongit.tlucontact.data.model.UnitType
import com.baotrongit.tlucontact.databinding.ActivityProfileBinding
import com.baotrongit.tlucontact.utils.DataProvider
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

                // Setup spinner after user data is loaded
                setupSpinner(user.unitId)
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

    private fun setupSpinner(currentUnitId: String) {
        // Get all faculties from DataProvider
        val units = DataProvider.getUnits()

        // Create a list of faculty names to display in the spinner
        val unitOptions = units
            .filter { it.type == UnitType.FACULTY } // Only show faculties
            .map { it.name }
            .toTypedArray()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, unitOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerClass.adapter = adapter

        // Find the unit name for the current unitId
        val unitName = units.find { it.id == currentUnitId }?.name

        // Find the index of this unit name in the options and set the spinner
        if (unitName != null) {
            val index = unitOptions.indexOf(unitName)
            if (index >= 0) {
                binding.spinnerClass.setSelection(index)
            }
        }
    }

    private fun updateUserProfile() {
        val fullName = binding.etFullName.text.toString().trim()
        val phoneNumber = binding.etPhoneNumber.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()

        // Get the selected unit name from spinner
        val selectedUnitName = binding.spinnerClass.selectedItem.toString()

        // Find the unit ID for this name
        val unitId = DataProvider.getUnits()
            .filter { it.type == UnitType.FACULTY }
            .find { it.name == selectedUnitName }?.id ?: ""

        val position = binding.etPosition.text.toString().trim()
        val photoUrl = binding.etPhotoUrl.text.toString().trim()

        // Call the ViewModel to update the profile with the unitId
        viewModel.updateUserProfile(fullName, phoneNumber, address, unitId, position, photoUrl, this)
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            updateUserProfile()
        }

        binding.ivBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
