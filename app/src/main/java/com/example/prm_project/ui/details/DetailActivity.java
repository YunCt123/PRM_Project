package com.example.prm_project.ui.details;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.prm_project.R;
import com.example.prm_project.activies.PaymentActivity;
import com.example.prm_project.ui.home.Vehicle;

import java.text.NumberFormat;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private Vehicle vehicle;
    private String vehicleImageUrl;
    private String vehicleId;
    private double pricePerDay;
    private double pricePerHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Get vehicle data from intent
        Intent intent = getIntent();
        if (intent != null) {
            // Get vehicle ID and prices
            vehicleId = intent.getStringExtra("vehicle_id");
            pricePerDay = intent.getDoubleExtra("price_per_day", 0);
            pricePerHour = intent.getDoubleExtra("price_per_hour", 0);
            
            vehicleId = intent.getStringExtra("vehicle_id");
            vehicleImageUrl = intent.getStringExtra("vehicle_image_url");

            // Use constructor with ID and prices
            vehicle = new Vehicle(
                vehicleId,                                          // id
                intent.getStringExtra("vehicle_name"),              // name
                intent.getStringExtra("vehicle_details"),           // details
                intent.getStringExtra("vehicle_battery"),           // batteryPercent
                intent.getStringExtra("vehicle_range"),             // range
                intent.getStringExtra("vehicle_seats"),             // seats
                intent.getStringExtra("vehicle_location"),          // location
                intent.getStringExtra("vehicle_price"),             // price
                intent.getStringExtra("vehicle_price_details"),     // priceDetails
                intent.getStringExtra("vehicle_status"),            // status
                intent.getStringExtra("vehicle_rating"),            // rating
                intent.getStringExtra("vehicle_condition"),         // condition
                vehicleImageUrl,                                    // imageUrl
                null,                                               // brand
                pricePerDay,                                        // pricePerDay
                pricePerHour                                        // pricePerHour
            );

            setupViews();
        }
    }

    private void setupViews() {
        // Find views
        ImageView ivVehicleImage = findViewById(R.id.iv_vehicle_detail_image);
        TextView tvVehicleName = findViewById(R.id.tv_vehicle_detail_name);
        TextView tvVehicleDetails = findViewById(R.id.tv_vehicle_detail_info);
        TextView tvBatteryPercent = findViewById(R.id.tv_vehicle_detail_battery);
        TextView tvRange = findViewById(R.id.tv_vehicle_detail_range);
        TextView tvSeats = findViewById(R.id.tv_vehicle_detail_seats);
        TextView tvLocation = findViewById(R.id.tv_vehicle_detail_location);
        TextView tvRating = findViewById(R.id.tv_vehicle_detail_rating);
        TextView tvPrice = findViewById(R.id.tv_vehicle_detail_price);
        TextView tvPriceDetails = findViewById(R.id.tv_vehicle_detail_price_info);
        Button btnBookNow = findViewById(R.id.btn_detail_book_now);
        TextView btnBack = findViewById(R.id.btn_detail_back);

        // Set data
        // Load image from URL using Glide
        if (vehicleImageUrl != null && !vehicleImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(vehicleImageUrl)
                    .placeholder(R.drawable.xe1)
                    .error(R.drawable.xe1)
                    .centerCrop()
                    .into(ivVehicleImage);
        } else {
            ivVehicleImage.setImageResource(R.drawable.xe1);
        }
        
        tvVehicleName.setText(vehicle.getName());
        
        // Display vehicle details (year • color format)
        tvVehicleDetails.setText(vehicle.getDetails());
        
        // Display battery and range
        tvBatteryPercent.setText(vehicle.getBatteryPercent());
        tvRange.setText(vehicle.getRange());
        
        // Display seats and location
        tvSeats.setText(vehicle.getSeats());
        tvLocation.setText(vehicle.getLocation());
        
        // Display rating
        tvRating.setText(vehicle.getRating());
        
        // Format and display price
        tvPrice.setText(formatPrice(vehicle.getPrice()));
        tvPriceDetails.setText(vehicle.getPriceDetails());

        // Set click listeners
        btnBookNow.setOnClickListener(v -> {
            // Navigate to PaymentActivity
            Intent paymentIntent = new Intent(DetailActivity.this, PaymentActivity.class);
            
            // Pass vehicle ID
            paymentIntent.putExtra("vehicle_id", vehicleId);
            
            // Pass vehicle name
            paymentIntent.putExtra("vehicle_name", vehicle.getName());
            
            // Pass station name (location)
            paymentIntent.putExtra("station_name", vehicle.getLocation());
            
            // Pass prices (số thực)
            paymentIntent.putExtra("price_per_day", pricePerDay);
            paymentIntent.putExtra("price_per_hour", pricePerHour);
            
            startActivity(paymentIntent);
        });

        btnBack.setOnClickListener(v -> {
            finish(); // Close this activity and return to previous screen
        });
    }
    
    /**
     * Format price to Vietnamese format
     */
    private String formatPrice(String price) {
        if (price == null || price.isEmpty()) {
            return "0 ₫";
        }
        
        // If already formatted, return as is
        if (price.contains("₫") || price.contains("đ")) {
            return price;
        }
        
        // Try to parse and format
        try {
            // Remove currency symbols and extract number
            String numStr = price.replaceAll("[^0-9.]", "").trim();
            if (!numStr.isEmpty()) {
                double amount = Double.parseDouble(numStr);
                NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
                return formatter.format(amount) + " ₫";
            }
        } catch (Exception e) {
            // If parsing fails, return original
        }
        
        return price;
    }
}