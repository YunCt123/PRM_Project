package com.example.prm_project.ui.booking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.prm_project.databinding.FragmentBookingListBinding;

import java.util.ArrayList;
import java.util.List;

public class BookingListFragment extends Fragment {

    private static final String ARG_STATUS = "status";
    private String bookingStatus;
    private FragmentBookingListBinding binding;
    private BookingAdapter bookingAdapter;
    private List<Booking> bookingList;

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
        // TODO: Load bookings from database/API based on bookingStatus
        // For now, load dummy data for demonstration
        bookingList.clear();
        
        if ("ACTIVE".equals(bookingStatus)) {
            // Add some dummy active bookings
            bookingList.add(new Booking(
                "12345",
                "Tesla Model 3",
                "https://example.com/tesla.jpg",
                "01/11/2025",
                "10:00 AM",
                "05/11/2025",
                "10:00 AM",
                "$360",
                "Đang hoạt động",
                "ACTIVE"
            ));
            
            bookingList.add(new Booking(
                "12346",
                "BMW X5",
                "https://example.com/bmw.jpg",
                "02/11/2025",
                "09:00 AM",
                "06/11/2025",
                "09:00 AM",
                "$450",
                "Đang hoạt động",
                "ACTIVE"
            ));
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
