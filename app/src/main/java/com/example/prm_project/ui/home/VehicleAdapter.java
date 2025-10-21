package com.example.prm_project.ui.home;

import android.content.Context;
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

        // Set click listeners for buttons
        holder.btnViewDetails.setOnClickListener(v -> {
            Toast.makeText(context, "View details for " + vehicle.getName(), Toast.LENGTH_SHORT).show();
            // Navigate to vehicle details screen
        });

        holder.btnBookNow.setOnClickListener(v -> {
            Toast.makeText(context, "Booking " + vehicle.getName(), Toast.LENGTH_SHORT).show();
            // Navigate to booking screen
        });

        // Set vehicle image based on position
        int[] vehicleImages = {R.drawable.xe1, R.drawable.xe2, R.drawable.xe3};
        int imageIndex = position % vehicleImages.length;
        holder.ivVehicleImage.setImageResource(vehicleImages[imageIndex]);
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
