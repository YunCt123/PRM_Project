package com.example.prm_project.ui.bookings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.prm_project.R;
import com.example.prm_project.adapters.BookingHistoryAdapter;
import com.example.prm_project.api.ApiClient;
import com.example.prm_project.api.BookingApiService;
import com.example.prm_project.databinding.FragmentBookingHistoryBinding;
import com.example.prm_project.models.ApiResponse;
import com.example.prm_project.models.Booking;
import com.example.prm_project.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingHistoryFragment extends Fragment {

    private static final String TAG = "BookingHistoryFragment";
    private FragmentBookingHistoryBinding binding;
    private BookingHistoryAdapter adapter;
    private SessionManager sessionManager;
    private BookingApiService bookingApiService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBookingHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize
        sessionManager = new SessionManager(requireContext());
        bookingApiService = ApiClient.getClient().create(BookingApiService.class);

        // Setup RecyclerView
        setupRecyclerView();

        // Back button
        binding.btnBack.setOnClickListener(v ->
                NavHostFragment.findNavController(BookingHistoryFragment.this).popBackStack()
        );

        // Load bookings
        loadBookings();

        return root;
    }

    private void setupRecyclerView() {
        adapter = new BookingHistoryAdapter(booking -> {
            // Handle booking item click
            Toast.makeText(getContext(), "Chi tiết: " + booking.getVehicle().getName(), Toast.LENGTH_SHORT).show();
            // TODO: Navigate to booking detail screen
        });

        binding.rvBookings.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvBookings.setAdapter(adapter);
    }

    private void loadBookings() {
        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            Log.e(TAG, "No token found");
            Toast.makeText(getContext(), "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.rvBookings.setVisibility(View.GONE);
        binding.emptyState.setVisibility(View.GONE);

        String bearerToken = token.startsWith("Bearer ") ? token : "Bearer " + token;

        Log.d(TAG, "Loading bookings...");

        Call<ApiResponse<List<Booking>>> call = bookingApiService.getMyBookings(bearerToken);
        call.enqueue(new Callback<ApiResponse<List<Booking>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Booking>>> call, Response<ApiResponse<List<Booking>>> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Booking>> apiResponse = response.body();

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        List<Booking> bookings = apiResponse.getData();
                        Log.d(TAG, "Loaded " + bookings.size() + " bookings");

                        if (bookings.isEmpty()) {
                            // Show empty state
                            binding.emptyState.setVisibility(View.VISIBLE);
                            binding.rvBookings.setVisibility(View.GONE);
                        } else {
                            // Show bookings
                            adapter.setBookings(bookings);
                            binding.rvBookings.setVisibility(View.VISIBLE);
                            binding.emptyState.setVisibility(View.GONE);
                        }
                    } else {
                        Log.e(TAG, "API returned error: " + apiResponse.getMessage());
                        showError("Không thể tải lịch sử thuê xe");
                    }
                } else {
                    Log.e(TAG, "API call failed: " + response.code());
                    showError("Lỗi server: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Booking>>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Network error", t);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        binding.emptyState.setVisibility(View.VISIBLE);
        binding.rvBookings.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

