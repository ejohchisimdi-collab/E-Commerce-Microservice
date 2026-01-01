package com.chisimdi.order.service.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int userId;
    private String productName;
    private BigDecimal basePrice;
    private BigDecimal totalPrice;
    private int cartId;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private int amount;
    private List<String>failureReasons=new ArrayList<>();
    @Version
    private int version;

    private LocalDateTime createdAt=LocalDateTime.now();


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setFailureReasons(List<String> failureReasons) {
        this.failureReasons = failureReasons;
    }

    public List<String> getFailureReasons() {
        return failureReasons;
    }
}
