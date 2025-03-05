package com.baotrongit.tlucontact.utils

import com.baotrongit.tlucontact.data.model.User
import com.baotrongit.tlucontact.data.model.UserType

object UserManager {
    private var currentUser: User? = null

    fun setCurrentUser(user: User) {
        currentUser = user
    }

    fun getCurrentUser(): User? = currentUser

    fun clearCurrentUser() {
        currentUser = null
    }

    fun isStaff(): Boolean = currentUser?.userType == UserType.STAFF

    fun isStudent(): Boolean = currentUser?.userType == UserType.STUDENT

    fun getCurrentUserClassId(): String? =
        if (isStudent()) currentUser?.classId else null
}
