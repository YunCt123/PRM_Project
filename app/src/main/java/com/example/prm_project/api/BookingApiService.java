package com.example.prm_project.api;

import com.example.prm_project.models.Booking;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BookingApiService {
    @POST("api/bookings")
    Call<Booking> createBooking(@Body Booking booking);
}
