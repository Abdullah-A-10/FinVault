package com.banking.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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

import com.banking.gui.dialogs.CustomerDialog;
import com.banking.model.Customer;
import com.banking.service.CustomerService;

/**
 * Customer Management Panel 
 */
public class CustomerManagementPanel extends JPanel {

    private final CustomerService customerService;
    
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton refreshButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewDetailsButton;
    private JLabel statusLabel;
    private JLabel totalCountLabel;
    
    private final Color primaryColor = new Color(45, 85, 255);
    private final Color successColor = new Color(40, 167, 69);
    private final Color warningColor = new Color(255, 193, 7);
    private final Color dangerColor = new Color(220, 53, 69);
    private final Color infoColor = new Color(23, 162, 184);
    private final Color borderColor = new Color(50, 40, 60);
    private final Color alternateRowColor = new Color(246, 249, 250);

    public CustomerManagementPanel() {
        this.customerService = new CustomerService();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 10, 20));
        
        initComponents();
        loadCustomers();
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
        JPanel toolbar = new JPanel(new BorderLayout(15, 0));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Search panel with icon
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(Color.WHITE);

        JLabel searchIcon = new JLabel("⌕");
        searchIcon.setFont(new Font("SansSerif", Font.BOLD, 20));
        searchIcon.setForeground(Color.GRAY);

        searchField = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        searchField.setPreferredSize(new Dimension(320, 38));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            new EmptyBorder(5, 15, 5, 15)
        ));
        searchField.setOpaque(false);
        searchField.putClientProperty("JTextField.placeholderText", "Search by name, email or phone...");

        searchButton = createRoundedButton("⌕ Search", primaryColor);
        searchButton.addActionListener(e -> searchCustomers());

        refreshButton = createRoundedButton("↻ Refresh", new Color(108, 117, 125));
        refreshButton.addActionListener(e -> loadCustomers());

        searchPanel.add(searchIcon);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setBackground(Color.WHITE);

        addButton = createRoundedButton("✚ Add", successColor);
        addButton.addActionListener(e -> addCustomer());

        editButton = createRoundedButton("✎ Edit", warningColor);
        editButton.setEnabled(false);
        editButton.addActionListener(e -> editCustomer());

        deleteButton = createRoundedButton("✕ Delete", dangerColor);
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteCustomer());

        viewDetailsButton = createRoundedButton("◉ Details", infoColor);
        viewDetailsButton.setEnabled(false);
        viewDetailsButton.addActionListener(e -> viewCustomerDetails());

        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(viewDetailsButton);

        toolbar.add(searchPanel, BorderLayout.WEST);
        toolbar.add(actionPanel, BorderLayout.EAST);

        return toolbar;
    }

    private JButton createRoundedButton(String text, Color bgColor) {
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
        
        button.setFont(new Font("SansSerif", Font.PLAIN, 13));
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        
        return button;
    }

private JPanel createTablePanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(borderColor, 1),
        new EmptyBorder(10, 10, 10, 10)
    ));

    // Table columns
    String[] columns = {"ID", "First Name", "Last Name", "Email", "Phone", "Status", "Registered"};
    tableModel = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    // JTable
    customerTable = new JTable(tableModel) {
        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            Component comp = super.prepareRenderer(renderer, row, column);

            // Zebra striping for sleek look
            if (!isRowSelected(row)) {
                comp.setBackground(row % 2 == 0 ? new Color(250, 250, 252) : Color.WHITE);
            }

            // Hover effect
            if (getMousePosition() != null && row == rowAtPoint(getMousePosition())) {
                comp.setBackground(new Color(primaryColor.getRed(), primaryColor.getGreen(),
                                             primaryColor.getBlue(), 30));
            }

            // Status column coloring
            String status = (String) getValueAt(row, 5);
            if (column == 5 && status != null) {
                switch (status.trim()) {
                    case "● ACTIVE":
                        comp.setForeground(new Color(40, 167, 69));
                        break;
                    case "● INACTIVE":
                        comp.setForeground(new Color(250, 100, 100));
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

    // Table general properties
    customerTable.setRowHeight(42);
    customerTable.setShowGrid(false); 
    customerTable.setIntercellSpacing(new Dimension(0, 0));
    customerTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
    customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    customerTable.setBackground(Color.WHITE);
    customerTable.setSelectionBackground(new Color(primaryColor.getRed(), primaryColor.getGreen(),
                                                   primaryColor.getBlue(), 50));
    customerTable.setSelectionForeground(Color.BLACK);

    // Column widths
    customerTable.getColumnModel().getColumn(0).setPreferredWidth(70);
    customerTable.getColumnModel().getColumn(1).setPreferredWidth(110);
    customerTable.getColumnModel().getColumn(2).setPreferredWidth(110);
    customerTable.getColumnModel().getColumn(3).setPreferredWidth(210);
    customerTable.getColumnModel().getColumn(4).setPreferredWidth(100);

    // Table header styling
    JTableHeader header = customerTable.getTableHeader();
    header.setFont(new Font("SansSerif", Font.BOLD, 16));
    header.setBackground(new Color(248, 249, 250));
    header.setForeground(Color.BLACK);
    header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, primaryColor));

    // Enable/disable buttons based on row selection
    customerTable.getSelectionModel().addListSelectionListener(e -> {
        boolean rowSelected = customerTable.getSelectedRow() != -1;
        editButton.setEnabled(rowSelected);
        deleteButton.setEnabled(rowSelected);
        viewDetailsButton.setEnabled(rowSelected);
    });

    // Double-click to view details
    customerTable.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            if (evt.getClickCount() == 2) {
                viewCustomerDetails();
            }
        }
    });

    // Scroll pane
    JScrollPane scrollPane = new JScrollPane(customerTable);
    scrollPane.setBorder(null);
    scrollPane.getViewport().setBackground(Color.WHITE);

    panel.add(scrollPane, BorderLayout.CENTER);
    return panel;
}

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 5, 5, 5));

        totalCountLabel = new JLabel("Total Customers: 0");
        totalCountLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        totalCountLabel.setForeground(primaryColor);

        statusLabel = new JLabel("● System ready");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(successColor);

        panel.add(totalCountLabel, BorderLayout.WEST);
        panel.add(statusLabel, BorderLayout.EAST);

        return panel;
    }

    private void loadCustomers() {
        statusLabel.setText("⏳ Loading...");
        statusLabel.setForeground(Color.GRAY);
        
        SwingWorker<List<Customer>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Customer> doInBackground() throws Exception {
                return customerService.getAllCustomers();
            }

            @Override
            protected void done() {
                try {
                    List<Customer> customers = get();
                    updateTableData(customers);
                    totalCountLabel.setText("Total Customers: " + customers.size());
                    statusLabel.setText("● Loaded " + customers.size() + " customers");
                    statusLabel.setForeground(successColor);
                } catch (Exception e) {
                    e.printStackTrace();
                    statusLabel.setText("⚠ Error loading customers");
                    statusLabel.setForeground(dangerColor);
                    JOptionPane.showMessageDialog(CustomerManagementPanel.this,
                        "Error loading customers: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void updateTableData(List<Customer> customers) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (Customer customer : customers) {
            tableModel.addRow(new Object[]{
                customer.getCustomerId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone() != null ? customer.getPhone() : "-",
                formatStatus(customer.getStatus()),
                customer.getDateRegistered().format(formatter)
            });
        }
    }

    private String formatStatus(String status) {
        if (status == null) return "⚪ Unknown";
        
        switch (status) {
            case "ACTIVE": return "● ACTIVE";
            case "INACTIVE": return "● INACTIVE";
            case "BLOCKED": return "● BLOCKED";
            default: return "⚪ " + status;
        }
    }

    private void searchCustomers() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadCustomers();
            return;
        }

        statusLabel.setText("⏳ Searching...");
        statusLabel.setForeground(Color.GRAY);

        SwingWorker<List<Customer>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Customer> doInBackground() throws Exception {
                List<Customer> allCustomers = customerService.getAllCustomers();
                return allCustomers.stream()
                    .filter(c -> (c.getFirstName() != null && c.getFirstName().toLowerCase().contains(searchTerm)) ||
                                (c.getLastName() != null && c.getLastName().toLowerCase().contains(searchTerm)) ||
                                (c.getEmail() != null && c.getEmail().toLowerCase().contains(searchTerm)) ||
                                (c.getPhone() != null && c.getPhone().contains(searchTerm)))
                    .toList();
            }

            @Override
            protected void done() {
                try {
                    List<Customer> filtered = get();
                    updateTableData(filtered);
                    totalCountLabel.setText("Showing: " + filtered.size() + " customers");
                    statusLabel.setText("● Found " + filtered.size() + " matches");
                    statusLabel.setForeground(successColor);
                } catch (Exception e) {
                    e.printStackTrace();
                    statusLabel.setText("⚠ Search failed");
                    statusLabel.setForeground(dangerColor);
                }
            }
        };
        worker.execute();
    }

    private void addCustomer() {
        CustomerDialog dialog = new CustomerDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadCustomers();
            JOptionPane.showMessageDialog(this,
                "✓ Customer added successfully",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow != -1) {
            int customerId = (int) tableModel.getValueAt(selectedRow, 0);
            try {
                Customer customer = customerService.getCustomerById(customerId);
                if (customer != null) {
                    CustomerDialog dialog = new CustomerDialog((JFrame) SwingUtilities.getWindowAncestor(this), customer);
                    dialog.setVisible(true);
                    if (dialog.isSaved()) {
                        loadCustomers();
                        JOptionPane.showMessageDialog(this,
                            "✓ Customer updated successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "✕ Error loading customer: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow != -1) {
            int customerId = (int) tableModel.getValueAt(selectedRow, 0);
            String customerName = tableModel.getValueAt(selectedRow, 1) + " " + 
                                 tableModel.getValueAt(selectedRow, 2);

            int confirm = JOptionPane.showConfirmDialog(this,
                "⚠ Delete customer: " + customerName + "?\n" +
                "This will also delete all associated accounts and transactions!",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    boolean deleted = customerService.deleteCustomer(customerId);
                    if (deleted) {
                        JOptionPane.showMessageDialog(this,
                            "✓ Customer deleted successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadCustomers();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "✕ Failed to delete customer",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                        "✕ Error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void viewCustomerDetails() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow != -1) {
            int customerId = (int) tableModel.getValueAt(selectedRow, 0);
            try {
                Customer customer = customerService.getCustomerById(customerId);
                if (customer != null) {
                    showCustomerDetailsDialog(customer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showCustomerDetailsDialog(Customer customer) {

    JDialog dialog = new JDialog(
        (JFrame) SwingUtilities.getWindowAncestor(this),
        "Customer Details", true);

    dialog.setSize(520, 470);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout());
    dialog.getContentPane().setBackground(new Color(245,247,250));

    JPanel card = new JPanel(new BorderLayout()) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            g2.setColor(new Color(225,225,225));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
            g2.dispose();
            super.paintComponent(g);
        }
    };
    card.setOpaque(false);
    card.setBorder(new EmptyBorder(18,18,18,18));

    dialog.add(card, BorderLayout.CENTER);

    JLabel header = new JLabel(
        customer.getFirstName() + " " + customer.getLastName());
    header.setFont(new Font("Segoe UI", Font.BOLD, 20));
    header.setForeground(new Color(40,40,40));
    header.setBorder(new EmptyBorder(5,8,12,8));

    JSeparator sep = new JSeparator();
    sep.setForeground(new Color(230,230,230));

    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setOpaque(false);
    headerPanel.add(header, BorderLayout.WEST);
    headerPanel.add(sep, BorderLayout.SOUTH);

    card.add(headerPanel, BorderLayout.NORTH);

    JPanel detailsPanel = new JPanel(new GridBagLayout());
    detailsPanel.setOpaque(false);
    detailsPanel.setBorder(new EmptyBorder(10,6,6,6));
    card.add(detailsPanel, BorderLayout.CENTER);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8,8,10,8);

    int row = 0;

    String[][] details = {
        {"First Name", customer.getFirstName()},
        {"Last Name", customer.getLastName()},
        {"Email", customer.getEmail()},
        {"Phone", customer.getPhone()!=null ? customer.getPhone() : "-"},
        {"Status", customer.getStatus()},
        {"Registered", customer.getDateRegistered().toString()}
    };

    for (String[] detail : details) {

        JLabel label = new JLabel(detail[0]);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(90,90,90));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        detailsPanel.add(label, gbc);

        JLabel value = new JLabel(detail[1]);
        value.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        value.setForeground(new Color(25,25,25));
        value.setBorder(new EmptyBorder(3,6,3,6));

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        detailsPanel.add(value, gbc);

        row++;
    }

    JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    footer.setOpaque(false);

    JButton close = new JButton("Close");
    close.setFocusPainted(false);
    close.setBorder(new EmptyBorder(8,18,8,18));
    close.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    close.setBackground(new Color(235,235,235));

    close.addActionListener(e -> dialog.dispose());
    footer.add(close);

    card.add(footer, BorderLayout.SOUTH);

    dialog.setVisible(true);
}

}
