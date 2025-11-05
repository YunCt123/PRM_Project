package com.example.prm_project.repository;

import android.util.Log;

import com.example.prm_project.api.ApiClient;
import com.example.prm_project.api.AuthApiService;
import com.example.prm_project.models.ApiResponse;
import com.example.prm_project.models.AuthData;
import com.example.prm_project.models.AuthRequest;
import com.example.prm_project.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Auth Repository - dùng ApiResponse và AuthRequest thống nhất
 */
public class AuthRepository {
    private static final String TAG = "AuthRepository";
    private AuthApiService apiService;

    public AuthRepository() {
        this.apiService = ApiClient.getClient().create(AuthApiService.class);
    }

    public interface AuthCallback {
        void onSuccess(User user, String token);
        void onError(String errorMessage);
    }

    public void login(String email, String password, final AuthCallback callback) {
        AuthRequest request = new AuthRequest(email, password);
        Call<ApiResponse<AuthData>> call = apiService.login(request);
        
        call.enqueue(new Callback<ApiResponse<AuthData>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthData>> call, Response<ApiResponse<AuthData>> response) {
                handleAuthResponse(response, callback, "Login");
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthData>> call, Throwable t) {
                Log.e(TAG, "Login network error", t);
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void register(String name, String email, String password, String phone, 
                        String gender, String role, final AuthCallback callback) {
        AuthRequest request = new AuthRequest(name, email, password, phone, gender, role);
        Call<ApiResponse<AuthData>> call = apiService.register(request);
        
        call.enqueue(new Callback<ApiResponse<AuthData>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthData>> call, Response<ApiResponse<AuthData>> response) {
                handleAuthResponse(response, callback, "Register");
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthData>> call, Throwable t) {
                Log.e(TAG, "Register network error", t);
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void handleAuthResponse(Response<ApiResponse<AuthData>> response, 
                                    AuthCallback callback, String action) {
        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<AuthData> apiResponse = response.body();
            
            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                AuthData authData = apiResponse.getData();
                if (authData.getUser() != null && authData.getToken() != null) {
                    Log.d(TAG, action + " successful");
                    callback.onSuccess(authData.getUser(), authData.getToken());
                    return;
                }
            }
            
            String errorMsg = apiResponse.getError();
            if (errorMsg == null || errorMsg.isEmpty()) {
                errorMsg = apiResponse.getMessage();
            }
            if (errorMsg == null || errorMsg.isEmpty()) {
                errorMsg = action + " thất bại";
            }
            Log.e(TAG, action + " error: " + errorMsg);
            callback.onError(errorMsg);
        } else {
            String errorMsg = getHttpErrorMessage(response.code());
            Log.e(TAG, action + " failed: " + errorMsg);
            callback.onError(errorMsg);
        }
    }

    private String getHttpErrorMessage(int code) {
        switch (code) {
            case 400:
                return "Thông tin không hợp lệ";
            case 401:
                return "Email hoặc mật khẩu không đúng";
            case 404:
                return "Tài khoản không tồn tại";
            case 409:
                return "Email đã được sử dụng";
            case 500:
                return "Lỗi server";
            default:
                return "Lỗi: " + code;
        }
    }
}
