package com.banking;

import java.sql.Timestamp;

public class Transaction {
    private Long transactionId;
    private String utrNumber;
    private Timestamp transactionDate;
    private double transactionAmount;
    private Timestamp debitedDate;
    private Long accountId;
    private double balanceAfterTxn;
    private String description;
    private String modifiedBy;
    private String receiver;
    private String transactionType;
    private String modeOfTransaction;
    private String bankBranch;

}
