package com.example.prm_project.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.prm_project.models.User;

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
    private static final String KEY_USER_GENDER = "userGender";
    private static final String KEY_USER_ROLE = "userRole";
    private static final String KEY_TOKEN = "token";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
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
        editor.putString(KEY_USER_GENDER, user.getGender());
        editor.putString(KEY_USER_ROLE, user.getRole());
        editor.putString(KEY_TOKEN, token);
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

        User user = new User();
        user.setId(prefs.getString(KEY_USER_ID, ""));
        user.setName(prefs.getString(KEY_USER_NAME, ""));
        user.setEmail(prefs.getString(KEY_USER_EMAIL, ""));
        user.setPhone(prefs.getString(KEY_USER_PHONE, ""));
        user.setGender(prefs.getString(KEY_USER_GENDER, ""));
        user.setRole(prefs.getString(KEY_USER_ROLE, ""));
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
     * Logout user - xóa tất cả session data
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
