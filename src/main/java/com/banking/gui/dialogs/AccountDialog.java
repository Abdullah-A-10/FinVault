package com.banking.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.banking.model.Account;
import com.banking.model.CurrentAccount;
import com.banking.model.SavingsAccount;
import com.banking.service.AccountService;
import com.banking.service.CustomerService;

public class AccountDialog extends JDialog {

    private final AccountService accountService = new AccountService();
    private final CustomerService customerService = new CustomerService();
    private final Account account;
    private boolean saved = false;

    private JComboBox<String> accountTypeCombo;
    private JTextField customerIdField, initialDepositField, interestRateField, overdraftLimitField;
    private JComboBox<String> statusCombo;
    private JButton verifyButton;
    private JLabel customerNameLabel, validationLabel;
    private boolean customerVerified = false;

    public AccountDialog(JFrame parent, Account account) {
        super(parent, true);
        this.account = account;

        setTitle(account == null ? "Open New Account" : "Edit Account");
        setSize(520, account == null ? 580 : 540);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
        if (account != null) loadAccountData();
    }

    private void initComponents() {

        // ===== FORM PANEL =====
        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(new EmptyBorder(20, 25, 20, 25));
        content.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        // ===== Account Type =====
        if (account == null) {
            content.add(createLabel("Account Type *"), gbcLabel(0, row));
            accountTypeCombo = new JComboBox<>(new String[]{"Select", "Savings", "Current"});
            styleComboBox(accountTypeCombo);
            content.add(accountTypeCombo, gbcField(1, row++));
            accountTypeCombo.addActionListener(e -> toggleFields());
        }

        // ===== Customer ID =====
        content.add(createLabel("Customer ID *"), gbcLabel(0, row));

        JPanel customerPanel = new JPanel(new BorderLayout(6, 0));
        customerPanel.setBackground(Color.WHITE);

        customerIdField = new JTextField();
        styleTextField(customerIdField);

        verifyButton = createButton("✓ Verify", new Color(45, 85, 255));
        customerNameLabel = new JLabel(" ");
        customerNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        customerPanel.add(customerIdField, BorderLayout.CENTER);
        customerPanel.add(verifyButton, BorderLayout.EAST);

        content.add(customerPanel, gbcField(1, row++));
        content.add(customerNameLabel, gbcField(1, row++));

        verifyButton.addActionListener(e -> verifyCustomer());

        // ===== Deposit =====
        content.add(createLabel("Initial Deposit *"), gbcLabel(0, row));
        initialDepositField = new JTextField("0.00");
        styleTextField(initialDepositField);
        content.add(initialDepositField, gbcField(1, row++));

        // ===== Interest =====
        content.add(createLabel("Interest Rate (%)"), gbcLabel(0, row));
        interestRateField = new JTextField("2.5");
        styleTextField(interestRateField);
        interestRateField.setVisible(false);
        content.add(interestRateField, gbcField(1, row++));

        // ===== Overdraft =====
        content.add(createLabel("Overdraft Limit ($)"), gbcLabel(0, row));
        overdraftLimitField = new JTextField("500.00");
        styleTextField(overdraftLimitField);
        overdraftLimitField.setVisible(false);
        content.add(overdraftLimitField, gbcField(1, row++));

        // ===== Status =====
        if (account != null) {
            content.add(createLabel("Status"), gbcLabel(0, row));
            statusCombo = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE", "FROZEN", "CLOSED"});
            styleComboBox(statusCombo);
            content.add(statusCombo, gbcField(1, row++));
        }

        // ===== Validation =====
        validationLabel = new JLabel(" ");
        validationLabel.setForeground(Color.RED);
        validationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        content.add(validationLabel, gbcField(0, row, 2));

        // ===== Buttons =====
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setBackground(Color.WHITE);

        JButton cancelBtn = createButton("✕ Cancel", new Color(108, 117, 125));
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = createButton(account == null ? "✓ Create Account" : "✓ Update Account", new Color(40, 167, 69));
        saveBtn.addActionListener(e -> saveAccount());

        buttons.add(cancelBtn);
        buttons.add(saveBtn);

        gbc.gridy = row + 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        content.add(buttons, gbc);

        // ===== WRAPPER PANEL (HEADER + FORM) =====
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(createHeaderPanel(), BorderLayout.NORTH);
        wrapper.add(content, BorderLayout.CENTER);

        setContentPane(wrapper);
    }

    // ===== HEADER =====
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(245, 248, 255));
        header.setBorder(BorderFactory.createEmptyBorder(18, 25, 15, 25));

        String titleText = account == null ? "Create a New Account" : "Edit Account Details";
        String subtitleText = account == null
                ? "Enter customer details to open a new bank account"
                : "Modify account information and update status";

        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 55, 140));

        JLabel subtitleLabel = new JLabel(subtitleText);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(110, 120, 145));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitleLabel);

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(220, 225, 235));

        header.add(textPanel, BorderLayout.CENTER);
        header.add(separator, BorderLayout.SOUTH);

        return header;
    }

    // ===== Helpers =====
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return lbl;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(bg.darker()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(bg); }
        });
        return btn;
    }

    private GridBagConstraints gbcLabel(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x; gbc.gridy = y;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        return gbc;
    }

    private GridBagConstraints gbcField(int x, int y) { return gbcField(x, y, 1); }
    private GridBagConstraints gbcField(int x, int y, int width) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x; gbc.gridy = y; gbc.gridwidth = width;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        return gbc;
    }

    // ===== Logic =====
    private void toggleFields() {
        if (accountTypeCombo == null) return;
        String type = (String) accountTypeCombo.getSelectedItem();
        interestRateField.setVisible("Savings".equals(type));
        overdraftLimitField.setVisible("Current".equals(type));
        revalidate(); repaint();
    }

    private void verifyCustomer() {
        String idText = customerIdField.getText().trim();
        if (idText.isEmpty()) {
            customerNameLabel.setText("⚠ Enter customer ID");
            customerNameLabel.setForeground(Color.RED);
            customerVerified = false;
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            var customer = customerService.getCustomerById(id);
            if (customer != null) {
                customerVerified = true;
                customerNameLabel.setText("✓ " + customer.getFullName());
                customerNameLabel.setForeground(new Color(40, 167, 69));
                validationLabel.setText(" ");
            } else {
                customerVerified = false;
                customerNameLabel.setText("✗ Customer not found");
                customerNameLabel.setForeground(Color.RED);
            }
        } catch (NumberFormatException e) {
            customerVerified = false;
            customerNameLabel.setText("✗ Invalid ID format");
            customerNameLabel.setForeground(Color.RED);
        } catch (Exception e) {
            customerVerified = false;
            customerNameLabel.setText("✗ Error verifying customer");
            customerNameLabel.setForeground(Color.RED);
        }
    }

    private void loadAccountData() {
        customerIdField.setText(String.valueOf(account.getCustomerId()));
        customerIdField.setEnabled(false);
        verifyButton.setEnabled(false);
        initialDepositField.setText(account.getBalance().toString());
        initialDepositField.setEnabled(false);

        if (account instanceof SavingsAccount) {
            interestRateField.setText(((SavingsAccount) account)
                    .getInterestRate().multiply(BigDecimal.valueOf(100)).toString());
            interestRateField.setVisible(true);
        } else if (account instanceof CurrentAccount) {
            overdraftLimitField.setText(((CurrentAccount) account)
                    .getOverdraftLimit().toString());
            overdraftLimitField.setVisible(true);
        }

        if (statusCombo != null) statusCombo.setSelectedItem(account.getStatus());
        customerVerified = true;
        customerNameLabel.setText("✓ Customer verified");
        customerNameLabel.setForeground(new Color(40, 167, 69));
    }

    private void saveAccount() {
        if (!validateInput()) return;

        try {
            if (account == null) {
                // CREATE NEW ACCOUNT
                int customerId = Integer.parseInt(customerIdField.getText().trim());
                BigDecimal initialDeposit = new BigDecimal(initialDepositField.getText().trim());
                String type = (String) accountTypeCombo.getSelectedItem();

                Account createdAccount = null;

                if ("Savings".equals(type)) {
                    BigDecimal interestRate = new BigDecimal(interestRateField.getText().trim())
                            .divide(new BigDecimal("100"));
                    createdAccount = accountService.createSavingsAccount(customerId, initialDeposit, interestRate);
                } else if ("Current".equals(type)) {
                    BigDecimal overdraftLimit = new BigDecimal(overdraftLimitField.getText().trim());
                    createdAccount = accountService.createCurrentAccount(customerId, initialDeposit, overdraftLimit);
                }

                if (createdAccount != null) {
                    saved = true;
                    JOptionPane.showMessageDialog(this,
                        "✓ Account created successfully!\nAccount ID: " + createdAccount.getAccountId(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }

            } else {
                // UPDATE EXISTING ACCOUNT STATUS
                String newStatus = (String) statusCombo.getSelectedItem();
                if (!newStatus.equals(account.getStatus())) {
                    boolean updated = accountService.updateAccountStatus(account.getAccountId(), newStatus);
                    if (updated) {
                        saved = true;
                        JOptionPane.showMessageDialog(this,
                            "✓ Account status updated to " + newStatus,
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                        "No changes made to account",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }
            }
        } catch (NumberFormatException e) {
            validationLabel.setText("✗ Invalid number format");
        } catch (IllegalArgumentException e) {
            validationLabel.setText("✗ " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "✗ Error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateInput() {
        // Clear previous validation
        validationLabel.setText(" ");

        // Validate for new account
        if (account == null) {
            if (accountTypeCombo.getSelectedIndex() == 0) {
                validationLabel.setText("✗ Select account type");
                return false;
            }
        }

        // Customer verification
        if (!customerVerified) {
            validationLabel.setText("✗ Verify customer ID first");
            return false;
        }

        // Initial deposit
        try {
            BigDecimal deposit = new BigDecimal(initialDepositField.getText().trim());
            if (deposit.compareTo(BigDecimal.ZERO) < 0) {
                validationLabel.setText("✗ Initial deposit cannot be negative");
                return false;
            }
        } catch (NumberFormatException e) {
            validationLabel.setText("✗ Invalid deposit amount");
            return false;
        }

        // Validate for new account type-specific fields
        if (account == null) {
            String type = (String) accountTypeCombo.getSelectedItem();
            
            if ("Savings".equals(type)) {
                try {
                    BigDecimal rate = new BigDecimal(interestRateField.getText().trim());
                    if (rate.compareTo(BigDecimal.ZERO) < 0) {
                        validationLabel.setText("✗ Interest rate cannot be negative");
                        return false;
                    }
                } catch (NumberFormatException e) {
                    validationLabel.setText("✗ Invalid interest rate");
                    return false;
                }
            } else if ("Current".equals(type)) {
                try {
                    BigDecimal limit = new BigDecimal(overdraftLimitField.getText().trim());
                    if (limit.compareTo(BigDecimal.ZERO) < 0) {
                        validationLabel.setText("✗ Overdraft limit cannot be negative");
                        return false;
                    }
                } catch (NumberFormatException e) {
                    validationLabel.setText("✗ Invalid overdraft limit");
                    return false;
                }
            }
        }

        return true;
    }

    public boolean isSaved() { return saved; }
}