package com.example.prm_project.ui.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm_project.R;
import com.example.prm_project.activies.VerifyAccountActivity;
import com.example.prm_project.utils.SessionManager;

import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private List<Vehicle> vehicleList;
    private Context context;
    private SessionManager sessionManager;

    public VehicleAdapter(List<Vehicle> vehicleList, Context context) {
        this.vehicleList = vehicleList;
        this.context = context;
        this.sessionManager = new SessionManager(context);
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vehicle, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicle vehicle = vehicleList.get(position);

        holder.tvVehicleName.setText(vehicle.getName());
        holder.tvVehicleDetails.setText(vehicle.getDetails());
        holder.tvBatteryPercent.setText(vehicle.getBatteryPercent());
        holder.tvRating.setText(vehicle.getRating());
        holder.tvPrice.setText(vehicle.getPrice());
        holder.tvPriceDetails.setText(vehicle.getPriceDetails());
        holder.tvStatus.setText(vehicle.getStatus());
        holder.tvLocation.setText("📍 " + vehicle.getLocation());
        holder.tvSeats.setText("👥 " + vehicle.getSeats());

        // Load image from URL using Glide
        if (vehicle.getImageUrl() != null && !vehicle.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(vehicle.getImageUrl())
                    .placeholder(R.drawable.xe1)  // Placeholder khi đang load
                    .error(R.drawable.xe1)        // Ảnh mặc định nếu load lỗi
                    .centerCrop()
                    .into(holder.ivVehicleImage);
        } else {
            // Fallback nếu không có URL
            int[] vehicleImages = {R.drawable.xe1, R.drawable.xe2, R.drawable.xe3};
            int imageIndex = position % vehicleImages.length;
            holder.ivVehicleImage.setImageResource(vehicleImages[imageIndex]);
        }

        // Set click listeners for buttons
        holder.btnViewDetails.setOnClickListener(v -> {
            // Navigate to DetailActivity with vehicle data
            Intent intent = new Intent(context, com.example.prm_project.ui.details.DetailActivity.class);
            
            // Pass vehicle data via intent
            intent.putExtra("vehicle_id", vehicle.getId());  // Add vehicle ID
            intent.putExtra("vehicle_name", vehicle.getName());
            intent.putExtra("vehicle_details", vehicle.getDetails());
            intent.putExtra("vehicle_battery", vehicle.getBatteryPercent());
            intent.putExtra("vehicle_range", vehicle.getRange());
            intent.putExtra("vehicle_seats", vehicle.getSeats());
            intent.putExtra("vehicle_location", vehicle.getLocation());
            intent.putExtra("vehicle_price", vehicle.getPrice());
            intent.putExtra("vehicle_price_details", vehicle.getPriceDetails());
            intent.putExtra("vehicle_status", vehicle.getStatus());
            intent.putExtra("vehicle_rating", vehicle.getRating());
            intent.putExtra("vehicle_condition", vehicle.getCondition());
            intent.putExtra("vehicle_image_url", vehicle.getImageUrl());  // Pass URL thay vì resource ID
            intent.putExtra("price_per_day", vehicle.getPricePerDay());  // Add price per day (số)
            intent.putExtra("price_per_hour", vehicle.getPricePerHour());  // Add price per hour (số)
            
            context.startActivity(intent);
        });

        holder.btnBookNow.setOnClickListener(v -> {
            // Check if user is logged in first
            if (!sessionManager.isLoggedIn()) {
                showLoginRequiredDialog();
                return;
            }
            
            // Check if user is verified before allowing booking
            if (!sessionManager.isVerified()) {
                showVerificationRequiredDialog();
                return;
            }
            
            // User is logged in and verified, proceed to payment
            Intent intent = new Intent(context, com.example.prm_project.activies.PaymentActivity.class);

            // Pass vehicle data
            intent.putExtra("vehicle_id", vehicle.getId());
            intent.putExtra("vehicle_name", vehicle.getName());
            intent.putExtra("station_name", vehicle.getLocation());  // Station name
            intent.putExtra("price_per_day", vehicle.getPricePerDay());
            intent.putExtra("price_per_hour", vehicle.getPricePerHour());
            
            context.startActivity(intent);
        });

        // Set click listener for entire card
        holder.itemView.setOnClickListener(v -> {
            // Same navigation as VIEW button
            Intent intent = new Intent(context, com.example.prm_project.ui.details.DetailActivity.class);
            
            intent.putExtra("vehicle_id", vehicle.getId());  // Add vehicle ID
            intent.putExtra("vehicle_name", vehicle.getName());
            intent.putExtra("vehicle_details", vehicle.getDetails());
            intent.putExtra("vehicle_battery", vehicle.getBatteryPercent());
            intent.putExtra("vehicle_range", vehicle.getRange());
            intent.putExtra("vehicle_seats", vehicle.getSeats());
            intent.putExtra("vehicle_location", vehicle.getLocation());
            intent.putExtra("vehicle_price", vehicle.getPrice());
            intent.putExtra("vehicle_price_details", vehicle.getPriceDetails());
            intent.putExtra("vehicle_status", vehicle.getStatus());
            intent.putExtra("vehicle_rating", vehicle.getRating());
            intent.putExtra("vehicle_condition", vehicle.getCondition());
            intent.putExtra("vehicle_image_url", vehicle.getImageUrl());  // Pass URL thay vì resource ID
            intent.putExtra("price_per_day", vehicle.getPricePerDay());  // Add price per day (số)
            intent.putExtra("price_per_hour", vehicle.getPricePerHour());  // Add price per hour (số)
            
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    /**
     * Show dialog when user is not verified
     */
    private void showVerificationRequiredDialog() {
        new AlertDialog.Builder(context)
                .setTitle("Yêu cầu xác minh tài khoản")
                .setMessage("Bạn cần xác minh tài khoản trước khi có thể đặt xe. Vui lòng hoàn tất việc xác minh để tiếp tục.")
                .setPositiveButton("Xác minh ngay", (dialog, which) -> {
                    // Navigate to VerifyAccountActivity
                    Intent verifyIntent = new Intent(context, VerifyAccountActivity.class);
                    context.startActivity(verifyIntent);
                })
                .setNegativeButton("Để sau", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(true)
                .show();
    }

    /**
     * Show dialog when user is not logged in
     */
    private void showLoginRequiredDialog() {
        new AlertDialog.Builder(context)
                .setTitle("Yêu cầu đăng nhập")
                .setMessage("Bạn cần đăng nhập để đặt xe. Vui lòng đăng nhập để tiếp tục.")
                .setPositiveButton("Đăng nhập", (dialog, which) -> {
                    // Navigate to LoginActivity
                    Intent loginIntent = new Intent(context, com.example.prm_project.activies.LoginActivity.class);
                    context.startActivity(loginIntent);
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(true)
                .show();
    }

    public static class VehicleViewHolder extends RecyclerView.ViewHolder {
        ImageView ivVehicleImage;
        TextView tvVehicleName;
        TextView tvVehicleDetails;
        TextView tvBatteryPercent;
        TextView tvRating;
        TextView tvPrice;
        TextView tvPriceDetails;
        TextView tvStatus;
        TextView tvLocation;
        TextView tvSeats;
        Button btnViewDetails;
        Button btnBookNow;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);

            ivVehicleImage = itemView.findViewById(R.id.iv_vehicle_image);
            tvVehicleName = itemView.findViewById(R.id.tv_vehicle_name);
            tvVehicleDetails = itemView.findViewById(R.id.tv_vehicle_details);
            tvBatteryPercent = itemView.findViewById(R.id.tv_battery_percent);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvPriceDetails = itemView.findViewById(R.id.tv_price_details);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvSeats = itemView.findViewById(R.id.tv_seats);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
            btnBookNow = itemView.findViewById(R.id.btn_book_now);
        }
    }
}
