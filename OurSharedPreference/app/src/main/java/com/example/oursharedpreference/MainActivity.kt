    package com.example.oursharedpreference

    import android.os.Bundle
    import android.widget.Button
    import android.widget.EditText
    import android.widget.TextView
    import androidx.activity.ComponentActivity
    import androidx.activity.compose.setContent
    import androidx.activity.enableEdgeToEdge
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.padding
    import androidx.compose.material3.Scaffold
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.tooling.preview.Preview
    import com.example.oursharedpreference.ui.theme.OurSharedPreferenceTheme
    import androidx.core.content.edit

    class MainActivity : ComponentActivity() {
        private lateinit var editPhone: EditText
        private lateinit var btnSave: Button
        private lateinit var btnLoad: Button
        private lateinit var txtInfo: TextView
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            editPhone = findViewById(R.id.edit_phone)
            btnSave = findViewById(R.id.btn_save)
            btnLoad = findViewById(R.id.btn_load)
            txtInfo = findViewById(R.id.txt_info)

            btnSave.setOnClickListener {
                val phone = editPhone.text.toString()
                val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
                sharedPreferences.edit() {
                    putString("phone", phone)
                }
            }

            btnLoad.setOnClickListener {
                val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
                val phone = sharedPreferences.getString("phone", "")
                txtInfo.text = phone
            }
        }
    }
