package com.baotrongit.tlucontact.utils

object ValidationUtils {

    private const val TLU_STAFF_EMAIL_DOMAIN = "@tlu.edu.vn"
    private const val TLU_STUDENT_EMAIL_DOMAIN = "@e.tlu.edu.vn"

    fun validateEmail(email: String): EmailValidationResult {
        if (email.isBlank()) {
            return EmailValidationResult.EMPTY
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return EmailValidationResult.INVALID_FORMAT
        }

        return when {
            email.endsWith(TLU_STAFF_EMAIL_DOMAIN) -> EmailValidationResult.VALID_STAFF
            email.endsWith(TLU_STUDENT_EMAIL_DOMAIN) -> EmailValidationResult.VALID_STUDENT
            else -> EmailValidationResult.INVALID_DOMAIN
        }
    }

    fun validatePassword(password: String): PasswordValidationResult {
        if (password.isBlank()) {
            return PasswordValidationResult.EMPTY
        }

        if (password.length < 8) {
            return PasswordValidationResult.TOO_SHORT
        }

        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

        if (!hasUppercase || !hasLowercase || !hasDigit || !hasSpecialChar) {
            return PasswordValidationResult.TOO_WEAK
        }

        return PasswordValidationResult.VALID
    }

    fun doPasswordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    fun validateStudentId(studentId: String, isStaff: Boolean): Boolean {
        if (studentId.isBlank()) {
            return false
        }

        // Thêm logic kiểm tra mã sinh viên/cán bộ nếu cần
        // Ví dụ: mã sinh viên thường có định dạng 2023xxxx

        return true
    }
}

enum class EmailValidationResult {
    EMPTY, INVALID_FORMAT, INVALID_DOMAIN, VALID_STAFF, VALID_STUDENT
}

enum class PasswordValidationResult {
    EMPTY, TOO_SHORT, TOO_WEAK, VALID
}
