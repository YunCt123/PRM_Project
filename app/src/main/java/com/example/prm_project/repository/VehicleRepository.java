package com.example.prm_project.repository;

import android.util.Log;
import com.example.prm_project.api.ApiClient;
import com.example.prm_project.api.VehicleApiService;
import com.example.prm_project.models.Vehicle;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleRepository {
    private static final String TAG = "VehicleRepository";
    private VehicleApiService apiService;

    public VehicleRepository() {
        this.apiService = ApiClient.getClient().create(VehicleApiService.class);
    }

    /**
     * Lấy tất cả vehicles
     */
    public void getAllVehicles(final VehicleCallback callback) {
        Call<com.example.prm_project.models.ApiResponse<List<Vehicle>>> call = apiService.getAllVehicles();
        call.enqueue(new Callback<com.example.prm_project.models.ApiResponse<List<Vehicle>>>() {
            @Override
            public void onResponse(Call<com.example.prm_project.models.ApiResponse<List<Vehicle>>> call, 
                                 Response<com.example.prm_project.models.ApiResponse<List<Vehicle>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.prm_project.models.ApiResponse<List<Vehicle>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError("API returned error: " + apiResponse.getMessage());
                        Log.e(TAG, "API Error: " + apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to load vehicles: " + response.code());
                    Log.e(TAG, "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<com.example.prm_project.models.ApiResponse<List<Vehicle>>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
                Log.e(TAG, "Network error", t);
            }
        });
    }

    /**
     * Lấy vehicles có sẵn (available)
     */
    public void getAvailableVehicles(Integer limit, final VehicleCallback callback) {
        Call<com.example.prm_project.models.ApiResponse<List<Vehicle>>> call = apiService.getAvailableVehicles("available", limit);
        call.enqueue(new Callback<com.example.prm_project.models.ApiResponse<List<Vehicle>>>() {
            @Override
            public void onResponse(Call<com.example.prm_project.models.ApiResponse<List<Vehicle>>> call, 
                                 Response<com.example.prm_project.models.ApiResponse<List<Vehicle>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.prm_project.models.ApiResponse<List<Vehicle>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError("API returned error: " + apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to load available vehicles: " + response.code());
                    Log.e(TAG, "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<com.example.prm_project.models.ApiResponse<List<Vehicle>>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
                Log.e(TAG, "Network error", t);
            }
        });
    }

    /**
     * Tìm kiếm vehicles
     */
    public void searchVehicles(String query, String status, String brand, 
                               Double minPrice, Double maxPrice, Integer year,
                               final VehicleCallback callback) {
        Call<com.example.prm_project.models.ApiResponse<List<Vehicle>>> call = apiService.searchVehicles(query, status, brand, minPrice, maxPrice, year);
        call.enqueue(new Callback<com.example.prm_project.models.ApiResponse<List<Vehicle>>>() {
            @Override
            public void onResponse(Call<com.example.prm_project.models.ApiResponse<List<Vehicle>>> call, 
                                 Response<com.example.prm_project.models.ApiResponse<List<Vehicle>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.prm_project.models.ApiResponse<List<Vehicle>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError("Search failed: " + apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Search failed: " + response.code());
                    Log.e(TAG, "Search error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<com.example.prm_project.models.ApiResponse<List<Vehicle>>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
                Log.e(TAG, "Network error", t);
            }
        });
    }

    /**
     * Lấy chi tiết vehicle theo ID
     */
    public void getVehicleById(String id, final SingleVehicleCallback callback) {
        Call<com.example.prm_project.models.ApiResponse<Vehicle>> call = apiService.getVehicleById(id);
        call.enqueue(new Callback<com.example.prm_project.models.ApiResponse<Vehicle>>() {
            @Override
            public void onResponse(Call<com.example.prm_project.models.ApiResponse<Vehicle>> call, 
                                 Response<com.example.prm_project.models.ApiResponse<Vehicle>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.prm_project.models.ApiResponse<Vehicle> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError("Failed to load vehicle: " + apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to load vehicle: " + response.code());
                    Log.e(TAG, "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<com.example.prm_project.models.ApiResponse<Vehicle>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
                Log.e(TAG, "Network error", t);
            }
        });
    }

    /**
     * Lấy vehicles với filter
     */
    public void getVehiclesWithFilter(Integer page, Integer limit, String status, 
                                     String brand, Double minPrice, Double maxPrice,
                                     final VehicleCallback callback) {
        Call<com.example.prm_project.models.ApiResponse<List<Vehicle>>> call = apiService.getVehicles(page, limit, status, brand, minPrice, maxPrice);
        call.enqueue(new Callback<com.example.prm_project.models.ApiResponse<List<Vehicle>>>() {
            @Override
            public void onResponse(Call<com.example.prm_project.models.ApiResponse<List<Vehicle>>> call, 
                                 Response<com.example.prm_project.models.ApiResponse<List<Vehicle>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.prm_project.models.ApiResponse<List<Vehicle>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError("Failed to load vehicles: " + apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to load vehicles: " + response.code());
                    Log.e(TAG, "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<com.example.prm_project.models.ApiResponse<List<Vehicle>>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
                Log.e(TAG, "Network error", t);
            }
        });
    }

    // Callback interfaces
    public interface VehicleCallback {
        void onSuccess(List<Vehicle> vehicles);
        void onError(String errorMessage);
    }

    public interface SingleVehicleCallback {
        void onSuccess(Vehicle vehicle);
        void onError(String errorMessage);
    }
}
