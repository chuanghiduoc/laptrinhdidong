package com.baotrongit.tlucontact.data.repository

import android.util.Log
import com.baotrongit.tlucontact.data.model.Student
import com.baotrongit.tlucontact.data.model.User
import com.baotrongit.tlucontact.data.model.UserType
import com.baotrongit.tlucontact.utils.EmailValidationResult
import com.baotrongit.tlucontact.utils.ValidationUtils
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")
    private val staffCollection = firestore.collection("staff")
    private val studentsCollection = firestore.collection("students")

    /**
     * Đăng ký người dùng mới.
     */
// Hàm kiểm tra định dạng email theo domain của trường
    fun isValidEmail(email: String): Boolean {
        val teacherRegex = Regex("^[a-zA-Z0-9._%+-]+@tlu\\.edu\\.vn$")
        val studentRegex = Regex("^[a-zA-Z0-9._%+-]+@e\\.tlu\\.edu\\.vn$")
        return teacherRegex.matches(email) || studentRegex.matches(email)
    }

    suspend fun registerUser(
        email: String,
        password: String,
        confirmPassword: String,
        fullName: String,
        profileId: String? = null // Thêm tham số profileId
    ): Result<FirebaseUser> {
        return try {
            // 1. Kiểm tra định dạng email
            if (!isValidEmail(email)) {
                return Result.failure(Exception("Email không hợp lệ. Vui lòng sử dụng email do trường cung cấp."))
            }

            // 2. Kiểm tra mật khẩu và xác nhận mật khẩu
            if (password.trim() != confirmPassword.trim()) {
                return Result.failure(Exception("Mật khẩu và xác nhận mật khẩu không khớp."))
            }
            if (password.length < 6) { // Quy định về độ mạnh mật khẩu (tối thiểu 6 ký tự)
                return Result.failure(Exception("Mật khẩu phải có ít nhất 6 ký tự."))
            }

            // 3. Xác định userType dựa trên định dạng email
            val userType = when {
                email.endsWith("@tlu.edu.vn", ignoreCase = true) -> UserType.STAFF
                email.endsWith("@e.tlu.edu.vn", ignoreCase = true) -> UserType.STUDENT
                else -> return Result.failure(Exception("Email không hợp lệ."))
            }

            // 4. Tạo tài khoản mới trên Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Đăng ký thất bại")

            // 5. Gửi email xác minh
            firebaseUser.sendEmailVerification().await()

            // 6. Tạo đối tượng người dùng để lưu vào Firestore (bảng users)
            val userObj = User(
                uid = firebaseUser.uid,
                email = email,
                fullName = fullName,
                photoURL = "",       // Có thể cập nhật sau nếu có ảnh đại diện
                phoneNumber = "",    // Có thể cập nhật sau nếu cần
                address = "",
                userType = userType,
                isEmailVerified = false,
                profileId = profileId ?: "",
                createdAt = Timestamp.now(),
                lastUpdated = Timestamp.now()
            )

            // Lưu thông tin vào bảng users
            usersCollection.document(firebaseUser.uid).set(userObj).await()

            // 7. Trả về kết quả thành công
            Result.success(firebaseUser)
        } catch (e: Exception) {
            Log.e("AuthRepository", "registerUser error: ${e.message}")
            Result.failure(e)
        }
    }



    /**
     * Đăng nhập người dùng bằng email và mật khẩu.
     */
    suspend fun loginUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            // Đăng nhập với email và mật khẩu
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("Đăng nhập thất bại"))

            Result.success(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Lấy thông tin người dùng hiện tại.
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Đăng xuất người dùng hiện tại.
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Gửi email đặt lại mật khẩu đến địa chỉ đã nhập.
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Lấy thông tin người dùng từ Firestore.
     */
    suspend fun getUserData(userId: String): Result<User> {
        return try {
            val document = usersCollection.document(userId).get().await()
            val user = document.toObject(User::class.java)

            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Không tìm thấy thông tin người dùng"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Lấy thông tin chi tiết của người dùng (staff hoặc student)
     */
    suspend fun getUserProfileData(user: User): Result<Any> {
        return try {
            if (user.userType == UserType.STAFF) {
                val staffDoc = staffCollection.document(user.profileId).get().await()
                val staffData = staffDoc.data
                if (staffData != null) {
                    Result.success(staffData)
                } else {
                    Result.failure(Exception("Không tìm thấy thông tin cán bộ/giảng viên"))
                }
            } else {
                val studentDoc = studentsCollection.document(user.profileId).get().await()
                val studentData = studentDoc.data
                if (studentData != null) {
                    Result.success(studentData)
                } else {
                    Result.failure(Exception("Không tìm thấy thông tin sinh viên"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createUserProfile(user: User): Result<Unit> {
        return try {
            if (user.userType == UserType.STAFF) {
                val staffData = hashMapOf(
                    "staffId" to user.profileId,
                    "userId" to user.uid,
                    "fullName" to user.fullName,
                    "email" to user.email
                )
                staffCollection.document(user.profileId).set(staffData).await()
            } else {
                val studentData = hashMapOf(
                    "studentId" to user.profileId,
                    "userId" to user.uid,
                    "fullName" to user.fullName,
                    "email" to user.email
                )
                studentsCollection.document(user.profileId).set(studentData).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(user: User): Result<Unit> {
        return try {
            // Cập nhật thông tin người dùng trong collection `users`
            usersCollection.document(user.uid).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
