package com.example.appwithsettings;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;
import android.widget.Switch;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchNotifications;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // Tìm Toolbar trong layout và đặt làm ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        // Hiển thị nút quay lại (Up button)
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Lấy Switch từ layout
        switchNotifications = findViewById(R.id.example_switch);

        // Lấy SharedPreferences
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);

        // Tải giá trị đã lưu
        loadSettings();

        // Xử lý khi người dùng thay đổi trạng thái Switch
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> saveSettings(isChecked));

        // Xử lý padding để hỗ trợ EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadSettings() {
        boolean isNotificationsEnabled = sharedPreferences.getBoolean("notifications", true);
        switchNotifications.setChecked(isNotificationsEnabled);
    }

    private void saveSettings(boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notifications", isChecked);
        editor.apply();
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Quay lại MainActivity khi bấm nút back trên ActionBar
        return true;
    }
}
