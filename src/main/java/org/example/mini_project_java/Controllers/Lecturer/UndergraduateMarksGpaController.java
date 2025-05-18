package org.example.mini_project_java.Controllers.Lecturer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.mini_project_java.Database.DatabaseConnection;
import org.example.mini_project_java.Models.Marks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UndergraduateMarksGpaController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(UndergraduateMarksGpaController.class);
    private static final String DEFAULT_LECTURER_ID = "LEC/RUH/TEC/001";

    @FXML
    private TableView<GradeRecord> marksGpaTable;

    @FXML
    private TableColumn<GradeRecord, String> studentIdColumn;

    @FXML
    private TableColumn<GradeRecord, String> studentNameColumn;

    @FXML
    private TableColumn<GradeRecord, Double> marksColumn;

    @FXML
    private TableColumn<GradeRecord, String> gradeColumn;

    @FXML
    private TableColumn<GradeRecord, Double> gpaColumn;

    @FXML
    private TextField searchField;

    private String loggedInLecturerId;
    private ObservableList<GradeRecord> gradeList = FXCollections.observableArrayList();

    // Inner class to hold table data including student name
    public static class GradeRecord {
        private final String stuId;
        private final String studentName;
        private final double finalMarks;
        private final String grade;
        private final double gpa;

        public GradeRecord(String stuId, String studentName, double finalMarks, String grade, double gpa) {
            this.stuId = stuId;
            this.studentName = studentName;
            this.finalMarks = finalMarks;
            this.grade = grade;
            this.gpa = gpa;
        }

        public String getStuId() { return stuId; }
        public String getStudentName() { return studentName; }
        public double getFinalMarks() { return finalMarks; }
        public String getGrade() { return grade; }
        public double getGpa() { return gpa; }
    }

    public void setLoggedInLecturerId(String lecturerId) {
        if (lecturerId != null && !lecturerId.trim().isEmpty()) {
            this.loggedInLecturerId = lecturerId;
        } else {
            logger.warn("Invalid lecturer ID provided; using default ID: {}", DEFAULT_LECTURER_ID);
            this.loggedInLecturerId = DEFAULT_LECTURER_ID;
        }
        loadGradeData();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTableColumns();
        if (loggedInLecturerId == null) {
            logger.info("No lecturer ID set; using default ID: {}", DEFAULT_LECTURER_ID);
            loggedInLecturerId = DEFAULT_LECTURER_ID;
        }
        loadGradeData();
        setupSearch();
    }

    private void initializeTableColumns() {
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("stuId"));
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        marksColumn.setCellValueFactory(new PropertyValueFactory<>("finalMarks"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        gpaColumn.setCellValueFactory(new PropertyValueFactory<>("gpa"));
    }

    private void loadGradeData() {
        Task<ObservableList<GradeRecord>> task = new Task<>() {
            @Override
            protected ObservableList<GradeRecord> call() throws Exception {
                logger.debug("Loading grade data for lecturer {}", loggedInLecturerId);
                ObservableList<GradeRecord> list = FXCollections.observableArrayList();
                try (Connection conn = DatabaseConnection.getConnection()) {
                    if (conn == null) {
                        throw new SQLException("Database connection is null");
                    }
                    String query = "SELECT sg.stu_id, sg.course_code, sg.finalMarks, sg.grade, sg.gpa, u.full_name AS stu_name " +
                            "FROM Student_Grades sg " +
                            "JOIN Course c ON sg.course_code = c.course_code " +
                            "JOIN USERS u ON sg.stu_id = u.username " +
                            "WHERE c.lecturer_id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setString(1, loggedInLecturerId);
                        try (ResultSet rs = stmt.executeQuery()) {
                            while (rs.next()) {
                                String studentId = rs.getString("stu_id") != null ? rs.getString("stu_id") : "";
                                String courseCode = rs.getString("course_code") != null ? rs.getString("course_code") : "";
                                double finalMarks = rs.getDouble("finalMarks");
                                String grade = rs.getString("grade") != null ? rs.getString("grade") : "";
                                double gpa = rs.getDouble("gpa");
                                String studentName = rs.getString("stu_name") != null ? rs.getString("stu_name") : "Unknown";
                                if (studentId.isEmpty() || courseCode.isEmpty()) {
                                    logger.warn("Skipping record with empty stu_id or course_code for lecturer {}", loggedInLecturerId);
                                    continue;
                                }
                                list.add(new GradeRecord(studentId, studentName, finalMarks, grade, gpa));
                            }
                        }
                    }
                } catch (SQLException e) {
                    logger.error("SQL error loading grade data for lecturer {}: {}", loggedInLecturerId, e.getMessage(), e);
                    throw e;
                }
                logger.debug("Loaded {} grade records", list.size());
                return list;
            }
        };
        task.setOnSucceeded(event -> {
            gradeList.setAll(task.getValue());
            marksGpaTable.setItems(gradeList);
        });
        task.setOnFailed(event -> {
            logger.error("Failed to load grade data for lecturer {}", loggedInLecturerId, event.getSource().getException());
            showAlert("Database Error", "Error loading grade data: " + event.getSource().getException().getMessage());
        });
        new Thread(task).start();
    }

    private void setupSearch() {
        FilteredList<GradeRecord> filteredData = new FilteredList<>(gradeList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(record -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                String studentId = record.getStuId() != null ? record.getStuId().toLowerCase() : "";
                String studentName = record.getStudentName() != null ? record.getStudentName().toLowerCase() : "";
                return studentId.contains(lowerCaseFilter) || studentName.contains(lowerCaseFilter);
            });
        });
        SortedList<GradeRecord> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(marksGpaTable.comparatorProperty());
        marksGpaTable.setItems(sortedData);
    }

    @FXML
    private void refreshData() {
        loadGradeData();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}