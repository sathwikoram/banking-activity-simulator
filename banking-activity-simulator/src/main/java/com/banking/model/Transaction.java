package com.banking.model;

import java.sql.Timestamp;

public class Transaction {
    private long transactionId;
    private String utrNumber;
    private Timestamp transactionDate;
    private double transactionAmount;
    private Timestamp debitedDate;
    private long accountId;
    private double balanceAfterTxn;
    private String description;
    private String modifiedBy;
    private String receiver;
    private String transactionType;
    private String modeOfTransaction;
    private String bankBranch;

    public long getTransactionId() { return transactionId; }
    public void setTransactionId(long transactionId) { this.transactionId = transactionId; }

    public String getUtrNumber() { return utrNumber; }
    public void setUtrNumber(String utrNumber) { this.utrNumber = utrNumber; }

    public Timestamp getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Timestamp transactionDate) { this.transactionDate = transactionDate; }

    public double getTransactionAmount() { return transactionAmount; }
    public void setTransactionAmount(double transactionAmount) { this.transactionAmount = transactionAmount; }

    public Timestamp getDebitedDate() { return debitedDate; }
    public void setDebitedDate(Timestamp debitedDate) { this.debitedDate = debitedDate; }

    public long getAccountId() { return accountId; }
    public void setAccountId(long accountId) { this.accountId = accountId; }

    public double getBalanceAfterTxn() { return balanceAfterTxn; }
    public void setBalanceAfterTxn(double balanceAfterTxn) { this.balanceAfterTxn = balanceAfterTxn; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getModifiedBy() { return modifiedBy; }
    public void setModifiedBy(String modifiedBy) { this.modifiedBy = modifiedBy; }

    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public String getModeOfTransaction() { return modeOfTransaction; }
    public void setModeOfTransaction(String modeOfTransaction) { this.modeOfTransaction = modeOfTransaction; }

    public String getBankBranch() { return bankBranch; }
    public void setBankBranch(String bankBranch) { this.bankBranch = bankBranch; }
}
