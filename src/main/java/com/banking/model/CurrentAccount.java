package com.banking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * CurrentAccount class - extends the base Account class Implements specific
 * functionality for a current (checking) account
 */
public class CurrentAccount extends Account {

    private BigDecimal overdraftLimit;

    /**
     * Default constructor
     */
    public CurrentAccount() {
        super();
        this.accountType = "CURRENT";
        this.overdraftLimit = BigDecimal.ZERO; // No overdraft by default
    }

    /**
     * Constructor with parameters
     *
     * @param accountId Account identifier
     * @param customerId Customer identifier
     * @param balance Initial balance
     * @param overdraftLimit Overdraft limit
     */
    public CurrentAccount(int accountId, int customerId, BigDecimal balance, BigDecimal overdraftLimit) {
        super(accountId, customerId, balance);
        this.accountType = "CURRENT";
        this.overdraftLimit = overdraftLimit;
    }

    /**
     * Full constructor
     *
     * @param accountId Account identifier
     * @param customerId Customer identifier
     * @param balance Initial balance
     * @param dateOpened Opening date
     * @param status Account status
     * @param overdraftLimit Overdraft limit
     */
    public CurrentAccount(int accountId, int customerId, BigDecimal balance,
            LocalDateTime dateOpened, String status, BigDecimal overdraftLimit) {
        super(accountId, customerId, balance, dateOpened, status);
        this.accountType = "CURRENT";
        this.overdraftLimit = overdraftLimit;
    }

    /**
     * Calculate interest - current accounts typically don't earn interest
     *
     * @return Zero interest
     */
    @Override
    public BigDecimal calculateInterest() {
        return BigDecimal.ZERO;
    }

    /**
     * Withdraw money from the account, allowing for overdraft
     *
     * @param amount Amount to withdraw
     * @return true if successful, false otherwise
     * @throws InsufficientFundsException if withdrawal exceeds balance plus
     * overdraft limit
     */
    @Override
    public boolean withdraw(BigDecimal amount) throws InsufficientFundsException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        if (!"ACTIVE".equals(status)) {
            return false;
        }

        // Check if withdrawal exceeds balance plus overdraft limit
        BigDecimal maximumWithdrawal = balance.add(overdraftLimit);
        if (amount.compareTo(maximumWithdrawal) > 0) {
            throw new InsufficientFundsException(
                    "Withdrawal amount exceeds balance plus overdraft limit");
        }

        balance = balance.subtract(amount);
        return true;
    }

    /**
     * Check if the account is overdrawn
     *
     * @return true if balance is negative, false otherwise
     */
    public boolean isOverdrawn() {
        return balance.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Get the available balance including overdraft
     *
     * @return Available balance (balance + overdraft limit)
     */
    public BigDecimal getAvailableBalance() {
        return balance.add(overdraftLimit);
    }

    // Getters and setters
    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(BigDecimal overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public String toString() {
        return "CurrentAccount{"
                + "accountId=" + accountId
                + ", customerId=" + customerId
                + ", balance=" + balance
                + ", dateOpened=" + dateOpened
                + ", status='" + status + '\''
                + ", overdraftLimit=" + overdraftLimit
                + ", isOverdrawn=" + isOverdrawn()
                + '}';
    }
}
