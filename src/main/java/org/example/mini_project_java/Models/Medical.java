package org.example.mini_project_java.Models;

import org.example.mini_project_java.Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Medical {
    private int medicalId;
    private String courseCode;
    private String studentId;
    private String week;
    private String medicalReport;
    private String medicalNo;
    private String status;
    private java.sql.Date submissionDate;

    public Medical(int medicalId, String courseCode, String studentId, String week, String medicalReport, String medicalNo) {
        this.medicalId = medicalId;
        this.courseCode = courseCode;
        this.studentId = studentId;
        this.week = week;
        this.medicalReport = medicalReport;
        this.medicalNo = medicalNo;
        this.status = "pending";
    }

    public Medical(int medicalId, String courseCode, String studentId, String week, String medicalReport, String medicalNo, String status) {
        this.medicalId = medicalId;
        this.courseCode = courseCode;
        this.studentId = studentId;
        this.week = week;
        this.medicalReport = medicalReport;
        this.medicalNo = medicalNo;
        this.status = status;
    }

    public Medical(int medicalId, String courseCode, String studentId, String week, String medicalReport, String medicalNo, String status, java.sql.Date submissionDate) {
        this.medicalId = medicalId;
        this.courseCode = courseCode;
        this.studentId = studentId;
        this.week = week;
        this.medicalReport = medicalReport;
        this.medicalNo = medicalNo;
        this.status = status;
        this.submissionDate = submissionDate;
    }

    // Getters and Setters
    public int getMedicalId() { return medicalId; }
    public void setMedicalId(int medicalId) { this.medicalId = medicalId; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getWeek() { return week; }
    public void setWeek(String week) { this.week = week; }
    public String getMedicalReport() { return medicalReport; }
    public void setMedicalReport(String medicalReport) { this.medicalReport = medicalReport; }
    public String getMedicalNo() { return medicalNo; }
    public void setMedicalNo(String medicalNo) { this.medicalNo = medicalNo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public java.sql.Date getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(java.sql.Date submissionDate) { this.submissionDate = submissionDate; }

    public List<Medical> viewMedicalReport(String courseCode) {
        List<Medical> medicalReports = new ArrayList<>();
        String sql = "SELECT medicalId, courseCode, studentId, week, medicalReport, medicalNo, status, submissionDate " +
                "FROM MEDICAL_REPORTS WHERE courseCode = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    medicalReports.add(new Medical(
                            rs.getInt("medicalId"),
                            rs.getString("courseCode"),
                            rs.getString("studentId"),
                            rs.getString("week"),
                            rs.getString("medicalReport"),
                            rs.getString("medicalNo"),
                            rs.getString("status"),
                            rs.getDate("submissionDate")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving medical reports: " + e.getMessage());
            e.printStackTrace();
        }
        return medicalReports;
    }

    public List<Medical> viewMedicalReportByStatus(String courseCode, String status) {
        List<Medical> medicalReports = new ArrayList<>();
        String sql = "SELECT medicalId, courseCode, studentId, week, medicalReport, medicalNo, status, submissionDate " +
                "FROM MEDICAL_REPORTS WHERE courseCode = ? AND status = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            ps.setString(2, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    medicalReports.add(new Medical(
                            rs.getInt("medicalId"),
                            rs.getString("courseCode"),
                            rs.getString("studentId"),
                            rs.getString("week"),
                            rs.getString("medicalReport"),
                            rs.getString("medicalNo"),
                            rs.getString("status"),
                            rs.getDate("submissionDate")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving medical reports by status: " + e.getMessage());
            e.printStackTrace();
        }
        return medicalReports;
    }

    public boolean createMedicalReport() {
        String sql = "INSERT INTO MEDICAL_REPORTS (courseCode, studentId, week, medicalReport, medicalNo, status, submissionDate) " +
                "VALUES (?, ?, ?, ?, ?, 'pending', ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, this.courseCode);
            ps.setString(2, this.studentId);
            ps.setString(3, this.week);
            ps.setString(4, this.medicalReport);
            ps.setString(5, this.medicalNo);
            ps.setDate(6, this.submissionDate);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error creating medical report: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMedicalStatus(String newStatus) {
        if (!newStatus.matches("pending|accepted|rejected")) return false;
        String sql = "UPDATE MEDICAL_REPORTS SET status = ? WHERE medicalId = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, this.medicalId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                this.status = newStatus;
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error updating medical report status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}