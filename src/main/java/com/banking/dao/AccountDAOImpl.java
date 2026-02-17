package com.banking.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.banking.config.DatabaseConfig;
import com.banking.model.Account;
import com.banking.model.CurrentAccount;
import com.banking.model.SavingsAccount;

/**
 * Implementation of the AccountDAO interface for PostgreSQL
 */
public class AccountDAOImpl implements AccountDAO {

    private final DatabaseConfig dbConfig;

    /**
     * Constructor
     */
    public AccountDAOImpl() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    /**
     * Map a ResultSet row to an Account object
     * 
     * @param rs ResultSet containing account data
     * @return Account object (either SavingsAccount or CurrentAccount)
     * @throws Exception if mapping fails
     */

    private Account mapAccountFromResultSet(ResultSet rs) throws Exception {
        String accountType = rs.getString("account_type");
        Account account;

        if (null == accountType) {
            throw new IllegalArgumentException("Unknown account type: " + accountType);
        } else
            switch (accountType) {
                case "SAVINGS":
                    SavingsAccount savingsAccount = new SavingsAccount();
                    savingsAccount.setInterestRate(rs.getBigDecimal("interest_rate"));
                    account = savingsAccount;
                    break;
                case "CURRENT":
                    CurrentAccount currentAccount = new CurrentAccount();
                    // In this schema, overdraft_limit is stored as interest_rate for CURRENT
                    // accounts
                    currentAccount.setOverdraftLimit(rs.getBigDecimal("interest_rate"));
                    account = currentAccount;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown account type: " + accountType);
            }

        account.setAccountId(rs.getInt("account_id"));
        account.setCustomerId(rs.getInt("customer_id"));
        account.setBalance(rs.getBigDecimal("balance"));

        Timestamp dateOpened = rs.getTimestamp("date_opened");
        if (dateOpened != null) {
            account.setDateOpened(dateOpened.toLocalDateTime());
        }

        account.setStatus(rs.getString("status"));

        return account;
    }

    @Override
    public Account createAccount(Account account) throws Exception {
        if (account instanceof SavingsAccount) {
            return createSavingsAccount((SavingsAccount) account);
        } else if (account instanceof CurrentAccount) {
            return createCurrentAccount((CurrentAccount) account);
        } else {
            throw new IllegalArgumentException("Unknown account type");
        }
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public SavingsAccount createSavingsAccount(SavingsAccount account) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.getConnection();

            String sql = "INSERT INTO accounts (customer_id, account_type, balance, interest_rate, date_opened, status) "
                    +
                    "VALUES (?, 'SAVINGS', ?, ?, ?, ?) RETURNING account_id";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, account.getCustomerId());
            pstmt.setBigDecimal(2, account.getBalance());
            pstmt.setBigDecimal(3, account.getInterestRate());
            pstmt.setTimestamp(4,
                    Timestamp.valueOf(account.getDateOpened() != null ? account.getDateOpened() : LocalDateTime.now()));
            pstmt.setString(5, account.getStatus() != null ? account.getStatus() : "ACTIVE");

            rs = pstmt.executeQuery();

            if (rs.next()) {
                account.setAccountId(rs.getInt("account_id"));
                return account;
            } else {
                throw new Exception("Failed to create savings account - no ID returned");
            }
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    /* ignore */ }
            if (pstmt != null)
                try {
                    pstmt.close();
                } catch (Exception e) {
                    /* ignore */ }
            if (conn != null)
                dbConfig.closeConnection(conn);
        }
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public CurrentAccount createCurrentAccount(CurrentAccount account) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.getConnection();

            String sql = "INSERT INTO accounts (customer_id, account_type, balance, interest_rate, date_opened, status) "
                    +
                    "VALUES (?, 'CURRENT', ?, ?, ?, ?) RETURNING account_id";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, account.getCustomerId());
            pstmt.setBigDecimal(2, account.getBalance());
            pstmt.setBigDecimal(3, account.getOverdraftLimit()); // Using interest_rate column for overdraft limit
            pstmt.setTimestamp(4,
                    Timestamp.valueOf(account.getDateOpened() != null ? account.getDateOpened() : LocalDateTime.now()));
            pstmt.setString(5, account.getStatus() != null ? account.getStatus() : "ACTIVE");

            rs = pstmt.executeQuery();

            if (rs.next()) {
                account.setAccountId(rs.getInt("account_id"));
                return account;
            } else {
                throw new Exception("Failed to create current account - no ID returned");
            }
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e) {
                    /* ignore */ }
            if (pstmt != null)
                try {
                    pstmt.close();
                } catch (Exception e) {
                    /* ignore */ }
            if (conn != null)
                dbConfig.closeConnection(conn);
        }
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public Account getAccountById(int accountId) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.getConnection();

            String sql = "SELECT * FROM accounts WHERE account_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, accountId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapAccountFromResultSet(rs);
            } else {
                return null; // Account not found
            }
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e) {
                    /* ignore */ }
            if (pstmt != null)
                try {
                    pstmt.close();
                } catch (Exception e) {
                    /* ignore */ }
            if (conn != null)
                dbConfig.closeConnection(conn);
        }
    }

    @Override
    public SavingsAccount getSavingsAccountById(int accountId) throws Exception {
        Account account = getAccountById(accountId);
        if (account instanceof SavingsAccount) {
            return (SavingsAccount) account;
        }
        return null;
    }

    @Override
    public CurrentAccount getCurrentAccountById(int accountId) throws Exception {
        Account account = getAccountById(accountId);
        if (account instanceof CurrentAccount) {
            return (CurrentAccount) account;
        }
        return null;
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public boolean updateAccount(Account account) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dbConfig.getConnection();

            String sql = "UPDATE accounts SET balance = ?, status = ? WHERE account_id = ?";

            // For updates specific to account types
            BigDecimal interestOrOverdraft = BigDecimal.ZERO;
            if (account instanceof SavingsAccount) {
                interestOrOverdraft = ((SavingsAccount) account).getInterestRate();
            } else if (account instanceof CurrentAccount) {
                interestOrOverdraft = ((CurrentAccount) account).getOverdraftLimit();
                sql = "UPDATE accounts SET balance = ?, status = ?, interest_rate = ? WHERE account_id = ?";
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setBigDecimal(1, account.getBalance());
            pstmt.setString(2, account.getStatus());

            if (account instanceof CurrentAccount) {
                pstmt.setBigDecimal(3, interestOrOverdraft);
                pstmt.setInt(4, account.getAccountId());
            } else {
                pstmt.setInt(3, account.getAccountId());
            }

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            if (pstmt != null)
                try {
                    pstmt.close();
                } catch (Exception e) {
                    /* ignore */ }
            if (conn != null)
                dbConfig.closeConnection(conn);
        }
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public boolean updateBalance(int accountId, BigDecimal newBalance) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dbConfig.getConnection();

            String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setBigDecimal(1, newBalance);
            pstmt.setInt(2, accountId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            if (pstmt != null)
                try {
                    pstmt.close();
                } catch (Exception e) {
                    /* ignore */ }
            if (conn != null)
                dbConfig.closeConnection(conn);
        }
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public boolean updateAccountStatus(int accountId, String newStatus) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dbConfig.getConnection();

            String sql = "UPDATE accounts SET status = ? WHERE account_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, accountId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            if (pstmt != null)
                try {
                    pstmt.close();
                } catch (Exception e) {
                    /* ignore */ }
            if (conn != null)
                dbConfig.closeConnection(conn);
        }
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public boolean deleteAccount(int accountId) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dbConfig.getConnection();

            // Begin transaction
            conn.setAutoCommit(false);

            // First delete all associated transactions
            String deleteTransactionsSql = "DELETE FROM transactions WHERE account_id = ? OR recipient_account_id = ?";
            pstmt = conn.prepareStatement(deleteTransactionsSql);
            pstmt.setInt(1, accountId);
            pstmt.setInt(2, accountId);
            pstmt.executeUpdate();
            pstmt.close();

            // Then delete the account
            String deleteAccountSql = "DELETE FROM accounts WHERE account_id = ?";
            pstmt = conn.prepareStatement(deleteAccountSql);
            pstmt.setInt(1, accountId);
            int affectedRows = pstmt.executeUpdate();

            // Commit transaction
            conn.commit();

            return affectedRows > 0;
        } catch (Exception e) {
            // Rollback transaction on error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Error during transaction rollback");
                }
            }
            throw e;
        } finally {
            // Reset auto-commit
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (Exception e) {
                    System.err.println("Error resetting auto-commit");
                }
            }

            if (pstmt != null)
                try {
                    pstmt.close();
                } catch (Exception e) {
                    /* ignore */ }
            if (conn != null)
                dbConfig.closeConnection(conn);
        }
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public List<Account> getAccountsByCustomerId(int customerId) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.getConnection();

            String sql = "SELECT * FROM accounts WHERE customer_id = ? ORDER BY account_id";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);

            rs = pstmt.executeQuery();

            List<Account> accounts = new ArrayList<>();
            while (rs.next()) {
                accounts.add(mapAccountFromResultSet(rs));
            }

            return accounts;
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e) {
                    /* ignore */ }
            if (pstmt != null)
                try {
                    pstmt.close();
                } catch (Exception e) {
                    /* ignore */ }
            if (conn != null)
                dbConfig.closeConnection(conn);
        }
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public List<Account> getAccountsByCustomerIdAndType(int customerId, String accountType) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.getConnection();

            String sql = "SELECT * FROM accounts WHERE customer_id = ? AND account_type = ? ORDER BY account_id";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            pstmt.setString(2, accountType);

            rs = pstmt.executeQuery();

            List<Account> accounts = new ArrayList<>();
            while (rs.next()) {
                accounts.add(mapAccountFromResultSet(rs));
            }

            return accounts;
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e) {
                    /* ignore */ }
            if (pstmt != null)
                try {
                    pstmt.close();
                } catch (Exception e) {
                    /* ignore */ }
            if (conn != null)
                dbConfig.closeConnection(conn);
        }
    }
}