package org.example.mini_project_java.Models;

import org.example.mini_project_java.Database.DatabaseConnection;

import java.sql.*;

public class Tecninical_Officer extends Users {
    public Tecninical_Officer(String username, String password, String name, String email, String role, String mobileNo) {
        super(username, password, name, email, role,password);
    }

    public Tecninical_Officer(String username, String password, String name, String email, String role, String mobile_no, String profilePicture) {
        super(username, password, name, email, role, mobile_no, profilePicture);
    }

    @Override
    public void updateProfile() {
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            String updateSQL = "UPDATE USERS SET full_name = ?, email = ?, role = ?, contact_number = ?, profile_picture = ? WHERE username = ?";
            try (PreparedStatement ps = connectDB.prepareStatement(updateSQL)) {
                ps.setString(1, getName());
                ps.setString(2, getEmail());
                ps.setString(3, getRole());
                ps.setString(4, getMobileNo());
                ps.setString(5, getProfilePicture());
                ps.setString(6, getUsername()); // Username used only in WHERE clause, not changed
                ps.executeUpdate();
                System.out.println("Technical Officer profile updated successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to update Technical Officer profile.");
        }
    }

}
