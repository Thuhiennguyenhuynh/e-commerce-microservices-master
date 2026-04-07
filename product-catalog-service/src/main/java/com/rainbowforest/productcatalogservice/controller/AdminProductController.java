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

    // API Thêm sản phẩm
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product, HttpServletRequest request){
    	if(product != null) {
    		try {
    			Product savedProduct = productService.addProduct(product);
    	        return new ResponseEntity<>(
    	        		savedProduct,
    	        		headerGenerator.getHeadersForSuccessPostMethod(request, savedProduct.getId()),
    	        		HttpStatus.CREATED);
    		}catch (Exception e) {
				e.printStackTrace();
				return new ResponseEntity<>(
						headerGenerator.getHeadersForError(),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
    	}
    	return new ResponseEntity<>(
    			headerGenerator.getHeadersForError(),
    			HttpStatus.BAD_REQUEST);       
    }
    
    // API Cập nhật sản phẩm
    @PutMapping(value = "/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") Long id, @RequestBody Product productRequest) {
        Product currentProduct = productService.getProductById(id);
        if (currentProduct != null) {
            try {
                // Gọi sang ProductService để update (Đảm bảo logic update nằm trong service)
                Product updatedProduct = productService.updateProduct(id, productRequest); 
                return new ResponseEntity<>(
                        updatedProduct,
                        headerGenerator.getHeadersForSuccessGetMethod(),
                        HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(
                        headerGenerator.getHeadersForError(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);
    }

    // API Xóa sản phẩm
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id){
    	Product product = productService.getProductById(id);
    	if(product != null) {
    		try {
    			productService.deleteProduct(id);
    	        return new ResponseEntity<>(
    	        		headerGenerator.getHeadersForSuccessGetMethod(),
    	        		HttpStatus.OK);
    		}catch (Exception e) {
				e.printStackTrace();
    	        return new ResponseEntity<>(
    	        		headerGenerator.getHeadersForError(),
    	        		HttpStatus.INTERNAL_SERVER_ERROR);
			}
    	}
    	return new ResponseEntity<>(headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);      
    }
    @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<Product> addProduct(
        @RequestPart("product") Product product,
        @RequestPart("files") MultipartFile[] files,
        @RequestParam("defaultImageIndex") int defaultImageIndex) {
    
    try {
        // 1. Lưu thông tin sản phẩm trước
        Product savedProduct = productService.addProduct(product);

        // 2. Xử lý lưu nhiều file ảnh
        List<ProductImage> productImages = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            String fileName = fileService.saveFile(files[i], product.getProductName()); // Hàm tạo slug + save file
            
            ProductImage img = new ProductImage();
            img.setImageUrl(fileName);
            img.setProduct(savedProduct);
            img.setDefault(i == defaultImageIndex); // Chọn ảnh đại diện theo index gửi từ Front-end
            productImages.add(img);
        }
        
        savedProduct.setImages(productImages);
        productService.updateProduct(savedProduct.getId(), savedProduct);

        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    } catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
}