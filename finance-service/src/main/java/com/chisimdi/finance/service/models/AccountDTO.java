package com.chisimdi.finance.service.models;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;

public class AccountDTO {
    private int id;
    private int userId;
    private BigDecimal balance;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    public BigDecimal getBalance() {
        return balance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
