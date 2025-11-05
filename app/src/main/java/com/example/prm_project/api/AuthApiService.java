package com.example.prm_project.api;

import com.example.prm_project.models.ApiResponse;
import com.example.prm_project.models.AuthData;
import com.example.prm_project.models.AuthRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Auth API Service - dùng chung ApiResponse và AuthRequest
 */
public interface AuthApiService {
    
    @POST("api/auth/login")
    Call<ApiResponse<AuthData>> login(@Body AuthRequest request);
    
    @POST("api/auth/register")
    Call<ApiResponse<AuthData>> register(@Body AuthRequest request);
}
