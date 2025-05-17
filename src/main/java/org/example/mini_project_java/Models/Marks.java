package org.example.mini_project_java.Models;

import org.example.mini_project_java.Controllers.Undergraduate.ViewGrade_controller;
import org.example.mini_project_java.Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Marks {
    private String stu_id;
    private String course_code;
    private double quiz01;
    private double quiz02;
    private double quiz03;
    private double quiz04;
    private double assessment01;
    private double assessment02;
    private double mid;
    private double finalTheory;
    private double finalPractical;
    private double caTotal;
    private double finalTotal;
    private double finalMarks;
    private String grade;
    private double gpa;
    private Courses course;

    public Marks(String stu_id, String course_code, double quiz01, double quiz02, double quiz03, double quiz04, double assessment01, double assessment02, double mid, double finalTheory, double finalPractical, double caTotal, double finalTotal, double finalMarks, String grade, double gpa) {
        this.stu_id = stu_id;
        this.course_code = course_code;
        this.quiz01 = quiz01;
        this.quiz02 = quiz02;
        this.quiz03 = quiz03;
        this.quiz04 = quiz04;
        this.assessment01 = assessment01;
        this.assessment02 = assessment02;
        this.mid = mid;
        this.finalTheory = finalTheory;
        this.finalPractical = finalPractical;
        this.caTotal = caTotal;
        this.finalTotal = finalTotal;
        this.finalMarks = finalMarks;
        this.grade = grade;
        this.gpa = gpa;
    }

    public String getStu_id() {
        return stu_id;
    }

    public void setStu_id(String stu_id) {
        this.stu_id = stu_id;
    }

    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }

    public double getQuiz01() {
        return quiz01;
    }

    public void setQuiz01(double quiz01) {
        this.quiz01 = quiz01;
    }

    public double getQuiz02() {
        return quiz02;
    }

    public void setQuiz02(double quiz02) {
        this.quiz02 = quiz02;
    }

    public double getQuiz03() {
        return quiz03;
    }

    public void setQuiz03(double quiz03) {
        this.quiz03 = quiz03;
    }

    public double getQuiz04() {
        return quiz04;
    }

    public void setQuiz04(double quiz04) {
        this.quiz04 = quiz04;
    }

    public double getAssessment01() {
        return assessment01;
    }

    public void setAssessment01(double assessment01) {
        this.assessment01 = assessment01;
    }

    public double getAssessment02() {
        return assessment02;
    }

    public void setAssessment02(double assessment02) {
        this.assessment02 = assessment02;
    }

    public double getMid() {
        return mid;
    }

    public void setMid(double mid) {
        this.mid = mid;
    }

    public double getFinalTheory() {
        return finalTheory;
    }

    public void setFinalTheory(double finalTheory) {
        this.finalTheory = finalTheory;
    }

    public double getFinalPractical() {
        return finalPractical;
    }

    public void setFinalPractical(double finalPractical) {
        this.finalPractical = finalPractical;
    }

    public double getCaTotal() {
        return caTotal;
    }

    public void setCaTotal(double caTotal) {
        this.caTotal = caTotal;
    }

    public double getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(double finalTotal) {
        this.finalTotal = finalTotal;
    }

    public double getFinalMarks() {
        return finalMarks;
    }

    public void setFinalMarks(double finalMarks) {
        this.finalMarks = finalMarks;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public Courses getCourse() {
        return course;
    }

    public void setCourse(Courses course) {
        this.course = course;
    }

    public List<Marks> calculateCaTotal(String userName) throws SQLException {
        List<Marks> marksRecords = new ArrayList<>();
        try (Connection dbConnection = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = dbConnection.prepareStatement(
                    "SELECT stu_id, course_code, quiz01, quiz02, quiz03, quiz04, assessment01, assessment02, mid FROM Student_Grades WHERE stu_id = ?")) {
                stmt.setString(1, userName);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String studentId = rs.getString("stu_id");
                        String courseCode = rs.getString("course_code");
                        double quiz01 = rs.getDouble("quiz01");
                        double quiz02 = rs.getDouble("quiz02");
                        double quiz03 = rs.getDouble("quiz03");
                        double quiz04 = rs.getDouble("quiz04");
                        double assessment01 = rs.getDouble("assessment01");
                        double assessment02 = rs.getDouble("assessment02");
                        double mid = rs.getDouble("mid");
                        double caFinal = 0.0;

                        switch (courseCode) {
                            case "ICT2113":
                                double[] marks = new double[]{quiz01, quiz02, quiz03};
                                Arrays.sort(marks);
                                caFinal = (((marks[1] + marks[2]) * 10) / 100) + ((mid * 20) / 100);
                                break;
                            case "ICT2122":
                                double[] marks1 = new double[]{quiz01, quiz02, quiz03, quiz04};
                                Arrays.sort(marks1);
                                caFinal = (((marks1[1] + marks1[2] + marks1[3]) * 10) / 100) + ((assessment01 * 10) / 100) + ((mid * 20) / 100);
                                break;
                            case "ICT2133":
                                double[] marks2 = new double[]{quiz01, quiz02, quiz03, quiz04};
                                Arrays.sort(marks2);
                                caFinal = (((marks2[2] + marks2[3]) * 10) / 100) + (((assessment01 + assessment02) * 20) / 100);
                                break;
                            case "ICT2142":
                                caFinal = ((assessment01 * 20) / 100) + ((mid * 20) / 100);
                                break;
                            case "ICT2152":
                                double[] marks4 = new double[]{quiz01, quiz02, quiz03};
                                Arrays.sort(marks4);
                                caFinal = (((marks4[1] + marks4[2]) * 10) / 100) + (((assessment01 + assessment02) * 20) / 100);
                                break;
                        }

                        // Update caTotal in the database
                        updateGradeTotals(studentId, courseCode, caFinal, 0.0, 0.0, 0.0);

                        Marks mark = new Marks(studentId, courseCode, quiz01, quiz02, quiz03, quiz04, assessment01, assessment02, mid, 0.0, 0.0, caFinal, 0.0, 0.0, null, 0.0);
                        marksRecords.add(mark);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in calculateCaTotal for user " + userName + ": " + e.getMessage());
            throw e;
        }
        return marksRecords;
    }

    public List<Marks> calculateFinalTotal(String userName) throws SQLException {
        List<Marks> marksRecords = new ArrayList<>();
        try (Connection dbConnection = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = dbConnection.prepareStatement(
                    "SELECT stu_id, course_code, finalPractical, finalTheory, caTotal FROM Student_Grades WHERE stu_id = ?")) {
                stmt.setString(1, userName);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String studentId = rs.getString("stu_id");
                        String courseCode = rs.getString("course_code");
                        double finalPractical = rs.getDouble("finalPractical");
                        double finalTheory = rs.getDouble("finalTheory");
                        double caTotal = rs.getDouble("caTotal");
                        double finalTot = 0.0;

                        switch (courseCode) {
                            case "ICT2113":
                                finalTot = ((finalTheory * 40) / 100) + ((finalPractical * 30) / 100);
                                break;
                            case "ICT2122":
                                finalTot = ((finalTheory * 60) / 100);
                                break;
                            case "ICT2133":
                                finalTot = ((finalTheory * 40) / 100) + ((finalPractical * 30) / 100);
                                break;
                            case "ICT2142":
                                finalTot = ((finalPractical * 60) / 100);
                                break;
                            case "ICT2152":
                                finalTot = ((finalTheory * 70) / 100);
                                break;
                        }

                        // Update finalTotal in the database
                        updateGradeTotals(studentId, courseCode, caTotal, finalTot, 0.0, 0.0);

                        Marks mark = new Marks(studentId, courseCode, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, finalTheory, finalPractical, caTotal, finalTot, 0.0, null, 0.0);
                        marksRecords.add(mark);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in calculateFinalTotal for user " + userName + ": " + e.getMessage());
            throw e;
        }
        return marksRecords;
    }

    public List<Marks> finalMarksGradeCalculation(String userName) throws SQLException {
        List<Marks> marksRecords = new ArrayList<>();
        try (Connection dbConnection = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = dbConnection.prepareStatement(
                    "SELECT stu_id, course_code, finalTotal, caTotal FROM Student_Grades WHERE stu_id = ?")) {
                stmt.setString(1, userName);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String studentId = rs.getString("stu_id");
                        String courseCode = rs.getString("course_code");
                        double finalCa = rs.getDouble("caTotal");
                        double finalTot = rs.getDouble("finalTotal");

                        double finalMarks = finalCa + finalTot;
                        String grade;
                        double gpa;
                        if (finalMarks >= 85) {
                            grade = "A+";
                            gpa = 4.0;
                        } else if (finalMarks >= 70) {
                            grade = "A";
                            gpa = 4.0;
                        } else if (finalMarks >= 65) {
                            grade = "A-";
                            gpa = 3.7;
                        } else if (finalMarks >= 60) {
                            grade = "B+";
                            gpa = 3.3;
                        } else if (finalMarks >= 55) {
                            grade = "B";
                            gpa = 3.0;
                        } else if (finalMarks >= 50) {
                            grade = "B-";
                            gpa = 2.7;
                        } else if (finalMarks >= 45) {
                            grade = "C+";
                            gpa = 2.3;
                        } else if (finalMarks >= 40) {
                            grade = "C";
                            gpa = 2.0;
                        } else if (finalMarks >= 35) {
                            grade = "C-";
                            gpa = 1.7;
                        } else if (finalMarks >= 30) {
                            grade = "D+";
                            gpa = 1.3;
                        } else if (finalMarks >= 25) {
                            grade = "D";
                            gpa = 1.0;
                        } else {
                            grade = "E";
                            gpa = 0.0;
                        }

                        // Update finalMarks, grade, and gpa in the database
                        updateGradeTotalsAndGrade(studentId, courseCode, finalCa, finalTot, finalMarks, grade, gpa);

                        Marks mark = new Marks(studentId, courseCode, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, finalCa, finalTot, finalMarks, grade, gpa);
                        marksRecords.add(mark);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in finalMarksGradeCalculation for user " + userName + ": " + e.getMessage());
            throw e;
        }
        return marksRecords;
    }

    private void storeFinalGPA(String stu_id, double finalGPA) throws SQLException {
        try (Connection dbConnection = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = dbConnection.prepareStatement(
                    "INSERT INTO Student_Final_GPA (stu_id, final_gpa) VALUES (?, ?) " +
                            "ON DUPLICATE KEY UPDATE final_gpa = ?, last_updated = CURRENT_TIMESTAMP")) {
                stmt.setString(1, stu_id);
                stmt.setDouble(2, finalGPA);
                stmt.setDouble(3, finalGPA);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error in storeFinalGPA for user " + stu_id + ": " + e.getMessage());
            throw e;
        }
    }

    public double calculateFinalGPA(String stu_id) throws SQLException {
        double totalGradePoints = 0.0;
        double totalCredits = 0.0;

        try (Connection dbConnection = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = dbConnection.prepareStatement(
                    "SELECT sg.stu_id, sg.course_code, sg.gpa, c.course_credit " +
                            "FROM Student_Grades sg " +
                            "JOIN Course c ON sg.course_code = c.course_code " +
                            "WHERE sg.stu_id = ?")) {
                stmt.setString(1, stu_id);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        double eachGPA = rs.getDouble("gpa");
                        double courseCredit = rs.getDouble("course_credit");
                        totalGradePoints += eachGPA * courseCredit;
                        totalCredits += courseCredit;
                    }
                }
            }
            double finalGPA = totalCredits > 0 ? totalGradePoints / totalCredits : 0.0;
            storeFinalGPA(stu_id, finalGPA);
            return finalGPA;
        } catch (SQLException e) {
            System.err.println("Error in calculateFinalGPA for user " + stu_id + ": " + e.getMessage());
            throw new RuntimeException("Error calculating final GPA: " + e.getMessage(), e);
        }
    }

    public List<ViewGrade_controller.GradeRecord> getGradeDetails(String stu_id) throws SQLException {
        List<ViewGrade_controller.GradeRecord> records = new ArrayList<>();
        try (Connection dbConnection = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = dbConnection.prepareStatement(
                    "SELECT sg.course_code, c.course_title, sg.grade, sg.gpa " +
                            "FROM Student_Grades sg " +
                            "JOIN Course c ON sg.course_code = c.course_code " +
                            "WHERE sg.stu_id = ?")) {
                stmt.setString(1, stu_id);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String courseCode = rs.getString("course_code");
                        String courseName = rs.getString("course_title");
                        String grade = rs.getString("grade");
                        records.add(new ViewGrade_controller.GradeRecord(courseCode, courseName, grade));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in getGradeDetails for user " + stu_id + ": " + e.getMessage());
            throw e;
        }
        return records;
    }

    public double getFinalGPA(String stu_id) throws SQLException {
        try (Connection dbConnection = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = dbConnection.prepareStatement(
                    "SELECT final_gpa FROM Student_Final_GPA WHERE stu_id = ?")) {
                stmt.setString(1, stu_id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getDouble("final_gpa");
                    } else {
                        // No GPA found; calculate and store it
                        return calculateFinalGPA(stu_id);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in getFinalGPA for user " + stu_id + ": " + e.getMessage());
            throw e;
        }
    }

    public void updateGradeTotals(String stu_id, String course_code, double caTotal, double finalTotal, double finalMarks, double gpa) throws SQLException {
        try (Connection dbConnection = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = dbConnection.prepareStatement(
                    "UPDATE Student_Grades SET caTotal = ?, finalTotal = ?, finalMarks = ?, gpa = ? WHERE stu_id = ? AND course_code = ?")) {
                stmt.setDouble(1, caTotal);
                stmt.setDouble(2, finalTotal);
                stmt.setDouble(3, finalMarks);
                stmt.setDouble(4, gpa);
                stmt.setString(5, stu_id);
                stmt.setString(6, course_code);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    System.err.println("No records updated for stu_id: " + stu_id + ", course_code: " + course_code);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in updateGradeTotals for user " + stu_id + ", course " + course_code + ": " + e.getMessage());
            throw e;
        }
    }

    public void updateGradeTotalsAndGrade(String stu_id, String course_code, double caTotal, double finalTotal, double finalMarks, String grade, double gpa) throws SQLException {
        try (Connection dbConnection = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = dbConnection.prepareStatement(
                    "UPDATE Student_Grades SET caTotal = ?, finalTotal = ?, finalMarks = ?, grade = ?, gpa = ? WHERE stu_id = ? AND course_code = ?")) {
                stmt.setDouble(1, caTotal);
                stmt.setDouble(2, finalTotal);
                stmt.setDouble(3, finalMarks);
                stmt.setString(4, grade);
                stmt.setDouble(5, gpa);
                stmt.setString(6, stu_id);
                stmt.setString(7, course_code);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    System.err.println("No records updated for stu_id: " + stu_id + ", course_code: " + course_code);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in updateGradeTotalsAndGrade for user " + stu_id + ", course " + course_code + ": " + e.getMessage());
            throw e;
        }
    }
}