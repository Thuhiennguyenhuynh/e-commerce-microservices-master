package com.rainbowforest.productcatalogservice.controller;

import com.rainbowforest.productcatalogservice.entity.Category;
import com.rainbowforest.productcatalogservice.http.header.HeaderGenerator;
import com.rainbowforest.productcatalogservice.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private HeaderGenerator headerGenerator;

    // GET: Lấy danh sách danh mục
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        if (!categories.isEmpty()) {
            return new ResponseEntity<>(categories, headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // POST: Thêm mới danh mục
    @PostMapping("/categories")
    public ResponseEntity<?> addCategory(@RequestBody Category category, HttpServletRequest request) {
        if (category != null) {
            try {
                Category savedCategory = categoryService.addCategory(category);
                return new ResponseEntity<>(savedCategory, headerGenerator.getHeadersForSuccessPostMethod(request, savedCategory.getId()), HttpStatus.CREATED);
            } catch (RuntimeException e) {
                // Bắt lỗi trùng tên danh mục từ Service
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(headerGenerator.getHeadersForError(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(headerGenerator.getHeadersForError(), HttpStatus.BAD_REQUEST);
    }

    // PUT: Cập nhật danh mục
    @PutMapping("/categories/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable("id") Long id, @RequestBody Category categoryRequest) {
        try {
            // Đã mở khóa: Gọi hàm update từ Service
            Category updatedCategory = categoryService.updateCategory(id, categoryRequest);
            if (updatedCategory != null) {
                return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
            }
            return new ResponseEntity<>("Không tìm thấy danh mục để cập nhật", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE: Xóa danh mục
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable("id") Long id) {
        try {
            // Đã mở khóa: Gọi hàm xóa từ Service
            categoryService.deleteCategory(id);
            return new ResponseEntity<>("Xóa danh mục thành công", HttpStatus.OK);
        } catch (RuntimeException e) {
            // Bắt lỗi không cho xóa nếu danh mục đang chứa sản phẩm
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}