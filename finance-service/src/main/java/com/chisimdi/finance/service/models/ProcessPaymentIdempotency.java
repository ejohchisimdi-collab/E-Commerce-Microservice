package com.chisimdi.finance.service.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
@Entity
public class ProcessPaymentIdempotency {
    @Id
    private String id;
    private String context="Processing Payment";
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
