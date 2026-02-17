package com.banking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction class representing financial transactions
 */
public class Transaction {

    private long transactionId;
    private int accountId;
    private String transactionType;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String description;
    private Integer recipientAccountId; // Optional, for transfers

    /**
     * Default constructor
     */
    public Transaction() {
        this.transactionDate = LocalDateTime.now();
    }

    /**
     * Constructor with essential parameters
     *
     * @param accountId Account ID
     * @param transactionType Type of transaction
     * @param amount Transaction amount
     * @param description Transaction description
     */
    public Transaction(int accountId, String transactionType, BigDecimal amount, String description) {
        this();
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
    }

    /**
     * Constructor for transfer transactions
     *
     * @param accountId Source account ID
     * @param recipientAccountId Recipient account ID
     * @param amount Transfer amount
     * @param description Transaction description
     */
    public Transaction(int accountId, int recipientAccountId, BigDecimal amount, String description) {
        this(accountId, "TRANSFER_OUT", amount, description);
        this.recipientAccountId = recipientAccountId;
    }

    /**
     * Full constructor
     *
     * @param transactionId Transaction identifier
     * @param accountId Account identifier
     * @param transactionType Type of transaction
     * @param amount Transaction amount
     * @param transactionDate Date of transaction
     * @param description Transaction description
     * @param recipientAccountId Recipient account ID (for transfers)
     */
    public Transaction(long transactionId, int accountId, String transactionType,
            BigDecimal amount, LocalDateTime transactionDate,
            String description, Integer recipientAccountId) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.description = description;
        this.recipientAccountId = recipientAccountId;
    }

    /**
     * Create a matching pair of transactions for a transfer
     *
     * @param sourceAccountId Source account
     * @param destinationAccountId Destination account
     * @param amount Transfer amount
     * @param description Transfer description
     * @return Array of two transactions (outgoing and incoming)
     */
    public static Transaction[] createTransferPair(int sourceAccountId, int destinationAccountId,
            BigDecimal amount, String description) {
        Transaction outgoing = new Transaction(sourceAccountId, "TRANSFER_OUT", amount,
                "Transfer to account #" + destinationAccountId + ": " + description);
        outgoing.setRecipientAccountId(destinationAccountId);

        Transaction incoming = new Transaction(destinationAccountId, "TRANSFER_IN", amount,
                "Transfer from account #" + sourceAccountId + ": " + description);
        incoming.setRecipientAccountId(sourceAccountId);

        return new Transaction[]{outgoing, incoming};
    }

    // Getters and setters
    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRecipientAccountId() {
        return recipientAccountId;
    }

    public void setRecipientAccountId(Integer recipientAccountId) {
        this.recipientAccountId = recipientAccountId;
    }

    @Override
    public String toString() {
        return "Transaction{"
                + "transactionId=" + transactionId
                + ", accountId=" + accountId
                + ", transactionType='" + transactionType + '\''
                + ", amount=" + amount
                + ", transactionDate=" + transactionDate
                + ", description='" + description + '\''
                + ", recipientAccountId=" + recipientAccountId
                + '}';
    }
}
