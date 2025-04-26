package org.example.mini_project_java.Models;

import org.example.mini_project_java.Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Notice {
    private int notice_id;
    private String title;
    private String content;
    private String role;

    public Notice(int notice_id, String title, String content, String role) {
        this.notice_id = notice_id;
        this.title = title;
        this.content = content;
        this.role = role;
    }

    public int getNotice_id() {
        return notice_id;
    }

    public void setNotice_id(int notice_id) {
        this.notice_id = notice_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Create or Edit Notice
    public void createOrEditNotice(String title, String content, String role, boolean isEdit, int noticeId) {
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            if (!isEdit) {
                String insertSQL = "INSERT INTO NOTICE (title, content, role) VALUES (?, ?, ?)";
                try (PreparedStatement ps = connectDB.prepareStatement(insertSQL)) {
                    ps.setString(1, title);
                    ps.setString(2, content);
                    ps.setString(3, role);
                    ps.executeUpdate();
                }
            } else {
                String updateSQL = "UPDATE NOTICE SET title = ?, content = ?, role = ? WHERE notice_id = ?";
                try (PreparedStatement ps = connectDB.prepareStatement(updateSQL)) {
                    ps.setString(1, title);
                    ps.setString(2, content);
                    ps.setString(3, role);
                    ps.setInt(4, noticeId);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete Notice
    public void deleteNotice(int notice_id) {
        try (Connection connectDB = DatabaseConnection.getConnection();
             PreparedStatement deleteStmt = connectDB.prepareStatement("DELETE FROM NOTICE WHERE notice_id = ?")) {
            deleteStmt.setInt(1, notice_id);
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View Notice
    public Notice viewNotice(int notice_id) {
        String query = "SELECT * FROM NOTICE WHERE notice_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, notice_id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("notice_id");
                String title = rs.getString("title");
                String content = rs.getString("content");
                String role = rs.getString("role");

                return new Notice(id, title, content, role);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching notice details: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
