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

public class BookingRepository {
    private static final String TAG = "BookingRepository";
    private BookingApiService bookingApiService;
    private Gson gson = new Gson();
    private Context context;
    private SessionManager sessionManager;

    public BookingRepository(Context context) {
        this.context = context;
        this.bookingApiService = ApiClient.getAuthenticatedClient(context).create(BookingApiService.class);
        this.sessionManager = new SessionManager(context);
    }

    public LiveData<BookingResponse> createBooking(String vehicleId, String startTime, String endTime) {
        MutableLiveData<BookingResponse> data = new MutableLiveData<>();

        // Create BookingRequest with deposit provider
        BookingRequest bookingRequest = new BookingRequest(vehicleId, startTime, endTime, "payos");

        // Get Bearer token
        String token = sessionManager.getToken();
        String authHeader = "Bearer " + token;

        bookingApiService.createBooking(authHeader, bookingRequest).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        
                        // Parse error message and show toast
                        try {
                            JSONObject json = new JSONObject(errorBody);
                            String message = json.optString("message", "Booking creation failed");
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        } catch (Exception parseException) {
                            Toast.makeText(context, "Tạo thông tin đặt xe thất bại", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Booking creation failed", Toast.LENGTH_LONG).show();
                    }
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                Toast.makeText(context, "Lỗi mạng khi tạo booking", Toast.LENGTH_SHORT).show();
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<Booking.BookingListResponse> getMyBookings(int page, int limit, String status) {
        MutableLiveData<Booking.BookingListResponse> data = new MutableLiveData<>();

        // Get Bearer token
        String token = sessionManager.getToken();
        String authHeader = "Bearer " + token;

        Log.d(TAG, "getMyBookings - page: " + page + ", limit: " + limit + ", status: '" + status + "'");
        Log.d(TAG, "Token: " + (token != null ? "exists" : "null"));

        bookingApiService.getMyBookings(authHeader, page, limit, status).enqueue(new Callback<Booking.BookingListResponse>() {
            @Override
            public void onResponse(Call<Booking.BookingListResponse> call, Response<Booking.BookingListResponse> response) {
                Log.d(TAG, "Response code: " + response.code());
                if (response.isSuccessful()) {
                    Booking.BookingListResponse body = response.body();
                    if (body != null) {
                        Log.d(TAG, "Success! Items: " + (body.getItems() != null ? body.getItems().size() : 0));
                        Log.d(TAG, "Total: " + body.getTotal() + ", Page: " + body.getPage());
                    }
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "Error response: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "Error body: " + errorBody);
                        Toast.makeText(context, "Không thể tải danh sách đặt xe", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Booking.BookingListResponse> call, Throwable t) {
                Log.e(TAG, "Network failure", t);
                Toast.makeText(context, "Lỗi mạng khi tải danh sách đặt xe", Toast.LENGTH_SHORT).show();
                data.setValue(null);
            }
        });

        return data;
    }

    public LiveData<Booking.BookingItem> getBookingDetails(String bookingId) {
        MutableLiveData<Booking.BookingItem> data = new MutableLiveData<>();

        // Get Bearer token
        String token = sessionManager.getToken();
        String authHeader = "Bearer " + token;

        bookingApiService.getBookingDetails(authHeader, bookingId).enqueue(new Callback<Booking.BookingItem>() {
            @Override
            public void onResponse(Call<Booking.BookingItem> call, Response<Booking.BookingItem> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Toast.makeText(context, "Không thể tải chi tiết đặt xe", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        // Silent catch
                    }
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Booking.BookingItem> call, Throwable t) {
                Toast.makeText(context, "Lỗi mạng khi tải chi tiết đặt xe", Toast.LENGTH_SHORT).show();
                data.setValue(null);
            }
        });

        return data;
    }

    /**
     * Hủy booking
     * @param bookingId ID của booking cần hủy
     * @return LiveData chứa booking đã hủy hoặc null nếu lỗi
     */
    public LiveData<Booking.BookingItem> cancelBooking(String bookingId) {
        MutableLiveData<Booking.BookingItem> data = new MutableLiveData<>();

        // Get Bearer token
        String token = sessionManager.getToken();
        String authHeader = "Bearer " + token;

        Log.d(TAG, "Canceling booking: " + bookingId);

        bookingApiService.cancelBooking(authHeader, bookingId).enqueue(new Callback<Booking.BookingItem>() {
            @Override
            public void onResponse(Call<Booking.BookingItem> call, Response<Booking.BookingItem> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Booking cancelled successfully");
                    Toast.makeText(context, "Đã hủy đơn đặt xe thành công", Toast.LENGTH_SHORT).show();
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "Cancel booking failed: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "Error body: " + errorBody);
                        
                        // Parse error message
                        try {
                            JSONObject json = new JSONObject(errorBody);
                            String message = json.optString("message", "Không thể hủy đơn đặt xe");
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        } catch (Exception parseException) {
                            Toast.makeText(context, "Không thể hủy đơn đặt xe", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Không thể hủy đơn đặt xe", Toast.LENGTH_SHORT).show();
                    }
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Booking.BookingItem> call, Throwable t) {
                Log.e(TAG, "Network failure when canceling booking", t);
                Toast.makeText(context, "Lỗi mạng khi hủy đơn đặt xe", Toast.LENGTH_SHORT).show();
                data.setValue(null);
            }
        });

        return data;
    }
}
