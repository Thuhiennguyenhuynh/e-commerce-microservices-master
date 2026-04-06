package com.rainbowforest.productcatalogservice.service;

import com.rainbowforest.productcatalogservice.entity.Product;
import com.rainbowforest.productcatalogservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final String URL_REGEX = "^(https?://[\\w\\-./?=&%#]+)$";

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getAllProductByCategory(String category) {
        return productRepository.findAllByCategory_CategoryName(category);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public List<Product> getAllProductsByName(String name) {
        return productRepository.findAllByProductNameContainingIgnoreCase(name);
    }

    @Override
    public Product addProduct(Product product) {
        validateProductImages(product);
        if (productRepository.existsBySku(product.getSku())) {
            throw new IllegalArgumentException("SKU đã tồn tại trong hệ thống");
        }
        normalizeMainImage(product);
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long productId, Product product) {
        return productRepository.findById(productId).map(existing -> {
            if (product.getSku() != null) {
                if (!product.getSku().equals(existing.getSku()) && productRepository.existsBySku(product.getSku())) {
                    throw new IllegalArgumentException("SKU đã tồn tại trong hệ thống");
                }
                existing.setSku(product.getSku());
            }
            if (product.getProductName() != null) {
                existing.setProductName(product.getProductName());
            }
            if (product.getPrice() != null) {
                existing.setPrice(product.getPrice());
            }
            if (product.getDiscription() != null) {
                existing.setDiscription(product.getDiscription());
            }
            if (product.getCategory() != null) {
                existing.setCategory(product.getCategory());
            }
            if (product.getImageUrls() != null) {
                existing.setImageUrls(product.getImageUrls());
            }
            if (product.getMainImageUrl() != null) {
                existing.setMainImageUrl(product.getMainImageUrl());
            }
            existing.setAvailability(product.getAvailability());
            validateProductImages(existing);
            normalizeMainImage(existing);
            return productRepository.save(existing);
        }).orElse(null);
    }

    @Override
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    private void validateProductImages(Product product) {
        if (product.getMainImageUrl() != null && !product.getMainImageUrl().isBlank()) {
            if (!product.getMainImageUrl().matches(URL_REGEX)) {
                throw new IllegalArgumentException("URL ảnh đại diện không hợp lệ");
            }
        }
        if (product.getImageUrls() != null) {
            for (String imageUrl : product.getImageUrls()) {
                if (imageUrl == null || imageUrl.isBlank() || !imageUrl.matches(URL_REGEX)) {
                    throw new IllegalArgumentException("URL ảnh không hợp lệ: " + imageUrl);
                }
            }
        }
    }

    private void normalizeMainImage(Product product) {
        if ((product.getMainImageUrl() == null || product.getMainImageUrl().isBlank())
                && product.getImageUrls() != null
                && !product.getImageUrls().isEmpty()) {
            product.setMainImageUrl(product.getImageUrls().get(0));
        }
    }
}
