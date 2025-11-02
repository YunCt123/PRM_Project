package com.example.prm_project.ui.booking;

public class Booking {
    private String bookingId;
    private String vehicleName;
    private String vehicleImageUrl;
    private String pickupDate;
    private String pickupTime;
    private String returnDate;
    private String returnTime;
    private String totalPrice;
    private String statusText;
    private String status; // ACTIVE, COMPLETED, CANCELLED

    public Booking(String bookingId, String vehicleName, String vehicleImageUrl,
                   String pickupDate, String pickupTime, String returnDate,
                   String returnTime, String totalPrice, String statusText, String status) {
        this.bookingId = bookingId;
        this.vehicleName = vehicleName;
        this.vehicleImageUrl = vehicleImageUrl;
        this.pickupDate = pickupDate;
        this.pickupTime = pickupTime;
        this.returnDate = returnDate;
        this.returnTime = returnTime;
        this.totalPrice = totalPrice;
        this.statusText = statusText;
        this.status = status;
    }

    // Getters
    public String getBookingId() {
        return bookingId;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public String getVehicleImageUrl() {
        return vehicleImageUrl;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public String getStatusText() {
        return statusText;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public void setVehicleImageUrl(String vehicleImageUrl) {
        this.vehicleImageUrl = vehicleImageUrl;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
