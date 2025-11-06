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
        @SerializedName("idNumber")
        private String idNumber;

        @SerializedName("licenseNumber")
        private String licenseNumber;

        @SerializedName("idFrontImage")
        private ImageObject idFrontImage;

        @SerializedName("idBackImage")
        private ImageObject idBackImage;

        @SerializedName("licenseFrontImage")
        private ImageObject licenseFrontImage;

        @SerializedName("licenseBackImage")
        private ImageObject licenseBackImage;

        @SerializedName("verified")
        private Boolean verified;

        @SerializedName("verifiedAt")
        private String verifiedAt;

        public String getIdNumber() { return idNumber; }
        public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

        public String getLicenseNumber() { return licenseNumber; }
        public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

        public ImageObject getIdFrontImage() { return idFrontImage; }
        public void setIdFrontImage(ImageObject idFrontImage) { this.idFrontImage = idFrontImage; }

        public ImageObject getIdBackImage() { return idBackImage; }
        public void setIdBackImage(ImageObject idBackImage) { this.idBackImage = idBackImage; }

        public ImageObject getLicenseFrontImage() { return licenseFrontImage; }
        public void setLicenseFrontImage(ImageObject licenseFrontImage) { this.licenseFrontImage = licenseFrontImage; }

        public ImageObject getLicenseBackImage() { return licenseBackImage; }
        public void setLicenseBackImage(ImageObject licenseBackImage) { this.licenseBackImage = licenseBackImage; }

        public Boolean getVerified() { return verified; }
        public void setVerified(Boolean verified) { this.verified = verified; }

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

    // Verification helpers - get from kyc object (backend structure)
    public Boolean getIsVerified() {
        // Check kyc.verified (correct location in backend response)
        if (kyc != null && kyc.getVerified() != null) {
            return kyc.getVerified();
        }
        return false;
    }

    public void setIsVerified(Boolean verified) {
        // Also set in kyc object
        if (kyc == null) {
            kyc = new Kyc();
        }
        kyc.setVerified(verified);
        // Keep root level for backward compatibility
        this.verified = verified;
    }

    public Boolean getVerified() {
        // Check kyc.verified (correct location in backend response)
        if (kyc != null && kyc.getVerified() != null) {
            return kyc.getVerified();
        }
        return false;
    }

    public void setVerified(Boolean verified) {
        // Also set in kyc object
        if (kyc == null) {
            kyc = new Kyc();
        }
        kyc.setVerified(verified);
        // Keep root level for backward compatibility
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
        // Check verification status from kyc object (correct location in backend response)
        if (kyc != null && kyc.getVerified() != null && kyc.getVerified()) {
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
