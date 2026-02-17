package com.banking;

import com.banking.gui.LoginPanel;
import com.banking.gui.MainFrame;
import com.banking.util.DatabaseUtil;

import javax.swing.*;

public class BankingSystemMain {

    public static void main(String[] args) {
        System.out.println("Banking System Initializing...");

        // Initialize database
        boolean dbInitialized = DatabaseUtil.initializeDatabase();
        if (!dbInitialized) {
            System.err.println("Failed to initialize database. Exiting...");
            System.exit(1);
        }

        System.out.println("Database initialized successfully.");

        // Start with login panel
        SwingUtilities.invokeLater(() -> {
            JFrame loginFrame = new JFrame();
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setSize(1400, 800);
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setContentPane(new LoginPanel(loginFrame));
            loginFrame.setVisible(true);
        });
    }
}