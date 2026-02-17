package com.banking.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database configuration and connection management
 */
public class DatabaseConfig {

    // Database connection properties
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/banking_system";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "Abdullah@1234"; 

    private static DatabaseConfig instance;

    private DatabaseConfig() {
        // Private constructor to enforce singleton pattern
        try {
            // Register the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found!");
        }
    }

    /**
     * Singleton pattern implementation
     *
     * @return DatabaseConfig instance
     */
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    /**
     * Get a connection to the database
     *
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Close a database connection safely
     *
     * @param connection Connection to close
     */
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection");
            }
        }
    }
}
