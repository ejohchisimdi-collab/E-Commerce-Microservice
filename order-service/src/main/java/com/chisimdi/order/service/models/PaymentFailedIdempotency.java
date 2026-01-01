package com.chisimdi.order.service.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
@Entity
public class PaymentFailedIdempotency {
    @Id
    private String id;
    private String context="Payment Failed";
    private LocalDateTime localDateTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
