package com.banking.controller;

import com.banking.dao.AccountDAO;
import com.banking.model.Account;
import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class AccountController extends HttpServlet {
    private AccountDAO dao = new AccountDAO();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Create new account
        Account acc = gson.fromJson(req.getReader(), Account.class);
        long id = dao.addAccount(acc);
        resp.setContentType("application/json");
        resp.getWriter().write("{\"account_id\": " + id + "}");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");
        resp.setContentType("application/json");

        if (idParam != null) {
            long id = Long.parseLong(idParam);
            Account acc = dao.getAccountById(id);
            resp.getWriter().write(gson.toJson(acc));
        } else {
            List<Account> accounts = dao.getAllAccounts();
            resp.getWriter().write(gson.toJson(accounts));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Account acc = gson.fromJson(req.getReader(), Account.class);
        boolean updated = dao.updateAccount(acc);
        resp.setContentType("application/json");
        resp.getWriter().write("{\"updated\": " + updated + "}");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");
        boolean deleted = dao.deleteAccount(Long.parseLong(idParam));
        resp.setContentType("application/json");
        resp.getWriter().write("{\"deleted\": " + deleted + "}");
    }
}
