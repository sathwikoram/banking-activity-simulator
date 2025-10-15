package com.banking.service;

import com.banking.dao.AccountDAO;
import com.banking.dao.TransactionDAO;
import com.banking.model.Account;
import com.banking.model.AccountEmailDetails;
import com.banking.model.Transaction;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

public class TransactionService {

    private final TransactionDAO transactionDAO;
    private final AccountDAO accountDAO;
    private final EmailService emailService;

    public TransactionService() {
        this.transactionDAO = new TransactionDAO();
        this.accountDAO = new AccountDAO();
        this.emailService = new EmailService();
    }

    // Helper method to generate the HTML email body
    private String generateHtmlEmailBody(String customerName, String transactionType, String accountNumber, BigDecimal amount, BigDecimal newBalance) {
        String htmlBody = String.format(
                "<html>" +
                        "<body style='font-family: Arial, sans-serif;'>" +
                        "<div style='background-color: #003366; color: white; padding: 20px; text-align: center;'>" +
                        "<h2>Transaction Alert</h2>" +
                        "</div>" +
                        "<div style='padding: 20px; border: 1px solid #ddd;'>" +
                        "<p>Dear %s,</p>" +
                        "<p>This is a notification for a %s transaction on your account %s.</p>" +
                        "<ul>" +
                        "<li><strong>Amount:</strong> &#x20B9; %.2f</li>" +
                        "<li><strong>New Balance:</strong> &#x20B9; %.2f</li>" +
                        "</ul>" +
                        "<p>Thank you for banking with us.</p>" +
                        "</div>" +
                        "</body>" +
                        "</html>",
                customerName,
                transactionType,
                accountNumber,
                amount,
                newBalance
        );
        return htmlBody;
    }

    public boolean transferAmount(long senderId, long receiverId, double amount, String description) {
        boolean success = transactionDAO.transferAmount(senderId, receiverId, amount, description);
        if (success) {
            AccountEmailDetails senderDetails = accountDAO.getAccountDetailsForEmail(senderId);
            AccountEmailDetails receiverDetails = accountDAO.getAccountDetailsForEmail(receiverId);

            if (senderDetails != null) {
                String senderSubject = "Funds Transferred";
                String senderBody = generateHtmlEmailBody(
                        senderDetails.getUsername(),
                        "Debit",
                        senderDetails.getAccountNumber(),
                        BigDecimal.valueOf(amount),
                        senderDetails.getBalance()
                );
                emailService.sendTransactionEmail(senderDetails.getEmail(), senderSubject, senderBody);
            }
            if (receiverDetails != null) {
                String receiverSubject = "Funds Received";
                String receiverBody = generateHtmlEmailBody(
                        receiverDetails.getUsername(),
                        "Credit",
                        receiverDetails.getAccountNumber(),
                        BigDecimal.valueOf(amount),
                        receiverDetails.getBalance()
                );
                emailService.sendTransactionEmail(receiverDetails.getEmail(), receiverSubject, receiverBody);
            }
        }
        return success;
    }

    public long createTransaction(Transaction transaction) {
        if ("TRANSFER".equalsIgnoreCase(transaction.getTransactionType())) {
            long senderId = transaction.getAccountId();
            long receiverId = Long.parseLong(transaction.getReceiver());

            boolean success = transferAmount(
                    senderId,
                    receiverId,
                    transaction.getTransactionAmount(),
                    transaction.getDescription()
            );
            return success ? 1 : -1;
        }

        Account account = accountDAO.getAccountById(transaction.getAccountId());
        if (account == null) {
            throw new IllegalArgumentException("Account not found with ID: " + transaction.getAccountId());
        }

        BigDecimal oldBalance = account.getBalance();
        BigDecimal newBalance;
        String transactionType = transaction.getTransactionType();

        switch (transactionType.toUpperCase()) {
            case "DEPOSIT":
                newBalance = oldBalance.add(BigDecimal.valueOf(transaction.getTransactionAmount()));
                break;
            case "WITHDRAWAL":
                if (oldBalance.compareTo(BigDecimal.valueOf(transaction.getTransactionAmount())) < 0) {
                    throw new IllegalArgumentException("Insufficient balance for withdrawal");
                }
                newBalance = oldBalance.subtract(BigDecimal.valueOf(transaction.getTransactionAmount()));
                break;
            default:
                throw new IllegalArgumentException("Invalid transaction type: " + transactionType);
        }

        account.setBalance(newBalance);
        accountDAO.updateAccount(account);
        long transactionId = transactionDAO.addTransaction(transaction, newBalance.doubleValue());

        if (transactionId > 0) {
            AccountEmailDetails accountDetails = accountDAO.getAccountDetailsForEmail(account.getAccountId());
            if (accountDetails != null) {
                String subject = transactionType.toUpperCase() + " Successful";
                String body = generateHtmlEmailBody(
                        accountDetails.getUsername(),
                        transactionType,
                        accountDetails.getAccountNumber(),
                        BigDecimal.valueOf(transaction.getTransactionAmount()),
                        newBalance
                );
                emailService.sendTransactionEmail(accountDetails.getEmail(), subject, body);
            }
        }
        return transactionId;
    }

    public boolean transferByCustomerId(long senderCustomerId, long receiverCustomerId, double amount, String description) {
        long senderAccountId = accountDAO.getAccountIdByCustomerId(senderCustomerId);
        long receiverAccountId = accountDAO.getAccountIdByCustomerId(receiverCustomerId);

        if (senderAccountId > 0 && receiverAccountId > 0) {
            boolean success = transactionDAO.transferAmount(senderAccountId, receiverAccountId, amount, description);
            if (success) {
                AccountEmailDetails senderDetails = accountDAO.getAccountDetailsForEmail(senderAccountId);
                AccountEmailDetails receiverDetails = accountDAO.getAccountDetailsForEmail(receiverAccountId);

                if (senderDetails != null) {
                    String senderSubject = "Funds Transferred";
                    String senderBody = generateHtmlEmailBody(
                            senderDetails.getUsername(),
                            "Debit",
                            senderDetails.getAccountNumber(),
                            BigDecimal.valueOf(amount),
                            senderDetails.getBalance()
                    );
                    emailService.sendTransactionEmail(senderDetails.getEmail(), senderSubject, senderBody);
                }
                if (receiverDetails != null) {
                    String receiverSubject = "Funds Received";
                    String receiverBody = generateHtmlEmailBody(
                            receiverDetails.getUsername(),
                            "Credit",
                            receiverDetails.getAccountNumber(),
                            BigDecimal.valueOf(amount),
                            receiverDetails.getBalance()
                    );
                    emailService.sendTransactionEmail(receiverDetails.getEmail(), receiverSubject, receiverBody);
                }
            }
            return success;
        }
        return false;
    }

    public long depositWithdrawByCustomerId(long customerId, double amount, String type, String description, String mode) {
        long accountId = accountDAO.getAccountIdByCustomerId(customerId);

        if (accountId <= 0) {
            throw new IllegalArgumentException("Account not found for customer with ID: " + customerId);
        }

        Transaction txn = new Transaction();
        txn.setAccountId(accountId);
        txn.setTransactionAmount(amount);
        txn.setTransactionType(type);
        txn.setDescription(description);
        txn.setModeOfTransaction(mode);

        long transactionId = createTransaction(txn);

        if (transactionId > 0) {
            AccountEmailDetails accountDetails = accountDAO.getAccountDetailsForEmail(accountId);
            if (accountDetails != null) {
                String subject = type.toUpperCase() + " Successful";
                String body = generateHtmlEmailBody(
                        accountDetails.getUsername(),
                        type,
                        accountDetails.getAccountNumber(),
                        BigDecimal.valueOf(amount),
                        accountDetails.getBalance()
                );
                emailService.sendTransactionEmail(accountDetails.getEmail(), subject, body);
            }
        }
        return transactionId;
    }

    public long depositWithdrawByAccountNumber(String accountNumber, double amount, String type, String description, String mode) {
        long accountId = accountDAO.getAccountIdByNumber(accountNumber);

        if (accountId <= 0) {
            throw new IllegalArgumentException("Account not found with number: " + accountNumber);
        }

        Transaction txn = new Transaction();
        txn.setAccountId(accountId);
        txn.setTransactionAmount(amount);
        txn.setTransactionType(type);
        txn.setDescription(description);
        txn.setModeOfTransaction(mode);

        long transactionId = createTransaction(txn);

        if (transactionId > 0) {
            AccountEmailDetails accountDetails = accountDAO.getAccountDetailsForEmail(accountId);
            if (accountDetails != null) {
                String subject = type.toUpperCase() + " Successful";
                String body = generateHtmlEmailBody(
                        accountDetails.getUsername(),
                        type,
                        accountDetails.getAccountNumber(),
                        BigDecimal.valueOf(amount),
                        accountDetails.getBalance()
                );
                emailService.sendTransactionEmail(accountDetails.getEmail(), subject, body);
            }
        }
        return transactionId;
    }

    public boolean transferByAccountNumber(String senderAccountNumber, String receiverAccountNumber, double amount, String description) {
        long senderAccountId = accountDAO.getAccountIdByNumber(senderAccountNumber);
        long receiverAccountId = accountDAO.getAccountIdByNumber(receiverAccountNumber);

        if (senderAccountId > 0 && receiverAccountId > 0) {
            boolean success = transactionDAO.transferAmount(senderAccountId, receiverAccountId, amount, description);
            if (success) {
                AccountEmailDetails senderDetails = accountDAO.getAccountDetailsForEmail(senderAccountId);
                AccountEmailDetails receiverDetails = accountDAO.getAccountDetailsForEmail(receiverAccountId);

                if (senderDetails != null) {
                    String senderSubject = "Funds Transferred";
                    String senderBody = generateHtmlEmailBody(
                            senderDetails.getUsername(),
                            "Debit",
                            senderDetails.getAccountNumber(),
                            BigDecimal.valueOf(amount),
                            senderDetails.getBalance()
                    );
                    emailService.sendTransactionEmail(senderDetails.getEmail(), senderSubject, senderBody);
                }
                if (receiverDetails != null) {
                    String receiverSubject = "Funds Received";
                    String receiverBody = generateHtmlEmailBody(
                            receiverDetails.getUsername(),
                            "Credit",
                            receiverDetails.getAccountNumber(),
                            BigDecimal.valueOf(amount),
                            receiverDetails.getBalance()
                    );
                    emailService.sendTransactionEmail(receiverDetails.getEmail(), receiverSubject, receiverBody);
                }
            }
            return success;
        }
        return false;
    }

    // NEW: Method to fetch all transactions and create an Excel file
    public byte[] generateTransactionHistoryExcel() {
        List<Transaction> transactions = transactionDAO.getAllTransactions();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Transaction History");

            // Create header row
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Transaction ID");
            header.createCell(1).setCellValue("Account ID");
            header.createCell(2).setCellValue("Type");
            header.createCell(3).setCellValue("Amount");
            header.createCell(4).setCellValue("Date");
            header.createCell(5).setCellValue("Description");

            // Populate data rows
            int rowNum = 1;
            for (Transaction txn : transactions) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(txn.getTransactionId());
                row.createCell(1).setCellValue(txn.getAccountId());
                row.createCell(2).setCellValue(txn.getTransactionType());
                row.createCell(3).setCellValue(txn.getTransactionAmount());
                row.createCell(4).setCellValue(txn.getTransactionDate().toString());
                row.createCell(5).setCellValue(txn.getDescription());
            }

            // Write the workbook to a byte array
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public Transaction getTransactionById(long id) {
        return transactionDAO.getTransactionById(id);
    }

    public List<Transaction> getAllTransactions() {
        return transactionDAO.getAllTransactions();
    }
}