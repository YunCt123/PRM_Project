package com.example.prm_project.ui.home;

public class Vehicle {
    private String id;        // Vehicle ID từ API
    private String name;
    private String details;
    private String batteryPercent;
    private String range;
    private String seats;
    private String location;
    private String price;
    private String priceDetails;
    private String status;
    private String rating;
    private String condition;
    private String imageUrl;  // URL hình ảnh từ API
    private String brand;     // Brand từ API để filter
    private double pricePerDay;   // Giá theo ngày (số)
    private double pricePerHour;  // Giá theo giờ (số)

    public Vehicle(String name, String details, String batteryPercent, String range,
                  String seats, String location, String price, String priceDetails,
                  String status, String rating, String condition) {
        this.name = name;
        this.details = details;
        this.batteryPercent = batteryPercent;
        this.range = range;
        this.seats = seats;
        this.location = location;
        this.price = price;
        this.priceDetails = priceDetails;
        this.status = status;
        this.rating = rating;
        this.condition = condition;
        this.imageUrl = null;
        this.brand = null;
    }
    
    // Constructor với imageUrl
    public Vehicle(String name, String details, String batteryPercent, String range,
                  String seats, String location, String price, String priceDetails,
                  String status, String rating, String condition, String imageUrl) {
        this.name = name;
        this.details = details;
        this.batteryPercent = batteryPercent;
        this.range = range;
        this.seats = seats;
        this.location = location;
        this.price = price;
        this.priceDetails = priceDetails;
        this.status = status;
        this.rating = rating;
        this.condition = condition;
        this.imageUrl = imageUrl;
        this.brand = null;
    }
    
    // Constructor với imageUrl và brand
    public Vehicle(String name, String details, String batteryPercent, String range,
                  String seats, String location, String price, String priceDetails,
                  String status, String rating, String condition, String imageUrl, String brand) {
        this.name = name;
        this.details = details;
        this.batteryPercent = batteryPercent;
        this.range = range;
        this.seats = seats;
        this.location = location;
        this.price = price;
        this.priceDetails = priceDetails;
        this.status = status;
        this.rating = rating;
        this.condition = condition;
        this.imageUrl = imageUrl;
        this.brand = brand;
        this.pricePerDay = 0;
        this.pricePerHour = 0;
    }
    
    // Constructor đầy đủ với ID và prices
    public Vehicle(String id, String name, String details, String batteryPercent, String range,
                  String seats, String location, String price, String priceDetails,
                  String status, String rating, String condition, String imageUrl, String brand,
                  double pricePerDay, double pricePerHour) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.batteryPercent = batteryPercent;
        this.range = range;
        this.seats = seats;
        this.location = location;
        this.price = price;
        this.priceDetails = priceDetails;
        this.status = status;
        this.rating = rating;
        this.condition = condition;
        this.imageUrl = imageUrl;
        this.brand = brand;
        this.pricePerDay = pricePerDay;
        this.pricePerHour = pricePerHour;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDetails() { return details; }
    public String getBatteryPercent() { return batteryPercent; }
    public String getRange() { return range; }
    public String getSeats() { return seats; }
    public String getLocation() { return location; }
    public String getPrice() { return price; }
    public String getPriceDetails() { return priceDetails; }
    public String getStatus() { return status; }
    public String getRating() { return rating; }
    public String getCondition() { return condition; }
    public String getImageUrl() { return imageUrl; }
    public String getBrand() { return brand; }
    public double getPricePerDay() { return pricePerDay; }
    public double getPricePerHour() { return pricePerHour; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDetails(String details) { this.details = details; }
    public void setBatteryPercent(String batteryPercent) { this.batteryPercent = batteryPercent; }
    public void setRange(String range) { this.range = range; }
    public void setSeats(String seats) { this.seats = seats; }
    public void setLocation(String location) { this.location = location; }
    public void setPrice(String price) { this.price = price; }
    public void setPriceDetails(String priceDetails) { this.priceDetails = priceDetails; }
    public void setStatus(String status) { this.status = status; }
    public void setRating(String rating) { this.rating = rating; }
    public void setCondition(String condition) { this.condition = condition; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setPricePerDay(double pricePerDay) { this.pricePerDay = pricePerDay; }
    public void setPricePerHour(double pricePerHour) { this.pricePerHour = pricePerHour; }
}
