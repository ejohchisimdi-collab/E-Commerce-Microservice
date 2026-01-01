package com.chisimdi.finance.service.models;

import com.chisimdi.events.Location;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @ManyToOne
    private Account account;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private Location location;
    private List<String> warnings =new ArrayList<>();
    private LocalDateTime localDate=LocalDateTime.now();
    @Version
    private int version;

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setLocalDate(LocalDateTime localDate) {
        this.localDate = localDate;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public LocalDateTime getLocalDate() {
        return localDate;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }


}
