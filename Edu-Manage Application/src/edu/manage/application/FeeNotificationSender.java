package edu.manage.application;

import java.sql.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class FeeNotificationSender {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/edu_manage";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Rajashree@123";

    public static void sendNotifications() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT s.email, f.remaining_fees " +
                    "FROM students s JOIN fees f ON s.id = f.student_id " +
                    "WHERE f.status = 'Incomplete'";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String email = rs.getString("email");
                double remainingFees = rs.getDouble("remaining_fees");

                String subject = "Outstanding Fee Notification";
                String body = "Dear Student,\n\nYou have an outstanding fee of $" + remainingFees +
                        ". Please make the payment at your earliest convenience.\n\nThank you.";

                sendEmail(email, subject, body);
            }

            System.out.println("Fee notifications sent successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void sendEmail(String to, String subject, String body) {
        String from = "abhijeetkamalekar1509@gmail.com";
        String password = "yfvq rgcy qtju wygh";
        String host = "smtp.gmail.com";

        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent to: " + to);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        sendNotifications();
    }
}
