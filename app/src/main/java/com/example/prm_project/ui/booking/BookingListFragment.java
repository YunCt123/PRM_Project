package com.example.prm_project.ui.booking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.prm_project.databinding.FragmentBookingListBinding;
import com.example.prm_project.repository.BookingRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingListFragment extends Fragment {

    private static final String ARG_STATUS = "status";
    private String bookingStatus;
    private FragmentBookingListBinding binding;
    private BookingApiAdapter bookingAdapter;
    private List<com.example.prm_project.models.Booking.BookingItem> bookingList;
    private BookingRepository bookingRepository;

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
        bookingRepository = new BookingRepository(requireContext());
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
        bookingAdapter = new BookingApiAdapter(bookingList, getContext());
        
        // Set action listener for cancel booking
        bookingAdapter.setActionListener(bookingId -> {
            cancelBooking(bookingId);
        });
        
        binding.recyclerViewBookings.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewBookings.setAdapter(bookingAdapter);
    }

    private void loadBookings() {
        bookingList.clear();

        // For "Đang hoạt động" tab, we need to show both reserved and active
        // Since API doesn't support multiple statuses, we'll load all and filter
        if (bookingStatus != null && bookingStatus.contains(",")) {
            // Load all bookings without status filter, then filter on client
            loadAllAndFilter();
        } else {
            // Single status - use API filter
            loadBookingsByStatus(bookingStatus);
        }
    }

    private void loadAllAndFilter() {
        // Load without status filter
        bookingRepository.getMyBookings(1, 100, "").observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.isSuccess() && response.getItems() != null) {
                // Get allowed statuses
                String[] allowedStatuses = bookingStatus.split(",");
                
                for (com.example.prm_project.models.Booking.BookingItem item : response.getItems()) {
                    // Check if item status matches any allowed status
                    boolean isAllowed = false;
                    for (String status : allowedStatuses) {
                        if (status.trim().equalsIgnoreCase(item.getStatus())) {
                            isAllowed = true;
                            break;
                        }
                    }
                    
                    if (isAllowed) {
                        bookingList.add(item);
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

    private void loadBookingsByStatus(String status) {
        // Map status to API parameter
        String apiStatus = "";
        if ("PENDING".equals(status)) {
            apiStatus = "pending";
        } else if ("RESERVED".equals(status)) {
            apiStatus = "reserved";
        } else if ("ACTIVE".equals(status)) {
            apiStatus = "active";
        } else if ("COMPLETED".equals(status)) {
            apiStatus = "completed";
        } else if ("CANCELLED".equals(status)) {
            apiStatus = "cancelled";
        } else if ("EXPIRED".equals(status)) {
            apiStatus = "expired";
        }

        // Load bookings from API
        bookingRepository.getMyBookings(1, 20, apiStatus).observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.isSuccess() && response.getItems() != null) {
                bookingList.addAll(response.getItems());
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

    /**
     * Cancel a booking
     */
    private void cancelBooking(String bookingId) {
        bookingRepository.cancelBooking(bookingId).observe(getViewLifecycleOwner(), cancelledBooking -> {
            if (cancelledBooking != null) {
                // Success - reload bookings
                loadBookings();
            }
            // Error toast is already shown in repository
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
