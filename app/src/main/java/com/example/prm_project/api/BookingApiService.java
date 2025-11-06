package com.example.prm_project.api;

import com.example.prm_project.models.Booking;
import com.example.prm_project.models.BookingRequest;
import com.example.prm_project.models.BookingResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookingApiService {
    /**
     * Tạo booking mới với thanh toán PayOS
     * POST /api/bookings
     * @param authorization Bearer token
     * @param bookingRequest Request body chứa vehicleId, startTime, endTime, deposit
     * @return BookingResponse với thông tin booking và PayOS checkout URL
     */
    @POST("api/bookings")
    Call<BookingResponse> createBooking(
            @Header("Authorization") String authorization,
            @Body BookingRequest bookingRequest
    );

    @GET("api/bookings/mine")
    Call<Booking.BookingListResponse> getMyBookings(@Query("page") int page, @Query("limit") int limit, @Query("status") String status);

    @GET("api/bookings/{id}")
    Call<Booking.BookingItem> getBookingDetails(@Path("id") String bookingId);
}
