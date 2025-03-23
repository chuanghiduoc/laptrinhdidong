package com.example.sqlitedemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button
    private lateinit var btnView: Button
    private lateinit var rvContacts: RecyclerView

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var contactAdapter: ContactAdapter

    private var selectedContact: Contact? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Khởi tạo DatabaseHelper
        dbHelper = DatabaseHelper(this)

        // Ánh xạ các thành phần UI
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        btnAdd = findViewById(R.id.btnAdd)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnDelete = findViewById(R.id.btnDelete)
        btnView = findViewById(R.id.btnView)
        rvContacts = findViewById(R.id.rvContacts)

        // Thiết lập RecyclerView
        setupRecyclerView()

        // Thiết lập sự kiện cho các nút
        setupButtonListeners()
    }

    private fun setupRecyclerView() {
        contactAdapter = ContactAdapter(emptyList()) { contact ->
            // Xử lý khi người dùng chọn một liên hệ
            selectedContact = contact
            etName.setText(contact.name)
            etPhone.setText(contact.phone)
            Toast.makeText(this, "Đã chọn: ${contact.name}", Toast.LENGTH_SHORT).show()
        }

        rvContacts.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = contactAdapter
        }
    }

    private fun setupButtonListeners() {
        // Nút Thêm
        btnAdd.setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Thêm liên hệ vào cơ sở dữ liệu
            val id = dbHelper.addContact(name, phone)
            if (id > 0) {
                Toast.makeText(this, "Thêm liên hệ thành công", Toast.LENGTH_SHORT).show()
                clearFields()
                loadContacts() // Cập nhật danh sách
            } else {
                Toast.makeText(this, "Thêm liên hệ thất bại", Toast.LENGTH_SHORT).show()
            }
        }

        // Nút Sửa
        btnUpdate.setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedContact == null) {
                Toast.makeText(this, "Vui lòng chọn liên hệ cần sửa", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cập nhật liên hệ trong cơ sở dữ liệu
            val result = dbHelper.updateContact(selectedContact!!.id, name, phone)
            if (result > 0) {
                Toast.makeText(this, "Cập nhật liên hệ thành công", Toast.LENGTH_SHORT).show()
                clearFields()
                loadContacts()
                selectedContact = null
            } else {
                Toast.makeText(this, "Cập nhật liên hệ thất bại", Toast.LENGTH_SHORT).show()
            }
        }

        // Nút Xóa
        btnDelete.setOnClickListener {
            if (selectedContact == null) {
                Toast.makeText(this, "Vui lòng chọn liên hệ cần xóa", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Hiển thị hộp thoại xác nhận
            AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa liên hệ này?")
                .setPositiveButton("Xóa") { _, _ ->
                    // Xóa liên hệ khỏi cơ sở dữ liệu
                    val result = dbHelper.deleteContact(selectedContact!!.id)
                    if (result > 0) {
                        Toast.makeText(this, "Xóa liên hệ thành công", Toast.LENGTH_SHORT).show()
                        clearFields()
                        loadContacts()
                        selectedContact = null
                    } else {
                        Toast.makeText(this, "Xóa liên hệ thất bại", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Hủy", null)
                .show()
        }

        // Nút Hiển thị
        btnView.setOnClickListener {
            loadContacts()
        }
    }

    private fun loadContacts() {
        val contacts = dbHelper.getAllContacts()
        contactAdapter.updateContacts(contacts)

        if (contacts.isEmpty()) {
            Toast.makeText(this, "Không có liên hệ nào", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearFields() {
        etName.text.clear()
        etPhone.text.clear()
    }
}
