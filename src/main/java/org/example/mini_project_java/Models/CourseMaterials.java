package org.example.mini_project_java.Models;

import org.example.mini_project_java.Database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseMaterials {
    private int id;
    private String courseCode;
    private String week;
    private String courseMaterial;
    private String courseType;

    public CourseMaterials(String courseCode, String week, String courseMaterial, String courseType) {
        this.courseCode = courseCode;
        this.week = week;
        this.courseMaterial = courseMaterial;
        this.courseType = courseType;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getWeek() { return week; }
    public void setWeek(String week) { this.week = week; }
    public String getCourseMaterial() { return courseMaterial; }
    public void setCourseMaterial(String courseMaterial) { this.courseMaterial = courseMaterial; }
    public String getCourseType() { return courseType; }
    public void setCourseType(String courseType) { this.courseType = courseType; }

    public void save() {
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            String insertSQL = "INSERT INTO course_materials (course_code, week, courseMaterial, courseType) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = connectDB.prepareStatement(insertSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, courseCode);
                ps.setString(2, week);
                ps.setString(3, courseMaterial);
                ps.setString(4, courseType);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    this.id = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save course material: " + e.getMessage());
        }
    }

    public static List<CourseMaterials> fetchMaterialsByCourseCode(String courseCode) {
        List<CourseMaterials> materials = new ArrayList<>();
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM course_materials WHERE course_code = ? ORDER BY week";
            try (PreparedStatement ps = connectDB.prepareStatement(query)) {
                ps.setString(1, courseCode);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    CourseMaterials material = new CourseMaterials(
                            rs.getString("course_code"),
                            rs.getString("week"),
                            rs.getString("courseMaterial"),
                            rs.getString("courseType")
                    );
                    material.setId(rs.getInt("id"));
                    materials.add(material);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch course materials: " + e.getMessage());
        }
        return materials;
    }

    public static List<String> fetchCoursesByLecturer(String lecturerId) {
        List<String> courses = new ArrayList<>();
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            String query = "SELECT course_code FROM course WHERE lecturer_id = ?";
            try (PreparedStatement ps = connectDB.prepareStatement(query)) {
                ps.setString(1, lecturerId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    courses.add(rs.getString("course_code"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch courses: " + e.getMessage());
        }
        return courses;
    }
}
