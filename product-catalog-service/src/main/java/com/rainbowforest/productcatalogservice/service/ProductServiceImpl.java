package com.rainbowforest.productcatalogservice.service;

import com.rainbowforest.productcatalogservice.entity.Product;
import com.rainbowforest.productcatalogservice.entity.ProductImage;
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
        
        // Kiểm tra Slug có bị trùng không
        if (product.getSlug() != null && productRepository.existsBySlug(product.getSlug())) {
            throw new IllegalArgumentException("Slug đã tồn tại trong hệ thống");
        }

        // Đảm bảo các ảnh phụ được gán đúng ID của Product trước khi lưu (quan hệ 2 chiều)
        if (product.getImages() != null) {
            for (ProductImage image : product.getImages()) {
                image.setProduct(product);
            }
        }

        normalizeMainImage(product);
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long productId, Product product) {
        return productRepository.findById(productId).map(existing -> {
            // Cập nhật SKU
            if (product.getSku() != null) {
                if (!product.getSku().equals(existing.getSku()) && productRepository.existsBySku(product.getSku())) {
                    throw new IllegalArgumentException("SKU đã tồn tại trong hệ thống");
                }
                existing.setSku(product.getSku());
            }

            // Cập nhật Slug
            if (product.getSlug() != null) {
                if (!product.getSlug().equals(existing.getSlug()) && productRepository.existsBySlug(product.getSlug())) {
                    throw new IllegalArgumentException("Slug đã tồn tại trong hệ thống");
                }
                existing.setSlug(product.getSlug());
            }

            // Cập nhật thông tin cơ bản
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

            // Cập nhật danh sách ảnh phụ
            if (product.getImages() != null) {
                existing.getImages().clear(); // Xóa các ảnh cũ (nhờ orphanRemoval = true, nó sẽ tự xóa trong CSDL)
                for (ProductImage image : product.getImages()) {
                    existing.addImage(image); // Sử dụng hàm addImage tiện ích đã khai báo bên Product.java
                }
            }

            if (product.getMainImageUrl() != null) {
                existing.setMainImageUrl(product.getMainImageUrl());
            }
            
            existing.setAvailability(product.getAvailability());

            // Validate và chuẩn hóa lại ảnh trước khi lưu
            validateProductImages(existing);
            normalizeMainImage(existing);
            
            return productRepository.save(existing);
        }).orElse(null);
    }

    @Override
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    // Đổi logic validate từ List<String> sang List<ProductImage>
    private void validateProductImages(Product product) {
        if (product.getMainImageUrl() != null && !product.getMainImageUrl().isBlank()) {
            if (!product.getMainImageUrl().matches(URL_REGEX)) {
                throw new IllegalArgumentException("URL ảnh đại diện không hợp lệ");
            }
        }
        if (product.getImages() != null) {
            for (ProductImage image : product.getImages()) {
                String imageUrl = image.getImageUrl();
                if (imageUrl == null || imageUrl.isBlank() || !imageUrl.matches(URL_REGEX)) {
                    throw new IllegalArgumentException("URL ảnh phụ không hợp lệ: " + imageUrl);
                }
            }
        }
    }

    // Đổi logic lấy phần tử List<String> thành lấy GetImageUrl từ phần tử đầu tiên trong List<ProductImage>
    private void normalizeMainImage(Product product) {
        if ((product.getMainImageUrl() == null || product.getMainImageUrl().isBlank())
                && product.getImages() != null
                && !product.getImages().isEmpty()) {
            product.setMainImageUrl(product.getImages().get(0).getImageUrl());
        }
    }
}