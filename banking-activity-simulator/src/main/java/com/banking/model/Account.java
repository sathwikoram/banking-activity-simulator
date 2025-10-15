package com.banking.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Account {
    private long accountId;
    private long customerId;
    private String accountType;
    private String bankName;
    private String branch;
    private BigDecimal balance;
    private String status;
    private Timestamp createdAt;
    private Timestamp modifiedAt;
    private String accountNumber;
    private String ifscCode;
    private String nameOnAccount;
    private String phoneLinked;
    private BigDecimal savingAmount;

    public long getAccountId() { return accountId; }
    public void setAccountId(long accountId) { this.accountId = accountId; }

    public long getCustomerId() { return customerId; }
    public void setCustomerId(long customerId) { this.customerId = customerId; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getModifiedAt() { return modifiedAt; }
    public void setModifiedAt(Timestamp modifiedAt) { this.modifiedAt = modifiedAt; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }

    public String getNameOnAccount() { return nameOnAccount; }
    public void setNameOnAccount(String nameOnAccount) { this.nameOnAccount = nameOnAccount; }

    public String getPhoneLinked() { return phoneLinked; }
    public void setPhoneLinked(String phoneLinked) { this.phoneLinked = phoneLinked; }

    public BigDecimal getSavingAmount() { return savingAmount; }
    public void setSavingAmount(BigDecimal savingAmount) { this.savingAmount = savingAmount; }
}
