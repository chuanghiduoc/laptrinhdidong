package com.baotrongit.tlucontact.ui.auth

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baotrongit.tlucontact.data.model.User
import com.baotrongit.tlucontact.data.repository.AuthRepository
import com.baotrongit.tlucontact.utils.EmailValidationResult
import com.baotrongit.tlucontact.utils.PasswordValidationResult
import com.baotrongit.tlucontact.utils.UserManager
import com.baotrongit.tlucontact.utils.ValidationUtils
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _registrationResult = MutableLiveData<Result<Unit>>()
    val registrationResult: LiveData<Result<Unit>> = _registrationResult

    private val _loginResult = MutableLiveData<Result<Unit>>()
    val loginResult: LiveData<Result<Unit>> = _loginResult

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _userProfile = MutableLiveData<Result<Any>>()
    val userProfile: LiveData<Result<Any>> = _userProfile

    private val _resetPasswordResult = MutableLiveData<Result<Unit>>()
    val resetPasswordResult: LiveData<Result<Unit>> = _resetPasswordResult

    init {
        // Kiểm tra nếu người dùng đã đăng nhập
        val firebaseUser = authRepository.getCurrentUser()
        if (firebaseUser != null) {
            viewModelScope.launch {
                val userResult = authRepository.getUserData(firebaseUser.uid)
                userResult.onSuccess { user ->
                    _currentUser.value = user
                    // Lấy thông tin profile chi tiết
                    UserManager.initialize(user)

                    loadUserProfile(user)
                }.onFailure {
                    // Xử lý lỗi nếu không lấy được thông tin người dùng
                }
            }
        }
    }

    fun register(email: String, password: String, confirmPassword: String, fullName: String, profileId: String
    ) {
        // Kiểm tra định dạng email
        val emailValidation = ValidationUtils.validateEmail(email)
        if (emailValidation == EmailValidationResult.EMPTY) {
            _registrationResult.value = Result.failure(Exception("Email không được để trống"))
            return
        }

        if (emailValidation == EmailValidationResult.INVALID_FORMAT) {
            _registrationResult.value = Result.failure(Exception("Định dạng email không hợp lệ"))
            return
        }

        if (emailValidation == EmailValidationResult.INVALID_DOMAIN) {
            _registrationResult.value = Result.failure(Exception("Email phải thuộc domain @tlu.edu.vn hoặc @e.tlu.edu.vn"))
            return
        }

        // Kiểm tra mật khẩu
        val passwordValidation = ValidationUtils.validatePassword(password)
        if (passwordValidation == PasswordValidationResult.EMPTY) {
            _registrationResult.value = Result.failure(Exception("Mật khẩu không được để trống"))
            return
        }

        if (passwordValidation == PasswordValidationResult.TOO_SHORT) {
            _registrationResult.value = Result.failure(Exception("Mật khẩu phải có ít nhất 8 ký tự"))
            return
        }

        if (passwordValidation == PasswordValidationResult.TOO_WEAK) {
            _registrationResult.value = Result.failure(Exception("Mật khẩu phải có ít nhất 1 chữ hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt"))
            return
        }

        // Kiểm tra xác nhận mật khẩu
        if (!ValidationUtils.doPasswordsMatch(password, confirmPassword)) {
            _registrationResult.value = Result.failure(Exception("Mật khẩu xác nhận không khớp"))
            return
        }

        // Kiểm tra họ tên
        if (fullName.isBlank()) {
            _registrationResult.value = Result.failure(Exception("Họ tên không được để trống"))
            return
        }

        // Kiểm tra mã sinh viên/cán bộ
        if (profileId.isBlank()) {
            _registrationResult.value = Result.failure(Exception("Mã sinh viên/cán bộ không được để trống"))
            return
        }

        viewModelScope.launch {
            val result = authRepository.registerUser(email, password, confirmPassword ,fullName, profileId)
            result.onSuccess {
                _registrationResult.value = Result.success(Unit)
            }.onFailure {
                _registrationResult.value = Result.failure(it)
            }
        }
    }



    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginResult.value = Result.failure(Exception("Email và mật khẩu không được để trống"))
            return
        }

        viewModelScope.launch {
            val result = authRepository.loginUser(email, password)
            result.onSuccess { firebaseUser ->
                val userResult = authRepository.getUserData(firebaseUser.uid)
                userResult.onSuccess { user ->
                    _currentUser.value = user
                    UserManager.initialize(user)

                    // Lấy thông tin profile chi tiết
                    loadUserProfile(user)
                    _loginResult.value = Result.success(Unit)
                }.onFailure {
                    _loginResult.value = Result.failure(it)
                }
            }.onFailure {
                _loginResult.value = Result.failure(it)
            }
        }
    }

    private fun loadUserProfile(user: User) {
        viewModelScope.launch {
            val profileResult = authRepository.getUserProfileData(user)
            _userProfile.value = profileResult
        }
    }

    fun sendPasswordResetEmail(email: String) {
        if (email.isBlank()) {
            _resetPasswordResult.value = Result.failure(Exception("Email không được để trống"))
            return
        }

        val emailValidation = ValidationUtils.validateEmail(email)
        if (emailValidation == EmailValidationResult.INVALID_FORMAT) {
            _resetPasswordResult.value = Result.failure(Exception("Định dạng email không hợp lệ"))
            return
        }

        viewModelScope.launch {
            val result = authRepository.sendPasswordResetEmail(email)
            _resetPasswordResult.value = result
        }
    }

    fun refreshUserData() {
        val firebaseUser = authRepository.getCurrentUser()
        if (firebaseUser != null) {
            viewModelScope.launch {
                val userResult = authRepository.getUserData(firebaseUser.uid)
                userResult.onSuccess { user ->
                    _currentUser.value = user
                    UserManager.initialize(user)

                    loadUserProfile(user)
                }.onFailure {
                    // Xử lý lỗi
                }
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _currentUser.value = null
        _userProfile.value = null
        UserManager.clear()
    }
    fun updateUserProfile(
        fullName: String,
        phoneNumber: String,
        address: String,
        unitId: String,
        position: String,
        photoUrl: String,
        context: Context
    ) {
        val currentUser = currentUser.value ?: return

        // Cập nhật các trường cần thiết
        val updatedUser = currentUser.copy(
            fullName = fullName,
            phoneNumber = phoneNumber,
            address = address,
            unitId = unitId,
            position = position,
            photoURL = photoUrl
        )

        viewModelScope.launch {
            val result = authRepository.updateUserProfile(updatedUser)
            result.onSuccess {
                // Cập nhật lại User trong LiveData
                _currentUser.value = updatedUser
                Toast.makeText(context, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show()
            }.onFailure {
                // Xử lý lỗi
                Toast.makeText(context, "Cập nhật thông tin thất bại: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun isUserLoggedIn(): Boolean {
        return authRepository.getCurrentUser() != null
    }
}
