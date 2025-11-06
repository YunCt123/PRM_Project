package com.example.prm_project.models;

import com.google.gson.annotations.SerializedName;

/**
 * Request model for updating user settings (name, phone, gender)
 */
public class ProfileSettingsUpdateRequest {

    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("gender")
    private String gender;

    public ProfileSettingsUpdateRequest() {
    }

    public ProfileSettingsUpdateRequest(String name, String phone, String gender) {
        this.name = name;
        this.phone = phone;
        this.gender = gender;
    }

    // Getters and Setters
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
}

