package com.banking.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * SavingsAccount class - extends the base Account class Implements specific
 * functionality for a savings account
 */
public class SavingsAccount extends Account {

    private BigDecimal interestRate;
    private static final BigDecimal MINIMUM_BALANCE = new BigDecimal("100.00");

    /**
     * Default constructor
     */
    public SavingsAccount() {
        super();
        this.accountType = "SAVINGS";
        this.interestRate = new BigDecimal("0.025"); // 2.5% default interest rate
    }

    /**
     * Constructor with parameters
     *
     * @param accountId Account identifier
     * @param customerId Customer identifier
     * @param balance Initial balance
     * @param interestRate Interest rate
     */
    public SavingsAccount(int accountId, int customerId, BigDecimal balance, BigDecimal interestRate) {
        super(accountId, customerId, balance);
        this.accountType = "SAVINGS";
        this.interestRate = interestRate;
    }

    /**
     * Full constructor
     *
     * @param accountId Account identifier
     * @param customerId Customer identifier
     * @param balance Initial balance
     * @param dateOpened Opening date
     * @param status Account status
     * @param interestRate Interest rate
     */
    public SavingsAccount(int accountId, int customerId, BigDecimal balance,
            LocalDateTime dateOpened, String status, BigDecimal interestRate) {
        super(accountId, customerId, balance, dateOpened, status);
        this.accountType = "SAVINGS";
        this.interestRate = interestRate;
    }

    /**
     * Calculate interest based on current balance and interest rate
     *
     * @return Calculated interest amount
     */
    @Override
    public BigDecimal calculateInterest() {
        return balance.multiply(interestRate).setScale(2, RoundingMode.HALF_EVEN);
    }

    /**
     * Apply monthly interest to the account
     *
     * @return The interest amount that was added
     */
    public BigDecimal applyMonthlyInterest() {
        BigDecimal interest = calculateInterest();
        balance = balance.add(interest);
        return interest;
    }

    /**
     * Withdraw money from the account, checking for minimum balance requirement
     *
     * @param amount Amount to withdraw
     * @return true if successful, false otherwise
     * @throws InsufficientFundsException if balance would fall below minimum
     */
    @Override
    public boolean withdraw(BigDecimal amount) throws InsufficientFundsException {
        // Check if withdrawal would put balance below minimum
        if (balance.subtract(amount).compareTo(MINIMUM_BALANCE) < 0) {
            throw new InsufficientFundsException(
                    "Withdrawal would put the account below the minimum balance of " + MINIMUM_BALANCE);
        }

        return super.withdraw(amount);
    }

    // Getters and setters
    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public static BigDecimal getMinimumBalance() {
        return MINIMUM_BALANCE;
    }

    @Override
    public String toString() {
        return "SavingsAccount{"
                + "accountId=" + accountId
                + ", customerId=" + customerId
                + ", balance=" + balance
                + ", dateOpened=" + dateOpened
                + ", status='" + status + '\''
                + ", interestRate=" + interestRate
                + '}';
    }
}
