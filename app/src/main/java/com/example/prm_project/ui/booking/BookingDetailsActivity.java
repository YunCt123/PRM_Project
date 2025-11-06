package com.example.prm_project.ui.booking;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_project.R;
import com.example.prm_project.repository.BookingRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;

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
    private TextView tvDeposit;
    private TextView tvRentalPriceLabel;
    private TextView tvDailyRate;
    private TextView tvTax;
    private TextView tvTotalPrice;
    private MaterialButton btnContactSupport;
    private MaterialButton btnPayment;
    private MaterialButton btnCancelBooking;

    private String bookingId;
    private String status;
    private String checkoutUrl;
    private BookingRepository bookingRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        bookingRepository = new BookingRepository(this);

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
        tvDeposit = findViewById(R.id.tvDeposit);
        tvRentalPriceLabel = findViewById(R.id.tvRentalPriceLabel);
        tvDailyRate = findViewById(R.id.tvDailyRate);
        tvTax = findViewById(R.id.tvTax);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnContactSupport = findViewById(R.id.btnContactSupport);
        btnPayment = findViewById(R.id.btnPayment);
        btnCancelBooking = findViewById(R.id.btnCancelBooking);
    }

    private void loadBookingData() {
        // Get booking ID from intent
        bookingId = getIntent().getStringExtra("BOOKING_ID");

        if (bookingId == null) {
            Toast.makeText(this, "Không tìm thấy mã đặt xe", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch booking details from API
        bookingRepository.getBookingDetails(bookingId).observe(this, bookingItem -> {
            if (bookingItem != null) {
                populateBookingData(bookingItem);
            } else {
                Toast.makeText(this, "Không thể tải chi tiết đặt xe", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void populateBookingData(com.example.prm_project.models.Booking.BookingItem bookingItem) {
        try {
            // Set booking ID
            tvBookingId.setText("#" + bookingItem.getId());

            // Set vehicle information
            if (bookingItem.getVehicle() != null) {
                String vehicleName = bookingItem.getVehicle().getBrand() + " " + bookingItem.getVehicle().getModel();
                tvVehicleName.setText(vehicleName);
                tvVehicleType.setText("Biển số: " + bookingItem.getVehicle().getPlateNumber());
            }

            // Set station information
            if (bookingItem.getStation() != null) {
                // Could add station name display if needed
            }

            // Parse and set dates
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
            isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());

            if (bookingItem.getStartTime() != null) {
                java.util.Date startDate = isoFormat.parse(bookingItem.getStartTime());
                tvPickupDate.setText(dateFormat.format(startDate));
                tvPickupTime.setText(timeFormat.format(startDate));
            }

            if (bookingItem.getEndTime() != null) {
                java.util.Date endDate = isoFormat.parse(bookingItem.getEndTime());
                tvReturnDate.setText(dateFormat.format(endDate));
                tvReturnTime.setText(timeFormat.format(endDate));
            }

            // Set status
            status = bookingItem.getStatus();
            String statusText = getStatusText(status);
            tvBookingStatus.setText(statusText);

            // Store checkout URL
            if (bookingItem.getDeposit() != null) {
                checkoutUrl = bookingItem.getDeposit().getCheckoutUrl();
            }

            // Set status color and button visibility
            if ("active".equals(status)) {
                tvBookingStatus.setTextColor(getResources().getColor(R.color.primary_blue, getTheme()));
                btnCancelBooking.setVisibility(View.VISIBLE);
            } else if ("completed".equals(status)) {
                tvBookingStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark, getTheme()));
                btnCancelBooking.setVisibility(View.GONE);
            } else if ("cancelled".equals(status)) {
                tvBookingStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark, getTheme()));
                btnCancelBooking.setVisibility(View.GONE);
            } else if ("expired".equals(status)) {
                tvBookingStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark, getTheme()));
                btnCancelBooking.setVisibility(View.GONE);
            }

            // Show payment button if checkout URL exists
            btnPayment.setVisibility(checkoutUrl != null && !checkoutUrl.isEmpty() ? View.VISIBLE : View.GONE);

            // Set price breakdown using amounts data
            if (bookingItem.getAmounts() != null) {
                long rentalEstimated = bookingItem.getAmounts().getRentalEstimated();
                tvDailyRate.setText(formatCurrency(rentalEstimated));

                // Deposit = grandTotal - rentalEstimated
                long depositAmount = bookingItem.getAmounts().getGrandTotal() - rentalEstimated;
                tvDeposit.setText(formatCurrency(depositAmount));

                // Tax
                tvTax.setText(formatCurrency(bookingItem.getAmounts().getTax()));

                // Set total price from grandTotal
                tvTotalPrice.setText(formatCurrency(bookingItem.getAmounts().getGrandTotal()));

                // Update rental price label with days from pricing snapshot
                if (bookingItem.getPricingSnapshot() != null) {
                    int days = bookingItem.getPricingSnapshot().getDays();
                    tvRentalPriceLabel.setText("Giá thuê (" + days + " ngày)");
                }
            }

        } catch (Exception e) {
            Log.e("BookingDetailsActivity", "Error parsing booking data", e);
            Toast.makeText(this, "Lỗi khi xử lý dữ liệu đặt xe", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnContactSupport.setOnClickListener(v -> {
            Toast.makeText(this, "Liên hệ hỗ trợ cho đơn #" + bookingId, Toast.LENGTH_SHORT).show();
            // TODO: Open support chat or call
        });

        btnPayment.setOnClickListener(v -> {
            if (checkoutUrl != null && !checkoutUrl.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(checkoutUrl));
                startActivity(browserIntent);
            } else {
                Toast.makeText(this, "Không tìm thấy liên kết thanh toán", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancelBooking.setOnClickListener(v -> showCancelDialog());
    }

    private String getStatusText(String status) {
        if (status == null) return "Không xác định";
        switch (status.toLowerCase()) {
            case "pending":
                return "Chờ xác nhận";
            case "active":
                return "Đang hoạt động";
            case "completed":
                return "Đã hoàn thành";
            case "cancelled":
                return "Đã hủy";
            case "expired":
                return "Đã hết hạn";
            default:
                return "Không xác định";
        }
    }

    private String formatCurrency(long amount) {
        // Format amount as VND currency
        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(java.util.Locale.getDefault());
        return formatter.format(amount) + " VND";
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
