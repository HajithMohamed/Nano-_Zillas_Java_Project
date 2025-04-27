package org.example.mini_project_java.Models;

import org.example.mini_project_java.Database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Undergratuate extends Users {

    public Undergratuate(String username, String password, String name, String email, String role, String mobileNo) {
        super(username, password, name, email, role, mobileNo);
    }

    public Undergratuate(String username, String password, String name, String email, String role, String mobileNo, String profilePicture) {
        super(username, password, name, email, role, mobileNo, profilePicture);
    }

    // Method to view course details
    public List<Courses> viewCourseDetails() {
        List<Courses> courseList = new ArrayList<>();

        try (Connection connectDB = DatabaseConnection.getConnection();
             PreparedStatement statement = connectDB.prepareStatement("SELECT * FROM COURSE");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Courses course = new Courses(
                        resultSet.getString("course_code"),
                        resultSet.getString("course_title"),
                        resultSet.getString("lecturer_id"),
                        resultSet.getInt("course_credit"),
                        resultSet.getString("course_type"),
                        resultSet.getInt("credit_hours")
                );
                courseList.add(course);
            }

        } catch (SQLException e) {
            System.out.printf("SQLException: %s%n", e);
        }
        return courseList;
    }

    @Override
    public void updateProfile() {
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            String updateSQL = "UPDATE USERS SET contact_number = ?, profile_picture = ? WHERE username = ?";
            try (PreparedStatement ps = connectDB.prepareStatement(updateSQL)) {
                ps.setString(1, getMobileNo());
                ps.setString(2, getProfilePicture());
                ps.setString(3, getUsername());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
