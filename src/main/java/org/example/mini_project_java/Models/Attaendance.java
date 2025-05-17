package org.example.mini_project_java.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.example.mini_project_java.Database.DatabaseConnection;

public class Attaendance {
    private String studentId;
    private String courseCode;
    private int week;
    private String status;

    public Attaendance() {
    }

    public Attaendance(String studentId, String courseCode, int week, String status) {
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.week = week;
        this.status = status;
    }

    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Undergratuate> loadStudents(String courseCode, int weekNumber) {
        List<Undergratuate> studentRecords = new ArrayList<>();
        Connection dbConnection = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = dbConnection.prepareStatement(
                "SELECT u.username, u.full_name, a.week" + weekNumber +
                        " FROM USERS u LEFT JOIN attendance a ON u.username = a.student_id AND a.course_code = ? " +
                        " WHERE u.role = 'student'")) {
            stmt.setString(1, courseCode);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String studentId = rs.getString("username");
                    String studentName = rs.getString("full_name");
                    String existingStatus;
                    if (rs.getObject("week" + weekNumber) == null) {
                        existingStatus = "Not Posted";
                    } else {
                        existingStatus = rs.getBoolean("week" + weekNumber) ? "Present" : "Absent";
                    }
                    Undergratuate student = new Undergratuate(studentId, null, studentName, null, "student", null);
                    student.setAttendanceStatus(existingStatus);
                    // Calculate attendance percentage
                    double percentage = calculateAttendancePercentage(studentId, courseCode);
                    student.setAttendancePercentage(percentage);
                    studentRecords.add(student);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading students: " + e.getMessage(), e);
        }
        return studentRecords;
    }

    public void updateAttendance(String studentId, String courseCode, int weekNumber, boolean isPresent) {
        Connection dbConnection = DatabaseConnection.getConnection();
        String weekColumn = "week" + weekNumber;

        try {
            dbConnection.setAutoCommit(false);

            String checkQuery = "SELECT 1 FROM attendance WHERE student_id = ? AND course_code = ?";
            try (PreparedStatement checkStmt = dbConnection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, studentId);
                checkStmt.setString(2, courseCode);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        String updateQuery = "UPDATE attendance SET " + weekColumn + " = ? WHERE student_id = ? AND course_code = ?";
                        try (PreparedStatement updateStmt = dbConnection.prepareStatement(updateQuery)) {
                            updateStmt.setBoolean(1, isPresent);
                            updateStmt.setString(2, studentId);
                            updateStmt.setString(3, courseCode);
                            updateStmt.executeUpdate();
                        }
                    } else {
                        String insertQuery = "INSERT INTO attendance (student_id, course_code, " + weekColumn + ") VALUES (?, ?, ?)";
                        try (PreparedStatement insertStmt = dbConnection.prepareStatement(insertQuery)) {
                            insertStmt.setString(1, studentId);
                            insertStmt.setString(2, courseCode);
                            insertStmt.setBoolean(3, isPresent);
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }

            dbConnection.commit();
        } catch (SQLException e) {
            try {
                dbConnection.rollback();
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Rollback failed: " + rollbackEx.getMessage(), rollbackEx);
            }
            throw new RuntimeException("Error updating attendance: " + e.getMessage(), e);
        } finally {
            try {
                dbConnection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Error resetting auto-commit: " + e.getMessage(), e);
            }
        }
    }

    public double calculateAttendancePercentage(String studentId, String courseCode) {
        Connection dbConnection = DatabaseConnection.getConnection();
        String query = "SELECT week1, week2, week3, week4, week5, week6, week7, week8, week9, week10, week11, week12, week13, week14, week15 " +
                "FROM attendance WHERE student_id = ? AND course_code = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
            stmt.setString(1, studentId);
            stmt.setString(2, courseCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int presentCount = 0;
                    int totalPosted = 0;
                    for (int i = 1; i <= 15; i++) {
                        if (rs.getObject("week" + i) != null) {
                            totalPosted++;
                            if (rs.getBoolean("week" + i)) {
                                presentCount++;
                            }
                        }
                    }
                    return totalPosted == 0 ? 0.0 : (presentCount * 100.0) / totalPosted;
                }
                return 0.0; // No attendance record exists
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating attendance percentage: " + e.getMessage(), e);
        }
    }

    public boolean isValidStudent(String studentId) {
        Connection dbConnection = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = dbConnection.prepareStatement("SELECT 1 FROM USERS WHERE username = ? AND role = 'student'")) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error validating student: " + e.getMessage(), e);
        }
    }

    public boolean isValidCourse(String courseCode) {
        Connection dbConnection = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = dbConnection.prepareStatement("SELECT 1 FROM COURSE WHERE course_code = ?")) {
            stmt.setString(1, courseCode);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error validating course: " + e.getMessage(), e);
        }
    }
}