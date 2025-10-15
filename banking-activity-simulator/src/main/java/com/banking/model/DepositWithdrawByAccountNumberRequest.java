package com.banking.model;

public class DepositWithdrawByAccountNumberRequest {
    public String accountNumber;
    public double transactionAmount;
    public String transactionType;
    public String description;
    public String modeOfTransaction;
}