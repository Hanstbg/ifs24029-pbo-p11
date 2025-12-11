package org.delcom.app.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {
    
    @Value("${app.upload.dir:./uploads}")
    protected String uploadDir;

    // --- 1. Method Utama Upload (Yang sudah kita perbaiki) ---
    public String storeFile(MultipartFile file, UUID id) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = "img_" + id.toString() + "_" + System.currentTimeMillis() + fileExtension;

        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }

    // --- 2. Method Tambahan (YANG HILANG & BIKIN ERROR TEST) ---
    
    // Untuk menghapus file
    public boolean deleteFile(String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Untuk mengambil path file
    public Path loadFile(String filename) {
        return Paths.get(uploadDir).resolve(filename);
    }

    // Untuk cek apakah file ada
    public boolean fileExists(String filename) {
        Path path = loadFile(filename);
        return Files.exists(path);
    }
}