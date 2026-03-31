package com.rainbowforest.productcatalogservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table (name = "products")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "product_name")
	@NotBlank(message = "Tên sản phẩm không được để trống")
    // @NotNull
    private String productName;

    @Column (name = "price")
    @NotNull(message = "Giá không được để trống")
	@DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal price;

    @Column (name = "discription")
    private String discription;

    // @Column (name = "category")
    // @NotNull
    // private String category;

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
	@NotNull(message = "Danh mục không được để trống")
    private Category category;

    @Column (name = "availability")
    @NotNull(message = "Số lượng không được để trống")
	@Min(value = 0, message = "Số lượng không được phép nhỏ hơn 0")

    private int availability;

	public Product() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	// public String getCategory() {
	// 	return category;
	// }

	// public void setCategory(String category) {
	// 	this.category = category;
	// }

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
}
