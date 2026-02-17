package com.banking.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.banking.model.Transaction;

/**
 * Data Access Object interface for Transaction entities
 */
public interface TransactionDAO {

    /**
     * Create a new transaction in the database
     *
     * @param transaction Transaction object to add
     * @return Created transaction with generated ID
     * @throws Exception if database operation fails
     */ Transaction createTransaction(Transaction transaction) throws Exception;

    /**
     * Retrieve a transaction by ID
     *
     * @param transactionId Transaction identifier
     * @return Transaction object if found, null otherwise
     * @throws Exception if database operation fails
     */
    Transaction getTransactionById(long transactionId) throws Exception;

    /**
     * Get all transactions for an account
     *
     * @param accountId Account identifier
     * @return List of transactions
     * @throws Exception if database operation fails
     */
    List<Transaction> getTransactionsByAccountId(int accountId) throws Exception;

    /**
     * Get transactions for an account within a date range
     *
     * @param accountId Account identifier
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of transactions within the date range
     * @throws Exception if database operation fails
     */
    List<Transaction> getTransactionsByAccountIdAndDateRange(int accountId,
            LocalDateTime startDate,
            LocalDateTime endDate) throws Exception;

    /**
     * Get transactions by type for an account
     *
     * @param accountId Account identifier
     * @param transactionType Type of transaction
     * @return List of transactions of the specified type
     * @throws Exception if database operation fails
     */
    List<Transaction> getTransactionsByAccountIdAndType(int accountId, String transactionType) throws Exception;

    /**
     * Get all transfer transactions between two accounts
     *
     * @param accountId1 First account identifier
     * @param accountId2 Second account identifier
     * @return List of transfer transactions between the accounts
     * @throws Exception if database operation fails
     */
    List<Transaction> getTransfersBetweenAccounts(int accountId1, int accountId2) throws Exception;

    /**
     * Create a deposit transaction and update account balance
     *
     * @param accountId Account identifier
     * @param amount Amount to deposit
     * @param description Transaction description
     * @return Created transaction with generated ID
     * @throws Exception if database operation fails
     */
    Transaction createDepositTransaction(int accountId, BigDecimal amount, String description) throws Exception;

    /**
     * Create a withdrawal transaction and update account balance
     *
     * @param accountId Account identifier
     * @param amount Amount to withdraw
     * @param description Transaction description
     * @return Created transaction with generated ID
     * @throws Exception if database operation fails
     */
    Transaction createWithdrawalTransaction(int accountId, BigDecimal amount, String description) throws Exception;

    /**
     * Create a transfer transaction between accounts and update both balances
     *
     * @param fromAccountId Source account identifier
     * @param toAccountId Destination account identifier
     * @param amount Amount to transfer
     * @param description Transaction description
     * @return Array of created transactions (outgoing and incoming)
     * @throws Exception if database operation fails
     */
    Transaction[] createTransferTransaction(int fromAccountId, int toAccountId,
            BigDecimal amount, String description) throws Exception;

    /**
     * Delete a transaction by ID
     *
     * @param transactionId Transaction identifier
     * @return true if deleted successfully, false otherwise
     * @throws Exception if database operation fails
     */
    boolean deleteTransaction(long transactionId) throws Exception;
}
