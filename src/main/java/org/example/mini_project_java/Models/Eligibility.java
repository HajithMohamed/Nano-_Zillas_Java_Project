package org.example.mini_project_java.Models;

import org.example.mini_project_java.Database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Eligibility {
    private static final Logger logger = LoggerFactory.getLogger(Eligibility.class);

    private static final Map<String, Double> COURSE_CA_TOTALS = new HashMap<>();
    static {
        COURSE_CA_TOTALS.put("ICT2113", 30.0);
        COURSE_CA_TOTALS.put("ICT2122", 40.0);
        COURSE_CA_TOTALS.put("ICT2133", 30.0);
        COURSE_CA_TOTALS.put("ICT2142", 40.0);
        COURSE_CA_TOTALS.put("ICT2152", 30.0);
    }

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

    private Map<String, Double> getCaFinal(String course_code) {
        validateInput(course_code, "Course code");
        logger.debug("Fetching CA finals for all students in course {}", course_code);
        Map<String, Double> caTotals = new HashMap<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                logger.error("Database connection is null");
                return caTotals;
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT stu_id, caTotal FROM Student_Grades WHERE course_code = ?")) {
                stmt.setString(1, course_code);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String stu_id = rs.getString("stu_id");
                        double caTotal = rs.getDouble("caTotal");
                        double maxCaTotal = COURSE_CA_TOTALS.getOrDefault(course_code, 100.0);
                        if (caTotal >= 0) {
                            double adjustedCaTotal = Math.min(caTotal, maxCaTotal);
                            caTotals.put(stu_id, adjustedCaTotal);
                            logger.debug("CA final found for student {}: {} (adjusted to {})",
                                    stu_id, caTotal, adjustedCaTotal);
                            if (caTotal > maxCaTotal) {
                                logger.warn("CA total {} for student {} in course {} was capped at {}",
                                        caTotal, stu_id, course_code, maxCaTotal);
                            }
                        } else {
                            logger.error("Invalid CA total {} for student {} in course {}",
                                    caTotal, stu_id, course_code);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching CA finals for course {}", course_code, e);
        }
        if (caTotals.isEmpty()) {
            logger.warn("No CA finals found for course {}", course_code);
        }
        return caTotals;
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

    public static boolean calculateAndStoreEligibility(String stu_id, String course_code) {
        logger.debug("Starting calculateAndStoreEligibility for student {} and course {}", stu_id, course_code);
        validateInputs(stu_id, course_code);

        if (!COURSE_CA_TOTALS.containsKey(course_code)) {
            logger.error("Unknown course code: {}", course_code);
            throw new IllegalArgumentException("Unknown course code: " + course_code);
        }

        Map<String, Double> caTotals = new Eligibility(stu_id, course_code, 0.0, 0.0, "").getCaFinal(course_code);
        Optional<Double> caFinalOpt = Optional.ofNullable(caTotals.get(stu_id));
        Optional<Double> attendanceOpt = new Eligibility(stu_id, course_code, 0.0, 0.0, "").getAttendancePercentage(stu_id, course_code);

        if (caFinalOpt.isEmpty() || attendanceOpt.isEmpty()) {
            logger.warn("Incomplete data for eligibility calculation: CA={}, Attendance={} for student {} in course {}",
                    caFinalOpt, attendanceOpt, stu_id, course_code);
            return false;
        }

        double caFinal = caFinalOpt.get();
        double attendancePercentage = attendanceOpt.get();
        double maxCaTotal = COURSE_CA_TOTALS.get(course_code);
        double caThreshold = maxCaTotal * 0.5;

        String eligibilityStatus;
        if (attendancePercentage >= 80 && caFinal >= caThreshold) {
            eligibilityStatus = "Eligible";
        } else if (attendancePercentage < 80 && caFinal < caThreshold) {
            eligibilityStatus = "Not eligible (CA & Attendance)";
        } else if (attendancePercentage < 80) {
            eligibilityStatus = "Not eligible (Attendance)";
        } else {
            eligibilityStatus = "Not eligible (CA)";
        }
        logger.debug("Calculated eligibility: status={}, CA={} (threshold={}), attendance={} for student {} in course {}",
                eligibilityStatus, caFinal, caThreshold, attendancePercentage, stu_id, course_code);

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                logger.error("Database connection is null for student {} in course {}", stu_id, course_code);
                return false;
            }
            conn.setAutoCommit(false);
            boolean recordExists;
            try (PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT 1 FROM Eligibility WHERE student_id = ? AND course_code = ?")) {
                System.out.println("hAJITH");
                checkStmt.setString(1, stu_id);
                checkStmt.setString(2, course_code);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    recordExists = rs.next();
                }
            }

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
                    logger.debug("Update executed, rows affected: {} for student {} in course {}",
                            rowsAffected, stu_id, course_code);
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
                    logger.debug("Insert executed, rows affected: {} for student {} in course {}",
                            rowsAffected, stu_id, course_code);
                }
            }
            conn.commit();
            if (rowsAffected == 0) {
                logger.warn("No rows affected for student {} in course {}", stu_id, course_code);
                return false;
            }
            return true;
        } catch (SQLException e) {
            logger.error("Error storing eligibility for student {} and course {}: {}",
                    stu_id, course_code, e.getMessage(), e);
            try (Connection conn = DatabaseConnection.getConnection()) {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                logger.error("Error rolling back transaction for student {} in course {}",
                        stu_id, course_code, rollbackEx);
            }
            return false;
        }
    }

    public static boolean calculateEligibilityForCourse(String course_code) {
        validateInput(course_code, "Course code");
        logger.debug("Calculating eligibility for course {}", course_code);
        if (!COURSE_CA_TOTALS.containsKey(course_code)) {
            logger.error("Unknown course code: {}", course_code);
            throw new IllegalArgumentException("Unknown course code: " + course_code);
        }
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
                boolean result = calculateAndStoreEligibility(studentId, course_code);
                if (!result) {
                    logger.warn("Failed to update eligibility for student {} and course {}", studentId, course_code);
                    success = false;
                }
            }
            return success;
        } catch (SQLException e) {
            logger.error("Error calculating eligibility for course {}: {}", course_code, e.getMessage(), e);
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