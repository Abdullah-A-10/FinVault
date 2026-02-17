package com.banking.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.banking.model.Account;
import com.banking.model.InsufficientFundsException;
import com.banking.model.Transaction;
import com.banking.service.AccountService;
import com.banking.service.TransactionService;

/**
 * Modern transaction management panel with real database integration
 */
public class TransactionPanel extends JPanel {

    private final TransactionService transactionService;
    private final AccountService accountService;
    
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> transactionTypeFilter;
    private JTextField searchField;
    private JButton refreshButton;
    private JButton depositButton;
    private JButton withdrawButton;
    private JButton transferButton;
    private JButton viewDetailsButton;
    private JLabel totalCreditLabel;
    private JLabel totalDebitLabel;
    private JLabel balanceLabel;
    private JLabel statusLabel;

    public TransactionPanel() {
        this.transactionService = new TransactionService();
        this.accountService = new AccountService();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        initComponents();
        loadTransactions();
    }

    private void initComponents() {
        // Top action buttons
        JPanel actionPanel = createActionPanel();
        add(actionPanel, BorderLayout.NORTH);

        // Center table
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Bottom summary panel
        JPanel summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.SOUTH);
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Left side - filters
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(Color.WHITE);

        JLabel filterLabel = new JLabel("Show:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        transactionTypeFilter = new JComboBox<>(new String[]{
            "All Transactions", "Deposits", "Withdrawals", "Transfers"
        });
        transactionTypeFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        transactionTypeFilter.setPreferredSize(new Dimension(150, 35));
        transactionTypeFilter.addActionListener(e -> filterTransactions());

        searchField = new JTextField(15);
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        searchField.putClientProperty("JTextField.placeholderText", "Search by ID or description...");

        JButton searchButton = createStyledButton("🔍 Search", new Color(45, 85, 255));
        searchButton.addActionListener(e -> searchTransactions());

        refreshButton = createStyledButton("🔄 Refresh", new Color(108, 117, 125));
        refreshButton.addActionListener(e -> loadTransactions());

        leftPanel.add(filterLabel);
        leftPanel.add(transactionTypeFilter);
        leftPanel.add(searchField);
        leftPanel.add(searchButton);
        leftPanel.add(refreshButton);

        // Right side - transaction buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(Color.WHITE);

        depositButton = createStyledButton("+ Deposit", new Color(40, 167, 69));
        depositButton.addActionListener(e -> showDepositDialog());

        withdrawButton = createStyledButton("- Withdraw", new Color(255, 193, 7));
        withdrawButton.addActionListener(e -> showWithdrawDialog());

        transferButton = createStyledButton("-> Transfer", new Color(23, 162, 184));
        transferButton.addActionListener(e -> showTransferDialog());

        viewDetailsButton = createStyledButton("* Details", new Color(108, 117, 125));
        viewDetailsButton.setEnabled(false);
        viewDetailsButton.addActionListener(e -> viewTransactionDetails());

        rightPanel.add(depositButton);
        rightPanel.add(withdrawButton);
        rightPanel.add(transferButton);
        rightPanel.add(viewDetailsButton);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));

        // Create table model
        String[] columns = {"ID", "Date", "Type", "Account", "Amount", "Description", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(tableModel);
        transactionTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        transactionTable.setRowHeight(35);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.setShowGrid(false);
        transactionTable.setIntercellSpacing(new Dimension(0, 0));
        transactionTable.setBackground(new Color(252,252,255));
        transactionTable.setSelectionBackground(new Color(45, 85, 255, 50));

        // Style table header
        JTableHeader header = transactionTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(Color.BLACK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(45, 85, 255)));

        // Add selection listener
        transactionTable.getSelectionModel().addListSelectionListener(e -> {
            viewDetailsButton.setEnabled(transactionTable.getSelectedRow() != -1);
        });

        // Add double-click listener
        transactionTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    viewTransactionDetails();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 10, 10, 10));

        // Total Credit
        JPanel creditPanel = createSummaryCard("Total Credit", "$0.00", new Color(40, 167, 69));
        totalCreditLabel = (JLabel) ((JPanel) creditPanel.getComponent(1)).getComponent(0);

        // Total Debit
        JPanel debitPanel = createSummaryCard("Total Debit", "$0.00", new Color(220, 53, 69));
        totalDebitLabel = (JLabel) ((JPanel) debitPanel.getComponent(1)).getComponent(0);

        // Net Balance
        JPanel balancePanel = createSummaryCard("Net Balance", "$0.00", new Color(45, 85, 255));
        balanceLabel = (JLabel) ((JPanel) balancePanel.getComponent(1)).getComponent(0);

        panel.add(creditPanel);
        panel.add(debitPanel);
        panel.add(balancePanel);

        // Status label at bottom
        statusLabel = new JLabel("Loading transactions...");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(Color.GRAY);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(Color.GRAY);

        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        valuePanel.setBackground(Color.WHITE);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(color);

        valuePanel.add(valueLabel);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valuePanel, BorderLayout.CENTER);

        return card;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void loadTransactions() {
        SwingWorker<List<Transaction>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Transaction> doInBackground() throws Exception {
                statusLabel.setText("Loading transactions from database...");
                
                // Get all accounts first
                List<com.banking.model.Customer> customers = new com.banking.service.CustomerService().getAllCustomers();
                java.util.ArrayList<Transaction> allTransactions = new java.util.ArrayList<>();
                
                for (var customer : customers) {
                    List<Account> accounts = accountService.getAccountsByCustomerId(customer.getCustomerId());
                    for (Account account : accounts) {
                        allTransactions.addAll(transactionService.getTransactionsByAccountId(account.getAccountId()));
                    }
                }
                
                // Sort by date (most recent first)
                allTransactions.sort((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()));
                
                return allTransactions;
            }

            @Override
            protected void done() {
                try {
                    List<Transaction> transactions = get();
                    updateTableData(transactions);
                    updateSummary(transactions);
                    statusLabel.setText(String.format("Showing %d transactions", transactions.size()));
                } catch (Exception e) {
                    e.printStackTrace();
                    statusLabel.setText("Error loading transactions: " + e.getMessage());
                    JOptionPane.showMessageDialog(TransactionPanel.this,
                        "Error loading transactions: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void updateTableData(List<Transaction> transactions) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (Transaction transaction : transactions) {
            String amountStr = formatAmount(transaction);
            String typeDisplay = getTypeDisplay(transaction.getTransactionType());
            String colorCode = getTypeColor(transaction.getTransactionType());
            
            tableModel.addRow(new Object[]{
                transaction.getTransactionId(),
                transaction.getTransactionDate().format(formatter),
                "<html><font color='" + colorCode + "'>" + typeDisplay + "</font></html>",
                transaction.getAccountId() + (transaction.getRecipientAccountId() != null ? 
                    " → " + transaction.getRecipientAccountId() : ""),
                amountStr,
                transaction.getDescription(),
                "Completed" // All transactions in DB are completed
            });
        }
    }

    private void updateSummary(List<Transaction> transactions) {
        BigDecimal totalCredit = BigDecimal.ZERO;
        BigDecimal totalDebit = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            String type = transaction.getTransactionType();
            BigDecimal amount = transaction.getAmount();
            
            if ("DEPOSIT".equals(type) || "TRANSFER_IN".equals(type)) {
                totalCredit = totalCredit.add(amount);
            } else if ("WITHDRAWAL".equals(type) || "TRANSFER_OUT".equals(type)) {
                totalDebit = totalDebit.add(amount);
            }
        }

        totalCreditLabel.setText("$" + String.format("%,.2f", totalCredit));
        totalDebitLabel.setText("$" + String.format("%,.2f", totalDebit));
        
        BigDecimal netBalance = totalCredit.subtract(totalDebit);
        balanceLabel.setText("$" + String.format("%,.2f", netBalance));
        
        // Set color based on balance
        if (netBalance.compareTo(BigDecimal.ZERO) >= 0) {
            balanceLabel.setForeground(new Color(40, 167, 69));
        } else {
            balanceLabel.setForeground(new Color(220, 53, 69));
        }
    }

    private String formatAmount(Transaction transaction) {
        String type = transaction.getTransactionType();
        BigDecimal amount = transaction.getAmount();
        String prefix = ("DEPOSIT".equals(type) || "TRANSFER_IN".equals(type)) ? "+" : "-";
        return prefix + "$" + String.format("%,.2f", amount);
    }

    private String getTypeDisplay(String type) {
        switch (type) {
            case "DEPOSIT": return "Deposit";
            case "WITHDRAWAL": return "Withdrawal";
            case "TRANSFER_OUT": return "Transfer (Out)";
            case "TRANSFER_IN": return "Transfer (In)";
            default: return type;
        }
    }

    private String getTypeColor(String type) {
        switch (type) {
            case "DEPOSIT":
            case "TRANSFER_IN":
                return "#28a745";
            case "WITHDRAWAL":
            case "TRANSFER_OUT":
                return "#dc3545";
            default:
                return "#6c757d";
        }
    }

    private void filterTransactions() {
        String filter = (String) transactionTypeFilter.getSelectedItem();
        SwingWorker<List<Transaction>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Transaction> doInBackground() throws Exception {
                statusLabel.setText("Filtering transactions...");
                
                List<com.banking.model.Customer> customers = new com.banking.service.CustomerService().getAllCustomers();
                java.util.ArrayList<Transaction> filteredTransactions = new java.util.ArrayList<>();
                
                for (var customer : customers) {
                    List<Account> accounts = accountService.getAccountsByCustomerId(customer.getCustomerId());
                    for (Account account : accounts) {
                        List<Transaction> transactions = transactionService.getTransactionsByAccountId(account.getAccountId());
                        
                        for (Transaction t : transactions) {
                            if ("All Transactions".equals(filter)) {
                                filteredTransactions.add(t);
                            } else if ("Deposits".equals(filter) && "DEPOSIT".equals(t.getTransactionType())) {
                                filteredTransactions.add(t);
                            } else if ("Withdrawals".equals(filter) && "WITHDRAWAL".equals(t.getTransactionType())) {
                                filteredTransactions.add(t);
                            } else if ("Transfers".equals(filter) && 
                                      (t.getTransactionType().startsWith("TRANSFER"))) {
                                filteredTransactions.add(t);
                            }
                        }
                    }
                }
                
                filteredTransactions.sort((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()));
                return filteredTransactions;
            }

            @Override
            protected void done() {
                try {
                    List<Transaction> transactions = get();
                    updateTableData(transactions);
                    updateSummary(transactions);
                    statusLabel.setText(String.format("Showing %d %s", 
                        transactions.size(), filter.toLowerCase()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void searchTransactions() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadTransactions();
            return;
        }

        SwingWorker<List<Transaction>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Transaction> doInBackground() throws Exception {
                statusLabel.setText("Searching transactions...");
                
                List<com.banking.model.Customer> customers = new com.banking.service.CustomerService().getAllCustomers();
                java.util.ArrayList<Transaction> searchResults = new java.util.ArrayList<>();
                
                for (var customer : customers) {
                    List<Account> accounts = accountService.getAccountsByCustomerId(customer.getCustomerId());
                    for (Account account : accounts) {
                        List<Transaction> transactions = transactionService.getTransactionsByAccountId(account.getAccountId());
                        
                        for (Transaction t : transactions) {
                            if (String.valueOf(t.getTransactionId()).contains(searchTerm) ||
                                t.getDescription().toLowerCase().contains(searchTerm)) {
                                searchResults.add(t);
                            }
                        }
                    }
                }
                
                searchResults.sort((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()));
                return searchResults;
            }

            @Override
            protected void done() {
                try {
                    List<Transaction> transactions = get();
                    updateTableData(transactions);
                    updateSummary(transactions);
                    statusLabel.setText(String.format("Found %d transactions matching '%s'", 
                        transactions.size(), searchTerm));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void showDepositDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
            "Make Deposit", true);
        dialog.setSize(600, 500);
        dialog.setMinimumSize(new Dimension(600, 500));
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        // Create main panel with padding
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("Deposit Funds");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(45, 85, 255));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 25, 10);
        mainPanel.add(titleLabel, gbc);

        // Reset insets for form fields
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = 1;

        // Account ID field
        JLabel accountLabel = new JLabel("Account ID:");
        accountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        accountLabel.setForeground(Color.DARK_GRAY);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        mainPanel.add(accountLabel, gbc);

        JTextField accountIdField = new JTextField(15);
        accountIdField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        accountIdField.setPreferredSize(new Dimension(250, 40));
        accountIdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        mainPanel.add(accountIdField, gbc);

        // Amount field
        JLabel amountLabel = new JLabel("Amount ($):");
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        amountLabel.setForeground(Color.DARK_GRAY);
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(amountLabel, gbc);

        JTextField amountField = new JTextField(15);
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        amountField.setPreferredSize(new Dimension(250, 40));
        amountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        gbc.gridx = 1;
        mainPanel.add(amountField, gbc);

        // Description field
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        descLabel.setForeground(Color.DARK_GRAY);
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(descLabel, gbc);

        JTextField descriptionField = new JTextField(15);
        descriptionField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionField.setPreferredSize(new Dimension(250, 40));
        descriptionField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        gbc.gridx = 1;
        mainPanel.add(descriptionField, gbc);

        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        infoPanel.setBackground(new Color(240, 248, 255));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(45, 85, 255, 50), 1),
            new EmptyBorder(12, 18, 12, 18)
        ));
        
        JLabel infoIcon = new JLabel("ℹ️");
        infoIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        JLabel infoText = new JLabel("Deposits are processed immediately and reflected in balance");
        infoText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoText.setForeground(Color.DARK_GRAY);
        
        infoPanel.add(infoIcon);
        infoPanel.add(infoText);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 15, 10);
        mainPanel.add(infoPanel, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JButton depositBtn = createStyledButton("Deposit", new Color(40, 167, 69));
        depositBtn.setPreferredSize(new Dimension(140, 45));
        depositBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JButton cancelBtn = createStyledButton("Cancel", new Color(108, 117, 125));
        cancelBtn.setPreferredSize(new Dimension(140, 45));
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));

        buttonPanel.add(depositBtn);
        buttonPanel.add(cancelBtn);

        // Add panels to dialog
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners
        depositBtn.addActionListener(e -> {
            try {
                int accountId = Integer.parseInt(accountIdField.getText().trim());
                BigDecimal amount = new BigDecimal(amountField.getText().trim());
                String description = descriptionField.getText().trim();

                // Verify account exists
                Account account = accountService.getAccountById(accountId);
                if (account == null) {
                    JOptionPane.showMessageDialog(dialog,
                        "Account not found",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Perform deposit
                Transaction transaction = transactionService.deposit(accountId, amount, description);
                
                dialog.dispose();
                
                // Show success message in a nicer format
                showSuccessDialog("Deposit Successful", 
                    String.format("Amount: $%,.2f\nAccount: #%d\nTransaction ID: %d",
                        amount, accountId, transaction.getTransactionId()));
                
                loadTransactions();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Please enter valid numbers",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showWithdrawDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
            "Make Withdrawal", true);
        dialog.setSize(600, 500);
        dialog.setMinimumSize(new Dimension(600, 500));
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("Withdraw Funds");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(255, 193, 7));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 25, 10);
        mainPanel.add(titleLabel, gbc);

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = 1;

        // Account ID field
        JLabel accountLabel = new JLabel("Account ID:");
        accountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        accountLabel.setForeground(Color.DARK_GRAY);
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(accountLabel, gbc);

        JTextField accountIdField = new JTextField(15);
        accountIdField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        accountIdField.setPreferredSize(new Dimension(250, 40));
        accountIdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        gbc.gridx = 1;
        mainPanel.add(accountIdField, gbc);

        // Amount field
        JLabel amountLabel = new JLabel("Amount ($):");
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        amountLabel.setForeground(Color.DARK_GRAY);
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(amountLabel, gbc);

        JTextField amountField = new JTextField(15);
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        amountField.setPreferredSize(new Dimension(250, 40));
        amountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        gbc.gridx = 1;
        mainPanel.add(amountField, gbc);

        // Description field
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        descLabel.setForeground(Color.DARK_GRAY);
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(descLabel, gbc);

        JTextField descriptionField = new JTextField(15);
        descriptionField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionField.setPreferredSize(new Dimension(250, 40));
        descriptionField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        gbc.gridx = 1;
        mainPanel.add(descriptionField, gbc);

        // Info panel with warning
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        infoPanel.setBackground(new Color(255, 243, 205));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 193, 7, 50), 1),
            new EmptyBorder(12, 18, 12, 18)
        ));
        
        JLabel infoIcon = new JLabel("⚠️");
        infoIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        JLabel infoText = new JLabel("Ensure sufficient balance before withdrawal");
        infoText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoText.setForeground(Color.DARK_GRAY);
        
        infoPanel.add(infoIcon);
        infoPanel.add(infoText);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 15, 10);
        mainPanel.add(infoPanel, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JButton withdrawBtn = createStyledButton("Withdraw", new Color(255, 193, 7));
        withdrawBtn.setPreferredSize(new Dimension(140, 45));
        withdrawBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JButton cancelBtn = createStyledButton("Cancel", new Color(108, 117, 125));
        cancelBtn.setPreferredSize(new Dimension(140, 45));
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));

        buttonPanel.add(withdrawBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners
        withdrawBtn.addActionListener(e -> {
            try {
                int accountId = Integer.parseInt(accountIdField.getText().trim());
                BigDecimal amount = new BigDecimal(amountField.getText().trim());
                String description = descriptionField.getText().trim();

                Account account = accountService.getAccountById(accountId);
                if (account == null) {
                    JOptionPane.showMessageDialog(dialog,
                        "Account not found",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Transaction transaction = transactionService.withdraw(accountId, amount, description);
                
                dialog.dispose();
                
                showSuccessDialog("Withdrawal Successful", 
                    String.format("Amount: $%,.2f\nAccount: #%d\nTransaction ID: %d\nNew Balance: $%,.2f",
                        amount, accountId, transaction.getTransactionId(),
                        account.getBalance().subtract(amount)));
                
                loadTransactions();
                
            } catch (InsufficientFundsException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Insufficient funds: " + ex.getMessage(),
                    "Transaction Failed",
                    JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Please enter valid numbers",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showTransferDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
            "Make Transfer", true);
        dialog.setSize(650, 600);
        dialog.setMinimumSize(new Dimension(650, 600));
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("Transfer Funds");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(23, 162, 184));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 25, 10);
        mainPanel.add(titleLabel, gbc);

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = 1;

        // From Account field
        JLabel fromLabel = new JLabel("From Account:");
        fromLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        fromLabel.setForeground(Color.DARK_GRAY);
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(fromLabel, gbc);

        JTextField fromAccountField = new JTextField(15);
        fromAccountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fromAccountField.setPreferredSize(new Dimension(250, 40));
        fromAccountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        gbc.gridx = 1;
        mainPanel.add(fromAccountField, gbc);

        // To Account field
        JLabel toLabel = new JLabel("To Account:");
        toLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        toLabel.setForeground(Color.DARK_GRAY);
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(toLabel, gbc);

        JTextField toAccountField = new JTextField(15);
        toAccountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        toAccountField.setPreferredSize(new Dimension(250, 40));
        toAccountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        gbc.gridx = 1;
        mainPanel.add(toAccountField, gbc);

        // Amount field
        JLabel amountLabel = new JLabel("Amount ($):");
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        amountLabel.setForeground(Color.DARK_GRAY);
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(amountLabel, gbc);

        JTextField amountField = new JTextField(15);
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        amountField.setPreferredSize(new Dimension(250, 40));
        amountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        gbc.gridx = 1;
        mainPanel.add(amountField, gbc);

        // Description field
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        descLabel.setForeground(Color.DARK_GRAY);
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(descLabel, gbc);

        JTextField descriptionField = new JTextField(15);
        descriptionField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionField.setPreferredSize(new Dimension(250, 40));
        descriptionField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        gbc.gridx = 1;
        mainPanel.add(descriptionField, gbc);

        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        infoPanel.setBackground(new Color(229, 244, 247));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(23, 162, 184, 50), 1),
            new EmptyBorder(12, 18, 12, 18)
        ));
        
        JLabel infoIcon = new JLabel("🔄");
        infoIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        JLabel infoText = new JLabel("Transfer between accounts - both accounts must exist");
        infoText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoText.setForeground(Color.DARK_GRAY);
        
        infoPanel.add(infoIcon);
        infoPanel.add(infoText);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 15, 10);
        mainPanel.add(infoPanel, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JButton transferBtn = createStyledButton("Transfer", new Color(23, 162, 184));
        transferBtn.setPreferredSize(new Dimension(140, 45));
        transferBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JButton cancelBtn = createStyledButton("Cancel", new Color(108, 117, 125));
        cancelBtn.setPreferredSize(new Dimension(140, 45));
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));

        buttonPanel.add(transferBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners
        transferBtn.addActionListener(e -> {
            try {
                int fromAccount = Integer.parseInt(fromAccountField.getText().trim());
                int toAccount = Integer.parseInt(toAccountField.getText().trim());
                BigDecimal amount = new BigDecimal(amountField.getText().trim());
                String description = descriptionField.getText().trim();

                if (fromAccount == toAccount) {
                    JOptionPane.showMessageDialog(dialog,
                        "Cannot transfer to the same account",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Account sourceAccount = accountService.getAccountById(fromAccount);
                Account destAccount = accountService.getAccountById(toAccount);
                
                if (sourceAccount == null) {
                    JOptionPane.showMessageDialog(dialog,
                        "Source account not found",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (destAccount == null) {
                    JOptionPane.showMessageDialog(dialog,
                        "Destination account not found",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Transaction[] transactions = transactionService.transfer(fromAccount, toAccount, amount, description);
                
                dialog.dispose();
                
                showSuccessDialog("Transfer Successful", 
                    String.format("Amount: $%,.2f\nFrom: #%d\nTo: #%d\nTransaction IDs: %d and %d\nSource New Balance: $%,.2f",
                        amount, fromAccount, toAccount, 
                        transactions[0].getTransactionId(), transactions[1].getTransactionId(),
                        sourceAccount.getBalance().subtract(amount)));
                
                loadTransactions();
                
            } catch (InsufficientFundsException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Insufficient funds: " + ex.getMessage(),
                    "Transaction Failed",
                    JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Please enter valid numbers",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void viewTransactionDetails() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow != -1) {
            long transactionId = (Long) tableModel.getValueAt(selectedRow, 0);
            try {
                Transaction transaction = transactionService.getTransactionById(transactionId);
                if (transaction != null) {
                    showTransactionDetailsDialog(transaction);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error loading transaction details: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showTransactionDetailsDialog(Transaction transaction) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
            "Transaction Details", true);
        dialog.setSize(500, 450);
        dialog.setMinimumSize(new Dimension(500, 450));
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        int row = 0;
        
        addDetailRow(detailsPanel, gbc, row++, "Transaction ID:", String.valueOf(transaction.getTransactionId()));
        addDetailRow(detailsPanel, gbc, row++, "Date:", 
            transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        addDetailRow(detailsPanel, gbc, row++, "Type:", transaction.getTransactionType());
        addDetailRow(detailsPanel, gbc, row++, "Account ID:", String.valueOf(transaction.getAccountId()));
        
        if (transaction.getRecipientAccountId() != null) {
            addDetailRow(detailsPanel, gbc, row++, "Recipient Account:", 
                String.valueOf(transaction.getRecipientAccountId()));
        }
        
        addDetailRow(detailsPanel, gbc, row++, "Amount:", "$" + String.format("%,.2f", transaction.getAmount()));
        addDetailRow(detailsPanel, gbc, row++, "Description:", transaction.getDescription());

        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        closeButton.setBackground(new Color(45, 85, 255));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBorder(new EmptyBorder(12, 25, 12, 25));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(closeButton);

        dialog.add(new JScrollPane(detailsPanel), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComp.setForeground(Color.GRAY);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        panel.add(labelComp, gbc);

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueComp.setForeground(Color.BLACK);
        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(valueComp, gbc);
    }

    // Add this helper method for success dialogs
    private void showSuccessDialog(String title, String message) {
        JDialog successDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
            title, true);
        successDialog.setSize(450, 300);
        successDialog.setMinimumSize(new Dimension(450, 300));
        successDialog.setLocationRelativeTo(this);
        successDialog.setLayout(new BorderLayout());
        successDialog.getContentPane().setBackground(Color.WHITE);

        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Success icon
        JLabel iconLabel = new JLabel("✅");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Message
        JTextArea messageArea = new JTextArea(message);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageArea.setEditable(false);
        messageArea.setBackground(Color.WHITE);
        messageArea.setWrapStyleWord(true);
        messageArea.setLineWrap(true);
        messageArea.setMargin(new Insets(10, 10, 10, 10));
        
        // OK button
        JButton okButton = createStyledButton("OK", new Color(40, 167, 69));
        okButton.setPreferredSize(new Dimension(120, 40));
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        okButton.addActionListener(e -> successDialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(okButton);

        panel.add(iconLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(messageArea), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        successDialog.add(panel);
        successDialog.setVisible(true);
    }
}