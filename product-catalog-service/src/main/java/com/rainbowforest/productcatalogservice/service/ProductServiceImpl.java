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

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

   @Override
public List<Product> getAllProductByCategory(Long categoryId) {
    return productRepository.findAllByCategory_Id(categoryId);
}

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public List<Product> getAllProductsByName(String name) {
        return productRepository.findAllByProductName(name);
    }

    @Override
    public Product addProduct(Product product) {
        if (product.getImages() != null) {
            product.getImages().forEach(image -> image.setProduct(product));
        }
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long productId, Product product) {
        return productRepository.findById(productId).map(existing -> {
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
            if (product.getImageUrl() != null) {
                existing.setImageUrl(product.getImageUrl());
            }
            if (product.getImages() != null) {
                existing.getImages().clear();
                product.getImages().forEach(image -> {
                    image.setProduct(existing);
                    existing.getImages().add(image);
                });
            }
            existing.setAvailability(product.getAvailability());
            return productRepository.save(existing);
        }).orElse(null);
    }

    @Override
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }
}
