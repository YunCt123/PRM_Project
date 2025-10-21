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
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_project.R;

import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private List<Vehicle> vehicleList;
    private Context context;

    public VehicleAdapter(List<Vehicle> vehicleList, Context context) {
        this.vehicleList = vehicleList;
        this.context = context;
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

        // Set vehicle image based on position (DEFINE FIRST)
        int[] vehicleImages = {R.drawable.xe1, R.drawable.xe2, R.drawable.xe3};
        int imageIndex = position % vehicleImages.length;
        holder.ivVehicleImage.setImageResource(vehicleImages[imageIndex]);

        // Set click listeners for buttons
        holder.btnViewDetails.setOnClickListener(v -> {
            // Navigate to DetailActivity with vehicle data
            Intent intent = new Intent(context, com.example.prm_project.ui.details.DetailActivity.class);
            
            // Pass vehicle data via intent
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
            intent.putExtra("vehicle_image", vehicleImages[imageIndex]);
            
            context.startActivity(intent);
        });

        holder.btnBookNow.setOnClickListener(v -> {
            Toast.makeText(context, "Booking " + vehicle.getName() + "...", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to booking scr1een
        });

        // Set click listener for entire card
        holder.itemView.setOnClickListener(v -> {
            // Same navigation as VIEW button
            Intent intent = new Intent(context, com.example.prm_project.ui.details.DetailActivity.class);
            
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
            intent.putExtra("vehicle_image", vehicleImages[imageIndex]);
            
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
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
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
            btnBookNow = itemView.findViewById(R.id.btn_book_now);
        }
    }
}
