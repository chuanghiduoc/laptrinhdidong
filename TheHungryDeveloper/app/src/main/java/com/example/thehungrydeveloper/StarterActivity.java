package com.example.thehungrydeveloper;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StarterActivity extends AppCompatActivity {

    private ListView lstStarterFoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_starter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lstStarterFoods = findViewById(R.id.lst_starter_foods);

        String[] starterFoods = {
                "Spring Rolls",
                "Garlic Bread",
                "Bruschetta",
                "Stuffed Mushrooms",
                "Caprese Salad",
                "Spring Rolls",
                "Garlic Bread",
                "Bruschetta",
                "Stuffed Mushrooms",
                "Caprese Salad",
                "Spring Rolls",
                "Garlic Bread",
                "Bruschetta",
                "Stuffed Mushrooms",
                "Caprese Salad",
                "Spring Rolls",
                "Garlic Bread",
                "Bruschetta",
                "Stuffed Mushrooms",
                "Caprese Salad"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.list_item_starter,
                R.id.txt_food_name,
                starterFoods
        );

        lstStarterFoods.setAdapter(adapter);
    }



}