package com.rainbowforest.productcatalogservice.service;

import com.rainbowforest.productcatalogservice.entity.Banner;
import java.util.List;
import java.util.Optional;

public interface BannerService {
    List<Banner> getAllBanners();
    Optional<Banner> getBannerById(Long id);
    Banner createBanner(Banner banner);
    Optional<Banner> updateBanner(Long id, Banner banner);
    boolean deleteBanner(Long id);
}
