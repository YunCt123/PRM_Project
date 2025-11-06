package com.example.prm_project.activies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.prm_project.R;
import com.example.prm_project.activies.MainActivity;
import com.example.prm_project.api.ApiClient;
import com.example.prm_project.api.VehicleApiService;
import com.example.prm_project.models.ApiResponse;
import com.example.prm_project.models.Vehicle;
import com.example.prm_project.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingResultActivity extends AppCompatActivity {
    private static final String TAG = "BookingResult";
    
    private ImageButton btnBack;
    private ImageView ivStatusIcon, ivVehicleImage;
    private TextView tvStatusTitle, tvStatusMessage;
    private TextView tvVehicleName, tvLicensePlate, tvBookingId;
    private TextView tvStartDate, tvStartTime, tvEndDate, tvEndTime;
    private TextView tvDeposit, tvRentalDurationLabel, tvRentalPrice, tvTax, tvTotalAmount;
    private MaterialButton btnViewBookings, btnBackToHome;
    
    private VehicleApiService vehicleApiService;
    private SessionManager sessionManager;
    private boolean isSuccess = true;
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat displayTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        setContentView(R.layout.activity_booking_result);
        
        // Initialize API and SessionManager
        vehicleApiService = ApiClient.getClient().create(VehicleApiService.class);
        sessionManager = new SessionManager(this);
        
        initViews();
        handlePaymentCallback();
        setupListeners();
    }
    
    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        ivStatusIcon = findViewById(R.id.ivStatusIcon);
        ivVehicleImage = findViewById(R.id.ivVehicleImage);
        tvStatusTitle = findViewById(R.id.tvStatusTitle);
        tvStatusMessage = findViewById(R.id.tvStatusMessage);
        tvVehicleName = findViewById(R.id.tvVehicleName);
        tvLicensePlate = findViewById(R.id.tvLicensePlate);
        tvBookingId = findViewById(R.id.tvBookingId);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndDate = findViewById(R.id.tvEndDate);
        tvEndTime = findViewById(R.id.tvEndTime);
        tvDeposit = findViewById(R.id.tvDeposit);
        tvRentalDurationLabel = findViewById(R.id.tvRentalDurationLabel);
        tvRentalPrice = findViewById(R.id.tvRentalPrice);
        tvTax = findViewById(R.id.tvTax);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnViewBookings = findViewById(R.id.btnViewBookings);
        btnBackToHome = findViewById(R.id.btnBackToHome);
    }
    
    private void handlePaymentCallback() {
        Intent intent = getIntent();
        Uri data = intent.getData();
        
        // Check if this is a payment callback from PayOS
        if (data != null) {
            Log.d(TAG, "Received callback URI: " + data.toString());
            
            // PayOS returns: ?code=00&id=<orderCode>&cancel=false&status=PAID
            String code = data.getQueryParameter("code");
            String status = data.getQueryParameter("status");
            String cancel = data.getQueryParameter("cancel");
            
            Log.d(TAG, "Payment code: " + code + ", status: " + status + ", cancel: " + cancel);
            
            // Determine payment success or failure
            if ("00".equals(code) && "PAID".equals(status)) {
                isSuccess = true;
                updateUIForSuccess();
            } else {
                isSuccess = false;
                updateUIForFailure();
            }
        } else {
            // No callback data, check intent extras
            String bookingId = intent.getStringExtra("booking_id");
            String paymentStatus = intent.getStringExtra("status");
            
            if (bookingId != null) {
                isSuccess = "pending".equals(paymentStatus) || "success".equals(paymentStatus);
                if (isSuccess) {
                    updateUIForSuccess();
                } else {
                    updateUIForFailure();
                }
            }
        }
        
        // Load booking data from intent
        loadBookingDataFromIntent();
    }
    
    private void updateUIForSuccess() {
        ivStatusIcon.setImageResource(R.drawable.ic_check_circle_24dp);
        ivStatusIcon.setColorFilter(getResources().getColor(R.color.primary_green, null));
        tvStatusTitle.setText("Đặt xe thành công!");
        tvStatusTitle.setTextColor(getResources().getColor(R.color.primary_green, null));
        tvStatusMessage.setText("Thanh toán đã được xác nhận. Vui lòng đến điểm nhận xe đúng giờ.");
    }
    
    private void updateUIForFailure() {
        ivStatusIcon.setImageResource(R.drawable.ic_error_24dp);
        ivStatusIcon.setColorFilter(getResources().getColor(R.color.red, null));
        tvStatusTitle.setText("Đặt xe thất bại");
        tvStatusTitle.setTextColor(getResources().getColor(R.color.red, null));
        tvStatusMessage.setText("Thanh toán không thành công. Vui lòng thử lại.");
        btnViewBookings.setVisibility(View.GONE);
    }
    
    private void loadBookingDataFromIntent() {
        // Get booking info from SessionManager (saved before PayOS redirect)
        String bookingId = sessionManager.getPendingBookingId();
        String vehicleId = sessionManager.getPendingVehicleId();
        String vehicleName = sessionManager.getPendingVehicleName();
        String startTime = sessionManager.getPendingStartTime();
        String endTime = sessionManager.getPendingEndTime();
        double amount = sessionManager.getPendingAmount();
        double deposit = sessionManager.getPendingDeposit();
        
        // Clear pending booking data after retrieval
        if (isSuccess) {
            sessionManager.clearPendingBooking();
        }
        
        // Set booking ID
        if (bookingId != null) {
            tvBookingId.setText("#" + bookingId);
        }
        
        // Set vehicle name
        if (vehicleName != null) {
            tvVehicleName.setText(vehicleName);
        }
        
        // Load vehicle image and license plate from API
        if (vehicleId != null) {
            loadVehicleDetails(vehicleId);
        }
        
        // Format and set dates/times
        try {
            if (startTime != null) {
                Date startDate = apiFormat.parse(startTime);
                if (startDate != null) {
                    tvStartDate.setText(displayDateFormat.format(startDate));
                    tvStartTime.setText(displayTimeFormat.format(startDate));
                }
            }
            
            if (endTime != null) {
                Date endDate = apiFormat.parse(endTime);
                if (endDate != null) {
                    tvEndDate.setText(displayDateFormat.format(endDate));
                    tvEndTime.setText(displayTimeFormat.format(endDate));
                }
            }
            
            // Calculate duration
            if (startTime != null && endTime != null) {
                Date start = apiFormat.parse(startTime);
                Date end = apiFormat.parse(endTime);
                if (start != null && end != null) {
                    long diffMs = end.getTime() - start.getTime();
                    long days = diffMs / (1000 * 60 * 60 * 24);
                    long hours = (diffMs / (1000 * 60 * 60)) % 24;
                    
                    String durationText = days + " ngày";
                    if (hours > 0) {
                        durationText += " " + hours + " giờ";
                    }
                    tvRentalDurationLabel.setText("Giá thuê (" + durationText + ")");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing dates", e);
        }
        
        // Format and set prices
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvDeposit.setText(formatter.format(deposit) + " VND");
        tvRentalPrice.setText(formatter.format(amount) + " VND");
        tvTax.setText("0 VND");
        tvTotalAmount.setText(formatter.format(amount) + " VND");
    }
    
    private void loadVehicleDetails(String vehicleId) {
        Call<ApiResponse<Vehicle>> call = vehicleApiService.getVehicleById(vehicleId);
        
        call.enqueue(new Callback<ApiResponse<Vehicle>>() {
            @Override
            public void onResponse(Call<ApiResponse<Vehicle>> call, 
                                 Response<ApiResponse<Vehicle>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Vehicle vehicle = response.body().getData();
                    
                    if (vehicle != null) {
                        // Set license plate
                        if (vehicle.getPlateNumber() != null) {
                            tvLicensePlate.setText("Biển số: " + vehicle.getPlateNumber());
                        }
                        
                        // Load vehicle image using Glide
                        String imageUrl = vehicle.getMainImageUrl();
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(BookingResultActivity.this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.xe1)
                                    .error(R.drawable.xe1)
                                    .centerCrop()
                                    .into(ivVehicleImage);
                        }
                    }
                } else {
                    Log.e(TAG, "Failed to load vehicle details: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Vehicle>> call, Throwable t) {
                Log.e(TAG, "Error loading vehicle details", t);
            }
        });
    }
    
    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnViewBookings.setOnClickListener(v -> {
            // Navigate to Bookings tab
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("navigate_to", "bookings");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
        
        btnBackToHome.setOnClickListener(v -> {
            // Navigate to Home
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
