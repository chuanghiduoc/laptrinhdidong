package com.example.sqlitedemo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "ContactsDB"
        private const val TABLE_CONTACTS = "contacts"

        // Tên các cột
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_PHONE = "phone"
    }

    // Tạo bảng
    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_PHONE + " TEXT" + ")")
        db.execSQL(createTableQuery)
    }

    // Cập nhật cơ sở dữ liệu
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Xóa bảng cũ nếu tồn tại
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        // Tạo lại bảng
        onCreate(db)
    }

    // Thêm liên hệ mới
    fun addContact(name: String, phone: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_NAME, name)
        values.put(KEY_PHONE, phone)

        // Chèn dữ liệu vào bảng
        val id = db.insert(TABLE_CONTACTS, null, values)
        db.close()
        return id
    }

    // Lấy tất cả liên hệ
    fun getAllContacts(): List<Contact> {
        val contactList = mutableListOf<Contact>()
        val selectQuery = "SELECT * FROM $TABLE_CONTACTS"

        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        // Duyệt qua tất cả các dòng và thêm vào danh sách
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PHONE))

                val contact = Contact(id, name, phone)
                contactList.add(contact)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return contactList
    }

    // Cập nhật liên hệ
    fun updateContact(id: Int, name: String, phone: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_NAME, name)
        values.put(KEY_PHONE, phone)

        // Cập nhật dòng
        val result = db.update(TABLE_CONTACTS, values, "$KEY_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    // Xóa liên hệ
    fun deleteContact(id: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_CONTACTS, "$KEY_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    // Tìm liên hệ theo tên
    fun getContactByName(name: String): Contact? {
        val db = this.readableDatabase
        var contact: Contact? = null

        val cursor = db.query(
            TABLE_CONTACTS,
            arrayOf(KEY_ID, KEY_NAME, KEY_PHONE),
            "$KEY_NAME = ?",
            arrayOf(name),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID))
            val contactName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PHONE))
            contact = Contact(id, contactName, phone)
        }

        cursor.close()
        db.close()
        return contact
    }
}
