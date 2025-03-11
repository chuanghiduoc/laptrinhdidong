package com.example.csesport

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_nav)
//        val frameLayout = findViewById<FrameLayout>(R.id.frame_container)

        bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_running -> {
                    loadFragment(RunningFragment())
                }
                R.id.navigation_cycling -> {
                    loadFragment(CyclingFragment())
                }
            }
            true
        }
        if (savedInstanceState == null) {
            loadFragment(RunningFragment())
            bottomNavView.selectedItemId = R.id.navigation_running
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }
}
