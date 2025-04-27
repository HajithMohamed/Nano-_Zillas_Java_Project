package org.example.mini_project_java.Models;

import org.example.mini_project_java.Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Medical {
    private int medical_id;
    private String course_code;
    private String student_id;
    private String week;
    private String medicalReport;
    private String status; // Added status field

    public Medical(int medical_id, String course_code, String student_id, String week, String medicalReport) {
        this.medical_id = medical_id;
        this.course_code = course_code;
        this.student_id = student_id;
        this.week = week;
        this.medicalReport = medicalReport;
        this.status = "pending"; // Default status
    }

    public Medical(int medical_id, String course_code, String student_id, String week, String medicalReport, String status) {
        this.medical_id = medical_id;
        this.course_code = course_code;
        this.student_id = student_id;
        this.week = week;
        this.medicalReport = medicalReport;
        this.status = status;
    }

    // Existing getters and setters

    public int getMedical_id() {
        return medical_id;
    }

    public void setMedical_id(int medical_id) {
        this.medical_id = medical_id;
    }

    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getMedicalReport() {
        return medicalReport;
    }

    public void setMedicalReport(String medicalReport) {
        this.medicalReport = medicalReport;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Retrieves medical reports for a specific course from the database
     * @param course_code The course code to search for
     * @return List of Medical objects containing the medical reports
     */
    public List<Medical> viewMedicalReport(String course_code) {
        List<Medical> medicalReports = new ArrayList<>();

        // SQL query to get medical reports for the specified course code
        String sql = "SELECT medicalId, courseCode, studentId, week, medicalReport, status " +
                "FROM MEDICAL_REPORTS " +
                "WHERE courseCode = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, course_code);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int medicalId = resultSet.getInt("medicalId");
                    String courseCode = resultSet.getString("courseCode");
                    String studentId = resultSet.getString("studentId");
                    String week = resultSet.getString("week");
                    String medicalReportText = resultSet.getString("medicalReport");
                    String status = resultSet.getString("status");

                    Medical medical = new Medical(medicalId, courseCode, studentId, week, medicalReportText, status);
                    medicalReports.add(medical);
                }
            }

            return medicalReports;

        } catch (SQLException e) {
            System.err.println("Error retrieving medical reports: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // Return empty list on error
        }
    }

    /**
     * Retrieves medical reports with specific status for a course
     * @param course_code The course code to search for
     * @param status The status to filter by (pending, accepted, rejected)
     * @return List of Medical objects with the specified status
     */
    public List<Medical> viewMedicalReportByStatus(String course_code, String status) {
        List<Medical> medicalReports = new ArrayList<>();

        // SQL query to get medical reports for the specified course code and status
        String sql = "SELECT medicalId, courseCode, studentId, week, medicalReport, status " +
                "FROM MEDICAL_REPORTS " +
                "WHERE courseCode = ? AND status = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, course_code);
            preparedStatement.setString(2, status);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int medicalId = resultSet.getInt("medicalId");
                    String courseCode = resultSet.getString("courseCode");
                    String studentId = resultSet.getString("studentId");
                    String week = resultSet.getString("week");
                    String medicalReportText = resultSet.getString("medicalReport");
                    String reportStatus = resultSet.getString("status");

                    Medical medical = new Medical(medicalId, courseCode, studentId, week, medicalReportText, reportStatus);
                    medicalReports.add(medical);
                }
            }

            return medicalReports;

        } catch (SQLException e) {
            System.err.println("Error retrieving medical reports by status: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // Return empty list on error
        }
    }

    /**
     * Create a new medical report entry in the database
     * @return boolean indicating whether the operation was successful
     */
    public boolean createMedicalReport() {
        String sql = "INSERT INTO MEDICAL_REPORTS (courseCode, studentId, week, medicalReport, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, this.course_code);
            preparedStatement.setString(2, this.student_id);
            preparedStatement.setString(3, this.week);
            preparedStatement.setString(4, this.medicalReport);
            preparedStatement.setString(5, this.status);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error creating medical report: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update the status of a medical report
     * @param newStatus The new status (pending, accepted, rejected)
     * @return boolean indicating whether the update was successful
     */
    public boolean updateMedicalStatus(String newStatus) {
        if (!newStatus.equals("pending") && !newStatus.equals("accepted") && !newStatus.equals("rejected")) {
            System.err.println("Invalid status: " + newStatus);
            return false;
        }

        String sql = "UPDATE MEDICAL_REPORTS SET status = ? WHERE medicalId = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, newStatus);
            preparedStatement.setInt(2, this.medical_id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
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

//CREATE TABLE MEDICAL_REPORTS (
//        medicalId INT PRIMARY KEY AUTO_INCREMENT,
//        courseCode VARCHAR(20) NOT NULL,
//studentId VARCHAR(20) NOT NULL,
//week VARCHAR(10) NOT NULL,
//medicalReport VARCHAR(255) NOT NULL,
//status VARCHAR(10) DEFAULT 'pending' CHECK (status IN ('pending', 'accepted', 'rejected')),
//submissionDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
//FOREIGN KEY (courseCode) REFERENCES COURSES(courseCode),
//FOREIGN KEY (studentId) REFERENCES USERS(username)
//        );