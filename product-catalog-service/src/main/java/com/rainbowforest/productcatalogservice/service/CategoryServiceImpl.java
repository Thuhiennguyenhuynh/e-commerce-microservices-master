package com.rainbowforest.productcatalogservice.service;

import com.rainbowforest.productcatalogservice.entity.Category;
import com.rainbowforest.productcatalogservice.repository.CategoryRepository;
import com.rainbowforest.productcatalogservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public Category addCategory(Category category) {
        if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new RuntimeException("Tên danh mục đã tồn tại!");
        }
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, Category categoryRequest) {
        Category existingCategory = categoryRepository.findById(id).orElse(null);
        if (existingCategory != null) {
            existingCategory.setCategoryName(categoryRequest.getCategoryName());
            existingCategory.setDescription(categoryRequest.getDescription());
            return categoryRepository.save(existingCategory);
        }
        return null;
    }

    @Override
    public void deleteCategory(Long id) {
        if (productRepository.existsByCategory_Id(id)) {
            throw new RuntimeException("Không thể xóa danh mục vì đang có sản phẩm thuộc danh mục này!");
        }
        categoryRepository.deleteById(id);
    }
}