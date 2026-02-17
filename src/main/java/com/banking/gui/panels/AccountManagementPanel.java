package com.banking.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import com.banking.gui.dialogs.AccountDialog;
import com.banking.model.Account;
import com.banking.model.CurrentAccount;
import com.banking.model.Customer;
import com.banking.model.SavingsAccount;
import com.banking.service.AccountService;
import com.banking.service.CustomerService;

/**
 *  account management panel 
 */
public class AccountManagementPanel extends JPanel {

    private final AccountService accountService;
    private final CustomerService customerService;
    
    private JTable accountTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> accountTypeFilter;
    private JTextField searchField;
    private JButton refreshButton;
    private JButton addButton;
    private JButton editButton;
    private JButton viewDetailsButton;
    private JButton applyInterestButton;
    private JLabel statusLabel;
    private JLabel totalBalanceLabel;
    private JLabel totalAccountsLabel;

    public AccountManagementPanel() {
        this.accountService = new AccountService();
        this.customerService = new CustomerService();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        initComponents();
        loadAccounts();
    }

    private void initComponents() {
        // Top toolbar
        JPanel toolbarPanel = createToolbar();
        add(toolbarPanel, BorderLayout.NORTH);

        // Center table
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Bottom status panel
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Left panel - filters and search
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(Color.WHITE);

        JLabel filterLabel = new JLabel("Filter by:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        accountTypeFilter = new JComboBox<>(new String[]{"All Accounts", "Savings", "Current", "Active", "Closed", "Frozen"});
        accountTypeFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        accountTypeFilter.setPreferredSize(new Dimension(150, 35));
        accountTypeFilter.addActionListener(e -> filterAccounts());

        searchField = new JTextField(15);
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        searchField.putClientProperty("JTextField.placeholderText", "Search by ID, customer...");

        JButton searchButton = createStyledButton("🔍 Search", new Color(45, 85, 255));
        searchButton.addActionListener(e -> searchAccounts());

        refreshButton = createStyledButton("🔄 Refresh", new Color(108, 117, 125));
        refreshButton.addActionListener(e -> loadAccounts());

        leftPanel.add(filterLabel);
        leftPanel.add(accountTypeFilter);
        leftPanel.add(searchField);
        leftPanel.add(searchButton);
        leftPanel.add(refreshButton);

        // Right panel - action buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(Color.WHITE);

        addButton = createStyledButton("+ New Account", new Color(40, 167, 69));
        addButton.addActionListener(e -> addAccount());

        editButton = createStyledButton("- Edit / Status", new Color(255, 193, 7));
        editButton.setEnabled(false);
        editButton.addActionListener(e -> editAccount());

        viewDetailsButton = createStyledButton("* Details", new Color(23, 162, 184));
        viewDetailsButton.setEnabled(false);
        viewDetailsButton.addActionListener(e -> viewAccountDetails());

        applyInterestButton = createStyledButton("$ Apply Interest", new Color(111, 66, 193));
        applyInterestButton.addActionListener(e -> applyInterestToAll());

        rightPanel.add(addButton);
        rightPanel.add(editButton);
        rightPanel.add(viewDetailsButton);
        rightPanel.add(applyInterestButton);

        toolbar.add(leftPanel, BorderLayout.WEST);
        toolbar.add(rightPanel, BorderLayout.EAST);

        return toolbar;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));

        // Create table model
        String[] columns = {"ID", "Type", "Customer", "Balance $", "Status", "Interest/Overdraft", "Opened"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return BigDecimal.class;
                return String.class;
            }
        };

        
        accountTable = new JTable(tableModel) {
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component comp = super.prepareRenderer(renderer, row, column);

        // Get account type and status
        String type = (String) getValueAt(row, 1);   
        String status = (String) getValueAt(row, 4); 

        // Set row background based on account type
        if (!isRowSelected(row)) {
            if ("Savings".equalsIgnoreCase(type)) {
                comp.setBackground(new Color(235, 255, 235)); 
            } else if ("Current".equalsIgnoreCase(type)) {
                comp.setBackground(new Color(242, 242, 255)); 
            } else {
                comp.setBackground(Color.WHITE);
            }
        } else {
            comp.setBackground(new Color(45, 85, 255, 50));
        }

        // Set status text color
        if (column == 4 && status != null) { // Status column
            switch (status.trim()) {
                case "● ACTIVE":
                    comp.setForeground(new Color(40, 167, 69));
                    break;
                case "● INACTIVE":
                    comp.setForeground(new Color(255, 193, 7)); 
                    break;
                case "● FROZEN":
                    comp.setForeground(new Color(108, 117, 155)); 
                    break;
                case "● CLOSED":
                    comp.setForeground(new Color(220, 53, 69)); 
                    break;
                default:
                    comp.setForeground(Color.BLACK);
            }
        } else {
            comp.setForeground(Color.BLACK);
        }
        // Sleek bottom separator for each row
        if (comp instanceof JComponent) {
            ((JComponent) comp).setBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200))
            );
        }
        return comp;
    }
};

        
        accountTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        accountTable.setRowHeight(35);
        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountTable.setShowGrid(false);
        accountTable.setIntercellSpacing(new Dimension(0, 0));
        accountTable.setBackground(Color.WHITE);
        accountTable.setSelectionBackground(new Color(45, 85, 255, 50));
        accountTable.getColumnModel().getColumn(2).setPreferredWidth(100);

        // Style table header
        JTableHeader header = accountTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(Color.BLACK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(45, 85, 255)));

        // Add selection listener
        accountTable.getSelectionModel().addListSelectionListener(e -> {
            boolean rowSelected = accountTable.getSelectedRow() != -1;
            editButton.setEnabled(rowSelected);
            viewDetailsButton.setEnabled(rowSelected);
        });

        // Add double-click listener
        accountTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editAccount();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(accountTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        statsPanel.setBackground(Color.WHITE);

        totalAccountsLabel = new JLabel("Total Accounts: 0");
        totalAccountsLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        totalAccountsLabel.setForeground(new Color(45, 85, 255));

        totalBalanceLabel = new JLabel("Total Balance: $0.00");
        totalBalanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        totalBalanceLabel.setForeground(new Color(40, 167, 69));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(Color.GRAY);

        statsPanel.add(totalAccountsLabel);
        statsPanel.add(totalBalanceLabel);
        
        panel.add(statsPanel, BorderLayout.WEST);
        panel.add(statusLabel, BorderLayout.EAST);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(8, 12, 8, 12));
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

    private void loadAccounts() {
        SwingWorker<List<Account>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Account> doInBackground() throws Exception {
                statusLabel.setText("Loading accounts from database...");
                List<Customer> customers = customerService.getAllCustomers();
                List<Account> allAccounts = new ArrayList<>();
                
                for (Customer customer : customers) {
                    allAccounts.addAll(accountService.getAccountsByCustomerId(customer.getCustomerId()));
                }
                
                // Sort by account ID (newest first)
                allAccounts.sort((a1, a2) -> Integer.compare(a2.getAccountId(), a1.getAccountId()));
                
                return allAccounts;
            }

            @Override
            protected void done() {
                try {
                    List<Account> accounts = get();
                    updateTableData(accounts);
                    updateStatistics(accounts);
                    statusLabel.setText(String.format("Loaded %d accounts", accounts.size()));
                } catch (Exception e) {
                    e.printStackTrace();
                    statusLabel.setText("Error loading accounts");
                    JOptionPane.showMessageDialog(AccountManagementPanel.this,
                        "Error loading accounts: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void updateTableData(List<Account> accounts) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (Account account : accounts) {
            String customerName = "";
            try {
                Customer customer = customerService.getCustomerById(account.getCustomerId());
                if (customer != null) {
                    customerName = customer.getFullName() + " (ID: " + customer.getCustomerId() + ")";
                }
            } catch (Exception e) {
                customerName = "ID: " + account.getCustomerId();
            }

            String extraInfo = "";
            if (account instanceof SavingsAccount) {
                SavingsAccount sa = (SavingsAccount) account;
                extraInfo = String.format("%.2f%%", sa.getInterestRate().multiply(new BigDecimal("100")));
            } else if (account instanceof CurrentAccount) {
                CurrentAccount ca = (CurrentAccount) account;
                extraInfo = "$" + String.format("%,.2f", ca.getOverdraftLimit());
            }

            tableModel.addRow(new Object[]{
                account.getAccountId(),
                account.getAccountType(),
                customerName,
                account.getBalance(),
                getStatusWithIcon(account.getStatus()),
                extraInfo,
                account.getDateOpened().format(formatter)
            });
        }
    }

private String getStatusWithIcon(String status) {
    switch (status) {
        case "ACTIVE": return "   ● ACTIVE";
        case "INACTIVE": return "   ● INACTIVE";
        case "FROZEN": return "   ● FROZEN";
        case "CLOSED": return "   ● CLOSED";
        default: return status;
    }
}



    private void updateStatistics(List<Account> accounts) {
        int totalCount = accounts.size();
        BigDecimal totalBalance = accounts.stream()
            .map(Account::getBalance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        totalAccountsLabel.setText("Total Accounts: " + totalCount);
        totalBalanceLabel.setText("Total Balance: $" + String.format("%,.2f", totalBalance));
    }

    private void filterAccounts() {
        String filter = (String) accountTypeFilter.getSelectedItem();
        
        SwingWorker<List<Account>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Account> doInBackground() throws Exception {
                statusLabel.setText("Filtering accounts...");
                List<Customer> customers = customerService.getAllCustomers();
                List<Account> filteredAccounts = new ArrayList<>();
                
                for (Customer customer : customers) {
                    List<Account> accounts = accountService.getAccountsByCustomerId(customer.getCustomerId());
                    for (Account account : accounts) {
                        boolean matches = false;
                        
                        switch (filter) {
                            case "All Accounts":
                                matches = true;
                                break;
                            case "Savings":
                                matches = account instanceof SavingsAccount;
                                break;
                            case "Current":
                                matches = account instanceof CurrentAccount;
                                break;
                            case "Active":
                                matches = "ACTIVE".equals(account.getStatus());
                                break;
                            case "Closed":
                                matches = "CLOSED".equals(account.getStatus());
                                break;
                            case "Frozen":
                                matches = "FROZEN".equals(account.getStatus());
                                break;
                        }
                        
                        if (matches) {
                            filteredAccounts.add(account);
                        }
                    }
                }
                
                filteredAccounts.sort((a1, a2) -> Integer.compare(a2.getAccountId(), a1.getAccountId()));
                return filteredAccounts;
            }

            @Override
            protected void done() {
                try {
                    List<Account> accounts = get();
                    updateTableData(accounts);
                    updateStatistics(accounts);
                    statusLabel.setText(String.format("Showing %d %s", accounts.size(), filter.toLowerCase()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void searchAccounts() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadAccounts();
            return;
        }

        SwingWorker<List<Account>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Account> doInBackground() throws Exception {
                statusLabel.setText("Searching accounts...");
                List<Customer> customers = customerService.getAllCustomers();
                List<Account> searchResults = new ArrayList<>();
                
                for (Customer customer : customers) {
                    List<Account> accounts = accountService.getAccountsByCustomerId(customer.getCustomerId());
                    for (Account account : accounts) {
                        if (String.valueOf(account.getAccountId()).contains(searchTerm) ||
                            customer.getFullName().toLowerCase().contains(searchTerm) ||
                            customer.getEmail().toLowerCase().contains(searchTerm)) {
                            searchResults.add(account);
                        }
                    }
                }
                
                searchResults.sort((a1, a2) -> Integer.compare(a2.getAccountId(), a1.getAccountId()));
                return searchResults;
            }

            @Override
            protected void done() {
                try {
                    List<Account> accounts = get();
                    updateTableData(accounts);
                    updateStatistics(accounts);
                    statusLabel.setText(String.format("Found %d accounts matching '%s'", 
                        accounts.size(), searchTerm));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void addAccount() {
        AccountDialog dialog = new AccountDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadAccounts();
            JOptionPane.showMessageDialog(this,
                "Account created successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editAccount() {
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow != -1) {
            int accountId = (int) tableModel.getValueAt(selectedRow, 0);
            editAccount(accountId);
        }
    }

    private void editAccount(int accountId) {
        try {
            Account account = accountService.getAccountById(accountId);
            if (account != null) {
                showEditStatusDialog(account);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading account: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Edit dialog focused on status management 
     */
    private void showEditStatusDialog(Account account) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
            "Manage Account - #" + account.getAccountId(), true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        int row = 0;

        // Account Info Header
        JLabel headerLabel = new JLabel(account.getAccountType() + " ACCOUNT #" + account.getAccountId());
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setForeground(new Color(45, 85, 255));
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        mainPanel.add(headerLabel, gbc);

        // Customer Info
        try {
            Customer customer = customerService.getCustomerById(account.getCustomerId());
            if (customer != null) {
                JLabel customerLabel = new JLabel("Customer: " + customer.getFullName());
                customerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                gbc.gridx = 0;
                gbc.gridy = row++;
                gbc.gridwidth = 2;
                mainPanel.add(customerLabel, gbc);
            }
        } catch (Exception e) {
            // Ignore
        }

        // Current Balance
        JLabel balanceLabel = new JLabel("Current Balance: $" + String.format("%,.2f", account.getBalance()));
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        balanceLabel.setForeground(new Color(40, 167, 69));
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        mainPanel.add(balanceLabel, gbc);

        // Separator
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(200, 200, 200));
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(separator, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Current Status Display
        JPanel currentStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        currentStatusPanel.setBackground(Color.WHITE);
        
        JLabel currentStatusLabel = new JLabel("Current Status:");
        currentStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JLabel statusValueLabel = new JLabel(getStatusWithIcon(account.getStatus()));
        statusValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        currentStatusPanel.add(currentStatusLabel);
        currentStatusPanel.add(statusValueLabel);
        
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        mainPanel.add(currentStatusPanel, gbc);

        // Status Management Section
        JLabel statusSectionLabel = new JLabel("Change Account Status:");
        statusSectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusSectionLabel.setForeground(new Color(45, 85, 255));
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        mainPanel.add(statusSectionLabel, gbc);

        // Status Change Buttons
        JPanel statusButtonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        statusButtonPanel.setBackground(Color.WHITE);
        statusButtonPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JButton activeButton = createStatusButton("⚪Activate", new Color(40, 167, 69));
        JButton freezeButton = createStatusButton("⚪ Freeze", new Color(108, 117, 125));
        JButton inactiveButton = createStatusButton("⚪ Inactive", new Color(255, 193, 7));
        JButton closeButton = createStatusButton("🔒 Close", new Color(220, 53, 69));

        activeButton.addActionListener(e -> updateAccountStatus(account, "ACTIVE", dialog));
        freezeButton.addActionListener(e -> updateAccountStatus(account, "FROZEN", dialog));
        inactiveButton.addActionListener(e -> updateAccountStatus(account, "INACTIVE", dialog));
        closeButton.addActionListener(e -> {
            if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
                JOptionPane.showMessageDialog(dialog,
                    "Cannot close account with non-zero balance.\nCurrent balance: $" + 
                    String.format("%,.2f", account.getBalance()),
                    "Cannot Close Account",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            updateAccountStatus(account, "CLOSED", dialog);
        });

        // Disable current status button
        switch (account.getStatus()) {
            case "ACTIVE": activeButton.setEnabled(false); break;
            case "FROZEN": freezeButton.setEnabled(false); break;
            case "INACTIVE": inactiveButton.setEnabled(false); break;
            case "CLOSED": closeButton.setEnabled(false); break;
        }

        statusButtonPanel.add(activeButton);
        statusButtonPanel.add(freezeButton);
        statusButtonPanel.add(inactiveButton);
        statusButtonPanel.add(closeButton);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        mainPanel.add(statusButtonPanel, gbc);

        // Warning label
        JLabel warningLabel = new JLabel("Note: Status changes affect account operations immediately");
        warningLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        warningLabel.setForeground(Color.GRAY);
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        mainPanel.add(warningLabel, gbc);

        // Close button only
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        closeBtn.setBackground(new Color(108, 117, 125));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setBorder(new EmptyBorder(10, 30, 10, 30));
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(closeBtn);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JButton createStatusButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
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

    private void updateAccountStatus(Account account, String newStatus, JDialog dialog) {
        String currentStatus = account.getStatus();
        
        if (currentStatus.equals(newStatus)) {
            JOptionPane.showMessageDialog(dialog,
                "Account is already " + currentStatus,
                "No Change",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(dialog,
            "Change account status from " + currentStatus + " to " + newStatus + "?",
            "Confirm Status Change",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean updated = accountService.updateAccountStatus(account.getAccountId(), newStatus);
                if (updated) {
                    account.setStatus(newStatus);
                    JOptionPane.showMessageDialog(dialog,
                        "Account status updated to " + newStatus,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadAccounts();
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                    "Error updating status: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
        private void viewAccountDetails() {
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow != -1) {
            int accountId = (int) tableModel.getValueAt(selectedRow, 0);
            viewAccountDetails(accountId);
        }
    }

    private void viewAccountDetails(int accountId) {
        try {
            Account account = accountService.getAccountById(accountId);
            if (account != null) {
                showAccountDetailsDialog(account);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading account details: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAccountDetailsDialog(Account account) {

        JDialog dialog = new JDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Account Details - #" + account.getAccountId(),
                true
        );

        dialog.setSize(550, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(new EmptyBorder(20,20,20,20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // ===== HEADER =====
        JLabel typeLabel = new JLabel(account.getAccountType() + " ACCOUNT");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        typeLabel.setForeground(new Color(45,85,255));

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        detailsPanel.add(typeLabel, gbc);

        gbc.gridwidth = 1;

        // ===== BASIC DETAILS =====
        addDetailRow(detailsPanel, gbc, row++, "Account ID:",
                String.valueOf(account.getAccountId()));

        try {
            Customer customer = customerService.getCustomerById(account.getCustomerId());
            addDetailRow(detailsPanel, gbc, row++, "Customer:",
                    customer != null
                            ? customer.getFullName() + " (ID: " + account.getCustomerId() + ")"
                            : "ID: " + account.getCustomerId());
        } catch (Exception e) {
            addDetailRow(detailsPanel, gbc, row++, "Customer ID:",
                    String.valueOf(account.getCustomerId()));
        }

        addDetailRow(detailsPanel, gbc, row++, "Balance:",
                "$" + String.format("%,.2f", account.getBalance()));

        addDetailRow(detailsPanel, gbc, row++, "Status:", account.getStatus());

        addDetailRow(detailsPanel, gbc, row++, "Date Opened:",
                account.getDateOpened().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // ===== SEPARATOR =====
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(200,200,200));

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        detailsPanel.add(separator, gbc);

        gbc.gridwidth = 1;

        // ===== TYPE SPECIFIC DETAILS =====
        if (account instanceof SavingsAccount) {

            SavingsAccount sa = (SavingsAccount) account;

            addDetailRow(detailsPanel, gbc, row++, "Account Type:", "Savings Account");
            addDetailRow(detailsPanel, gbc, row++, "Interest Rate:",
                    String.format("%.2f%%",
                            sa.getInterestRate().multiply(new BigDecimal("100"))));

            addDetailRow(detailsPanel, gbc, row++, "Minimum Balance:",
                    "$" + String.format("%,.2f", SavingsAccount.getMinimumBalance()));

            addDetailRow(detailsPanel, gbc, row++, "Monthly Interest:",
                    "$" + String.format("%,.2f", sa.calculateInterest()));

        } else if (account instanceof CurrentAccount) {

            CurrentAccount ca = (CurrentAccount) account;

            addDetailRow(detailsPanel, gbc, row++, "Account Type:", "Current Account");

            addDetailRow(detailsPanel, gbc, row++, "Overdraft Limit:",
                    "$" + String.format("%,.2f", ca.getOverdraftLimit()));

            addDetailRow(detailsPanel, gbc, row++, "Available Balance:",
                    "$" + String.format("%,.2f", ca.getAvailableBalance()));

            addDetailRow(detailsPanel, gbc, row++, "Overdrawn:",
                    ca.isOverdrawn() ? "Yes" : "No");
        }

        // Push everything to top
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weighty = 1;
        detailsPanel.add(Box.createVerticalGlue(), gbc);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        buttonPanel.setBackground(Color.WHITE);

        JButton editButton = new JButton("Manage Status");
        editButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        editButton.setBackground(new Color(255,193,7));
        editButton.setForeground(Color.WHITE);
        editButton.setBorder(new EmptyBorder(10,25,10,25));
        editButton.setFocusPainted(false);
        editButton.addActionListener(e -> {
            dialog.dispose();
            editAccount(account.getAccountId());
        });

        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setBackground(new Color(108,117,125));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBorder(new EmptyBorder(10,25,10,25));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(editButton);
        buttonPanel.add(closeButton);

        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }


    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row,
                              String label, String value) {

        gbc.gridy = row;

        gbc.gridx = 0;
        gbc.weightx = 0.35;

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelComp.setForeground(new Color(110,110,110));
        panel.add(labelComp, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        valueComp.setForeground(Color.BLACK);
        panel.add(valueComp, gbc);
    }
    private void applyInterestToAll() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Apply interest to ALL active savings accounts?",
            "Apply Interest to All",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Integer, Void> worker = new SwingWorker<>() {
                @Override
                protected Integer doInBackground() throws Exception {
                    statusLabel.setText("Applying interest to savings accounts...");
                    return accountService.applyInterestToSavingsAccounts();
                }

                @Override
                protected void done() {
                    try {
                        int count = get();
                        JOptionPane.showMessageDialog(AccountManagementPanel.this,
                            "Interest applied to " + count + " savings accounts",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadAccounts();
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(AccountManagementPanel.this,
                            "Error applying interest: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
}