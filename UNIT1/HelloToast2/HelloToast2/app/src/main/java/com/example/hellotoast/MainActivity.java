package com.example.hellotoast;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private Button btnToast, btnCount;
    private TextView textToast;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLUE));
        }
        setContentView(R.layout.activity_main);
        btnToast = findViewById(R.id.btnToast);
        btnCount = findViewById(R.id.btnCount);
        textToast = findViewById(R.id.show_count);

        // Xử lý xự kiện
        btnToast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast(v);
            }
        });
        btnCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countUp(v);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void showToast(View v){
        Toast.makeText(MainActivity.this, R.string.toast_message, Toast.LENGTH_LONG).show();
    }
    public void countUp(View v){
        count++;
        textToast.setText(String.valueOf(count));
    }

//    @Override
//    public void onBackPressed() {
//        AlertDialog.Builder myDialog = new AlertDialog.Builder(MainActivity.this);
//        myDialog.setTitle("Question");
//        myDialog.setMessage("Are you sure you want to exit?");
//        //myDialog.setIcon(R.drawable)
//        myDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //Thoát
//                finish();
//            }
//        });
//        myDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialogInterface.cancel();
//            }
//        })
//    }
}