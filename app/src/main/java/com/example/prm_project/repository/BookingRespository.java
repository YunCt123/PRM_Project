package com.example.prm_project.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.prm_project.api.ApiClient;
import com.example.prm_project.api.BookingApiService;
import com.example.prm_project.models.Booking;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingRespository {
    private static final String TAG = "BookingRepository";
    private BookingApiService bookingApiService;

    public BookingRespository() {
        this.bookingApiService = ApiClient.getClient().create(BookingApiService.class);
    }

    public LiveData<Booking> createBooking(Booking booking) {
        MutableLiveData<Booking> data = new MutableLiveData<>();
        bookingApiService.createBooking(booking).enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Booking created successfully: " + response.body());
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "Booking creation failed - Code: " + response.code() + ", Message: " + response.message());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "Error Body: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                Log.e(TAG, "Booking creation network failure", t);
                data.setValue(null);
            }
        });
        return data;
    }
}
