package com.banking.dao;

import com.banking.model.Account;
import com.banking.model.AccountEmailDetails;
import com.banking.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    private String lastError;

    public long addAccount(Account acc) {
        this.lastError = null;
        String sql = "INSERT INTO accounts (customer_id, account_type, bank_name, branch, balance, status, account_number, ifsc_code, name_on_account, phone_linked, saving_amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, acc.getCustomerId());
            ps.setString(2, acc.getAccountType());
            ps.setString(3, acc.getBankName());
            ps.setString(4, acc.getBranch());
            ps.setObject(5, acc.getBalance());
            ps.setString(6, acc.getStatus());
            ps.setString(7, acc.getAccountNumber());
            ps.setObject(8, acc.getIfscCode());
            ps.setObject(9, acc.getNameOnAccount());
            ps.setObject(10, acc.getPhoneLinked());
            ps.setObject(11, acc.getSavingAmount());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) return rs.getLong(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.lastError = e.getMessage();
        }
        return -1;
    }

    public Account getAccountById(long id) {
        this.lastError = null;
        String sql = "SELECT * FROM accounts WHERE account_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapAccount(rs);
        } catch (Exception e) {
            e.printStackTrace();
            this.lastError = e.getMessage();
        }
        return null;
    }

    public List<Account> getAllAccounts() {
        this.lastError = null;
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapAccount(rs));
        } catch (Exception e) {
            e.printStackTrace();
            this.lastError = e.getMessage();
        }
        return list;
    }

    // NEW: Method to get account ID by account number
    public long getAccountIdByNumber(String accountNumber) {
        this.lastError = null;
        String sql = "SELECT account_id FROM accounts WHERE account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("account_id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.lastError = e.getMessage();
        }
        return -1;
    }

    public String getCustomerEmailByAccountId(long accountId) {
        this.lastError = null;
        String sql = "SELECT c.email FROM customers c JOIN accounts a ON c.customer_id = a.customer_id WHERE a.account_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("email");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.lastError = e.getMessage();
        }
        return null;
    }

    public long getAccountIdByCustomerId(long customerId) {
        this.lastError = null;
        String sql = "SELECT account_id FROM accounts WHERE customer_id = ? ORDER BY created_at ASC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("account_id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.lastError = e.getMessage();
        }
        return -1;
    }

    public AccountEmailDetails getAccountDetailsForEmail(long accountId) {
        String sql = "SELECT c.username, c.email, a.account_number, a.balance FROM customers c JOIN accounts a ON c.customer_id = a.customer_id WHERE a.account_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AccountEmailDetails(
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("account_number"),
                            rs.getBigDecimal("balance")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateAccount(Account acc) {
        this.lastError = null;
        String sql = "UPDATE accounts SET account_type=?, bank_name=?, branch=?, balance=?, status=?, account_number=?, ifsc_code=?, name_on_account=?, phone_linked=?, saving_amount=? WHERE account_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, acc.getAccountType());
            ps.setString(2, acc.getBankName());
            ps.setString(3, acc.getBranch());
            ps.setBigDecimal(4, acc.getBalance());
            ps.setString(5, acc.getStatus());
            ps.setString(6, acc.getAccountNumber());
            ps.setString(7, acc.getIfscCode());
            ps.setString(8, acc.getNameOnAccount());
            ps.setString(9, acc.getPhoneLinked());
            ps.setBigDecimal(10, acc.getSavingAmount());
            ps.setLong(11, acc.getAccountId());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            this.lastError = e.getMessage();
        }
        return false;
    }

    public boolean deleteAccount(long id) {
        this.lastError = null;
        String sql = "DELETE FROM accounts WHERE account_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            this.lastError = e.getMessage();
        }
        return false;
    }

    private Account mapAccount(ResultSet rs) throws SQLException {
        Account acc = new Account();
        acc.setAccountId(rs.getLong("account_id"));
        acc.setCustomerId(rs.getLong("customer_id"));
        acc.setAccountType(rs.getString("account_type"));
        acc.setBankName(rs.getString("bank_name"));
        acc.setBranch(rs.getString("branch"));
        acc.setBalance(rs.getBigDecimal("balance"));
        acc.setStatus(rs.getString("status"));
        acc.setCreatedAt(rs.getTimestamp("created_at"));
        acc.setModifiedAt(rs.getTimestamp("modified_at"));
        acc.setAccountNumber(rs.getString("account_number"));
        acc.setIfscCode(rs.getString("ifsc_code"));
        acc.setNameOnAccount(rs.getString("name_on_account"));
        acc.setPhoneLinked(rs.getString("phone_linked"));
        acc.setSavingAmount(rs.getBigDecimal("saving_amount"));
        return acc;
    }

    public String getLastError() {
        return this.lastError;
    }
}