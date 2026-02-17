package com.banking.service;

import com.banking.dao.AccountDAO;
import com.banking.dao.AccountDAOImpl;
import com.banking.dao.CustomerDAO;
import com.banking.dao.CustomerDAOImpl;
import com.banking.model.Account;
import com.banking.model.CurrentAccount;
import com.banking.model.Customer;
import com.banking.model.SavingsAccount;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service class to manage Account-related operations
 */
public class AccountService {

    private final AccountDAO accountDAO;
    private final CustomerDAO customerDAO;

    /**
     * Default constructor
     */
    public AccountService() {
        this.accountDAO = new AccountDAOImpl();
        this.customerDAO = new CustomerDAOImpl();
    }

    /**
     * Create a new savings account for a customer
     *
     * @param customerId Customer ID
     * @param initialDeposit Initial deposit amount
     * @param interestRate Interest rate
     * @return Created account
     * @throws Exception if operation fails
     */
    public SavingsAccount createSavingsAccount(int customerId, BigDecimal initialDeposit,
            BigDecimal interestRate) throws Exception {
        // Validate input
        if (initialDeposit.compareTo(SavingsAccount.getMinimumBalance()) < 0) {
            throw new IllegalArgumentException("Initial deposit must be at least "
                    + SavingsAccount.getMinimumBalance());
        }

        if (interestRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative");
        }

        // Check if customer exists
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found: " + customerId);
        }

        // Create and save account
        SavingsAccount account = new SavingsAccount();
        account.setCustomerId(customerId);
        account.setBalance(initialDeposit);
        account.setInterestRate(interestRate);

        return accountDAO.createSavingsAccount(account);
    }

    /**
     * Create a new current account for a customer
     *
     * @param customerId Customer ID
     * @param initialDeposit Initial deposit amount
     * @param overdraftLimit Overdraft limit
     * @return Created account
     * @throws Exception if operation fails
     */
    public CurrentAccount createCurrentAccount(int customerId, BigDecimal initialDeposit,
            BigDecimal overdraftLimit) throws Exception {
        // Validate input
        if (initialDeposit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial deposit cannot be negative");
        }

        if (overdraftLimit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Overdraft limit cannot be negative");
        }

        // Check if customer exists
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found: " + customerId);
        }

        // Create and save account
        CurrentAccount account = new CurrentAccount();
        account.setCustomerId(customerId);
        account.setBalance(initialDeposit);
        account.setOverdraftLimit(overdraftLimit);

        return accountDAO.createCurrentAccount(account);
    }

    /**
     * Get an account by ID
     *
     * @param accountId Account ID
     * @return Account if found, null otherwise
     * @throws Exception if operation fails
     */
    public Account getAccountById(int accountId) throws Exception {
        return accountDAO.getAccountById(accountId);
    }

    /**
     * Get all accounts for a customer
     *
     * @param customerId Customer ID
     * @return List of accounts
     * @throws Exception if operation fails
     */
    public List<Account> getAccountsByCustomerId(int customerId) throws Exception {
        return accountDAO.getAccountsByCustomerId(customerId);
    }

    /**
     * Update account status
     *
     * @param accountId Account ID
     * @param newStatus New status (ACTIVE, INACTIVE, FROZEN, CLOSED)
     * @return true if updated successfully, false otherwise
     * @throws Exception if operation fails
     */
    public boolean updateAccountStatus(int accountId, String newStatus) throws Exception {
        if (newStatus == null || (!newStatus.equals("ACTIVE") && !newStatus.equals("INACTIVE")
                && !newStatus.equals("FROZEN") && !newStatus.equals("CLOSED"))) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }

        Account account = accountDAO.getAccountById(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }

        return accountDAO.updateAccountStatus(accountId, newStatus);
    }

    /**
     * Close an account
     *
     * @param accountId Account ID
     * @return true if closed successfully, false otherwise
     * @throws Exception if operation fails
     */
    public boolean closeAccount(int accountId) throws Exception {
        Account account = accountDAO.getAccountById(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }

        // Check if account has zero balance
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Account must have zero balance before closing");
        }

        return accountDAO.updateAccountStatus(accountId, "CLOSED");
    }

    /**
     * Delete an account
     *
     * @param accountId Account ID
     * @return true if deleted successfully, false otherwise
     * @throws Exception if operation fails
     */
    public boolean deleteAccount(int accountId) throws Exception {
        return accountDAO.deleteAccount(accountId);
    }

    /**
     * Apply interest to all savings accounts
     *
     * @return Number of accounts updated
     * @throws Exception if operation fails
     */
    public int applyInterestToSavingsAccounts() throws Exception {
        List<Account> savingsAccounts = accountDAO.getAccountsByCustomerIdAndType(0, "SAVINGS");
        int updatedCount = 0;

        for (Account account : savingsAccounts) {
            if (account instanceof SavingsAccount && "ACTIVE".equals(account.getStatus())) {
                SavingsAccount savingsAccount = (SavingsAccount) account;
                BigDecimal interest = savingsAccount.calculateInterest();
                BigDecimal newBalance = savingsAccount.getBalance().add(interest);

                accountDAO.updateBalance(savingsAccount.getAccountId(), newBalance);
                updatedCount++;
            }
        }

        return updatedCount;
    }

    public void updateBalance(int accountId, BigDecimal newBalance) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateBalance'");
    }
}
