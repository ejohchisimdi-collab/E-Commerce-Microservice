package com.chisimdi.finance.service.models;

import com.chisimdi.events.Location;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaymentDTO {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @ManyToOne
    private int accountId;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private Location location;
    private List<String> warnings =new ArrayList<>();
    private LocalDateTime localDate;

    public LocalDateTime getLocalDate() {
        return localDate;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setLocalDate(LocalDateTime localDate) {
        this.localDate = localDate;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

}
