package com.banking.service;

import java.util.List;

import com.banking.dao.CustomerDAO;
import com.banking.dao.CustomerDAOImpl;
import com.banking.model.Customer;

/**
 * Service class to manage Customer-related operations
 */
public class CustomerService {

    private final CustomerDAO customerDAO;

    /**
     * Default constructor
     */
    public CustomerService() {
        this.customerDAO = new CustomerDAOImpl();
    }

    /**
     * Register a new customer
     *
     * @param firstName First name
     * @param lastName Last name
     * @param email Email address
     * @param phone Phone number
     * @param address Address
     * @return Registered customer with ID
     * @throws Exception if registration fails
     */
    public Customer registerCustomer(String firstName, String lastName, String email,
            String phone, String address) throws Exception {
        // Validate input
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        // Check if email already exists
        Customer existingCustomer = customerDAO.getCustomerByEmail(email);
        if (existingCustomer != null) {
            throw new IllegalArgumentException("Email is already registered");
        }

        // Create and save customer
        Customer customer = new Customer(firstName, lastName, email);
        customer.setPhone(phone);
        customer.setAddress(address);

        return customerDAO.createCustomer(customer);
    }

    /**
     * Get customer by ID
     *
     * @param customerId Customer ID
     * @return Customer if found, null otherwise
     * @throws Exception if operation fails
     */
    public Customer getCustomerById(int customerId) throws Exception {
        return customerDAO.getCustomerById(customerId);
    }

    /**
     * Get customer by email
     *
     * @param email Email address
     * @return Customer if found, null otherwise
     * @throws Exception if operation fails
     */
    public Customer getCustomerByEmail(String email) throws Exception {
        return customerDAO.getCustomerByEmail(email);
    }

    /**
     * Update customer information
     *
     * @param customer Customer with updated information
     * @return true if updated successfully, false otherwise
     * @throws Exception if operation fails
     */
    public boolean updateCustomer(Customer customer) throws Exception {
        // Validate input
        if (customer.getFirstName() == null || customer.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }

        if (customer.getLastName() == null || customer.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }

        if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        // Check if another customer already has this email
        Customer existingCustomer = customerDAO.getCustomerByEmail(customer.getEmail());
        if (existingCustomer != null && existingCustomer.getCustomerId() != customer.getCustomerId()) {
            throw new IllegalArgumentException("Email is already used by another customer");
        }

        return customerDAO.updateCustomer(customer);
    }

    /**
     * Update customer status
     *
     * @param customerId Customer ID
     * @param newStatus New status (ACTIVE, INACTIVE, BLOCKED)
     * @return true if updated successfully, false otherwise
     * @throws Exception if operation fails
     */
    public boolean updateCustomerStatus(int customerId, String newStatus) throws Exception {
        if (newStatus == null || (!newStatus.equals("ACTIVE") && !newStatus.equals("INACTIVE") && !newStatus.equals("BLOCKED"))) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }

        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found: " + customerId);
        }

        customer.setStatus(newStatus);
        return customerDAO.updateCustomer(customer);
    }

    /**
     * Delete a customer and all associated accounts
     *
     * @param customerId Customer ID
     * @return true if deleted successfully, false otherwise
     * @throws Exception if operation fails
     */
    public boolean deleteCustomer(int customerId) throws Exception {
        return customerDAO.deleteCustomer(customerId);
    }

    /**
     * Get all customers
     *
     * @return List of all customers
     * @throws Exception if operation fails
     */
    public List<Customer> getAllCustomers() throws Exception {
        return customerDAO.getAllCustomers();

    }
}
