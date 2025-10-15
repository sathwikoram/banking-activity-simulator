package com.banking.service;

import com.banking.dao.CustomerDAO;
import com.banking.model.Customer;

import java.util.List;


public class CustomerService {

    private final CustomerDAO customerDAO;

    public CustomerService() {
        this.customerDAO = new CustomerDAO();
    }


    public long createCustomer(Customer customer) {
        return customerDAO.addCustomer(customer); // calls DAO.addCustomer(...)
    }

    public Customer getCustomerById(long id) {
        return customerDAO.getCustomerById(id);
    }

    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }


    public boolean updateCustomer(long id, Customer customer) {
        return customerDAO.updateCustomer(id, customer); // calls DAO.updateCustomer(long, Customer)
    }

    public boolean deleteCustomer(long id) {
        return customerDAO.deleteCustomer(id);
    }
}
