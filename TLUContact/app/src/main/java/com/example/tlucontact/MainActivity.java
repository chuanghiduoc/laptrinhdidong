package com.example.tlucontact;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tlucontact.adapter.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo ViewPager và Adapter
        viewPager = findViewById(R.id.view_pager);
        adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Tắt khả năng vuốt để chuyển trang (tùy chọn)
        // viewPager.setUserInputEnabled(false);

        // Thiết lập BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Xử lý sự kiện khi chọn item trên BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_units) {
                viewPager.setCurrentItem(0, false);
                return true;
            } else if (itemId == R.id.nav_staff) {
                viewPager.setCurrentItem(1, false);
                return true;
            }
            return false;
        });

        // Xử lý sự kiện khi ViewPager thay đổi trang
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.nav_units);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.nav_staff);
                        break;
                }
            }
        });
    }
}
