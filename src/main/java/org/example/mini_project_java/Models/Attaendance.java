package org.example.mini_project_java.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public List<Map<String, String>> viewAttendance(String studentId) {
        List<Map<String, String>> attendanceRecords = new ArrayList<>();
        Connection dbConnection = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = dbConnection.prepareStatement(
                "SELECT a.student_id, u.name, a.course_code, a.week, a.status " +
                        "FROM attendance a " +
                        "JOIN USERS u ON a.student_id = u.id " +
                        "WHERE a.student_id = ?")) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> record = new HashMap<>();
                    record.put("student_id", rs.getString("student_id"));
                    record.put("student_name", rs.getString("name"));
                    record.put("course_code", rs.getString("course_code"));
                    record.put("week", String.valueOf(rs.getInt("week")));
                    record.put("status", rs.getString("status"));
                    attendanceRecords.add(record);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving attendance records: " + e.getMessage(), e);
        }

        return attendanceRecords;
    }
}