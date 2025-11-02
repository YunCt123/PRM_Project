package com.example.prm_project.ui.details;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_project.R;
import com.example.prm_project.activies.PaymentActivity;
import com.example.prm_project.ui.home.Vehicle;

public class DetailActivity extends AppCompatActivity {

    private Vehicle vehicle;
    private int vehicleImageRes;

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
            vehicle = new Vehicle(
                intent.getStringExtra("vehicle_name"),
                intent.getStringExtra("vehicle_details"),
                intent.getStringExtra("vehicle_battery"),
                intent.getStringExtra("vehicle_range"),
                intent.getStringExtra("vehicle_seats"),
                intent.getStringExtra("vehicle_location"),
                intent.getStringExtra("vehicle_price"),
                intent.getStringExtra("vehicle_price_details"),
                intent.getStringExtra("vehicle_status"),
                intent.getStringExtra("vehicle_rating"),
                intent.getStringExtra("vehicle_condition")
            );
            
            vehicleImageRes = intent.getIntExtra("vehicle_image", R.drawable.xe1);

            setupViews(vehicleImageRes);
        }
    }

    private void setupViews(int vehicleImageRes) {
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
        ivVehicleImage.setImageResource(vehicleImageRes);
        tvVehicleName.setText(vehicle.getName());
        tvVehicleDetails.setText(vehicle.getDetails());
        tvBatteryPercent.setText(vehicle.getBatteryPercent());
        tvRange.setText(vehicle.getRange());
        tvSeats.setText(vehicle.getSeats());
        tvLocation.setText(vehicle.getLocation());
        tvRating.setText(vehicle.getRating());
        tvPrice.setText(vehicle.getPrice());
        tvPriceDetails.setText(vehicle.getPriceDetails());

        // Set click listeners
        btnBookNow.setOnClickListener(v -> {
            // Navigate to PaymentActivity
            Intent paymentIntent = new Intent(DetailActivity.this, PaymentActivity.class);
            paymentIntent.putExtra("vehicle_name", vehicle.getName());
            
            // Extract price number from string like "$80" or "$80/day"
            String priceStr = vehicle.getPrice().replace("$", "").split("/")[0].trim();
            try {
                double price = Double.parseDouble(priceStr);
                paymentIntent.putExtra("daily_rate", price);
            } catch (NumberFormatException e) {
                paymentIntent.putExtra("daily_rate", 80.0); // Default value
            }
            
            paymentIntent.putExtra("rental_period", "1 ngày"); // Default 1 day
            startActivity(paymentIntent);
        });

        btnBack.setOnClickListener(v -> {
            finish(); // Close this activity and return to previous screen
        });
    }
}