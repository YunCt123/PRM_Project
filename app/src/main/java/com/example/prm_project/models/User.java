package com.example.prm_project.models;

import com.google.gson.annotations.SerializedName;

/**
 * User model representing user data from API
 */
public class User {

    @SerializedName("_id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("dateOfBirth")
    private String dateOfBirth;

    @SerializedName("gender")
    private String gender;

    @SerializedName("role")
    private String role;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    // Fix: backend uses "verified", not "isVerified"
    @SerializedName("verified")
    private Boolean verified;

    @SerializedName("verifiedAt")
    private String verifiedAt;

    @SerializedName("kyc")
    private Kyc kyc;

    public static class Kyc {
        @SerializedName("idFrontImage")
        private String idFrontImage;

        @SerializedName("idBackImage")
        private String idBackImage;

        @SerializedName("licenseFrontImage")
        private String licenseFrontImage;

        @SerializedName("licenseBackImage")
        private String licenseBackImage;

        @SerializedName("verifiedAt")
        private String verifiedAt;

        public String getIdFrontImage() { return idFrontImage; }
        public void setIdFrontImage(String idFrontImage) { this.idFrontImage = idFrontImage; }

        public String getIdBackImage() { return idBackImage; }
        public void setIdBackImage(String idBackImage) { this.idBackImage = idBackImage; }

        public String getLicenseFrontImage() { return licenseFrontImage; }
        public void setLicenseFrontImage(String licenseFrontImage) { this.licenseFrontImage = licenseFrontImage; }

        public String getLicenseBackImage() { return licenseBackImage; }
        public void setLicenseBackImage(String licenseBackImage) { this.licenseBackImage = licenseBackImage; }

        public String getVerifiedAt() { return verifiedAt; }
        public void setVerifiedAt(String verifiedAt) { this.verifiedAt = verifiedAt; }
    }

    // Constructors
    public User() {
    }

    public User(String id, String name, String email, String phone, String dateOfBirth, String gender, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.role = role;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Verification helpers - updated to use "verified" field
    public Boolean getIsVerified() {
        return verified != null ? verified : false;
    }

    public void setIsVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getVerified() {
        return verified != null ? verified : false;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(String verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public Kyc getKyc() { return kyc; }
    public void setKyc(Kyc kyc) { this.kyc = kyc; }

    /**
     * Returns a simple verification status string: "verified", "pending", or "unverified"
     */
    public String getVerificationStatus() {
        // If verified is true and has verifiedAt date, user is fully verified
        if (verified != null && verified && verifiedAt != null) {
            return "verified";
        }
        // If has KYC images uploaded but not yet verified, status is pending
        if (kyc != null &&
            kyc.getIdFrontImage() != null &&
            kyc.getIdBackImage() != null &&
            kyc.getLicenseFrontImage() != null &&
            kyc.getLicenseBackImage() != null) {
            return "pending";
        }
        return "unverified";
    }

    @Override
    public String toString() {
        return "User{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", email='" + email + '\'' + ", phone='" + phone + '\'' + ", dateOfBirth='" + dateOfBirth + '\'' + ", gender='" + gender + '\'' + ", role='" + role + '\'' + ", verified=" + verified + '}';
    }
}
