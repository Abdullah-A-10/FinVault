# Banking System

A Java-based Banking System with PostgreSQL integration that demonstrates Object-Oriented Programming concepts and database interaction.

## Overview

This banking system implements core banking functionality including:

- Customer management
- Account creation and management (Savings and Current accounts)
- Transaction processing (deposits, withdrawals, and transfers)
- Account balance tracking and transaction history
- Interest calculation for savings accounts

The system uses Java for its implementation and PostgreSQL for persistent storage.

## Database Schema

The system utilizes the following database tables:

- `customers`: Stores customer information
- `accounts`: Stores account information with account type differentiation
- `transactions`: Records all financial transactions

Views are also created for convenient data access:

- `account_summary`: Provides account details with customer information
- `transaction_history`: Provides transaction details with account and customer information

## OOP Concepts Demonstrated

- **Inheritance**: Base `Account` class with `SavingsAccount` and `CurrentAccount` subclasses
- **Encapsulation**: Private fields with getter/setter methods
- **Polymorphism**: Method overriding in account subclasses
- **Composition**: Customer has a collection of accounts
- **Abstraction**: Base abstract Account class with concrete implementations

## Project Structure

```java
com.banking
├── config
│   └── DatabaseConfig.java
├── dao
│   ├── AccountDAO.java
│   ├── AccountDAOImpl.java
│   ├── CustomerDAO.java
│   ├── CustomerDAOImpl.java
│   ├── TransactionDAO.java
│   └── TransactionDAOImpl.java
├── model
│   ├── Account.java
│   ├── CurrentAccount.java
│   ├── Customer.java
│   ├── InsufficientFundsException.java
│   ├── SavingsAccount.java
│   └── Transaction.java
├── service
│   ├── AccountService.java
│   ├── CustomerService.java
│   └── TransactionService.java
├── util
│   ├── DatabaseUtil.java
│   ├── DateUtil.java
│   └── ValidationUtil.java
├── BankingApplication.java
└── BankingSystemMain.java
```

## Prerequisites

- Java 11 or higher
- PostgreSQL 12 or higher
- Maven (for building)

## Configuration

Edit the database connection settings in `com.banking.config.DatabaseConfig.java`:

```java
private static final String DB_URL = "jdbc:postgresql://localhost:5432/banking_system";
private static final String DB_USER = "postgres";
private static final String DB_PASSWORD = "password"; // Change to your actual password
```

## Building the Project

1. Clone the repository
2. Navigate to the project root directory
3. Build with Maven:

```java
mvn clean package
```

## Running the Application

Run the main application class:

```java
java -cp target/banking-system-1.0.jar com.banking.BankingSystemMain
```

## Database Setup

Before running the application for the first time, create a PostgreSQL database:

```sql
CREATE DATABASE banking_system;
```

The application will automatically create all required tables and sequences on startup.

## Features

- **Customer Management**
  - Register new customers
  - Update customer information
  - Search customers by name or email
- **Account Management**
  - Create savings accounts with interest rates
  - Create current accounts with overdraft limits
  - View account details and balances
  - Change account status (active, inactive, frozen, closed)
- **Transaction Processing**
  - Deposit funds
  - Withdraw funds (with overdraft handling for current accounts)
  - Transfer between accounts
  - View transaction history
- **System Management**
  - Database initialization
  - Data validation
  - Date/time utilities

## Example Usage

The `BankingApplication` class demonstrates the core functionality of the system:

1. Creating customers
2. Creating accounts
3. Making deposits and withdrawals
4. Transferring funds between accounts
5. Viewing transaction history
6. Calculating interest
7. Displaying customer and account summaries

## License

This project is available for educational purposes.
