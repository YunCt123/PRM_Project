package com.example.prm_project.api;

import com.example.prm_project.models.ApiResponse;
import com.example.prm_project.models.Booking;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * API Service interface for Booking endpoints
 */
public interface BookingApiService {

    /**
     * Get all bookings for the current user
     * GET /api/bookings/mine
     * @param token Authorization Bearer token
     * @return ApiResponse with list of Booking data
     */
    @GET("api/bookings/mine")
    Call<ApiResponse<List<Booking>>> getMyBookings(
            @Header("Authorization") String token
    );
}

