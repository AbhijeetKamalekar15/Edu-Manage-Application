package edu.manage.application;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
//import com.itextpdf.layout.style.Style;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileNotFoundException;
import java.sql.*;

public class ViewStudentsFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> courseComboBox;

    public ViewStudentsFrame() {
        setTitle("View Students");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Name", "DOB", "Address", "Email", "Phone", "Course", "Fees"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        courseComboBox = new JComboBox<>(new String[]{"All", "C", "C++", "DSA", "C++, DSA", "Java", "SpringBoot"});
        courseComboBox.addActionListener(e -> loadStudents());

        JButton refreshButton = new JButton("Refresh");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton pdfButton = new JButton("Export to PDF");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(new JLabel("Filter by Course:"));
        buttonPanel.add(courseComboBox);
        buttonPanel.add(refreshButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(pdfButton);

        refreshButton.addActionListener(e -> loadStudents());

        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                String name = (String) tableModel.getValueAt(selectedRow, 1);
                String dob = (String) tableModel.getValueAt(selectedRow, 2);
                String address = (String) tableModel.getValueAt(selectedRow, 3);
                String email = (String) tableModel.getValueAt(selectedRow, 4);
                String phone = (String) tableModel.getValueAt(selectedRow, 5);
                String course = (String) tableModel.getValueAt(selectedRow, 6);
                double fees = (double) tableModel.getValueAt(selectedRow, 7);

                updateStudent(id, name, dob, address, email, phone, course, fees);
            } else {
                JOptionPane.showMessageDialog(ViewStudentsFrame.this, "No student selected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(ViewStudentsFrame.this, "Are you sure you want to delete this student?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteStudent(id);
                }
            } else {
                JOptionPane.showMessageDialog(ViewStudentsFrame.this, "No student selected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        pdfButton.addActionListener(e -> generatePDF());

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.NORTH);

        loadStudents();
    }

    private void loadStudents() {
        tableModel.setRowCount(0);
        String url = "jdbc:mysql://localhost:3306/edu_manage";
        String user = "root";
        String password = "Rajashree@123";

        String selectedCourse = courseComboBox.getSelectedItem().toString();
        String query = "SELECT * FROM students";
        if (!selectedCourse.equals("All")) {
            query += " WHERE course = ?";
        }

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (!selectedCourse.equals("All")) {
                stmt.setString(1, selectedCourse);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String dob = rs.getString("dob");
                    String address = rs.getString("address");
                    String email = rs.getString("email");
                    String phone = rs.getString("phone");
                    String course = rs.getString("course");
                    double fees = rs.getDouble("fees");

                    tableModel.addRow(new Object[]{id, name, dob, address, email, phone, course, fees});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading students.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudent(int id, String name, String dob, String address, String email, String phone, String course, double fees) {
        JTextField nameField = new JTextField(name);
        JTextField dobField = new JTextField(dob);
        JTextField addressField = new JTextField(address);
        JTextField emailField = new JTextField(email);
        JTextField phoneField = new JTextField(phone);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("DOB:"));
        panel.add(dobField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Update Student", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String updatedName = nameField.getText();
            String updatedDob = dobField.getText();
            String updatedAddress = addressField.getText();
            String updatedEmail = emailField.getText();
            String updatedPhone = phoneField.getText();

            String url = "jdbc:mysql://localhost:3306/edu_manage";
            String user = "root";
            String password = "Rajashree@123";

            String query = "UPDATE students SET name = ?, dob = ?, address = ?, email = ?, phone = ? WHERE id = ?";

            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, updatedName);
                stmt.setString(2, updatedDob);
                stmt.setString(3, updatedAddress);
                stmt.setString(4, updatedEmail);
                stmt.setString(5, updatedPhone);
                stmt.setInt(6, id);

                int rowsUpdated = stmt.executeUpdate();

                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Student updated successfully.");
                    loadStudents();
                } else {
                    JOptionPane.showMessageDialog(this, "No student found with the specified ID.", "Update Failed", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating student: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void deleteStudent(int id) {
        String url = "jdbc:mysql://localhost:3306/edu_manage";
        String user = "root";
        String password = "Rajashree@123";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);

            try {
                String deleteFeesQuery = "DELETE FROM fees WHERE student_id = ?";
                try (PreparedStatement deleteFeesStmt = conn.prepareStatement(deleteFeesQuery)) {
                    deleteFeesStmt.setInt(1, id);
                    deleteFeesStmt.executeUpdate();
                }

                String deleteStudentQuery = "DELETE FROM students WHERE id = ?";
                try (PreparedStatement deleteStudentStmt = conn.prepareStatement(deleteStudentQuery)) {
                    deleteStudentStmt.setInt(1, id);
                    int rowsDeleted = deleteStudentStmt.executeUpdate();

                    if (rowsDeleted > 0) {
                        JOptionPane.showMessageDialog(this, "Student deleted successfully.");
                        loadStudents();
                    } else {
                        JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting student.", "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generatePDF() {
        String pdfPath = "students_report.pdf"; // Specify the path for the generated PDF

        try {
            PdfWriter writer = new PdfWriter(pdfPath);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Add title with custom style
            document.add(new Paragraph("Students Report").setFontSize(16));

            float[] columnWidths = {50, 150, 80, 150, 150, 100, 100, 70};
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
                    if (col == 4) { // Email column
                        cell.setFontColor(new DeviceRgb(0, 0, 255));
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
            new ViewStudentsFrame().setVisible(true);
        });
    }
}
