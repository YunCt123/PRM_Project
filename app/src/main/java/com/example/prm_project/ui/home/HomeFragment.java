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

import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private Button loginButton;
    private FragmentHomeBinding binding;
    private VehicleAdapter vehicleAdapter;
    private List<Vehicle> vehicleList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupRecyclerView();
        setupDatePickers();
        setupSearchButton();
        loadVehicleData();
        login();

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
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
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

    private void searchVehicles(String location, String pickupDate, String returnDate) {
        // Filter vehicles based on search criteria
        Toast.makeText(getContext(),
            "Searching vehicles for " + location + " from " + pickupDate + " to " + returnDate,
            Toast.LENGTH_SHORT).show();

        // You can implement actual filtering logic here
        loadVehicleData();
    }

    private void loadVehicleData() {
        vehicleList.clear();

        // Sample data with vehicle images cycling through xe1, xe2, xe3
        vehicleList.add(new Vehicle(
            "BMW iX3",
            "2023 • BMW • SUV",
            "92%",
            "460 km range",
            "5 Seats",
            "District 3 Station",
            "$25",
            "/hour • $200/day",
            "Available",
            "4.7",
            "Excellent"
        ));

        vehicleList.add(new Vehicle(
            "Tesla Model 3",
            "2022 • Tesla • Sedan",
            "85%",
            "358 km range",
            "5 Seats",
            "District 7 Station",
            "$18",
            "/hour • $150/day",
            "Available",
            "4.9",
            "Good"
        ));

        vehicleList.add(new Vehicle(
            "Audi e-tron GT",
            "2024 • Audi • Sports Car",
            "78%",
            "380 km range",
            "4 Seats",
            "District 1 Station",
            "$35",
            "/hour • $280/day",
            "Available",
            "4.8",
            "Excellent"
        ));

        vehicleList.add(new Vehicle(
            "VinFast VF8",
            "2023 • VinFast • SUV",
            "90%",
            "420 km range",
            "5 Seats",
            "District 1 Station",
            "$15",
            "/hour • $120/day",
            "Available",
            "4.8",
            "Excellent"
        ));

        vehicleList.add(new Vehicle(
            "Hyundai Kona Electric",
            "2023 • Hyundai • Crossover",
            "78%",
            "305 km range",
            "5 Seats",
            "Binh Thanh Station",
            "$12",
            "/hour • $90/day",
            "Available",
            "4.5",
            "Good"
        ));

        vehicleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}