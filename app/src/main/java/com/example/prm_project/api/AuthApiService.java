package com.example.prm_project.api;

import com.example.prm_project.models.AuthResponse;
import com.example.prm_project.models.LoginRequest;
import com.example.prm_project.models.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * API Service interface cho Authentication endpoints
 */
public interface AuthApiService {
    
    /**
     * Login user
     * POST /api/auth/login
     * @param loginRequest Request body chứa email và password
     * @return AuthResponse với user data và token
     */
    @POST("api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest loginRequest);
    
    /**
     * Register new user
     * POST /api/auth/register
     * @param registerRequest Request body chứa thông tin đăng ký
     * @return AuthResponse với user data và token
     */
    @POST("api/auth/register")
    Call<AuthResponse> register(@Body RegisterRequest registerRequest);
}
