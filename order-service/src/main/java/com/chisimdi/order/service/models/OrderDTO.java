package com.chisimdi.order.service.models;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDTO {
    private int id;
    private int userId;
    private String productName;
    private BigDecimal basePrice;
    private BigDecimal totalPrice;
    private int cartId;
    private int amount;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    private List<String>failureReasons=new ArrayList<>();

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public List<String> getFailureReasons() {
        return failureReasons;
    }

    public void setFailureReasons(List<String> failureReasons) {
        this.failureReasons = failureReasons;
    }
}
