package com.baotrongit.tlucontact.data.model

import com.google.firebase.Timestamp

data class User(
    val uid: String = "",                // UID từ Firebase Authentication
    val email: String = "",              // Email đăng nhập
    val fullName: String = "",           // Họ và tên đầy đủ
    val photoURL: String = "",           // URL ảnh đại diện (từ Firebase Storage)
    val phoneNumber: String = "",        // Số điện thoại
    val address: String = "",             // Địa chỉ nơi ở (đối với sinh viên)
    val userType: UserType = UserType.STUDENT, // Loại người dùng: STAFF hoặc STUDENT
    val isEmailVerified: Boolean = false, // Trạng thái xác minh email
    val profileId: String = "",          // ID tham chiếu đến document trong collection staff hoặc students
    val createdAt: Timestamp = Timestamp.now(), // Ngày tạo tài khoản
    val lastUpdated: Timestamp = Timestamp.now(), // Ngày cập nhật gần nhất
    val classId: String = "",            // Mã lớp (đối với sinh viên)
    val position: String = "",            // Chức vụ (đối với cán bộ giảng viên)
    val unitId: String = "",              // ID đơn vị (đối với cán bộ giảng viên)
    val unitName: String = ""            // Tên đơn vị (đối với cán bộ giảng viên)
)

enum class UserType {
    STAFF, STUDENT
}
