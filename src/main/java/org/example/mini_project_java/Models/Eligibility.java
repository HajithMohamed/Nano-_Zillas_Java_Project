package org.example.mini_project_java.Models;

import org.example.mini_project_java.Database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Eligibility {
    private static final Logger logger = LoggerFactory.getLogger(Eligibility.class);

    private String student_id;
    private String course_code;
    private double caFinal;
    private double attendancePercentage;
    private String eligibilityStatus;

    public Eligibility(String student_id, String course_code, double caFinal, double attendancePercentage, String eligibilityStatus) {
        this.student_id = student_id;
        this.course_code = course_code;
        this.caFinal = caFinal;
        this.attendancePercentage = attendancePercentage;
        this.eligibilityStatus = eligibilityStatus;
    }

    // Getters and setters
    public String getStudent_id() { return student_id; }
    public void setStudent_id(String student_id) { this.student_id = student_id; }
    public String getCourse_code() { return course_code; }
    public void setCourse_code(String course_code) { this.course_code = course_code; }
    public double getCaFinal() { return caFinal; }
    public void setCaFinal(double caFinal) { this.caFinal = caFinal; }
    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
    public String getEligibilityStatus() { return eligibilityStatus; }
    public void setEligibilityStatus(String eligibilityStatus) { this.eligibilityStatus = eligibilityStatus; }

    private Optional<Double> getCaFinal(String stu_id, String course_code) {
        validateInputs(stu_id, course_code);
        logger.debug("Fetching CA final for student {} and course {}", stu_id, course_code);
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                logger.error("Database connection is null");
                return Optional.empty();
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT caTotal FROM Student_Grades WHERE stu_id = ? AND course_code = ?")) {
                stmt.setString(1, stu_id);
                stmt.setString(2, course_code);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        double caTotal = rs.getDouble("caTotal");
                        logger.debug("CA final found: {}", caTotal);
                        return Optional.of(caTotal);
                    } else {
                        logger.warn("No CA final found for student {} and course {}", stu_id, course_code);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching CA final for student {} and course {}", stu_id, course_code, e);
        }
        return Optional.empty();
    }

    private Optional<Double> getAttendancePercentage(String stu_id, String course_code) {
        validateInputs(stu_id, course_code);
        logger.debug("Fetching attendance for student {} and course {}", stu_id, course_code);
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                logger.error("Database connection is null");
                return Optional.empty();
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT week1, week2, week3, week4, week5, week6, week7, week8, week9, week10, " +
                            "week11, week12, week13, week14, week15 FROM attendance " +
                            "WHERE student_id = ? AND course_code = ?")) {
                stmt.setString(1, stu_id);
                stmt.setString(2, course_code);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int presentCount = 0;
                        for (int i = 1; i <= 15; i++) {
                            if (rs.getBoolean("week" + i)) {
                                presentCount++;
                            }
                        }
                        double percentage = (presentCount / 15.0) * 100;
                        logger.debug("Attendance percentage: {} ({} present weeks)", percentage, presentCount);
                        return Optional.of(percentage);
                    } else {
                        logger.warn("No attendance record found for student {} and course {}", stu_id, course_code);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching attendance for student {} and course {}", stu_id, course_code, e);
        }
        return Optional.empty();
    }

    public boolean calculateAndStoreEligibility(String stu_id, String course_code) {
        logger.debug("Starting calculateAndStoreEligibility for student {} and course {}", stu_id, course_code);
        validateInputs(stu_id, course_code);
        Optional<Double> caFinalOpt = getCaFinal(stu_id, course_code);
        Optional<Double> attendanceOpt = getAttendancePercentage(stu_id, course_code);

        if (caFinalOpt.isEmpty() || attendanceOpt.isEmpty()) {
            logger.warn("Incomplete data for eligibility calculation: CA={}, Attendance={}", caFinalOpt, attendanceOpt);
            return false;
        }

        double caFinal = caFinalOpt.get();
        double attendancePercentage = attendanceOpt.get();
        String eligibilityStatus;
        if (attendancePercentage >= 80 && caFinal >= 50) {
            eligibilityStatus = "Eligible";
        } else if (attendancePercentage < 80 && caFinal < 50) {
            eligibilityStatus = "Not eligible (CA & Attendance)";
        } else if (attendancePercentage < 80) {
            eligibilityStatus = "Not eligible (Attendance)";
        } else {
            eligibilityStatus = "Not eligible (CA)";
        }
        logger.debug("Calculated eligibility: status={}, CA={}, attendance={}", eligibilityStatus, caFinal, attendancePercentage);

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                logger.error("Database connection is null");
                return false;
            }
            boolean recordExists;
            try (PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT 1 FROM Eligibility WHERE student_id = ? AND course_code = ?")) {
                checkStmt.setString(1, stu_id);
                checkStmt.setString(2, course_code);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    recordExists = rs.next();
                }
            }
            logger.debug("Record exists: {}", recordExists);

            int rowsAffected;
            if (recordExists) {
                logger.debug("Updating Eligibility for student {} and course {}", stu_id, course_code);
                try (PreparedStatement updateStmt = conn.prepareStatement(
                        "UPDATE Eligibility SET caFinal = ?, attendancePercentage = ?, eligibilityStatus = ? " +
                                "WHERE student_id = ? AND course_code = ?")) {
                    updateStmt.setDouble(1, caFinal);
                    updateStmt.setDouble(2, attendancePercentage);
                    updateStmt.setString(3, eligibilityStatus);
                    updateStmt.setString(4, stu_id);
                    updateStmt.setString(5, course_code);
                    rowsAffected = updateStmt.executeUpdate();
                    logger.debug("Update executed, rows affected: {}", rowsAffected);
                }
            } else {
                logger.debug("Inserting into Eligibility for student {} and course {}", stu_id, course_code);
                try (PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO Eligibility (student_id, course_code, caFinal, attendancePercentage, eligibilityStatus) " +
                                "VALUES (?, ?, ?, ?, ?)")) {
                    insertStmt.setString(1, stu_id);
                    insertStmt.setString(2, course_code);
                    insertStmt.setDouble(3, caFinal);
                    insertStmt.setDouble(4, attendancePercentage);
                    insertStmt.setString(5, eligibilityStatus);
                    rowsAffected = insertStmt.executeUpdate();
                    logger.debug("Insert executed, rows affected: {}", rowsAffected);
                }
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.error("Error storing eligibility for student {} and course {}", stu_id, course_code, e);
            return false;
        }
    }

    public static Optional<Eligibility> getEligibility(String stu_id, String course_code) {
        validateInputs(stu_id, course_code);
        logger.debug("Fetching eligibility for student {} and course {}", stu_id, course_code);
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                logger.error("Database connection is null");
                return Optional.empty();
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM Eligibility WHERE student_id = ? AND course_code = ?")) {
                stmt.setString(1, stu_id);
                stmt.setString(2, course_code);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Eligibility eligibility = new Eligibility(
                                rs.getString("student_id"),
                                rs.getString("course_code"),
                                rs.getDouble("caFinal"),
                                rs.getDouble("attendancePercentage"),
                                rs.getString("eligibilityStatus")
                        );
                        logger.debug("Eligibility found: {}", eligibility);
                        return Optional.of(eligibility);
                    } else {
                        logger.debug("No eligibility found for student {} and course {}", stu_id, course_code);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching eligibility for student {} and course {}", stu_id, course_code, e);
        }
        return Optional.empty();
    }

    public static List<Eligibility> getEligibilityForStudent(String stu_id) {
        validateInput(stu_id, "Student ID");
        logger.debug("Fetching all eligibility records for student {}", stu_id);
        List<Eligibility> eligibilityList = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                logger.error("Database connection is null");
                return eligibilityList;
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM Eligibility WHERE student_id = ?")) {
                stmt.setString(1, stu_id);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        eligibilityList.add(new Eligibility(
                                rs.getString("student_id"),
                                rs.getString("course_code"),
                                rs.getDouble("caFinal"),
                                rs.getDouble("attendancePercentage"),
                                rs.getString("eligibilityStatus")
                        ));
                    }
                }
            }
            logger.debug("Found {} eligibility records for student {}", eligibilityList.size(), stu_id);
        } catch (SQLException e) {
            logger.error("Error fetching eligibility for student {}", stu_id, e);
        }
        return eligibilityList;
    }

    public static boolean calculateEligibilityForCourse(String course_code) {
        validateInput(course_code, "Course code");
        logger.debug("Calculating eligibility for course {}", course_code);
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                logger.error("Database connection is null");
                return false;
            }
            List<String> studentIds = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT DISTINCT stu_id FROM Student_Grades WHERE course_code = ?")) {
                stmt.setString(1, course_code);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        studentIds.add(rs.getString("stu_id"));
                    }
                }
            }
            logger.debug("Found {} students for course {}", studentIds.size(), course_code);

            boolean success = true;
            for (String studentId : studentIds) {
                logger.debug("Processing student {} for course {}", studentId, course_code);
                boolean result = new Eligibility(studentId, course_code, 0.0, 0.0, "")
                        .calculateAndStoreEligibility(studentId, course_code);
                if (!result) {
                    logger.warn("Failed to update eligibility for student {} and course {}", studentId, course_code);
                    success = false;
                }
            }
            return success;
        } catch (SQLException e) {
            logger.error("Error calculating eligibility for course {}", course_code, e);
            return false;
        }
    }

    private static void validateInputs(String stu_id, String course_code) {
        validateInput(stu_id, "Student ID");
        validateInput(course_code, "Course code");
    }

    private static void validateInput(String input, String fieldName) {
        if (input == null || input.trim().isEmpty()) {
            logger.error("{} cannot be null or empty", fieldName);
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }

    @Override
    public String toString() {
        return "Eligibility{" +
                "student_id='" + student_id + '\'' +
                ", course_code='" + course_code + '\'' +
                ", caFinal=" + caFinal +
                ", attendancePercentage=" + attendancePercentage +
                ", eligibilityStatus='" + eligibilityStatus + '\'' +
                '}';
    }
}