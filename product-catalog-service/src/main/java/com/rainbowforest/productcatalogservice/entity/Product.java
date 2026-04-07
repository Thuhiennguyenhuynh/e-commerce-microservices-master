package com.rainbowforest.productcatalogservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {

    private static final String SKU_PATTERN = "^[A-Z0-9\\-]{3,20}$";
    private static final String NAME_PATTERN = "^[\\p{L}0-9 .,'\"()\\-]{3,100}$";
    private static final String URL_PATTERN = "^(https?://[\\w\\-./?=&%#]+)$";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sku", unique = true)
    @NotBlank(message = "Mã sản phẩm không được để trống")
    @Pattern(regexp = SKU_PATTERN, message = "Mã sản phẩm chỉ gồm chữ hoa, số và dấu gạch ngang, từ 3 đến 20 ký tự")
    private String sku;

    @Column(name = "product_name")
    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Pattern(regexp = NAME_PATTERN, message = "Tên sản phẩm không hợp lệ")
    private String productName;

    @Column(name = "price")
    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal price;

    @Column(name = "discription")
    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    private String discription;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    @NotNull(message = "Danh mục không được để trống")
    @Valid
    private Category category;

    @Column(name = "availability")
    @Min(value = 0, message = "Số lượng không được phép nhỏ hơn 0")
    private int availability;

    @Column(name = "main_image_url")
    @Pattern(regexp = URL_PATTERN, message = "URL ảnh đại diện không hợp lệ")
    private String mainImageUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<@Pattern(regexp = URL_PATTERN, message = "URL ảnh không hợp lệ") String> imageUrls = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    public Product() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
