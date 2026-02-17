package com.banking.model;

/**
 * Exception thrown when an account has insufficient funds to perform an
 * operation
 */
public class InsufficientFundsException extends Exception {

    /**
     * Default constructor
     */
    public InsufficientFundsException() {
        super("Insufficient funds in the account");
    }

    /**
     * Constructor with custom message
     *
     * @param message Error message
     */
    public InsufficientFundsException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     *
     * @param message Error message
     * @param cause Cause of the exception
     */
    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
    }
}
