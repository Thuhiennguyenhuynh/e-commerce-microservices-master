package com.rainbowforest.productcatalogservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.rainbowforest.productcatalogservice.entity.Banner;
import com.rainbowforest.productcatalogservice.repository.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public List<Banner> getAllBanners() {
        return bannerRepository.findAll();
    }

    @Override
    public Optional<Banner> getBannerById(Long id) {
        return bannerRepository.findById(id);
    }

    @Override
    public Banner createBanner(Banner banner, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            var uploaded = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.asMap("folder", "banners"));
            banner.setImageUrl(uploaded.get("secure_url").toString());
        }
        return bannerRepository.save(banner);
    }

    @Override
    public Optional<Banner> updateBanner(Long id, Banner banner, MultipartFile imageFile) throws IOException {
        return bannerRepository.findById(id).map(existingBanner -> {
            existingBanner.setName(banner.getName());
            existingBanner.setLinkUrl(banner.getLinkUrl());
            existingBanner.setActive(banner.isActive());
            existingBanner.setPosition(banner.getPosition());
            
            try {
                if (imageFile != null && !imageFile.isEmpty()) {
                    var uploaded = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.asMap("folder", "banners"));
                    existingBanner.setImageUrl(uploaded.get("secure_url").toString());
                } else if (banner.getImageUrl() != null) {
                    existingBanner.setImageUrl(banner.getImageUrl());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bannerRepository.save(existingBanner);
        });
    }

    @Override
    public boolean deleteBanner(Long id) {
        if (bannerRepository.existsById(id)) {
            bannerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}