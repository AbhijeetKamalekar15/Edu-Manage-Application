package edu.manage.application;

// DashboardFrame.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DashboardFrame extends JFrame {
    private JButton addStudentButton, viewStudentsButton, manageFeesButton, settingsButton;

    public DashboardFrame() {
        setTitle("Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for better control

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding around buttons
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Initialize buttons
        addStudentButton = createButton("Add Student");
        viewStudentsButton = createButton("View Students");
        manageFeesButton = createButton("Manage Fees");
        settingsButton = createButton("Settings");

        // Add buttons to the panel with GridBagConstraints
        buttonPanel.add(addStudentButton, gbc);
        gbc.gridy++;
        buttonPanel.add(viewStudentsButton, gbc);
        gbc.gridy++;
        buttonPanel.add(manageFeesButton, gbc);
        gbc.gridy++;
        buttonPanel.add(settingsButton, gbc);

        // Set the button panel as the content pane
        setContentPane(buttonPanel);

        // Add action listeners
        addStudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddStudentFrame().setVisible(true);
            }
        });

        viewStudentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ViewStudentsFrame().setVisible(true);
            }
        });

        manageFeesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ManageFeesFrame().setVisible(true);
            }
        });

        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SettingsFrame().setVisible(true);
            }
        });
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 40)); // Set button size
        button.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font and size
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder()); // Add border
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DashboardFrame().setVisible(true);
        });
    }
}
