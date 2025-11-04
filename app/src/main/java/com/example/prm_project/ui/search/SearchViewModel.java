package com.example.prm_project.ui.search;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm_project.repository.VehicleRepository;
import com.example.prm_project.ui.home.Vehicle;
import com.example.prm_project.utils.VehicleConverter;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends ViewModel {

    private static final String TAG = "SearchViewModel";
    
    private final MutableLiveData<String> mText = new MutableLiveData<>();
    private final MutableLiveData<List<Vehicle>> filteredVehicles = new MutableLiveData<>();
    private final List<Vehicle> allVehicles = new ArrayList<>();
    private final VehicleRepository vehicleRepository;

    public SearchViewModel() {
        mText.setValue("This is search fragment");
        vehicleRepository = new VehicleRepository();
        
        // Load all vehicles from API
        loadAllVehiclesFromApi();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<Vehicle>> getFilteredVehicles() {
        return filteredVehicles;
    }

    private void loadAllVehiclesFromApi() {
        // Load all vehicles from API
        vehicleRepository.getAllVehicles(new VehicleRepository.VehicleCallback() {
            @Override
            public void onSuccess(List<com.example.prm_project.models.Vehicle> apiVehicles) {
                allVehicles.clear();
                for (com.example.prm_project.models.Vehicle apiVehicle : apiVehicles) {
                    Vehicle uiVehicle = VehicleConverter.toUIVehicle(apiVehicle);
                    if (uiVehicle != null) {
                        allVehicles.add(uiVehicle);
                    }
                }
                filteredVehicles.postValue(new ArrayList<>(allVehicles));
                Log.d(TAG, "Loaded " + allVehicles.size() + " vehicles from API");
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error loading vehicles from API: " + errorMessage);
                // Không có fallback data - chỉ hiển thị lỗi
                filteredVehicles.postValue(new ArrayList<>());
            }
        });
    }

    public void search(String location, String pickupDate, String returnDate) {
        if (location == null) location = "";
        String q = location.trim().toLowerCase();
        
        if (q.isEmpty()) {
            // If empty, show all vehicles
            filteredVehicles.setValue(new ArrayList<>(allVehicles));
            return;
        }

        // Search locally in loaded vehicles
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

    /**
     * Filter vehicles by brand từ API data
     * @param brand Brand name để filter (ví dụ: "Xiaomi", "VinFast", etc.)
     */
    public void searchByBrand(String brand) {
        if (brand == null || brand.trim().isEmpty()) {
            filteredVehicles.setValue(new ArrayList<>(allVehicles));
            return;
        }

        String brandLower = brand.trim().toLowerCase();
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle v : allVehicles) {
            // Filter theo brand field từ API thay vì name
            if (v.getBrand() != null && v.getBrand().toLowerCase().contains(brandLower)) {
                result.add(v);
            }
        }
        filteredVehicles.setValue(result);
        Log.d(TAG, "Filtered by brand '" + brand + "': " + result.size() + " vehicles");
    }
    
    /**
     * Get danh sách các brand unique từ data
     */
    public List<String> getAvailableBrands() {
        List<String> brands = new ArrayList<>();
        for (Vehicle v : allVehicles) {
            if (v.getBrand() != null && !v.getBrand().isEmpty() && !brands.contains(v.getBrand())) {
                brands.add(v.getBrand());
            }
        }
        return brands;
    }

    public void reload() {
        loadAllVehiclesFromApi();
    }
}