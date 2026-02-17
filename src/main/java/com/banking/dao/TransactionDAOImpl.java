package com.banking.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.banking.config.DatabaseConfig;
import com.banking.model.Account;
import com.banking.model.InsufficientFundsException;
import com.banking.model.Transaction;

/**
 * Implementation of the TransactionDAO interface for PostgreSQL
 */
public class TransactionDAOImpl implements TransactionDAO {

    private final DatabaseConfig dbConfig;
    private final AccountDAO accountDAO;

    /**
     * Constructor
     */
    public TransactionDAOImpl() {
        this.dbConfig = DatabaseConfig.getInstance();
        this.accountDAO = new AccountDAOImpl();
    }

    /**
     * Map a ResultSet row to a Transaction object
     *
     * @param rs ResultSet containing transaction data
     * @return Transaction object
     * @throws Exception if mapping fails
     */
    private Transaction mapTransactionFromResultSet(ResultSet rs) throws Exception {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getLong("transaction_id"));
        transaction.setAccountId(rs.getInt("account_id"));
        transaction.setTransactionType(rs.getString("transaction_type"));
        transaction.setAmount(rs.getBigDecimal("amount"));

        Timestamp transactionDate = rs.getTimestamp("transaction_date");
        if (transactionDate != null) {
            transaction.setTransactionDate(transactionDate.toLocalDateTime());
        }

        transaction.setDescription(rs.getString("description"));

        // recipient_account_id can be NULL
        int recipientAccountId = rs.getInt("recipient_account_id");
        if (!rs.wasNull()) {
            transaction.setRecipientAccountId(recipientAccountId);
        }

        return transaction;
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public Transaction createTransaction(Transaction transaction) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.getConnection();

            String sql = "INSERT INTO transactions (account_id, transaction_type, amount, "
                    + "transaction_date, description, recipient_account_id) "
                    + "VALUES (?, ?, ?, ?, ?, ?) RETURNING transaction_id";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, transaction.getAccountId());
            pstmt.setString(2, transaction.getTransactionType());
            pstmt.setBigDecimal(3, transaction.getAmount());

            LocalDateTime transactionDate = transaction.getTransactionDate();
            if (transactionDate == null) {
                transactionDate = LocalDateTime.now();
            }
            pstmt.setTimestamp(4, Timestamp.valueOf(transactionDate));

            pstmt.setString(5, transaction.getDescription());

            if (transaction.getRecipientAccountId() != null) {
                pstmt.setInt(6, transaction.getRecipientAccountId());
            } else {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            }

            rs = pstmt.executeQuery();

            if (rs.next()) {
                transaction.setTransactionId(rs.getLong("transaction_id"));
                return transaction;
            } else {
                throw new Exception("Failed to create transaction - no ID returned");
            }
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (Exception e) {
                /* ignore */ }
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
    public Transaction getTransactionById(long transactionId) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.getConnection();

            String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, transactionId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapTransactionFromResultSet(rs);
            } else {
                return null; // Transaction not found
            }
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (Exception e) {
                /* ignore */ }
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
    public List<Transaction> getTransactionsByAccountId(int accountId) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.getConnection();

            String sql = "SELECT * FROM transactions WHERE account_id = ? OR recipient_account_id = ? "
                    + "ORDER BY transaction_date DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, accountId);
            pstmt.setInt(2, accountId);

            rs = pstmt.executeQuery();

            List<Transaction> transactions = new ArrayList<>();
            while (rs.next()) {
                transactions.add(mapTransactionFromResultSet(rs));
            }

            return transactions;
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (Exception e) {
                /* ignore */ }
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
    public List<Transaction> getTransactionsByAccountIdAndDateRange(int accountId,
            LocalDateTime startDate,
            LocalDateTime endDate) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.getConnection();

            String sql = "SELECT * FROM transactions WHERE (account_id = ? OR recipient_account_id = ?) "
                    + "AND transaction_date BETWEEN ? AND ? ORDER BY transaction_date DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, accountId);
            pstmt.setInt(2, accountId);
            pstmt.setTimestamp(3, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(4, Timestamp.valueOf(endDate));

            rs = pstmt.executeQuery();

            List<Transaction> transactions = new ArrayList<>();
            while (rs.next()) {
                transactions.add(mapTransactionFromResultSet(rs));
            }

            return transactions;
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (Exception e) {
                /* ignore */ }
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
    public List<Transaction> getTransactionsByAccountIdAndType(int accountId, String transactionType) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.getConnection();

            String sql = "SELECT * FROM transactions WHERE account_id = ? AND transaction_type = ? "
                    + "ORDER BY transaction_date DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, accountId);
            pstmt.setString(2, transactionType);

            rs = pstmt.executeQuery();

            List<Transaction> transactions = new ArrayList<>();
            while (rs.next()) {
                transactions.add(mapTransactionFromResultSet(rs));
            }

            return transactions;
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (Exception e) {
                /* ignore */ }
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
    public List<Transaction> getTransfersBetweenAccounts(int accountId1, int accountId2) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dbConfig.getConnection();

            String sql = "SELECT * FROM transactions WHERE "
                    + "((account_id = ? AND recipient_account_id = ?) OR "
                    + "(account_id = ? AND recipient_account_id = ?)) "
                    + "AND (transaction_type = 'TRANSFER_OUT' OR transaction_type = 'TRANSFER_IN') "
                    + "ORDER BY transaction_date DESC";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, accountId1);
            pstmt.setInt(2, accountId2);
            pstmt.setInt(3, accountId2);
            pstmt.setInt(4, accountId1);

            rs = pstmt.executeQuery();

            List<Transaction> transactions = new ArrayList<>();
            while (rs.next()) {
                transactions.add(mapTransactionFromResultSet(rs));
            }

            return transactions;
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (Exception e) {
                /* ignore */ }
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
    public Transaction createDepositTransaction(int accountId, BigDecimal amount, String description) throws Exception {
        Connection conn = null;

        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            // Get the account
            Account account = accountDAO.getAccountById(accountId);
            if (account == null) {
                throw new Exception("Account not found: " + accountId);
            }

            // Check if account is active
            if (!"ACTIVE".equals(account.getStatus())) {
                throw new Exception("Cannot deposit to a non-active account");
            }

            // Validate amount
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Deposit amount must be positive");
            }

            // Create transaction object
            Transaction transaction = new Transaction(accountId, "DEPOSIT", amount, description);

            // Update account balance
            BigDecimal newBalance = account.getBalance().add(amount);
            account.setBalance(newBalance);

            // Update in database
            accountDAO.updateBalance(accountId, newBalance);
            Transaction createdTransaction = createTransaction(transaction);

            // Commit transaction
            conn.commit();

            return createdTransaction;
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
                    e.printStackTrace();
                }
                dbConfig.closeConnection(conn);
            }
        }
    }

    @Override
    public Transaction createWithdrawalTransaction(int accountId, BigDecimal amount, String description) throws Exception {
        Connection conn = null;

        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            // Get the account
            Account account = accountDAO.getAccountById(accountId);
            if (account == null) {
                throw new Exception("Account not found: " + accountId);
            }

            // Check if account is active
            if (!"ACTIVE".equals(account.getStatus())) {
                throw new Exception("Cannot withdraw from a non-active account");
            }

            // Validate amount
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Withdrawal amount must be positive");
            }

            // Check if sufficient funds
            if (account.getBalance().compareTo(amount) < 0) {
                // For current accounts, check overdraft limit
                if (account instanceof com.banking.model.CurrentAccount) {
                    com.banking.model.CurrentAccount currentAccount = (com.banking.model.CurrentAccount) account;
                    BigDecimal availableBalance = account.getBalance().add(currentAccount.getOverdraftLimit());

                    if (availableBalance.compareTo(amount) < 0) {
                        throw new InsufficientFundsException("Insufficient funds for withdrawal");
                    }
                } else {
                    throw new InsufficientFundsException("Insufficient funds for withdrawal");
                }
            }

            // Create transaction object
            Transaction transaction = new Transaction(accountId, "WITHDRAWAL", amount, description);

            // Update account balance
            BigDecimal newBalance = account.getBalance().subtract(amount);
            account.setBalance(newBalance);

            // Update in database
            accountDAO.updateBalance(accountId, newBalance);
            Transaction createdTransaction = createTransaction(transaction);

            // Commit transaction
            conn.commit();

            return createdTransaction;
        } catch (Exception e) {
            // Rollback transaction on error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception rollbackEx) {
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
                    System.err.println("Error resetting auto-commit");
                    e.printStackTrace();
                }
                dbConfig.closeConnection(conn);
            }
        }
    }

    @Override
    public Transaction[] createTransferTransaction(int fromAccountId, int toAccountId,
            BigDecimal amount, String description) throws Exception {
        Connection conn = null;

        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            // Get the source account
            Account fromAccount = accountDAO.getAccountById(fromAccountId);
            if (fromAccount == null) {
                throw new Exception("Source account not found: " + fromAccountId);
            }

            // Get the destination account
            Account toAccount = accountDAO.getAccountById(toAccountId);
            if (toAccount == null) {
                throw new Exception("Destination account not found: " + toAccountId);
            }

            // Check if accounts are active
            if (!"ACTIVE".equals(fromAccount.getStatus()) || !"ACTIVE".equals(toAccount.getStatus())) {
                throw new Exception("Cannot transfer between non-active accounts");
            }

            // Validate amount
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Transfer amount must be positive");
            }

            // Check if sufficient funds in source account
            if (fromAccount.getBalance().compareTo(amount) < 0) {
                // For current accounts, check overdraft limit
                if (fromAccount instanceof com.banking.model.CurrentAccount) {
                    com.banking.model.CurrentAccount currentAccount = (com.banking.model.CurrentAccount) fromAccount;
                    BigDecimal availableBalance = fromAccount.getBalance().add(currentAccount.getOverdraftLimit());

                    if (availableBalance.compareTo(amount) < 0) {
                        throw new InsufficientFundsException("Insufficient funds for transfer");
                    }
                } else {
                    throw new InsufficientFundsException("Insufficient funds for transfer");
                }
            }

            // Create transaction objects
            Transaction[] transactions = Transaction.createTransferPair(fromAccountId, toAccountId, amount, description);

            // Update account balances
            BigDecimal newFromBalance = fromAccount.getBalance().subtract(amount);
            BigDecimal newToBalance = toAccount.getBalance().add(amount);

            fromAccount.setBalance(newFromBalance);
            toAccount.setBalance(newToBalance);

            // Update in database
            accountDAO.updateBalance(fromAccountId, newFromBalance);
            accountDAO.updateBalance(toAccountId, newToBalance);

            // Create transactions in database
            transactions[0] = createTransaction(transactions[0]); // Outgoing
            transactions[1] = createTransaction(transactions[1]); // Incoming

            // Commit transaction
            conn.commit();

            return transactions;
        } catch (Exception e) {
            // Rollback transaction on error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception rollbackEx) {
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
                    System.err.println("Error resetting auto-commit");
                    e.printStackTrace();
                }
                dbConfig.closeConnection(conn);
            }
        }
    }

    @Override
    public boolean deleteTransaction(long transactionId) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dbConfig.getConnection();

            String sql = "DELETE FROM transactions WHERE transaction_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, transactionId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            if (pstmt != null) try {
                pstmt.close();
            } catch (Exception e) {
                /* ignore */ }
            if (conn != null) {
                dbConfig.closeConnection(conn);
            }
        }
    }
}
