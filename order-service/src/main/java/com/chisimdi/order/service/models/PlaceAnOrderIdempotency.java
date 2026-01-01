package com.chisimdi.order.service.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PlaceAnOrderIdempotency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    private Orders orders;
    private String idempotencyKey;

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Orders getOrders() {
        return orders;
    }

    public int getId() {
        return id;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

}
