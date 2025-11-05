package com.example.prm_project.models;

import com.google.gson.annotations.SerializedName;

public class BookingRequest {
    @SerializedName("vehicleId")
    private String vehicleId;

    @SerializedName("startTime")
    private String startTime;

    @SerializedName("endTime")
    private String endTime;

    @SerializedName("deposit")
    private Deposit deposit;

    public BookingRequest(String vehicleId, String startTime, String endTime, String provider) {
        this.vehicleId = vehicleId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.deposit = new Deposit(provider);
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Deposit getDeposit() {
        return deposit;
    }

    public void setDeposit(Deposit deposit) {
        this.deposit = deposit;
    }

    public static class Deposit {
        @SerializedName("provider")
        private String provider;

        public Deposit(String provider) {
            this.provider = provider;
        }

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }
    }
}
