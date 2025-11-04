package com.example.prm_project.repository;

import android.util.Log;

import com.example.prm_project.api.ApiClient;
import com.example.prm_project.api.AuthApiService;
import com.example.prm_project.models.AuthResponse;
import com.example.prm_project.models.LoginRequest;
import com.example.prm_project.models.RegisterRequest;
import com.example.prm_project.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository class để quản lý các API calls liên quan đến Authentication
 */
public class AuthRepository {
    private static final String TAG = "AuthRepository";
    private AuthApiService apiService;

    public AuthRepository() {
        this.apiService = ApiClient.getClient().create(AuthApiService.class);
    }

    /**
     * Callback interface cho login/register operations
     */
    public interface AuthCallback {
        void onSuccess(User user, String token);
        void onError(String errorMessage);
    }

    /**
     * Login user
     * @param email User's email
     * @param password User's password
     * @param callback Callback để nhận kết quả
     */
    public void login(String email, String password, final AuthCallback callback) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<AuthResponse> call = apiService.login(loginRequest);
        
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    
                    if (authResponse.isSuccess() && authResponse.getUser() != null) {
                        Log.d(TAG, "Login successful: " + authResponse.getUser().getEmail());
                        callback.onSuccess(authResponse.getUser(), authResponse.getToken());
                    } else {
                        String errorMsg = authResponse.getError() != null ? 
                                authResponse.getError() : "Login failed";
                        Log.e(TAG, "Login error: " + errorMsg);
                        callback.onError(errorMsg);
                    }
                } else {
                    String errorMsg = "Login failed: " + response.code();
                    if (response.code() == 401) {
                        errorMsg = "Email hoặc mật khẩu không đúng";
                    } else if (response.code() == 404) {
                        errorMsg = "Tài khoản không tồn tại";
                    }
                    Log.e(TAG, errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, "Network error", t);
                callback.onError(errorMsg);
            }
        });
    }

    /**
     * Register new user
     * @param name User's full name
     * @param email User's email
     * @param password User's password
     * @param phone User's phone number
     * @param gender User's gender (male/female)
     * @param role User's role (renter/owner)
     * @param callback Callback để nhận kết quả
     */
    public void register(String name, String email, String password, String phone, 
                        String gender, String role, final AuthCallback callback) {
        RegisterRequest registerRequest = new RegisterRequest(name, email, password, phone, gender, role);
        Call<AuthResponse> call = apiService.register(registerRequest);
        
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    
                    if (authResponse.isSuccess() && authResponse.getUser() != null) {
                        Log.d(TAG, "Registration successful: " + authResponse.getUser().getEmail());
                        callback.onSuccess(authResponse.getUser(), authResponse.getToken());
                    } else {
                        String errorMsg = authResponse.getError() != null ? 
                                authResponse.getError() : "Registration failed";
                        Log.e(TAG, "Registration error: " + errorMsg);
                        callback.onError(errorMsg);
                    }
                } else {
                    String errorMsg = "Registration failed: " + response.code();
                    if (response.code() == 400) {
                        errorMsg = "Thông tin đăng ký không hợp lệ";
                    } else if (response.code() == 409) {
                        errorMsg = "Email đã được sử dụng";
                    }
                    Log.e(TAG, errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, "Network error", t);
                callback.onError(errorMsg);
            }
        });
    }
}
