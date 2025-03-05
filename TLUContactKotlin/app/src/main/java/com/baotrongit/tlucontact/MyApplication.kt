package com.baotrongit.tlucontact

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Khởi tạo Firebase
        FirebaseApp.initializeApp(this)

        // Vô hiệu hóa reCAPTCHA trong môi trường phát triển
        Firebase.auth.firebaseAuthSettings.setAppVerificationDisabledForTesting(true)
    }
}
