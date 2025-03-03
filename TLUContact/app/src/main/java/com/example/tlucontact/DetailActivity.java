package com.example.tlucontact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;

public class DetailActivity extends AppCompatActivity {

    private TextView textViewName;
    private TextView textViewPhone;
    private TextView textViewEmail;
    private TextView textViewAddress;
    private TextView textViewPosition;
    private MaterialButton btnCall, btnMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Ánh xạ toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết");
        }

        // Ánh xạ các TextView
        initializeViews();

        // Nhận và hiển thị dữ liệu
        displayDetails();

        // Thiết lập sự kiện cho các nút
        setupActionButtons();
    }

    private void initializeViews() {
        textViewName = findViewById(R.id.text_view_name);
        textViewPhone = findViewById(R.id.text_view_phone);
        textViewEmail = findViewById(R.id.text_view_email);
        textViewAddress = findViewById(R.id.text_view_address);
        textViewPosition = findViewById(R.id.text_view_position);

        btnCall = findViewById(R.id.btn_call);
        btnMessage = findViewById(R.id.btn_message);
    }

    private void displayDetails() {
        Intent intent = getIntent();

        String name = intent.getStringExtra("NAME");
        textViewName.setText(name);

        String phone = intent.getStringExtra("PHONE");
        View phoneContainer = (View) textViewPhone.getParent();
        if (phone != null && !phone.isEmpty()) {
            textViewPhone.setText(phone);
            phoneContainer.setVisibility(View.VISIBLE);
        } else {
            phoneContainer.setVisibility(View.GONE);
        }

        String email = intent.getStringExtra("EMAIL");
        View emailContainer = (View) textViewEmail.getParent();
        if (email != null && !email.isEmpty()) {
            textViewEmail.setText(email);
            emailContainer.setVisibility(View.VISIBLE);
        } else {
            emailContainer.setVisibility(View.GONE);
        }

        String address = intent.getStringExtra("ADDRESS");
        View addressContainer = (View) textViewAddress.getParent();
        if (address != null && !address.isEmpty()) {
            textViewAddress.setText(address);
            addressContainer.setVisibility(View.VISIBLE);
        } else {
            addressContainer.setVisibility(View.GONE);
        }

        String position = intent.getStringExtra("POSITION");
        View positionContainer = (View) textViewPosition.getParent();
        if (position != null && !position.isEmpty()) {
            textViewPosition.setText(position);
            positionContainer.setVisibility(View.VISIBLE);
        } else {
            positionContainer.setVisibility(View.GONE);
        }
    }


    private void setupActionButtons() {
        String phone = getIntent().getStringExtra("PHONE");

        btnCall.setOnClickListener(v -> {
            if (phone != null && !phone.isEmpty()) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);
            }
        });

        btnMessage.setOnClickListener(v -> {
            if (phone != null && !phone.isEmpty()) {
                Intent messageIntent = new Intent(Intent.ACTION_SENDTO);
                messageIntent.setData(Uri.parse("smsto:" + phone));
                startActivity(messageIntent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
