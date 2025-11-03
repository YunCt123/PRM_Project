package com.example.prm_project.models;

import com.google.gson.annotations.SerializedName;

/**
 * Generic API Response wrapper for all endpoints
 * @param <T> The type of data returned
 */
public class ApiResponse<T> {
    
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private T data;

    // Backend trả về "items" cho list responses
    @SerializedName("items")
    private T items;

    @SerializedName("error")
    private String error;

    @SerializedName("statusCode")
    private Integer statusCode;

    // Pagination fields
    @SerializedName("page")
    private Integer page;

    @SerializedName("limit")
    private Integer limit;

    @SerializedName("total")
    private Integer total;

    @SerializedName("totalPages")
    private Integer totalPages;

    // Constructors
    public ApiResponse() {
    }

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        // Nếu data null, thử lấy từ items (cho list responses)
        return data != null ? data : items;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getItems() {
        return items;
    }

    public void setItems(T items) {
        this.items = items;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    // Helper methods
    public boolean hasData() {
        return data != null || items != null;
    }

    public boolean hasPagination() {
        return page != null && limit != null && total != null;
    }

    public boolean hasError() {
        return error != null && !error.isEmpty();
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", error='" + error + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }
}
