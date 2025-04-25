package org.example.mini_project_java.Models;

import org.example.mini_project_java.Database.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class Admin extends Users {

    public Admin(String username, String password, String name, String email, String role, String mobileNo) {
        super(username, password, name, email, role, mobileNo);
    }

    public Admin() {
        // Default constructor
    }

    public void createUserProfiles(String username, String fullName, String email, String role, String mobile, String password, boolean isEdit) {
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            String hashedPassword = password != null && !password.isEmpty() ? BCrypt.hashpw(password, BCrypt.gensalt()) : null;

            if (!isEdit) {
                // Insert new user
                String insertSQL = "INSERT INTO USERS (username, full_name, email, role, contact_number, password) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connectDB.prepareStatement(insertSQL)) {

                    ps.setString(1, username);
                    ps.setString(2, fullName);
                    ps.setString(3, email);
                    ps.setString(4, role);
                    ps.setString(5, mobile);
                    ps.setString(6, hashedPassword); // Insert hashed password
                    ps.executeUpdate();
                }
                System.out.println("✅ User added successfully!");
            } else {
                // Update existing user
                String updateSQL = "UPDATE USERS SET full_name = ?, email = ?, role = ?, contact_number = ?" +
                        (hashedPassword != null ? ", password = ?" : "") +
                        " WHERE username = ?";
                try (PreparedStatement ps = connectDB.prepareStatement(updateSQL)) {
                    ps.setString(1, fullName);
                    ps.setString(2, email);
                    ps.setString(3, role);
                    ps.setString(4, mobile);
                    if (hashedPassword != null) {
                        ps.setString(5, hashedPassword); // Update password if provided
                        ps.setString(6, username);
                    } else {
                        ps.setString(5, username);
                    }
                    ps.executeUpdate();
                }
                System.out.println("✅ User updated successfully!");
            }
        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addOrEditCourse(String courseCode, String courseTitle, String lectureId, int courseCredit, String courseType, int courseCreditHours, boolean isEdit) {
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            if (!isEdit) {
                // Check if the course code already exists
                String checkSQL = "SELECT COUNT(*) FROM COURSE WHERE course_code = ?";
                try (PreparedStatement checkStmt = connectDB.prepareStatement(checkSQL)) {
                    checkStmt.setString(1, courseCode);
                    ResultSet resultSet = checkStmt.executeQuery();
                    if (resultSet.next() && resultSet.getInt(1) > 0) {
                        throw new SQLException("Course code already exists.");
                    }
                }

                // Insert new course
                String insertSQL = "INSERT INTO COURSE (course_code, course_title, lecturer_id, course_credit, course_type, credit_hours) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connectDB.prepareStatement(insertSQL)) {
                    ps.setString(1, courseCode);
                    ps.setString(2, courseTitle);
                    ps.setString(3, lectureId);
                    ps.setInt(4, courseCredit);
                    ps.setString(5, courseType);
                    ps.setInt(6, courseCreditHours);
                    ps.executeUpdate();
                }
                System.out.println("✅ Course added successfully!");
            } else {
                // Update existing course
                String updateSQL = "UPDATE COURSE SET course_title = ?, lecturer_id = ?, course_credit = ?, course_type = ?, credit_hours = ? WHERE course_code = ?";
                try (PreparedStatement ps = connectDB.prepareStatement(updateSQL)) {
                    ps.setString(1, courseTitle);
                    ps.setString(2, lectureId);
                    ps.setInt(3, courseCredit);
                    ps.setString(4, courseType);
                    ps.setInt(5, courseCreditHours);
                    ps.setString(6, courseCode);
                    ps.executeUpdate();
                }
                System.out.println("✅ Course updated successfully!");
            }
        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void updateProfile() {
        // Implementation for updating the admin's profile
        System.out.println("Admin profile updated.");
    }
}