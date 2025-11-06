package com.example.prm_project.api;

import com.example.prm_project.models.ApiResponse;
import com.example.prm_project.models.ProfileSettingsUpdateRequest;
import com.example.prm_project.models.User;
import com.example.prm_project.models.UserUpdateRequest;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
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
     * Update basic user profile info (name, email, dateOfBirth, phone)
     * PATCH /api/users/me
     * @param token Authorization Bearer token
     * @param request UserUpdateRequest with fields to update
     * @return ApiResponse với User data
     */
    @PATCH("api/users/me")
    Call<ApiResponse<User>> updateBasicProfile(
            @Header("Authorization") String token,
            @Body UserUpdateRequest request
    );

    /**
     * Update profile settings (name, phone, gender only)
     * PATCH /api/users/me
     * @param token Authorization Bearer token
     * @param request ProfileSettingsUpdateRequest with name, phone, gender
     * @return ApiResponse với User data
     */
    @PATCH("api/users/me")
    Call<ApiResponse<User>> updateProfileSettings(
            @Header("Authorization") String token,
            @Body ProfileSettingsUpdateRequest request
    );

    /**
     * Update user profile with KYC documents
     * PATCH /api/users/me
     * @param token Authorization Bearer token
     * @param namePart Multipart part containing name (form field 'name')
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
            @Part MultipartBody.Part namePart,
            @Part("phone") RequestBody phone,
            @Part("gender") RequestBody gender,
            @Part MultipartBody.Part avatar,
            @Part MultipartBody.Part licenseFrontImage,
            @Part MultipartBody.Part licenseBackImage,
            @Part MultipartBody.Part idFrontImage,
            @Part MultipartBody.Part idBackImage
    );
}
