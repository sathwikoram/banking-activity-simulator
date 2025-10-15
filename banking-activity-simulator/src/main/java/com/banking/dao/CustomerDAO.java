package com.banking.dao;

import com.banking.model.Customer;
import com.banking.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    private String lastError;

    // Get all customers
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM customers";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                customers.add(mapCustomer(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.lastError = e.getMessage();
        }
        return customers;
    }

    // Get one customer by ID
    public Customer getCustomerById(long id) {
        this.lastError = null;
        String query = "SELECT * FROM customers WHERE customer_id=?";
        Customer c = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                c = mapCustomer(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.lastError = e.getMessage();
        }
        return c;
    }

    // Add new customer and return generated ID
    public long addCustomer(Customer c) {
        this.lastError = null;
        String query = "INSERT INTO customers " +
                "(username, password, aadhar_number, permanent_address, state, country, city, email, phone_number, " +
                "status, dob, age, gender, father_name, mother_name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getUsername());
            ps.setString(2, c.getPassword());
            ps.setString(3, c.getAadharNumber());

            ps.setObject(4, c.getPermanentAddress());
            ps.setObject(5, c.getState());
            ps.setObject(6, c.getCountry());
            ps.setObject(7, c.getCity());
            ps.setObject(8, c.getEmail());
            ps.setObject(9, c.getPhoneNumber());
            ps.setObject(10, c.getStatus());

            if (c.getDob() != null) {
                ps.setDate(11, new java.sql.Date(c.getDob().getTime()));
            } else {
                ps.setNull(11, Types.DATE);
            }

            if (c.getAge() != null) {
                ps.setInt(12, c.getAge());
            } else {
                ps.setNull(12, Types.INTEGER);
            }

            ps.setObject(13, c.getGender());
            ps.setObject(14, c.getFatherName());
            ps.setObject(15, c.getMotherName());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.lastError = e.getMessage();
        }
        return -1;
    }

    // Update existing customer
    public boolean updateCustomer(long id, Customer c) {
        this.lastError = null;
        String query = "UPDATE customers SET username=?, password=?, aadhar_number=?, permanent_address=?, " +
                "state=?, country=?, city=?, email=?, phone_number=?, status=?, dob=?, age=?, gender=?, " +
                "father_name=?, mother_name=?, modified_on=NOW() WHERE customer_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, c.getUsername());
            ps.setString(2, c.getPassword());
            ps.setString(3, c.getAadharNumber());

            ps.setObject(4, c.getPermanentAddress());
            ps.setObject(5, c.getState());
            ps.setObject(6, c.getCountry());
            ps.setObject(7, c.getCity());
            ps.setObject(8, c.getEmail());
            ps.setObject(9, c.getPhoneNumber());
            ps.setObject(10, c.getStatus());

            if (c.getDob() != null) {
                ps.setDate(11, new java.sql.Date(c.getDob().getTime()));
            } else {
                ps.setNull(11, Types.DATE);
            }

            if (c.getAge() != null) {
                ps.setInt(12, c.getAge());
            } else {
                ps.setNull(12, Types.INTEGER);
            }

            ps.setObject(13, c.getGender());
            ps.setObject(14, c.getFatherName());
            ps.setObject(15, c.getMotherName());
            ps.setLong(16, id);

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            this.lastError = e.getMessage();
        }
        return false;
    }

    // Delete customer by ID
    public boolean deleteCustomer(long id) {
        this.lastError = null;
        String query = "DELETE FROM customers WHERE customer_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setLong(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            this.lastError = e.getMessage();
        }
        return false;
    }

    // Helper method to map ResultSet to Customer object
    private Customer mapCustomer(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getLong("customer_id"));
        c.setUsername(rs.getString("username"));
        c.setPermanentAddress(rs.getString("permanent_address"));
        c.setState(rs.getString("state"));
        c.setCountry(rs.getString("country"));
        c.setCity(rs.getString("city"));
        c.setEmail(rs.getString("email"));
        c.setPhoneNumber(rs.getString("phone_number"));
        c.setStatus(rs.getString("status"));
        c.setDob(rs.getDate("dob"));
        c.setAge(rs.getInt("age"));
        c.setGender(rs.getString("gender"));
        c.setFatherName(rs.getString("father_name"));
        c.setMotherName(rs.getString("mother_name"));
        c.setCreatedOn(rs.getTimestamp("created_on"));
        c.setModifiedOn(rs.getTimestamp("modified_on"));
        return c;
    }

    public String getLastError() {
        return this.lastError;
    }
}