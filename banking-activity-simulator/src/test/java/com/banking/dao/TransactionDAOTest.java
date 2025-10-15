package com.banking.dao;

import com.banking.model.Transaction;
import org.junit.jupiter.api.*;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionDAOTest {

    private static TransactionDAO transactionDAO;
    private static long createdTransactionId;
    private static String generatedUTR;

    @BeforeAll
    static void setup() {
        transactionDAO = new TransactionDAO();
    }

    @Test
    @Order(1)
    void testAddTransaction() {
        Transaction txn = new Transaction();
        generatedUTR = "UTR" + System.currentTimeMillis();
        txn.setUtrNumber(generatedUTR);
        txn.setTransactionAmount(5000.00);
        txn.setDebitedDate(new Timestamp(System.currentTimeMillis()));
        txn.setAccountId(1L);
        txn.setBalanceAfterTxn(15000.00);
        txn.setDescription("JUnit Test Deposit");
        txn.setModifiedBy("JUnitUser");
        txn.setReceiver("Receiver1");
        txn.setTransactionType("DEPOSIT");
        txn.setModeOfTransaction("UPI");
        txn.setBankBranch("Main Branch");

        createdTransactionId = transactionDAO.addTransaction(txn, txn.getBalanceAfterTxn());

        assertTrue(createdTransactionId > 0, "Transaction should be inserted");
    }

    @Test
    @Order(2)
    void testGetTransactionById() {
        Transaction txn = transactionDAO.getTransactionById(createdTransactionId);
        assertNotNull(txn, "Transaction should not be null");
        assertEquals(generatedUTR, txn.getUtrNumber());
    }

    @Test
    @Order(3)
    void testGetAllTransactions() {
        List<Transaction> list = transactionDAO.getAllTransactions();
        assertFalse(list.isEmpty(), "Transaction list should not be empty");
    }
}