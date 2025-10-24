package com.example.prm_project.ui.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm_project.ui.home.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends ViewModel {

    private final MutableLiveData<String> mText = new MutableLiveData<>();
    private final MutableLiveData<List<Vehicle>> filteredVehicles = new MutableLiveData<>();
    private final List<Vehicle> allVehicles = new ArrayList<>();

    public SearchViewModel() {
        mText.setValue("This is search fragment");

        // Sample data
        allVehicles.add(new Vehicle("BMW iX3", "2023 • BMW • SUV", "92%", "460 km range", "5 Seats", "District 3 Station", "$25", "/hour • $200/day", "Available", "4.7", "Excellent"));
        allVehicles.add(new Vehicle("Tesla Model 3", "2022 • Tesla • Sedan", "85%", "358 km range", "5 Seats", "District 7 Station", "$18", "/hour • $150/day", "Available", "4.9", "Good"));
        allVehicles.add(new Vehicle("Audi e-tron GT", "2024 • Audi • Sports Car", "78%", "380 km range", "4 Seats", "District 1 Station", "$35", "/hour • $280/day", "Available", "4.8", "Excellent"));
        allVehicles.add(new Vehicle("VinFast VF8", "2023 • VinFast • SUV", "90%", "420 km range", "5 Seats", "District 1 Station", "$15", "/hour • $120/day", "Available", "4.8", "Excellent"));
        allVehicles.add(new Vehicle("Hyundai Kona Electric", "2023 • Hyundai • Crossover", "78%", "305 km range", "5 Seats", "Binh Thanh Station", "$12", "/hour • $90/day", "Available", "4.5", "Good"));

        filteredVehicles.setValue(new ArrayList<>(allVehicles));
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<Vehicle>> getFilteredVehicles() {
        return filteredVehicles;
    }

    public void search(String location, String pickupDate, String returnDate) {
        if (location == null) location = "";
        String q = location.trim().toLowerCase();
        if (q.isEmpty()) {
            filteredVehicles.setValue(new ArrayList<>(allVehicles));
            return;
        }

        List<Vehicle> result = new ArrayList<>();
        for (Vehicle v : allVehicles) {
            if (v.getLocation() != null && v.getLocation().toLowerCase().contains(q)) {
                result.add(v);
                continue;
            }
            if (v.getName() != null && v.getName().toLowerCase().contains(q)) {
                result.add(v);
            }
        }

        filteredVehicles.setValue(result);
    }
}