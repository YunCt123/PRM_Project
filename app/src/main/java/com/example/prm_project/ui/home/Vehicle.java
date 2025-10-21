package com.example.prm_project.ui.home;

public class Vehicle {
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
    }

    // Getters
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

    // Setters
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
}
