package com.chisimdi.order.service.models;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int userId;
    private String productName;
    private BigDecimal basePrice;
    private BigDecimal totalPrice;
    @Enumerated(EnumType.STRING)
    private CartStatus cartStatus;
    private int amount;


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public CartStatus getCartStatus() {
        return cartStatus;
    }

    public void setCartStatus(CartStatus cartStatus) {
        this.cartStatus = cartStatus;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }
}
