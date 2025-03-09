package com.baotrongit.tlucontact.data.model

data class Staff(
    val id: String,
    val fullName: String,
    val staffId: String,
    val position: String,
    val unitId: String,
    val email: String? = null,
    val phone: String? = null,
    val avatarUrl: String? = null
)
