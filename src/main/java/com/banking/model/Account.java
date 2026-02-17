package com.banking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Base Account class that serves as a parent class for specific account types
 */
public abstract class Account {

    protected int accountId;
    protected int customerId;
    protected String accountType;
    protected BigDecimal balance;
    protected LocalDateTime dateOpened;
    protected String status;

    /**
     * Default constructor
     */
    public Account() {
        this.balance = BigDecimal.ZERO;
        this.dateOpened = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    /**
     * Constructor with parameters
     *
     * @param accountId Account identifier
     * @param customerId Customer identifier
     * @param balance Initial balance
     */
    public Account(int accountId, int customerId, BigDecimal balance) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.balance = balance;
        this.dateOpened = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    /**
     * Full constructor
     *
     * @param accountId Account identifier
     * @param customerId Customer identifier
     * @param balance Initial balance
     * @param dateOpened Opening date
     * @param status Account status
     */
    public Account(int accountId, int customerId, BigDecimal balance,
            LocalDateTime dateOpened, String status) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.balance = balance;
        this.dateOpened = dateOpened;
        this.status = status;
    }

    /**
     * Deposit money into the account
     *
     * @param amount Amount to deposit
     * @return true if successful, false otherwise
     * @throws IllegalArgumentException if amount is negative
     */
    public boolean deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        if (!"ACTIVE".equals(status)) {
            return false;
        }

        balance = balance.add(amount);
        return true;
    }

    /**
     * Withdraw money from the account
     *
     * @param amount Amount to withdraw
     * @return true if successful, false otherwise
     * @throws IllegalArgumentException if amount is negative
     * @throws InsufficientFundsException if balance is insufficient
     */
    public boolean withdraw(BigDecimal amount) throws InsufficientFundsException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        if (!"ACTIVE".equals(status)) {
            return false;
        }

        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal");
        }

        balance = balance.subtract(amount);
        return true;
    }

    /**
     * Calculate interest (different for each account type)
     *
     * @return Interest amount
     */
    public abstract BigDecimal calculateInterest();

    // Getters and Setters
    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getAccountType() {
        return accountType;
    }

    protected void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getDateOpened() {
        return dateOpened;
    }

    public void setDateOpened(LocalDateTime dateOpened) {
        this.dateOpened = dateOpened;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Account{"
                + "accountId=" + accountId
                + ", customerId=" + customerId
                + ", accountType='" + accountType + '\''
                + ", balance=" + balance
                + ", dateOpened=" + dateOpened
                + ", status='" + status + '\''
                + '}';
    }

    public String getAccountNumber() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
