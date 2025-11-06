package com.example.prm_project.models;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DefaultPhotosDeserializer implements JsonDeserializer<Vehicle.DefaultPhotos> {
    
    @Override
    public Vehicle.DefaultPhotos deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
            throws JsonParseException {
        
        Vehicle.DefaultPhotos defaultPhotos = new Vehicle.DefaultPhotos();
        JsonObject jsonObject = json.getAsJsonObject();
        
        // Deserialize exterior
        if (jsonObject.has("exterior")) {
            defaultPhotos.setExterior(deserializePhotoList(jsonObject.get("exterior"), context));
        }
        
        // Deserialize interior
        if (jsonObject.has("interior")) {
            defaultPhotos.setInterior(deserializePhotoList(jsonObject.get("interior"), context));
        }
        
        return defaultPhotos;
    }
    
    private List<Vehicle.Photo> deserializePhotoList(JsonElement element, JsonDeserializationContext context) {
        List<Vehicle.Photo> photoList = new ArrayList<>();
        
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            
            for (JsonElement item : array) {
                if (item.isJsonPrimitive() && item.getAsJsonPrimitive().isString()) {
                    // Case 1: Array of strings (IDs from booking)
                    // Skip these as we can't get URLs from IDs alone
                    // Or create Photo with just ID if needed
                    Vehicle.Photo photo = new Vehicle.Photo();
                    photo.setId(item.getAsString());
                    photo.setUrl(null); // No URL available
                    photoList.add(photo);
                } else if (item.isJsonObject()) {
                    // Case 2: Array of objects (from vehicle detail)
                    Vehicle.Photo photo = context.deserialize(item, Vehicle.Photo.class);
                    photoList.add(photo);
                }
            }
        }
        
        return photoList;
    }
}
