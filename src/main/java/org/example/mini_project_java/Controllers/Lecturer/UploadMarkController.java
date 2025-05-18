package org.example.mini_project_java.Controllers.Lecturer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.mini_project_java.Database.DatabaseConnection;
import org.example.mini_project_java.Models.Marks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UploadMarkController {

    @FXML
    private ComboBox<String> examTypeComboBox;

    @FXML
    private TextField studentIdField;

    @FXML
    private TextField marksField;

    @FXML
    private TableView<MarkRecord> marksTable;

    @FXML
    private TableColumn<MarkRecord, String> studentIdColumn;

    @FXML
    private TableColumn<MarkRecord, String> studentNameColumn;

    @FXML
    private TableColumn<MarkRecord, String> marksColumn;

    @FXML
    private TableColumn<MarkRecord, Double> marksColumn1;

    @FXML
    private Button uploadMarksButton;

    private String lecturerId; // Store the logged-in lecturer's ID
    private final ObservableList<MarkRecord> markRecords = FXCollections.observableArrayList();

    // Class to hold table data
    public static class MarkRecord {
        private final String studentId;
        private final String courseCode;
        private final String examType;
        private final double marks;

        public MarkRecord(String studentId, String courseCode, String examType, double marks) {
            this.studentId = studentId;
            this.courseCode = courseCode;
            this.examType = examType;
            this.marks = marks;
        }

        public String getStudentId() { return studentId; }
        public String getCourseCode() { return courseCode; }
        public String getExamType() { return examType; }
        public Double getMarks() { return marks; }
    }

    @FXML
    public void initialize() {
        // Assuming lecturerId is passed or retrieved (e.g., from a login session)
        lecturerId = "LEC/RUH/TEC/001"; // Replace with actual lecturer ID retrieval logic

        // Initialize table columns
        studentIdColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("studentId"));
        studentNameColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("courseCode"));
        marksColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("examType"));
        marksColumn1.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("marks"));
        marksTable.setItems(markRecords);

        // Populate exam type combo box
        populateExamTypeComboBox();

        // Refresh table with initial data
        refreshTable();

        // Set action for upload button
        uploadMarksButton.setOnAction(event -> handleUploadMarks());
    }

    private void populateExamTypeComboBox() {
        List<String> examTypes = List.of("Quiz01", "Quiz02", "Quiz03", "Quiz04", "Assessment01", "Assessment02", "Mid", "FinalTheory", "FinalPractical");
        examTypeComboBox.setItems(FXCollections.observableArrayList(examTypes));
    }

    private void handleUploadMarks() {
        String studentId = studentIdField.getText().trim();
        String examType = examTypeComboBox.getValue();
        String marksText = marksField.getText().trim();

        if (studentId.isEmpty() || examType == null || marksText.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        double marks;
        try {
            marks = Double.parseDouble(marksText);
            if (marks < 0 || marks > 100) {
                showAlert("Error", "Marks must be between 0 and 100.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid marks format. Please enter a number.");
            return;
        }

        // Fetch all courses for the lecturer to update marks
        List<String> courseCodes = getLecturerCourses();
        if (courseCodes.isEmpty()) {
            showAlert("Error", "No courses assigned to the lecturer.");
            return;
        }

        // Update marks for each course (assuming one student can have multiple courses)
        boolean updated = false;
        try {
            for (String courseCode : courseCodes) {
                if (updateStudentMarks(studentId, courseCode, examType, marks)) {
                    updated = true;
                    // Recalculate totals and grades
                    Marks marksModel = new Marks(null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, null, 0);
                    marksModel.calculateCaTotal(studentId);
                    marksModel.calculateFinalTotal(studentId);
                    marksModel.finalMarksGradeCalculation(studentId);
                }
            }
            if (updated) {
                refreshTable();
                showAlert("Success", "Marks uploaded successfully.");
            } else {
                showAlert("Error", "No records updated. Check student ID and course assignment.");
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to upload marks: " + e.getMessage());
        }
    }

    private List<String> getLecturerCourses() {
        List<String> courseCodes = new ArrayList<>();
        try (Connection dbConnection = DatabaseConnection.getConnection()) {
            String query = "SELECT course_code FROM Course WHERE lecturer_id = ?";
            try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
                stmt.setString(1, lecturerId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        courseCodes.add(rs.getString("course_code"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching courses: " + e.getMessage());
        }
        return courseCodes;
    }

    private boolean updateStudentMarks(String studentId, String courseCode, String examType, double marks) throws SQLException {
        try (Connection dbConnection = DatabaseConnection.getConnection()) {
            String columnName = examType.toLowerCase();
            String query = "UPDATE Student_Grades SET " + columnName + " = ? WHERE stu_id = ? AND course_code = ?";
            try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
                stmt.setDouble(1, marks);
                stmt.setString(2, studentId);
                stmt.setString(3, courseCode);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    // Insert a new record if the student-course combination doesn't exist
                    insertNewStudentGrade(studentId, courseCode, examType, marks);
                    return true; // Assume insertion is a successful update for this context
                }
                return rowsAffected > 0;
            }
        }
    }

    private void insertNewStudentGrade(String studentId, String courseCode, String examType, double marks) throws SQLException {
        try (Connection dbConnection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO Student_Grades (stu_id, course_code, " + examType.toLowerCase() + ") VALUES (?, ?, ?)";
            try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
                stmt.setString(1, studentId);
                stmt.setString(2, courseCode);
                stmt.setDouble(3, marks);
                stmt.executeUpdate();
            }
        }
    }

    private void refreshTable() {
        markRecords.clear();
        try (Connection dbConnection = DatabaseConnection.getConnection()) {
            String query = "SELECT stu_id, course_code, quiz01, quiz02, quiz03, quiz04, assessment01, assessment02, mid, finalTheory, finalPractical " +
                    "FROM Student_Grades WHERE course_code IN (SELECT course_code FROM Course WHERE lecturer_id = ?)";
            try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
                stmt.setString(1, lecturerId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String studentId = rs.getString("stu_id");
                        String courseCode = rs.getString("course_code");
                        addMarkRecord(studentId, courseCode, "Quiz01", rs.getDouble("quiz01"));
                        addMarkRecord(studentId, courseCode, "Quiz02", rs.getDouble("quiz02"));
                        addMarkRecord(studentId, courseCode, "Quiz03", rs.getDouble("quiz03"));
                        addMarkRecord(studentId, courseCode, "Quiz04", rs.getDouble("quiz04"));
                        addMarkRecord(studentId, courseCode, "Assessment01", rs.getDouble("assessment01"));
                        addMarkRecord(studentId, courseCode, "Assessment02", rs.getDouble("assessment02"));
                        addMarkRecord(studentId, courseCode, "Mid", rs.getDouble("mid"));
                        addMarkRecord(studentId, courseCode, "FinalTheory", rs.getDouble("finalTheory"));
                        addMarkRecord(studentId, courseCode, "FinalPractical", rs.getDouble("finalPractical"));
                    }
                }
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to refresh table: " + e.getMessage());
        }
    }

    private void addMarkRecord(String studentId, String courseCode, String examType, double marks) {
        if (marks > 0) { // Only add non-zero marks to the table
            markRecords.add(new MarkRecord(studentId, courseCode, examType, marks));
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}