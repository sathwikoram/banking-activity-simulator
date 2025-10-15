package com.banking.model;

import java.math.BigDecimal;

public class AccountEmailDetails {
    private final String username;
    private final String email;
    private final String accountNumber;
    private final BigDecimal balance;

    public AccountEmailDetails(String username, String email, String accountNumber, BigDecimal balance) {
        this.username = username;
        this.email = email;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}