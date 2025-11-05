package com.example.prm_project.api;

import com.example.prm_project.models.BookingRequest;
import com.example.prm_project.models.BookingResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

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
}
