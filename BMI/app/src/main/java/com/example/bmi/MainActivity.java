package com.example.bmi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private RadioButton radioButton_Male, radioButton_Female;
    private EditText editTextNumber_Age, editTextNumberDecimal_Inches, editTextNumberDecimal_Feet, editTextNumber_Weight;
    private Button button_cal;
    private TextView textView5;

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

        // Initialize views
        radioButton_Male = findViewById(R.id.radioButton_Male);
        radioButton_Female = findViewById(R.id.radioButton_Female);
        editTextNumber_Age = findViewById(R.id.editTextNumber_Age);
        editTextNumber_Weight = findViewById(R.id.editTextNumber_Weight);
        editTextNumberDecimal_Feet = findViewById(R.id.editTextNumberDecimal_Feet);
        editTextNumberDecimal_Inches = findViewById(R.id.editTextNumberDecimal_Inches);
        button_cal = findViewById(R.id.button_cal);
        textView5 = findViewById(R.id.textView5);

        // Set up button click listener
        setupButtonClickListener(button_cal);
    }

    private void setupButtonClickListener(Button btnCalculate) {
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if inputs are not empty
                if (editTextNumber_Age.getText().toString().isEmpty() ||
                        editTextNumberDecimal_Feet.getText().toString().isEmpty() ||
                        editTextNumberDecimal_Inches.getText().toString().isEmpty() ||
                        editTextNumber_Weight.getText().toString().isEmpty()) {
                    textView5.setText("Please fill in all fields");
                    return;
                }

                double bmiResult = calculateBMI();
                String ageText = editTextNumber_Age.getText().toString();
                int age = Integer.parseInt(ageText);

                if (age >= 18) {
                    displayResult(bmiResult);
                } else {
                    displayGuidance(bmiResult);
                }
            }
        });
    }

    private double calculateBMI() {
        String feetText = editTextNumberDecimal_Feet.getText().toString();
        String inchesText = editTextNumberDecimal_Inches.getText().toString();
        String weightText = editTextNumber_Weight.getText().toString();

        int feet = Integer.parseInt(feetText);
        int inches = Integer.parseInt(inchesText);
        int weight = Integer.parseInt(weightText);

        int totalInches = (feet * 12) + inches;
        double heightInMeters = totalInches * 0.0254;

        return weight / (heightInMeters * heightInMeters);
    }

    private void displayResult(double bmi) {
        DecimalFormat dcf = new DecimalFormat("0.00");
        String bmiTextResult = dcf.format(bmi);

        String fullResultString;
        if (bmi < 18.5) {
            fullResultString = bmiTextResult + " - You are underweight";
        } else if (bmi > 25) {
            fullResultString = bmiTextResult + " - You are overweight";
        } else {
            fullResultString = bmiTextResult + " - You are a healthy weight";
        }
        textView5.setText(fullResultString);
    }

    private void displayGuidance(double bmi) {
        DecimalFormat dcf = new DecimalFormat("0.00");
        String bmiTextResult = dcf.format(bmi);
        String fullResultString;

        if (radioButton_Male.isChecked()) {
            fullResultString = bmiTextResult + " - As you are under 18, please consult with your doctor for the healthy range for boys";
        } else if (radioButton_Female.isChecked()) {
            fullResultString = bmiTextResult + " - As you are under 18, please consult with your doctor for the healthy range for girls";
        } else {
            fullResultString = bmiTextResult + " - As you are under 18, please consult with your doctor for the healthy range";
        }
        textView5.setText(fullResultString);
    }
}
