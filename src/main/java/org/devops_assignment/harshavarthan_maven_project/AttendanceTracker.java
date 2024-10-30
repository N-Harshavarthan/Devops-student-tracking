package org.devops_assignment.harshavarthan_maven_project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AttendanceTracker {

    // Method to mark attendance for a student
    public void markAttendance(String studentName, boolean status) {
        String sql = "INSERT INTO attendance (student_name, date, status) VALUES (?, CURDATE(), ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentName);
            stmt.setBoolean(2, status);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    // Method to calculate attendance rate for each student
    public Map<String, Double> calculateAttendanceRate() {
        String sql = "SELECT student_name, (SUM(CASE WHEN status = TRUE THEN 1 ELSE 0 END) / COUNT(*)) * 100 AS attendance_rate " +
                     "FROM attendance GROUP BY student_name";
        Map<String, Double> attendanceRates = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String studentName = rs.getString("student_name");
                double attendanceRate = rs.getDouble("attendance_rate");
                attendanceRates.put(studentName, attendanceRate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendanceRates;
    }
    
    public void runAttendanceTracker() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter student's name: ");
        String studentName = scanner.nextLine();
        
        System.out.print("Is the student present? (yes/no): ");
        String statusInput = scanner.nextLine();
        boolean status = statusInput.equalsIgnoreCase("yes");

        markAttendance(studentName, status);

        // Optional: Calculate and display the attendance rate after marking attendance
        Map<String, Double> rates = calculateAttendanceRate();
        System.out.println("\n\nCurrent Attendance Rates:");
        for (Map.Entry<String, Double> entry : rates.entrySet()) {
            System.out.println("Student: " + entry.getKey() + ", Attendance Rate: " + entry.getValue() + "%");
        }

        scanner.close();
    }
}

