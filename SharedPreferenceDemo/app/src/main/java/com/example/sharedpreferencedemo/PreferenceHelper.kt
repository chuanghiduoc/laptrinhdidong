package com.example.sharedpreferencedemo

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper(context: Context) {

    private val PREF_NAME = "UserPrefs"
    private val KEY_USERNAME = "username"
    private val KEY_PASSWORD = "password"

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // Lưu thông tin người dùng
    fun saveUserCredentials(username: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_PASSWORD, password)
        editor.apply()
    }

    // Lấy tên người dùng
    fun getUsername(): String {
        return sharedPreferences.getString(KEY_USERNAME, "") ?: ""
    }

    // Lấy mật khẩu
    fun getPassword(): String {
        return sharedPreferences.getString(KEY_PASSWORD, "") ?: ""
    }

    // Xóa thông tin người dùng
    fun clearUserCredentials() {
        val editor = sharedPreferences.edit()
        editor.remove(KEY_USERNAME)
        editor.remove(KEY_PASSWORD)
        editor.apply()
    }

    // Kiểm tra xem đã có thông tin người dùng chưa
    fun hasUserCredentials(): Boolean {
        return getUsername().isNotEmpty() && getPassword().isNotEmpty()
    }
}
