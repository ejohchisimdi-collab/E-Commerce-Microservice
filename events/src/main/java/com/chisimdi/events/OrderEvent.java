package com.chisimdi.events;

import java.math.BigDecimal;

public class OrderEvent {
    private String id;
    private int orderId;
    private int userId;
    private int accountId;
    private Location location;
    private int amount;
    private BigDecimal totalPrice;
    private String productName;

    public OrderEvent(){}

    public OrderEvent(String id, int orderId, int userId, int accountId, Location location, int amount, BigDecimal totalPrice, String productName){
        this.id=id;
        this.userId=userId;
        this.accountId=accountId;
        this.location=location;
        this.amount=amount;
        this.totalPrice=totalPrice;
        this.productName=productName;
        this.orderId=orderId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
