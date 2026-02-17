package com.banking.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.banking.dao.AccountDAO;
import com.banking.dao.AccountDAOImpl;
import com.banking.dao.TransactionDAO;
import com.banking.dao.TransactionDAOImpl;
import com.banking.model.Account;
import com.banking.model.Transaction;

/**
 * Service class to manage Transaction-related operations
 */
public class TransactionService {

    private final TransactionDAO transactionDAO;
    private final AccountDAO accountDAO;

    /**
     * Default constructor
     */
    public TransactionService() {
        this.transactionDAO = new TransactionDAOImpl();
        this.accountDAO = new AccountDAOImpl();
    }

    /**
     * Make a deposit to an account
     *
     * @param accountId Account ID
     * @param amount Amount to deposit
     * @param description Transaction description
     * @return Transaction record
     * @throws Exception if operation fails
     */
    public Transaction deposit(int accountId, BigDecimal amount, String description) throws Exception {
        // Validate input
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        // Check if account exists and is active
        Account account = accountDAO.getAccountById(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }

        if (!"ACTIVE".equals(account.getStatus())) {
            throw new IllegalStateException("Cannot deposit to a non-active account");
        }

        // Create deposit transaction
        return transactionDAO.createDepositTransaction(accountId, amount, description);
    }

    /**
     * Make a withdrawal from an account
     *
     * @param accountId Account ID
     * @param amount Amount to withdraw
     * @param description Transaction description
     * @return Transaction record
     * @throws Exception if operation fails
     */
    public Transaction withdraw(int accountId, BigDecimal amount, String description) throws Exception {
        // Validate input
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        // Check if account exists and is active
        Account account = accountDAO.getAccountById(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }

        if (!"ACTIVE".equals(account.getStatus())) {
            throw new IllegalStateException("Cannot withdraw from a non-active account");
        }

        // Create withdrawal transaction
        return transactionDAO.createWithdrawalTransaction(accountId, amount, description);
    }

    /**
     * Transfer money between accounts
     *
     * @param fromAccountId Source account ID
     * @param toAccountId Destination account ID
     * @param amount Amount to transfer
     * @param description Transaction description
     * @return Array of transaction records (outgoing and incoming)
     * @throws Exception if operation fails
     */
    public Transaction[] transfer(int fromAccountId, int toAccountId, BigDecimal amount, String description) throws Exception {
        // Validate input
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        if (fromAccountId == toAccountId) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        // Check if accounts exist and are active
        Account fromAccount = accountDAO.getAccountById(fromAccountId);
        if (fromAccount == null) {
            throw new IllegalArgumentException("Source account not found: " + fromAccountId);
        }

        Account toAccount = accountDAO.getAccountById(toAccountId);
        if (toAccount == null) {
            throw new IllegalArgumentException("Destination account not found: " + toAccountId);
        }

        if (!"ACTIVE".equals(fromAccount.getStatus())) {
            throw new IllegalStateException("Cannot transfer from a non-active account");
        }

        if (!"ACTIVE".equals(toAccount.getStatus())) {
            throw new IllegalStateException("Cannot transfer to a non-active account");
        }

        // Create transfer transaction
        return transactionDAO.createTransferTransaction(fromAccountId, toAccountId, amount, description);
    }

    /**
     * Get transaction by ID
     *
     * @param transactionId Transaction ID
     * @return Transaction if found, null otherwise
     * @throws Exception if operation fails
     */
    public Transaction getTransactionById(long transactionId) throws Exception {
        return transactionDAO.getTransactionById(transactionId);
    }

    /**
     * Get all transactions for an account
     *
     * @param accountId Account ID
     * @return List of transactions
     * @throws Exception if operation fails
     */
    public List<Transaction> getTransactionsByAccountId(int accountId) throws Exception {
        // Check if account exists
        Account account = accountDAO.getAccountById(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }

        return transactionDAO.getTransactionsByAccountId(accountId);
    }

    /**
     * Get transactions for an account within a date range
     *
     * @param accountId Account ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of transactions within the date range
     * @throws Exception if operation fails
     */
    public List<Transaction> getTransactionsByDateRange(int accountId, LocalDateTime startDate,
            LocalDateTime endDate) throws Exception {
        // Check if account exists
        Account account = accountDAO.getAccountById(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }

        return transactionDAO.getTransactionsByAccountIdAndDateRange(accountId, startDate, endDate);
    }

    /**
     * Get specific type of transactions for an account
     *
     * @param accountId Account ID
     * @param transactionType Transaction type (DEPOSIT, WITHDRAWAL,
     * TRANSFER_IN, TRANSFER_OUT)
     * @return List of transactions of the specified type
     * @throws Exception if operation fails
     */
    public List<Transaction> getTransactionsByType(int accountId, String transactionType) throws Exception {
        // Check if account exists
        Account account = accountDAO.getAccountById(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }

        // Validate transaction type
        if (!transactionType.equals("DEPOSIT") && !transactionType.equals("WITHDRAWAL")
                && !transactionType.equals("TRANSFER_IN") && !transactionType.equals("TRANSFER_OUT")) {
            throw new IllegalArgumentException("Invalid transaction type: " + transactionType);
        }

        return transactionDAO.getTransactionsByAccountIdAndType(accountId, transactionType);
    }
}
