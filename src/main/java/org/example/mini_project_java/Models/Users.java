package org.example.mini_project_java.Models;

import org.example.mini_project_java.Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Users {
    private String username;
    private String password;
    private String name;
    private String email;
    private String role;
    private String mobile_no;
    private String profilePicture;

    public Users() {}

    public Users(String username, String password, String name, String email, String role, String mobile_no, String profilePicture) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.role = role;
        this.mobile_no = mobile_no;
        this.profilePicture = profilePicture;
    }

    public Users(String username, String password, String name, String email, String role, String mobile_no) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.role = role;
        this.mobile_no = mobile_no;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMobileNo() {
        return mobile_no;
    }

    public void setMobileNo(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public static Users login(String inputUsername, String inputPassword) {
        String sql = "SELECT * FROM USERS WHERE username = ?";

        try (Connection connectDB = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement(sql)) {

            preparedStatement.setString(1, inputUsername);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");
                String role = resultSet.getString("role");

                if (storedPassword.equals(inputPassword)) {
                    switch (role.toLowerCase()) {
                        case "admin":
                            return new Admin(
                                    inputUsername,
                                    storedPassword,
                                    resultSet.getString("full_name"),
                                    resultSet.getString("email"),
                                    role,
                                    resultSet.getString("contact_number"),
                                    resultSet.getString("profile_picture")

                            );
                        case "student":
                            return new Undergratuate(
                                    inputUsername,
                                    storedPassword,
                                    resultSet.getString("full_name"),
                                    resultSet.getString("email"),
                                    role,
                                    resultSet.getString("contact_number"),
                                    resultSet.getString("profile_picture")
                            );
                        case "technical_officer":
                            return new Tecninical_Officer(
                                    inputUsername,
                                    storedPassword,
                                    resultSet.getString("full_name"),
                                    resultSet.getString("email"),
                                    role,
                                    resultSet.getString("contact_number"),
                                    resultSet.getString("profile_picture")
                            );
                        case "lecturer": // Corrected typo from "lecture" to "lecturer"
                            return new Lecture(
                                    inputUsername,
                                    storedPassword,
                                    resultSet.getString("full_name"),
                                    resultSet.getString("email"),
                                    role,
                                    resultSet.getString("contact_number"),
                                    resultSet.getString("profile_picture")
                            );
                        default:
                            System.out.println("⚠️ Unknown role: " + role);
                            return null;
                    }
                } else {
                    System.out.println("❌ Incorrect password.");
                }
            } else {
                System.out.println("❌ No user found with username: " + inputUsername);
            }

        } catch (SQLException e) {
            System.err.println("Database login error: " + e.getMessage());
        }
        return null;
    }

    public void logout() {
        System.out.println("User logged out.");
    }

    public abstract void updateProfile();
}