package com.rainbowforest.productcatalogservice.entity;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User ID không được để trống")
    @Column(name = "user_id")
    private Long userId;

    @NotNull(message = "Product ID không được để trống")
    @Column(name = "product_id")
    private Long productId;

    @Min(value = 1, message = "Đánh giá phải từ 1 đến 5 sao")
    @Max(value = 5, message = "Đánh giá phải từ 1 đến 5 sao")
    @Column(name = "rating")
    private int rating;

    @NotBlank(message = "Nội dung đánh giá không được để trống")
    @Column(name = "comment")
    private String comment;

    public Review() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
