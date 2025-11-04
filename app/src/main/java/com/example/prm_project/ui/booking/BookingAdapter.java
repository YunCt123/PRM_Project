package com.example.prm_project.ui.booking;

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

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookingList;
    private Context context;

    public BookingAdapter(List<Booking> bookingList, Context context) {
        this.bookingList = bookingList;
        this.context = context;
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
        Booking booking = bookingList.get(position);
        
        holder.tvVehicleName.setText(booking.getVehicleName());
        holder.tvBookingId.setText("ID: #" + booking.getBookingId());
        holder.tvBookingStatus.setText(booking.getStatusText());
        holder.tvPickupDate.setText(booking.getPickupDate());
        holder.tvPickupTime.setText(booking.getPickupTime());
        holder.tvReturnDate.setText(booking.getReturnDate());
        holder.tvReturnTime.setText(booking.getReturnTime());
        holder.tvTotalPrice.setText(booking.getTotalPrice());

        // Set status color based on booking status
        if ("ACTIVE".equals(booking.getStatus())) {
            holder.tvBookingStatus.setTextColor(context.getResources().getColor(R.color.primary_blue));
        } else if ("COMPLETED".equals(booking.getStatus())) {
            holder.tvBookingStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else if ("CANCELLED".equals(booking.getStatus())) {
            holder.tvBookingStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        // Show/hide cancel button based on status
        if ("ACTIVE".equals(booking.getStatus())) {
            holder.btnCancelBooking.setVisibility(View.VISIBLE);
        } else {
            holder.btnCancelBooking.setVisibility(View.GONE);
        }

        // Set click listeners
        holder.btnViewDetails.setOnClickListener(v -> {
            // Navigate to booking details
            Intent intent = new Intent(context, BookingDetailsActivity.class);
            intent.putExtra("BOOKING_ID", booking.getBookingId());
            intent.putExtra("VEHICLE_NAME", booking.getVehicleName());
            intent.putExtra("PICKUP_DATE", booking.getPickupDate());
            intent.putExtra("PICKUP_TIME", booking.getPickupTime());
            intent.putExtra("RETURN_DATE", booking.getReturnDate());
            intent.putExtra("RETURN_TIME", booking.getReturnTime());
            intent.putExtra("TOTAL_PRICE", booking.getTotalPrice());
            intent.putExtra("STATUS", booking.getStatus());
            intent.putExtra("STATUS_TEXT", booking.getStatusText());
            context.startActivity(intent);
        });

        holder.btnCancelBooking.setOnClickListener(v -> {
            Toast.makeText(context, "Hủy đơn đặt xe #" + booking.getBookingId(), Toast.LENGTH_SHORT).show();
            // TODO: Show confirmation dialog and cancel booking
        });

        // Load vehicle image (using placeholder for now)
        // TODO: Use Glide or Picasso to load image from URL
        holder.ivVehicleImage.setImageResource(R.drawable.ic_car);
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
            btnCancelBooking = itemView.findViewById(R.id.btnCancelBooking);
        }
    }
}
