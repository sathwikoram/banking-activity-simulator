package com.banking.dao;

import com.banking.model.Account;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountDAOTest {

    private static AccountDAO accountDAO;
    private static long createdAccountId;

    @BeforeAll
    static void setup() {
        accountDAO = new AccountDAO();
    }

    @Test
    @Order(1)
    void testAddAccount() {
        Account acc = new Account();
        acc.setCustomerId(1L); //  Ensure a customer with ID=1 exists in DB
        acc.setAccountType("SAVINGS");
        acc.setBankName("Test Bank");
        acc.setBranch("Main Branch");
        acc.setBalance(new BigDecimal("10000.00"));
        acc.setStatus("ACTIVE");
        acc.setAccountNumber("ACC" + System.currentTimeMillis());
        acc.setIfscCode("TEST0001234");
        acc.setNameOnAccount("JUnit User");
        acc.setPhoneLinked("9876543210");
        acc.setSavingAmount(new BigDecimal("5000.00"));

        createdAccountId = accountDAO.addAccount(acc);

        System.out.println(" Created Account ID: " + createdAccountId);
        assertTrue(createdAccountId > 0, "Account ID should be generated");
    }

    @Test
    @Order(2)
    void testGetAccountById() {
        Account acc = accountDAO.getAccountById(createdAccountId);
        assertNotNull(acc, "Account should be found");
        assertEquals("SAVINGS", acc.getAccountType(), "Account type should match");
        assertEquals("Test Bank", acc.getBankName(), "Bank name should match");
    }

    @Test
    @Order(3)
    void testUpdateAccount() {
        Account acc = accountDAO.getAccountById(createdAccountId);
        acc.setAccountType("CURRENT");
        acc.setBalance(new BigDecimal("20000.00"));
        acc.setStatus("UPDATED");

        boolean updated = accountDAO.updateAccount(acc);
        assertTrue(updated, "Account should be updated");

        Account dbAcc = accountDAO.getAccountById(createdAccountId);
        assertEquals("CURRENT", dbAcc.getAccountType(), "Account type should be updated");
        assertEquals(new BigDecimal("20000.00"), dbAcc.getBalance(), "Balance should be updated");
    }

    @Test
    @Order(4)
    void testGetAllAccounts() {
        List<Account> accounts = accountDAO.getAllAccounts();
        assertNotNull(accounts, "Accounts list should not be null");
        assertTrue(accounts.size() > 0, "There should be at least one account in DB");
    }

    @Test
    @Order(5)
    void testDeleteAccount() {
        boolean deleted = accountDAO.deleteAccount(createdAccountId);
        assertTrue(deleted, "Account should be deleted");

        Account acc = accountDAO.getAccountById(createdAccountId);
        assertNull(acc, "Account should not exist after deletion");
    }
}
