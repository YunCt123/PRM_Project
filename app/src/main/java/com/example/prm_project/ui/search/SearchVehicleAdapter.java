package com.example.prm_project.ui.search;

import android.content.Intent;
import android.util.Log;
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
import com.example.prm_project.ui.details.DetailActivity;
import com.example.prm_project.ui.home.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class SearchVehicleAdapter extends RecyclerView.Adapter<SearchVehicleAdapter.VehicleViewHolder> {

    private static final String TAG = "SearchVehicleAdapter";
    private final List<Vehicle> vehicleList;

    public SearchVehicleAdapter(List<Vehicle> vehicleList) {
        this.vehicleList = vehicleList != null ? vehicleList : new ArrayList<>();
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vehicle_compact, parent, false);
            return new VehicleViewHolder(view);
        } catch (Exception e) {
            Log.e(TAG, "Error creating view holder", e);
            // Fallback to empty view if inflation fails
            View emptyView = new View(parent.getContext());
            return new VehicleViewHolder(emptyView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        try {
            if (position < 0 || position >= vehicleList.size()) return;

            Vehicle v = vehicleList.get(position);
            if (v == null) return;

            if (holder.tvVehicleName != null) {
                holder.tvVehicleName.setText(v.getName());
            }
            if (holder.tvVehicleDetails != null) {
                holder.tvVehicleDetails.setText(v.getDetails());
            }
            if (holder.tvPrice != null) {
                holder.tvPrice.setText(v.getPrice());
            }

            int[] vehicleImages = {R.drawable.xe1, R.drawable.xe2, R.drawable.xe3};
            int imageIndex = Math.abs(position % vehicleImages.length);
            if (holder.ivVehicleImage != null) {
                holder.ivVehicleImage.setImageResource(vehicleImages[imageIndex]);
            }

            if (holder.btnView != null) {
                holder.btnView.setOnClickListener(view -> {
                    try {
                        Intent intent = new Intent(view.getContext(), DetailActivity.class);
                        intent.putExtra("vehicle_name", v.getName());
                        intent.putExtra("vehicle_details", v.getDetails());
                        intent.putExtra("vehicle_battery", v.getBatteryPercent());
                        intent.putExtra("vehicle_range", v.getRange());
                        intent.putExtra("vehicle_seats", v.getSeats());
                        intent.putExtra("vehicle_location", v.getLocation());
                        intent.putExtra("vehicle_price", v.getPrice());
                        intent.putExtra("vehicle_price_details", v.getPriceDetails());
                        intent.putExtra("vehicle_status", v.getStatus());
                        intent.putExtra("vehicle_rating", v.getRating());
                        intent.putExtra("vehicle_condition", v.getCondition());
                        intent.putExtra("vehicle_image", vehicleImages[imageIndex]);
                        view.getContext().startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error launching detail activity", e);
                        Toast.makeText(view.getContext(), "Could not open vehicle details", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error binding view holder at position " + position, e);
        }
    }

    @Override
    public int getItemCount() {
        return vehicleList != null ? vehicleList.size() : 0;
    }

    public static class VehicleViewHolder extends RecyclerView.ViewHolder {
        ImageView ivVehicleImage;
        TextView tvVehicleName;
        TextView tvVehicleDetails;
        TextView tvPrice;
        Button btnView;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                ivVehicleImage = itemView.findViewById(R.id.iv_vehicle_image);
                tvVehicleName = itemView.findViewById(R.id.tv_vehicle_name);
                tvVehicleDetails = itemView.findViewById(R.id.tv_vehicle_details);
                tvPrice = itemView.findViewById(R.id.tv_price);
                btnView = itemView.findViewById(R.id.btn_view);
            } catch (Exception e) {
                Log.e("VehicleViewHolder", "Error finding views", e);
            }
        }
    }
}
