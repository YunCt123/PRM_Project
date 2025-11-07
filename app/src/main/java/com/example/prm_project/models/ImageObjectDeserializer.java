package com.example.prm_project.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Custom deserializer for ImageObject that handles both String URL and Object {_id, url} formats
 */
public class ImageObjectDeserializer implements JsonDeserializer<ImageObject> {
    
    @Override
    public ImageObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
            throws JsonParseException {
        
        if (json.isJsonNull()) {
            return null;
        }
        
        ImageObject imageObject = new ImageObject();
        
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            // Case 1: Backend returns plain URL string
            String url = json.getAsString();
            imageObject.setUrl(url);
            imageObject.setId(null); // No ID in this case
            
        } else if (json.isJsonObject()) {
            // Case 2: Backend returns object {_id: "...", url: "..."}
            String id = json.getAsJsonObject().has("_id") 
                    ? json.getAsJsonObject().get("_id").getAsString() 
                    : null;
            String url = json.getAsJsonObject().has("url") 
                    ? json.getAsJsonObject().get("url").getAsString() 
                    : null;
            
            imageObject.setId(id);
            imageObject.setUrl(url);
            
        } else {
            // Unexpected format, return null
            return null;
        }
        
        return imageObject;
    }
}
