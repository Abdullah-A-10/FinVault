package com.banking.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Utility class for input validation
 */
public class ValidationUtil {
    
    // Regular expression for email validation
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    
    // Regular expression for phone number validation
    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^\\+?[0-9]{10,15}$");
    
    /**
     * Validate an email address
     * 
     * @param email Email address to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validate a phone number
     * 
     * @param phone Phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        
        // Remove common separators for validation
        String cleanPhone = phone.replaceAll("[\\s\\-\\(\\)\\.]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }
    
    /**
     * Validate a currency amount
     * 
     * @param amount Amount to validate
     * @param allowNegative Whether to allow negative amounts
     * @return true if valid, false otherwise
     */
    public static boolean isValidAmount(BigDecimal amount, boolean allowNegative) {
        if (amount == null) {
            return false;
        }
        
        if (!allowNegative && amount.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        
        // Check scale (decimal places)
        return amount.scale() <= 2;
    }
    
    /**
     * Validate a customer name
     * 
     * @param name Name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= 50;
    }
    
    /**
     * Validate an account type
     * 
     * @param accountType Account type to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidAccountType(String accountType) {
        return accountType != null && 
               (accountType.equals("SAVINGS") || accountType.equals("CURRENT"));
    }
    
    /**
     * Validate a transaction type
     * 
     * @param transactionType Transaction type to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidTransactionType(String transactionType) {
        return transactionType != null && 
               (transactionType.equals("DEPOSIT") || 
                transactionType.equals("WITHDRAWAL") ||
                transactionType.equals("TRANSFER_IN") ||
                transactionType.equals("TRANSFER_OUT"));
    }
    
    /**
     * Validate a customer status
     * 
     * @param status Status to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidCustomerStatus(String status) {
        return status != null && 
                (status.equals("ACTIVE") || 
                status.equals("INACTIVE") ||
                status.equals("BLOCKED"));
    }
    
    /**
     * Validate an account status
     * 
     * @param status Status to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidAccountStatus(String status) {
        return status != null && 
                (status.equals("ACTIVE") || 
                status.equals("INACTIVE") ||
                status.equals("FROZEN") ||
                status.equals("CLOSED"));
    }
}