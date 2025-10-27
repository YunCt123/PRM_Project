package com.example.prm_project.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> name;
    private final MutableLiveData<String> email;
    private final MutableLiveData<Integer> ridesCount;
    private final MutableLiveData<Integer> favCount;
    private final MutableLiveData<Integer> reviewsCount;
    private final MutableLiveData<String> distanceKm; // formatted
    private final MutableLiveData<Integer> avatarResId; // drawable id
    private final MutableLiveData<Integer> coverResId; // drawable id
    private final MutableLiveData<String> birthDate;
    private final MutableLiveData<String> phone;

    public ProfileViewModel() {
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

        // Load initial data
        reloadFromRepository();
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