package com.banking;

import static spark.Spark.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
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
import java.util.Date;

public class Main {

    public static void main(String[] args) {
        port(8081);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> new Date(json.getAsLong()))
                .create();

        // Add CORS filter here
        options("/*",
                (request, response) -> {
                    String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
                    if (accessControlRequestHeaders != null) {
                        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
                    }

                    String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
                    if (accessControlRequestMethod != null) {
                        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
                    }

                    return "OK";
                });
        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));


        CustomerDAO customerDao = new CustomerDAO();
        AccountDAO accountDao = new AccountDAO();
        TransactionDAO transactionDao = new TransactionDAO();

        AccountService accountService = new AccountService();
        TransactionService transactionService = new TransactionService();

        TransactionController.init(transactionService);

        // ======================
        // CUSTOMER ROUTES
        // ======================

        post("/customers", (req, res) -> {
            res.type("application/json");
            try {
                Customer c = gson.fromJson(req.body(), Customer.class);

                if (c.getUsername() == null || c.getPassword() == null || c.getAadharNumber() == null) {
                    res.status(400);
                    return "{\"error\":\"username, password and aadharNumber are required\"}";
                }

                long id = customerDao.addCustomer(c);
                if (id > 0) {
                    res.status(201);
                    return "{\"id\":" + id + "}";
                } else {
                    res.status(500);
                    return "{\"error\":\"" + (customerDao.getLastError() != null ? customerDao.getLastError() : "Failed to create customer") + "\"}";
                }
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "{\"error\":\"" + e.getMessage() + "\"}";
            }
        });

        get("/customers", (req, res) -> {
            res.type("application/json");
            return gson.toJson(customerDao.getAllCustomers());
        });

        get("/customers/:id", (req, res) -> {
            res.type("application/json");
            long id = Long.parseLong(req.params(":id"));
            Customer c = customerDao.getCustomerById(id);
            return (c != null) ? gson.toJson(c) : "{}";
        });

        put("/customers/:id", (req, res) -> {
            res.type("application/json");
            long id = Long.parseLong(req.params(":id"));
            Customer c = gson.fromJson(req.body(), Customer.class);
            boolean updated = customerDao.updateCustomer(id, c);
            return "{\"updated\":" + updated + "}";
        });

        delete("/customers/:id", (req, res) -> {
            res.type("application/json");
            long id = Long.parseLong(req.params(":id"));
            boolean deleted = customerDao.deleteCustomer(id);
            return "{\"deleted\":" + deleted + "}";
        });



        post("/accounts", (req, res) -> {
            res.type("application/json");
            try {
                Account account = gson.fromJson(req.body(), Account.class);
                long id = accountService.createAccount(account);

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

        get("/accounts", (req, res) -> {
            res.type("application/json");
            return gson.toJson(accountDao.getAllAccounts());
        });

        get("/accounts/:id", (req, res) -> {
            res.type("application/json");
            long id = Long.parseLong(req.params(":id"));
            Account a = accountDao.getAccountById(id);
            return (a != null) ? gson.toJson(a) : "{}";
        });

        put("/accounts/:id", (req, res) -> {
            res.type("application/json");
            long id = Long.parseLong(req.params(":id"));
            Account a = gson.fromJson(req.body(), Account.class);
            a.setAccountId(id);
            boolean updated = accountDao.updateAccount(a);
            return "{\"updated\":" + updated + "}";
        });

        delete("/accounts/:id", (req, res) -> {
            res.type("application/json");
            long id = Long.parseLong(req.params(":id"));
            boolean deleted = accountDao.deleteAccount(id);
            return "{\"deleted\":" + deleted + "}";
        });



        post("/transactions", (req, res) -> {
            res.type("application/json");
            try {
                Transaction txn = gson.fromJson(req.body(), Transaction.class);

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

        get("/transactions", (req, res) -> {
            res.type("application/json");
            return gson.toJson(transactionDao.getAllTransactions());
        });

        get("/transactions/:id", (req, res) -> {
            res.type("application/json");
            long id = Long.parseLong(req.params(":id"));
            Transaction txn = transactionDao.getTransactionById(id);
            return (txn != null) ? gson.toJson(txn) : "{}";
        });

        put("/transactions/:id", (req, res) -> {
            res.type("application/json");
            long id = Long.parseLong(req.params(":id"));
            Transaction txn = gson.fromJson(req.body(), Transaction.class);
            boolean updated = transactionDao.updateTransaction(id, txn);
            return "{\"updated\":" + updated + "}";
        });

        delete("/transactions/:id", (req, res) -> {
            res.type("application/json");
            long id = Long.parseLong(req.params(":id"));
            boolean deleted = transactionDao.deleteTransaction(id);
            return "{\"deleted\":" + deleted + "}";
        });

        System.out.println("Server running at http://localhost:8081");
    }
}