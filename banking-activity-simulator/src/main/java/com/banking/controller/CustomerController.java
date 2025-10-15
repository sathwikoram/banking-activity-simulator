package com.banking.controller;

import com.banking.dao.CustomerDAO;
import com.banking.model.Customer;
import com.google.gson.Gson;

import static spark.Spark.*;

public class CustomerController {

    private final CustomerDAO customerDAO;
    private final Gson gson;

    public CustomerController(CustomerDAO customerDAO, Gson gson) {
        this.customerDAO = customerDAO;
        this.gson = gson;
    }

    public void initRoutes() {


        get("/customers", (req, res) -> {
            res.type("application/json");
            return gson.toJson(customerDAO.getAllCustomers());
        });

        get("/customers/:id", (req, res) -> {
            res.type("application/json");
            long id = Long.parseLong(req.params(":id"));
            Customer c = customerDAO.getCustomerById(id);
            return (c != null) ? gson.toJson(c) : "{}";
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
                    return "{\"error\":\"Insert failed\"}";
                }
            } catch (Exception e) {
                res.status(500);
                return "{\"error\":\"" + e.getMessage() + "\"}";
            }
        });


        put("/customers/:id", (req, res) -> {
            res.type("application/json");
            long id = Long.parseLong(req.params(":id"));
            Customer c = gson.fromJson(req.body(), Customer.class);
            boolean updated = customerDAO.updateCustomer(id, c);
            return "{\"updated\":" + updated + "}";
        });

        delete("/customers/:id", (req, res) -> {
            res.type("application/json");
            long id = Long.parseLong(req.params(":id"));
            boolean deleted = customerDAO.deleteCustomer(id);
            return "{\"deleted\":" + deleted + "}";
        });
    }
}
