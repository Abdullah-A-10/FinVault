// package com.banking;

// import java.math.BigDecimal;
// import java.time.format.DateTimeFormatter;
// import java.util.List;

// import com.banking.model.Account;
// import com.banking.model.CurrentAccount;
// import com.banking.model.Customer;
// import com.banking.model.InsufficientFundsException;
// import com.banking.model.SavingsAccount;
// import com.banking.model.Transaction;
// import com.banking.service.AccountService;
// import com.banking.service.CustomerService;
// import com.banking.service.TransactionService;

// /**
//  * Main class for the Banking System application Demonstrates the functionality
//  * of the banking system
//  */
// public class BankingApplication {

//         private static final CustomerService customerService = new CustomerService();
//         private static final AccountService accountService = new AccountService();
//         private static final TransactionService transactionService = new TransactionService();

//         private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

//         @SuppressWarnings("CallToPrintStackTrace")
//         public static void main(String[] args) {
//                 try {
//                         // Demonstrate banking operations
//                         demonstrateBankingOperations();
//                 } catch (Exception e) {

//                         System.err.println("An error occurred: " + e.getMessage());
//                         e.printStackTrace();
//                 }
//         }

//         /**
//          * Demonstrate various banking operations
//          */
//         private static void demonstrateBankingOperations() throws Exception {
//                 System.out.println("=== Banking System Demonstration ===");

//                 // 1. Create customers
//                 System.out.println("\n--- Creating Customers ---");
//                 Customer customer1 = customerService.registerCustomer("John", "Doe", "john.doe@example.com",
//                                 "555-123-4567", "123 Main St");
//                 System.out.println("Created customer: " + customer1.getFullName() + " (ID: " + customer1.getCustomerId()
//                                 + ")");

//                 Customer customer2 = customerService.registerCustomer("Jane", "Smith", "jane.smith@example.com",
//                                 "555-987-6543", "456 Elm St");
//                 System.out.println("Created customer: " + customer2.getFullName() + " (ID: " + customer2.getCustomerId()
//                                 + ")");

//                 // 2. Create accounts
//                 System.out.println("\n--- Creating Accounts ---");
//                 // Savings account for customer 1
//                 SavingsAccount savingsAccount = accountService.createSavingsAccount(
//                                 customer1.getCustomerId(),
//                                 new BigDecimal("1000.00"),
//                                 new BigDecimal("0.025") // 2.5% interest rate
//                 );
//                 System.out.println("Created savings account for " + customer1.getFullName()
//                                 + ": Account #" + savingsAccount.getAccountId()
//                                 + ", Balance: $" + savingsAccount.getBalance());

//                 // Current account for customer 1
//                 CurrentAccount currentAccount = accountService.createCurrentAccount(
//                                 customer1.getCustomerId(),
//                                 new BigDecimal("500.00"),
//                                 new BigDecimal("200.00") // $200 overdraft limit
//                 );
//                 System.out.println("Created current account for " + customer1.getFullName()
//                                 + ": Account #" + currentAccount.getAccountId()
//                                 + ", Balance: $" + currentAccount.getBalance()
//                                 + ", Overdraft: $" + currentAccount.getOverdraftLimit());

//                 // Savings account for customer 2
//                 SavingsAccount savingsAccount2 = accountService.createSavingsAccount(
//                                 customer2.getCustomerId(),
//                                 new BigDecimal("2000.00"),
//                                 new BigDecimal("0.03") // 3% interest rate
//                 );
//                 System.out.println("Created savings account for " + customer2.getFullName()
//                                 + ": Account #" + savingsAccount2.getAccountId()
//                                 + ", Balance: $" + savingsAccount2.getBalance());

//                 // 3. Deposit money
//                 System.out.println("\n--- Making Deposits ---");
//                 Transaction deposit1 = transactionService.deposit(
//                                 savingsAccount.getAccountId(),
//                                 new BigDecimal("500.00"),
//                                 "Initial deposit");
//                 System.out.println("Deposited $" + deposit1.getAmount() + " to Account #"
//                                 + deposit1.getAccountId() + " (Transaction ID: " + deposit1.getTransactionId() + ")");

//                 // Update account object with new balance
//                 savingsAccount = (SavingsAccount) accountService.getAccountById(savingsAccount.getAccountId());
//                 System.out.println("New balance: $" + savingsAccount.getBalance());

//                 // 4. Withdraw money
//                 System.out.println("\n--- Making Withdrawals ---");
//                 try {
//                         Transaction withdrawal1 = transactionService.withdraw(
//                                         currentAccount.getAccountId(),
//                                         new BigDecimal("200.00"),
//                                         "ATM withdrawal");
//                         System.out.println("Withdrew $" + withdrawal1.getAmount() + " from Account #"
//                                         + withdrawal1.getAccountId() + " (Transaction ID: "
//                                         + withdrawal1.getTransactionId() + ")");

//                         // Update account object with new balance
//                         currentAccount = (CurrentAccount) accountService.getAccountById(currentAccount.getAccountId());
//                         System.out.println("New balance: $" + currentAccount.getBalance());

//                         // Try to withdraw too much money from savings account (should fail)
//                         System.out.println("\nAttempting to withdraw too much from savings account...");
//                         transactionService.withdraw(
//                                         savingsAccount.getAccountId(),
//                                         new BigDecimal("2000.00"),
//                                         "Large withdrawal");
//                 } catch (InsufficientFundsException e) {
//                         System.out.println("ERROR: " + e.getMessage());
//                 }

//                 // 5. Transfer money
//                 System.out.println("\n--- Making Transfers ---");
//                 Transaction[] transfer = transactionService.transfer(
//                                 savingsAccount.getAccountId(),
//                                 currentAccount.getAccountId(),
//                                 new BigDecimal("300.00"),
//                                 "Transfer to current account");
//                 System.out.println("Transferred $" + transfer[0].getAmount() + " from Account #"
//                                 + transfer[0].getAccountId() + " to Account #" + transfer[0].getRecipientAccountId());

//                 // Update account objects with new balances
//                 savingsAccount = (SavingsAccount) accountService.getAccountById(savingsAccount.getAccountId());
//                 currentAccount = (CurrentAccount) accountService.getAccountById(currentAccount.getAccountId());

//                 System.out.println("Savings account balance: $" + savingsAccount.getBalance());
//                 System.out.println("Current account balance: $" + currentAccount.getBalance());

//                 // 6. Transfer with customer 2
//                 System.out.println("\n--- Inter-customer Transfer ---");
//                 Transaction[] transfer2 = transactionService.transfer(
//                                 savingsAccount.getAccountId(),
//                                 savingsAccount2.getAccountId(),
//                                 new BigDecimal("250.00"),
//                                 "Payment to Jane");
//                 System.out.println("Transferred $" + transfer2[0].getAmount() + " from "
//                                 + customer1.getFullName() + "'s Account #" + transfer2[0].getAccountId()
//                                 + " to " + customer2.getFullName() + "'s Account #"
//                                 + transfer2[0].getRecipientAccountId());

//                 // Update account objects with new balances
//                 savingsAccount = (SavingsAccount) accountService.getAccountById(savingsAccount.getAccountId());
//                 savingsAccount2 = (SavingsAccount) accountService.getAccountById(savingsAccount2.getAccountId());

//                 System.out.println(customer1.getFullName() + "'s savings balance: $" + savingsAccount.getBalance());
//                 System.out.println(customer2.getFullName() + "'s savings balance: $" + savingsAccount2.getBalance());

//                 // 7. View transaction history
//                 System.out.println(
//                                 "\n--- Transaction History for " + customer1.getFullName() + "'s Savings Account ---");
//                 List<Transaction> transactions = transactionService
//                                 .getTransactionsByAccountId(savingsAccount.getAccountId());

//                 for (Transaction tx : transactions) {
//                         System.out.println(
//                                         "ID: " + tx.getTransactionId() + ", "
//                                                         + "Type: " + tx.getTransactionType() + ", "
//                                                         + "Amount: $" + tx.getAmount() + ", "
//                                                         + "Date: " + tx.getTransactionDate().format(DATE_FORMATTER)
//                                                         + ", "
//                                                         + "Description: " + tx.getDescription());
//                 }

//                 // 8. Calculate and apply interest to savings account
//                 System.out.println("\n--- Calculating Interest ---");
//                 BigDecimal interest = savingsAccount.calculateInterest();
//                 System.out.println("Interest earned on savings account: $" + interest);

//                 // Manually apply interest (in production this would be done by a scheduled job)
//                 BigDecimal newBalance = savingsAccount.getBalance().add(interest);
//                 accountService.getAccountById(savingsAccount.getAccountId()).setBalance(newBalance);
//                 accountService.updateAccountStatus(savingsAccount.getAccountId(), "ACTIVE"); // Triggers balance update

//                 savingsAccount = (SavingsAccount) accountService.getAccountById(savingsAccount.getAccountId());
//                 System.out.println("New balance after interest: $" + savingsAccount.getBalance());

//                 // 9. Show customer information with all accounts
//                 System.out.println("\n--- Customer Summary ---");
//                 printCustomerSummary(customer1.getCustomerId());
//                 printCustomerSummary(customer2.getCustomerId());
//         }

//         /**
//          * Print a summary of a customer's information and accounts
//          *
//          * @param customerId Customer ID
//          */
//         private static void printCustomerSummary(int customerId) throws Exception {
//                 Customer customer = customerService.getCustomerById(customerId);
//                 List<Account> accounts = accountService.getAccountsByCustomerId(customerId);

//                 System.out.println("\nCustomer: " + customer.getFullName());
//                 System.out.println("Email: " + customer.getEmail());
//                 System.out.println("Phone: " + customer.getPhone());
//                 System.out.println("Status: " + customer.getStatus());
//                 System.out.println("Registered: " + customer.getDateRegistered().format(DATE_FORMATTER));

//                 System.out.println("\nAccounts:");
//                 BigDecimal totalBalance = BigDecimal.ZERO;

//                 for (Account account : accounts) {
//                         System.out.println(
//                                         "- Account #" + account.getAccountId() + ": "
//                                                         + account.getAccountType() + ", "
//                                                         + "Balance: $" + account.getBalance() + ", "
//                                                         + "Status: " + account.getStatus());

//                         totalBalance = totalBalance.add(account.getBalance());
//                 }

//                 System.out.println("\nTotal balance across all accounts: $" + totalBalance);
//         }
// }

package com.banking;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import com.banking.model.Account;
import com.banking.model.CurrentAccount;
import com.banking.model.Customer;
import com.banking.model.InsufficientFundsException;
import com.banking.model.SavingsAccount;
import com.banking.model.Transaction;
import com.banking.service.AccountService;
import com.banking.service.CustomerService;
import com.banking.service.TransactionService;

/**
 * Interactive Banking System Application
 */
public class InteractiveBankingApplication {

    private static final CustomerService customerService = new CustomerService();
    private static final AccountService accountService = new AccountService();
    private static final TransactionService transactionService = new TransactionService();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Interactive Banking System ===");

        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");

            try {
                switch (choice) {
                    case 1: // Customer Management
                        customerMenu();
                        break;
                    case 2: // Account Management
                        accountMenu();
                        break;
                    case 3: // Transaction Management
                        transactionMenu();
                        break;
                    case 4: // Reports
                        reportsMenu();
                        break;
                    case 5: // Run Demo
                        runDemo();
                        break;
                    case 6: // Exit
                        running = false;
                        System.out.println("Thank you for using the Banking System. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }

        scanner.close();
    }

    /**
     * Display the main menu options
     */
    private static void displayMainMenu() {
        System.out.println("\n=== Banking System Main Menu ===");
        System.out.println("1. Customer Management");
        System.out.println("2. Account Management");
        System.out.println("3. Transaction Management");
        System.out.println("4. Reports");
        System.out.println("5. Run Demo");
        System.out.println("6. Exit");
    }

    /**
     * Display and handle customer management menu
     */
    private static void customerMenu() throws Exception {
        boolean back = false;

        while (!back) {
            System.out.println("\n=== Customer Management ===");
            System.out.println("1. Create New Customer");
            System.out.println("2. Find Customer by ID");
            System.out.println("3. Find Customer by Email");
            System.out.println("4. Update Customer Information");
            System.out.println("5. List All Customers");
            System.out.println("6. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1: // Create New Customer
                    createCustomer();
                    break;
                case 2: // Find Customer by ID
                    findCustomerById();
                    break;
                case 3: // Find Customer by Email
                    findCustomerByEmail();
                    break;
                case 4: // Update Customer Information
                    updateCustomer();
                    break;
                case 5: // List All Customers
                    listAllCustomers();
                    break;
                case 6: // Back
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Display and handle account management menu
     */
    private static void accountMenu() throws Exception {
        boolean back = false;

        while (!back) {
            System.out.println("\n=== Account Management ===");
            System.out.println("1. Create Savings Account");
            System.out.println("2. Create Current Account");
            System.out.println("3. Find Account by ID");
            System.out.println("4. View Customer Accounts");
            System.out.println("5. Close Account");
            System.out.println("6. Apply Interest to Savings Account");
            System.out.println("7. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1: // Create Savings Account
                    createSavingsAccount();
                    break;
                case 2: // Create Current Account
                    createCurrentAccount();
                    break;
                case 3: // Find Account by ID
                    findAccountById();
                    break;
                case 4: // View Customer Accounts
                    viewCustomerAccounts();
                    break;
                case 5: // Close Account
                    closeAccount();
                    break;
                case 6: // Apply Interest to Savings Account
                    applyInterest();
                    break;
                case 7: // Back
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Display and handle transaction management menu
     */
    private static void transactionMenu() throws Exception {
        boolean back = false;

        while (!back) {
            System.out.println("\n=== Transaction Management ===");
            System.out.println("1. Make Deposit");
            System.out.println("2. Make Withdrawal");
            System.out.println("3. Make Transfer");
            System.out.println("4. View Transaction History");
            System.out.println("5. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1: // Make Deposit
                    makeDeposit();
                    break;
                case 2: // Make Withdrawal
                    makeWithdrawal();
                    break;
                case 3: // Make Transfer
                    makeTransfer();
                    break;
                case 4: // View Transaction History
                    viewTransactionHistory();
                    break;
                case 5: // Back
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Display and handle reports menu
     */
    private static void reportsMenu() throws Exception {
        boolean back = false;

        while (!back) {
            System.out.println("\n=== Reports ===");
            System.out.println("1. Customer Summary");
            System.out.println("2. Account Details");
            System.out.println("3. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1: // Customer Summary
                    customerSummary();
                    break;
                case 2: // Account Details
                    accountDetails();
                    break;
                case 3: // Back
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // =================== Customer Management Methods ===================

    /**
     * Create a new customer
     */
    private static void createCustomer() throws Exception {
        System.out.println("\n--- Create New Customer ---");

        String firstName = getStringInput("Enter First Name: ");
        String lastName = getStringInput("Enter Last Name: ");
        String email = getStringInput("Enter Email: ");
        String phone = getStringInput("Enter Phone Number: ");
        String address = getStringInput("Enter Address: ");

        Customer customer = customerService.registerCustomer(firstName, lastName, email, phone, address);

        System.out.println("Customer created successfully:");
        System.out.println("ID: " + customer.getCustomerId() + ", Name: " + customer.getFullName());
    }

    /**
     * Find a customer by ID
     */
    private static void findCustomerById() throws Exception {
        System.out.println("\n--- Find Customer by ID ---");

        int customerId = getIntInput("Enter Customer ID: ");
        Customer customer = customerService.getCustomerById(customerId);

        if (customer != null) {
            System.out.println("Customer found:");
            System.out.println("ID: " + customer.getCustomerId());
            System.out.println("Name: " + customer.getFullName());
            System.out.println("Email: " + customer.getEmail());
            System.out.println("Phone: " + customer.getPhone());
            System.out.println("Address: " + customer.getAddress());
            System.out.println("Status: " + customer.getStatus());
            System.out.println("Registered: " + customer.getDateRegistered().format(DATE_FORMATTER));
        } else {
            System.out.println("Customer not found with ID: " + customerId);
        }
    }

    /**
     * Find a customer by email
     */
    private static void findCustomerByEmail() throws Exception {
        System.out.println("\n--- Find Customer by Email ---");

        String email = getStringInput("Enter Customer Email: ");
        Customer customer = customerService.getCustomerByEmail(email);

        if (customer != null) {
            System.out.println("Customer found:");
            System.out.println("ID: " + customer.getCustomerId());
            System.out.println("Name: " + customer.getFullName());
            System.out.println("Email: " + customer.getEmail());
            System.out.println("Phone: " + customer.getPhone());
            System.out.println("Address: " + customer.getAddress());
            System.out.println("Status: " + customer.getStatus());
            System.out.println("Registered: " + customer.getDateRegistered().format(DATE_FORMATTER));
        } else {
            System.out.println("Customer not found with email: " + email);
        }
    }

    /**
     * Update customer information
     */
    private static void updateCustomer() throws Exception {
        System.out.println("\n--- Update Customer Information ---");

        int customerId = getIntInput("Enter Customer ID: ");
        Customer customer = customerService.getCustomerById(customerId);

        if (customer == null) {
            System.out.println("Customer not found with ID: " + customerId);
            return;
        }

        System.out.println("Current Information:");
        System.out.println("Name: " + customer.getFullName());
        System.out.println("Email: " + customer.getEmail());
        System.out.println("Phone: " + customer.getPhone());
        System.out.println("Address: " + customer.getAddress());

        System.out.println("\nLeave field empty to keep current value");

        String input;

        input = getStringInput("Enter New First Name [" + customer.getFirstName() + "]: ");
        if (!input.isEmpty()) {
            customer.setFirstName(input);
        }

        input = getStringInput("Enter New Last Name [" + customer.getLastName() + "]: ");
        if (!input.isEmpty()) {
            customer.setLastName(input);
        }

        input = getStringInput("Enter New Email [" + customer.getEmail() + "]: ");
        if (!input.isEmpty()) {
            customer.setEmail(input);
        }

        input = getStringInput("Enter New Phone [" + customer.getPhone() + "]: ");
        if (!input.isEmpty()) {
            customer.setPhone(input);
        }

        input = getStringInput("Enter New Address [" + customer.getAddress() + "]: ");
        if (!input.isEmpty()) {
            customer.setAddress(input);
        }

        boolean updated = customerService.updateCustomer(customer);

        if (updated) {
            System.out.println("Customer updated successfully");
        } else {
            System.out.println("Failed to update customer");
        }
    }

    /**
     * List all customers
     */
    private static void listAllCustomers() throws Exception {
        System.out.println("\n--- All Customers ---");

        List<Customer> customers = customerService.getAllCustomers();

        if (customers.isEmpty()) {
            System.out.println("No customers found");
            return;
        }

        for (Customer customer : customers) {
            System.out.println("ID: " + customer.getCustomerId() +
                    ", Name: " + customer.getFullName() +
                    ", Email: " + customer.getEmail() +
                    ", Status: " + customer.getStatus());
        }
    }

    // =================== Account Management Methods ===================

    /**
     * Create a savings account
     */
    private static void createSavingsAccount() throws Exception {
        System.out.println("\n--- Create Savings Account ---");

        int customerId = getIntInput("Enter Customer ID: ");
        Customer customer = customerService.getCustomerById(customerId);

        if (customer == null) {
            System.out.println("Customer not found with ID: " + customerId);
            return;
        }

        BigDecimal initialDeposit = getBigDecimalInput("Enter Initial Deposit Amount: $");
        BigDecimal interestRate = getBigDecimalInput("Enter Interest Rate (e.g., 0.025 for 2.5%): ");

        SavingsAccount account = accountService.createSavingsAccount(customerId, initialDeposit, interestRate);

        System.out.println("Savings account created successfully:");
        System.out.println("Account ID: " + account.getAccountId() +
                ", Balance: $" + account.getBalance() +
                ", Interest Rate: " + interestRate.multiply(new BigDecimal("100")) + "%");
    }

    /**
     * Create a current account
     */
    private static void createCurrentAccount() throws Exception {
        System.out.println("\n--- Create Current Account ---");

        int customerId = getIntInput("Enter Customer ID: ");
        Customer customer = customerService.getCustomerById(customerId);

        if (customer == null) {
            System.out.println("Customer not found with ID: " + customerId);
            return;
        }

        BigDecimal initialDeposit = getBigDecimalInput("Enter Initial Deposit Amount: $");
        BigDecimal overdraftLimit = getBigDecimalInput("Enter Overdraft Limit: $");

        CurrentAccount account = accountService.createCurrentAccount(customerId, initialDeposit, overdraftLimit);

        System.out.println("Current account created successfully:");
        System.out.println("Account ID: " + account.getAccountId() +
                ", Balance: $" + account.getBalance() +
                ", Overdraft Limit: $" + account.getOverdraftLimit());
    }

    /**
     * Find and display account details
     */
    private static void findAccountById() throws Exception {
        System.out.println("\n--- Find Account by ID ---");

        int accountId = getIntInput("Enter Account ID: ");
        Account account = accountService.getAccountById(accountId);

        if (account != null) {
            System.out.println("Account found:");
            System.out.println("ID: " + account.getAccountId());
            System.out.println("Type: " + account.getAccountType());
            System.out.println("Balance: $" + account.getBalance());
            System.out.println("Status: " + account.getStatus());
            System.out.println("Opened: " + account.getDateOpened().format(DATE_FORMATTER));

            if (account instanceof SavingsAccount) {
                SavingsAccount savingsAccount = (SavingsAccount) account;
                System.out.println(
                        "Interest Rate: " + savingsAccount.getInterestRate().multiply(new BigDecimal("100")) + "%");
                System.out.println("Minimum Balance: $" + SavingsAccount.getMinimumBalance());
            } else if (account instanceof CurrentAccount) {
                CurrentAccount currentAccount = (CurrentAccount) account;
                System.out.println("Overdraft Limit: $" + currentAccount.getOverdraftLimit());
                System.out.println("Available Balance: $" + currentAccount.getAvailableBalance());
                System.out.println("Overdrawn: " + (currentAccount.isOverdrawn() ? "Yes" : "No"));
            }

            // Find account owner
            Customer owner = customerService.getCustomerById(account.getCustomerId());
            System.out.println("Owner: " + owner.getFullName() + " (ID: " + owner.getCustomerId() + ")");
        } else {
            System.out.println("Account not found with ID: " + accountId);
        }
    }

    /**
     * View all accounts for a customer
     */
    private static void viewCustomerAccounts() throws Exception {
        System.out.println("\n--- View Customer Accounts ---");

        int customerId = getIntInput("Enter Customer ID: ");
        Customer customer = customerService.getCustomerById(customerId);

        if (customer == null) {
            System.out.println("Customer not found with ID: " + customerId);
            return;
        }

        List<Account> accounts = accountService.getAccountsByCustomerId(customerId);

        System.out.println("Accounts for " + customer.getFullName() + ":");

        if (accounts.isEmpty()) {
            System.out.println("No accounts found");
        } else {
            for (Account account : accounts) {
                System.out.println("- Account #" + account.getAccountId() +
                        ": " + account.getAccountType() +
                        ", Balance: $" + account.getBalance() +
                        ", Status: " + account.getStatus());
            }
        }
    }

    /**
     * Close an account
     */
    private static void closeAccount() throws Exception {
        System.out.println("\n--- Close Account ---");

        int accountId = getIntInput("Enter Account ID: ");
        Account account = accountService.getAccountById(accountId);

        if (account == null) {
            System.out.println("Account not found with ID: " + accountId);
            return;
        }

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            System.out.println("Account must have zero balance before closing");
            System.out.println("Current balance: $" + account.getBalance());
            return;
        }

        boolean closed = accountService.updateAccountStatus(accountId, "CLOSED");

        if (closed) {
            System.out.println("Account closed successfully");
        } else {
            System.out.println("Failed to close account");
        }
    }

    /**
     * Apply interest to a savings account
     */
    private static void applyInterest() throws Exception {
        System.out.println("\n--- Apply Interest to Savings Account ---");

        int accountId = getIntInput("Enter Savings Account ID: ");
        Account account = accountService.getAccountById(accountId);

        if (account == null) {
            System.out.println("Account not found with ID: " + accountId);
            return;
        }

        if (!(account instanceof SavingsAccount)) {
            System.out.println("Account is not a savings account");
            return;
        }

        SavingsAccount savingsAccount = (SavingsAccount) account;
        BigDecimal interest = savingsAccount.calculateInterest();
        System.out.println("Interest earned: $" + interest);

        String confirm = getStringInput("Apply interest to account? (y/n): ");
        if (confirm.equalsIgnoreCase("y")) {
            BigDecimal newBalance = savingsAccount.getBalance().add(interest);
            savingsAccount.setBalance(newBalance);
            accountService.updateBalance(accountId, newBalance);

            System.out.println("Interest applied successfully");
            System.out.println("New balance: $" + savingsAccount.getBalance());
        } else {
            System.out.println("Interest application cancelled");
        }
    }

    // =================== Transaction Management Methods ===================

    /**
     * Make a deposit
     */
    private static void makeDeposit() throws Exception {
        System.out.println("\n--- Make Deposit ---");

        int accountId = getIntInput("Enter Account ID: ");
        Account account = accountService.getAccountById(accountId);

        if (account == null) {
            System.out.println("Account not found with ID: " + accountId);
            return;
        }

        if (!"ACTIVE".equals(account.getStatus())) {
            System.out.println("Cannot deposit to a non-active account");
            return;
        }

        BigDecimal amount = getBigDecimalInput("Enter Deposit Amount: $");
        String description = getStringInput("Enter Description: ");

        Transaction transaction = transactionService.deposit(accountId, amount, description);

        System.out.println("Deposit successful:");
        System.out.println("Transaction ID: " + transaction.getTransactionId() +
                ", Amount: $" + transaction.getAmount());

        // Update account with new balance
        account = accountService.getAccountById(accountId);
        System.out.println("New Balance: $" + account.getBalance());
    }

    /**
     * Make a withdrawal
     */
    private static void makeWithdrawal() throws Exception {
        System.out.println("\n--- Make Withdrawal ---");

        int accountId = getIntInput("Enter Account ID: ");
        Account account = accountService.getAccountById(accountId);

        if (account == null) {
            System.out.println("Account not found with ID: " + accountId);
            return;
        }

        if (!"ACTIVE".equals(account.getStatus())) {
            System.out.println("Cannot withdraw from a non-active account");
            return;
        }

        BigDecimal amount = getBigDecimalInput("Enter Withdrawal Amount: $");
        String description = getStringInput("Enter Description: ");

        try {
            Transaction transaction = transactionService.withdraw(accountId, amount, description);

            System.out.println("Withdrawal successful:");
            System.out.println("Transaction ID: " + transaction.getTransactionId() +
                    ", Amount: $" + transaction.getAmount());

            // Update account with new balance
            account = accountService.getAccountById(accountId);
            System.out.println("New Balance: $" + account.getBalance());

        } catch (InsufficientFundsException e) {
            System.out.println("Error: " + e.getMessage());

            if (account instanceof CurrentAccount) {
                CurrentAccount currentAccount = (CurrentAccount) account;
                System.out.println("Available Balance: $" + currentAccount.getAvailableBalance() +
                        " (includes overdraft limit of $" + currentAccount.getOverdraftLimit() + ")");
            } else {
                System.out.println("Current Balance: $" + account.getBalance());
            }
        }
    }

    /**
     * Transfer money between accounts
     */
    private static void makeTransfer() throws Exception {
        System.out.println("\n--- Make Transfer ---");

        int fromAccountId = getIntInput("Enter Source Account ID: ");
        Account fromAccount = accountService.getAccountById(fromAccountId);

        if (fromAccount == null) {
            System.out.println("Source account not found with ID: " + fromAccountId);
            return;
        }

        if (!"ACTIVE".equals(fromAccount.getStatus())) {
            System.out.println("Cannot transfer from a non-active account");
            return;
        }

        int toAccountId = getIntInput("Enter Destination Account ID: ");
        Account toAccount = accountService.getAccountById(toAccountId);

        if (toAccount == null) {
            System.out.println("Destination account not found with ID: " + toAccountId);
            return;
        }

        if (!"ACTIVE".equals(toAccount.getStatus())) {
            System.out.println("Cannot transfer to a non-active account");
            return;
        }

        BigDecimal amount = getBigDecimalInput("Enter Transfer Amount: $");
        String description = getStringInput("Enter Description: ");

        try {
            Transaction[] transactions = transactionService.transfer(fromAccountId, toAccountId, amount, description);

            System.out.println("Transfer successful:");
            System.out.println("Transaction ID: " + transactions[0].getTransactionId() +
                    ", Amount: $" + transactions[0].getAmount());

            // Update accounts with new balances
            fromAccount = accountService.getAccountById(fromAccountId);
            toAccount = accountService.getAccountById(toAccountId);

            System.out.println("Source Account New Balance: $" + fromAccount.getBalance());
            System.out.println("Destination Account New Balance: $" + toAccount.getBalance());

        } catch (InsufficientFundsException e) {
            System.out.println("Error: " + e.getMessage());

            if (fromAccount instanceof CurrentAccount) {
                CurrentAccount currentAccount = (CurrentAccount) fromAccount;
                System.out.println("Available Balance: $" + currentAccount.getAvailableBalance() +
                        " (includes overdraft limit of $" + currentAccount.getOverdraftLimit() + ")");
            } else {
                System.out.println("Current Balance: $" + fromAccount.getBalance());
            }
        }
    }

    /**
     * View transaction history for an account
     */
    private static void viewTransactionHistory() throws Exception {
        System.out.println("\n--- Transaction History ---");

        int accountId = getIntInput("Enter Account ID: ");
        Account account = accountService.getAccountById(accountId);

        if (account == null) {
            System.out.println("Account not found with ID: " + accountId);
            return;
        }

        List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);

        System.out.println("Transactions for Account #" + accountId + ":");

        if (transactions.isEmpty()) {
            System.out.println("No transactions found");
        } else {
            for (Transaction transaction : transactions) {
                System.out.println("ID: " + transaction.getTransactionId() +
                        ", Type: " + transaction.getTransactionType() +
                        ", Amount: $" + transaction.getAmount() +
                        ", Date: " + transaction.getTransactionDate().format(DATE_FORMATTER) +
                        ", Description: " + transaction.getDescription());
            }
        }
    }

    // =================== Report Methods ===================

    /**
     * Show customer summary with all accounts
     */
    private static void customerSummary() throws Exception {
        System.out.println("\n--- Customer Summary ---");

        int customerId = getIntInput("Enter Customer ID: ");
        Customer customer = customerService.getCustomerById(customerId);

        if (customer == null) {
            System.out.println("Customer not found with ID: " + customerId);
            return;
        }

        List<Account> accounts = accountService.getAccountsByCustomerId(customerId);

        System.out.println("Customer: " + customer.getFullName());
        System.out.println("Email: " + customer.getEmail());
        System.out.println("Phone: " + customer.getPhone());
        System.out.println("Status: " + customer.getStatus());
        System.out.println("Registered: " + customer.getDateRegistered().format(DATE_FORMATTER));

        System.out.println("\nAccounts:");
        BigDecimal totalBalance = BigDecimal.ZERO;

        if (accounts.isEmpty()) {
            System.out.println("No accounts found");
        } else {
            for (Account account : accounts) {
                System.out.println("- Account #" + account.getAccountId() +
                        ": " + account.getAccountType() +
                        ", Balance: $" + account.getBalance() +
                        ", Status: " + account.getStatus());

                totalBalance = totalBalance.add(account.getBalance());
            }

            System.out.println("\nTotal balance across all accounts: $" + totalBalance);
        }
    }

    /**
     * Show detailed account information
     */
    private static void accountDetails() throws Exception {
        System.out.println("\n--- Account Details ---");

        int accountId = getIntInput("Enter Account ID: ");
        Account account = accountService.getAccountById(accountId);

        if (account == null) {
            System.out.println("Account not found with ID: " + accountId);
            return;
        }

        Customer customer = customerService.getCustomerById(account.getCustomerId());

        System.out.println("Account ID: " + account.getAccountId());
        System.out.println("Type: " + account.getAccountType());
        System.out.println("Balance: $" + account.getBalance());
        System.out.println("Status: " + account.getStatus());
        System.out.println("Opened: " + account.getDateOpened().format(DATE_FORMATTER));

        if (account instanceof SavingsAccount) {
            SavingsAccount savingsAccount = (SavingsAccount) account;
            System.out.println(
                    "Interest Rate: " + savingsAccount.getInterestRate().multiply(new BigDecimal("100")) + "%");
            System.out.println("Minimum Balance: $" + SavingsAccount.getMinimumBalance());
        } else if (account instanceof CurrentAccount) {
            CurrentAccount currentAccount = (CurrentAccount) account;
            System.out.println("Overdraft Limit: $" + currentAccount.getOverdraftLimit());
            System.out.println("Available Balance: $" + currentAccount.getAvailableBalance());
            System.out.println("Overdrawn: " + (currentAccount.isOverdrawn() ? "Yes" : "No"));
        }

        System.out.println("\nOwner: " + customer.getFullName() + " (ID: " + customer.getCustomerId() + ")");

        // Show recent transactions
        List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);

        System.out.println("\nRecent Transactions:");

        if (transactions.isEmpty()) {
            System.out.println("No transactions found");
        } else {
            int count = 0;
            for (Transaction transaction : transactions) {
                System.out.println("ID: " + transaction.getTransactionId() +
                        ", Type: " + transaction.getTransactionType() +
                        ", Amount: $" + transaction.getAmount() +
                        ", Date: " + transaction.getTransactionDate().format(DATE_FORMATTER) +
                        ", Description: " + transaction.getDescription());
                count++;
                if (count >= 5) {
                    System.out.println("... and " + (transactions.size() - 5) + " more transactions");
                    break;
                }
            }
        }
    }

    /**
     * Run the demonstration of banking operations
     */
    private static void runDemo() throws Exception {
        System.out.println("\n=== Running Banking System Demonstration ===");

        // 1. Create customers
        System.out.println("\n--- Creating Customers ---");
        Customer customer1 = customerService.registerCustomer("John", "Doe", "john.doe@example.com",
                "555-123-4567", "123 Main St");
        System.out.println("Created customer: " + customer1.getFullName() + " (ID: " + customer1.getCustomerId()
                + ")");

        Customer customer2 = customerService.registerCustomer("Jane", "Smith", "jane.smith@example.com",
                "555-987-6543", "456 Elm St");
        System.out.println("Created customer: " + customer2.getFullName() + " (ID: " + customer2.getCustomerId()
                + ")");

        // 2. Create accounts
        System.out.println("\n--- Creating Accounts ---");
        // Savings account for customer 1
        SavingsAccount savingsAccount = accountService.createSavingsAccount(
                customer1.getCustomerId(),
                new BigDecimal("1000.00"),
                new BigDecimal("0.025") // 2.5% interest rate
        );
        System.out.println("Created savings account for " + customer1.getFullName()
                + ": Account #" + savingsAccount.getAccountId()
                + ", Balance: $" + savingsAccount.getBalance());

        // Current account for customer 1
        CurrentAccount currentAccount = accountService.createCurrentAccount(
                customer1.getCustomerId(),
                new BigDecimal("500.00"),
                new BigDecimal("200.00") // $200 overdraft limit
        );
        System.out.println("Created current account for " + customer1.getFullName()
                + ": Account #" + currentAccount.getAccountId()
                + ", Balance: $" + currentAccount.getBalance()
                + ", Overdraft: $" + currentAccount.getOverdraftLimit());

        // Savings account for customer 2
        SavingsAccount savingsAccount2 = accountService.createSavingsAccount(
                customer2.getCustomerId(),
                new BigDecimal("2000.00"),
                new BigDecimal("0.03") // 3% interest rate
        );
        System.out.println("Created savings account for " + customer2.getFullName()
                + ": Account #" + savingsAccount2.getAccountId()
                + ", Balance: $" + savingsAccount2.getBalance());

        // 3. Deposit money
        System.out.println("\n--- Making Deposits ---");
        Transaction deposit1 = transactionService.deposit(
                savingsAccount.getAccountId(),
                new BigDecimal("500.00"),
                "Initial deposit");
        System.out.println("Deposited $" + deposit1.getAmount() + " to Account #"
                + deposit1.getAccountId() + " (Transaction ID: " + deposit1.getTransactionId() + ")");

        // Update account object with new balance
        savingsAccount = (SavingsAccount) accountService.getAccountById(savingsAccount.getAccountId());
        System.out.println("New balance: $" + savingsAccount.getBalance());

        // 4. Withdraw money
        System.out.println("\n--- Making Withdrawals ---");
        try {
            Transaction withdrawal1 = transactionService.withdraw(
                    currentAccount.getAccountId(),
                    new BigDecimal("200.00"),
                    "ATM withdrawal");
            System.out.println("Withdrew $" + withdrawal1.getAmount() + " from Account #"
                    + withdrawal1.getAccountId() + " (Transaction ID: "
                    + withdrawal1.getTransactionId() + ")");

            // Update account object with new balance
            currentAccount = (CurrentAccount) accountService.getAccountById(currentAccount.getAccountId());
            System.out.println("New balance: $" + currentAccount.getBalance());

            // Try to withdraw too much money from savings account (should fail)
            System.out.println("\nAttempting to withdraw too much from savings account...");
            transactionService.withdraw(
                    savingsAccount.getAccountId(),
                    new BigDecimal("2000.00"),
                    "Large withdrawal");
        } catch (InsufficientFundsException e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        // 5. Transfer money
        System.out.println("\n--- Making Transfers ---");
        Transaction[] transfer = transactionService.transfer(
                savingsAccount.getAccountId(),
                currentAccount.getAccountId(),
                new BigDecimal("300.00"),
                "Transfer to current account");
        System.out.println("Transferred $" + transfer[0].getAmount() + " from Account #"
                + transfer[0].getAccountId() + " to Account #" + transfer[0].getRecipientAccountId());

        // Update account objects with new balances
        savingsAccount = (SavingsAccount) accountService.getAccountById(savingsAccount.getAccountId());
        currentAccount = (CurrentAccount) accountService.getAccountById(currentAccount.getAccountId());

        System.out.println("Savings account balance: $" + savingsAccount.getBalance());
        System.out.println("Current account balance: $" + currentAccount.getBalance());

        // 6. Transfer with customer 2
        System.out.println("\n--- Inter-customer Transfer ---");
        Transaction[] transfer2 = transactionService.transfer(
                savingsAccount.getAccountId(),
                savingsAccount2.getAccountId(),
                new BigDecimal("250.00"),
                "Payment to Jane");
        System.out.println("Transferred $" + transfer2[0].getAmount() + " from "
                + customer1.getFullName() + "'s Account #" + transfer2[0].getAccountId()
                + " to " + customer2.getFullName() + "'s Account #"
                + transfer2[0].getRecipientAccountId());

        // Update account objects with new balances
        savingsAccount = (SavingsAccount) accountService.getAccountById(savingsAccount.getAccountId());
        savingsAccount2 = (SavingsAccount) accountService.getAccountById(savingsAccount2.getAccountId());

        System.out.println(customer1.getFullName() + "'s savings balance: $" + savingsAccount.getBalance());
        System.out.println(customer2.getFullName() + "'s savings balance: $" + savingsAccount2.getBalance());

        // 7. View transaction history
        System.out.println(
                "\n--- Transaction History for " + customer1.getFullName() + "'s Savings Account ---");
        List<Transaction> transactions = transactionService
                .getTransactionsByAccountId(savingsAccount.getAccountId());

        for (Transaction tx : transactions) {
            System.out.println(
                    "ID: " + tx.getTransactionId() + ", "
                            + "Type: " + tx.getTransactionType() + ", "
                            + "Amount: $" + tx.getAmount() + ", "
                            + "Date: " + tx.getTransactionDate().format(DATE_FORMATTER)
                            + ", "
                            + "Description: " + tx.getDescription());
        }

        // 8. Calculate and apply interest to savings account
        System.out.println("\n--- Calculating Interest ---");
        BigDecimal interest = savingsAccount.calculateInterest();
        System.out.println("Interest earned on savings account: $" + interest);

        // Manually apply interest
        BigDecimal newBalance = savingsAccount.getBalance().add(interest);
        accountService.getAccountById(savingsAccount.getAccountId()).setBalance(newBalance);
        accountService.updateAccountStatus(savingsAccount.getAccountId(), "ACTIVE"); // Triggers balance update

        savingsAccount = (SavingsAccount) accountService.getAccountById(savingsAccount.getAccountId());
        System.out.println("New balance after interest: $" + savingsAccount.getBalance());

        // 9. Show customer information with all accounts
        System.out.println("\n--- Customer Summary ---");
        printCustomerSummary(customer1.getCustomerId());
        printCustomerSummary(customer2.getCustomerId());

        System.out.println("\nDemo completed successfully!");
    }

    /**
     * Print a summary of a customer's information and accounts
     *
     * @param customerId Customer ID
     */
    private static void printCustomerSummary(int customerId) throws Exception {
        Customer customer = customerService.getCustomerById(customerId);
        List<Account> accounts = accountService.getAccountsByCustomerId(customerId);

        System.out.println("\nCustomer: " + customer.getFullName());
        System.out.println("Email: " + customer.getEmail());
        System.out.println("Phone: " + customer.getPhone());
        System.out.println("Status: " + customer.getStatus());
        System.out.println("Registered: " + customer.getDateRegistered().format(DATE_FORMATTER));

        System.out.println("\nAccounts:");
        BigDecimal totalBalance = BigDecimal.ZERO;

        for (Account account : accounts) {
            System.out.println(
                    "- Account #" + account.getAccountId() + ": "
                            + account.getAccountType() + ", "
                            + "Balance: $" + account.getBalance() + ", "
                            + "Status: " + account.getStatus());

            totalBalance = totalBalance.add(account.getBalance());
        }

        System.out.println("\nTotal balance across all accounts: $" + totalBalance);
    }

    // =================== Helper Methods ===================

    /**
     * Get a string input from the user
     * 
     * @param prompt Prompt to display
     * @return User input string
     */
    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Get an integer input from the user
     * 
     * @param prompt Prompt to display
     * @return User input as integer
     */
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    /**
     * Get a BigDecimal input from the user
     * 
     * @param prompt Prompt to display
     * @return User input as BigDecimal
     */
    private static BigDecimal getBigDecimalInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid decimal number.");
            }
        }
    }
}