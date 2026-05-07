package com.rainbowforest.productcatalogservice.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class FileUploadController {

    @Autowired
    private Cloudinary cloudinary;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestPart("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không có file tải lên"));
        }

        try {
            // Lấy tên gốc của file
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            System.out.println("Đang upload file lên Cloudinary: " + originalFilename);

            // Upload thẳng lên Cloudinary vào thư mục "products"
            var uploaded = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder", "products"));
            
            // Lấy URL bảo mật trả về từ Cloudinary
            String fileUrl = uploaded.get("secure_url").toString();

            // Trả về JSON chứa URL của ảnh
            Map<String, String> result = new HashMap<>();
            result.put("fileName", originalFilename);
            result.put("fileUrl", fileUrl); // Frontend sẽ nhận URL thẳng từ Cloudinary (vd: https://res.cloudinary.com/...)
            
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
            
        } catch (IOException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Không thể lưu file lên Cloudinary: " + ex.getMessage()));
        }
    }
}