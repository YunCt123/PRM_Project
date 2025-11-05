package com.example.prm_project.activies;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_project.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.Calendar;

public class PaymentActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private MaterialCardView cardPayOS;
    private TextView tvVehicleName, tvPickupTime, tvPickupLocation;
    private TextView tvUnitPrice, tvRentalDuration, tvTotalPrice;
    private RadioGroup rgRentalType;
    private RadioButton rbDaily, rbHourly;
    private EditText etDuration;
    private MaterialButton btnProceedPayment;
    
    private String vehicleName;
    private double unitPricePerHour = 22.0; // 22đ/giờ
    private double unitPricePerDay = 150.0; // 150đ/ngày
    private int duration = 0;
    private boolean isDaily = true;
    private double totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        setContentView(R.layout.activity_payment);
        
        initViews();
        getIntentData();
        setupListeners();
        calculateTotal();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        cardPayOS = findViewById(R.id.cardPayOS);
        tvVehicleName = findViewById(R.id.tvVehicleName);
        tvPickupTime = findViewById(R.id.tvPickupTime);
        tvPickupLocation = findViewById(R.id.tvPickupLocation);
        tvUnitPrice = findViewById(R.id.tvUnitPrice);
        tvRentalDuration = findViewById(R.id.tvRentalDuration);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        rgRentalType = findViewById(R.id.rgRentalType);
        rbDaily = findViewById(R.id.rbDaily);
        rbHourly = findViewById(R.id.rbHourly);
        etDuration = findViewById(R.id.etDuration);
        btnProceedPayment = findViewById(R.id.btnProceedPayment);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        vehicleName = intent.getStringExtra("vehicle_name");
        
        // Set default values if not provided
        if (vehicleName == null) vehicleName = "VinFast VF8";
        
        // Update UI
        tvVehicleName.setText(vehicleName);
        tvPickupTime.setText(getCurrentTime());
        tvPickupLocation.setText("EV Station - Nguyen Hue");
        updatePriceDisplay();
    }
    
    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return String.format("%02d:%02d", hour, minute);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        // Rental type radio group
        rgRentalType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbDaily) {
                isDaily = true;
                etDuration.setHint("Nhập số ngày");
            } else {
                isDaily = false;
                etDuration.setHint("Nhập số giờ");
            }
            calculateTotal();
        });
        
        // Duration input
        etDuration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    try {
                        duration = Integer.parseInt(s.toString());
                        calculateTotal();
                    } catch (NumberFormatException e) {
                        duration = 0;
                        calculateTotal();
                    }
                } else {
                    duration = 0;
                    calculateTotal();
                }
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        // Pickup time picker
        tvPickupTime.setOnClickListener(v -> showTimePicker());
        
        // PayOS card - go to verify
        cardPayOS.setOnClickListener(v -> openVerifyScreen());
        
        // Proceed payment button
        btnProceedPayment.setOnClickListener(v -> proceedPayment());
    }
    
    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
            (view, hourOfDay, minuteOfHour) -> {
                String time = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                tvPickupTime.setText(time);
            }, hour, minute, true);
        timePickerDialog.show();
    }
    
    private void openVerifyScreen() {
        Intent intent = new Intent(this, VerifyAccountActivity.class);
        startActivity(intent);
    }

    private void calculateTotal() {
        if (duration > 0) {
            if (isDaily) {
                totalPrice = unitPricePerDay * duration;
            } else {
                totalPrice = unitPricePerHour * duration;
            }
            
            // Enable button
            btnProceedPayment.setEnabled(true);
            btnProceedPayment.setBackgroundTintList(getResources().getColorStateList(R.color.primary_blue, null));
            btnProceedPayment.setTextColor(getColor(R.color.white));
        } else {
            totalPrice = 0;
            // Disable button
            btnProceedPayment.setEnabled(false);
            btnProceedPayment.setBackgroundTintList(getResources().getColorStateList(R.color.gray_300, null));
            btnProceedPayment.setTextColor(getColor(R.color.gray_500));
        }
        
        updatePriceDisplay();
    }
    
    private void updatePriceDisplay() {
        // Unit price
        if (isDaily) {
            tvUnitPrice.setText((int)unitPricePerDay + "đ/ngày");
        } else {
            tvUnitPrice.setText((int)unitPricePerHour + "đ/giờ");
        }
        
        // Duration
        if (duration > 0) {
            if (isDaily) {
                tvRentalDuration.setText(duration + " ngày");
            } else {
                tvRentalDuration.setText(duration + " giờ");
            }
        } else {
            tvRentalDuration.setText("-");
        }
        
        // Total
        String totalText = (int)totalPrice + "đ";
        tvTotalPrice.setText(totalText);
    }

    private void proceedPayment() {
        if (duration == 0) {
            Toast.makeText(this, "Vui lòng nhập thời gian thuê", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show loading dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang kết nối PayOS...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        
        // Simulate PayOS integration
        new android.os.Handler().postDelayed(() -> {
            progressDialog.dismiss();
            
            // Show PayOS payment options
            showPayOSPaymentOptions();
        }, 1500);
    }

    private void showPayOSPaymentOptions() {
        // Create dialog to show PayOS payment methods
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Chọn phương thức PayOS");
        
        String[] options = {"QR Code", "Thẻ ATM/Ngân hàng", "Ví điện tử"};
        
        builder.setItems(options, (dialog, which) -> {
            String selectedOption = options[which];
            
            // Show processing dialog
            ProgressDialog processingDialog = new ProgressDialog(this);
            processingDialog.setMessage("Đang xử lý thanh toán...");
            processingDialog.setCancelable(false);
            processingDialog.show();
            
            // Simulate payment processing
            new android.os.Handler().postDelayed(() -> {
                processingDialog.dismiss();
                
                // Show success message
                Toast.makeText(this, "Thanh toán thành công qua " + selectedOption, Toast.LENGTH_SHORT).show();
                
                // Return result
                Intent intent = new Intent();
                intent.putExtra("payment_method", "PayOS - " + selectedOption);
                intent.putExtra("amount", totalPrice);
                intent.putExtra("duration", duration);
                intent.putExtra("rental_type", isDaily ? "Theo ngày" : "Theo giờ");
                intent.putExtra("transaction_id", "PAYOS" + System.currentTimeMillis());
                setResult(RESULT_OK, intent);
                finish();
            }, 2000);
        });
        
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
