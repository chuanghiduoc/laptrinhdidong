package com.example.tlustudents;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Student[] students = {
                new Student("S01", "Le Van A", R.drawable.avt),
                new Student("S02", "Nguyen Thi B", R.drawable.avt),
                new Student("S03", "Tran Van C", R.drawable.avt),
                new Student("S04", "Pham Thi D", R.drawable.avt),
                new Student("S05", "Hoang Van E", R.drawable.avt),
                new Student("S06", "Vu Thi F", R.drawable.avt),
                new Student("S07", "Dang Van G", R.drawable.avt),
                new Student("S08", "Bui Thi H", R.drawable.avt),
                new Student("S09", "Do Van I", R.drawable.avt),
                new Student("S10", "Ngo Thi K", R.drawable.avt)
        };

        recyclerView = findViewById(R.id.rcv_students);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new StudentAdapter(students);
        recyclerView.setAdapter(adapter);

    }
}