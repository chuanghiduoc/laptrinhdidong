package com.example.mycontactsapp

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    private lateinit var listViewContacts: ListView
    private lateinit var emptyTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listViewContacts = findViewById(R.id.listViewContacts)
        emptyTextView = findViewById(R.id.emptyTextView)

        // Kiểm tra quyền truy cập danh bạ
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {

            // Yêu cầu quyền nếu chưa được cấp
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )
        } else {
            // Đã có quyền, đọc danh bạ
            loadContacts()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền đã được cấp, đọc danh bạ
                loadContacts()
            } else {
                // Quyền bị từ chối
                Toast.makeText(
                    this,
                    "Bạn cần cấp quyền truy cập danh bạ để sử dụng tính năng này",
                    Toast.LENGTH_LONG
                ).show()
                emptyTextView.text = "Không có quyền truy cập danh bạ"
                emptyTextView.visibility = View.VISIBLE
            }
        }
    }

    private fun loadContacts() {
        // Danh sách lưu tên các liên hệ
        val contactsList = ArrayList<String>()

        // Truy vấn danh bạ
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            if (it.count > 0) {
                while (it.moveToNext()) {
                    val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
                    if (nameIndex >= 0) {
                        val name = it.getString(nameIndex)
                        contactsList.add(name)
                    }
                }
            }
        }

        // Hiển thị kết quả
        if (contactsList.isEmpty()) {
            emptyTextView.visibility = View.VISIBLE
            listViewContacts.visibility = View.GONE
        } else {
            emptyTextView.visibility = View.GONE
            listViewContacts.visibility = View.VISIBLE

            // Tạo adapter và gán cho ListView
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                contactsList
            )
            listViewContacts.adapter = adapter
        }
    }
}
