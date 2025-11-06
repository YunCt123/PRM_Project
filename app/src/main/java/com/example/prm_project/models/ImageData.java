package com.example.prm_project.models;

import com.google.gson.annotations.SerializedName;

/**
 * Model for image data returned from backend
 */
public class ImageData {

    @SerializedName("url")
    private String url;

    @SerializedName("publicId")
    private String publicId;

    @SerializedName("_id")
    private String id;

    public ImageData() {
    }

    public ImageData(String url, String publicId) {
        this.url = url;
        this.publicId = publicId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

