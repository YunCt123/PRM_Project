package com.example.prm_project.repository;

import android.util.Log;

import com.example.prm_project.api.ApiClient;
import com.example.prm_project.api.BookingApiService;
import com.example.prm_project.models.BookingRequest;
import com.example.prm_project.models.BookingResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingRepository {
    private static final String TAG = "BookingRepository";
    private final BookingApiService apiService;

    public BookingRepository() {
        this.apiService = ApiClient.getClient().create(BookingApiService.class);
    }

    /**
     * Tạo booking mới với thanh toán PayOS
     * @param token Bearer token của user
     * @param vehicleId ID của xe muốn thuê
     * @param startTime Thời gian bắt đầu (format: "2025-11-05T02:00:00.000Z")
     * @param endTime Thời gian kết thúc (format: "2025-11-06T02:00:00.000Z")
     * @param callback Callback để nhận kết quả
     */
    public void createBooking(String token, String vehicleId, String startTime, String endTime, BookingCallback callback) {
        // Tạo request với provider là "payos"
        BookingRequest request = new BookingRequest(vehicleId, startTime, endTime, "payos");
        
        // Authorization header với Bearer token
        String authHeader = "Bearer " + token;
        
        Call<BookingResponse> call = apiService.createBooking(authHeader, request);
        
        call.enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Booking created successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Error: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG, "Booking failed: " + errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                String errorMsg = "Network error: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                callback.onError(errorMsg);
            }
        });
    }

    // Callback interface
    public interface BookingCallback {
        void onSuccess(BookingResponse response);
        void onError(String errorMessage);
    }
}
