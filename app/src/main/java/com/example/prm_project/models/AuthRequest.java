package com.example.prm_project.models;

import com.google.gson.annotations.SerializedName;

public class AuthRequest {

    // Required cho cả login và register
    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    // Chỉ cho register
    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("gender")
    private String gender;

    @SerializedName("role")
    private String role;

    // Constructor cho Login
    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Constructor cho Register
    public AuthRequest(String name, String email, String password, String phone, String gender, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.gender = gender;
        this.role = role;
    }

    // Getters and Setters
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
