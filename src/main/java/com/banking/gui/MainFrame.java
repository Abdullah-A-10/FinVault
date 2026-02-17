package com.banking.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.banking.gui.panels.AccountManagementPanel;
import com.banking.gui.panels.CustomerManagementPanel;
import com.banking.gui.panels.DashboardPanel;
import com.banking.gui.panels.ReportsPanel;
import com.banking.gui.panels.SettingsPanel;
import com.banking.gui.panels.TransactionPanel;

/**
 * Main application frame with FinVault branding
 */
public class MainFrame extends JFrame {

    private JPanel mainPanel;
    private JPanel contentPanel;
    private JPanel navigationPanel;
    private CardLayout cardLayout;
    
    // Color scheme
    private static final Color PRIMARY_DARK = new Color(18, 52, 77);
    private static final Color PRIMARY_BLUE = new Color(25, 50, 75);
    private static final Color PRIMARY_LIGHT = new Color(98, 130, 255);
    private static final Color NAV_BG = new Color(25, 28, 31);
    private static final Color NAV_HOVER = new Color(52, 58, 64);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    
    // Navigation buttons
    private JButton dashboardButton;
    private JButton customersButton;
    private JButton accountsButton;
    private JButton transactionsButton;
    private JButton reportsButton;
    private JButton settingsButton;
    private JButton logoutButton;

    public MainFrame() {
        initFrame();
        initComponents();
    }

    private void initFrame() {
        setTitle("FinVault - Banking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setMinimumSize(new Dimension(1200, 600));
        setLocationRelativeTo(null);
        
        // Set application icon
        setIconImage(createIconImage());
    }

    private Image createIconImage() {
        // Create a simple colored square as icon
        ImageIcon icon = new ImageIcon(new java.awt.image.BufferedImage(64, 64, java.awt.image.BufferedImage.TYPE_INT_ARGB));
        return icon.getImage();
    }

    private void initComponents() {
        // Main container
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        setContentPane(mainPanel);

        // Create navigation panel
        createNavigationPanel();

        // Content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        // Add panels to card layout
        contentPanel.add(new DashboardPanel(), "DASHBOARD");
        contentPanel.add(new CustomerManagementPanel(), "CUSTOMERS");
        contentPanel.add(new AccountManagementPanel(), "ACCOUNTS");
        contentPanel.add(new TransactionPanel(), "TRANSACTIONS");
        contentPanel.add(new ReportsPanel(), "REPORTS");
        contentPanel.add(new SettingsPanel(), "SETTINGS");

        // Add to main panel
        mainPanel.add(navigationPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Add status bar
        mainPanel.add(createStatusBar(), BorderLayout.SOUTH);
        
        // Set default selection
        dashboardButton.setSelected(true);
        updateNavButtonSelection(dashboardButton);
    }

    private void createNavigationPanel() {
        navigationPanel = new JPanel();
        navigationPanel.setPreferredSize(new Dimension(280, 0));
        navigationPanel.setBackground(NAV_BG);
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));

        // Logo/Brand section with custom drawing
        JPanel brandPanel = createBrandPanel();
        navigationPanel.add(brandPanel);
        navigationPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // User info panel
        JPanel userPanel = createUserPanel();
        navigationPanel.add(userPanel);
        navigationPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Navigation buttons with text-based icons (universally compatible)
        dashboardButton = createNavButton("≡", "Dashboard", "DASHBOARD"); 
        customersButton = createNavButton("👥", "Customers", "CUSTOMERS");
        accountsButton = createNavButton("💰", "Accounts", "ACCOUNTS"); 
        transactionsButton = createNavButton("↔", "Transactions", "TRANSACTIONS"); 
        reportsButton = createNavButton("📊", "Reports", "REPORTS");
        settingsButton = createNavButton("⚙", "Settings", "SETTINGS");

        navigationPanel.add(dashboardButton);
        navigationPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        navigationPanel.add(customersButton);
        navigationPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        navigationPanel.add(accountsButton);
        navigationPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        navigationPanel.add(transactionsButton);
        navigationPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        navigationPanel.add(reportsButton);
        navigationPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        navigationPanel.add(settingsButton);
        
        navigationPanel.add(Box.createVerticalGlue());

        // Logout button at bottom
        logoutButton = createLogoutButton();
        navigationPanel.add(logoutButton);
        navigationPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    }

    private JPanel createBrandPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();
                
                // Gradient background
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_BLUE, w, 0, PRIMARY_LIGHT);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
                
                g2d.dispose();
            }
        };
        panel.setPreferredSize(new Dimension(280, 100));
        panel.setMaximumSize(new Dimension(280, 100));

        // Logo - using text-based lock symbol
        JLabel logoLabel = new JLabel("🔒");
        logoLabel.setFont(new Font("SansSerif", Font.PLAIN, 32));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setBorder(new EmptyBorder(0, 20, 0, 0));

        // Brand text
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        
        JLabel brandLabel = new JLabel("FinVault");
        brandLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        brandLabel.setForeground(Color.WHITE);
        
        JLabel taglineLabel = new JLabel("Secure Banking");
        taglineLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        taglineLabel.setForeground(new Color(255, 255, 255, 200));
        
        textPanel.add(brandLabel);
        textPanel.add(taglineLabel);

        panel.add(logoLabel, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(NAV_BG);
        panel.setMaximumSize(new Dimension(280, 70));
        panel.setPreferredSize(new Dimension(280, 70));

        // User avatar with text-based icon
        JLabel avatarLabel = new JLabel("👤");
        avatarLabel.setFont(new Font("SansSerif", Font.PLAIN, 32));
        avatarLabel.setForeground(PRIMARY_LIGHT);

        // User info
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);
        
        JLabel userName = new JLabel("Admin User");
        userName.setFont(new Font("SansSerif", Font.BOLD, 14));
        userName.setForeground(Color.WHITE);
        
        JLabel userRole = new JLabel("System Administrator");
        userRole.setFont(new Font("SansSerif", Font.PLAIN, 11));
        userRole.setForeground(new Color(200, 200, 200));
        
        infoPanel.add(userName);
        infoPanel.add(userRole);

        panel.add(avatarLabel);
        panel.add(infoPanel);

        return panel;
    }

    private JButton createNavButton(String icon, String text, String action) {
        JButton button = new JButton(icon + "  " + text);
        button.setMaximumSize(new Dimension(260, 45));
        button.setPreferredSize(new Dimension(260, 45));
        button.setMinimumSize(new Dimension(260, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(0, 20, 0, 0));
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setBackground(NAV_BG);
        button.setForeground(new Color(200, 200, 200));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(e -> {
            cardLayout.show(contentPanel, action);
            updateNavButtonSelection(button);
        });

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!isSelected(button)) {
                    button.setBackground(NAV_HOVER);
                    button.setForeground(Color.WHITE);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!isSelected(button)) {
                    button.setBackground(NAV_BG);
                    button.setForeground(new Color(200, 200, 200));
                }
            }
        });

        return button;
    }

    private JButton createLogoutButton() {
        JButton button = new JButton("✕  Logout"); 
        button.setMaximumSize(new Dimension(260, 45));
        button.setPreferredSize(new Dimension(260, 45));
        button.setMinimumSize(new Dimension(260, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(0, 20, 0, 0));
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setBackground(NAV_BG);
        button.setForeground(new Color(200, 200, 200));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(e -> logout());

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(DANGER_COLOR);
                button.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(NAV_BG);
                button.setForeground(new Color(200, 200, 200));
            }
        });

        return button;
    }

    private boolean isSelected(JButton button) {
        return button.getForeground().equals(Color.WHITE) && 
               button.getBackground().equals(PRIMARY_BLUE);
    }

    private void updateNavButtonSelection(JButton selectedButton) {
        JButton[] buttons = {dashboardButton, customersButton, accountsButton, 
                             transactionsButton, reportsButton, settingsButton};
        
        for (JButton button : buttons) {
            button.setBackground(NAV_BG);
            button.setForeground(new Color(200, 200, 200));
            button.setContentAreaFilled(false);
        }
        
        selectedButton.setBackground(PRIMARY_BLUE);
        selectedButton.setForeground(Color.WHITE);
        selectedButton.setContentAreaFilled(true);
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setPreferredSize(new Dimension(0, 32));
        statusBar.setBackground(new Color(245, 245, 245));
        statusBar.setBorder(new EmptyBorder(0, 15, 0, 15));

        // Left side status
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);
        
        JLabel statusIcon = new JLabel("●");
        statusIcon.setFont(new Font("SansSerif", Font.BOLD, 12));
        statusIcon.setForeground(SUCCESS_COLOR);
        
        JLabel statusLabel = new JLabel("System Online");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(100, 100, 100));
        
        JLabel dbStatus = new JLabel("• Database Connected");
        dbStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        dbStatus.setForeground(new Color(100, 100, 100));
        
        leftPanel.add(statusIcon);
        leftPanel.add(statusLabel);
        leftPanel.add(dbStatus);

        // Right side info
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        
        JLabel versionLabel = new JLabel("FinVault v1.0");
        versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        versionLabel.setForeground(new Color(100, 100, 100));
        
        JLabel dateLabel = new JLabel(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy • HH:mm"))
        );
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(100, 100, 100));
        
        rightPanel.add(versionLabel);
        rightPanel.add(dateLabel);

        statusBar.add(leftPanel, BorderLayout.WEST);
        statusBar.add(rightPanel, BorderLayout.EAST);

        return statusBar;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            JFrame loginFrame = new JFrame();
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setSize(1400, 800);
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setContentPane(new LoginPanel(loginFrame));
            loginFrame.setVisible(true);
        }
    }

    // Public method to switch panels from other components
    public void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
        switch (panelName) {
            case "DASHBOARD": updateNavButtonSelection(dashboardButton); break;
            case "CUSTOMERS": updateNavButtonSelection(customersButton); break;
            case "ACCOUNTS": updateNavButtonSelection(accountsButton); break;
            case "TRANSACTIONS": updateNavButtonSelection(transactionsButton); break;
            case "REPORTS": updateNavButtonSelection(reportsButton); break;
            case "SETTINGS": updateNavButtonSelection(settingsButton); break;
        }
    }
}