package com.example.prm_project.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.prm_project.models.ImageObject;
import com.example.prm_project.models.ImageObjectDeserializer;
import com.example.prm_project.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Session Manager để lưu trữ và quản lý thông tin user session
 */
public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PHONE = "userPhone";
    private static final String KEY_USER_DATE_OF_BIRTH = "userDateOfBirth";
    private static final String KEY_USER_GENDER = "userGender";
    private static final String KEY_USER_ROLE = "userRole";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_IS_VERIFIED = "isVerified";
    private static final String KEY_KYC_STATUS = "kycStatus";
    private static final String KEY_USER_JSON = "userJson"; // Store full User object as JSON
    private static final String KEY_PENDING_BOOKING_ID = "pendingBookingId";
    private static final String KEY_PENDING_VEHICLE_ID = "pendingVehicleId";
    private static final String KEY_PENDING_VEHICLE_NAME = "pendingVehicleName";
    private static final String KEY_PENDING_START_TIME = "pendingStartTime";
    private static final String KEY_PENDING_END_TIME = "pendingEndTime";
    private static final String KEY_PENDING_AMOUNT = "pendingAmount";
    private static final String KEY_PENDING_DEPOSIT = "pendingDeposit";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;
    private Gson gson;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        // Use same Gson configuration as ApiClient with custom deserializer
        gson = new GsonBuilder()
                .registerTypeAdapter(ImageObject.class, new ImageObjectDeserializer())
                .create();
    }

    /**
     * Lưu thông tin user sau khi login/register thành công
     */
    public void createLoginSession(User user, String token) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_PHONE, user.getPhone());
        editor.putString(KEY_USER_DATE_OF_BIRTH, user.getDateOfBirth());
        editor.putString(KEY_USER_GENDER, user.getGender());
        editor.putString(KEY_USER_ROLE, user.getRole());
        editor.putString(KEY_TOKEN, token);

        // Save verification info - now correctly using "verified" field from backend
        editor.putBoolean(KEY_IS_VERIFIED, user.getVerified());

        // Save KYC status based on verification logic
        String kycStatus = user.getVerificationStatus();
        editor.putString(KEY_KYC_STATUS, kycStatus);

        // Save full User object as JSON to preserve KYC data
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER_JSON, userJson);

        editor.apply();
    }

    /**
     * Kiểm tra user đã login chưa
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Lấy thông tin user
     */
    public User getUserDetails() {
        if (!isLoggedIn()) {
            return null;
        }

        // Try to get full User object from JSON first (includes KYC data)
        String userJson = prefs.getString(KEY_USER_JSON, null);
        if (userJson != null && !userJson.isEmpty()) {
            try {
                return gson.fromJson(userJson, User.class);
            } catch (Exception e) {
                android.util.Log.e("SessionManager", "Error parsing user JSON", e);
            }
        }

        // Fallback to old method if JSON not available
        User user = new User();
        user.setId(prefs.getString(KEY_USER_ID, ""));
        user.setName(prefs.getString(KEY_USER_NAME, ""));
        user.setEmail(prefs.getString(KEY_USER_EMAIL, ""));
        user.setPhone(prefs.getString(KEY_USER_PHONE, ""));
        user.setDateOfBirth(prefs.getString(KEY_USER_DATE_OF_BIRTH, ""));
        user.setGender(prefs.getString(KEY_USER_GENDER, ""));
        user.setRole(prefs.getString(KEY_USER_ROLE, ""));

        // restore verification info
        boolean isVerified = prefs.getBoolean(KEY_IS_VERIFIED, false);
        user.setVerified(isVerified);

        return user;
    }

    /**
     * Lấy token
     */
    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    /**
     * Lấy user ID
     */
    public String getUserId() {
        return prefs.getString(KEY_USER_ID, "");
    }

    /**
     * Lấy user name
     */
    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "");
    }

    /**
     * Lấy user email
     */
    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    /**
     * Get whether user has been verified
     */
    public boolean isVerified() {
        return prefs.getBoolean(KEY_IS_VERIFIED, false);
    }

    /**
     * Get KYC status string (e.g., "pending", "verified")
     */
    public String getKycStatus() {
        return prefs.getString(KEY_KYC_STATUS, "unverified");
    }

    /**
     * Save pending booking data (before PayOS payment)
     */
    public void saveBookingData(String bookingId, String vehicleId, String vehicleName,
                                String startTime, String endTime, double amount, double deposit) {
        editor.putString(KEY_PENDING_BOOKING_ID, bookingId);
        editor.putString(KEY_PENDING_VEHICLE_ID, vehicleId);
        editor.putString(KEY_PENDING_VEHICLE_NAME, vehicleName);
        editor.putString(KEY_PENDING_START_TIME, startTime);
        editor.putString(KEY_PENDING_END_TIME, endTime);
        editor.putFloat(KEY_PENDING_AMOUNT, (float) amount);
        editor.putFloat(KEY_PENDING_DEPOSIT, (float) deposit);
        editor.apply();
    }

    /**
     * Get pending booking ID
     */
    public String getPendingBookingId() {
        return prefs.getString(KEY_PENDING_BOOKING_ID, null);
    }

    /**
     * Get pending vehicle ID
     */
    public String getPendingVehicleId() {
        return prefs.getString(KEY_PENDING_VEHICLE_ID, null);
    }

    /**
     * Get pending vehicle name
     */
    public String getPendingVehicleName() {
        return prefs.getString(KEY_PENDING_VEHICLE_NAME, null);
    }

    /**
     * Get pending start time
     */
    public String getPendingStartTime() {
        return prefs.getString(KEY_PENDING_START_TIME, null);
    }

    /**
     * Get pending end time
     */
    public String getPendingEndTime() {
        return prefs.getString(KEY_PENDING_END_TIME, null);
    }

    /**
     * Get pending amount
     */
    public double getPendingAmount() {
        return prefs.getFloat(KEY_PENDING_AMOUNT, 0);
    }

    /**
     * Get pending deposit
     */
    public double getPendingDeposit() {
        return prefs.getFloat(KEY_PENDING_DEPOSIT, 0);
    }

    /**
     * Clear pending booking data after payment completed
     */
    public void clearPendingBooking() {
        editor.remove(KEY_PENDING_BOOKING_ID);
        editor.remove(KEY_PENDING_VEHICLE_ID);
        editor.remove(KEY_PENDING_VEHICLE_NAME);
        editor.remove(KEY_PENDING_START_TIME);
        editor.remove(KEY_PENDING_END_TIME);
        editor.remove(KEY_PENDING_AMOUNT);
        editor.remove(KEY_PENDING_DEPOSIT);
        editor.apply();
    }

    /**
     * Logout user - xóa tất cả session data
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
