package com.example.prm_project.api;

import com.example.prm_project.models.ApiResponse;
import com.example.prm_project.models.User;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.Part;

/**
 * API Service interface cho User endpoints
 */
public interface UserApiService {

    /**
     * Get current user profile
     * GET /api/users/me
     * @param token Authorization Bearer token
     * @return ApiResponse với User data
     */
    @GET("api/users/me")
    Call<ApiResponse<User>> getUserProfile(
            @Header("Authorization") String token
    );

    /**
     * Update user profile with KYC documents
     * PATCH /api/users/me
     * @param token Authorization Bearer token
     * @param name Tên người dùng
     * @param phone Số điện thoại
     * @param gender Giới tính
     * @param avatar File ảnh đại diện
     * @param licenseFrontImage Ảnh mặt trước giấy phép lái xe
     * @param licenseBackImage Ảnh mặt sau giấy phép lái xe
     * @param idFrontImage Ảnh mặt trước CMND/CCCD
     * @param idBackImage Ảnh mặt sau CMND/CCCD
     * @return ApiResponse với User data
     */
    @Multipart
    @PATCH("api/users/me")
    Call<ApiResponse<User>> updateProfile(
            @Header("Authorization") String token,
            @Part("name") RequestBody name,
            @Part("phone") RequestBody phone,
            @Part("gender") RequestBody gender,
            @Part MultipartBody.Part avatar,
            @Part MultipartBody.Part licenseFrontImage,
            @Part MultipartBody.Part licenseBackImage,
            @Part MultipartBody.Part idFrontImage,
            @Part MultipartBody.Part idBackImage
    );
}
