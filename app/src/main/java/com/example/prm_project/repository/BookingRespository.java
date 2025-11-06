package com.example.prm_project.repository;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.prm_project.api.ApiClient;
import com.example.prm_project.api.BookingApiService;
import com.example.prm_project.models.Booking;
import com.example.prm_project.models.BookingRequest;
import com.example.prm_project.models.BookingResponse;
import com.example.prm_project.utils.SessionManager;
import com.google.gson.Gson;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingRespository {
    private static final String TAG = "BookingRepository";
    private BookingApiService bookingApiService;
    private Gson gson = new Gson();
    private Context context;
    private SessionManager sessionManager;

    public BookingRespository(Context context) {
        this.context = context;
        this.bookingApiService = ApiClient.getAuthenticatedClient(context).create(BookingApiService.class);
        this.sessionManager = new SessionManager(context);
    }

    public LiveData<BookingResponse> createBooking(String vehicleId, String startTime, String endTime) {
        MutableLiveData<BookingResponse> data = new MutableLiveData<>();

        // Create BookingRequest with deposit provider
        BookingRequest bookingRequest = new BookingRequest(vehicleId, startTime, endTime, "payos");

        // Debug log the booking request
        Log.d(TAG, "Creating booking - vehicleId: " + vehicleId);
        Log.d(TAG, "Creating booking - startTime: " + startTime);
        Log.d(TAG, "Creating booking - endTime: " + endTime);
        Log.d(TAG, "Creating booking - deposit provider: payos");

        // Log the full JSON request body
        String jsonBody = gson.toJson(bookingRequest);
        Log.d(TAG, "Booking request JSON: " + jsonBody);

        // Get Bearer token
        String token = sessionManager.getToken();
        String authHeader = "Bearer " + token;

        bookingApiService.createBooking(authHeader, bookingRequest).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                Log.d(TAG, "Response received - Code: " + response.code() + ", Message: " + response.message());
                if (response.isSuccessful()) {
                    Log.d(TAG, "Booking created successfully: " + response.body());
                    data.setValue(response.body());
                } else {
                    Log.e(TAG,
                            "Booking creation failed - Code: " + response.code() + ", Message: " + response.message());
                    Log.e(TAG, "Request headers: " + call.request().headers());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string()
                                : "No error body";
                        Log.e(TAG, "Error Body: " + errorBody);

                        // Parse error message and show toast
                        try {
                            JSONObject json = new JSONObject(errorBody);
                            String message = json.optString("message", "Booking creation failed");
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        } catch (Exception parseException) {
                            Toast.makeText(context, "Tạo thông tin đặt xe thất bại", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                        Toast.makeText(context, "Booking creation failed", Toast.LENGTH_LONG).show();
                    }
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                Log.e(TAG, "Booking creation network failure", t);
                Log.e(TAG, "Request URL: " + call.request().url());
                Log.e(TAG, "Request method: " + call.request().method());
                Toast.makeText(context, "Lỗi mạng khi tạo booking", Toast.LENGTH_SHORT).show();
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<Booking.BookingListResponse> getMyBookings(int page, int limit, String status) {
        MutableLiveData<Booking.BookingListResponse> data = new MutableLiveData<>();

        Log.d(TAG, "Getting my bookings - page: " + page + ", limit: " + limit + ", status: " + status);

        bookingApiService.getMyBookings(page, limit, status).enqueue(new Callback<Booking.BookingListResponse>() {
            @Override
            public void onResponse(Call<Booking.BookingListResponse> call, Response<Booking.BookingListResponse> response) {
                Log.d(TAG, "Response received - Code: " + response.code() + ", Message: " + response.message());
                if (response.isSuccessful()) {
                    Log.d(TAG, "Bookings retrieved successfully");
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "Failed to get bookings - Code: " + response.code() + ", Message: " + response.message());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "Error Body: " + errorBody);
                        Toast.makeText(context, "Không thể tải danh sách đặt xe", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Booking.BookingListResponse> call, Throwable t) {
                Log.e(TAG, "Network failure getting bookings", t);
                Toast.makeText(context, "Lỗi mạng khi tải danh sách đặt xe", Toast.LENGTH_SHORT).show();
                data.setValue(null);
            }
        });

        return data;
    }

    public LiveData<Booking.BookingItem> getBookingDetails(String bookingId) {
        MutableLiveData<Booking.BookingItem> data = new MutableLiveData<>();

        Log.d(TAG, "Getting booking details for ID: " + bookingId);

        bookingApiService.getBookingDetails(bookingId).enqueue(new Callback<Booking.BookingItem>() {
            @Override
            public void onResponse(Call<Booking.BookingItem> call, Response<Booking.BookingItem> response) {
                Log.d(TAG, "Response received - Code: " + response.code() + ", Message: " + response.message());
                if (response.isSuccessful()) {
                    Log.d(TAG, "Booking details retrieved successfully");
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "Failed to get booking details - Code: " + response.code() + ", Message: " + response.message());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "Error Body: " + errorBody);
                        Toast.makeText(context, "Không thể tải chi tiết đặt xe", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Booking.BookingItem> call, Throwable t) {
                Log.e(TAG, "Network failure getting booking details", t);
                Toast.makeText(context, "Lỗi mạng khi tải chi tiết đặt xe", Toast.LENGTH_SHORT).show();
                data.setValue(null);
            }
        });

        return data;
    }
}
