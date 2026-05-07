package com.rainbowforest.productcatalogservice.service;

import com.rainbowforest.productcatalogservice.entity.Banner;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

public interface BannerService {
    List<Banner> getAllBanners();
    Optional<Banner> getBannerById(Long id);
    Banner createBanner(Banner banner, MultipartFile imageFile) throws IOException;
    Optional<Banner> updateBanner(Long id, Banner banner, MultipartFile imageFile) throws IOException;
    boolean deleteBanner(Long id);
}
