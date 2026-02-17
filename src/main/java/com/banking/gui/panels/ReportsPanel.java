package com.banking.gui.panels;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Modern reports panel with charts and analytics
 */
public class ReportsPanel extends JPanel {

    private JComboBox<String> reportTypeCombo;
    private JComboBox<String> timeRangeCombo;
    private JButton generateButton;
    private JButton exportButton;
    private JPanel chartContainer;
    private JPanel summaryPanel;

    public ReportsPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        initComponents();
    }

    private void initComponents() {
        // Top controls
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);

        // Center - charts
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        centerPanel.setBackground(Color.WHITE);
        
        chartContainer = new JPanel(new CardLayout());
        chartContainer.setBackground(Color.WHITE);
        
        // Add chart panels
        chartContainer.add(createAccountDistributionChart(), "DISTRIBUTION");
        chartContainer.add(createMonthlyTransactionChart(), "TRANSACTIONS");
        chartContainer.add(createCustomerGrowthChart(), "GROWTH");
        chartContainer.add(createBalanceTrendChart(), "TRENDS");
        
        centerPanel.add(createChartWrapper("Account Distribution", chartContainer));
        centerPanel.add(createChartWrapper("Monthly Transactions", createMonthlyTransactionChart()));
        centerPanel.add(createChartWrapper("Customer Growth", createCustomerGrowthChart()));
        centerPanel.add(createChartWrapper("Balance Trends", createBalanceTrendChart()));
        
        add(centerPanel, BorderLayout.CENTER);

        // Bottom summary
        summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.SOUTH);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Left controls
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(Color.WHITE);

        JLabel reportLabel = new JLabel("Report Type:");
        reportLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        reportTypeCombo = new JComboBox<>(new String[]{
            "Account Distribution", "Transaction Analysis", "Customer Growth", "Balance Trends"
        });
        reportTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reportTypeCombo.setPreferredSize(new Dimension(180, 35));

        JLabel rangeLabel = new JLabel("Time Range:");
        rangeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        timeRangeCombo = new JComboBox<>(new String[]{
            "Last 7 Days", "Last 30 Days", "Last 3 Months", "Last Year", "All Time"
        });
        timeRangeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        timeRangeCombo.setPreferredSize(new Dimension(150, 35));

        generateButton = createStyledButton("📊 Generate Report", new Color(45, 85, 255));
        generateButton.addActionListener(e -> generateReport());

        leftPanel.add(reportLabel);
        leftPanel.add(reportTypeCombo);
        leftPanel.add(rangeLabel);
        leftPanel.add(timeRangeCombo);
        leftPanel.add(generateButton);

        // Right controls
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(Color.WHITE);

        exportButton = createStyledButton("📥 Export PDF", new Color(108, 117, 125));
        exportButton.addActionListener(e -> exportReport());

        rightPanel.add(exportButton);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createChartWrapper(String title, JComponent chart) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(chart, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAccountDistributionChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Savings Accounts", 65);
        dataset.setValue("Current Accounts", 35);

        JFreeChart chart = ChartFactory.createPieChart(
            "", dataset, true, true, false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Savings Accounts", new Color(45, 85, 255));
        plot.setSectionPaint("Current Accounts", new Color(40, 167, 69));
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 11));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(300, 200));
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(null);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(chartPanel, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createMonthlyTransactionChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Sample data
        dataset.addValue(15000, "Deposits", "Jan");
        dataset.addValue(12000, "Withdrawals", "Jan");
        dataset.addValue(18000, "Deposits", "Feb");
        dataset.addValue(14000, "Withdrawals", "Feb");
        dataset.addValue(22000, "Deposits", "Mar");
        dataset.addValue(16000, "Withdrawals", "Mar");
        dataset.addValue(20000, "Deposits", "Apr");
        dataset.addValue(18000, "Withdrawals", "Apr");

        JFreeChart chart = ChartFactory.createBarChart(
            "",
            "Month",
            "Amount ($)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(200, 200, 200));
        plot.setDomainGridlinePaint(new Color(200, 200, 200));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(300, 200));
        chartPanel.setBackground(Color.WHITE);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(chartPanel, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createCustomerGrowthChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Sample data
        dataset.addValue(45, "New Customers", "Week 1");
        dataset.addValue(52, "New Customers", "Week 2");
        dataset.addValue(58, "New Customers", "Week 3");
        dataset.addValue(63, "New Customers", "Week 4");
        dataset.addValue(71, "New Customers", "Week 5");

        JFreeChart chart = ChartFactory.createLineChart(
            "",
            "Week",
            "Number of Customers",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(200, 200, 200));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(300, 200));
        chartPanel.setBackground(Color.WHITE);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(chartPanel, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createBalanceTrendChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Sample data
        dataset.addValue(450000, "Total Balance", "Jan");
        dataset.addValue(475000, "Total Balance", "Feb");
        dataset.addValue(510000, "Total Balance", "Mar");
        dataset.addValue(495000, "Total Balance", "Apr");
        dataset.addValue(525000, "Total Balance", "May");

        JFreeChart chart = ChartFactory.createLineChart(
            "",
            "Month",
            "Balance ($)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(200, 200, 200));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(300, 200));
        chartPanel.setBackground(Color.WHITE);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(chartPanel, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 10, 10, 10));

        panel.add(createMetricCard("Total Customers", "156", "+12 this month", new Color(45, 85, 255)));
        panel.add(createMetricCard("Total Accounts", "243", "+23 this month", new Color(40, 167, 69)));
        panel.add(createMetricCard("Total Balance", "$5.2M", "+$425K this month", new Color(255, 193, 7)));
        panel.add(createMetricCard("Monthly Transactions", "1,245", "+8.3% vs last month", new Color(23, 162, 184)));

        return panel;
    }

    private JPanel createMetricCard(String title, String value, String trend, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(Color.GRAY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(color);

        JLabel trendLabel = new JLabel(trend);
        trendLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        trendLabel.setForeground(trend.startsWith("+") ? new Color(40, 167, 69) : Color.GRAY);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(trendLabel, BorderLayout.WEST);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);

        return card;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
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

    private void generateReport() {
        String reportType = (String) reportTypeCombo.getSelectedItem();
        String timeRange = (String) timeRangeCombo.getSelectedItem();
        
        JOptionPane.showMessageDialog(this,
            "Generating " + reportType + " report for " + timeRange + "...\nThis feature is coming soon!",
            "Generate Report",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportReport() {
        JOptionPane.showMessageDialog(this,
            "Exporting report to PDF...\nThis feature is coming soon!",
            "Export Report",
            JOptionPane.INFORMATION_MESSAGE);
    }
}