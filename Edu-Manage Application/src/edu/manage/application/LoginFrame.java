package edu.manage.application;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginFrame extends JFrame implements ActionListener {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        // Set up the frame
        setTitle("Login");
        setSize(400, 250);
        setLocation(600,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Create and add components
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 45, 80, 25);
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 45, 165, 25);
        add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 75, 80, 25);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 75, 165, 25);
        add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(150, 115, 80, 25);
        loginButton.addActionListener(this);
        add(loginButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Retrieve user input
        String username = usernameField.getText();
        char[] password = passwordField.getPassword();

        // Authenticate user
        boolean isAuthenticated = authenticateUser(username, new String(password));

        if (isAuthenticated) {
            // Open Dashboard
            openDashboard();
        } else {
            // Show error message
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean authenticateUser(String username, String password) {
        final String URL = "jdbc:mysql://localhost:3306/edu_manage";
        final String DB_USER = "root";
        final String DB_PASSWORD = "Rajashree@123";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish a connection to the database
            conn = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);

            // Prepare the SQL query
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            // Execute the query
            rs = pstmt.executeQuery();

            // Check if a matching user was found
            return rs.next();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void openDashboard() {
        // Replace with actual code to open the dashboard
        JOptionPane.showMessageDialog(this, "Login successful! Opening Dashboard...", "Success", JOptionPane.INFORMATION_MESSAGE);
        // Example: new DashboardFrame().setVisible(true);
        // this.dispose(); // Close login frame
        // Create and show the dashboard frame
        SwingUtilities.invokeLater(() -> {
            new DashboardFrame().setVisible(true);
        });

        // Close the login frame
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
