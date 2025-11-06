package com.example.prm_project.ui.booking;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm_project.R;
import com.example.prm_project.models.Booking;
import com.example.prm_project.repository.VehicleRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for booking list using API Booking model directly
 */
public class BookingApiAdapter extends RecyclerView.Adapter<BookingApiAdapter.BookingViewHolder> {

    private List<Booking.BookingItem> bookingList;
    private Context context;
    private VehicleRepository vehicleRepository;
    private OnBookingActionListener actionListener;

    public interface OnBookingActionListener {
        void onCancelBooking(String bookingId);
    }

    public BookingApiAdapter(List<Booking.BookingItem> bookingList, Context context) {
        this.bookingList = bookingList;
        this.context = context;
        this.vehicleRepository = new VehicleRepository();
    }

    public void setActionListener(OnBookingActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking.BookingItem booking = bookingList.get(position);
        
        // Vehicle name
        String vehicleName = "Unknown Vehicle";
        if (booking.getVehicle() != null) {
            vehicleName = booking.getVehicle().getBrand() + " " + booking.getVehicle().getModel();
        }
        holder.tvVehicleName.setText(vehicleName);
        
        // Booking ID
        holder.tvBookingId.setText("ID: #" + booking.getId());
        
        // Status
        String statusText = getStatusText(booking.getStatus());
        holder.tvBookingStatus.setText(statusText);
        
        // Set status color
        setStatusColor(holder.tvBookingStatus, booking.getStatus());
        
        // Parse and format dates/times
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

            Date startDate = isoFormat.parse(booking.getStartTime());
            Date endDate = isoFormat.parse(booking.getEndTime());

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            holder.tvPickupDate.setText(dateFormat.format(startDate));
            holder.tvPickupTime.setText(timeFormat.format(startDate));
            holder.tvReturnDate.setText(dateFormat.format(endDate));
            holder.tvReturnTime.setText(timeFormat.format(endDate));
        } catch (Exception e) {
            holder.tvPickupDate.setText("--/--/----");
            holder.tvPickupTime.setText("--:--");
            holder.tvReturnDate.setText("--/--/----");
            holder.tvReturnTime.setText("--:--");
        }
        
        // Total price
        if (booking.getDeposit() != null) {
            holder.tvTotalPrice.setText(booking.getDeposit().getAmount() + " VND");
        } else {
            holder.tvTotalPrice.setText("0 VND");
        }

        // Button visibility based on status
        String status = booking.getStatus().toLowerCase();
        
        // Cancel button only for active bookings
        if ("active".equals(status) || "reserved".equals(status)) {
            holder.btnCancelBooking.setVisibility(View.VISIBLE);
        } else {
            holder.btnCancelBooking.setVisibility(View.GONE);
        }

        // Payment button ONLY for pending status with checkout URL
        if ("pending".equals(status) && 
            booking.getDeposit() != null && 
            booking.getDeposit().getCheckoutUrl() != null && 
            !booking.getDeposit().getCheckoutUrl().isEmpty()) {
            holder.btnPayment.setVisibility(View.VISIBLE);
        } else {
            holder.btnPayment.setVisibility(View.GONE);
        }

        // Click listeners
        holder.btnViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookingDetailsActivity.class);
            intent.putExtra("BOOKING_ID", booking.getId());
            context.startActivity(intent);
        });

        holder.btnPayment.setOnClickListener(v -> {
            if (booking.getDeposit() != null && booking.getDeposit().getCheckoutUrl() != null) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(booking.getDeposit().getCheckoutUrl()));
                context.startActivity(browserIntent);
            } else {
                Toast.makeText(context, "Không tìm thấy liên kết thanh toán", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnCancelBooking.setOnClickListener(v -> {
            if (actionListener != null) {
                // Show confirmation dialog
                new androidx.appcompat.app.AlertDialog.Builder(context)
                        .setTitle("Hủy đơn đặt xe")
                        .setMessage("Bạn có chắc chắn muốn hủy đơn đặt xe này?")
                        .setPositiveButton("Hủy đơn", (dialog, which) -> {
                            actionListener.onCancelBooking(booking.getId());
                        })
                        .setNegativeButton("Không", null)
                        .show();
            } else {
                Toast.makeText(context, "Hủy đơn đặt xe #" + booking.getId(), Toast.LENGTH_SHORT).show();
            }
        });

        // Load vehicle image
        // Note: API returns vehicle with defaultPhotos.exterior as array of IDs (strings)
        // We need to fetch vehicle details to get actual image URLs
        // For now, we'll try to get the URL if available, otherwise use placeholder
        loadVehicleImage(holder.ivVehicleImage, booking);
    }

    private void loadVehicleImage(ImageView imageView, Booking.BookingItem booking) {
        String imageUrl = null;
        
        // Try to get image URL from vehicle
        if (booking.getVehicle() != null) {
            imageUrl = booking.getVehicle().getMainImageUrl();
        }
        
        // If we have URL, load it; otherwise try to fetch vehicle details
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.xe1)
                    .error(R.drawable.xe1)
                    .centerCrop()
                    .into(imageView);
        } else {
            // Fetch vehicle details to get image URL
            String vehicleId = booking.getVehicle() != null ? booking.getVehicle().getId() : null;
            if (vehicleId != null) {
                vehicleRepository.getVehicleById(vehicleId, new VehicleRepository.SingleVehicleCallback() {
                    @Override
                    public void onSuccess(com.example.prm_project.models.Vehicle vehicle) {
                        String vehicleImageUrl = vehicle.getMainImageUrl();
                        if (vehicleImageUrl != null && !vehicleImageUrl.isEmpty()) {
                            Glide.with(context)
                                    .load(vehicleImageUrl)
                                    .placeholder(R.drawable.xe1)
                                    .error(R.drawable.xe1)
                                    .centerCrop()
                                    .into(imageView);
                        } else {
                            imageView.setImageResource(R.drawable.xe1);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        // Use placeholder on error
                        imageView.setImageResource(R.drawable.xe1);
                    }
                });
            } else {
                // No vehicle ID, use placeholder
                imageView.setImageResource(R.drawable.xe1);
            }
        }
    }

    private String getStatusText(String status) {
        switch (status.toLowerCase()) {
            case "pending":
                return "Chờ thanh toán";
            case "reserved":
                return "Đã đặt";
            case "active":
                return "Đang hoạt động";
            case "completed":
                return "Đã hoàn thành";
            case "cancelled":
                return "Đã hủy";
            case "expired":
                return "Đã hết hạn";
            default:
                return "Không xác định";
        }
    }

    private void setStatusColor(TextView textView, String status) {
        switch (status.toLowerCase()) {
            case "pending":
                textView.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                break;
            case "reserved":
            case "active":
                textView.setTextColor(context.getResources().getColor(R.color.primary_blue));
                break;
            case "completed":
                textView.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                break;
            case "cancelled":
            case "expired":
                textView.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        ImageView ivVehicleImage;
        TextView tvVehicleName;
        TextView tvBookingId;
        TextView tvBookingStatus;
        TextView tvPickupDate;
        TextView tvPickupTime;
        TextView tvReturnDate;
        TextView tvReturnTime;
        TextView tvTotalPrice;
        Button btnViewDetails;
        Button btnPayment;
        Button btnCancelBooking;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            ivVehicleImage = itemView.findViewById(R.id.ivVehicleImage);
            tvVehicleName = itemView.findViewById(R.id.tvVehicleName);
            tvBookingId = itemView.findViewById(R.id.tvBookingId);
            tvBookingStatus = itemView.findViewById(R.id.tvBookingStatus);
            tvPickupDate = itemView.findViewById(R.id.tvPickupDate);
            tvPickupTime = itemView.findViewById(R.id.tvPickupTime);
            tvReturnDate = itemView.findViewById(R.id.tvReturnDate);
            tvReturnTime = itemView.findViewById(R.id.tvReturnTime);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
            btnPayment = itemView.findViewById(R.id.btnPayment);
            btnCancelBooking = itemView.findViewById(R.id.btnCancelBooking);
        }
    }
}
