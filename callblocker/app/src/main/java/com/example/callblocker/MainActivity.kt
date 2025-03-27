package com.example.callblocker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BlockedNumbersAdapter
    private lateinit var addButton: FloatingActionButton
    private val blockedNumbers = mutableListOf<String>()

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 100
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.CALL_PHONE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Kiểm tra và yêu cầu quyền
        if (!hasPermissions()) {
            requestPermissions()
        }

        // Khởi tạo RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Tải danh sách số điện thoại bị chặn từ SharedPreferences
        loadBlockedNumbers()

        // Khởi tạo adapter
        adapter = BlockedNumbersAdapter(blockedNumbers) { position ->
            // Xử lý sự kiện xóa số điện thoại
            removeBlockedNumber(position)
        }
        recyclerView.adapter = adapter

        // Nút thêm số điện thoại mới
        addButton = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            showAddNumberDialog()
        }
    }

    private fun hasPermissions(): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
    }

    private fun loadBlockedNumbers() {
        val sharedPrefs = getSharedPreferences("BlockedNumbers", Context.MODE_PRIVATE)
        val numbersSet = sharedPrefs.getStringSet("numbers", setOf()) ?: setOf()
        blockedNumbers.addAll(numbersSet)
    }

    private fun saveBlockedNumbers() {
        val sharedPrefs = getSharedPreferences("BlockedNumbers", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putStringSet("numbers", blockedNumbers.toSet())
        editor.apply()
    }

    private fun showAddNumberDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_number, null)
        val editText = dialogView.findViewById<EditText>(R.id.phoneNumberEditText)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Thêm số điện thoại")
            .setView(dialogView)
            .setPositiveButton("Thêm", null)
            .setNegativeButton("Hủy", null)
            .create()

        dialog.show()

        // Xử lý nút Thêm
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val number = editText.text.toString().trim()
            if (number.isNotEmpty()) {
                if (number in blockedNumbers) {
                    Toast.makeText(this, "Số điện thoại này đã có trong danh sách", Toast.LENGTH_SHORT).show()
                } else {
                    addBlockedNumber(number)
                    dialog.dismiss()
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addBlockedNumber(number: String) {
        blockedNumbers.add(number)
        adapter.notifyItemInserted(blockedNumbers.size - 1)
        saveBlockedNumbers()
        Toast.makeText(this, "Đã thêm số $number vào danh sách chặn", Toast.LENGTH_SHORT).show()
    }

    private fun removeBlockedNumber(position: Int) {
        val number = blockedNumbers[position]
        blockedNumbers.removeAt(position)
        adapter.notifyItemRemoved(position)
        saveBlockedNumbers()
        Toast.makeText(this, "Đã xóa số $number khỏi danh sách chặn", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Tất cả quyền đã được cấp", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Một số quyền bị từ chối, ứng dụng có thể không hoạt động đầy đủ", Toast.LENGTH_LONG).show()
            }
        }
    }
}
