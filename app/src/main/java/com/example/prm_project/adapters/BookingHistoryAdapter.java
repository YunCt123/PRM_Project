package com.example.prm_project.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm_project.R;
import com.example.prm_project.models.Booking;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying booking history in RecyclerView
 */
public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.BookingViewHolder> {

    private List<Booking.BookingItem> bookingList;
    private OnBookingClickListener listener;

    public interface OnBookingClickListener {
        void onBookingClick(Booking.BookingItem booking);
    }

    public BookingHistoryAdapter(OnBookingClickListener listener) {
        this.bookingList = new ArrayList<>();
        this.listener = listener;
    }

    public void setBookings(List<Booking.BookingItem> bookings) {
        this.bookingList = bookings != null ? bookings : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_history, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking.BookingItem booking = bookingList.get(position);
        holder.bind(booking);
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final ImageView ivVehicle;
        private final TextView tvVehicleName;
        private final TextView tvVehicleInfo;
        private final TextView tvDateRange;
        private final TextView tvTotalPrice;
        private final TextView tvStatus;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardBookingHistory);
            ivVehicle = itemView.findViewById(R.id.ivVehicleHistory);
            tvVehicleName = itemView.findViewById(R.id.tvVehicleNameHistory);
            tvVehicleInfo = itemView.findViewById(R.id.tvVehicleInfoHistory);
            tvDateRange = itemView.findViewById(R.id.tvDateRangeHistory);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPriceHistory);
            tvStatus = itemView.findViewById(R.id.tvStatusHistory);
        }

        public void bind(Booking.BookingItem booking) {
            if (booking.getVehicle() != null) {
                Booking.Vehicle vehicle = booking.getVehicle();

                // Vehicle name: brand + model
                String vehicleName = (vehicle.getBrand() != null ? vehicle.getBrand() : "") +
                        (vehicle.getModel() != null ? " " + vehicle.getModel() : "");
                tvVehicleName.setText(vehicleName.trim());

                // Info: plate number
                String info = "Plate: " + (vehicle.getPlateNumber() != null ? vehicle.getPlateNumber() : "-");
                tvVehicleInfo.setText(info);

                // Load vehicle image using getMainImageUrl()
                String imageUrl = vehicle.getMainImageUrl();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(itemView.getContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_car_placeholder)
                            .error(R.drawable.ic_car_placeholder)
                            .centerCrop()
                            .into(ivVehicle);
                } else {
                    ivVehicle.setImageResource(R.drawable.ic_car_placeholder);
                }
            } else {
                tvVehicleName.setText("Unknown vehicle");
                tvVehicleInfo.setText("-");
                ivVehicle.setImageResource(R.drawable.ic_car_placeholder);
            }

            // Format date range using startTime/endTime
            String dateRange = formatDate(booking.getStartTime()) + " - " + formatDate(booking.getEndTime());
            tvDateRange.setText(dateRange);

            // Format price using amounts.grandTotal if available
            if (booking.getAmounts() != null) {
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                tvTotalPrice.setText(formatter.format(booking.getAmounts().getGrandTotal()));
            } else {
                tvTotalPrice.setText("0 ₫");
            }

            // Set status text (use status field)
            String statusText = booking.getStatus() != null ? booking.getStatus() : "unknown";
            tvStatus.setText(statusText);

            // Set status color
            int statusColor = getStatusColor(booking.getStatus());
            tvStatus.setTextColor(itemView.getContext().getColor(statusColor));

            // Set click listener
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBookingClick(booking);
                }
            });
        }

        private String formatDate(String dateString) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = inputFormat.parse(dateString);
                return outputFormat.format(date);
            } catch (Exception e) {
                return dateString != null ? dateString : "";
            }
        }

        private int getStatusColor(String status) {
            if (status == null) return R.color.gray_500;
            switch (status.toLowerCase()) {
                case "pending":
                    return R.color.orange_500;
                case "approved":
                    return R.color.primary_blue;
                case "rejected":
                    return R.color.red_500;
                case "completed":
                    return R.color.green_500;
                case "cancelled":
                    return R.color.gray_500;
                default:
                    return R.color.gray_500;
            }
        }
    }
}
