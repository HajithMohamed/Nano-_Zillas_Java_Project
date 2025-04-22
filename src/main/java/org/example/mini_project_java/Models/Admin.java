package org.example.mini_project_java.Models;

import org.example.mini_project_java.Database.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    @Override
    public void updateProfile() {
        // Implementation for updating the admin's profile
        System.out.println("Admin profile updated.");
    }
}