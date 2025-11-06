package com.example.prm_project.api;

import com.example.prm_project.models.Booking;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookingApiService {
    @POST("api/bookings")
    Call<Booking> createBooking(@Body Booking booking);

    @GET("api/bookings/mine")
    Call<Booking.BookingListResponse> getMyBookings(@Query("page") int page, @Query("limit") int limit, @Query("status") String status);

    @GET("api/bookings/{id}")
    Call<Booking.BookingItem> getBookingDetails(@Path("id") String bookingId);
}
