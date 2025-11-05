package com.example.prm_project.utils;

import com.example.prm_project.models.Vehicle;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Helper class để convert Vehicle từ API sang UI Vehicle model
 */
public class VehicleConverter {
    
    /**
     * Convert API Vehicle sang UI Vehicle
     */
    public static com.example.prm_project.ui.home.Vehicle toUIVehicle(Vehicle apiVehicle) {
        if (apiVehicle == null) return null;

        // Format price to VND
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String pricePerDay = formatter.format(apiVehicle.getPricePerDay()) + " ₫";
        String pricePerHour = formatter.format(apiVehicle.getPricePerHour()) + " ₫";

        // Format details: Year • Color
        String details = apiVehicle.getYear() + " • " + translateColor(apiVehicle.getColor());
        
        // Get image URL từ API
        String imageUrl = apiVehicle.getMainImageUrl();
        
        // Calculate range from battery capacity (rough estimate: 1 kWh ≈ 5-6 km)
        // hoặc có thể API có field riêng cho range
        int estimatedRange = apiVehicle.getBatteryCapacity() * 5; // Ước tính

        // Tạo UI Vehicle với brand từ API và đầy đủ thông tin
        return new com.example.prm_project.ui.home.Vehicle(
                apiVehicle.getId(),                                 // id: vehicle ID
                apiVehicle.getFullName(),                           // name: "Xiaomi SU7"
                details,                                            // details: "2024 • Xanh"
                apiVehicle.getBatteryCapacity() + "%",              // batteryPercent: "90%"
                estimatedRange + " km",                             // range: "450 km"
                "5 chỗ",                                            // seats (default - có thể thêm field này vào API)
                apiVehicle.getStationName(),                        // location: "Bãi Sau Station"
                pricePerDay,                                        // price: "2.000 ₫"
                pricePerHour + "/giờ",                             // priceDetails: "1.000 ₫/giờ"
                translateStatus(apiVehicle.getStatus()),            // status: "Có sẵn"
                apiVehicle.getRatingText(),                         // rating: "Chưa có đánh giá"
                "Tốt",                                              // condition (default)
                imageUrl,                                           // imageUrl từ API
                apiVehicle.getBrand(),                              // brand từ API để filter
                apiVehicle.getPricePerDay(),                        // pricePerDay (số)
                apiVehicle.getPricePerHour()                        // pricePerHour (số)
        );
    }

    /**
     * Convert list API Vehicles sang UI Vehicles
     */
    public static List<com.example.prm_project.ui.home.Vehicle> toUIVehicles(List<Vehicle> apiVehicles) {
        List<com.example.prm_project.ui.home.Vehicle> uiVehicles = new ArrayList<>();
        if (apiVehicles != null) {
            for (Vehicle apiVehicle : apiVehicles) {
                uiVehicles.add(toUIVehicle(apiVehicle));
            }
        }
        return uiVehicles;
    }

    /**
     * Dịch status sang tiếng Việt
     */
    private static String translateStatus(String status) {
        if (status == null) return "Không rõ";
        
        switch (status.toLowerCase()) {
            case "available":
                return "Có sẵn";
            case "rented":
                return "Đã thuê";
            case "maintenance":
                return "Bảo trì";
            case "unavailable":
                return "Không khả dụng";
            default:
                return status;
        }
    }

    /**
     * Dịch color sang tiếng Việt
     */
    private static String translateColor(String color) {
        if (color == null) return "";
        
        switch (color.toLowerCase()) {
            case "green":
                return "Xanh lá";
            case "blue":
                return "Xanh dương";
            case "red":
                return "Đỏ";
            case "white":
                return "Trắng";
            case "black":
                return "Đen";
            case "gray":
            case "grey":
                return "Xám";
            case "silver":
                return "Bạc";
            case "yellow":
                return "Vàng";
            case "orange":
                return "Cam";
            case "brown":
                return "Nâu";
            case "purple":
                return "Tím";
            case "pink":
                return "Hồng";
            default:
                return color;
        }
    }

    /**
     * Get image URL từ API Vehicle
     */
    public static String getMainImageUrl(Vehicle apiVehicle) {
        if (apiVehicle == null) return null;
        return apiVehicle.getMainImageUrl();
    }

    /**
     * Get all exterior image URLs
     */
    public static List<String> getExteriorImageUrls(Vehicle apiVehicle) {
        List<String> urls = new ArrayList<>();
        if (apiVehicle != null && 
            apiVehicle.getDefaultPhotos() != null && 
            apiVehicle.getDefaultPhotos().getExterior() != null) {
            
            for (Vehicle.Photo photo : apiVehicle.getDefaultPhotos().getExterior()) {
                urls.add(photo.getUrl());
            }
        }
        return urls;
    }

    /**
     * Get all interior image URLs
     */
    public static List<String> getInteriorImageUrls(Vehicle apiVehicle) {
        List<String> urls = new ArrayList<>();
        if (apiVehicle != null && 
            apiVehicle.getDefaultPhotos() != null && 
            apiVehicle.getDefaultPhotos().getInterior() != null) {
            
            for (Vehicle.Photo photo : apiVehicle.getDefaultPhotos().getInterior()) {
                urls.add(photo.getUrl());
            }
        }
        return urls;
    }
}
