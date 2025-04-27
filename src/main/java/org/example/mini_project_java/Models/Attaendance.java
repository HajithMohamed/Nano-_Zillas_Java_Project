package org.example.mini_project_java.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Attaendance {
    private String student_id;
    private String Course_code;
    private String week;
    private String status;

    // Assuming we have a database connection somewhere
    private Connection dbConnection;

    public Attaendance(String student_id, String course_code, String week, String status) {
        this.student_id = student_id;
        Course_code = course_code;
        this.week = week;
        this.status = status;
    }

    public Attaendance() {
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getCourse_code() {
        return Course_code;
    }

    public void setCourse_code(String course_code) {
        Course_code = course_code;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Map<String, String>> viewAttaendance(String student_id) {
        List<Map<String, String>> attaendanceRecords = new ArrayList<>();

        try {
            // SQL query to get all attaendance details for a specific student
            String query = "SELECT a.student_id, s.student_name, a.Course_code, c.course_name, " +
                    "a.week, a.status " +
                    "FROM attaendance a " +
                    "JOIN students s ON a.student_id = s.student_id " +
                    "JOIN courses c ON a.Course_code = c.course_code " +
                    "WHERE a.student_id = ?";

            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setString(1, student_id);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, String> record = new HashMap<>();
                record.put("student_id", rs.getString("student_id"));
                record.put("student_name", rs.getString("student_name"));
                record.put("Course_code", rs.getString("Course_code"));
                record.put("course_name", rs.getString("course_name"));
                record.put("week", rs.getString("week"));
                record.put("status", rs.getString("status"));

                attaendanceRecords.add(record);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return attaendanceRecords;
    }

    public void updateAttaendance(String course_code, String week) {
        try {
            // First get all students enrolled in this course
            String studentQuery = "SELECT s.student_id, s.student_name " +
                    "FROM students s " +
                    "JOIN enrollment e ON s.student_id = e.student_id " +
                    "WHERE e.course_code = ?";

            PreparedStatement studentStmt = dbConnection.prepareStatement(studentQuery);
            studentStmt.setString(1, course_code);

            ResultSet studentRs = studentStmt.executeQuery();

            // For each student, update or insert attaendance record
            while (studentRs.next()) {
                String studentId = studentRs.getString("student_id");

                // Check if attaendance record already exists
                String checkQuery = "SELECT * FROM attaendance WHERE student_id = ? AND Course_code = ? AND week = ?";
                PreparedStatement checkStmt = dbConnection.prepareStatement(checkQuery);
                checkStmt.setString(1, studentId);
                checkStmt.setString(2, course_code);
                checkStmt.setString(3, week);

                ResultSet checkRs = checkStmt.executeQuery();

                if (checkRs.next()) {
                    // Record exists, update it
                    // This would typically come from a form input, defaulting to "Present" here
                    String status = "Present";

                    String updateQuery = "UPDATE attaendance SET status = ? " +
                            "WHERE student_id = ? AND Course_code = ? AND week = ?";

                    PreparedStatement updateStmt = dbConnection.prepareStatement(updateQuery);
                    updateStmt.setString(1, status);
                    updateStmt.setString(2, studentId);
                    updateStmt.setString(3, course_code);
                    updateStmt.setString(4, week);

                    updateStmt.executeUpdate();
                    updateStmt.close();
                } else {
                    // Record doesn't exist, insert new one
                    // This would typically come from a form input, defaulting to "Present" here
                    String status = "Present";

                    String insertQuery = "INSERT INTO attaendance (student_id, Course_code, week, status) " +
                            "VALUES (?, ?, ?, ?)";

                    PreparedStatement insertStmt = dbConnection.prepareStatement(insertQuery);
                    insertStmt.setString(1, studentId);
                    insertStmt.setString(2, course_code);
                    insertStmt.setString(3, week);
                    insertStmt.setString(4, status);

                    insertStmt.executeUpdate();
                    insertStmt.close();
                }

                checkRs.close();
                checkStmt.close();
            }

            studentRs.close();
            studentStmt.close();

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}