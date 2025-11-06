package com.example.prm_project.models;

import com.google.gson.annotations.SerializedName;

/**
 * Response từ login/register API
 */
public class AuthResponse {
    
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private AuthData data;
    
    @SerializedName("error")
    private String error;

    // Nested class cho data
    public static class AuthData {
        @SerializedName("user")
        private User user;
        
        @SerializedName("token")
        private String token;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AuthData getData() {
        return data;
    }

    public void setData(AuthData data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    // Helper methods
    public User getUser() {
        return data != null ? data.getUser() : null;
    }

    public String getToken() {
        return data != null ? data.getToken() : null;
    }
}
