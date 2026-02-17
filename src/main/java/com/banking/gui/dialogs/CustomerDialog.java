package com.banking.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.regex.Pattern;

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
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.banking.model.Customer;
import com.banking.service.CustomerService;

public class CustomerDialog extends JDialog {

    private final CustomerService customerService = new CustomerService();
    private final Customer customer;
    private boolean saved = false;

    private JTextField firstNameField = new JTextField(20);
    private JTextField lastNameField  = new JTextField(20);
    private JTextField emailField     = new JTextField(20);
    private JTextField phoneField     = new JTextField(20);
    private JTextArea  addressArea    = new JTextArea(3,20);
    private JComboBox<String> statusCombo;

    private JLabel validationLabel = new JLabel(" ");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public CustomerDialog(JFrame parent, Customer customer) {
        super(parent, true);
        this.customer = customer;

        setTitle(customer == null ? "Add Customer" : "Edit Customer");
        setSize(520, 560);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();

        if(customer!=null){
            loadCustomerData();
        }

        SwingUtilities.invokeLater(() -> firstNameField.requestFocusInWindow());
    }

    private void initUI() {

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20,25,20,25));
        form.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int row=0;

        addField(form,gbc,row++,"First Name *",firstNameField);
        addField(form,gbc,row++,"Last Name *",lastNameField);
        addField(form,gbc,row++,"Email *",emailField);
        addField(form,gbc,row++,"Phone",phoneField);

        gbc.gridx=0; gbc.gridy=row;
        form.add(createLabel("Address"),gbc);

        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setBorder(new LineBorder(new Color(210,210,210)));

        gbc.gridx=1;
        form.add(new JScrollPane(addressArea),gbc);
        row++;

        if(customer!=null){
            gbc.gridx=0; gbc.gridy=row;
            form.add(createLabel("Status"),gbc);

            statusCombo = new JComboBox<>(
                    new String[]{"ACTIVE","INACTIVE","BLOCKED"});
            styleCombo(statusCombo);

            gbc.gridx=1;
            form.add(statusCombo,gbc);
            row++;
        }

        validationLabel.setForeground(new Color(200,0,0));
        validationLabel.setFont(new Font("Segoe UI",Font.PLAIN,12));
        gbc.gridx=0; gbc.gridy=row;
        gbc.gridwidth=2;
        form.add(validationLabel,gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(Color.WHITE);
        buttons.setBorder(new EmptyBorder(10,0,0,0));

        JButton cancel = createButton("Cancel",false);
        JButton save   = createButton("Save",true);

        cancel.addActionListener(e->dispose());
        save.addActionListener(e->saveCustomer());

        buttons.add(cancel);
        buttons.add(save);

        gbc.gridy=row+1;
        form.add(buttons,gbc);

        applyFocusStyle(firstNameField);
        applyFocusStyle(lastNameField);
        applyFocusStyle(emailField);
        applyFocusStyle(phoneField);

        // ===== Wrapper with header =====
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(createHeaderPanel(),BorderLayout.NORTH);
        wrapper.add(form,BorderLayout.CENTER);

        setContentPane(wrapper);
    }

    // ===== HEADER =====
    private JPanel createHeaderPanel(){
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(245,248,255));
        header.setBorder(BorderFactory.createEmptyBorder(18,25,15,25));

        String titleText = customer==null ? "Register New Customer" : "Edit Customer Profile";
        String subtitleText = customer==null
                ? "Enter customer information to create a profile"
                : "Modify customer details and account status";

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI",Font.BOLD,24));
        title.setForeground(new Color(30,55,140));

        JLabel subtitle = new JLabel(subtitleText);
        subtitle.setFont(new Font("Segoe UI",Font.PLAIN,13));
        subtitle.setForeground(new Color(110,120,145));

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text,BoxLayout.Y_AXIS));
        text.setOpaque(false);
        text.add(title);
        text.add(Box.createVerticalStrut(4));
        text.add(subtitle);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(220,225,235));

        header.add(text,BorderLayout.CENTER);
        header.add(sep,BorderLayout.SOUTH);

        return header;
    }

    // ===== UI Helpers =====
    private JLabel createLabel(String text){
        JLabel lbl=new JLabel(text);
        lbl.setFont(new Font("Segoe UI",Font.BOLD,14));
        return lbl;
    }

    private void addField(JPanel panel, GridBagConstraints gbc,
                          int row, String label, JTextField field){

        gbc.gridx=0; gbc.gridy=row;
        panel.add(createLabel(label),gbc);

        field.setBorder(new LineBorder(new Color(210,210,210)));
        field.setPreferredSize(new Dimension(200,32));

        gbc.gridx=1;
        panel.add(field,gbc);
    }

    private void applyFocusStyle(JTextField field){
        field.addFocusListener(new FocusAdapter(){
            public void focusGained(FocusEvent e){
                field.setBorder(new LineBorder(new Color(80,130,255),2));
            }
            public void focusLost(FocusEvent e){
                field.setBorder(new LineBorder(new Color(210,210,210)));
            }
        });
    }

    private JButton createButton(String text, boolean primary){
        JButton b=new JButton(text);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(8,18,8,18));
        b.setFont(new Font("Segoe UI",Font.PLAIN,14));

        if(primary){
            b.setBackground(new Color(70,120,255));
            b.setForeground(Color.WHITE);
        }else{
            b.setBackground(new Color(235,235,235));
        }
        return b;
    }

    private void styleCombo(JComboBox<?> combo){
        combo.setFont(new Font("Segoe UI",Font.PLAIN,13));
        combo.setBackground(Color.WHITE);
    }

    // ===== Data Handling =====
    private void loadCustomerData(){
        firstNameField.setText(customer.getFirstName());
        lastNameField.setText(customer.getLastName());
        emailField.setText(customer.getEmail());
        phoneField.setText(customer.getPhone());
        addressArea.setText(customer.getAddress());
        if(statusCombo!=null){
            statusCombo.setSelectedItem(customer.getStatus());
        }
    }

    private void saveCustomer(){

        validationLabel.setText(" ");

        if(firstNameField.getText().trim().isEmpty()){
            validationLabel.setText("First name required");
            return;
        }

        if(lastNameField.getText().trim().isEmpty()){
            validationLabel.setText("Last name required");
            return;
        }

        String email=emailField.getText().trim();
        if(email.isEmpty() || !EMAIL_PATTERN.matcher(email).matches()){
            validationLabel.setText("Invalid email");
            return;
        }

        try{
            if(customer==null){
                Customer c = customerService.registerCustomer(
                        firstNameField.getText().trim(),
                        lastNameField.getText().trim(),
                        email,
                        phoneField.getText().trim(),
                        addressArea.getText().trim()
                );
                JOptionPane.showMessageDialog(this,
                        "Customer created. ID: "+c.getCustomerId());
            }else{
                customer.setFirstName(firstNameField.getText().trim());
                customer.setLastName(lastNameField.getText().trim());
                customer.setEmail(email);
                customer.setPhone(phoneField.getText().trim());
                customer.setAddress(addressArea.getText().trim());
                if(statusCombo!=null){
                    customer.setStatus((String)statusCombo.getSelectedItem());
                }

                customerService.updateCustomer(customer);
                JOptionPane.showMessageDialog(this,"Customer updated");
            }

            saved=true;
            dispose();

        }catch(Exception ex){
            JOptionPane.showMessageDialog(this,ex.getMessage(),
                    "Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved(){
        return saved;
    }
}
