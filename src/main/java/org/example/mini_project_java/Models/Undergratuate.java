package org.example.mini_project_java.Models;
import org.example.mini_project_java.Database.DatabaseConnection;

import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


    public class Undergratuate extends Users {

        public Undergratuate(String username, String password, String name, String email, String role, String mobileNo) {
            super(username, password, name, email, role, mobileNo);
        }

        public Undergratuate(String username, String password, String name, String email, String role, String mobileNo, String profilePicture) {
            super(username, password, name, email, role, mobileNo, profilePicture);
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

