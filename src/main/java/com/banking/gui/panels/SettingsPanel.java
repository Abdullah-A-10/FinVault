package com.banking.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

/**
 * Modern settings panel
 */
public class SettingsPanel extends JPanel {

    private JComboBox<String> themeCombo;
    private JComboBox<String> languageCombo;
    private JCheckBox notificationsCheck;
    private JCheckBox autoRefreshCheck;
    private JSpinner refreshIntervalSpinner;
    private JComboBox<String> dateFormatCombo;
    private JComboBox<String> currencyCombo;
    private JButton saveButton;
    private JButton resetButton;

    public SettingsPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        initComponents();
    }

    private void initComponents() {
        // Title
        JLabel titleLabel = new JLabel("⚙️ Settings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Settings content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        int row = 0;

        // Appearance Section
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel appearanceTitle = createSectionTitle("Appearance");
        contentPanel.add(appearanceTitle, gbc);
        row++;

        // Theme
        addSettingRow(contentPanel, gbc, row++, "Theme:", 
            themeCombo = createComboBox(new String[]{"Light Mode", "Dark Mode", "System Default"}));

        // Language
        addSettingRow(contentPanel, gbc, row++, "Language:", 
            languageCombo = createComboBox(new String[]{"English (US)", "English (UK)", "Spanish", "French", "German"}));

        // Date Format
        addSettingRow(contentPanel, gbc, row++, "Date Format:", 
            dateFormatCombo = createComboBox(new String[]{"MM/dd/yyyy", "dd/MM/yyyy", "yyyy-MM-dd"}));

        // Currency
        addSettingRow(contentPanel, gbc, row++, "Currency:", 
            currencyCombo = createComboBox(new String[]{"USD ($)", "EUR (€)", "GBP (£)", "JPY (¥)"}));

        // Add spacing
        gbc.gridy = row++;
        contentPanel.add(Box.createVerticalStrut(20), gbc);

        // Notifications Section
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JLabel notificationsTitle = createSectionTitle("Notifications");
        contentPanel.add(notificationsTitle, gbc);
        row++;

        // Enable Notifications
        notificationsCheck = new JCheckBox("Enable desktop notifications");
        notificationsCheck.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        notificationsCheck.setBackground(Color.WHITE);
        notificationsCheck.setSelected(true);
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.gridwidth = 1;
        contentPanel.add(notificationsCheck, gbc);

        // Email Alerts
        JCheckBox emailAlerts = new JCheckBox("Email alerts for transactions");
        emailAlerts.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emailAlerts.setBackground(Color.WHITE);
        emailAlerts.setSelected(true);
        gbc.gridy = row++;
        contentPanel.add(emailAlerts, gbc);

        // SMS Alerts
        JCheckBox smsAlerts = new JCheckBox("SMS alerts for large transactions");
        smsAlerts.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        smsAlerts.setBackground(Color.WHITE);
        smsAlerts.setSelected(false);
        gbc.gridy = row++;
        contentPanel.add(smsAlerts, gbc);

        // Add spacing
        gbc.gridy = row++;
        contentPanel.add(Box.createVerticalStrut(20), gbc);

        // System Section
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JLabel systemTitle = createSectionTitle("System");
        contentPanel.add(systemTitle, gbc);
        row++;

        // Auto Refresh
        autoRefreshCheck = new JCheckBox("Auto-refresh data");
        autoRefreshCheck.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        autoRefreshCheck.setBackground(Color.WHITE);
        autoRefreshCheck.setSelected(true);
        autoRefreshCheck.addActionListener(e -> 
            refreshIntervalSpinner.setEnabled(autoRefreshCheck.isSelected()));
        
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.gridwidth = 1;
        contentPanel.add(autoRefreshCheck, gbc);

        // Refresh Interval
        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        refreshPanel.setBackground(Color.WHITE);
        refreshPanel.add(new JLabel("Refresh interval:"));
        
        refreshIntervalSpinner = new JSpinner(new SpinnerNumberModel(30, 5, 300, 5));
        refreshIntervalSpinner.setPreferredSize(new Dimension(80, 30));
        refreshPanel.add(refreshIntervalSpinner);
        refreshPanel.add(new JLabel("seconds"));

        gbc.gridy = row++;
        contentPanel.add(refreshPanel, gbc);

        // Session Timeout
        JPanel timeoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        timeoutPanel.setBackground(Color.WHITE);
        timeoutPanel.add(new JLabel("Session timeout:"));
        
        JSpinner timeoutSpinner = new JSpinner(new SpinnerNumberModel(30, 5, 120, 5));
        timeoutSpinner.setPreferredSize(new Dimension(80, 30));
        timeoutPanel.add(timeoutSpinner);
        timeoutPanel.add(new JLabel("minutes"));

        gbc.gridy = row++;
        contentPanel.add(timeoutPanel, gbc);

        // Add spacing
        gbc.gridy = row++;
        contentPanel.add(Box.createVerticalStrut(20), gbc);

        // Database Section
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JLabel dbTitle = createSectionTitle("Database");
        contentPanel.add(dbTitle, gbc);
        row++;

        // Connection Status
        JPanel dbStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        dbStatusPanel.setBackground(Color.WHITE);
        dbStatusPanel.add(new JLabel("Status:"));
        
        JLabel statusValue = new JLabel("● Connected");
        statusValue.setForeground(new Color(40, 167, 69));
        statusValue.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dbStatusPanel.add(statusValue);

        gbc.gridx = 1;
        gbc.gridy = row++;
        contentPanel.add(dbStatusPanel, gbc);

        // Database Info
        JPanel dbInfoPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        dbInfoPanel.setBackground(Color.WHITE);
        dbInfoPanel.add(new JLabel("Host:"));
        dbInfoPanel.add(new JLabel("localhost:5432"));
        dbInfoPanel.add(new JLabel("Database:"));
        dbInfoPanel.add(new JLabel("banking_system"));

        gbc.gridy = row++;
        contentPanel.add(dbInfoPanel, gbc);

        JButton testConnectionButton = new JButton("Test Connection");
        testConnectionButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        testConnectionButton.setBackground(new Color(23, 162, 184));
        testConnectionButton.setForeground(Color.WHITE);
        testConnectionButton.setBorder(new EmptyBorder(5, 10, 5, 10));
        testConnectionButton.setFocusPainted(false);
        testConnectionButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Database connection successful!\nHost: localhost:5432\nDatabase: banking_system",
                "Connection Test",
                JOptionPane.INFORMATION_MESSAGE);
        });

        gbc.gridy = row++;
        contentPanel.add(testConnectionButton, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        buttonPanel.setBackground(Color.WHITE);

        resetButton = new JButton("Reset to Defaults");
        resetButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        resetButton.setBackground(new Color(108, 117, 125));
        resetButton.setForeground(Color.WHITE);
        resetButton.setBorder(new EmptyBorder(10, 20, 10, 20));
        resetButton.setFocusPainted(false);
        resetButton.addActionListener(e -> resetSettings());

        saveButton = new JButton("Save Settings");
        saveButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        saveButton.setBackground(new Color(40, 167, 69));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorder(new EmptyBorder(10, 20, 10, 20));
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> saveSettings());

        buttonPanel.add(resetButton);
        buttonPanel.add(saveButton);

        // Add to main panel
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(new Color(45, 85, 255));
        label.setBorder(new EmptyBorder(10, 0, 5, 0));
        return label;
    }

    private void addSettingRow(JPanel panel, GridBagConstraints gbc, int row, 
                               String labelText, JComponent component) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(Color.GRAY);
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.7;
        panel.add(component, gbc);
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setPreferredSize(new Dimension(200, 35));
        comboBox.setBackground(Color.WHITE);
        return comboBox;
    }

    private void saveSettings() {
        String theme = (String) themeCombo.getSelectedItem();
        String language = (String) languageCombo.getSelectedItem();
        
        // Here you would save settings to a properties file or database
        
        JOptionPane.showMessageDialog(this,
            "Settings saved successfully!\n" +
            "Theme: " + theme + "\n" +
            "Language: " + language,
            "Settings Saved",
            JOptionPane.INFORMATION_MESSAGE);
            
        /* Apply theme if changed
        if ("Dark Mode".equals(theme)) {
            BankingSystemMain.toggleTheme();
        }*/
    }

    private void resetSettings() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to reset all settings to default values?",
            "Reset Settings",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            themeCombo.setSelectedIndex(0);
            languageCombo.setSelectedIndex(0);
            notificationsCheck.setSelected(true);
            autoRefreshCheck.setSelected(true);
            refreshIntervalSpinner.setValue(30);
            dateFormatCombo.setSelectedIndex(0);
            currencyCombo.setSelectedIndex(0);
            
            JOptionPane.showMessageDialog(this,
                "Settings have been reset to defaults.",
                "Reset Complete",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}