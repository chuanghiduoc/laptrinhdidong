package com.example.quanlychitieu;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.quanlychitieu.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Lấy NavController từ NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            // Bỏ dòng setupActionBarWithNavController
            // Chỉ thiết lập Bottom Navigation với NavController
            NavigationUI.setupWithNavController(binding.navView, navController);

            // Thiết lập FloatingActionButton nếu có
            FloatingActionButton fab = binding.fabAddTransaction;
            if (fab != null) {
                fab.setOnClickListener(view -> {
                    // Xử lý khi người dùng nhấn nút thêm giao dịch
                    // Ví dụ: navController.navigate(R.id.action_to_add_transaction);
                });
            }

            if (binding.toolbar != null) {
                navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                    binding.toolbar.setTitle(destination.getLabel());
                });
            }
        }
    }
}
