package com.rainbowforest.productcatalogservice.service;

import com.rainbowforest.productcatalogservice.entity.Banner;
import com.rainbowforest.productcatalogservice.repository.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerRepository bannerRepository;

    @Override
    public List<Banner> getAllBanners() {
        return bannerRepository.findAll();
    }

    @Override
    public Optional<Banner> getBannerById(Long id) {
        return bannerRepository.findById(id);
    }

    @Override
    public Banner createBanner(Banner banner) {
        return bannerRepository.save(banner);
    }

    @Override
    public Optional<Banner> updateBanner(Long id, Banner banner) {
        return bannerRepository.findById(id).map(existingBanner -> {
            existingBanner.setName(banner.getName());
            existingBanner.setImageUrl(banner.getImageUrl());
            existingBanner.setLinkUrl(banner.getLinkUrl());
            existingBanner.setActive(banner.isActive());
            existingBanner.setPosition(banner.getPosition());
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
