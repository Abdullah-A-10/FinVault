package com.banking.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.banking.config.DatabaseConfig;
import com.banking.model.Customer;

/**
 * Implementation of the CustomerDAO interface for PostgreSQL
 */
public class CustomerDAOImpl implements CustomerDAO {

    private final DatabaseConfig dbConfig;

    /**
     * Constructor
     */
    public CustomerDAOImpl() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    /**
     * Map a ResultSet row to a Customer object
     *
     * @param rs ResultSet containing customer data
     * @return Customer object
     * @throws Exception if mapping fails
     */
    private Customer mapCustomerFromResultSet(ResultSet rs) throws Exception {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setFirstName(rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone(rs.getString("phone"));
        customer.setAddress(rs.getString("address"));

        Timestamp dateRegistered = rs.getTimestamp("date_registered");
        if (dateRegistered != null) {
            customer.setDateRegistered(dateRegistered.toLocalDateTime());
        }

        customer.setStatus(rs.getString("status"));

        return customer;
    }

    @Override
    public Customer createCustomer(Customer customer) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.getConnection();

            String sql = "INSERT INTO customers (first_name, last_name, email, phone, address, status) "
                    + "VALUES (?, ?, ?, ?, ?, ?) RETURNING customer_id, date_registered";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getPhone());
            pstmt.setString(5, customer.getAddress());
            pstmt.setString(6, customer.getStatus());

            rs = pstmt.executeQuery();

            if (rs.next()) {
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setDateRegistered(rs.getTimestamp("date_registered").toLocalDateTime());
                return customer;
            } else {
                throw new Exception("Failed to create customer - no ID returned");
            }
        } finally {
            // Close resources in reverse order
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                /* ignore */ }
            if (pstmt != null) try {
                pstmt.close();
            } catch (SQLException e) {
                /* ignore */ }
            if (conn != null) {
                dbConfig.closeConnection(conn);
            }
        }
    }

    @Override
    public Customer getCustomerById(int customerId) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.getConnection();

            String sql = "SELECT * FROM customers WHERE customer_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapCustomerFromResultSet(rs);
            } else {
                return null; // Customer not found
            }
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                /* ignore */ }
            if (pstmt != null) try {
                pstmt.close();
            } catch (SQLException e) {
                /* ignore */ }
            if (conn != null) {
                dbConfig.closeConnection(conn);
            }
        }
    }

    @Override
    public Customer getCustomerByEmail(String email) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.getConnection();

            String sql = "SELECT * FROM customers WHERE email = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapCustomerFromResultSet(rs);
            } else {
                return null; // Customer not found
            }
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                /* ignore */ }
            if (pstmt != null) try {
                pstmt.close();
            } catch (SQLException e) {
                /* ignore */ }
            if (conn != null) {
                dbConfig.closeConnection(conn);
            }
        }
    }

    @Override
    public boolean updateCustomer(Customer customer) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dbConfig.getConnection();

            String sql = "UPDATE customers SET first_name = ?, last_name = ?, email = ?, "
                    + "phone = ?, address = ?, status = ? WHERE customer_id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getPhone());
            pstmt.setString(5, customer.getAddress());
            pstmt.setString(6, customer.getStatus());
            pstmt.setInt(7, customer.getCustomerId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            if (pstmt != null) try {
                pstmt.close();
            } catch (SQLException e) {
                /* ignore */ }
            if (conn != null) {
                dbConfig.closeConnection(conn);
            }
        }
    }

    @Override
    @SuppressWarnings({"UseSpecificCatch", "CallToPrintStackTrace"})
    public boolean deleteCustomer(int customerId) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dbConfig.getConnection();

            // Begin transaction
            conn.setAutoCommit(false);

            // First delete all associated accounts (cascade will handle transactions)
            String deleteAccountsSql = "DELETE FROM accounts WHERE customer_id = ?";
            pstmt = conn.prepareStatement(deleteAccountsSql);
            pstmt.setInt(1, customerId);
            pstmt.executeUpdate();
            pstmt.close();

            // Then delete the customer
            String deleteCustomerSql = "DELETE FROM customers WHERE customer_id = ?";
            pstmt = conn.prepareStatement(deleteCustomerSql);
            pstmt.setInt(1, customerId);
            int affectedRows = pstmt.executeUpdate();

            // Commit transaction
            conn.commit();

            return affectedRows > 0;
        } catch (SQLException e) {
            // Rollback transaction on error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception rollbackEx) {
                    // Log rollback error
                    System.err.println("Error during transaction rollback");
                    rollbackEx.printStackTrace();
                }
            }
            throw e;
        } finally {
            // Reset auto-commit
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (Exception e) {
                    // Log auto-commit reset error
                    System.err.println("Error resetting auto-commit");
                    e.printStackTrace();
                }
            }

            if (pstmt != null) try {
                pstmt.close();
            } catch (Exception e) {
                /* ignore */ }
            if (conn != null) {
                dbConfig.closeConnection(conn);
            }
        }
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public List<Customer> getAllCustomers() throws Exception {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.getConnection();

            String sql = "SELECT * FROM customers ORDER BY customer_id";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            List<Customer> customers = new ArrayList<>();
            while (rs.next()) {
                customers.add(mapCustomerFromResultSet(rs));
            }

            return customers;
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (Exception e) {
                /* ignore */ }
            if (stmt != null) try {
                stmt.close();
            } catch (Exception e) {
                /* ignore */ }
            if (conn != null) {
                dbConfig.closeConnection(conn);
            }
        }
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public List<Customer> searchCustomersByName(String name) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.getConnection();

            String sql = "SELECT * FROM customers WHERE LOWER(first_name) LIKE LOWER(?) OR LOWER(last_name) LIKE LOWER(?)";
            pstmt = conn.prepareStatement(sql);

            String searchPattern = "%" + name + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            rs = pstmt.executeQuery();

            List<Customer> customers = new ArrayList<>();
            while (rs.next()) {
                customers.add(mapCustomerFromResultSet(rs));
            }

            return customers;
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                /* ignore */ }
            if (pstmt != null) try {
                pstmt.close();
            } catch (SQLException e) {
                /* ignore */ }
            if (conn != null) {
                dbConfig.closeConnection(conn);
            }
        }
    }
}
