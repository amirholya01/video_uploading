package com.amir.video_uploading.services;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class FileValidationService {
    public boolean isValidVideoFormat(String contentType){
        // List of supported video formats
        List<String> supportedFormats = Arrays.asList(
                "video/mp4",
                "video/quicktime",
                "video/x-msvideo", // AVI
                "video/x-flv"    // FLV
                // Add more formats here as needed
        );
        return contentType != null && supportedFormats.contains(contentType);
    }


    public String getFileExtension(String filename){
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
