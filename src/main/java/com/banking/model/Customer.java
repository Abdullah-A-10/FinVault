package com.banking.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Customer class representing a bank customer Demonstrates composition with
 * Account objects
 */
public class Customer {

    private int customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private LocalDateTime dateRegistered;
    private String status;
    private List<Account> accounts;

    /**
     * Default constructor
     */
    public Customer() {
        this.dateRegistered = LocalDateTime.now();
        this.status = "ACTIVE";
        this.accounts = new ArrayList<>();
    }

    /**
     * Constructor with essential parameters
     *
     * @param firstName Customer's first name
     * @param lastName Customer's last name
     * @param email Customer's email
     */
    public Customer(String firstName, String lastName, String email) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * Full constructor
     *
     * @param customerId Customer identifier
     * @param firstName Customer's first name
     * @param lastName Customer's last name
     * @param email Customer's email
     * @param phone Customer's phone
     * @param address Customer's address
     * @param dateRegistered Registration date
     * @param status Customer status
     */
    public Customer(int customerId, String firstName, String lastName, String email,
            String phone, String address, LocalDateTime dateRegistered, String status) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.dateRegistered = dateRegistered;
        this.status = status;
        this.accounts = new ArrayList<>();
    }

    /**
     * Add an account to this customer
     *
     * @param account Account to add
     */
    public void addAccount(Account account) {
        account.setCustomerId(this.customerId);
        accounts.add(account);
    }

    /**
     * Remove an account from this customer
     *
     * @param accountId ID of the account to remove
     * @return true if removed, false if not found
     */
    public boolean removeAccount(int accountId) {
        return accounts.removeIf(account -> account.getAccountId() == accountId);
    }

    /**
     * Get a specific account by ID
     *
     * @param accountId ID of the account to retrieve
     * @return Account if found, null otherwise
     */
    public Account getAccount(int accountId) {
        return accounts.stream()
                .filter(account -> account.getAccountId() == accountId)
                .findFirst()
                .orElse(null);
    }

    /**
     * Get all accounts of a specific type
     *
     * @param accountType Type of accounts to retrieve
     * @return List of accounts of the specified type
     */
    public List<Account> getAccountsByType(String accountType) {
        return accounts.stream()
                .filter(account -> account.getAccountType().equals(accountType))
                .toList();
    }

    /**
     * Get customer's full name
     *
     * @return Full name (first name + last name)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Getters and setters
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(LocalDateTime dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Account> getAccounts() {
        return new ArrayList<>(accounts); // Return a copy to prevent direct modification
    }

    @Override
    public String toString() {
        return "Customer{"
                + "customerId=" + customerId
                + ", firstName='" + firstName + '\''
                + ", lastName='" + lastName + '\''
                + ", email='" + email + '\''
                + ", phone='" + phone + '\''
                + ", status='" + status + '\''
                + ", accountCount=" + accounts.size()
                + '}';
    }
}
