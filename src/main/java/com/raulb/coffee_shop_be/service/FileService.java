package com.raulb.coffee_shop_be.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileService {
    public static String saveFile(MultipartFile file, String targetDirectory) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Ensure the target directory exists
        File directory = new File(targetDirectory);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("Failed to create directory: " + targetDirectory);
            }
        }

        // Get original filename
        String originalFilename = file.getOriginalFilename();

        // Define the path where the file will be saved
        Path filePath = Paths.get(targetDirectory, originalFilename);

        // Write file to the specified location
        Files.write(filePath, file.getBytes());

        // Return the file path for reference
        return filePath.toString();
    }

    public static byte[] getBytesAsFile(String imageUrl) {
        File file = new File(imageUrl);
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
