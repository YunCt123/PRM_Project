package com.example.prm_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class VerifyAccountActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private LinearLayout layoutIdFront, layoutIdBack, layoutLicenseFront, layoutLicenseBack;
    private MaterialButton btnSubmit;
    
    private boolean hasIdFront = false;
    private boolean hasIdBack = false;
    private boolean hasLicenseFront = false;
    private boolean hasLicenseBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        layoutIdFront = findViewById(R.id.layoutIdFront);
        layoutIdBack = findViewById(R.id.layoutIdBack);
        layoutLicenseFront = findViewById(R.id.layoutLicenseFront);
        layoutLicenseBack = findViewById(R.id.layoutLicenseBack);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Upload listeners (simulate)
        layoutIdFront.setOnClickListener(v -> {
            hasIdFront = true;
            Toast.makeText(this, "Đã tải ảnh CMND/CCCD mặt trước", Toast.LENGTH_SHORT).show();
            checkAllUploaded();
        });

        layoutIdBack.setOnClickListener(v -> {
            hasIdBack = true;
            Toast.makeText(this, "Đã tải ảnh CMND/CCCD mặt sau", Toast.LENGTH_SHORT).show();
            checkAllUploaded();
        });

        layoutLicenseFront.setOnClickListener(v -> {
            hasLicenseFront = true;
            Toast.makeText(this, "Đã tải ảnh giấy phép lái xe mặt trước", Toast.LENGTH_SHORT).show();
            checkAllUploaded();
        });

        layoutLicenseBack.setOnClickListener(v -> {
            hasLicenseBack = true;
            Toast.makeText(this, "Đã tải ảnh giấy phép lái xe mặt sau", Toast.LENGTH_SHORT).show();
            checkAllUploaded();
        });

        btnSubmit.setOnClickListener(v -> {
            if (!hasIdFront || !hasIdBack || !hasLicenseFront || !hasLicenseBack) {
                Toast.makeText(this, "Vui lòng tải đầy đủ ảnh giấy tờ", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Đã gửi yêu cầu xác minh thành công!", Toast.LENGTH_LONG).show();
            finish();
        });
    }

    private void checkAllUploaded() {
        if (hasIdFront && hasIdBack && hasLicenseFront && hasLicenseBack) {
            btnSubmit.setEnabled(true);
            btnSubmit.setBackgroundTintList(getResources().getColorStateList(R.color.primary_green, null));
        }
    }
}

