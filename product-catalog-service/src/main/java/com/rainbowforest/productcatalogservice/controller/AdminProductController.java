package com.rainbowforest.productcatalogservice.controller;

import com.rainbowforest.productcatalogservice.entity.Product;
import com.rainbowforest.productcatalogservice.entity.ProductImage;
import com.rainbowforest.productcatalogservice.http.header.HeaderGenerator;
import com.rainbowforest.productcatalogservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/products")
public class AdminProductController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private HeaderGenerator headerGenerator;

    // BỔ SUNG: API Lấy danh sách sản phẩm cho Admin
    // @GetMapping
    // public ResponseEntity<List<Product>> getAllProductsForAdmin() {
    //     List<Product> products = productService.getAllProduct();
    //     if (products != null && !products.isEmpty()) {
    //         return new ResponseEntity<>(
    //                 products,
    //                 headerGenerator.getHeadersForSuccessGetMethod(),
    //                 HttpStatus.OK);
    //     }
    //     return new ResponseEntity<>(
    //             headerGenerator.getHeadersForError(),
    //             HttpStatus.NOT_FOUND);
                
    // }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProductsForAdmin() {
        List<Product> products = productService.getAllProduct();
        
        // Bỏ điều kiện kiểm tra rỗng đi, luôn trả về danh sách (dù rỗng) với status 200 OK
        return new ResponseEntity<>(
                products,
                headerGenerator.getHeadersForSuccessGetMethod(),
                HttpStatus.OK);
    }

  // Đã đổi từ private sang public
    @PostMapping(value = "/products")
    public ResponseEntity<Product> addProduct(@RequestBody Product product, HttpServletRequest request){
    	if(product != null) {
    		try {
    			productService.addProduct(product);
    	        return new ResponseEntity<Product>(
    	        		product,
    	        		headerGenerator.getHeadersForSuccessPostMethod(request, product.getId()),
    	        		HttpStatus.CREATED);
    		}catch (Exception e) {
				e.printStackTrace();
				return new ResponseEntity<Product>(
						headerGenerator.getHeadersForError(),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
    	}
    	return new ResponseEntity<Product>(
    			headerGenerator.getHeadersForError(),
    			HttpStatus.BAD_REQUEST);       
    }
    
    // Đã đổi từ private sang public
    @DeleteMapping(value = "/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id){
    	Product product = productService.getProductById(id);
    	if(product != null) {
    		try {
    			productService.deleteProduct(id);
    	        return new ResponseEntity<Void>(
    	        		headerGenerator.getHeadersForSuccessGetMethod(),
    	        		HttpStatus.OK);
    		}catch (Exception e) {
				e.printStackTrace();
    	        return new ResponseEntity<Void>(
    	        		headerGenerator.getHeadersForError(),
    	        		HttpStatus.INTERNAL_SERVER_ERROR);
			}
    	}
    	return new ResponseEntity<Void>(headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);      
    }

	@PutMapping(value = "/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") Long id, @RequestBody Product productRequest) {
        Product currentProduct = productService.getProductById(id);
        if (currentProduct != null) {
            try {
                Product updatedProduct = productService.updateProduct(id, productRequest); 
                return new ResponseEntity<Product>(
                        updatedProduct,
                        headerGenerator.getHeadersForSuccessGetMethod(),
                        HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<Product>(
                        headerGenerator.getHeadersForError(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<Product>(headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);
    }
}