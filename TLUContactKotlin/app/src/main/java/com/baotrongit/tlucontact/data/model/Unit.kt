package com.baotrongit.tlucontact.data.model

data class TLUUnit(
    val id: String,
    val name: String,
    val code: String? = null,
    val type: UnitType,
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val website: String? = null,
    val description: String? = null,
    val logoUrl: String? = null
)

enum class UnitType {
    FACULTY, DEPARTMENT, OFFICE, CENTER, OTHER;

    override fun toString(): String {
        return when (this) {
            FACULTY -> "Khoa"
            DEPARTMENT -> "Phòng"
            OFFICE -> "Ban"
            CENTER -> "Trung tâm"
            OTHER -> "Khác"
        }
    }

    companion object {
        fun fromString(type: String): UnitType {
            return when (type.lowercase()) {
                "khoa" -> FACULTY
                "phòng" -> DEPARTMENT
                "ban" -> OFFICE
                "trung tâm" -> CENTER
                else -> OTHER
            }
        }
    }
}
