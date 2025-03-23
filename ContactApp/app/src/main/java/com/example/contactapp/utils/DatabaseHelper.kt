package com.example.contactapp.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.contactapp.model.Contact

class DatabaseHelper: SQLiteOpenHelper {
    constructor(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int): super(context, name, factory, version) {
    }
    constructor(context: Context): super(context, DATABASE_NAME, null, DATABASE_VERSION) {
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_CONTACT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(DROP_TABLE_CONTACT)
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "contact.db"
        const val DATABASE_VERSION = 1
        const val TABLE_CONTACT = "contact"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_EMAIL = "email"
        const val CREATE_TABLE_CONTACT = "CREATE TABLE $TABLE_CONTACT ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME TEXT, $COLUMN_PHONE TEXT, $COLUMN_EMAIL TEXT)"
        const val DROP_TABLE_CONTACT = "DROP TABLE IF EXISTS $TABLE_CONTACT"
    }

    fun getAllContacts(): ArrayList<Contact> {
        val contacts = ArrayList<Contact>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CONTACT", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                val phone = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE))
                val email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL))
                contacts.add(Contact(id, name, email, phone))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return contacts
    }
}