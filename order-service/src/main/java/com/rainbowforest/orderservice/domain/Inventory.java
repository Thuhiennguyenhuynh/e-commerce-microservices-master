package com.rainbowforest.orderservice.domain;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên nguyên liệu không được để trống")
    @Column(name = "ingredient_name")
    private String ingredientName;

    @NotBlank(message = "Đơn vị tính không được để trống")
    @Column(name = "unit")
    private String unit;

    @Min(value = 0, message = "Tồn đầu ngày phải >= 0")
    @Column(name = "opening_stock")
    private int openingStock;

    @Min(value = 0, message = "Số lượng nhập phải >= 0")
    @Column(name = "stock_in")
    private int stockIn;

    @Min(value = 0, message = "Tồn cuối ngày phải >= 0")
    @Column(name = "closing_stock")
    private int closingStock;

    public Inventory() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIngredientName() { return ingredientName; }
    public void setIngredientName(String ingredientName) { this.ingredientName = ingredientName; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public int getOpeningStock() { return openingStock; }
    public void setOpeningStock(int openingStock) { this.openingStock = openingStock; }
    public int getStockIn() { return stockIn; }
    public void setStockIn(int stockIn) { this.stockIn = stockIn; }
    public int getClosingStock() { return closingStock; }
    public void setClosingStock(int closingStock) { this.closingStock = closingStock; }
}
