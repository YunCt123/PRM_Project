package com.example.prm_project.activies;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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
import com.example.prm_project.models.Booking;
import com.example.prm_project.models.Vehicle;
import com.example.prm_project.repository.BookingRespository;
import com.example.prm_project.repository.VehicleRepository;
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
    private String vehicleId;
    private long pickupTimeMillis;
    private long returnTimeMillis;

    private double vehicleRent;
    private double unitPricePerHour = 0.0; // Will be set from API
    private double unitPricePerDay = 0.0; // Will be set from API
    private int duration = 0;
    private boolean isDaily = true;
    private double totalPrice = 0;
    private VehicleRepository vehicleRepository;
    private BookingRespository bookingRepository;
    private Vehicle vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        setContentView(R.layout.activity_payment);
        
        initViews();
        vehicleRepository = new VehicleRepository();
        bookingRepository = new BookingRespository(this);
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
        vehicleId = intent.getStringExtra("vehicle_id");
        pickupTimeMillis = intent.getLongExtra("pickup_time", 0);
        returnTimeMillis = intent.getLongExtra("return_time", 0);

        Log.d("PaymentActivity", "Intent extras received:");
        Log.d("PaymentActivity", "Has pickup_time: " + intent.hasExtra("pickup_time"));
        Log.d("PaymentActivity", "Has return_time: " + intent.hasExtra("return_time"));
        Log.d("PaymentActivity", "pickup_time value: " + pickupTimeMillis);
        Log.d("PaymentActivity", "return_time value: " + returnTimeMillis);
        Log.d("PaymentActivity", "All extras: " + intent.getExtras());

        // Debug logging
        Log.d("PaymentActivity", "vehicleName: " + vehicleName);
        Log.d("PaymentActivity", "vehicleId: " + vehicleId);
        Log.d("PaymentActivity", "pickupTime: " + pickupTimeMillis + ", returnTime: " + returnTimeMillis);
        Log.d("PaymentActivity", "pickupTime date: " + new java.util.Date(pickupTimeMillis));
        Log.d("PaymentActivity", "returnTime date: " + new java.util.Date(returnTimeMillis));
        Log.d("PaymentActivity", "time diff: " + (returnTimeMillis - pickupTimeMillis) + " ms");

        // If times are not set, set default values for testing
        if (pickupTimeMillis == 0) {
            Log.d("PaymentActivity", "pickupTimeMillis is 0, setting default");
            pickupTimeMillis = System.currentTimeMillis() + 1000 * 60 * 60; // 1 hour from now
            returnTimeMillis = pickupTimeMillis + 1000L * 60 * 60 * 24 * duration; // duration days
            Log.d("PaymentActivity", "Default pickupTime: " + pickupTimeMillis + ", returnTime: " + returnTimeMillis);
        }

        // Set default values if not provided
        if (vehicleName == null) vehicleName = "VinFast VF8";

        // Update UI
        tvVehicleName.setText(vehicleName);
        if (pickupTimeMillis > 0) {
            tvPickupTime.setText(new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                    .format(new java.util.Date(pickupTimeMillis)));
        } else {
            tvPickupTime.setText(getCurrentTime());
        }
        tvPickupLocation.setText("EV Station - Nguyen Hue");

        // Calculate duration from times if provided
        if (pickupTimeMillis > 0 && returnTimeMillis > 0) {
            long diffMillis = returnTimeMillis - pickupTimeMillis;
            long hours = diffMillis / (1000 * 60 * 60);
            if (isDaily) {
                duration = (int) Math.ceil(hours / 24.0); // Convert to days
            } else {
                duration = (int) hours;
            }
            etDuration.setText(String.valueOf(duration));
        }

        // Fetch vehicle details if vehicleId is provided
        if (vehicleId != null && !vehicleId.isEmpty()) {
            fetchVehicleDetails(vehicleId);
        } else {
            // No vehicleId provided, show error and disable payment
            Toast.makeText(this, "Không có thông tin xe, không thể tính giá", Toast.LENGTH_SHORT).show();
            btnProceedPayment.setEnabled(false);
            btnProceedPayment.setBackgroundTintList(getResources().getColorStateList(R.color.gray_300, null));
            btnProceedPayment.setTextColor(getColor(R.color.gray_500));
        }
    }
    
    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return String.format("%02d:%02d", hour, minute);
    }

    private void fetchVehicleDetails(String vehicleId) {
        Log.d("PaymentActivity", "Fetching vehicle details for ID: " + vehicleId);
        vehicleRepository.getVehicleById(vehicleId, new VehicleRepository.SingleVehicleCallback() {
            @Override
            public void onSuccess(Vehicle fetchedVehicle) {
                Log.d("PaymentActivity", "Vehicle fetched successfully: " + fetchedVehicle);
                if (fetchedVehicle != null) {
                    Log.d("PaymentActivity", "PricePerHour: " + fetchedVehicle.getPricePerHour() + ", PricePerDay: " + fetchedVehicle.getPricePerDay());
                }
                vehicle = fetchedVehicle;
                if (vehicle != null) {
                    unitPricePerHour = vehicle.getPricePerHour();
                    unitPricePerDay = vehicle.getPricePerDay();
                    updatePriceDisplay();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("PaymentActivity", "Error fetching vehicle: " + errorMessage);
                Toast.makeText(PaymentActivity.this, "Không thể tải thông tin xe: " + errorMessage, Toast.LENGTH_SHORT).show();
                // Disable payment since we can't calculate prices
                btnProceedPayment.setEnabled(false);
                btnProceedPayment.setBackgroundTintList(getResources().getColorStateList(R.color.gray_300, null));
                btnProceedPayment.setTextColor(getColor(R.color.gray_500));
            }
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        // Rental type radio group
        rgRentalType.setOnCheckedChangeListener((group, checkedId) -> {
            boolean wasDaily = isDaily;
            if (checkedId == R.id.rbDaily) {
                isDaily = true;
                etDuration.setHint("Nhập số ngày");
            } else {
                isDaily = false;
                etDuration.setHint("Nhập số giờ");
            }

            // Recalculate duration based on current times when switching rental type
            if (pickupTimeMillis > 0 && returnTimeMillis > pickupTimeMillis) {
                long diffMillis = returnTimeMillis - pickupTimeMillis;
                if (isDaily) {
                    duration = (int) Math.ceil(diffMillis / (24.0 * 60 * 60 * 1000)); // Convert to days
                } else {
                    duration = (int) Math.ceil(diffMillis / (60.0 * 60 * 1000)); // Convert to hours
                }
                etDuration.setText(String.valueOf(duration));
            }

            calculateReturnTime();
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
                        calculateReturnTime();
                        calculateTotal();
                    } catch (NumberFormatException e) {
                        duration = 0;
                        calculateReturnTime();
                        calculateTotal();
                    }
                } else {
                    duration = 0;
                    calculateReturnTime();
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
        Intent intent = new Intent(this, com.example.prm_project.VerifyAccountActivity.class);
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

    private void calculateReturnTime() {
        if (pickupTimeMillis > 0 && duration > 0) {
            long multiplier = isDaily ? (24 * 60 * 60 * 1000L) : (60 * 60 * 1000L); // days or hours in millis
            returnTimeMillis = pickupTimeMillis + (duration * multiplier);
            Log.d("PaymentActivity", "Calculated returnTimeMillis: " + returnTimeMillis + " (" + new java.util.Date(returnTimeMillis) + ")");
        } else {
            returnTimeMillis = pickupTimeMillis; // Default to same as pickup if no duration
        }
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
        Log.d("PaymentActivity", "proceedPayment called - duration: " + duration + ", isDaily: " + isDaily);
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
        Log.d("PaymentActivity", "showPayOSPaymentOptions called");
        // Create dialog to show PayOS payment methods
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Chọn phương thức PayOS");

        String[] options = {"QR Code", "Thẻ ATM/Ngân hàng", "Ví điện tử"};

        builder.setItems(options, (dialog, which) -> {
            String selectedOption = options[which];
            Log.d("PaymentActivity", "Selected payment option: " + selectedOption);

            if ("QR Code".equals(selectedOption)) {
                // For QR Code, create booking directly and show QR
                createBookingForQR();
            } else {
                // For other options, simulate payment then create booking
                // Show processing dialog
                ProgressDialog processingDialog = new ProgressDialog(this);
                processingDialog.setMessage("Đang xử lý thanh toán...");
                processingDialog.setCancelable(false);
                processingDialog.show();

                // Simulate payment processing
                new android.os.Handler().postDelayed(() -> {
                    processingDialog.dismiss();

                    // Create booking after successful payment
                    createBookingAfterPayment(selectedOption);
                }, 2000);
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void createBookingForQR() {
        Log.d("PaymentActivity", "createBookingForQR called");
        if (vehicleId == null || vehicle == null) {
            Log.d("PaymentActivity", "vehicleId: " + vehicleId + ", vehicle: " + vehicle);
            Toast.makeText(this, "Không có thông tin xe để tạo booking", Toast.LENGTH_SHORT).show();
            return;
        }

        // Log vehicle valuation
        if (vehicle.getValuation() != null) {
            Log.d("PaymentActivity", "Vehicle valuation valueVND: " + vehicle.getValuation().getValueVND());
        } else {
            Log.d("PaymentActivity", "Vehicle valuation is null");
        }

        // Log times before validation
        Log.d("PaymentActivity", "Before validation - pickupTimeMillis: " + pickupTimeMillis + ", returnTimeMillis: " + returnTimeMillis);
        Log.d("PaymentActivity", "Time difference: " + (returnTimeMillis - pickupTimeMillis) + " ms");

        // Validate times
        if (returnTimeMillis <= pickupTimeMillis) {
            Toast.makeText(this, "Thời gian trả xe phải sau thời gian nhận xe", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use the times from BookingActivity
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
        dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        String startTime = dateFormat.format(new java.util.Date(pickupTimeMillis));
        String endTime = dateFormat.format(new java.util.Date(returnTimeMillis));

        Log.d("PaymentActivity", "Creating booking with startTime: " + startTime + ", endTime: " + endTime);
        Log.d("PaymentActivity", "Raw millis - pickup: " + pickupTimeMillis + ", return: " + returnTimeMillis);
        Log.d("PaymentActivity", "Parsed dates - pickup: " + new java.util.Date(pickupTimeMillis) + ", return: " + new java.util.Date(returnTimeMillis));
        Log.d("PaymentActivity", "Duration: " + duration + ", isDaily: " + isDaily);
        Log.d("PaymentActivity", "Vehicle ID: " + vehicleId + ", Total Price: " + totalPrice);

        // Check if endTime is actually after startTime
        try {
            java.text.SimpleDateFormat parseFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
            parseFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            java.util.Date startDate = parseFormat.parse(startTime);
            java.util.Date endDate = parseFormat.parse(endTime);
            Log.d("PaymentActivity", "Parsed comparison - start: " + startDate + ", end: " + endDate);
            Log.d("PaymentActivity", "Is end after start? " + endDate.after(startDate));
            Log.d("PaymentActivity", "Time difference: " + (endDate.getTime() - startDate.getTime()) + " ms");
        } catch (Exception e) {
            Log.e("PaymentActivity", "Error parsing dates", e);
        }

        // Create deposit - set minimum amount as per API requirement
        long depositAmount = 250000000L; // Minimum deposit 250000000 VND
        Booking.Deposit deposit = new Booking.Deposit(depositAmount, "VND", "payos", "PAYOS" + System.currentTimeMillis());
        Log.d("PaymentActivity", "Deposit - amount: " + depositAmount + ", currency: VND, provider: payos, ref: " + deposit.getProviderRef());

        // Create booking
        Booking booking = new Booking(vehicleId, startTime, endTime, deposit);

        // Call API to create booking
        bookingRepository.createBooking(booking).observe(this, result -> {
            if (result != null) {
                // Show success message
                Toast.makeText(this, "Đặt xe thành công! Đang tạo mã QR...", Toast.LENGTH_SHORT).show();

                // Get QR code from response
                String qrCode = result.getQrCode();
                String checkoutUrl = result.getCheckoutUrl();

                Log.d("PaymentActivity", "QR Code: " + qrCode);
                Log.d("PaymentActivity", "Checkout URL: " + checkoutUrl);

                if (qrCode != null && !qrCode.isEmpty()) {
                    // Generate and display QR code
                    displayQRCode(qrCode, checkoutUrl);
                } else {
                    Toast.makeText(this, "Không thể tạo mã QR. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }

                // Return result
                Intent intent = new Intent();
                intent.putExtra("payment_method", "PayOS - QR Code");
                intent.putExtra("amount", totalPrice);
                intent.putExtra("duration", duration);
                intent.putExtra("rental_type", isDaily ? "Theo ngày" : "Theo giờ");
                intent.putExtra("transaction_id", "PAYOS" + System.currentTimeMillis());
                intent.putExtra("booking_id", result.getVehicleId()); // Or whatever ID the API returns
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Log.e("PaymentActivity", "Booking creation failed for QR");
                // Error toast is shown in BookingRepository
            }
        });
    }

    private void displayQRCode(String qrCode, String checkoutUrl) {
        // For now, open checkout URL in browser
        try {
            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse(checkoutUrl));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Không thể mở trình duyệt", Toast.LENGTH_SHORT).show();
        }
    }

    private void createBookingAfterPayment(String paymentMethod) {
        Log.d("PaymentActivity", "createBookingAfterPayment called with paymentMethod: " + paymentMethod);
        if (vehicleId == null || vehicle == null) {
            Log.d("PaymentActivity", "vehicleId: " + vehicleId + ", vehicle: " + vehicle);
            Toast.makeText(this, "Không có thông tin xe để tạo booking", Toast.LENGTH_SHORT).show();
            return;
        }

        // Log vehicle valuation
        if (vehicle.getValuation() != null) {
            Log.d("PaymentActivity", "Vehicle valuation valueVND: " + vehicle.getValuation().getValueVND());
        } else {
            Log.d("PaymentActivity", "Vehicle valuation is null");
        }

        // Log times before validation
        Log.d("PaymentActivity", "Before validation - pickupTimeMillis: " + pickupTimeMillis + ", returnTimeMillis: " + returnTimeMillis);
        Log.d("PaymentActivity", "Time difference: " + (returnTimeMillis - pickupTimeMillis) + " ms");

        // Validate times
        if (returnTimeMillis <= pickupTimeMillis) {
            Toast.makeText(this, "Thời gian trả xe phải sau thời gian nhận xe", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use the times from BookingActivity
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
        dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        String startTime = dateFormat.format(new java.util.Date(pickupTimeMillis));
        String endTime = dateFormat.format(new java.util.Date(returnTimeMillis));

        Log.d("PaymentActivity", "Creating booking with startTime: " + startTime + ", endTime: " + endTime);
        Log.d("PaymentActivity", "Raw millis - pickup: " + pickupTimeMillis + ", return: " + returnTimeMillis);
        Log.d("PaymentActivity", "Parsed dates - pickup: " + new java.util.Date(pickupTimeMillis) + ", return: " + new java.util.Date(returnTimeMillis));
        Log.d("PaymentActivity", "Duration: " + duration + ", isDaily: " + isDaily);
        Log.d("PaymentActivity", "Vehicle ID: " + vehicleId + ", Total Price: " + totalPrice);

        // Check if endTime is actually after startTime
        try {
            java.text.SimpleDateFormat parseFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
            parseFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            java.util.Date startDate = parseFormat.parse(startTime);
            java.util.Date endDate = parseFormat.parse(endTime);
            Log.d("PaymentActivity", "Parsed comparison - start: " + startDate + ", end: " + endDate);
            Log.d("PaymentActivity", "Is end after start? " + endDate.after(startDate));
            Log.d("PaymentActivity", "Time difference: " + (endDate.getTime() - startDate.getTime()) + " ms");
        } catch (Exception e) {
            Log.e("PaymentActivity", "Error parsing dates", e);
        }

        // Create deposit - set minimum amount as per API requirement
        long depositAmount = 250000000L; // Minimum deposit 250000000 VND
        Booking.Deposit deposit = new Booking.Deposit(depositAmount, "VND", "payos", "PAYOS" + System.currentTimeMillis());
        Log.d("PaymentActivity", "Deposit - amount: " + depositAmount + ", currency: VND, provider: payos, ref: " + deposit.getProviderRef());

        // Create booking
        Booking booking = new Booking(vehicleId, startTime, endTime, deposit);

        // Call API to create booking
        bookingRepository.createBooking(booking).observe(this, result -> {
            if (result != null) {
                // Show success message
                Toast.makeText(this, "Thanh toán và đặt xe thành công qua " + paymentMethod, Toast.LENGTH_SHORT).show();

                // Return result
                Intent intent = new Intent();
                intent.putExtra("payment_method", "PayOS - " + paymentMethod);
                intent.putExtra("amount", totalPrice);
                intent.putExtra("duration", duration);
                intent.putExtra("rental_type", isDaily ? "Theo ngày" : "Theo giờ");
                intent.putExtra("transaction_id", "PAYOS" + System.currentTimeMillis());
                intent.putExtra("booking_id", result.getVehicleId()); // Or whatever ID the API returns
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Log.e("PaymentActivity", "Booking creation failed after payment");
                // Error toast is shown in BookingRepository
                // Still return success for payment but with booking failure note
                Intent intent = new Intent();
                intent.putExtra("payment_method", "PayOS - " + paymentMethod);
                intent.putExtra("amount", totalPrice);
                intent.putExtra("duration", duration);
                intent.putExtra("rental_type", isDaily ? "Theo ngày" : "Theo giờ");
                intent.putExtra("transaction_id", "PAYOS" + System.currentTimeMillis());
                intent.putExtra("booking_failed", true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
