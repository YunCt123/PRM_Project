package com.example.prm_project.ui.home;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_project.R;
import com.example.prm_project.activies.LoginActivity;
import com.example.prm_project.databinding.FragmentHomeBinding;
import com.example.prm_project.repository.VehicleRepository;
import com.example.prm_project.utils.SessionManager;
import com.example.prm_project.utils.VehicleConverter;

import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private Button loginButton;
    private FragmentHomeBinding binding;
    private VehicleAdapter vehicleAdapter;
    private List<Vehicle> vehicleList;
    private VehicleRepository vehicleRepository;
    private SessionManager sessionManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize session manager and repository
        sessionManager = new SessionManager(getContext());
        vehicleRepository = new VehicleRepository();

        setupRecyclerView();
        setupDatePickers();
        setupSearchButton();
        loadVehicleDataFromApi();
        setupLoginLogoutButton();

        return root;
    }

    private void setupRecyclerView() {
        vehicleList = new ArrayList<>();
        vehicleAdapter = new VehicleAdapter(vehicleList, getContext());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvVehicles.setLayoutManager(layoutManager);
        // Disable nested scrolling to allow parent ScrollView to handle all scrolling
        binding.rvVehicles.setNestedScrollingEnabled(false);
        binding.rvVehicles.setAdapter(vehicleAdapter);
    }

    private void setupDatePickers() {
        binding.etPickupDate.setOnClickListener(v -> showDatePicker(binding.etPickupDate));
        binding.etReturnDate.setOnClickListener(v -> showDatePicker(binding.etReturnDate));
    }

    private void setupSearchButton() {
        binding.btnSearch.setOnClickListener(v -> {
            String location = binding.etPickupLocation.getText().toString();
            String pickupDate = binding.etPickupDate.getText().toString();
            String returnDate = binding.etReturnDate.getText().toString();

            if (location.isEmpty() || pickupDate.isEmpty() || returnDate.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                searchVehicles(location, pickupDate, returnDate);
            }
        });
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            getContext(),
            (view, year1, month1, dayOfMonth) -> {
                String date = String.format("%02d/%02d/%04d", dayOfMonth, month1 + 1, year1);
                editText.setText(date);
            },
            year, month, day
        );

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    public void login() {
        // Login logic here
        // Initialize loginButton - find it from the root view since it's in an included layout
        loginButton = binding.getRoot().findViewById(R.id.btn_Login);
        if (loginButton != null) {
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle login button click
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void setupLoginLogoutButton() {
        loginButton = binding.getRoot().findViewById(R.id.btn_Login);
        if (loginButton != null) {
            updateButtonState();
            
            loginButton.setOnClickListener(v -> {
                if (sessionManager.isLoggedIn()) {
                    // Logout
                    sessionManager.logout();
                    Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                    updateButtonState();
                    
                    // Optionally, navigate to LoginActivity
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    // Login
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
    
    private void updateButtonState() {
        if (loginButton != null && sessionManager != null) {
            if (sessionManager.isLoggedIn()) {
                loginButton.setText("Logout");
            } else {
                loginButton.setText("Get Started");
            }
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Update button state when returning to this fragment
        updateButtonState();
    }

    private void searchVehicles(String location, String pickupDate, String returnDate) {
        // Filter vehicles based on search criteria
        Toast.makeText(getContext(),
            "Đang tìm xe tại " + location + " từ " + pickupDate + " đến " + returnDate,
            Toast.LENGTH_SHORT).show();

        // TODO: Implement actual filtering logic with API
        // For now, just reload from API
        loadVehicleDataFromApi();
    }

    private void loadVehicleDataFromApi() {
        // Show loading state
        if (binding.rvVehicles != null) {
            binding.rvVehicles.setVisibility(View.VISIBLE);
        }

        // Load available vehicles from API
        vehicleRepository.getAvailableVehicles(20, new VehicleRepository.VehicleCallback() {
            @Override
            public void onSuccess(List<com.example.prm_project.models.Vehicle> apiVehicles) {
                // Convert API vehicles to UI vehicles
                vehicleList.clear();
                for (com.example.prm_project.models.Vehicle apiVehicle : apiVehicles) {
                    Vehicle uiVehicle = VehicleConverter.toUIVehicle(apiVehicle);
                    if (uiVehicle != null) {
                        vehicleList.add(uiVehicle);
                    }
                }
                
                // Update UI on main thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        vehicleAdapter.notifyDataSetChanged();
                        if (vehicleList.isEmpty()) {
                            Toast.makeText(getContext(), "Không có xe nào khả dụng", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Show error on main thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Lỗi kết nối API: " + errorMessage, Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
