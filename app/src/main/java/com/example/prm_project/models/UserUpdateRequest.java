package com.example.prm_project.models;

import com.google.gson.annotations.SerializedName;

/**
 * Request model for updating user profile (name, email, dateOfBirth, phone)
 */
public class UserUpdateRequest {

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("dateOfBirth")
    private String dateOfBirth;

    @SerializedName("phone")
    private String phone;

    public UserUpdateRequest() {
    }

    public UserUpdateRequest(String name, String email, String dateOfBirth, String phone) {
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.phone = phone;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

