package com.example.prm_project.models;

import com.google.gson.annotations.SerializedName;

/**
 * Model for image objects returned by backend
 */
public class ImageObject {
    
    @SerializedName("_id")
    private String id;
    
    @SerializedName("url")
    private String url;
    
    public ImageObject() {
    }
    
    public ImageObject(String id, String url) {
        this.id = id;
        this.url = url;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    @Override
    public String toString() {
        return "ImageObject{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
