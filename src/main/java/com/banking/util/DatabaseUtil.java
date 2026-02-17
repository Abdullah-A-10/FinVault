package com.banking.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.banking.config.DatabaseConfig;

public class DatabaseUtil {
    private static final String CHECK_TABLE_EXISTS_SQL = 
        "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = ?)";
    
    private static final String CREATE_CUSTOMERS_TABLE_SQL =
        "CREATE TABLE customers (" +
        "customer_id SERIAL PRIMARY KEY, " +
        "first_name VARCHAR(50) NOT NULL, " +
        "last_name VARCHAR(50) NOT NULL, " +
        "email VARCHAR(100) UNIQUE NOT NULL, " +
        "phone VARCHAR(20), " +
        "address TEXT, " +
        "date_registered TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
        "status VARCHAR(20) DEFAULT 'ACTIVE'" +
        ")";
    
    private static final String CREATE_ACCOUNTS_TABLE_SQL =
        "CREATE TABLE accounts (" +
        "account_id SERIAL PRIMARY KEY, " +
        "customer_id INTEGER NOT NULL REFERENCES customers(customer_id), " +
        "account_type VARCHAR(20) NOT NULL, " + // 'SAVINGS' or 'CURRENT'
        "balance DECIMAL(15, 2) NOT NULL, " +
        "interest_rate DECIMAL(5, 4), " + // For savings accounts (rate) or current accounts (overdraft limit)
        "date_opened TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
        "status VARCHAR(20) DEFAULT 'ACTIVE'" +
        ")";
    
    private static final String CREATE_TRANSACTIONS_TABLE_SQL =
        "CREATE TABLE transactions (" +
        "transaction_id BIGSERIAL PRIMARY KEY, " +
        "account_id INTEGER NOT NULL REFERENCES accounts(account_id), " +
        "transaction_type VARCHAR(20) NOT NULL, " + // 'DEPOSIT', 'WITHDRAWAL', 'TRANSFER_OUT', 'TRANSFER_IN'
        "amount DECIMAL(15, 2) NOT NULL, " +
        "transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
        "description TEXT, " +
        "recipient_account_id INTEGER REFERENCES accounts(account_id)" +
        ")";

    public static boolean initializeDatabase() {
        DatabaseConfig dbConfig = DatabaseConfig.getInstance();
        
        try (Connection conn = dbConfig.getConnection()) {
            // Check and create customers table
            if (!tableExists(conn, "customers")) {
                try (PreparedStatement stmt = conn.prepareStatement(CREATE_CUSTOMERS_TABLE_SQL)) {
                    stmt.execute();
                }
            }
            
            // Check and create accounts table
            if (!tableExists(conn, "accounts")) {
                try (PreparedStatement stmt = conn.prepareStatement(CREATE_ACCOUNTS_TABLE_SQL)) {
                    stmt.execute();
                }
            }
            
            // Check and create transactions table
            if (!tableExists(conn, "transactions")) {
                try (PreparedStatement stmt = conn.prepareStatement(CREATE_TRANSACTIONS_TABLE_SQL)) {
                    stmt.execute();
                }
            }
            
            return true;
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(CHECK_TABLE_EXISTS_SQL)) {
            stmt.setString(1, tableName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }
        }
        return false;
    }
}