package com.example.prm_project.models;

import com.google.gson.annotations.SerializedName;

/**
 * Request body cho login API
 */
public class LoginRequest {
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("password")
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
