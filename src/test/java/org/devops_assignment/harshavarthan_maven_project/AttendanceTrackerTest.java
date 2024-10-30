package org.devops_assignment.harshavarthan_maven_project;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import static org.junit.Assert.*;

public class AttendanceTrackerTest {
    private AttendanceTracker tracker;

    @Before
    public void setUp() {
        // Initialize the AttendanceTracker
        tracker = new AttendanceTracker();
        // Clear the attendance table before each test
        clearAttendanceTable();
    }

    @After
    public void tearDown() {
        // Clear the attendance table after each test
        clearAttendanceTable();
    }

    // Helper method to clear the attendance table
    private void clearAttendanceTable() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM attendance")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDatabaseConnection() {
        // Test that the database connection is not null
        Connection conn = DatabaseConnection.getConnection();
        assertNotNull("Database connection should not be null", conn);
    }
    

    @Test
    public void testMarkAttendance() {
        // Mark attendance for a sample student
        tracker.markAttendance("Alice", true);

        // Retrieve attendance rates to verify
        Map<String, Double> rates = tracker.calculateAttendanceRate();
        assertTrue("Attendance rates should contain Alice", rates.containsKey("Alice"));
    }

    @Test
    public void testCalculateAttendanceRateSingleStudent() {
        // Mark attendance for a single student
        tracker.markAttendance("Alice", true);
        tracker.markAttendance("Alice", false);
        tracker.markAttendance("Alice", true);

        // Calculate attendance rate
        Map<String, Double> rates = tracker.calculateAttendanceRate();

        // Verify attendance rate (Alice: 2 out of 3 days = ~66.67%)
        assertEquals("Alice's attendance rate should be 66.67%", 66.67, rates.get("Alice"), 0.01);
    }

    @Test
    public void testCalculateAttendanceRateMultipleStudents() {
        // Mark attendance for multiple students
        tracker.markAttendance("Alice", true);
        tracker.markAttendance("Alice", false);
        tracker.markAttendance("Bob", true);
        tracker.markAttendance("Bob", true);

        // Calculate attendance rates
        Map<String, Double> rates = tracker.calculateAttendanceRate();

        // Verify attendance rates (Alice: 50%, Bob: 100%)
        assertEquals("Alice's attendance rate should be 50%", 50.0, rates.get("Alice"), 0.01);
        assertEquals("Bob's attendance rate should be 100%", 100.0, rates.get("Bob"), 0.01);
    }

    @Test
    public void testCalculateAttendanceRateNoRecords() {
        // Calculate attendance rate without any records
        Map<String, Double> rates = tracker.calculateAttendanceRate();

        // Verify that the rates map is empty
        assertTrue("Attendance rates should be empty when no records are present", rates.isEmpty());
    }
}

