package com.baotrongit.tlucontact.utils

import com.baotrongit.tlucontact.data.model.TLUUnit
import com.baotrongit.tlucontact.data.model.UnitType
import com.baotrongit.tlucontact.data.model.Staff
import com.baotrongit.tlucontact.data.model.Student
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DataProvider {
    private val db = FirebaseFirestore.getInstance()
    private const val UNITS_COLLECTION = "units"
    private const val STUDENTS_COLLECTION = "students"
    private const val STAFF_COLLECTION = "staff"

    suspend fun getUnits(): List<TLUUnit> = withContext(Dispatchers.IO) {
        try {
            val snapshot = db.collection(UNITS_COLLECTION).get().await()
            return@withContext snapshot.documents.mapNotNull { document ->
                try {
                    val unitTypeString = document.getString("type") ?: "OTHER"
                    val unitType = try {
                        UnitType.valueOf(unitTypeString.uppercase())
                    } catch (e: IllegalArgumentException) {
                        UnitType.OTHER
                    }

                    TLUUnit(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        code = document.getString("code"),
                        type = unitType,
                        email = document.getString("email"),
                        phone = document.getString("phone"),
                        address = document.getString("address"),
                        website = document.getString("website"),
                        description = document.getString("description"),
                        logoUrl = document.getString("logoUrl")
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            // Handle any errors here
            return@withContext emptyList<TLUUnit>()
        }
    }

    suspend fun getUnitById(unitId: String): TLUUnit? = withContext(Dispatchers.IO) {
        try {
            val document = db.collection(UNITS_COLLECTION).document(unitId).get().await()
            if (document.exists()) {
                val unitTypeString = document.getString("type") ?: "OTHER"
                val unitType = try {
                    UnitType.valueOf(unitTypeString.uppercase())
                } catch (e: IllegalArgumentException) {
                    UnitType.OTHER
                }

                return@withContext TLUUnit(
                    id = document.id,
                    name = document.getString("name") ?: "",
                    code = document.getString("code"),
                    type = unitType,
                    email = document.getString("email"),
                    phone = document.getString("phone"),
                    address = document.getString("address"),
                    website = document.getString("website"),
                    description = document.getString("description"),
                    logoUrl = document.getString("logoUrl")
                )
            } else {
                return@withContext null
            }
        } catch (e: Exception) {
            return@withContext null
        }
    }

    suspend fun getStudents(): List<Student> = withContext(Dispatchers.IO) {
        try {
            val snapshot = db.collection(STUDENTS_COLLECTION).get().await()
            return@withContext snapshot.documents.mapNotNull { document ->
                try {
                    Student(
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
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            // Handle any errors here
            return@withContext emptyList<Student>()
        }
    }

    suspend fun getStaff(): List<Staff> = withContext(Dispatchers.IO) {
        try {
            val snapshot = db.collection(STAFF_COLLECTION).get().await()
            return@withContext snapshot.documents.mapNotNull { document ->
                try {
                    Staff(
                        id = document.id,
                        fullName = document.getString("fullName") ?: "",
                        position = document.getString("position") ?: "",
                        unitId = document.getString("unitId") ?: "",
                        email = document.getString("email") ?: "",
                        phone = document.getString("phone"),
                        avatarUrl = document.getString("avatarUrl"),
                        staffId = document.getString("staffId") ?: "",
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            // Handle any errors here
            return@withContext emptyList<Staff>()
        }
    }
    suspend fun getStaffByUnitId(unitId: String): List<Staff> {
        return getStaff().filter { it.unitId == unitId }
    }

}
