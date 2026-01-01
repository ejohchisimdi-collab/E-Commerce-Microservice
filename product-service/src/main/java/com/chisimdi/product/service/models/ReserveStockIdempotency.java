package com.chisimdi.product.service.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class ReserveStockIdempotency {
    @Id
    private String id;
    private String context="Reserving stocks";
    private LocalDateTime localDateTime;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getContext() {
        return context;
    }
}
