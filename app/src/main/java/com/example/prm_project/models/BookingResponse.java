package com.example.prm_project.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BookingResponse {
    @SerializedName("renter")
    private Renter renter;

    @SerializedName("bookingId")
    private String bookingId;

    @SerializedName("status")
    private String status;

    @SerializedName("holdExpiresAt")
    private String holdExpiresAt;

    @SerializedName("deposit")
    private Deposit deposit;

    @SerializedName("pricingSnapshot")
    private PricingSnapshot pricingSnapshot;

    @SerializedName("amountEstimated")
    private double amountEstimated;

    @SerializedName("checkoutUrl")
    private String checkoutUrl;

    @SerializedName("qrCode")
    private String qrCode;

    // Getters and Setters
    public Renter getRenter() {
        return renter;
    }

    public void setRenter(Renter renter) {
        this.renter = renter;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHoldExpiresAt() {
        return holdExpiresAt;
    }

    public void setHoldExpiresAt(String holdExpiresAt) {
        this.holdExpiresAt = holdExpiresAt;
    }

    public Deposit getDeposit() {
        return deposit;
    }

    public void setDeposit(Deposit deposit) {
        this.deposit = deposit;
    }

    public PricingSnapshot getPricingSnapshot() {
        return pricingSnapshot;
    }

    public void setPricingSnapshot(PricingSnapshot pricingSnapshot) {
        this.pricingSnapshot = pricingSnapshot;
    }

    public double getAmountEstimated() {
        return amountEstimated;
    }

    public void setAmountEstimated(double amountEstimated) {
        this.amountEstimated = amountEstimated;
    }

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    // Nested classes
    public static class Renter {
        @SerializedName("_id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("email")
        private String email;

        @SerializedName("phone")
        private String phone;

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
    }

    public static class Deposit {
        @SerializedName("amount")
        private double amount;

        @SerializedName("currency")
        private String currency;

        @SerializedName("provider")
        private String provider;

        @SerializedName("providerRef")
        private String providerRef;

        @SerializedName("status")
        private String status;

        @SerializedName("payos")
        private PayOS payos;

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public PayOS getPayos() {
            return payos;
        }

        public void setPayos(PayOS payos) {
            this.payos = payos;
        }
    }

    public static class PayOS {
        @SerializedName("orderCode")
        private long orderCode;

        @SerializedName("paymentLinkId")
        private String paymentLinkId;

        @SerializedName("checkoutUrl")
        private String checkoutUrl;

        @SerializedName("qrCode")
        private String qrCode;

        @SerializedName("amountCaptured")
        private double amountCaptured;

        @SerializedName("needsRefundReview")
        private boolean needsRefundReview;

        @SerializedName("refundHistory")
        private List<Object> refundHistory;

        public long getOrderCode() {
            return orderCode;
        }

        public void setOrderCode(long orderCode) {
            this.orderCode = orderCode;
        }

        public String getPaymentLinkId() {
            return paymentLinkId;
        }

        public void setPaymentLinkId(String paymentLinkId) {
            this.paymentLinkId = paymentLinkId;
        }

        public String getCheckoutUrl() {
            return checkoutUrl;
        }

        public void setCheckoutUrl(String checkoutUrl) {
            this.checkoutUrl = checkoutUrl;
        }

        public String getQrCode() {
            return qrCode;
        }

        public void setQrCode(String qrCode) {
            this.qrCode = qrCode;
        }

        public double getAmountCaptured() {
            return amountCaptured;
        }

        public void setAmountCaptured(double amountCaptured) {
            this.amountCaptured = amountCaptured;
        }

        public boolean isNeedsRefundReview() {
            return needsRefundReview;
        }

        public void setNeedsRefundReview(boolean needsRefundReview) {
            this.needsRefundReview = needsRefundReview;
        }

        public List<Object> getRefundHistory() {
            return refundHistory;
        }

        public void setRefundHistory(List<Object> refundHistory) {
            this.refundHistory = refundHistory;
        }
    }

    public static class PricingSnapshot {
        @SerializedName("baseMode")
        private String baseMode;

        @SerializedName("days")
        private int days;

        @SerializedName("hours")
        private int hours;

        @SerializedName("unitPriceDay")
        private double unitPriceDay;

        @SerializedName("unitPriceHour")
        private double unitPriceHour;

        @SerializedName("baseUnit")
        private String baseUnit;

        @SerializedName("basePrice")
        private double basePrice;

        public String getBaseMode() {
            return baseMode;
        }

        public void setBaseMode(String baseMode) {
            this.baseMode = baseMode;
        }

        public int getDays() {
            return days;
        }

        public void setDays(int days) {
            this.days = days;
        }

        public int getHours() {
            return hours;
        }

        public void setHours(int hours) {
            this.hours = hours;
        }

        public double getUnitPriceDay() {
            return unitPriceDay;
        }

        public void setUnitPriceDay(double unitPriceDay) {
            this.unitPriceDay = unitPriceDay;
        }

        public double getUnitPriceHour() {
            return unitPriceHour;
        }

        public void setUnitPriceHour(double unitPriceHour) {
            this.unitPriceHour = unitPriceHour;
        }

        public String getBaseUnit() {
            return baseUnit;
        }

        public void setBaseUnit(String baseUnit) {
            this.baseUnit = baseUnit;
        }

        public double getBasePrice() {
            return basePrice;
        }

        public void setBasePrice(double basePrice) {
            this.basePrice = basePrice;
        }
    }
}
