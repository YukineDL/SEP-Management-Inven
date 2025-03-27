package com.inventorymanagement.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.cloudinary.utils.StringUtils;
import com.inventorymanagement.utils.Base64Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CloudinaryServices {

    private final Cloudinary cloudinary;
    private final Base64Utils base64Utils;
    // Inject Cloudinary credentials from application.properties
    public CloudinaryServices(@Value("${cloudinary.cloud-name}") String cloudName,
                              @Value("${cloudinary.api-key}") String apiKey,
                              @Value("${cloudinary.api-secret}") String apiSecret, Base64Utils base64Utils) {
        this.base64Utils = base64Utils;
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

    // Method to upload an image
    public String uploadImage(String imageBase64) throws IOException {
        // Cloudinary API upload options
        byte[] imageBytes = base64Utils.decodeBase64(imageBase64);
        if(imageBytes == null){
            return StringUtils.EMPTY;
        }
        var uploadResult = cloudinary.uploader().upload(imageBytes, ObjectUtils.emptyMap());
        return (String) uploadResult.get("url");  // Return the image URL
    }
}
