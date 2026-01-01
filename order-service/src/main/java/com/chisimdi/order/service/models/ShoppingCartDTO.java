package com.chisimdi.order.service.models;

import java.math.BigDecimal;

public class ShoppingCartDTO {
    private int id;
    private int userId;
    private String productName;
    private BigDecimal basePrice;
    private BigDecimal totalPrice;
    private int amount;
    private CartStatus cartStatus;


    public CartStatus getCartStatus() {
        return cartStatus;
    }

    public void setCartStatus(CartStatus cartStatus) {
        this.cartStatus = cartStatus;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getId() {
        return id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
