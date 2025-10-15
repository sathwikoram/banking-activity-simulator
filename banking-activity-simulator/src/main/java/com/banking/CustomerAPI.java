package com.banking;

import static spark.Spark.*;
import com.google.gson.Gson;
import com.banking.controller.TransactionController;
import com.banking.dao.CustomerDAO;
import com.banking.dao.AccountDAO;
import com.banking.dao.TransactionDAO;
import com.banking.model.Customer;
import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.service.AccountService;
import com.banking.service.TransactionService;
import com.banking.util.ApiResponse;

public class CustomerAPI {
    public static void main(String[] args) {
        port(8081);
        Gson gson = new Gson();

        CustomerDAO customerDAO = new CustomerDAO();
        AccountDAO accountDAO = new AccountDAO();
        TransactionDAO transactionDAO = new TransactionDAO();

        AccountService accountService = new AccountService();
        TransactionService transactionService = new TransactionService();

        TransactionController.init(transactionService);


        get("/customers", (req, res) -> {
            res.type("application/json");
            return gson.toJson(customerDAO.getAllCustomers());
        });

        get("/customers/:id", (req, res) -> {
            long id = Long.parseLong(req.params(":id"));
            Customer c = customerDAO.getCustomerById(id);
            res.type("application/json");
            return gson.toJson(c);
        });

        post("/customers", (req, res) -> {
            res.type("application/json");
            try {
                Customer c = gson.fromJson(req.body(), Customer.class);

                if (c.getUsername() == null || c.getPassword() == null || c.getAadharNumber() == null) {
                    res.status(400);
                    return "{\"error\":\"username, password and aadharNumber are required\"}";
                }

                long id = customerDAO.addCustomer(c);
                if (id > 0) {
                    res.status(201);
                    return "{\"id\":" + id + "}";
                } else {
                    res.status(500);
                    return "{\"error\":\"" + (customerDAO.getLastError() != null ? customerDAO.getLastError() : "Failed to create customer") + "\"}";
                }
            } catch (Exception e) {
                res.status(500);
                return "{\"error\":\"" + e.getMessage() + "\"}";
            }
        });

        put("/customers/:id", (req, res) -> {
            res.type("application/json");
            try {
                long id = Long.parseLong(req.params(":id"));
                Customer c = gson.fromJson(req.body(), Customer.class);
                boolean updated = customerDAO.updateCustomer(id, c);
                if (updated) {
                    res.status(200);
                    return "{\"message\":\"Customer updated successfully\"}";
                } else {
                    res.status(404);
                    return "{\"error\":\"Customer not found or not updated\"}";
                }
            } catch (Exception e) {
                res.status(500);
                return "{\"error\":\"" + e.getMessage() + "\"}";
            }
        });

        delete("/customers/:id", (req, res) -> {
            res.type("application/json");
            try {
                long id = Long.parseLong(req.params(":id"));
                boolean deleted = customerDAO.deleteCustomer(id);
                if (deleted) {
                    res.status(200);
                    return "{\"message\":\"Customer deleted successfully\"}";
                } else {
                    res.status(404);
                    return "{\"error\":\"Customer not found\"}";
                }
            } catch (Exception e) {
                res.status(500);
                return "{\"error\":\"" + e.getMessage() + "\"}";
            }
        });



        get("/accounts", (req, res) -> {
            res.type("application/json");
            return gson.toJson(accountDAO.getAllAccounts());
        });

        get("/accounts/:id", (req, res) -> {
            long id = Long.parseLong(req.params(":id"));
            Account acc = accountDAO.getAccountById(id);
            res.type("application/json");
            return gson.toJson(acc);
        });

        post("/accounts", (req, res) -> {
            res.type("application/json");
            try {
                Account acc = gson.fromJson(req.body(), Account.class);
                if (acc.getCustomerId() == 0 || acc.getAccountNumber() == null) {
                    res.status(400);
                    return "{\"error\":\"customerId and accountNumber are required\"}";
                }
                long id = accountService.createAccount(acc);
                if (id > 0) {
                    res.status(201);
                    return gson.toJson(new ApiResponse(true, "Account created successfully", id));
                } else {
                    res.status(500);
                    return gson.toJson(new ApiResponse(false, "Failed to create account"));
                }
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ApiResponse(false, "Error creating account: " + e.getMessage()));
            }
        });

        put("/accounts/:id", (req, res) -> {
            res.type("application/json");
            try {
                long id = Long.parseLong(req.params(":id"));
                Account acc = gson.fromJson(req.body(), Account.class);
                acc.setAccountId(id);
                boolean updated = accountDAO.updateAccount(acc);
                if (updated) {
                    res.status(200);
                    return "{\"message\":\"Account updated successfully\"}";
                } else {
                    res.status(404);
                    return "{\"error\":\"Account not found or not updated\"}";
                }
            } catch (Exception e) {
                res.status(500);
                return "{\"error\":\"" + e.getMessage() + "\"}";
            }
        });

        delete("/accounts/:id", (req, res) -> {
            res.type("application/json");
            try {
                long id = Long.parseLong(req.params(":id"));
                boolean deleted = accountDAO.deleteAccount(id);
                if (deleted) {
                    res.status(200);
                    return "{\"message\":\"Account deleted successfully\"}";
                } else {
                    res.status(404);
                    return "{\"error\":\"Account not found\"}";
                }
            } catch (Exception e) {
                res.status(500);
                return "{\"error\":\"" + e.getMessage() + "\"}";
            }
        });


        get("/transactions", (req, res) -> {
            res.type("application/json");
            return gson.toJson(transactionDAO.getAllTransactions());
        });

        get("/transactions/:id", (req, res) -> {
            long id = Long.parseLong(req.params(":id"));
            Transaction txn = transactionDAO.getTransactionById(id);
            res.type("application/json");
            return gson.toJson(txn);
        });

        post("/transactions", (req, res) -> {
            res.type("application/json");
            try {
                Transaction txn = gson.fromJson(req.body(), Transaction.class);
                if (txn.getAccountId() == 0 || txn.getTransactionAmount() <= 0 || txn.getTransactionType() == null) {
                    res.status(400);
                    return "{\"error\":\"accountId, transactionAmount (>0), and transactionType are required\"}";
                }
                long id = transactionService.createTransaction(txn);
                if (id > 0) {
                    res.status(201);
                    return gson.toJson(new ApiResponse(true, "Transaction successful", id));
                } else {
                    res.status(500);
                    return gson.toJson(new ApiResponse(false, "Failed to create transaction"));
                }
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ApiResponse(false, "Error creating transaction: " + e.getMessage()));
            }
        });

        put("/transactions/:id", (req, res) -> {
            res.type("application/json");
            try {
                long id = Long.parseLong(req.params(":id"));
                Transaction txn = gson.fromJson(req.body(), Transaction.class);
                boolean updated = transactionDAO.updateTransaction(id, txn);
                if (updated) {
                    res.status(200);
                    return "{\"message\":\"Transaction updated successfully\"}";
                } else {
                    res.status(404);
                    return "{\"error\":\"Transaction not found or not updated\"}";
                }
            } catch (Exception e) {
                res.status(500);
                return "{\"error\":\"" + e.getMessage() + "\"}";
            }
        });

        delete("/transactions/:id", (req, res) -> {
            res.type("application/json");
            try {
                long id = Long.parseLong(req.params(":id"));
                boolean deleted = transactionDAO.deleteTransaction(id);
                if (deleted) {
                    res.status(200);
                    return "{\"message\":\"Transaction deleted successfully\"}";
                } else {
                    res.status(404);
                    return "{\"error\":\"Transaction not found\"}";
                }
            } catch (Exception e) {
                res.status(500);
                return "{\"error\":\"" + e.getMessage() + "\"}";
            }
        });
    }
}