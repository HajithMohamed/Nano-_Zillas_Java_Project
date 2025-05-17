package org.example.mini_project_java.Controllers.Undergraduate;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.example.mini_project_java.Models.Marks;

import java.sql.SQLException;
import java.util.List;

public class ViewGrade_controller {
    @FXML
    public AnchorPane studentGrade;
    @FXML
    public TableView<GradeRecord> gradeDetailsTable;
    @FXML
    public TableColumn<GradeRecord, String> courseCodeColumn;
    @FXML
    public TableColumn<GradeRecord, String> courseNameColumn;
    @FXML
    public TableColumn<GradeRecord, String> gpaColumn;
    @FXML
    public TextField cgpaField;

    private Marks marksModel = new Marks(null, null, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, null, 0.0);
    private String loggedInUsername;

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
        System.out.println("ViewGrade_controller received username: " + username);
    }

    private String getLoggedInUsername() {
        if (loggedInUsername == null) {
            System.err.println("No logged-in user found, defaulting to TG/2022/1414");
            return "TG/2022/1414";
        }
        return loggedInUsername;
    }

    @FXML
    public void initialize() {
        courseCodeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().courseCode()));
        courseNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().courseName()));
        gpaColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().grade()));

        String username = getLoggedInUsername();
        try {
            marksModel.calculateCaTotal(username);
            marksModel.calculateFinalTotal(username);
            List<Marks> gradeRecords = marksModel.finalMarksGradeCalculation(username);
            updateStudentGrades(gradeRecords);

            List<GradeRecord> records = marksModel.getGradeDetails(username);
            gradeDetailsTable.setItems(FXCollections.observableArrayList(records));

            double cgpa = marksModel.getFinalGPA(username);
            cgpaField.setText(String.format("%.2f", cgpa));
        } catch (SQLException e) {
            System.err.println("Error loading grades for user " + username + ": " + e.getMessage());
            showErrorAlert("Grades Load Error", "Grades view could not be loaded. Check database connection or data.");
        }
    }

    private void updateStudentGrades(List<Marks> records) throws SQLException {
        try (java.sql.Connection conn = org.example.mini_project_java.Database.DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE Student_Grades SET caTotal = ?, finalTotal = ?, finalMarks = ?, grade = ?, gpa = ? WHERE stu_id = ? AND course_code = ?")) {
            for (Marks record : records) {
                stmt.setDouble(1, record.getCaTotal());
                stmt.setDouble(2, record.getFinalTotal());
                stmt.setDouble(3, record.getFinalMarks());
                stmt.setString(4, record.getGrade());
                double gpa = switch (record.getGrade()) {
                    case "A+", "A" -> 4.00;
                    case "A-" -> 3.70;
                    case "B+" -> 3.30;
                    case "B" -> 3.00;
                    case "B-" -> 2.70;
                    case "C+" -> 2.30;
                    case "C" -> 2.00;
                    case "C-" -> 1.70;
                    case "D+" -> 1.30;
                    case "D" -> 1.00;
                    default -> 0.00;
                };
                stmt.setDouble(5, gpa);
                stmt.setString(6, record.getStu_id());
                stmt.setString(7, record.getCourse_code());
                stmt.executeUpdate();
            }
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class GradeRecord {
        private final String courseCode;
        private final String courseName;
        private final String grade;

        public GradeRecord(String courseCode, String courseName, String grade) {
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.grade = grade;
        }

        public String courseCode() {
            return courseCode;
        }

        public String courseName() {
            return courseName;
        }

        public String grade() {
            return grade;
        }
    }
}