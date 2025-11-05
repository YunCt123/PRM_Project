package com.example.prm_project.models;

import com.google.gson.annotations.SerializedName;

public class AuthData {

    @SerializedName("user")
    private User user;

    @SerializedName("token")
    private String token;

    public AuthData() {
    }

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

