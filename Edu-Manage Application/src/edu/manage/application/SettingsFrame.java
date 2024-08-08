package edu.manage.application;

// SettingsFrame.java
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SettingsFrame extends JFrame {
    private JTextField smtpHostField, smtpPortField, emailField, passwordField;
    private JButton saveButton;

    public SettingsFrame() {
        setTitle("Settings");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        smtpHostField = new JTextField();
        smtpPortField = new JTextField();
        emailField = new JTextField();
        passwordField = new JPasswordField();
        saveButton = new JButton("Save");

        panel.add(new JLabel("SMTP Host:"));
        panel.add(smtpHostField);
        panel.add(new JLabel("SMTP Port:"));
        panel.add(smtpPortField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(saveButton);

        add(panel);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSettings();
            }
        });
    }

    private void saveSettings() {
        String smtpHost = smtpHostField.getText();
        String smtpPort = smtpPortField.getText();
        String email = emailField.getText();
        String password = new String(((JPasswordField) passwordField).getPassword());

        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", smtpHost);
        properties.setProperty("mail.smtp.port", smtpPort);
        properties.setProperty("mail.smtp.user", email);
        properties.setProperty("mail.smtp.password", password);

        try (FileOutputStream output = new FileOutputStream("email.properties")) {
            properties.store(output, null);
            JOptionPane.showMessageDialog(this, "Settings saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SettingsFrame().setVisible(true);
        });
    }
}

