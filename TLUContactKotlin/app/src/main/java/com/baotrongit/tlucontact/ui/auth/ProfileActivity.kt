package com.baotrongit.tlucontact.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.baotrongit.tlucontact.data.model.Staff
import com.baotrongit.tlucontact.data.model.Student
import com.baotrongit.tlucontact.data.model.TLUUnit
import com.baotrongit.tlucontact.data.model.UserType
import com.baotrongit.tlucontact.databinding.ActivityProfileBinding
import com.baotrongit.tlucontact.utils.DataProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val authViewModel: AuthViewModel by viewModels()
    private var unitList: List<TLUUnit> = emptyList()
    private var studentData: Student? = null
    private var staffData: Staff? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        loadUserData()
        setupListeners()
        loadUnitsFromFirebase()
    }

    private fun loadUnitsFromFirebase() {
        lifecycleScope.launch {
            try {
                unitList = DataProvider.getUnits()

                // If student data is already loaded, set up the spinner
                if (studentData != null) {
                    setupSpinner(unitList, studentData?.unitId ?: "")
                } else if (staffData != null) {
                    setupSpinner(unitList, staffData?.unitId ?: "")
                } else if (authViewModel.currentUser.value != null) {
                    // If no student/staff data but user data exists, use the unit from user data
                    setupSpinner(unitList, authViewModel.currentUser.value?.unitId ?: "")
                }
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Error loading units: ${e.message}")
                Toast.makeText(this@ProfileActivity, "Không thể tải danh sách đơn vị", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadUserData() {
        // Load user data from AuthViewModel
        authViewModel.refreshUserData()

        // Also try to load student or staff data if available
        val currentUser = authViewModel.currentUser.value
        if (currentUser != null) {
            if (currentUser.userType == UserType.STUDENT) {
                loadStudentData()
            } else if (currentUser.userType == UserType.STAFF) {
                loadStaffData()
            }
        }
    }

    private fun loadStudentData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Get student email from current user
        val userEmail = currentUser.email
        if (userEmail == null) {
            Toast.makeText(this, "Không tìm thấy email người dùng", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                // Query Firestore for student with matching email
                val querySnapshot = db.collection("students")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .await()

                if (querySnapshot.documents.isEmpty()) {
                    // No student record found - we'll create one later when saving
                    Log.d("ProfileActivity", "No student record found for email: $userEmail")
                    return@launch
                }

                // Get the first matching student document
                val document = querySnapshot.documents[0]
                studentData = Student(
                    id = document.id,
                    fullName = document.getString("fullName") ?: "",
                    studentCode = document.getString("studentId") ?: "",
                    email = document.getString("email") ?: "",
                    phone = document.getString("phone"),
                    address = document.getString("address"),
                    unitId = document.getString("unitId") ?: "",
                    avatarUrl = document.getString("avatarUrl"),
                    classId = document.getString("classId")
                )

                // Update UI with student data which takes precedence over user data
                updateUIWithStudentData()

                // Setup spinner if units are already loaded
                if (unitList.isNotEmpty()) {
                    setupSpinner(unitList, studentData?.unitId ?: "")
                }
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Error loading student data: ${e.message}")
                Toast.makeText(this@ProfileActivity, "Lỗi khi tải thông tin sinh viên", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadStaffData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Get staff email from current user
        val userEmail = currentUser.email
        if (userEmail == null) {
            Toast.makeText(this, "Không tìm thấy email người dùng", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                // Query Firestore for staff with matching email
                val querySnapshot = db.collection("staff")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .await()

                if (querySnapshot.documents.isEmpty()) {
                    // No staff record found - we'll create one later when saving
                    Log.d("ProfileActivity", "No staff record found for email: $userEmail")
                    return@launch
                }

                // Get the first matching staff document
                val document = querySnapshot.documents[0]
                staffData = Staff(
                    id = document.id,
                    fullName = document.getString("fullName") ?: "",
                    position = document.getString("position") ?: "",
                    unitId = document.getString("unitId") ?: "",
                    email = document.getString("email") ?: "",
                    phone = document.getString("phone"),
                    avatarUrl = document.getString("avatarUrl"),
                    staffId = document.getString("staffId") ?: ""
                )


                // Update UI with staff data
                updateUIWithStaffData()

                // Setup spinner if units are already loaded
                if (unitList.isNotEmpty()) {
                    setupSpinner(unitList, staffData?.unitId ?: "")
                }
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Error loading staff data: ${e.message}")
                Toast.makeText(this@ProfileActivity, "Lỗi khi tải thông tin cán bộ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUIWithStudentData() {
        studentData?.let { student ->
            binding.etFullName.setText(student.fullName)
            binding.etPhoneNumber.setText(student.phone ?: "")
            binding.etAddress.setText(student.address ?: "")
            binding.etPosition.visibility = View.GONE // Hide position field for students
            binding.etPhotoUrl.setText(student.avatarUrl ?: "")

            if (!student.avatarUrl.isNullOrEmpty()) {
                loadProfileImage(student.avatarUrl!!)
            }
        }
    }

    private fun updateUIWithStaffData() {
        staffData?.let { staff ->
            binding.etFullName.setText(staff.fullName)
            binding.etPhoneNumber.setText(staff.phone ?: "")
            binding.etPosition.visibility = View.VISIBLE
            binding.etPosition.setText(staff.position)
            binding.etPhotoUrl.setText(staff.avatarUrl ?: "")

            if (!staff.avatarUrl.isNullOrEmpty()) {
                loadProfileImage(staff.avatarUrl!!)
            }
        }
    }

    private fun setupObservers() {
        authViewModel.currentUser.observe(this) { user ->
            if (user != null) {
                // Only update UI from user data if we don't have student/staff data
                if (studentData == null && staffData == null) {
                    binding.etFullName.setText(user.fullName)
                    binding.etPhoneNumber.setText(user.phoneNumber)
                    binding.etAddress.setText(user.address)
                    binding.etPhotoUrl.setText(user.photoURL)

                    if (user.photoURL.isNotEmpty()) {
                        loadProfileImage(user.photoURL)
                    }

                    // Check userType and adjust UI accordingly
                    if (user.userType == UserType.STUDENT) {
                        binding.etPosition.visibility = View.GONE
                    } else {
                        binding.etPosition.visibility = View.VISIBLE
                        binding.etPosition.setText(user.position)
                    }

                    // Setup spinner if units are already loaded
                    if (unitList.isNotEmpty()) {
                        setupSpinner(unitList, user.unitId)
                    }
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

    private fun setupSpinner(units: List<TLUUnit>, currentUnitId: String) {
        val unitOptions = units.map { it.name }.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, unitOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerClass.adapter = adapter

        // Find unit name for currentUnitId
        val unitName = units.find { it.id == currentUnitId }?.name

        // Set spinner position
        if (unitName != null) {
            val index = unitOptions.indexOf(unitName)
            if (index >= 0) {
                binding.spinnerClass.setSelection(index)
            }
        }
    }

    private fun updateProfile() {
        val fullName = binding.etFullName.text.toString().trim()
        val phoneNumber = binding.etPhoneNumber.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val photoUrl = binding.etPhotoUrl.text.toString().trim()
        val position = if (binding.etPosition.visibility == View.VISIBLE) {
            binding.etPosition.text.toString().trim()
        } else {
            ""
        }

        // Get selected unit name from spinner
        val selectedUnitName = binding.spinnerClass.selectedItem?.toString() ?: return
        val unitId = unitList.find { it.name == selectedUnitName }?.id ?: ""

        // First, update the user in the users collection
        val currentUser = authViewModel.currentUser.value
        if (currentUser != null) {
            authViewModel.updateUserProfile(
                fullName,
                phoneNumber,
                address,
                unitId,
                position,
                photoUrl,
                this
            )

            // Then, update the appropriate collection based on user type
            if (currentUser.userType == UserType.STUDENT) {
                updateOrCreateStudentRecord(fullName, phoneNumber, address, unitId, photoUrl)
            } else if (currentUser.userType == UserType.STAFF) {
                updateOrCreateStaffRecord(fullName, position, phoneNumber, address, unitId, photoUrl)
            }
        }
    }

    private fun updateOrCreateStudentRecord(
        fullName: String,
        phoneNumber: String,
        address: String,
        unitId: String,
        photoUrl: String
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val email = currentUser.email ?: return

        lifecycleScope.launch {
            try {
                // Extract student ID from email if possible
                val studentId = extractStudentIdFromEmail(email)

                // Always query Firestore to check if record exists
                val querySnapshot = db.collection("students")
                    .whereEqualTo("email", email)
                    .get()
                    .await()

                if (!querySnapshot.isEmpty) {
                    // Record exists - update it
                    val documentId = querySnapshot.documents[0].id
                    val studentRef = db.collection("students").document(documentId)
                    val updates = hashMapOf<String, Any>(
                        "fullName" to fullName,
                        "phone" to phoneNumber,
                        "address" to address,
                        "unitId" to unitId,
                        "avatarUrl" to photoUrl,
                        "hasAccount" to true
                    )

                    studentRef.update(updates).await()
                    Log.d("ProfileActivity", "Updated existing student record")

                    // Update local data
                    studentData = Student(
                        id = documentId,
                        fullName = fullName,
                        studentCode = studentId,
                        email = email,
                        phone = phoneNumber,
                        address = address,
                        unitId = unitId,
                        avatarUrl = photoUrl,
                        classId = querySnapshot.documents[0].getString("classId")
                    )
                } else {
                    // No record exists - create new one
                    val studentData = hashMapOf(
                        "fullName" to fullName,
                        "studentId" to studentId,
                        "email" to email,
                        "phone" to phoneNumber,
                        "address" to address,
                        "unitId" to unitId,
                        "avatarUrl" to photoUrl,
                        "hasAccount" to true
                    )

                    val result = db.collection("students").add(studentData).await()
                    Log.d("ProfileActivity", "Created new student record with ID: ${result.id}")

                    // Update local data with the new student
                    this@ProfileActivity.studentData = Student(
                        id = result.id,
                        fullName = fullName,
                        studentCode = studentId,
                        email = email,
                        phone = phoneNumber,
                        address = address,
                        unitId = unitId,
                        avatarUrl = photoUrl,
                        classId = null
                    )
                }

                Toast.makeText(this@ProfileActivity, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Error updating student record: ${e.message}")
                Toast.makeText(this@ProfileActivity, "Lỗi khi cập nhật thông tin sinh viên", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateOrCreateStaffRecord(
        fullName: String,
        position: String,
        phoneNumber: String,
        officeLocation: String,
        unitId: String,
        photoUrl: String
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val email = currentUser.email ?: return

        lifecycleScope.launch {
            try {
                // Always query Firestore to check if record exists
                val querySnapshot = db.collection("staff")
                    .whereEqualTo("email", email)
                    .get()
                    .await()

                if (!querySnapshot.isEmpty) {
                    // Record exists - update it
                    val documentId = querySnapshot.documents[0].id
                    val staffRef = db.collection("staff").document(documentId)
                    val updates = hashMapOf<String, Any>(
                        "fullName" to fullName,
                        "position" to position,
                        "phone" to phoneNumber,
                        "officeLocation" to officeLocation,
                        "unitId" to unitId,
                        "avatarUrl" to photoUrl,
                        "hasAccount" to true
                    )

                    staffRef.update(updates).await()
                    Log.d("ProfileActivity", "Updated existing staff record")

                    // Update local data
                    staffData = Staff(
                        id = documentId,
                        fullName = fullName,
                        position = position,
                        email = email,
                        phone = phoneNumber,
                        unitId = unitId,
                        avatarUrl = photoUrl,
                        staffId = querySnapshot.documents[0].getString("staffId") ?: ""
                    )
                } else {
                    // No record exists - create new one
                    val staffId = getStaffIdByEmail(email) ?: ""
                    val staffData = hashMapOf(
                        "fullName" to fullName,
                        "position" to position,
                        "email" to email,
                        "phone" to phoneNumber,
                        "officeLocation" to officeLocation,
                        "unitId" to unitId,
                        "avatarUrl" to photoUrl,
                        "staffId" to staffId,
                        "hasAccount" to true
                    )

                    val result = db.collection("staff").add(staffData).await()
                    Log.d("ProfileActivity", "Created new staff record with ID: ${result.id}")

                    // Update local data with the new staff
                    this@ProfileActivity.staffData = Staff(
                        id = result.id,
                        fullName = fullName,
                        position = position,
                        email = email,
                        phone = phoneNumber,
                        unitId = unitId,
                        avatarUrl = photoUrl,
                        staffId = staffId
                    )
                }

                Toast.makeText(this@ProfileActivity, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Error updating staff record: ${e.message}")
                Toast.makeText(this@ProfileActivity, "Lỗi khi cập nhật thông tin cán bộ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun getStaffIdByEmail(email: String): String? {
        return try {
            val querySnapshot = db.collection("staff")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                querySnapshot.documents[0].getString("staffId")
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("ProfileActivity", "Error getting staff ID by email", e)
            null
        }
    }

    private fun extractStudentIdFromEmail(email: String): String {
        // Example: 2251061900@e.tlu.edu.vn -> 2251061900
        val parts = email.split("@")
        return if (parts.isNotEmpty()) parts[0] else ""
    }



    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            updateProfile()
        }

        binding.ivBackButton.setOnClickListener {
            onBackPressed()
        }

        binding.tvChangePhoto.setOnClickListener {
            binding.etPhotoUrl.requestFocus()
        }

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
