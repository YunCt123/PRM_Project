package com.example.prm_project.models;

import com.google.gson.annotations.SerializedName;

public class Booking {
    @SerializedName("vehicleId")
    private String vehicleId;

    @SerializedName("startTime")
    private String startTime;

    @SerializedName("endTime")
    private String endTime;

    @SerializedName("deposit")
    private Deposit deposit;

    public Booking(String vehicleId, String startTime, String endTime, Deposit deposit) {
        this.vehicleId = vehicleId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.deposit = deposit;
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
        @SerializedName("amount")
        private long amount;

        @SerializedName("currency")
        private String currency;

        @SerializedName("provider")
        private String provider;

        @SerializedName("providerRef")
        private String providerRef;

        public Deposit(long amount, String currency, String provider, String providerRef) {
            this.amount = amount;
            this.currency = currency;
            this.provider = provider;
            this.providerRef = providerRef;
        }

        public long getAmount() {
            return amount;
        }

        public void setAmount(long amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getProviderRef() {
            return providerRef;
        }

        public void setProviderRef(String providerRef) {
            this.providerRef = providerRef;
        }
    }
}
