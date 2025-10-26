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

    public ProfileViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is profile fragment");

        // Sample/mock profile data
        name = new MutableLiveData<>();
        name.setValue("Nguyễn Văn A");

        email = new MutableLiveData<>();
        email.setValue("nguyenvana@example.com");

        ridesCount = new MutableLiveData<>();
        ridesCount.setValue(12);

        favCount = new MutableLiveData<>();
        favCount.setValue(3);

        reviewsCount = new MutableLiveData<>();
        reviewsCount.setValue(5);
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<String> getName() { return name; }
    public LiveData<String> getEmail() { return email; }
    public LiveData<Integer> getRidesCount() { return ridesCount; }
    public LiveData<Integer> getFavCount() { return favCount; }
    public LiveData<Integer> getReviewsCount() { return reviewsCount; }
}