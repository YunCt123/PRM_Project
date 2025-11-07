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
        } else if ("EXPIRED".equals(booking.getStatus())) {
            holder.tvBookingStatus.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        }

        // Show/hide buttons based on status and availability
        if ("ACTIVE".equals(booking.getStatus())) {
            holder.btnCancelBooking.setVisibility(View.VISIBLE);
        } else {
            holder.btnCancelBooking.setVisibility(View.GONE);
        }

        // Show payment button ONLY for PENDING status
        if ("PENDING".equals(booking.getStatus()) && 
            booking.getCheckoutUrl() != null && 
            !booking.getCheckoutUrl().isEmpty()) {
            holder.btnPayment.setVisibility(View.VISIBLE);
        } else {
            holder.btnPayment.setVisibility(View.GONE);
        }

        // Set click listeners
        holder.btnViewDetails.setOnClickListener(v -> {
            // Navigate to booking details
            Intent intent = new Intent(context, BookingDetailsActivity.class);
            intent.putExtra("BOOKING_ID", booking.getBookingId());
            context.startActivity(intent);
        });

        holder.btnPayment.setOnClickListener(v -> {
            if (booking.getCheckoutUrl() != null && !booking.getCheckoutUrl().isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(booking.getCheckoutUrl()));
                context.startActivity(browserIntent);
            } else {
                Toast.makeText(context, "Không tìm thấy liên kết thanh toán", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnCancelBooking.setOnClickListener(v -> {
            Toast.makeText(context, "Hủy đơn đặt xe #" + booking.getBookingId(), Toast.LENGTH_SHORT).show();
            // TODO: Show confirmation dialog and cancel booking
        });

        // Load vehicle image using Glide
        if (booking.getVehicleImageUrl() != null && !booking.getVehicleImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(booking.getVehicleImageUrl())
                    .placeholder(R.drawable.xe1)
                    .error(R.drawable.xe1)
                    .centerCrop()
                    .into(holder.ivVehicleImage);
        } else {
            holder.ivVehicleImage.setImageResource(R.drawable.xe1);
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
