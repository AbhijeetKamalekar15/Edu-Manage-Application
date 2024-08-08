package edu.manage.application;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.sql.*;

public class ManageFeesFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton refreshButton, updateFeeButton, sendNotificationButton, pdfButton;

    public ManageFeesFrame() {
        setTitle("Manage Fees");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{"Student ID", "Name", "Total Fees", "Paid Fees", "Remaining Fees", "Status"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        refreshButton = new JButton("Refresh");
        updateFeeButton = new JButton("Update Fee");
        sendNotificationButton = new JButton("Send Notifications");
        pdfButton = new JButton("Export to PDF");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(updateFeeButton);
        buttonPanel.add(sendNotificationButton);
        buttonPanel.add(pdfButton);

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFees();
            }
        });

        updateFeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int studentId = (int) tableModel.getValueAt(selectedRow, 0);
                    updateStudentFee(studentId);
                } else {
                    JOptionPane.showMessageDialog(ManageFeesFrame.this, "No student selected.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        sendNotificationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FeeNotificationSender.sendNotifications();
            }
        });

        pdfButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generatePDF();
            }
        });

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadFees();
    }

    private void loadFees() {
        tableModel.setRowCount(0); // Clear existing data
        String url = "jdbc:mysql://localhost:3306/edu_manage";
        String user = "root";
        String password = "Rajashree@123";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT f.student_id, s.name, f.total_fees, f.paid_fees, f.remaining_fees, f.status " +
                    "FROM fees f JOIN students s ON f.student_id = s.id";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int studentId = rs.getInt("student_id");
                String name = rs.getString("name");
                double totalFees = rs.getDouble("total_fees");
                double paidFees = rs.getDouble("paid_fees");
                double remainingFees = rs.getDouble("remaining_fees");
                String status = rs.getString("status");

                tableModel.addRow(new Object[]{studentId, name, totalFees, paidFees, remainingFees, status});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateStudentFee(int studentId) {
        String url = "jdbc:mysql://localhost:3306/edu_manage";
        String user = "root";
        String password = "Rajashree@123";

        double newFeePaid;
        try {
            String input = JOptionPane.showInputDialog(this, "Enter amount paid:", "Update Fee", JOptionPane.PLAIN_MESSAGE);
            if (input == null || input.isEmpty()) return;
            newFeePaid = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);

            String query = "SELECT total_fees, paid_fees FROM fees WHERE student_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double totalFees = rs.getDouble("total_fees");
                double paidFees = rs.getDouble("paid_fees");
                double updatedPaidFees = paidFees + newFeePaid;
                double remainingFees = totalFees - updatedPaidFees;
                String status = (remainingFees <= 0) ? "Completed" : "Incomplete";

                String updateQuery = "UPDATE fees SET paid_fees = ?, remaining_fees = ?, status = ?, last_payment_date = ? WHERE student_id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setDouble(1, updatedPaidFees);
                    updateStmt.setDouble(2, remainingFees);
                    updateStmt.setString(3, status);
                    updateStmt.setDate(4, new Date(System.currentTimeMillis())); // Set current date as last payment date
                    updateStmt.setInt(5, studentId);

                    int rowsUpdated = updateStmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(this, "Fee updated successfully.");
                        loadFees();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update fee.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Student not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating fee: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generatePDF() {
        String pdfPath = "fees_report.pdf"; // Specify the path for the generated PDF

        try {
            PdfWriter writer = new PdfWriter(pdfPath);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Add title with custom style
            document.add(new Paragraph("Fees Report").setFontSize(16));

            float[] columnWidths = {50, 150, 100, 100, 100, 100};
            Table table = new Table(columnWidths);

            // Adding table headers with bold font
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                Cell headerCell = new Cell().add(new Paragraph(tableModel.getColumnName(i)))
                        .setBold().setFontSize(10);
                table.addHeaderCell(headerCell);
            }

            // Adding table rows
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                for (int col = 0; col < tableModel.getColumnCount(); col++) {
                    String cellValue = tableModel.getValueAt(row, col).toString();
                    Cell cell = new Cell().add(new Paragraph(cellValue).setFontSize(8));
                    if (col == 5) { // Status column
                        if ("Incomplete".equals(cellValue)) {
                            cell.setFontColor(new DeviceRgb(255, 0, 0)); // Red color for "Incomplete"
                        } else if ("Completed".equals(cellValue)) {
                            cell.setFontColor(new DeviceRgb(0, 128, 0)); // Green color for "Completed"
                        }
                    }
                    table.addCell(cell);
                }
            }

            document.add(table);
            document.close();

            JOptionPane.showMessageDialog(this, "PDF generated successfully at: " + pdfPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating PDF.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ManageFeesFrame().setVisible(true);
        });
    }
}
