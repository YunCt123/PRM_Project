package com.example.prm_project.ui.booking;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import androidx.core.text.HtmlCompat;

import com.example.prm_project.R;
import com.example.prm_project.models.Booking;
import com.example.prm_project.models.Vehicle;
import com.example.prm_project.repository.BookingRespository;
import com.example.prm_project.repository.VehicleRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class BookingActivity extends AppCompatActivity {

    private TextView tvPickupDate, tvReturnDate, tvPickupTime, tvReturnTime;
    private TextInputEditText etDuration;
    private TextView tvVehicleName, tvPrice, tvUploadProgress;
    private ImageView ivVehicleImage;
    private Calendar pickupCalendar, returnCalendar;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private boolean pickupTimeSelected = false;

    // Customer Information
    private EditText etFullName, etEmail, etPhone, etLicenseNumber;

    // Upload areas
    private LinearLayout uploadAreaLicense, uploadAreaIdFront, uploadAreaIdBack;
    private ProgressBar progressUpload;

    // Checkboxes and Spinner
    private Spinner spinnerRentalType;
    private CheckBox cbInsurance, cbTerms;
    private TextView tvTermsText;

    private String vehicleName;
    private String vehiclePrice;
    private int vehicleImageRes;
    private String vehicleId;
    private int documentsUploaded = 0;
    private BookingRespository bookingRepository;
    private VehicleRepository vehicleRepository;
    private Vehicle vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Get vehicle data from intent
        Intent intent = getIntent();
        if (intent != null) {
            vehicleName = intent.getStringExtra("vehicle_name");
            vehiclePrice = intent.getStringExtra("vehicle_price");
            vehicleImageRes = intent.getIntExtra("vehicle_image", R.drawable.xe1);
            vehicleId = intent.getStringExtra("vehicle_id");
            if (vehicleId != null) {
                fetchVehicleDetails(vehicleId);
            }
        }

        initViews();
        setupClickListeners();
        initializeCalendars();
        setupSpinner();
        bookingRepository = new BookingRespository(this);
        vehicleRepository = new VehicleRepository();
    }

    private void initViews() {
        // Header
        TextView btnBack = findViewById(R.id.btn_booking_back);

        // Vehicle info
        ivVehicleImage = findViewById(R.id.iv_booking_vehicle_image);
        tvVehicleName = findViewById(R.id.tv_booking_vehicle_name);
        tvPrice = findViewById(R.id.tv_booking_price);

        // Date/Time selectors
        tvPickupDate = findViewById(R.id.tv_pickup_date);
        tvReturnDate = findViewById(R.id.tv_return_date);
        tvPickupTime = findViewById(R.id.tv_pickup_time);
        tvReturnTime = findViewById(R.id.tv_return_time);
        etDuration = findViewById(R.id.et_duration);

        // Customer Information
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etLicenseNumber = findViewById(R.id.et_license_number);

        // Upload areas
        uploadAreaLicense = findViewById(R.id.upload_area_license);
        uploadAreaIdFront = findViewById(R.id.upload_area_id_front);
        uploadAreaIdBack = findViewById(R.id.upload_area_id_back);
        progressUpload = findViewById(R.id.progress_upload);
        tvUploadProgress = findViewById(R.id.tv_upload_progress);

        // Spinner and checkboxes
        spinnerRentalType = findViewById(R.id.spinner_rental_type);
        cbInsurance = findViewById(R.id.cb_insurance);
        cbTerms = findViewById(R.id.cb_terms);
        tvTermsText = findViewById(R.id.tv_terms_text);

        // Buttons
        Button btnConfirmBooking = findViewById(R.id.btn_confirm_booking);

        // Set vehicle data
        ivVehicleImage.setImageResource(vehicleImageRes);
        tvVehicleName.setText(vehicleName);
        tvPrice.setText("Loading price..."); // Will be updated after fetching vehicle details

        // Setup terms text with HTML links
        setupTermsText();

        btnBack.setOnClickListener(v -> finish());
        btnConfirmBooking.setOnClickListener(v -> confirmBooking());
    }

    private void setupTermsText() {
        // Set HTML text with clickable links
        String termsText = getString(R.string.terms_conditions);
        tvTermsText.setText(HtmlCompat.fromHtml(termsText, HtmlCompat.FROM_HTML_MODE_LEGACY));
        tvTermsText.setMovementMethod(LinkMovementMethod.getInstance());

        // Make links blue color
        tvTermsText.setLinkTextColor(getResources().getColor(R.color.primary_blue));
    }

    private void setupClickListeners() {
        tvPickupDate.setOnClickListener(v -> showDatePicker(true));
        tvPickupTime.setOnClickListener(v -> showTimePicker(true));

        // Add text change listener for duration to recalculate return time
        etDuration.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateReturnTime();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Upload area click listeners
        uploadAreaLicense.setOnClickListener(v -> {
            Toast.makeText(this, "Upload Driver's License", Toast.LENGTH_SHORT).show();
            simulateUpload();
        });

        uploadAreaIdFront.setOnClickListener(v -> {
            Toast.makeText(this, "Upload National ID Front", Toast.LENGTH_SHORT).show();
            simulateUpload();
        });

        uploadAreaIdBack.setOnClickListener(v -> {
            Toast.makeText(this, "Upload National ID Back", Toast.LENGTH_SHORT).show();
            simulateUpload();
        });
    }

    private void setupSpinner() {
        String[] rentalTypes = {"Daily Rental", "Hourly Rental", "Weekly Rental", "Monthly Rental"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, rentalTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRentalType.setAdapter(adapter);

        // Add listener to recalculate return time when rental type changes
        spinnerRentalType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                calculateReturnTime();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void simulateUpload() {
        if (documentsUploaded < 3) {
            documentsUploaded++;
            int progress = (documentsUploaded * 100) / 3;
            progressUpload.setProgress(progress);
            tvUploadProgress.setText(documentsUploaded + "/3 documents uploaded");

            if (documentsUploaded == 3) {
                tvUploadProgress.setTextColor(getResources().getColor(R.color.primary_green));
            }
        }
    }

    private void initializeCalendars() {
        pickupCalendar = Calendar.getInstance();
        returnCalendar = Calendar.getInstance();
        // Set default return time to 1 day later
        returnCalendar.add(Calendar.DAY_OF_MONTH, 1);

        Log.d("BookingActivity", "Calendars initialized - pickup: " + pickupCalendar.getTime() + ", return: " + returnCalendar.getTime());
        Log.d("BookingActivity", "Calendars millis - pickup: " + pickupCalendar.getTimeInMillis() + ", return: " + returnCalendar.getTimeInMillis());

        updateDateTimeDisplays();
    }

    private void showDatePicker(boolean isPickup) {
        Log.d("BookingActivity", "showDatePicker called - isPickup: " + isPickup);
        Calendar calendar = isPickup ? pickupCalendar : returnCalendar;
        Log.d("BookingActivity", "Current calendar time: " + calendar.getTime());

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if (isPickup) {
                    pickupTimeSelected = true;
                    calculateReturnTime();
                }
                updateDateTimeDisplays();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showTimePicker(boolean isPickup) {
        Log.d("BookingActivity", "showTimePicker called - isPickup: " + isPickup);
        Calendar calendar = isPickup ? pickupCalendar : returnCalendar;
        Log.d("BookingActivity", "Current calendar time: " + calendar.getTime());

        TimePickerDialog timePickerDialog = new TimePickerDialog(
            this,
            (view, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                if (isPickup) {
                    pickupTimeSelected = true;
                    calculateReturnTime();
                }
                updateDateTimeDisplays();

                // Log the selected duration in hours
                long diffMillis = returnCalendar.getTimeInMillis() - pickupCalendar.getTimeInMillis();
                double hours = diffMillis / (1000.0 * 60 * 60);
                Log.d("BookingActivity", "Selected duration in hours: " + hours);
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        );

        timePickerDialog.show();
    }

    private void calculateReturnTime() {
        String durationText = etDuration.getText() != null ? etDuration.getText().toString().trim() : "";
        if (durationText.isEmpty()) {
            // Set default return time to 1 day later
            returnCalendar = (Calendar) pickupCalendar.clone();
            returnCalendar.add(Calendar.DAY_OF_MONTH, 1);
            updateDateTimeDisplays();
            return;
        }

        try {
            int duration = Integer.parseInt(durationText);
            if (duration <= 0) {
                return;
            }

            // Clone pickup calendar to avoid modifying original
            returnCalendar = (Calendar) pickupCalendar.clone();

            // Get selected rental type
            String selectedType = spinnerRentalType.getSelectedItem() != null ?
                    spinnerRentalType.getSelectedItem().toString() : "Daily Rental";

            // Calculate return time based on rental type
            switch (selectedType) {
                case "Hourly Rental":
                    returnCalendar.add(Calendar.HOUR_OF_DAY, duration);
                    break;
                case "Daily Rental":
                    returnCalendar.add(Calendar.DAY_OF_MONTH, duration);
                    break;
                case "Weekly Rental":
                    returnCalendar.add(Calendar.DAY_OF_MONTH, duration * 7);
                    break;
                case "Monthly Rental":
                    returnCalendar.add(Calendar.MONTH, duration);
                    break;
                default:
                    returnCalendar.add(Calendar.DAY_OF_MONTH, duration);
                    break;
            }

            updateDateTimeDisplays();

        } catch (NumberFormatException e) {
            // Invalid duration, set default to 1 day
            returnCalendar = (Calendar) pickupCalendar.clone();
            returnCalendar.add(Calendar.DAY_OF_MONTH, 1);
            updateDateTimeDisplays();
        }
    }

    private void updateDateTimeDisplays() {
        tvPickupDate.setText(dateFormat.format(pickupCalendar.getTime()));
        tvReturnDate.setText(dateFormat.format(returnCalendar.getTime()));
        tvPickupTime.setText(timeFormat.format(pickupCalendar.getTime()));
        tvReturnTime.setText(timeFormat.format(returnCalendar.getTime()));
    }

    private void confirmBooking() {
        // Validate form
        if (!validateForm()) {
            return;
        }

        // Check if user has selected pickup time
        if (!pickupTimeSelected) {
            Toast.makeText(this, "Please select pickup date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if pickup time is not in the past
        long currentTime = System.currentTimeMillis();
        long pickupTime = pickupCalendar.getTimeInMillis();
        if (pickupTime < currentTime - 300000) { // More than 5 minutes ago
            Toast.makeText(this, "Pickup time cannot be in the past", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ensure return time is after pickup time
        if (returnCalendar.getTimeInMillis() <= pickupCalendar.getTimeInMillis()) {
            // Force return time to be at least 1 hour after pickup
            returnCalendar = (Calendar) pickupCalendar.clone();
            returnCalendar.add(Calendar.HOUR_OF_DAY, 1);
            Toast.makeText(this, "Return time adjusted to be after pickup time", Toast.LENGTH_SHORT).show();
        }

        // Check if vehicle details are loaded
        if (vehicle == null) {
            Toast.makeText(this, "Vehicle details not loaded yet. Please wait.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Navigate to PaymentActivity with booking details
        Intent paymentIntent = new Intent(this, com.example.prm_project.activies.PaymentActivity.class);
        paymentIntent.putExtra("vehicle_name", vehicleName);
        paymentIntent.putExtra("vehicle_id", this.vehicleId);

        // Pass pickup and return times for booking creation
        Log.d("BookingActivity", "About to send times - pickup calendar: " + pickupCalendar.getTime() + ", return calendar: " + returnCalendar.getTime());
        long pickupMillis = pickupCalendar.getTimeInMillis();
        long returnMillis = returnCalendar.getTimeInMillis();
        Log.d("BookingActivity", "Sending pickupTime: " + pickupMillis + " (" + new java.util.Date(pickupMillis) + ")");
        Log.d("BookingActivity", "Sending returnTime: " + returnMillis + " (" + new java.util.Date(returnMillis) + ")");
        Log.d("BookingActivity", "Time difference: " + (returnMillis - pickupMillis) + " ms");

        paymentIntent.putExtra("pickup_time", pickupMillis);
        paymentIntent.putExtra("return_time", returnMillis);

        startActivity(paymentIntent);
        finish();
    }

    private boolean validateForm() {
        // Check customer information
        if (etFullName.getText().toString().trim().isEmpty()) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return false;
        }

        if (etEmail.getText().toString().trim().isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }

        if (etPhone.getText().toString().trim().isEmpty()) {
            etPhone.setError("Phone number is required");
            etPhone.requestFocus();
            return false;
        }

        if (etLicenseNumber.getText().toString().trim().isEmpty()) {
            etLicenseNumber.setError("Driver's license number is required");
            etLicenseNumber.requestFocus();
            return false;
        }

        // Check duration
        String durationText = etDuration.getText() != null ? etDuration.getText().toString().trim() : "";
        if (durationText.isEmpty()) {
            etDuration.setError("Duration is required");
            etDuration.requestFocus();
            return false;
        }

        try {
            int duration = Integer.parseInt(durationText);
            if (duration <= 0) {
                etDuration.setError("Duration must be greater than 0");
                etDuration.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etDuration.setError("Please enter a valid number");
            etDuration.requestFocus();
            return false;
        }

        // Check documents uploaded
        if (documentsUploaded < 3) {
            Toast.makeText(this, "Please upload all required documents", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check terms agreement
        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Please agree to terms and conditions", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void fetchVehicleDetails(String vehicleId) {
        vehicleRepository.getVehicleById(vehicleId, new VehicleRepository.SingleVehicleCallback() {
            @Override
            public void onSuccess(Vehicle fetchedVehicle) {
                vehicle = fetchedVehicle;
                // Update price display
                if (vehicle != null) {
                    tvPrice.setText(String.format("%.0f VND/hour", vehicle.getPricePerHour()));
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(BookingActivity.this, "Failed to load vehicle details: " + errorMessage, Toast.LENGTH_SHORT).show();
                tvPrice.setText("Price unavailable");
            }
        });
    }

    private String formatDateTime(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private void setupBackButton() {
    TextView btnBack = findViewById(R.id.btn_booking_back);
    btnBack.setOnClickListener(v -> {
        finish(); // Close this activity and return to previous screen
    });
    }
}
