package com.example.prm_project.activies;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_project.R;
import com.example.prm_project.models.BookingResponse;
import com.example.prm_project.repository.BookingRepository;
import com.example.prm_project.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class PaymentActivity extends AppCompatActivity {
    private static final String TAG = "PaymentActivity";

    private ImageButton btnBack;
    private MaterialCardView cardPayOS;
    private TextView tvVehicleName, tvPickupLocation;
    private TextView tvStartDateTime, tvEndDateTime, tvDurationCalculated;
    private TextView tvEndDateTimeLabel, tvHourlyDurationLabel;
    private EditText etHourlyDuration;
    private TextView tvUnitPrice, tvRentalDuration, tvTotalPrice;
    private RadioGroup rgRentalType;
    private RadioButton rbDaily, rbHourly;
    private MaterialButton btnProceedPayment;
    
    private String vehicleId;
    private String vehicleName;
    private String stationName;
    private double unitPricePerHour; // 22đ/giờ
    private double unitPricePerDay; // 150đ/ngày
    private boolean isDaily = true;
    private double totalPrice;
    
    // For API booking
    private Calendar startDateTime;
    private Calendar endDateTime;
    private BookingRepository bookingRepository;
    private SessionManager sessionManager;
    private SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        setContentView(R.layout.activity_payment);
        
        // Initialize repositories
        sessionManager = new SessionManager(this);
        
        // Security check: Ensure user is logged in
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Bạn cần đăng nhập để đặt xe", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }
        
        // Security check: Ensure user is verified
        if (!sessionManager.isVerified()) {
            Toast.makeText(this, "Bạn cần xác minh tài khoản để đặt xe", Toast.LENGTH_SHORT).show();
            Intent verifyIntent = new Intent(this, VerifyAccountActivity.class);
            startActivity(verifyIntent);
            finish();
            return;
        }
        
        // Initialize API format timezone
        apiFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        // Initialize repository
        bookingRepository = new BookingRepository(this);
        
        // Initialize calendars - default start now, end 1 day later
        startDateTime = Calendar.getInstance();
        endDateTime = Calendar.getInstance();
        endDateTime.add(Calendar.DAY_OF_MONTH, 1);
        
        initViews();
        getIntentData();
        setupListeners();
        
        // Set initial UI state (default is daily)
        toggleRentalTypeUI();
        updateDisplay();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        cardPayOS = findViewById(R.id.cardPayOS);
        tvVehicleName = findViewById(R.id.tvVehicleName);
        tvStartDateTime = findViewById(R.id.tvStartDateTime);
        tvEndDateTime = findViewById(R.id.tvEndDateTime);
        tvEndDateTimeLabel = findViewById(R.id.tvEndDateTimeLabel);
        tvHourlyDurationLabel = findViewById(R.id.tvHourlyDurationLabel);
        etHourlyDuration = findViewById(R.id.etHourlyDuration);
        tvDurationCalculated = findViewById(R.id.tvDurationCalculated);
        tvPickupLocation = findViewById(R.id.tvPickupLocation);
        tvUnitPrice = findViewById(R.id.tvUnitPrice);
        tvRentalDuration = findViewById(R.id.tvRentalDuration);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        rgRentalType = findViewById(R.id.rgRentalType);
        rbDaily = findViewById(R.id.rbDaily);
        rbHourly = findViewById(R.id.rbHourly);
        btnProceedPayment = findViewById(R.id.btnProceedPayment);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        vehicleId = intent.getStringExtra("vehicle_id");
        vehicleName = intent.getStringExtra("vehicle_name");
        stationName = intent.getStringExtra("station_name");
        
        // Get prices from intent
        double pricePerDay = intent.getDoubleExtra("price_per_day", 0);
        double pricePerHour = intent.getDoubleExtra("price_per_hour", 0);
        
        if (pricePerDay > 0) {
            unitPricePerDay = pricePerDay;
        }
        if (pricePerHour > 0) {
            unitPricePerHour = pricePerHour;
        }
        
        // Set default values if not provided
        if (vehicleName == null) vehicleName = "VinFast VF8";
        if (stationName == null || stationName.isEmpty()) stationName = "EV Station";
        
        // Update UI
        tvVehicleName.setText(vehicleName);
        tvPickupLocation.setText(stationName);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        // Start date time picker
        tvStartDateTime.setOnClickListener(v -> showDateTimePicker(true));
        
        // End date time picker (only for daily)
        tvEndDateTime.setOnClickListener(v -> showDateTimePicker(false));
        
        // Rental type radio group - toggle UI
        rgRentalType.setOnCheckedChangeListener((group, checkedId) -> {
            isDaily = (checkedId == R.id.rbDaily);
            toggleRentalTypeUI();
            updateDisplay();
        });
        
        // Hourly duration input
        etHourlyDuration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isDaily && s.length() > 0) {
                    try {
                        int hours = Integer.parseInt(s.toString());
                        // Calculate end time based on hours
                        endDateTime = (Calendar) startDateTime.clone();
                        endDateTime.add(Calendar.HOUR_OF_DAY, hours);
                        updateDisplay();
                    } catch (NumberFormatException e) {
                        // Invalid number
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        // PayOS card - go to verify
        cardPayOS.setOnClickListener(v -> openVerifyScreen());
        
        // Proceed payment button
        btnProceedPayment.setOnClickListener(v -> proceedPayment());
    }
    
    private void toggleRentalTypeUI() {
        if (isDaily) {
            // Show end date picker, hide hourly input
            tvEndDateTimeLabel.setVisibility(View.VISIBLE);
            tvEndDateTime.setVisibility(View.VISIBLE);
            tvHourlyDurationLabel.setVisibility(View.GONE);
            etHourlyDuration.setVisibility(View.GONE);
            etHourlyDuration.setText("");
            
            // Reset end date to 1 day after start
            endDateTime = (Calendar) startDateTime.clone();
            endDateTime.add(Calendar.DAY_OF_MONTH, 1);
        } else {
            // Hide end date picker, show hourly input
            tvEndDateTimeLabel.setVisibility(View.GONE);
            tvEndDateTime.setVisibility(View.GONE);
            tvHourlyDurationLabel.setVisibility(View.VISIBLE);
            etHourlyDuration.setVisibility(View.VISIBLE);
            
            // Reset hourly duration
            etHourlyDuration.setText("");
            endDateTime = (Calendar) startDateTime.clone();
        }
    }
    
    private void showDateTimePicker(boolean isStartDate) {
        Calendar calendar = isStartDate ? startDateTime : endDateTime;
        
        // Show date picker first
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                
                // Then show time picker
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    (timeView, hourOfDay, minute) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        
                        // Validate dates
                        if (!isStartDate && endDateTime.before(startDateTime)) {
                            Toast.makeText(this, "Thời gian kết thúc phải sau thời gian bắt đầu", Toast.LENGTH_SHORT).show();
                            // Reset end date to 1 day after start
                            endDateTime = (Calendar) startDateTime.clone();
                            endDateTime.add(Calendar.DAY_OF_MONTH, 1);
                        }
                        
                        updateDisplay();
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                );
                timePickerDialog.show();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        // Set minimum date to today for start date
        if (isStartDate) {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        } else {
            datePickerDialog.getDatePicker().setMinDate(startDateTime.getTimeInMillis());
        }
        
        datePickerDialog.show();
    }
    
    private void openVerifyScreen() {
        Intent intent = new Intent(this, VerifyAccountActivity.class);
        startActivity(intent);
    }

    private void updateDisplay() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        
        // Update date displays
        tvStartDateTime.setText(displayFormat.format(startDateTime.getTime()));
        tvEndDateTime.setText(displayFormat.format(endDateTime.getTime()));
        
        // Calculate duration
        long diffInMillis = endDateTime.getTimeInMillis() - startDateTime.getTimeInMillis();
        long diffInHours = diffInMillis / (1000 * 60 * 60);
        long days = diffInHours / 24;
        long hours = diffInHours % 24;
        
        // Display duration
        String durationText;
        if (days > 0 && hours > 0) {
            durationText = days + " ngày " + hours + " giờ";
        } else if (days > 0) {
            durationText = days + " ngày";
        } else {
            durationText = hours + " giờ";
        }
        tvDurationCalculated.setText(durationText);
        tvRentalDuration.setText(durationText);
        
        // Calculate total price based on days and hours
        totalPrice = (days * unitPricePerDay) + (hours * unitPricePerHour);
        
        // Unit price display
        if (isDaily) {
            tvUnitPrice.setText(formatter.format(unitPricePerDay) + " ₫/ngày");
        } else {
            tvUnitPrice.setText(formatter.format(unitPricePerHour) + " ₫/giờ");
        }
        
        // Total price
        tvTotalPrice.setText(formatter.format(totalPrice) + " ₫");
        
        // Enable/disable button
        if (totalPrice > 0 && diffInMillis > 0) {
            btnProceedPayment.setEnabled(true);
            btnProceedPayment.setBackgroundTintList(getResources().getColorStateList(R.color.primary_blue, null));
            btnProceedPayment.setTextColor(getColor(R.color.white));
        } else {
            btnProceedPayment.setEnabled(false);
            btnProceedPayment.setBackgroundTintList(getResources().getColorStateList(R.color.gray_300, null));
            btnProceedPayment.setTextColor(getColor(R.color.gray_500));
        }
    }

    private void proceedPayment() {
        // Validate dates
        if (endDateTime.before(startDateTime) || endDateTime.equals(startDateTime)) {
            Toast.makeText(this, "Thời gian kết thúc phải sau thời gian bắt đầu", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Validate user is logged in
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập để đặt xe", Toast.LENGTH_LONG).show();
            // Navigate to login if needed
            return;
        }
        
        // Validate vehicle ID
        if (vehicleId == null || vehicleId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin xe", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tạo booking với PayOS...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        
        // Format dates to API format
        String startTimeStr = apiFormat.format(startDateTime.getTime());
        String endTimeStr = apiFormat.format(endDateTime.getTime());
        
        Log.d(TAG, "Creating booking: vehicleId=" + vehicleId + ", start=" + startTimeStr + ", end=" + endTimeStr);
        
        // Call API to create booking using LiveData
        bookingRepository.createBooking(vehicleId, startTimeStr, endTimeStr).observe(this, response -> {
            progressDialog.dismiss();
            
            if (response != null) {
                // Log response
                Log.d(TAG, "Booking created successfully!");
                Log.d(TAG, "Booking ID: " + response.getBookingId());
                Log.d(TAG, "Status: " + response.getStatus());
                Log.d(TAG, "Amount: " + response.getAmountEstimated() + " VND");
                Log.d(TAG, "Checkout URL: " + response.getCheckoutUrl());
                
                // Show success message
                Toast.makeText(PaymentActivity.this, 
                    "Đã tạo booking! Chuyển đến trang thanh toán PayOS...", 
                    Toast.LENGTH_LONG).show();
                
                // Save booking data to SharedPreferences for later retrieval
                sessionManager.saveBookingData(
                    response.getBookingId(),
                    vehicleId,
                    vehicleName,
                    startTimeStr,
                    endTimeStr,
                    response.getAmountEstimated(),
                    response.getDeposit() != null ? response.getDeposit().getAmount() : 0
                );
                
                // Open PayOS checkout URL in browser
                if (response.getCheckoutUrl() != null && !response.getCheckoutUrl().isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(response.getCheckoutUrl()));
                    startActivity(browserIntent);
                    
                    // Finish this activity - will wait for PayOS callback
                    finish();
                } else {
                    Toast.makeText(PaymentActivity.this, "Không thể mở trang thanh toán", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Booking failed: response is null");
                Toast.makeText(PaymentActivity.this, 
                    "Lỗi tạo booking. Vui lòng thử lại.", 
                    Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
