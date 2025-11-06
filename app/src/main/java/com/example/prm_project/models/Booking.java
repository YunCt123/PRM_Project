package com.example.prm_project.models;

import com.google.gson.annotations.SerializedName;

/**
 * Booking model representing rental booking data from API
 */
public class Booking {

    @SerializedName("_id")
    private String id;

    @SerializedName("vehicle")
    private Vehicle vehicle;

    @SerializedName("renter")
    private String renter;

    @SerializedName("startDate")
    private String startDate;

    @SerializedName("endDate")
    private String endDate;

    @SerializedName("totalPrice")
    private Double totalPrice;

    @SerializedName("status")
    private String status; // "pending", "approved", "rejected", "completed", "cancelled"

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    // Nested Vehicle class
    public static class Vehicle {
        @SerializedName("_id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("brand")
        private String brand;

        @SerializedName("model")
        private String model;

        @SerializedName("year")
        private Integer year;

        @SerializedName("pricePerDay")
        private Double pricePerDay;

        @SerializedName("images")
        private ImageData[] images;

        @SerializedName("licensePlate")
        private String licensePlate;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }

        public Integer getYear() { return year; }
        public void setYear(Integer year) { this.year = year; }

        public Double getPricePerDay() { return pricePerDay; }
        public void setPricePerDay(Double pricePerDay) { this.pricePerDay = pricePerDay; }

        public ImageData[] getImages() { return images; }
        public void setImages(ImageData[] images) { this.images = images; }

        public String getLicensePlate() { return licensePlate; }
        public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

        public String getFirstImageUrl() {
            if (images != null && images.length > 0 && images[0] != null) {
                return images[0].getUrl();
            }
            return null;
        }
    }

    // Constructors
    public Booking() {
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public String getRenter() {
        return renter;
    }

    public void setRenter(String renter) {
        this.renter = renter;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    // Helper methods
    public String getStatusText() {
        if (status == null) return "Không xác định";
        switch (status.toLowerCase()) {
            case "pending":
                return "Chờ xác nhận";
            case "approved":
                return "Đã xác nhận";
            case "rejected":
                return "Đã từ chối";
            case "completed":
                return "Hoàn thành";
            case "cancelled":
                return "Đã hủy";
            default:
                return status;
        }
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id='" + id + '\'' +
                ", vehicle=" + (vehicle != null ? vehicle.getName() : "null") +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                '}';
    }
}

