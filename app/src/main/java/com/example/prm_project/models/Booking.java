package com.example.prm_project.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Booking {
    @SerializedName("vehicleId")
    private String vehicleId;

    @SerializedName("startTime")
    private String startTime;

    @SerializedName("endTime")
    private String endTime;

    @SerializedName("deposit")
    private Deposit deposit;

    @SerializedName("qrCode")
    private String qrCode;

    @SerializedName("checkoutUrl")
    private String checkoutUrl;

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

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
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

    // Response classes for booking list API
    public static class BookingListResponse {
        @SerializedName("success")
        private boolean success;

        @SerializedName("page")
        private int page;

        @SerializedName("limit")
        private int limit;

        @SerializedName("total")
        private int total;

        @SerializedName("totalPages")
        private int totalPages;

        @SerializedName("items")
        private List<BookingItem> items;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public List<BookingItem> getItems() {
            return items;
        }

        public void setItems(List<BookingItem> items) {
            this.items = items;
        }
    }

    public static class BookingItem {
        @SerializedName("_id")
        private String id;

        @SerializedName("renter")
        private String renter;

        @SerializedName("vehicle")
        private Vehicle vehicle;

        @SerializedName("station")
        private Station station;

        @SerializedName("company")
        private String company;

        @SerializedName("startTime")
        private String startTime;

        @SerializedName("endTime")
        private String endTime;

        @SerializedName("timePolicy")
        private TimePolicy timePolicy;

        @SerializedName("status")
        private String status;

        @SerializedName("deposit")
        private BookingDeposit deposit;

        @SerializedName("holdExpiresAt")
        private String holdExpiresAt;

        @SerializedName("counterCheck")
        private CounterCheck counterCheck;

        @SerializedName("handoverPhotos")
        private HandoverPhotos handoverPhotos;

        @SerializedName("cancellationPolicySnapshot")
        private CancellationPolicySnapshot cancellationPolicySnapshot;

        @SerializedName("amounts")
        private Amounts amounts;

        @SerializedName("pricingSnapshot")
        private PricingSnapshot pricingSnapshot;

        @SerializedName("extensions")
        private List<Object> extensions;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("updatedAt")
        private String updatedAt;

        @SerializedName("__v")
        private int version;

        // Getters and setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRenter() {
            return renter;
        }

        public void setRenter(String renter) {
            this.renter = renter;
        }

        public Vehicle getVehicle() {
            return vehicle;
        }

        public void setVehicle(Vehicle vehicle) {
            this.vehicle = vehicle;
        }

        public Station getStation() {
            return station;
        }

        public void setStation(Station station) {
            this.station = station;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
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

        public TimePolicy getTimePolicy() {
            return timePolicy;
        }

        public void setTimePolicy(TimePolicy timePolicy) {
            this.timePolicy = timePolicy;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public BookingDeposit getDeposit() {
            return deposit;
        }

        public void setDeposit(BookingDeposit deposit) {
            this.deposit = deposit;
        }

        public String getHoldExpiresAt() {
            return holdExpiresAt;
        }

        public void setHoldExpiresAt(String holdExpiresAt) {
            this.holdExpiresAt = holdExpiresAt;
        }

        public CounterCheck getCounterCheck() {
            return counterCheck;
        }

        public void setCounterCheck(CounterCheck counterCheck) {
            this.counterCheck = counterCheck;
        }

        public HandoverPhotos getHandoverPhotos() {
            return handoverPhotos;
        }

        public void setHandoverPhotos(HandoverPhotos handoverPhotos) {
            this.handoverPhotos = handoverPhotos;
        }

        public CancellationPolicySnapshot getCancellationPolicySnapshot() {
            return cancellationPolicySnapshot;
        }

        public void setCancellationPolicySnapshot(CancellationPolicySnapshot cancellationPolicySnapshot) {
            this.cancellationPolicySnapshot = cancellationPolicySnapshot;
        }

        public Amounts getAmounts() {
            return amounts;
        }

        public void setAmounts(Amounts amounts) {
            this.amounts = amounts;
        }

        public PricingSnapshot getPricingSnapshot() {
            return pricingSnapshot;
        }

        public void setPricingSnapshot(PricingSnapshot pricingSnapshot) {
            this.pricingSnapshot = pricingSnapshot;
        }

        public List<Object> getExtensions() {
            return extensions;
        }

        public void setExtensions(List<Object> extensions) {
            this.extensions = extensions;
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

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }
    }

    public static class Vehicle {
        @SerializedName("_id")
        private String id;

        @SerializedName("plateNumber")
        private String plateNumber;

        @SerializedName("brand")
        private String brand;

        @SerializedName("model")
        private String model;

        @SerializedName("pricePerDay")
        private int pricePerDay;

        @SerializedName("pricePerHour")
        private int pricePerHour;

        @SerializedName("status")
        private String status;

        @SerializedName("defaultPhotos")
        private DefaultPhotos defaultPhotos;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPlateNumber() {
            return plateNumber;
        }

        public void setPlateNumber(String plateNumber) {
            this.plateNumber = plateNumber;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public int getPricePerDay() {
            return pricePerDay;
        }

        public void setPricePerDay(int pricePerDay) {
            this.pricePerDay = pricePerDay;
        }

        public int getPricePerHour() {
            return pricePerHour;
        }

        public void setPricePerHour(int pricePerHour) {
            this.pricePerHour = pricePerHour;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public DefaultPhotos getDefaultPhotos() {
            return defaultPhotos;
        }

        public void setDefaultPhotos(DefaultPhotos defaultPhotos) {
            this.defaultPhotos = defaultPhotos;
        }

        /**
         * Get main image URL from defaultPhotos exterior array
         */
        public String getMainImageUrl() {
            if (defaultPhotos != null && defaultPhotos.getExterior() != null && !defaultPhotos.getExterior().isEmpty()) {
                Photo firstPhoto = defaultPhotos.getExterior().get(0);
                // Return the URL from Photo object
                return firstPhoto != null ? firstPhoto.getUrl() : null;
            }
            return null;
        }

        public static class DefaultPhotos {
            @SerializedName("exterior")
            private List<Photo> exterior;

            @SerializedName("interior")
            private List<Photo> interior;

            public List<Photo> getExterior() {
                return exterior;
            }

            public void setExterior(List<Photo> exterior) {
                this.exterior = exterior;
            }

            public List<Photo> getInterior() {
                return interior;
            }

            public void setInterior(List<Photo> interior) {
                this.interior = interior;
            }
        }

        public static class Photo {
            @SerializedName("_id")
            private String id;

            @SerializedName("url")
            private String url;

            @SerializedName("type")
            private String type;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }

    public static class Station {
        @SerializedName("_id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("location")
        private Location location;

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

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public static class Location {
            @SerializedName("address")
            private String address;

            @SerializedName("lat")
            private double lat;

            @SerializedName("lng")
            private double lng;

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public double getLat() {
                return lat;
            }

            public void setLat(double lat) {
                this.lat = lat;
            }

            public double getLng() {
                return lng;
            }

            public void setLng(double lng) {
                this.lng = lng;
            }
        }
    }

    public static class TimePolicy {
        @SerializedName("alignEndClockToStart")
        private boolean alignEndClockToStart;

        @SerializedName("pricingMode")
        private String pricingMode;

        public boolean isAlignEndClockToStart() {
            return alignEndClockToStart;
        }

        public void setAlignEndClockToStart(boolean alignEndClockToStart) {
            this.alignEndClockToStart = alignEndClockToStart;
        }

        public String getPricingMode() {
            return pricingMode;
        }

        public void setPricingMode(String pricingMode) {
            this.pricingMode = pricingMode;
        }
    }

    public static class BookingDeposit {
        @SerializedName("amount")
        private int amount;

        @SerializedName("currency")
        private String currency;

        @SerializedName("provider")
        private String provider;

        @SerializedName("providerRef")
        private String providerRef;

        @SerializedName("status")
        private String status;

        @SerializedName("payos")
        private Payos payos;

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
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

        public Payos getPayos() {
            return payos;
        }

        public void setPayos(Payos payos) {
            this.payos = payos;
        }

        // Convenience method to get checkout URL
        public String getCheckoutUrl() {
            return payos != null ? payos.getCheckoutUrl() : null;
        }

        public static class Payos {
            @SerializedName("orderCode")
            private long orderCode;

            @SerializedName("paymentLinkId")
            private String paymentLinkId;

            @SerializedName("checkoutUrl")
            private String checkoutUrl;

            @SerializedName("qrCode")
            private String qrCode;

            @SerializedName("amountCaptured")
            private int amountCaptured;

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

            public int getAmountCaptured() {
                return amountCaptured;
            }

            public void setAmountCaptured(int amountCaptured) {
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
    }

    public static class CounterCheck {
        @SerializedName("licenseSnapshot")
        private List<Object> licenseSnapshot;

        @SerializedName("contractPhotos")
        private List<Object> contractPhotos;

        public List<Object> getLicenseSnapshot() {
            return licenseSnapshot;
        }

        public void setLicenseSnapshot(List<Object> licenseSnapshot) {
            this.licenseSnapshot = licenseSnapshot;
        }

        public List<Object> getContractPhotos() {
            return contractPhotos;
        }

        public void setContractPhotos(List<Object> contractPhotos) {
            this.contractPhotos = contractPhotos;
        }
    }

    public static class HandoverPhotos {
        @SerializedName("exteriorBefore")
        private List<Object> exteriorBefore;

        @SerializedName("interiorBefore")
        private List<Object> interiorBefore;

        @SerializedName("exteriorAfter")
        private List<Object> exteriorAfter;

        @SerializedName("interiorAfter")
        private List<Object> interiorAfter;

        public List<Object> getExteriorBefore() {
            return exteriorBefore;
        }

        public void setExteriorBefore(List<Object> exteriorBefore) {
            this.exteriorBefore = exteriorBefore;
        }

        public List<Object> getInteriorBefore() {
            return interiorBefore;
        }

        public void setInteriorBefore(List<Object> interiorBefore) {
            this.interiorBefore = interiorBefore;
        }

        public List<Object> getExteriorAfter() {
            return exteriorAfter;
        }

        public void setExteriorAfter(List<Object> exteriorAfter) {
            this.exteriorAfter = exteriorAfter;
        }

        public List<Object> getInteriorAfter() {
            return interiorAfter;
        }

        public void setInteriorAfter(List<Object> interiorAfter) {
            this.interiorAfter = interiorAfter;
        }
    }

    public static class CancellationPolicySnapshot {
        @SerializedName("windows")
        private List<Object> windows;

        @SerializedName("specialCases")
        private List<Object> specialCases;

        public List<Object> getWindows() {
            return windows;
        }

        public void setWindows(List<Object> windows) {
            this.windows = windows;
        }

        public List<Object> getSpecialCases() {
            return specialCases;
        }

        public void setSpecialCases(List<Object> specialCases) {
            this.specialCases = specialCases;
        }
    }

    public static class Amounts {
        @SerializedName("rentalEstimated")
        private int rentalEstimated;

        @SerializedName("overKmFee")
        private int overKmFee;

        @SerializedName("lateFee")
        private int lateFee;

        @SerializedName("batteryFee")
        private int batteryFee;

        @SerializedName("damageCharge")
        private int damageCharge;

        @SerializedName("discounts")
        private int discounts;

        @SerializedName("subtotal")
        private int subtotal;

        @SerializedName("tax")
        private int tax;

        @SerializedName("grandTotal")
        private int grandTotal;

        @SerializedName("totalPaid")
        private int totalPaid;

        public int getOverKmFee() {
            return overKmFee;
        }

        public void setOverKmFee(int overKmFee) {
            this.overKmFee = overKmFee;
        }

        public int getLateFee() {
            return lateFee;
        }

        public void setLateFee(int lateFee) {
            this.lateFee = lateFee;
        }

        public int getBatteryFee() {
            return batteryFee;
        }

        public void setBatteryFee(int batteryFee) {
            this.batteryFee = batteryFee;
        }

        public int getDamageCharge() {
            return damageCharge;
        }

        public void setDamageCharge(int damageCharge) {
            this.damageCharge = damageCharge;
        }

        public int getDiscounts() {
            return discounts;
        }

        public void setDiscounts(int discounts) {
            this.discounts = discounts;
        }

        public int getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(int subtotal) {
            this.subtotal = subtotal;
        }

        public int getTax() {
            return tax;
        }

        public void setTax(int tax) {
            this.tax = tax;
        }

        public int getGrandTotal() {
            return grandTotal;
        }

        public void setGrandTotal(int grandTotal) {
            this.grandTotal = grandTotal;
        }

        public int getRentalEstimated() {
            return rentalEstimated;
        }

        public void setRentalEstimated(int rentalEstimated) {
            this.rentalEstimated = rentalEstimated;
        }

        public int getTotalPaid() {
            return totalPaid;
        }

        public void setTotalPaid(int totalPaid) {
            this.totalPaid = totalPaid;
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
        private int unitPriceDay;

        @SerializedName("unitPriceHour")
        private int unitPriceHour;

        @SerializedName("baseUnit")
        private String baseUnit;

        @SerializedName("basePrice")
        private int basePrice;

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

        public int getUnitPriceDay() {
            return unitPriceDay;
        }

        public void setUnitPriceDay(int unitPriceDay) {
            this.unitPriceDay = unitPriceDay;
        }

        public int getUnitPriceHour() {
            return unitPriceHour;
        }

        public void setUnitPriceHour(int unitPriceHour) {
            this.unitPriceHour = unitPriceHour;
        }

        public String getBaseUnit() {
            return baseUnit;
        }

        public void setBaseUnit(String baseUnit) {
            this.baseUnit = baseUnit;
        }

        public int getBasePrice() {
            return basePrice;
        }

        public void setBasePrice(int basePrice) {
            this.basePrice = basePrice;
        }
    }
}
