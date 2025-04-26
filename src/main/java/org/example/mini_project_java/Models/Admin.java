package org.example.mini_project_java.Models;

import org.example.mini_project_java.Database.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public  class Admin extends Users {

    public Admin(String username, String password, String name, String email, String role, String mobile_no, String profilePicture) {
        super(username, password, name, email, role, mobile_no, profilePicture);
    }

    public Admin(String username, String password, String name, String email, String role, String mobile_no) {
        super(username, password, name, email, role, mobile_no);
    }





    public Admin getAdminDetails(String username) {
        String query = "SELECT * FROM USERS WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("full_name");
                String email = rs.getString("email");
                String role = rs.getString("role");
                String mobileNo = rs.getString("contact_number");
                String password = rs.getString("password");
                String profilePic = rs.getString("profile_picture");

                if ("Admin".equalsIgnoreCase(role)) {
                    return new Admin(username, password, name, email, role, mobileNo, profilePic);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching admin details: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public void updateProfile() {
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            String hashedPassword = (getPassword() != null && !getPassword().isEmpty())
                    ? BCrypt.hashpw(getPassword(), BCrypt.gensalt())
                    : null;

            String updateSQL = "UPDATE USERS SET full_name = ?, email = ?, role = ?, contact_number = ?, profile_picture = ?" +
                    (hashedPassword != null ? ", password = ?" : "") +
                    " WHERE username = ?";
            try (PreparedStatement ps = connectDB.prepareStatement(updateSQL)) {
                ps.setString(1, getName());
                ps.setString(2, getEmail());
                ps.setString(3, getRole());
                ps.setString(4, getMobileNo());
                ps.setString(5, getProfilePicture());
                if (hashedPassword != null) {
                    ps.setString(6, hashedPassword);
                    ps.setString(7, getUsername());
                } else {
                    ps.setString(6, getUsername());
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Admin() {
        // Default constructor
    }

    public void createUserProfiles(String username, String fullName, String email, String role, String mobile, String password, boolean isEdit) {
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            String hashedPassword = password != null && !password.isEmpty() ? BCrypt.hashpw(password, BCrypt.gensalt()) : null;

            if (!isEdit) {
                String insertSQL = "INSERT INTO USERS (username, full_name, email, role, contact_number, password) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connectDB.prepareStatement(insertSQL)) {
                    ps.setString(1, username);
                    ps.setString(2, fullName);
                    ps.setString(3, email);
                    ps.setString(4, role);
                    ps.setString(5, mobile);
                    ps.setString(6, hashedPassword);
                    ps.executeUpdate();
                }
            } else {
                String updateSQL = "UPDATE USERS SET full_name = ?, email = ?, role = ?, contact_number = ?" +
                        (hashedPassword != null ? ", password = ?" : "") +
                        " WHERE username = ?";
                try (PreparedStatement ps = connectDB.prepareStatement(updateSQL)) {
                    ps.setString(1, fullName);
                    ps.setString(2, email);
                    ps.setString(3, role);
                    ps.setString(4, mobile);
                    if (hashedPassword != null) {
                        ps.setString(5, hashedPassword);
                        ps.setString(6, username);
                    } else {
                        ps.setString(5, username);
                    }
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addOrEditCourse(String courseCode, String courseTitle, String lectureId, int courseCredit, String courseType, int courseCreditHours, boolean isEdit) throws SQLException {
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            if (!isEdit) {
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
            } else {
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
            }
        }
    }

    public boolean courseExists(String courseCode) throws SQLException {
        try (Connection connectDB = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = connectDB.prepareStatement("SELECT 1 FROM COURSE WHERE course_code = ?")) {
            checkStmt.setString(1, courseCode);
            return checkStmt.executeQuery().next();
        }
    }

    public void deleteCourse(String courseCode) throws SQLException {
        try (Connection connectDB = DatabaseConnection.getConnection();
             PreparedStatement deleteStmt = connectDB.prepareStatement("DELETE FROM COURSE WHERE course_code = ?")) {
            deleteStmt.setString(1, courseCode);
            deleteStmt.executeUpdate();
        }
    }
}