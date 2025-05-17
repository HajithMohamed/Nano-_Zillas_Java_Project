package org.example.mini_project_java.Models;

import org.example.mini_project_java.Database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Lecture extends Users {
    public Lecture(String username, String password, String name, String email, String role, String mobileNo) {
        super(username, password, name, email, role, mobileNo);
    }

    public Lecture(String username, String password, String name, String email, String role, String mobileNo, String profilePicture) {
        super(username, password, name, email, role, mobileNo, profilePicture);
    }

    // New method to fetch lecturer details from the database
    public static Lecture fetchLecturerByUsername(String username) {
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            String query = "SELECT full_name, email, contact_number, profile_picture FROM USERS WHERE username = ? AND role = 'LECTURER'";
            try (PreparedStatement ps = connectDB.prepareStatement(query)) {
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String name = rs.getString("full_name");
                    String email = rs.getString("email");
                    String mobileNo = rs.getString("contact_number");
                    String profilePicture = rs.getString("profile_picture");
                    return new Lecture(username, "", name, email, "LECTURER", mobileNo, profilePicture);
                } else {
                    throw new RuntimeException("Lecturer not found for username: " + username);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch lecturer details: " + e.getMessage());
        }
    }

    @Override
    public void updateProfile() {
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            String updateSQL = "UPDATE USERS SET full_name = ?, email = ?, contact_number = ?, profile_picture = ? WHERE username = ?";
            try (PreparedStatement ps = connectDB.prepareStatement(updateSQL)) {
                ps.setString(1, getName());
                ps.setString(2, getEmail());
                ps.setString(3, getMobileNo());
                ps.setString(4, getProfilePicture() != null ? getProfilePicture() : null);
                ps.setString(5, getUsername());
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new RuntimeException("No profile found for username: " + getUsername());
                }
                System.out.println("Lecturer profile updated successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update lecturer profile: " + e.getMessage());
        }
    }
}