package com.example.prm_project.ui.profile;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.prm_project.api.ApiClient;
import com.example.prm_project.api.UserApiService;
import com.example.prm_project.models.ApiResponse;
import com.example.prm_project.models.User;
import com.example.prm_project.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends AndroidViewModel {
    private static final String TAG = "ProfileViewModel";

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> name;
    private final MutableLiveData<String> email;
    private final MutableLiveData<Integer> ridesCount;
    private final MutableLiveData<Integer> favCount;
    private final MutableLiveData<Integer> reviewsCount;
    private final MutableLiveData<String> distanceKm;
    private final MutableLiveData<Integer> avatarResId;
    private final MutableLiveData<Integer> coverResId;
    private final MutableLiveData<String> birthDate;
    private final MutableLiveData<String> phone;

    // Loading and error states
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<String> errorMessage;

    private final UserApiService userApiService;
    private final SessionManager sessionManager;

    public ProfileViewModel(@NonNull Application application) {
        super(application);

        mText = new MutableLiveData<>();
        mText.setValue("This is profile fragment");

        // Initialize LiveData containers
        name = new MutableLiveData<>();
        email = new MutableLiveData<>();
        ridesCount = new MutableLiveData<>();
        favCount = new MutableLiveData<>();
        reviewsCount = new MutableLiveData<>();
        distanceKm = new MutableLiveData<>();
        avatarResId = new MutableLiveData<>();
        coverResId = new MutableLiveData<>();
        birthDate = new MutableLiveData<>();
        phone = new MutableLiveData<>();

        isLoading = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>();

        // Initialize API service and session manager
        userApiService = ApiClient.getClient().create(UserApiService.class);
        sessionManager = new SessionManager(application);

        // Load initial data from API
        loadUserProfile();
    }

    /**
     * Load user profile from API
     */
    public void loadUserProfile() {
        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) {
            Log.e(TAG, "No token found, user not logged in");
            errorMessage.setValue("Vui lòng đăng nhập lại");

            // Fallback to dummy data for testing
            reloadFromRepository();
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue(null);

        Call<ApiResponse<User>> call = userApiService.getUserProfile("Bearer " + token);
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        User user = apiResponse.getData();
                        Log.d(TAG, "Profile loaded successfully: " + user.getName());

                        // Update LiveData with API data
                        name.setValue(user.getName() != null ? user.getName() : "Unknown");
                        email.setValue(user.getEmail() != null ? user.getEmail() : "");
                        phone.setValue(user.getPhone() != null ? user.getPhone() : "");

                        // Update session with latest data
                        sessionManager.createLoginSession(user, token);

                        // TODO: Get these from API when available
                        ridesCount.setValue(0);
                        favCount.setValue(0);
                        reviewsCount.setValue(0);
                        distanceKm.setValue("0 km");
                    } else {
                        Log.e(TAG, "API returned success=false");
                        errorMessage.setValue("Không thể tải thông tin profile");
                        reloadFromRepository(); // Fallback to dummy
                    }
                } else {
                    Log.e(TAG, "API call failed with code: " + response.code());
                    errorMessage.setValue("Lỗi kết nối: " + response.code());
                    reloadFromRepository(); // Fallback to dummy
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "API call failed", t);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
                reloadFromRepository(); // Fallback to dummy
            }
        });
    }

    /**
     * Reloads profile data from the (dummy) repository. Call this to force-refresh
     * values so observers get updated (useful during development when replacing resources).
     */
    public void reloadFromRepository() {
        Profile p = DummyProfileRepository.getDummyProfile();

        name.setValue(p.getName());
        email.setValue(p.getEmail());
        ridesCount.setValue(p.getRidesCount());
        favCount.setValue(p.getFavCount());
        reviewsCount.setValue(p.getReviewsCount());
        distanceKm.setValue(p.getDistanceKm());
        avatarResId.setValue(p.getAvatarResId());
        coverResId.setValue(p.getCoverResId());
        birthDate.setValue(p.getBirthDate());
        phone.setValue(p.getPhone());
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<String> getName() { return name; }
    public LiveData<String> getEmail() { return email; }
    public LiveData<Integer> getRidesCount() { return ridesCount; }
    public LiveData<Integer> getFavCount() { return favCount; }
    public LiveData<Integer> getReviewsCount() { return reviewsCount; }
    public LiveData<String> getDistanceKm() { return distanceKm; }
    public LiveData<Integer> getAvatarResId() { return avatarResId; }
    public LiveData<Integer> getCoverResId() { return coverResId; }
    public LiveData<String> getBirthDate() { return birthDate; }
    public LiveData<String> getPhone() { return phone; }

    // Loading and error states
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    // Setters so other fragments can update profile info (kept in-memory for now)
    public void setName(String newName) { name.setValue(newName); }
    public void setEmail(String newEmail) { email.setValue(newEmail); }
    public void setRidesCount(int v) { ridesCount.setValue(v); }
    public void setDistanceKm(String d) { distanceKm.setValue(d); }
    public void setAvatarResId(int resId) { avatarResId.setValue(resId); }
    public void setCoverResId(int resId) { coverResId.setValue(resId); }
    public void setBirthDate(String value) { birthDate.setValue(value); }
    public void setPhone(String value) { phone.setValue(value); }
}