package com.banking.dao;

import java.math.BigDecimal;
import java.util.List;

import com.banking.model.Account;
import com.banking.model.CurrentAccount;
import com.banking.model.SavingsAccount;

/**
 * Data Access Object interface for Account entities
 */
public interface AccountDAO {

    /**
     * Create a new account in the database
     *
     * @param account Account object to add
     * @return Created account with generated ID
     * @throws Exception if database operation fails
     */
    Account createAccount(Account account) throws Exception;

    /**
     * Retrieve an account by ID
     *
     * @param accountId Account identifier
     * @return Account object if found, null otherwise
     * @throws Exception if database operation fails
     */
    Account getAccountById(int accountId) throws Exception;

    /**
     * Update an existing account
     *
     * @param account Account object with updated information
     * @return true if updated successfully, false otherwise
     * @throws Exception if database operation fails
     */
    boolean updateAccount(Account account) throws Exception;

    /**
     * Delete an account by ID
     *
     * @param accountId Account identifier
     * @return true if deleted successfully, false otherwise
     * @throws Exception if database operation fails
     */
    boolean deleteAccount(int accountId) throws Exception;

    /**
     * Get all accounts for a customer
     *
     * @param customerId Customer identifier
     * @return List of accounts
     * @throws Exception if database operation fails
     */
    List<Account> getAccountsByCustomerId(int customerId) throws Exception;

    /**
     * Get all accounts of a specific type for a customer
     *
     * @param customerId Customer identifier
     * @param accountType Type of accounts to retrieve (SAVINGS or CURRENT)
     * @return List of accounts of the specified type
     * @throws Exception if database operation fails
     */
    List<Account> getAccountsByCustomerIdAndType(int customerId, String accountType) throws Exception;

    /**
     * Update account balance
     *
     * @param accountId Account identifier
     * @param newBalance New balance
     * @return true if updated successfully, false otherwise
     * @throws Exception if database operation fails
     */
    boolean updateBalance(int accountId, BigDecimal newBalance) throws Exception;

    /**
     * Create a savings account
     *
     * @param savingsAccount SavingsAccount object
     * @return Created account with generated ID
     * @throws Exception if database operation fails
     */
    SavingsAccount createSavingsAccount(SavingsAccount savingsAccount) throws Exception;

    /**
     * Create a current account
     *
     * @param currentAccount CurrentAccount object
     * @return Created account with generated ID
     * @throws Exception if database operation fails
     */
    CurrentAccount createCurrentAccount(CurrentAccount currentAccount) throws Exception;

    /**
     * Get a savings account by ID
     *
     * @param accountId Account identifier
     * @return SavingsAccount object if found, null otherwise
     * @throws Exception if database operation fails
     */
    SavingsAccount getSavingsAccountById(int accountId) throws Exception;

    /**
     * Get a current account by ID
     *
     * @param accountId Account identifier
     * @return CurrentAccount object if found, null otherwise
     * @throws Exception if database operation fails
     */
    CurrentAccount getCurrentAccountById(int accountId) throws Exception;

    /**
     * Update account status
     *
     * @param accountId Account identifier
     * @param newStatus New status
     * @return true if updated successfully, false otherwise
     * @throws Exception if database operation fails
     */
    boolean updateAccountStatus(int accountId, String newStatus) throws Exception;
}
