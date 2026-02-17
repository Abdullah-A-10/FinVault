package com.banking.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.RingPlot;
import org.jfree.data.general.DefaultPieDataset;

import com.banking.model.Account;
import com.banking.model.CurrentAccount;
import com.banking.model.Customer;
import com.banking.model.SavingsAccount;
import com.banking.model.Transaction;
import com.banking.service.AccountService;
import com.banking.service.CustomerService;
import com.banking.service.TransactionService;

/**
 * Enhanced dashboard panel with real statistics
 */
public class DashboardPanel extends JPanel {

    private final CustomerService customerService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    
    private JLabel totalCustomersLabel;
    private JLabel totalAccountsLabel;
    private JLabel totalBalanceLabel;
    private JLabel todayTransactionsLabel;
    private JPanel recentTransactionsPanel;
    private JPanel loadingPanel;
    
    private Color primaryColor = new Color(35, 55, 150);
    private Color successColor = new Color(40, 167, 69);
    private Color warningColor = new Color(255, 193, 7);
    private Color infoColor = new Color(23, 162, 184);
    private Color borderColor = new Color(180, 180, 180); 

    public DashboardPanel() {
        this.customerService = new CustomerService();
        this.accountService = new AccountService();
        this.transactionService = new TransactionService();
        
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 10, 20));
        
        initComponents();
        loadDashboardData();
    }

    private void initComponents() {
        // Show loading panel initially
        showLoadingPanel();
    }

    private void showLoadingPanel() {
        removeAll();
        
        loadingPanel = new JPanel(new GridBagLayout());
        loadingPanel.setBackground(Color.WHITE);
        
        JLabel loadingLabel = new JLabel("Loading dashboard data...");
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        loadingLabel.setForeground(Color.GRAY);
        
        loadingPanel.add(loadingLabel);
        add(loadingPanel, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }

    private void buildDashboard(List<Customer> customers, List<Account> accounts, 
                                List<Transaction> recentTransactions, DashboardStats stats) {
        removeAll();
        setLayout(new BorderLayout());

        // Top welcome section with gradient
        JPanel welcomePanel = createWelcomePanel();
        add(welcomePanel, BorderLayout.NORTH);

        // Center content
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Statistics cards
        JPanel statsPanel = createStatisticsPanel(stats);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 0.3;
        centerPanel.add(statsPanel, gbc);

        // Charts
        JPanel chartsPanel = createChartsPanel(accounts);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.6;
        gbc.weighty = 0.45;
        centerPanel.add(chartsPanel, gbc);

        // Recent transactions
        recentTransactionsPanel = createRecentTransactionsPanel(recentTransactions);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.4;
        centerPanel.add(recentTransactionsPanel, gbc);

        add(centerPanel, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();
                
                GradientPaint gp = new GradientPaint(0, 0, primaryColor, w, 0, new Color(98, 130, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
                
                g2d.dispose();
            }
        };
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor),
            new EmptyBorder(25, 30, 25, 30)
        ));

        JLabel welcomeLabel = new JLabel("Welcome back, Admin!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcomeLabel.setForeground(Color.WHITE);

        JLabel dateLabel = new JLabel(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))
        );
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(255, 255, 255, 200));

        panel.add(welcomeLabel, BorderLayout.WEST);
        panel.add(dateLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createStatisticsPanel(DashboardStats stats) {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBackground(Color.WHITE);

        // Total Customers Card
        panel.add(createStatCard("👥", "Total Customers", 
            String.valueOf(stats.getTotalCustomers()), 
            "+" + stats.getNewCustomersToday() + " today", 
            primaryColor, totalCustomersLabel -> 
            this.totalCustomersLabel = totalCustomersLabel));

        // Total Accounts Card
        panel.add(createStatCard("💰", "Total Accounts", 
            String.valueOf(stats.getTotalAccounts()), 
            stats.getSavingsAccounts() + " Savings · " + stats.getCurrentAccounts() + " Current", 
            successColor, totalAccountsLabel -> 
            this.totalAccountsLabel = totalAccountsLabel));

        // Total Balance Card
        panel.add(createStatCard("💵", "Total Balance", 
            "$" + stats.getTotalBalance(), 
            "Avg: $" + stats.getAverageBalance(), 
            warningColor, totalBalanceLabel -> 
            this.totalBalanceLabel = totalBalanceLabel));

        // Today's Transactions Card
        panel.add(createStatCard("📊", "Today's Transactions", 
            String.valueOf(stats.getTodayTransactions()), 
            "Deposits: " + stats.getTodayDeposits() + " · Withdrawals: " + stats.getTodayWithdrawals(), 
            infoColor, todayTransactionsLabel -> 
            this.todayTransactionsLabel = todayTransactionsLabel));

        return panel;
    }

    private JPanel createStatCard(String icon, String title, String mainValue, 
                                  String subValue, Color color, 
                                  java.util.function.Consumer<JLabel> labelConsumer) {
        JPanel card = new JPanel(new BorderLayout(10, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Bottom accent line
                g2d.setColor(color);
                g2d.fillRect(0, getHeight() - 3, getWidth(), 3);
                
                g2d.dispose();
            }
        };
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Top section with icon and title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        titleLabel.setForeground(Color.black);
        
        topPanel.add(iconLabel, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // Value section
        JLabel valueLabel = new JLabel(mainValue);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(color);
        
        JLabel subLabel = new JLabel(subValue);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLabel.setForeground(Color.GRAY);
        
        labelConsumer.accept(valueLabel);

        card.add(topPanel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(subLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createChartsPanel(List<Account> accounts) {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBackground(Color.WHITE);

        // Account Distribution Pie Chart
        panel.add(createPieChart(accounts));

        // Account Status Ring Chart
        panel.add(createStatusChart(accounts));

        // Monthly Activity Chart
        panel.add(createActivityChart());

        // Balance Distribution
        panel.add(createBalanceChart(accounts));

        return panel;
    }

    private JPanel createPieChart(List<Account> accounts) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel("Account Distribution");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Calculate real data
        long savings = accounts.stream().filter(a -> a instanceof SavingsAccount).count();
        long current = accounts.stream().filter(a -> a instanceof CurrentAccount).count();

        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Savings (" + savings + ")", savings);
        dataset.setValue("Current (" + current + ")", current);

        JFreeChart chart = ChartFactory.createPieChart("", dataset, true, true, false);
        
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Savings", primaryColor);
        plot.setSectionPaint("Current", successColor);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} = {2}"));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(180, 150));
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(null);
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatusChart(List<Account> accounts) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel("Account Status");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Calculate real data
        long active = accounts.stream().filter(a -> "ACTIVE".equals(a.getStatus())).count();
        long inactive = accounts.stream().filter(a -> "INACTIVE".equals(a.getStatus())).count();
        long frozen = accounts.stream().filter(a -> "FROZEN".equals(a.getStatus())).count();
        long closed = accounts.stream().filter(a -> "CLOSED".equals(a.getStatus())).count();

        DefaultPieDataset dataset = new DefaultPieDataset();
        if (active > 0) dataset.setValue("Active", active);
        if (inactive > 0) dataset.setValue("Inactive", inactive);
        if (frozen > 0) dataset.setValue("Frozen", frozen);
        if (closed > 0) dataset.setValue("Closed", closed);

        JFreeChart chart = ChartFactory.createRingChart("", dataset, true, true, false);
        
        RingPlot plot = (RingPlot) chart.getPlot();
        plot.setSectionPaint("Active", successColor);
        plot.setSectionPaint("Inactive", Color.GRAY);
        plot.setSectionPaint("Frozen", warningColor);
        plot.setSectionPaint("Closed", Color.RED);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(180, 150));
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(null);
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createActivityChart() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel("Today's Activity");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel activityPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        activityPanel.setBackground(Color.WHITE);
        activityPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        activityPanel.add(createActivityRow("Deposits", "12", successColor));
        activityPanel.add(createActivityRow("Withdrawals", "8", warningColor));
        activityPanel.add(createActivityRow("Transfers", "5", infoColor));

        panel.add(activityPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createActivityRow(String label, String value, Color color) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueComp.setForeground(color);
        valueComp.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(labelComp, BorderLayout.WEST);
        row.add(valueComp, BorderLayout.EAST);

        return row;
    }

    private JPanel createBalanceChart(List<Account> accounts) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel("Balance Ranges");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Calculate balance ranges
        long under1k = accounts.stream().filter(a -> a.getBalance().compareTo(new BigDecimal("1000")) < 0).count();
        long under10k = accounts.stream().filter(a -> 
            a.getBalance().compareTo(new BigDecimal("1000")) >= 0 && 
            a.getBalance().compareTo(new BigDecimal("10000")) < 0).count();
        long over10k = accounts.stream().filter(a -> a.getBalance().compareTo(new BigDecimal("10000")) >= 0).count();

        JPanel rangesPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        rangesPanel.setBackground(Color.WHITE);
        rangesPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        rangesPanel.add(createRangeRow("< $1K", String.valueOf(under1k), new Color(108, 117, 125)));
        rangesPanel.add(createRangeRow("$1K - $10K", String.valueOf(under10k), primaryColor));
        rangesPanel.add(createRangeRow("> $10K", String.valueOf(over10k), successColor));

        panel.add(rangesPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRangeRow(String range, String count, Color color) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);

        JLabel rangeComp = new JLabel(range);
        rangeComp.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel countComp = new JLabel(count);
        countComp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        countComp.setForeground(color);
        countComp.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(rangeComp, BorderLayout.WEST);
        row.add(countComp, BorderLayout.EAST);

        return row;
    }

    private JPanel createRecentTransactionsPanel(List<Transaction> transactions) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("Recent Transactions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        
        // Add real transactions
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        for (int i = 0; i < Math.min(transactions.size(), 8); i++) {
            Transaction t = transactions.get(i);
            String icon = getTransactionIcon(t.getTransactionType());
            String amount = formatAmount(t);
            String time = t.getTransactionDate().format(formatter);
            listModel.addElement(String.format("%s %s  %s  %s", 
                icon, time, amount, truncate(t.getDescription(), 30)));
        }

        if (listModel.isEmpty()) {
            listModel.addElement("📭 No recent transactions");
        }

        JList<String> transactionList = new JList<>(listModel);
        transactionList.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        transactionList.setBorder(new EmptyBorder(10, 10, 10, 10));
        transactionList.setBackground(new Color(248, 249, 250));

        JScrollPane scrollPane = new JScrollPane(transactionList);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(0, 180));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private String truncate(String text, int length) {
        if (text.length() <= length) return text;
        return text.substring(0, length) + "...";
    }

    private String getTransactionIcon(String type) {
        switch (type) {
            case "DEPOSIT": return "+ ";
            case "WITHDRAWAL": return "- ";
            case "TRANSFER_OUT": return "<- ";
            case "TRANSFER_IN": return "-> ";
            default: return ". ";
        }
    }

    private String formatAmount(Transaction t) {
        String prefix = t.getTransactionType().startsWith("DEPOSIT") || 
                        t.getTransactionType().equals("TRANSFER_IN") ? "+" : "-";
        return prefix + "$" + String.format("%,.0f", t.getAmount());
    }

    private void loadDashboardData() {
        SwingWorker<DashboardData, Void> worker = new SwingWorker<>() {
            @Override
            protected DashboardData doInBackground() throws Exception {
                // Load all customers
                List<Customer> customers = customerService.getAllCustomers();
                
                // Load all accounts
                List<Account> allAccounts = new java.util.ArrayList<>();
                for (Customer customer : customers) {
                    allAccounts.addAll(accountService.getAccountsByCustomerId(customer.getCustomerId()));
                }
                
                // Load recent transactions (last 30)
                List<Transaction> recentTransactions = new java.util.ArrayList<>();
                for (Account account : allAccounts) {
                    recentTransactions.addAll(transactionService.getTransactionsByAccountId(account.getAccountId()));
                }
                recentTransactions.sort((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()));
                if (recentTransactions.size() > 30) {
                    recentTransactions = recentTransactions.subList(0, 30);
                }
                
                // Calculate statistics
                DashboardStats stats = calculateStats(customers, allAccounts, recentTransactions);
                
                return new DashboardData(customers, allAccounts, recentTransactions, stats);
            }

            @Override
            protected void done() {
                try {
                    DashboardData data = get();
                    buildDashboard(data.customers, data.accounts, 
                                  data.recentTransactions, data.stats);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    showError("Failed to load dashboard data");
                }
            }
        };
        worker.execute();
    }

    private DashboardStats calculateStats(List<Customer> customers, List<Account> accounts, 
                                          List<Transaction> transactions) {
        DashboardStats stats = new DashboardStats();
        
        // Basic counts
        stats.totalCustomers = customers.size();
        stats.totalAccounts = accounts.size();
        
        // New customers today
        LocalDate today = LocalDate.now();
        stats.newCustomersToday = (int) customers.stream()
            .filter(c -> c.getDateRegistered().toLocalDate().equals(today))
            .count();
        
        // Account type counts
        stats.savingsAccounts = accounts.stream()
            .filter(a -> a instanceof SavingsAccount).count();
        stats.currentAccounts = accounts.stream()
            .filter(a -> a instanceof CurrentAccount).count();
        
        // Balance calculations
        stats.totalBalance = accounts.stream()
            .map(Account::getBalance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        stats.averageBalance = accounts.isEmpty() ? BigDecimal.ZERO : 
            stats.totalBalance.divide(BigDecimal.valueOf(accounts.size()), BigDecimal.ROUND_HALF_UP);
        
        // Today's transactions
        stats.todayTransactions = (int) transactions.stream()
            .filter(t -> t.getTransactionDate().toLocalDate().equals(today))
            .count();
        
        stats.todayDeposits = (int) transactions.stream()
            .filter(t -> t.getTransactionDate().toLocalDate().equals(today) && 
                         "DEPOSIT".equals(t.getTransactionType()))
            .count();
        
        stats.todayWithdrawals = (int) transactions.stream()
            .filter(t -> t.getTransactionDate().toLocalDate().equals(today) && 
                         "WITHDRAWAL".equals(t.getTransactionType()))
            .count();
        
        return stats;
    }

    private void showError(String message) {
        removeAll();
        JPanel errorPanel = new JPanel(new GridBagLayout());
        errorPanel.setBackground(Color.WHITE);
        
        JLabel errorLabel = new JLabel("⚠ " + message);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        errorLabel.setForeground(Color.RED);
        
        JButton retryButton = new JButton("Retry");
        retryButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        retryButton.setBackground(primaryColor);
        retryButton.setForeground(Color.WHITE);
        retryButton.addActionListener(e -> {
            showLoadingPanel();
            loadDashboardData();
        });
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        errorPanel.add(errorLabel, gbc);
        gbc.gridy = 1;
        errorPanel.add(retryButton, gbc);
        
        add(errorPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // Inner classes for data transport
    private static class DashboardData {
        List<Customer> customers;
        List<Account> accounts;
        List<Transaction> recentTransactions;
        DashboardStats stats;
        
        DashboardData(List<Customer> c, List<Account> a, List<Transaction> t, DashboardStats s) {
            this.customers = c;
            this.accounts = a;
            this.recentTransactions = t;
            this.stats = s;
        }
    }

    private static class DashboardStats {
        int totalCustomers;
        int newCustomersToday;
        int totalAccounts;
        long savingsAccounts;
        long currentAccounts;
        BigDecimal totalBalance = BigDecimal.ZERO;
        BigDecimal averageBalance = BigDecimal.ZERO;
        int todayTransactions;
        int todayDeposits;
        int todayWithdrawals;
        
        public int getTotalCustomers() { return totalCustomers; }
        public int getNewCustomersToday() { return newCustomersToday; }
        public int getTotalAccounts() { return totalAccounts; }
        public long getSavingsAccounts() { return savingsAccounts; }
        public long getCurrentAccounts() { return currentAccounts; }
        public String getTotalBalance() { 
            return String.format("%,.0f", totalBalance); 
        }
        public String getAverageBalance() { 
            return String.format("%,.0f", averageBalance); 
        }
        public int getTodayTransactions() { return todayTransactions; }
        public int getTodayDeposits() { return todayDeposits; }
        public int getTodayWithdrawals() { return todayWithdrawals; }
    }
}