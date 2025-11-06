package com.example.prm_project.ui.booking;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.prm_project.databinding.FragmentBookingListBinding;
import com.example.prm_project.repository.BookingRespository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingListFragment extends Fragment {

    private static final String ARG_STATUS = "status";
    private String bookingStatus;
    private FragmentBookingListBinding binding;
    private BookingAdapter bookingAdapter;
    private List<Booking> bookingList;
    private BookingRespository bookingRepository;

    public static BookingListFragment newInstance(String status) {
        BookingListFragment fragment = new BookingListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookingStatus = getArguments().getString(ARG_STATUS);
        }
        bookingRepository = new BookingRespository(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBookingListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupRecyclerView();
        loadBookings();
    }

    private void setupRecyclerView() {
        bookingList = new ArrayList<>();
        bookingAdapter = new BookingAdapter(bookingList, getContext());
        
        binding.recyclerViewBookings.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewBookings.setAdapter(bookingAdapter);
    }

    private void loadBookings() {
        bookingList.clear();

        // Map status to API parameter
        String apiStatus = "";
        if ("PENDING".equals(bookingStatus)) {
            apiStatus = "pending";
        } else if ("ACTIVE".equals(bookingStatus)) {
            apiStatus = "active";
        } else if ("COMPLETED".equals(bookingStatus)) {
            apiStatus = "completed";
        } else if ("CANCELLED".equals(bookingStatus)) {
            apiStatus = "cancelled";
        } else if ("EXPIRED".equals(bookingStatus)) {
            apiStatus = "expired";
        }

        // Load bookings from API
        bookingRepository.getMyBookings(1, 20, apiStatus).observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.isSuccess() && response.getItems() != null) {
                for (com.example.prm_project.models.Booking.BookingItem item : response.getItems()) {
                    com.example.prm_project.ui.booking.Booking uiBooking = convertApiBookingToUiBooking(item);
                    if (uiBooking != null) {
                        bookingList.add(uiBooking);
                    }
                }
            }

            // Show/hide empty state
            if (bookingList.isEmpty()) {
                binding.recyclerViewBookings.setVisibility(View.GONE);
                binding.emptyStateLayout.setVisibility(View.VISIBLE);
            } else {
                binding.recyclerViewBookings.setVisibility(View.VISIBLE);
                binding.emptyStateLayout.setVisibility(View.GONE);
                bookingAdapter.notifyDataSetChanged();
            }
        });
    }

    private com.example.prm_project.ui.booking.Booking convertApiBookingToUiBooking(com.example.prm_project.models.Booking.BookingItem apiBooking) {
        try {
            // Parse startTime and endTime
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

            Date startDate = isoFormat.parse(apiBooking.getStartTime());
            Date endDate = isoFormat.parse(apiBooking.getEndTime());

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            String pickupDate = dateFormat.format(startDate);
            String pickupTime = timeFormat.format(startDate);
            String returnDate = dateFormat.format(endDate);
            String returnTime = timeFormat.format(endDate);

            // Vehicle info (hardcoded as requested)
            String vehicleId = apiBooking.getVehicle() != null ? apiBooking.getVehicle().getId() : "N/A";
            String vehicleName = apiBooking.getVehicle() != null ?
                apiBooking.getVehicle().getBrand() + " " + apiBooking.getVehicle().getModel() : "Unknown Vehicle";
            String vehicleImageUrl = "https://example.com/vehicle.jpg"; // Hardcoded as requested

            // Total price from deposit amount
            String totalPrice = apiBooking.getDeposit() != null ?
                String.valueOf(apiBooking.getDeposit().getAmount()) + " VND" : "0 VND";

            // Status text
            String statusText = getStatusText(apiBooking.getStatus());
            String status = apiBooking.getStatus().toUpperCase();

            // Get amounts data
            long rentalEstimated = 0;
            long serviceFee = 0;
            long insuranceFee = 0;
            if (apiBooking.getAmounts() != null) {
                rentalEstimated = apiBooking.getAmounts().getRentalEstimated();
                // Service fee and insurance can be hardcoded as mentioned
                serviceFee = 20000; // 20,000 VND
                insuranceFee = 20000; // 20,000 VND
            }

            // Calculate rental days (simplified calculation)
            String rentalDays = "4"; // Default, could be calculated from dates

            // Get checkout URL from deposit
            String checkoutUrl = "";
            if (apiBooking.getDeposit() != null) {
                checkoutUrl = apiBooking.getDeposit().getCheckoutUrl();
            }

            return new com.example.prm_project.ui.booking.Booking(
                apiBooking.getId(),
                vehicleName,
                vehicleImageUrl,
                pickupDate,
                pickupTime,
                returnDate,
                returnTime,
                totalPrice,
                statusText,
                status,
                rentalEstimated,
                serviceFee,
                insuranceFee,
                rentalDays,
                checkoutUrl
            );
        } catch (Exception e) {
            Log.e("BookingListFragment", "Error parsing booking dates", e);
            return null;
        }
    }

    private String getStatusText(String status) {
        switch (status.toLowerCase()) {
            case "pending":
                return "Chờ xác nhận";
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
