package com.rainbowforest.productcatalogservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rainbowforest.productcatalogservice.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByCategory_CategoryName(String categoryName);
    List<Product> findAllByProductNameContainingIgnoreCase(String name);
    boolean existsBySku(String sku);
    // BỔ SUNG THÊM DÒNG NÀY ĐỂ CHECK UNIQUE SLUG
    boolean existsBySlug(String slug);
}
