package com.banking.dao;

import java.util.List;

import com.banking.model.Customer;

/**
 * Data Access Object interface for Customer entities
 */
public interface CustomerDAO {

    /**
     * Create a new customer in the database
     *
     * @param customer Customer object to add
     * @return Created customer with generated ID
     * @throws Exception if database operation fails
     */
    Customer createCustomer(Customer customer) throws Exception;

    /**
     * Retrieve a customer by ID
     *
     * @param customerId Customer identifier
     * @return Customer object if found, null otherwise
     * @throws Exception if database operation fails
     */
    Customer getCustomerById(int customerId) throws Exception;

    /**
     * Retrieve a customer by email
     *
     * @param email Customer email
     * @return Customer object if found, null otherwise
     * @throws Exception if database operation fails
     */
    Customer getCustomerByEmail(String email) throws Exception;

    /**
     * Update an existing customer
     *
     * @param customer Customer object with updated information
     * @return true if updated successfully, false otherwise
     * @throws Exception if database operation fails
     */
    boolean updateCustomer(Customer customer) throws Exception;

    /**
     * Delete a customer by ID
     *
     * @param customerId Customer identifier
     * @return true if deleted successfully, false otherwise
     * @throws Exception if database operation fails
     */
    boolean deleteCustomer(int customerId) throws Exception;

    /**
     * Get all customers
     *
     * @return List of all customers
     * @throws Exception if database operation fails
     */
    List<Customer> getAllCustomers() throws Exception;

    /**
     * Search for customers by name (first or last)
     *
     * @param name Name to search for
     * @return List of matching customers
     * @throws Exception if database operation fails
     */
    List<Customer> searchCustomersByName(String name) throws Exception;
}
