package com.banking.dao;

import com.banking.model.Customer;
import org.junit.jupiter.api.*;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerDAOTest {

    private static CustomerDAO customerDAO;
    private static long createdCustomerId;
    private static String uniqueAadhar;
    private static String uniqueUsername;
    private static String uniqueEmail;

    @BeforeAll
    static void setup() {
        customerDAO = new CustomerDAO();
        long ts = System.currentTimeMillis();
        uniqueAadhar = "99999" + ts;
        uniqueUsername = "testuser_" + ts;
        uniqueEmail = "testuser_" + ts + "@example.com";
    }

    @Test
    @Order(1)
    void testAddCustomer() {
        Customer c = new Customer();
        c.setUsername(uniqueUsername);
        c.setPassword("secret123");
        c.setAadharNumber(uniqueAadhar);
        c.setEmail(uniqueEmail);
        c.setPhoneNumber("9876543210");
        c.setDob(new java.sql.Date(new Date().getTime()));
        c.setGender("MALE");
        c.setPermanentAddress("123 Test Address");
        c.setState("Test State");
        c.setCountry("Test Country");
        c.setCity("Test City");
        c.setAge(30);
        c.setFatherName("Test Father");
        c.setMotherName("Test Mother");

        createdCustomerId = customerDAO.addCustomer(c);
        assertTrue(createdCustomerId > 0, "Customer ID should be generated");
    }

    @Test
    @Order(2)
    void testGetCustomerById() {
        Customer c = customerDAO.getCustomerById(createdCustomerId);
        assertNotNull(c, "Customer should be found");
        assertEquals(uniqueUsername, c.getUsername(), "Username should match the inserted one");
    }

    @Test
    @Order(3)
    void testUpdateCustomer() {
        Customer updated = new Customer();
        updated.setUsername("updateduser_" + System.currentTimeMillis());
        updated.setPassword("newpassword");
        updated.setAadharNumber(uniqueAadhar);
        updated.setPermanentAddress("Test Address");
        updated.setState("Test State");
        updated.setCountry("India");
        updated.setCity("Test City");
        updated.setEmail("updated_" + System.currentTimeMillis() + "@example.com");
        updated.setPhoneNumber("1112223333");
        updated.setStatus("ACTIVE");
        updated.setDob(new java.sql.Date(new Date().getTime()));
        updated.setAge(25);
        updated.setGender("MALE");
        updated.setFatherName("Test Father");
        updated.setMotherName("Test Mother");

        boolean result = customerDAO.updateCustomer(createdCustomerId, updated);
        assertTrue(result, "Customer should be updated");

        Customer dbCustomer = customerDAO.getCustomerById(createdCustomerId);
        assertEquals(updated.getUsername(), dbCustomer.getUsername(), "Username should be updated");
    }

    @Test
    @Order(4)
    void testGetAllCustomers() {
        List<Customer> customers = customerDAO.getAllCustomers();
        assertNotNull(customers, "Customer list should not be null");
        assertTrue(customers.size() > 0, "There should be at least one customer");
    }

    @Test
    @Order(5)
    void testDeleteCustomer() {
        boolean deleted = customerDAO.deleteCustomer(createdCustomerId);
        assertTrue(deleted, "Customer should be deleted");

        Customer c = customerDAO.getCustomerById(createdCustomerId);
        assertNull(c, "Customer should not exist after deletion");
    }
}