package com.example.prm_project.models;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BookingVehicleDefaultPhotosDeserializer implements JsonDeserializer<Booking.Vehicle.DefaultPhotos> {
    
    @Override
    public Booking.Vehicle.DefaultPhotos deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
            throws JsonParseException {
        
        Booking.Vehicle.DefaultPhotos defaultPhotos = new Booking.Vehicle.DefaultPhotos();
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
    
    private List<Booking.Vehicle.Photo> deserializePhotoList(JsonElement element, JsonDeserializationContext context) {
        List<Booking.Vehicle.Photo> photoList = new ArrayList<>();
        
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            
            for (JsonElement item : array) {
                if (item.isJsonPrimitive() && item.getAsJsonPrimitive().isString()) {
                    // Case 1: Array of strings (IDs from booking)
                    // Skip these as we can't get URLs from IDs alone
                    // Or create Photo with just ID if needed
                    Booking.Vehicle.Photo photo = new Booking.Vehicle.Photo();
                    photo.setId(item.getAsString());
                    photo.setUrl(null); // No URL available
                    photoList.add(photo);
                } else if (item.isJsonObject()) {
                    // Case 2: Array of objects (from vehicle detail)
                    Booking.Vehicle.Photo photo = context.deserialize(item, Booking.Vehicle.Photo.class);
                    photoList.add(photo);
                }
            }
        }
        
        return photoList;
    }
}
