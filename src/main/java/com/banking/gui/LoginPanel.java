package com.banking.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

/**
 * Login Panel
 */
public class LoginPanel extends JPanel {

    private JFrame parentFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private JCheckBox rememberCheck;
    private JLabel messageLabel;
    
    // Color scheme
    private static final Color PRIMARY_BLUE = new Color(18, 52, 77);
    private static final Color PRIMARY_LIGHT = new Color(100, 130, 200);
    private static final Color TEXT_DARK = new Color(33, 37, 41);
    private static final Color TEXT_GRAY = new Color(108, 117, 125);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);
    private static final Color FOCUS_COLOR = new Color(45, 85, 255);
    private static final Color SUCCESS = new Color(40, 167, 69);
    private static final Color ERROR = new Color(220, 53, 69);

    public LoginPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        
        // Background
        setBackground(PRIMARY_BLUE);
        
        initComponents();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Gradient background
        int w = getWidth();
        int h = getHeight();
        GradientPaint gp = new GradientPaint(
            0, 0, new Color(100,100,110),
            w, h, new Color(50,50,60)
        );
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
        
        // Pattern overlay
        g2d.setColor(new Color(255, 255, 255, 20));
        for (int i = 0; i < w; i += 40) {
            for (int j = 0; j < h; j += 40) {
                g2d.fillOval(i, j, 2, 2);
            }
        }
        
        g2d.dispose();
    }

    private void initComponents() {
        // Center panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        
        JPanel loginPanel = createLoginPanel();
        centerPanel.add(loginPanel);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Footer
        add(createFooter(), BorderLayout.SOUTH);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();
                
                // Shadow
                for (int i = 0; i < 5; i++) {
                    g2d.setColor(new Color(0, 0, 0, 20 - i * 4));
                    g2d.fillRoundRect(5, h - 5 + i, w - 10, 5, 40, 40);
                }
                
                // Main background with border radius
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, w, h - 5, 40, 40);
                
                g2d.dispose();
            }
        };
        
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(40, 40, 35, 40));
        panel.setPreferredSize(new Dimension(420, 520));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        int row = 0;

        // Logo section
        JPanel logoPanel = createLogo();
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        panel.add(logoPanel, gbc);

        // Title
        JLabel titleLabel = new JLabel("Welcome Back !");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(TEXT_DARK);
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.gridy = row++;
        panel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Please enter your credentials");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_GRAY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(0, 8, 20, 8);
        gbc.gridy = row++;
        panel.add(subtitleLabel, gbc);

        // Username
        gbc.insets = new Insets(8, 8, 8, 8);
        
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        userIcon.setForeground(PRIMARY_BLUE);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.05;
        panel.add(userIcon, gbc);

        usernameField = createTextField();
        usernameField.setText("admin");
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.weightx = 0.95;
        panel.add(usernameField, gbc);

        // Password
        JLabel passIcon = new JLabel("🔒");
        passIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        passIcon.setForeground(PRIMARY_BLUE);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.05;
        panel.add(passIcon, gbc);

        passwordField = createPasswordField();
        passwordField.setText("admin123");
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.weightx = 0.95;
        panel.add(passwordField, gbc);

        // Options
        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.setOpaque(false);

        rememberCheck = new JCheckBox("Remember me");
        rememberCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rememberCheck.setForeground(TEXT_GRAY);
        rememberCheck.setOpaque(false);
        rememberCheck.setFocusPainted(false);

        JLabel forgotLabel = new JLabel("Forgot Password?");
        forgotLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotLabel.setForeground(PRIMARY_BLUE);
        forgotLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showForgotDialog();
            }
        });

        optionsPanel.add(rememberCheck, BorderLayout.WEST);
        optionsPanel.add(forgotLabel, BorderLayout.EAST);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 8, 15, 8);
        panel.add(optionsPanel, gbc);

        // Message
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(ERROR);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(0, 8, 10, 8);
        gbc.gridy = row++;
        panel.add(messageLabel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);

        loginButton = createButton("Sign In", SUCCESS);
        loginButton.addActionListener(e -> performLogin());

        cancelButton = createButton("Cancel", TEXT_GRAY);
        cancelButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        gbc.insets = new Insets(10, 8, 20, 8);
        gbc.gridy = row++;
        panel.add(buttonPanel, gbc);

        // Security note
        JLabel securityLabel = new JLabel("-> Secure Banking Solution");
        securityLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        securityLabel.setForeground(TEXT_DARK);
        securityLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(5, 8, 0, 8);
        gbc.gridy = row;
        panel.add(securityLabel, gbc);

        return panel;
    }

    private JPanel createLogo() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
    panel.setOpaque(false);

    JPanel icon = new JPanel() {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            // shield
            GradientPaint gp = new GradientPaint(
                    0,0,PRIMARY_BLUE,
                    40,40,PRIMARY_LIGHT);
            g2.setPaint(gp);
            int[] x = {25, 45, 40, 25, 10, 5};
            int[] y = {5, 18, 38, 48, 38, 18};
            g2.fillPolygon(x, y, 6);

            // lock body
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(18,24,14,12,4,4);

            // lock arc
            g2.drawArc(18,16,14,16,0,180);
        }
        public Dimension getPreferredSize() {
            return new Dimension(50,50);
        }
    };
    icon.setOpaque(false);

    JLabel text = new JLabel("FinVault");
    text.setFont(new Font("Segoe UI", Font.BOLD, 32));
    text.setForeground(PRIMARY_BLUE);

    panel.add(icon);
    panel.add(text);
    return panel;
}


    private JTextField createTextField() {
        JTextField field = new JTextField(15) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(TEXT_GRAY);
                    g2.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                    g2.drawString("Username", 10, 25);
                    g2.dispose();
                }
            }
        };
        
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(250, 45));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(FOCUS_COLOR, 2),
                    new EmptyBorder(7, 11, 7, 11)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    new EmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(15) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(TEXT_GRAY);
                    g2.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                    g2.drawString("Password", 10, 25);
                    g2.dispose();
                }
            }
        };
        
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(250, 45));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(FOCUS_COLOR, 2),
                    new EmptyBorder(7, 11, 7, 11)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    new EmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        
        return field;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(12, 25, 12, 25));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        
        return button;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footer.setOpaque(false);
        
        String[] links = {"Privacy", "Terms", "Support"};
        Color linkColor = new Color(255, 255, 255, 180);
        
        for (String link : links) {
            JLabel linkLabel = new JLabel(link);
            linkLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            linkLabel.setForeground(linkColor);
            linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            linkLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    linkLabel.setForeground(Color.WHITE);
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    linkLabel.setForeground(linkColor);
                }
            });
            
            footer.add(linkLabel);
        }
        
        return footer;
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        messageLabel.setText(" ");

        if (username.isEmpty()) {
            showError("Enter username");
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showError("Enter password");
            passwordField.requestFocus();
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Please wait...");

        Timer timer = new Timer(1500, e -> {
            if ("admin".equals(username) && "admin123".equals(password)) {
                loginButton.setEnabled(true);
                loginButton.setText("Sign In");
                parentFrame.setContentPane(new MainFrame().getContentPane());
                parentFrame.revalidate();
                parentFrame.repaint();
            } else {
                loginButton.setEnabled(true);
                loginButton.setText("Sign In");
                showError("Invalid credentials");
                shakePanel();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void showError(String message) {
        messageLabel.setText("⚠ " + message);
        
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ERROR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ERROR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        Timer timer = new Timer(2000, e -> {
            usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(8, 12, 8, 12)
            ));
            passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(8, 12, 8, 12)
            ));
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void shakePanel() {
        JPanel loginPanel = (JPanel) ((JPanel) getComponent(0)).getComponent(0);
        Point original = loginPanel.getLocation();
        
        Timer timer = new Timer(50, null);
        int[] counter = {0};
        
        timer.addActionListener(e -> {
            int offset = (counter[0] % 2 == 0) ? 3 : -3;
            loginPanel.setLocation(original.x + offset, original.y);
            counter[0]++;
            
            if (counter[0] >= 6) {
                timer.stop();
                loginPanel.setLocation(original);
            }
        });
        
        timer.start();
    }

    private void showForgotDialog() {
        JDialog dialog = new JDialog(parentFrame, "Reset Password", true);
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JLabel titleLabel = new JLabel("🔐 Reset Password");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_DARK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        JLabel instructionLabel = new JLabel("Enter your email");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        instructionLabel.setForeground(TEXT_GRAY);
        gbc.gridy = 1;
        panel.add(instructionLabel, gbc);
        
        JTextField emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        gbc.gridy = 2;
        panel.add(emailField, gbc);
        
        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sendButton.setBackground(PRIMARY_BLUE);
        sendButton.setForeground(Color.WHITE);
        sendButton.setBorder(new EmptyBorder(10, 20, 10, 20));
        sendButton.setFocusPainted(false);
        sendButton.addActionListener(e -> {
            if (!emailField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "✅ Reset link sent to " + emailField.getText(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(sendButton);
        
        gbc.gridy = 3;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}