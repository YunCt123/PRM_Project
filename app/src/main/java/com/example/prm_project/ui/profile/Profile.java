package com.example.prm_project.ui.profile;

/**
 * Simple POJO representing a user profile. Used for dummy data and later real API mapping.
 */
public class Profile {
    private String name;
    private String email;
    private int ridesCount;
    private int favCount;
    private int reviewsCount;
    private String distanceKm; // formatted string like "156.8 km"
    private int avatarResId;
    private int coverResId;
    private String birthDate;
    private String phone;

    public Profile(String name, String email, int ridesCount, int favCount, int reviewsCount, String distanceKm, int avatarResId, int coverResId, String birthDate, String phone) {
        this.name = name;
        this.email = email;
        this.ridesCount = ridesCount;
        this.favCount = favCount;
        this.reviewsCount = reviewsCount;
        this.distanceKm = distanceKm;
        this.avatarResId = avatarResId;
        this.coverResId = coverResId;
        this.birthDate = birthDate;
        this.phone = phone;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getRidesCount() { return ridesCount; }
    public int getFavCount() { return favCount; }
    public int getReviewsCount() { return reviewsCount; }
    public String getDistanceKm() { return distanceKm; }
    public int getAvatarResId() { return avatarResId; }
    public int getCoverResId() { return coverResId; }
    public String getBirthDate() { return birthDate; }
    public String getPhone() { return phone; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setRidesCount(int ridesCount) { this.ridesCount = ridesCount; }
    public void setFavCount(int favCount) { this.favCount = favCount; }
    public void setReviewsCount(int reviewsCount) { this.reviewsCount = reviewsCount; }
    public void setDistanceKm(String distanceKm) { this.distanceKm = distanceKm; }
    public void setAvatarResId(int avatarResId) { this.avatarResId = avatarResId; }
    public void setCoverResId(int coverResId) { this.coverResId = coverResId; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
    public void setPhone(String phone) { this.phone = phone; }
}
