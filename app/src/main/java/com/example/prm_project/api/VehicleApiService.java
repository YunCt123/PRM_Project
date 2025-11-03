package com.example.prm_project.api;

import com.example.prm_project.models.ApiResponse;
import com.example.prm_project.models.Vehicle;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface VehicleApiService {
    
    /**
     * Lấy danh sách tất cả vehicles
     * GET /api/vehicles
     * Response: { "success": true, "items": [...], "page": 1, "total": 10 }
     */
    @GET("api/vehicles")
    Call<ApiResponse<List<Vehicle>>> getAllVehicles();

    /**
     * Lấy danh sách vehicles với phân trang và filter
     * GET /api/vehicles
     * @param page Số trang (bắt đầu từ 1)
     * @param limit Số lượng items per page
     * @param status Trạng thái xe (available, rented, maintenance, etc.)
     * @param brand Hãng xe
     * @param minPrice Giá tối thiểu per day
     * @param maxPrice Giá tối đa per day
     */
    @GET("api/vehicles")
    Call<ApiResponse<List<Vehicle>>> getVehicles(
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("status") String status,
            @Query("brand") String brand,
            @Query("minPrice") Double minPrice,
            @Query("maxPrice") Double maxPrice
    );

    /**
     * Tìm kiếm vehicles
     * GET /api/vehicles/search
     * @param query Từ khóa tìm kiếm
     * @param status Trạng thái xe
     * @param brand Hãng xe
     * @param minPrice Giá tối thiểu
     * @param maxPrice Giá tối đa
     * @param year Năm sản xuất
     */
    @GET("api/vehicles/search")
    Call<ApiResponse<List<Vehicle>>> searchVehicles(
            @Query("query") String query,
            @Query("status") String status,
            @Query("brand") String brand,
            @Query("minPrice") Double minPrice,
            @Query("maxPrice") Double maxPrice,
            @Query("year") Integer year
    );

    /**
     * Lấy chi tiết 1 vehicle
     * GET /api/vehicles/{id}
     * @param id Vehicle ID
     */
    @GET("api/vehicles/{id}")
    Call<ApiResponse<Vehicle>> getVehicleById(@Path("id") String id);

    /**
     * Lấy vehicles available (đang có sẵn)
     * GET /api/vehicles?status=available
     */
    @GET("api/vehicles")
    Call<ApiResponse<List<Vehicle>>> getAvailableVehicles(
            @Query("status") String status,
            @Query("limit") Integer limit
    );
}
