package com.banking.controller;

import com.banking.model.Transaction;
import com.banking.service.TransactionService;
import com.banking.util.ApiResponse;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import java.io.ByteArrayInputStream;

import static spark.Spark.get;
import static spark.Spark.post;

public class TransactionController {

    private static final Gson gson = new Gson();
    private static TransactionService transactionService;

    public static void init(TransactionService service) {
        transactionService = service;

        // Create Deposit / Withdrawal Transaction by Account ID
        post("/transactions", (Request req, Response res) -> {
            res.type("application/json");
            try {
                Transaction transaction = gson.fromJson(req.body(), Transaction.class);

                if (transaction.getTransactionType() == null ||
                        transaction.getTransactionAmount() <= 0 ||
                        transaction.getAccountId() == 0) {
                    res.status(400);
                    return gson.toJson(new ApiResponse(false,
                            "accountId, transactionAmount (>0), and transactionType are required"));
                }

                long transactionId = transactionService.createTransaction(transaction);
                if (transactionId > 0) {
                    res.status(201);
                    return gson.toJson(new ApiResponse(true, "Transaction successful", transactionId));
                } else {
                    res.status(400);
                    return gson.toJson(new ApiResponse(false, "Transaction could not be created"));
                }
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ApiResponse(false, "Error creating transaction: " + e.getMessage()));
            }
        });

        //  Transfer by Account ID endpoint
        post("/transactions/transfer", (Request req, Response res) -> {
            res.type("application/json");
            try {
                TransferRequest transferReq = gson.fromJson(req.body(), TransferRequest.class);

                if (transferReq.senderAccountId <= 0 ||
                        transferReq.receiverAccountId <= 0 ||
                        transferReq.transactionAmount <= 0) {
                    res.status(400);
                    return gson.toJson(new ApiResponse(false,
                            "senderAccountId, receiverAccountId, and transactionAmount (>0) are required"));
                }

                boolean success = transactionService.transferAmount(
                        transferReq.senderAccountId,
                        transferReq.receiverAccountId,
                        transferReq.transactionAmount,
                        transferReq.description
                );

                if (success) {
                    res.status(201);
                    return gson.toJson(new ApiResponse(true, "Transfer successful"));
                } else {
                    res.status(400);
                    return gson.toJson(new ApiResponse(false, "Transfer failed"));
                }
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ApiResponse(false, "Error processing transfer: " + e.getMessage()));
            }
        });

        // Transfer by Customer ID endpoint
        post("/transactions/transferByCustomerId", (Request req, Response res) -> {
            res.type("application/json");
            try {
                TransferByCustomerIdRequest transferReq = gson.fromJson(req.body(), TransferByCustomerIdRequest.class);

                if (transferReq.senderId <= 0 ||
                        transferReq.receiverId <= 0 ||
                        transferReq.transactionAmount <= 0) {
                    res.status(400);
                    return gson.toJson(new ApiResponse(false, "senderId, receiverId, and transactionAmount (>0) are required"));
                }

                boolean success = transactionService.transferByCustomerId(
                        transferReq.senderId,
                        transferReq.receiverId,
                        transferReq.transactionAmount,
                        transferReq.description
                );

                if (success) {
                    res.status(201);
                    return gson.toJson(new ApiResponse(true, "Transfer successful"));
                } else {
                    res.status(400);
                    return gson.toJson(new ApiResponse(false, "Transfer failed"));
                }
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ApiResponse(false, "Error processing transfer: " + e.getMessage()));
            }
        });

        // NEW: Deposit/Withdrawal by Customer ID endpoint
        post("/transactions/depositWithdrawByCustomerId", (Request req, Response res) -> {
            res.type("application/json");
            try {
                DepositWithdrawRequest reqBody = gson.fromJson(req.body(), DepositWithdrawRequest.class);

                if (reqBody.customerId <= 0 ||
                        reqBody.transactionAmount <= 0 ||
                        reqBody.transactionType == null) {
                    res.status(400);
                    return gson.toJson(new ApiResponse(false, "customerId, transactionAmount (>0), and transactionType are required"));
                }

                long transactionId = transactionService.depositWithdrawByCustomerId(
                        reqBody.customerId,
                        reqBody.transactionAmount,
                        reqBody.transactionType,
                        reqBody.description,
                        reqBody.modeOfTransaction
                );

                if (transactionId > 0) {
                    res.status(201);
                    return gson.toJson(new ApiResponse(true, "Transaction successful", transactionId));
                } else {
                    res.status(400);
                    return gson.toJson(new ApiResponse(false, "Transaction could not be created"));
                }
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ApiResponse(false, "Error processing transaction: " + e.getMessage()));
            }
        });

        // NEW: Deposit/Withdrawal by Account Number endpoint
        post("/transactions/depositWithdrawByAccountNumber", (Request req, Response res) -> {
            res.type("application/json");
            try {
                DepositWithdrawByAccountNumberRequest reqBody = gson.fromJson(req.body(), DepositWithdrawByAccountNumberRequest.class);

                if (reqBody.accountNumber == null ||
                        reqBody.transactionAmount <= 0 ||
                        reqBody.transactionType == null) {
                    res.status(400);
                    return gson.toJson(new ApiResponse(false, "accountNumber, transactionAmount (>0), and transactionType are required"));
                }

                long transactionId = transactionService.depositWithdrawByAccountNumber(
                        reqBody.accountNumber,
                        reqBody.transactionAmount,
                        reqBody.transactionType,
                        reqBody.description,
                        reqBody.modeOfTransaction
                );

                if (transactionId > 0) {
                    res.status(201);
                    return gson.toJson(new ApiResponse(true, "Transaction successful", transactionId));
                } else {
                    res.status(400);
                    return gson.toJson(new ApiResponse(false, "Transaction could not be created"));
                }
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ApiResponse(false, "Error processing transaction: " + e.getMessage()));
            }
        });

        // NEW: Transfer by Account Number endpoint
        post("/transactions/transferByAccountNumber", (Request req, Response res) -> {
            res.type("application/json");
            try {
                TransferByAccountNumberRequest reqBody = gson.fromJson(req.body(), TransferByAccountNumberRequest.class);

                if (reqBody.senderAccountNumber == null ||
                        reqBody.receiverAccountNumber == null ||
                        reqBody.transactionAmount <= 0) {
                    res.status(400);
                    return gson.toJson(new ApiResponse(false, "senderAccountNumber, receiverAccountNumber, and transactionAmount (>0) are required"));
                }

                boolean success = transactionService.transferByAccountNumber(
                        reqBody.senderAccountNumber,
                        reqBody.receiverAccountNumber,
                        reqBody.transactionAmount,
                        reqBody.description
                );

                if (success) {
                    res.status(201);
                    return gson.toJson(new ApiResponse(true, "Transfer successful"));
                } else {
                    res.status(400);
                    return gson.toJson(new ApiResponse(false, "Transfer failed"));
                }
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ApiResponse(false, "Error processing transfer: " + e.getMessage()));
            }
        });


        // Get transaction by ID
        get("/transactions/:id", (Request req, Response res) -> {
            res.type("application/json");
            try {
                long id = Long.parseLong(req.params("id"));
                Transaction transaction = transactionService.getTransactionById(id);
                if (transaction != null) {
                    return gson.toJson(new ApiResponse(true, "Transaction found", transaction));
                } else {
                    res.status(404);
                    return gson.toJson(new ApiResponse(false, "Transaction not found"));
                }
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ApiResponse(false, "Error fetching transaction: " + e.getMessage()));
            }
        });

        //  Get all transactions
        get("/transactions", (Request req, Response res) -> {
            res.type("application/json");
            try {
                return gson.toJson(new ApiResponse(true, "All transactions",
                        transactionService.getAllTransactions()));
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ApiResponse(false, "Error fetching transactions: " + e.getMessage()));
            }
        });

        // NEW GET endpoint to export transactions to Excel
        get("/transactions/export/excel", (req, res) -> {
            try {
                byte[] excelFile = transactionService.generateTransactionHistoryExcel();
                if (excelFile != null) {
                    res.type("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                    res.header("Content-Disposition", "attachment; filename=\"transaction_history.xlsx\"");
                    return new ByteArrayInputStream(excelFile);
                } else {
                    res.status(500);
                    return "Failed to generate Excel file";
                }
            } catch (Exception e) {
                res.status(500);
                return "Error exporting transactions: " + e.getMessage();
            }
        });


    }

    // Helper class for Transfer by Account ID
    private static class TransferRequest {
        public long senderAccountId;
        public long receiverAccountId;
        public double transactionAmount;
        public String description;
    }

    // Helper class for Transfer by Customer ID
    private static class TransferByCustomerIdRequest {
        public long senderId;
        public long receiverId;
        public double transactionAmount;
        public String description;
    }

    // Helper class for Deposit/Withdrawal by Customer ID
    private static class DepositWithdrawRequest {
        public long customerId;
        public double transactionAmount;
        public String transactionType;
        public String description;
        public String modeOfTransaction;
    }

    // NEW Helper class for Deposit/Withdrawal by Account Number
    private static class DepositWithdrawByAccountNumberRequest {
        public String accountNumber;
        public double transactionAmount;
        public String transactionType;
        public String description;
        public String modeOfTransaction;
    }

    // NEW Helper class for Transfer by Account Number
    private static class TransferByAccountNumberRequest {
        public String senderAccountNumber;
        public String receiverAccountNumber;
        public double transactionAmount;
        public String description;
    }
}