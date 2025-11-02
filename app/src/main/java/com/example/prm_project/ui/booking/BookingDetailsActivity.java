package com.example.prm_project.ui.booking;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_project.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class BookingDetailsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvBookingStatus;
    private TextView tvVehicleName;
    private TextView tvVehicleType;
    private TextView tvBookingId;
    private TextView tvPickupDate;
    private TextView tvPickupTime;
    private TextView tvReturnDate;
    private TextView tvReturnTime;
    private TextView tvDailyRate;
    private TextView tvServiceFee;
    private TextView tvInsurance;
    private TextView tvTotalPrice;
    private MaterialButton btnContactSupport;
    private MaterialButton btnCancelBooking;

    private String bookingId;
    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        initViews();
        loadBookingData();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvBookingStatus = findViewById(R.id.tvBookingStatus);
        tvVehicleName = findViewById(R.id.tvVehicleName);
        tvVehicleType = findViewById(R.id.tvVehicleType);
        tvBookingId = findViewById(R.id.tvBookingId);
        tvPickupDate = findViewById(R.id.tvPickupDate);
        tvPickupTime = findViewById(R.id.tvPickupTime);
        tvReturnDate = findViewById(R.id.tvReturnDate);
        tvReturnTime = findViewById(R.id.tvReturnTime);
        tvDailyRate = findViewById(R.id.tvDailyRate);
        tvServiceFee = findViewById(R.id.tvServiceFee);
        tvInsurance = findViewById(R.id.tvInsurance);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnContactSupport = findViewById(R.id.btnContactSupport);
        btnCancelBooking = findViewById(R.id.btnCancelBooking);
    }

    private void loadBookingData() {
        // Get data from intent
        bookingId = getIntent().getStringExtra("BOOKING_ID");
        String vehicleName = getIntent().getStringExtra("VEHICLE_NAME");
        String pickupDate = getIntent().getStringExtra("PICKUP_DATE");
        String pickupTime = getIntent().getStringExtra("PICKUP_TIME");
        String returnDate = getIntent().getStringExtra("RETURN_DATE");
        String returnTime = getIntent().getStringExtra("RETURN_TIME");
        String totalPrice = getIntent().getStringExtra("TOTAL_PRICE");
        status = getIntent().getStringExtra("STATUS");
        String statusText = getIntent().getStringExtra("STATUS_TEXT");

        // Set data to views
        if (bookingId != null) tvBookingId.setText("#" + bookingId);
        if (vehicleName != null) tvVehicleName.setText(vehicleName);
        if (pickupDate != null) tvPickupDate.setText(pickupDate);
        if (pickupTime != null) tvPickupTime.setText(pickupTime);
        if (returnDate != null) tvReturnDate.setText(returnDate);
        if (returnTime != null) tvReturnTime.setText(returnTime);
        if (totalPrice != null) tvTotalPrice.setText(totalPrice);
        if (statusText != null) tvBookingStatus.setText(statusText);

        // Set status color
        if ("ACTIVE".equals(status)) {
            tvBookingStatus.setTextColor(getResources().getColor(R.color.primary_blue));
            btnCancelBooking.setVisibility(View.VISIBLE);
        } else if ("COMPLETED".equals(status)) {
            tvBookingStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            btnCancelBooking.setVisibility(View.GONE);
        } else if ("CANCELLED".equals(status)) {
            tvBookingStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            btnCancelBooking.setVisibility(View.GONE);
        }

        // Set vehicle type (dummy data)
        tvVehicleType.setText("Sedan • Electric • 2023");
        
        // Calculate price breakdown (dummy calculation)
        if (totalPrice != null) {
            try {
                int total = Integer.parseInt(totalPrice.replace("$", ""));
                int insurance = 20;
                int serviceFee = 20;
                int dailyRate = total - insurance - serviceFee;
                
                tvDailyRate.setText("$" + dailyRate);
                tvServiceFee.setText("$" + serviceFee);
                tvInsurance.setText("$" + insurance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnContactSupport.setOnClickListener(v -> {
            Toast.makeText(this, "Liên hệ hỗ trợ cho đơn #" + bookingId, Toast.LENGTH_SHORT).show();
            // TODO: Open support chat or call
        });

        btnCancelBooking.setOnClickListener(v -> showCancelDialog());
    }

    private void showCancelDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Hủy đơn đặt xe")
                .setMessage("Bạn có chắc chắn muốn hủy đơn đặt xe này? Hành động này không thể hoàn tác.")
                .setPositiveButton("Hủy đơn", (dialog, which) -> {
                    // TODO: Call API to cancel booking
                    Toast.makeText(this, "Đã hủy đơn đặt xe #" + bookingId, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
