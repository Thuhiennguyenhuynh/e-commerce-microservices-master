package com.rainbowforest.productcatalogservice.controller;

import com.rainbowforest.productcatalogservice.entity.Product;
import com.rainbowforest.productcatalogservice.http.header.HeaderGenerator;
import com.rainbowforest.productcatalogservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/admin")
public class AdminProductController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private HeaderGenerator headerGenerator;

    // API Lấy danh sách toàn bộ sản phẩm cho Admin
    // @GetMapping(value = "/products")
    // public ResponseEntity<List<Product>> getAllProducts() {
    //     List<Product> products = productService.getAllProduct();
    //     if(!products.isEmpty()) {
    //         return new ResponseEntity<List<Product>>(
    //                 products,
    //                 headerGenerator.getHeadersForSuccessGetMethod(),
    //                 HttpStatus.OK);
    //     }
    //     return new ResponseEntity<List<Product>>(
    //             headerGenerator.getHeadersForError(),
    //             HttpStatus.NOT_FOUND);       
    // }

    @GetMapping(value = "/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProduct();
        
        // Luôn trả về 200 OK, dù mảng products có dữ liệu hay rỗng
        return new ResponseEntity<List<Product>>(
                products,
                headerGenerator.getHeadersForSuccessGetMethod(),
                HttpStatus.OK);
    }

    // API Lấy 1 sản phẩm cụ thể theo ID cho Admin
    @GetMapping(value = "/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") Long id) {
        Product product = productService.getProductById(id);
        if(product != null) {
            return new ResponseEntity<Product>(
                    product,
                    headerGenerator.getHeadersForSuccessGetMethod(),
                    HttpStatus.OK);
        }
        return new ResponseEntity<Product>(
                headerGenerator.getHeadersForError(),
                HttpStatus.NOT_FOUND);
    }

    // ĐÃ SỬA: Đổi từ private thành public
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
    
    // ĐÃ SỬA: Đổi từ private thành public
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
                // Sử dụng phương thức updateProduct từ service
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