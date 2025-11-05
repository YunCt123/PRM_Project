package com.example.prm_project.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Vehicle {
    @SerializedName("_id")
    private String id;

    @SerializedName("owner")
    private String owner;

    @SerializedName("company")
    private String company;

    @SerializedName("valuation")
    private Valuation valuation;

    @SerializedName("plateNumber")
    private String plateNumber;

    @SerializedName("brand")
    private String brand;

    @SerializedName("model")
    private String model;

    @SerializedName("year")
    private int year;

    @SerializedName("color")
    private String color;

    @SerializedName("batteryCapacity")
    private int batteryCapacity;

    @SerializedName("mileage")
    private int mileage;

    @SerializedName("pricePerDay")
    private double pricePerDay;

    @SerializedName("pricePerHour")
    private double pricePerHour;

    @SerializedName("status")
    private String status;

    @SerializedName("station")
    private Station station;

    @SerializedName("defaultPhotos")
    private DefaultPhotos defaultPhotos;

    @SerializedName("ratingAvg")
    private double ratingAvg;

    @SerializedName("ratingCount")
    private int ratingCount;

    @SerializedName("tags")
    private List<String> tags;

    @SerializedName("maintenanceHistory")
    private List<Object> maintenanceHistory;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public Valuation getValuation() { return valuation; }
    public void setValuation(Valuation valuation) { this.valuation = valuation; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public int getBatteryCapacity() { return batteryCapacity; }
    public void setBatteryCapacity(int batteryCapacity) { this.batteryCapacity = batteryCapacity; }

    public int getMileage() { return mileage; }
    public void setMileage(int mileage) { this.mileage = mileage; }

    public double getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(double pricePerDay) { this.pricePerDay = pricePerDay; }

    public double getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(double pricePerHour) { this.pricePerHour = pricePerHour; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Station getStation() { return station; }
    public void setStation(Station station) { this.station = station; }

    public DefaultPhotos getDefaultPhotos() { return defaultPhotos; }
    public void setDefaultPhotos(DefaultPhotos defaultPhotos) { this.defaultPhotos = defaultPhotos; }

    public double getRatingAvg() { return ratingAvg; }
    public void setRatingAvg(double ratingAvg) { this.ratingAvg = ratingAvg; }

    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public List<Object> getMaintenanceHistory() { return maintenanceHistory; }
    public void setMaintenanceHistory(List<Object> maintenanceHistory) { this.maintenanceHistory = maintenanceHistory; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public String getFullName() {
        return brand + " " + model;
    }

    public String getYearAndColor() {
        return year + " • " + color;
    }

    public String getMainImageUrl() {
        if (defaultPhotos != null && defaultPhotos.getExterior() != null && !defaultPhotos.getExterior().isEmpty()) {
            return defaultPhotos.getExterior().get(0).getUrl();
        }
        return null;
    }

    public String getStationName() {
        return station != null ? station.getName() : "Unknown";
    }

    public String getStationAddress() {
        return station != null && station.getLocation() != null ? station.getLocation().getAddress() : "Unknown";
    }

    public String getRatingText() {
        if (ratingCount == 0) {
            return "Chưa có đánh giá";
        }
        return String.format("%.1f ⭐ (%d)", ratingAvg, ratingCount);
    }

    // Nested classes
    public static class Valuation {
        @SerializedName("valueVND")
        private double valueVND;

        @SerializedName("lastUpdatedAt")
        private String lastUpdatedAt;

        public double getValueVND() { return valueVND; }
        public void setValueVND(double valueVND) { this.valueVND = valueVND; }

        public String getLastUpdatedAt() { return lastUpdatedAt; }
        public void setLastUpdatedAt(String lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }
    }

    public static class Station {
        @SerializedName("_id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("code")
        private String code;

        @SerializedName("location")
        private Location location;

        @SerializedName("isActive")
        private boolean isActive;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public Location getLocation() { return location; }
        public void setLocation(Location location) { this.location = location; }

        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
    }

    public static class Location {
        @SerializedName("address")
        private String address;

        @SerializedName("lat")
        private double lat;

        @SerializedName("lng")
        private double lng;

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public double getLat() { return lat; }
        public void setLat(double lat) { this.lat = lat; }

        public double getLng() { return lng; }
        public void setLng(double lng) { this.lng = lng; }
    }

    public static class DefaultPhotos {
        @SerializedName("exterior")
        private List<Photo> exterior;

        @SerializedName("interior")
        private List<Photo> interior;

        public List<Photo> getExterior() { return exterior; }
        public void setExterior(List<Photo> exterior) { this.exterior = exterior; }

        public List<Photo> getInterior() { return interior; }
        public void setInterior(List<Photo> interior) { this.interior = interior; }
    }

    public static class Photo {
        @SerializedName("_id")
        private String id;

        @SerializedName("url")
        private String url;

        @SerializedName("type")
        private String type;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
}
