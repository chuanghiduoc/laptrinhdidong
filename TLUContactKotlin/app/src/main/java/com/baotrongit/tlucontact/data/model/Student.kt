package com.baotrongit.tlucontact.data.model

data class Student(
    val id: String,
    val fullName: String,
    val studentCode: String,
    val unitId: String,
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val avatarUrl: String? = null,
    val classId: String? = null

)
