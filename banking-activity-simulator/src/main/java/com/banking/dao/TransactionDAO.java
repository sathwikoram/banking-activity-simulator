package com.banking.dao;

import com.banking.model.Transaction;
import com.banking.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionDAO {

    public long addTransaction(Transaction t, double finalBalance) {
        String insertTxnSQL = "INSERT INTO transactions " +
                "(utr_number, transaction_amount, debited_date, account_id, balance_after_txn, description, modified_by, receiver, transaction_type, mode_of_transaction, bank_branch) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertTxnSQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, t.getUtrNumber() != null ? t.getUtrNumber() : UUID.randomUUID().toString());
            ps.setDouble(2, t.getTransactionAmount());
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setLong(4, t.getAccountId());
            ps.setDouble(5, finalBalance);
            ps.setString(6, t.getDescription());
            ps.setString(7, t.getModifiedBy());
            ps.setString(8, t.getReceiver());
            ps.setString(9, t.getTransactionType());
            ps.setString(10, t.getModeOfTransaction());
            ps.setString(11, t.getBankBranch());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean transferAmount(long senderId, long receiverId, double amount, String description) {
        String getBalanceSQL = "SELECT balance FROM accounts WHERE account_id = ?";
        String updateBalanceSQL = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        String insertTxnSQL = "INSERT INTO transactions " +
                "(utr_number, transaction_amount, debited_date, account_id, balance_after_txn, description, transaction_type, mode_of_transaction, receiver) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            double senderBalance, receiverBalance;

            try (PreparedStatement ps = conn.prepareStatement(getBalanceSQL)) {
                ps.setLong(1, senderId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) senderBalance = rs.getDouble("balance");
                    else throw new SQLException("Sender not found");
                }
            }

            if (senderBalance < amount) {
                throw new SQLException("Insufficient funds in sender account");
            }

            try (PreparedStatement ps = conn.prepareStatement(getBalanceSQL)) {
                ps.setLong(1, receiverId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) receiverBalance = rs.getDouble("balance");
                    else throw new SQLException("Receiver not found");
                }
            }

            double newSenderBalance = senderBalance - amount;
            try (PreparedStatement ps = conn.prepareStatement(updateBalanceSQL)) {
                ps.setDouble(1, newSenderBalance);
                ps.setLong(2, senderId);
                ps.executeUpdate();
            }

            double newReceiverBalance = receiverBalance + amount;
            try (PreparedStatement ps = conn.prepareStatement(updateBalanceSQL)) {
                ps.setDouble(1, newReceiverBalance);
                ps.setLong(2, receiverId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(insertTxnSQL)) {
                ps.setString(1, UUID.randomUUID().toString());
                ps.setDouble(2, amount);
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                ps.setLong(4, senderId);
                ps.setDouble(5, newSenderBalance);
                ps.setString(6, description);
                ps.setString(7, "TRANSFER");
                ps.setString(8, "NET_BANKING");
                ps.setString(9, String.valueOf(receiverId));
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(insertTxnSQL)) {
                ps.setString(1, UUID.randomUUID().toString());
                ps.setDouble(2, amount);
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                ps.setLong(4, receiverId);
                ps.setDouble(5, newReceiverBalance);
                ps.setString(6, description);
                ps.setString(7, "DEPOSIT");
                ps.setString(8, "NET_BANKING");
                ps.setString(9, String.valueOf(senderId));
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        String query = "SELECT * FROM transactions";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(mapTransaction(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Transaction getTransactionById(long id) {
        String query = "SELECT * FROM transactions WHERE transaction_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapTransaction(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateTransaction(long id, Transaction t) {
        String query = "UPDATE transactions SET utr_number=?, transaction_amount=?, debited_date=?, account_id=?, balance_after_txn=?, description=?, modified_by=?, receiver=?, transaction_type=?, mode_of_transaction=?, bank_branch=? WHERE transaction_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, t.getUtrNumber());
            ps.setDouble(2, t.getTransactionAmount());
            if (t.getDebitedDate() != null) {
                ps.setTimestamp(3, t.getDebitedDate());
            } else {
                ps.setNull(3, Types.TIMESTAMP);
            }
            ps.setLong(4, t.getAccountId());
            ps.setDouble(5, t.getBalanceAfterTxn());
            ps.setString(6, t.getDescription());
            ps.setString(7, t.getModifiedBy());
            ps.setString(8, t.getReceiver());
            ps.setString(9, t.getTransactionType());
            ps.setString(10, t.getModeOfTransaction());
            ps.setString(11, t.getBankBranch());
            ps.setLong(12, id);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteTransaction(long id) {
        String query = "DELETE FROM transactions WHERE transaction_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Transaction mapTransaction(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setTransactionId(rs.getLong("transaction_id"));
        t.setUtrNumber(rs.getString("utr_number"));
        t.setTransactionDate(rs.getTimestamp("transaction_date"));
        t.setTransactionAmount(rs.getDouble("transaction_amount"));
        t.setDebitedDate(rs.getTimestamp("debited_date"));
        t.setAccountId(rs.getLong("account_id"));
        t.setBalanceAfterTxn(rs.getDouble("balance_after_txn"));
        t.setDescription(rs.getString("description"));
        t.setModifiedBy(rs.getString("modified_by"));
        t.setReceiver(rs.getString("receiver"));
        t.setTransactionType(rs.getString("transaction_type"));
        t.setModeOfTransaction(rs.getString("mode_of_transaction"));
        t.setBankBranch(rs.getString("bank_branch"));
        return t;
    }
}