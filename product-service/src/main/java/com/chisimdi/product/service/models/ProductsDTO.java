package com.chisimdi.product.service.models;

import java.math.BigDecimal;

public class ProductsDTO {
    private int id;
    private  String name;
    private  int stockSize;
    private String category;
    private String description;
    private BigDecimal price;

    public void setStockSize(int stockSize) {
        this.stockSize = stockSize;
    }

    public int getStockSize() {
        return stockSize;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

}
