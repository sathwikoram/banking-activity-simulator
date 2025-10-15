package com.banking.service;

import com.banking.dao.AccountDAO;
import com.banking.model.Account;

import java.util.List;

public class AccountService {

    private final AccountDAO accountDAO;

    public AccountService() {
        this.accountDAO = new AccountDAO();
    }

    public long createAccount(Account account) {
        return accountDAO.addAccount(account);
    }

    public Account getAccountById(long id) {
        return accountDAO.getAccountById(id);
    }

    public List<Account> getAllAccounts() {
        return accountDAO.getAllAccounts();
    }

    public boolean updateAccount(Account account) {
        return accountDAO.updateAccount(account);
    }

    public boolean deleteAccount(long id) {
        return accountDAO.deleteAccount(id);
    }
}