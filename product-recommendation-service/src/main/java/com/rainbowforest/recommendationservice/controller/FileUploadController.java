package com.rainbowforest.recommendationservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class FileUploadController {

    private static final String UPLOAD_DIR = "uploads/images";

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestPart("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không có file tải lên"));
        }

        try {
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            System.out.println("Đang xử lý file: " + originalFilename); // Giữ log để theo dõi

            String extension = "png";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0) {
                extension = originalFilename.substring(dotIndex + 1);
            }
            String fileName = StringUtils.cleanPath(originalFilename.replaceAll("\\s+", "-")).toLowerCase();
            if (!fileName.contains(".")) {
                fileName = fileName + "." + extension;
            }

            // --- ĐOẠN ĐƯỢC SỬA ---
            // Lấy đường dẫn tuyệt đối ngay từ đầu để tránh lỗi trên Windows
            Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            Path destination = uploadPath.resolve(fileName).normalize();
            
            // So sánh chuẩn xác bằng đường dẫn tuyệt đối
            if (!destination.startsWith(uploadPath)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Tên file không hợp lệ do vi phạm bảo mật"));
            }
            // ----------------------

            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            Map<String, String> result = new HashMap<>();
            result.put("fileName", fileName);
            result.put("fileUrl", "/images/" + fileName);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
            
        } catch (IOException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Không thể lưu file"));
        }
    }
}
