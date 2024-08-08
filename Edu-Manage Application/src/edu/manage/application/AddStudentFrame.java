package edu.manage.application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddStudentFrame extends JFrame {
    private JTextField nameField, dobField, addressField, emailField, phoneField, feesField, paidField;
    private JComboBox<String> courseComboBox;
    private JButton addButton;

    public AddStudentFrame() {
        setTitle("Add Student");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;

        // Add each label and text field to the panel using GridBagConstraints
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(20);
        panel.add(nameField, gbc);

        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel("DOB (yyyy-mm-dd):"), gbc);

        gbc.gridx = 1;
        dobField = new JTextField(20);
        panel.add(dobField, gbc);

        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel("Address:"), gbc);

        gbc.gridx = 1;
        addressField = new JTextField(20);
        panel.add(addressField, gbc);

        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        panel.add(emailField, gbc);

        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel("Phone:"), gbc);

        gbc.gridx = 1;
        phoneField = new JTextField(20);
        panel.add(phoneField, gbc);

        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel("Course:"), gbc);

        gbc.gridx = 1;
        courseComboBox = new JComboBox<>(new String[]{"C", "C++", "DSA", "C++, DSA", "Java", "SpringBoot"});
        panel.add(courseComboBox, gbc);

        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel("Fees:"), gbc);

        gbc.gridx = 1;
        feesField = new JTextField(20);
        feesField.setEditable(false);
        panel.add(feesField, gbc);

        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel("Paid Fees:"), gbc);

        gbc.gridx = 1;
        paidField = new JTextField(20);
        panel.add(paidField, gbc);

        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        addButton = new JButton("Add Student");
        panel.add(addButton, gbc);

        add(panel);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStudent();
            }
        });

        courseComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateFees();
            }
        });

        updateFees(); // Initialize fees field based on selected course
    }

    private void updateFees() {
        String course = (String) courseComboBox.getSelectedItem();
        double totalFees;

        switch (course) {
            case "C":
                totalFees = 6000.00;
                break;
            case "C++":
                totalFees = 8000.00;
                break;
            case "DSA":
                totalFees = 8000.00;
                break;
            case "C++, DSA":
                totalFees = 15000.00;
                break;
            case "Java":
                totalFees = 8000.00;
                break;
            case "SpringBoot":
                totalFees = 6000.00;
                break;
            default:
                totalFees = 0.00;
                break;
        }

        feesField.setText(String.valueOf(totalFees));
    }

    private void addStudent() {
        String name = nameField.getText();
        String dob = dobField.getText();
        String address = addressField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String course = (String) courseComboBox.getSelectedItem();
        double paidFees;

        try {
            paidFees = Double.parseDouble(paidField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid paid fees amount.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double totalFees = Double.parseDouble(feesField.getText());

        String url = "jdbc:mysql://localhost:3306/edu_manage";
        String user = "root";
        String password = "Rajashree@123";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);

            String insertStudentQuery = "INSERT INTO students (name, dob, address, email, phone, course, fees, paid) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement studentStmt = conn.prepareStatement(insertStudentQuery, Statement.RETURN_GENERATED_KEYS)) {
                studentStmt.setString(1, name);
                studentStmt.setDate(2, Date.valueOf(dob));
                studentStmt.setString(3, address);
                studentStmt.setString(4, email);
                studentStmt.setString(5, phone);
                studentStmt.setString(6, course);
                studentStmt.setDouble(7, totalFees);
                studentStmt.setDouble(8, paidFees);
                studentStmt.executeUpdate();

                ResultSet generatedKeys = studentStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int studentId = generatedKeys.getInt(1);

                    String insertFeesQuery = "INSERT INTO fees (student_id, total_fees, paid_fees, remaining_fees, status, date_added, last_payment_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement feesStmt = conn.prepareStatement(insertFeesQuery)) {
                        feesStmt.setInt(1, studentId);
                        feesStmt.setDouble(2, totalFees);
                        feesStmt.setDouble(3, paidFees);
                        feesStmt.setDouble(4, totalFees - paidFees);
                        feesStmt.setString(5, (totalFees - paidFees) == 0 ? "Completed" : "Incomplete");
                        feesStmt.setDate(6, new Date(System.currentTimeMillis()));
                        feesStmt.setDate(7, new Date(System.currentTimeMillis()));
                        feesStmt.executeUpdate();
                    }
                }
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Student added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding student: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AddStudentFrame().setVisible(true);
        });
    }
}
