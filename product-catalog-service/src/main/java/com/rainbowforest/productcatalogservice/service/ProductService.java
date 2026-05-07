package com.rainbowforest.productcatalogservice.service;

import java.util.List;

import com.rainbowforest.productcatalogservice.entity.Product;

public interface ProductService {
    public List<Product> getAllProduct();
    List<Product> getAllProductByCategory(Long categoryId);
    public Product getProductById(Long id);
    public List<Product> getAllProductsByName(String name);
    public Product addProduct(Product product);
    public Product updateProduct(Long productId, Product product);
    public void deleteProduct(Long productId);
}
