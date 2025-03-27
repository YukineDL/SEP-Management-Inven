package com.inventorymanagement.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Configuration
public class Base64Utils {
    @Value("${file.upload-dir}")
    private String filePaths;

    public String saveImage(String imageBase64, String code){
        try {
            byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
            Path path = Paths.get(filePaths + code + ".jpg");
            if (Files.exists(path)) {
                Files.delete(path);  // Delete the existing file
            }
            Files.write(path, imageBytes);
            return path.toString();
        } catch (IOException e){
            return StringUtils.EMPTY;
        }
    }
    public byte[] decodeImage(String imagePath) throws IOException {
        if(imagePath != null){
            Path path = Paths.get(imagePath);
            return Files.readAllBytes(path);
        }
        return new byte[0];
    }
    public byte[] decodeBase64(String imageBase64){
        if(imageBase64 != null){
            return Base64.getDecoder().decode(imageBase64);
        }
        return null;
    }
}
