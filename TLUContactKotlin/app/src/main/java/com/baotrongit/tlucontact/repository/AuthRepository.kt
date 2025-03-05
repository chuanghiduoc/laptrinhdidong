package com.baotrongit.tlucontact.data.repository

import android.util.Log
import com.baotrongit.tlucontact.data.model.User
import com.baotrongit.tlucontact.data.model.UserType
import com.baotrongit.tlucontact.utils.EmailValidationResult
import com.baotrongit.tlucontact.utils.ValidationUtils
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
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
    suspend fun registerUser(
        email: String,
        password: String,
        fullName: String,
        studentId: String
    ): Result<FirebaseUser> {
        return try {
            // Tạo tài khoản người dùng mới
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("Đăng ký thất bại"))

            // Xác định loại người dùng từ email
            val userType = when (ValidationUtils.validateEmail(email)) {
                EmailValidationResult.VALID_STAFF -> UserType.STAFF
                EmailValidationResult.VALID_STUDENT -> UserType.STUDENT
                else -> return Result.failure(Exception("Email không hợp lệ"))
            }

            // Tạo đối tượng User
            val user = User(
                uid = firebaseUser.uid,
                email = email,
                fullName = fullName,
                photoURL = "", // Có thể để trống hoặc thêm khi có ảnh
                phoneNumber = "",
                address = "", // Địa chỉ có thể để trống cho đến khi cập nhật
                userType = userType,
                isEmailVerified = firebaseUser.isEmailVerified,
                profileId = studentId, // Mã cán bộ hoặc mã sinh viên
                createdAt = Timestamp.now(),
                lastUpdated = Timestamp.now()
            )

            // Lưu thông tin người dùng vào Firestore
            usersCollection.document(firebaseUser.uid).set(user).await()

            // Gửi email xác minh
            firebaseUser.sendEmailVerification().await()

            Result.success(firebaseUser)
        } catch (e: Exception) {
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
