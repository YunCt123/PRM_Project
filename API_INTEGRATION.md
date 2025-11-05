# API Integration Guide

## Tổng quan
Project đã được tích hợp với backend API để lấy danh sách phương tiện từ server:
- Base URL: `https://be-ev-rental-system-production.up.railway.app/`
- API Documentation: https://be-ev-rental-system-production.up.railway.app/docs/#/

## Các file đã tạo/cập nhật

### 1. Models (`app/src/main/java/com/example/prm_project/models/`)
- **Vehicle.java**: Model chính cho vehicle với tất cả fields từ API
  - Bao gồm nested classes: Valuation, Station, Location, DefaultPhotos, Photo
  - Helper methods: `getFullName()`, `getMainImageUrl()`, `getRatingText()`, etc.

- **VehicleResponse.java**: Response wrapper cho API (nếu cần pagination)

### 2. API Service (`app/src/main/java/com/example/prm_project/api/`)
- **ApiClient.java**: Retrofit client configuration với logging
- **VehicleApiService.java**: Interface định nghĩa các API endpoints:
  - `getAllVehicles()`: Lấy tất cả vehicles
  - `getVehicles(...)`: Lấy vehicles với filter
  - `searchVehicles(...)`: Tìm kiếm vehicles
  - `getVehicleById(id)`: Lấy chi tiết 1 vehicle
  - `getAvailableVehicles(...)`: Lấy vehicles có status "available"

### 3. Repository (`app/src/main/java/com/example/prm_project/repository/`)
- **VehicleRepository.java**: Quản lý tất cả API calls với callback interfaces
  - `getAllVehicles(callback)`
  - `getAvailableVehicles(limit, callback)`
  - `searchVehicles(query, status, brand, minPrice, maxPrice, year, callback)`
  - `getVehicleById(id, callback)`
  - `getVehiclesWithFilter(...)`

### 4. Utils (`app/src/main/java/com/example/prm_project/utils/`)
- **VehicleConverter.java**: Convert giữa API Vehicle và UI Vehicle
  - `toUIVehicle(apiVehicle)`: Convert 1 vehicle
  - `toUIVehicles(apiVehicles)`: Convert list vehicles
  - Helper methods cho images

### 5. UI Updates
- **HomeFragment.java**: Load vehicles từ API thay vì data mẫu
- **SearchViewModel.java**: Load và search vehicles từ API

## Cách sử dụng

### Load vehicles trong Fragment/Activity:

```java
// 1. Khởi tạo repository
VehicleRepository repository = new VehicleRepository();

// 2. Load tất cả vehicles có sẵn
repository.getAvailableVehicles(20, new VehicleRepository.VehicleCallback() {
    @Override
    public void onSuccess(List<Vehicle> apiVehicles) {
        // Convert sang UI vehicles
        List<com.example.prm_project.ui.home.Vehicle> uiVehicles = 
            VehicleConverter.toUIVehicles(apiVehicles);
        
        // Update UI trên main thread
        runOnUiThread(() -> {
            // Update adapter
            adapter.updateData(uiVehicles);
        });
    }

    @Override
    public void onError(String errorMessage) {
        runOnUiThread(() -> {
            Toast.makeText(context, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
        });
    }
});
```

### Search vehicles:

```java
repository.searchVehicles(
    "Xiaomi",           // query (search in brand, model)
    "available",        // status
    null,               // brand (null = all brands)
    null,               // minPrice
    null,               // maxPrice
    null,               // year
    new VehicleRepository.VehicleCallback() {
        @Override
        public void onSuccess(List<Vehicle> vehicles) {
            // Handle success
        }

        @Override
        public void onError(String errorMessage) {
            // Handle error
        }
    }
);
```

### Get vehicle by ID:

```java
repository.getVehicleById("6906c7940df825f0eb05473f", 
    new VehicleRepository.SingleVehicleCallback() {
        @Override
        public void onSuccess(Vehicle vehicle) {
            // Use vehicle data
            String name = vehicle.getFullName();
            String imageUrl = vehicle.getMainImageUrl();
            String station = vehicle.getStationName();
        }

        @Override
        public void onError(String errorMessage) {
            // Handle error
        }
    }
);
```

## Dependencies đã thêm

Trong `app/build.gradle.kts`:
```kotlin
// Retrofit for API calls
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// Gson for JSON parsing
implementation("com.google.code.gson:gson:2.10.1")

// OkHttp for logging
implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
```

## Permissions đã thêm

Trong `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## API Response Structure

Sample vehicle từ API:
```json
{
  "_id": "6906c7940df825f0eb05473f",
  "brand": "Xiaomi",
  "model": "SU7",
  "year": 2024,
  "color": "Green",
  "batteryCapacity": 90,
  "pricePerDay": 2000,
  "pricePerHour": 1000,
  "status": "available",
  "station": {
    "name": "Bãi Sau Station",
    "location": {
      "address": "...",
      "lat": 10.3342332,
      "lng": 107.0896751
    }
  },
  "defaultPhotos": {
    "exterior": [{
      "url": "https://res.cloudinary.com/...",
      "type": "image"
    }]
  },
  "ratingAvg": 0,
  "ratingCount": 0
}
```

## Testing

1. Build project: `./gradlew build`
2. Run app trên emulator/device
3. Check Logcat để xem API calls (tag: "VehicleRepository")
4. Nếu API fails, app sẽ fallback về sample data

## Troubleshooting

### Lỗi "Unable to resolve host"
- Kiểm tra internet connection
- Kiểm tra BASE_URL trong ApiClient.java

### Lỗi "Failed to load vehicles: 404"
- Kiểm tra API endpoint có đúng không
- Verify API documentation

### Lỗi "JSON parsing"
- Check response structure có match với Vehicle model không
- Check Logcat để xem raw response

## Next Steps

Có thể extend thêm:
1. Add image loading library (Glide/Picasso) để load vehicle images
2. Add caching với Room database
3. Add pull-to-refresh
4. Add pagination cho large lists
5. Add more filter options (price range, battery capacity, etc.)
