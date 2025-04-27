package org.example.mini_project_java.Controllers.Technical_Officer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.example.mini_project_java.Database.DatabaseConnection;

public class Technical_Officer_Attendance_controller {
    @FXML
    public AnchorPane techAttendance;
    @FXML
    public TextField courseId;
    @FXML
    public Button getStudentbtn;
    @FXML
    public TableView<Student> attendanceTable;
    @FXML
    public TableColumn<Student, String> studentIdColumn;
    @FXML
    public TableColumn<Student, String> studentNameColumn;
    @FXML
    public TableColumn<Student, String> attendanceColumn;
    @FXML
    public ComboBox<String> selectWeek;

    private Connection connection;
    private ObservableList<Student> studentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize database connection
        connection = DatabaseConnection.getConnection();

        // Setup weeks in combobox
        ObservableList<String> weeks = FXCollections.observableArrayList();
        for (int i = 1; i <= 14; i++) {
            weeks.add("Week " + i);
        }
        selectWeek.setItems(weeks);
        selectWeek.getSelectionModel().selectFirst();

        // Setup table columns
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        attendanceColumn.setCellFactory(createAttendanceButtonCellFactory());

        // Set action for get students button
        getStudentbtn.setOnAction(this::handleGetStudents);
    }

    private void handleGetStudents(ActionEvent event) {
        String courseCode = courseId.getText().trim();
        String week = (String) selectWeek.getValue();

        if (courseCode.isEmpty() || week == null) {
            showAlert("Input Error", "Please enter course code and select week");
            return;
        }

        // Validate course code
        if (!isValidCourse(courseCode)) {
            showAlert("Input Error", "Invalid course code");
            return;
        }

        loadStudents();
    }

    private boolean isValidCourse(String courseCode) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT 1 FROM COURSE WHERE course_code = ?")) {
            stmt.setString(1, courseCode);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error validating course: " + e.getMessage());
            return false;
        }
    }

    private void loadStudents() {
        studentList.clear();
        int weekNumber = Integer.parseInt(selectWeek.getValue().replace("Week ", ""));
        String courseCode = courseId.getText().trim();

        try {
            String query = "SELECT username, full_name FROM USERS u WHERE u.role = 'student'";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String studentId = rs.getString("username");
                        String studentName = rs.getString("full_name");
                        String existingStatus = checkExistingAttendance(studentId, courseCode, weekNumber);
                        studentList.add(new Student(studentId, studentName, existingStatus));
                    }
                }
            }

            attendanceTable.setItems(studentList);

        } catch (SQLException e) {
            showAlert("Database Error", "Error loading students: " + e.getMessage());
        }
    }

    private String checkExistingAttendance(String studentId, String courseCode, int week) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT status FROM attendance WHERE student_id = ? AND course_code = ? AND week = ?")) {
            stmt.setString(1, studentId);
            stmt.setString(2, courseCode);
            stmt.setInt(3, week);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error checking attendance: " + e.getMessage());
        }
        return null;
    }

    private Callback<TableColumn<Student, String>, TableCell<Student, String>> createAttendanceButtonCellFactory() {
        return param -> new TableCell<>() {
            private final Button presentBtn = new Button("Present");
            private final Button absentBtn = new Button("Absent");

            {
                presentBtn.setOnAction(event -> {
                    Student student = getTableView().getItems().get(getIndex());
                    updateAttendance(student, "Present");
                });
                absentBtn.setOnAction(event -> {
                    Student student = getTableView().getItems().get(getIndex());
                    updateAttendance(student, "Absent");
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Student student = getTableView().getItems().get(getIndex());
                    presentBtn.setStyle("Present".equals(student.getAttendanceStatus()) ?
                            "-fx-background-color: green; -fx-text-fill: white;" : "");
                    absentBtn.setStyle("Absent".equals(student.getAttendanceStatus()) ?
                            "-fx-background-color: red; -fx-text-fill: white;" : "");

                    HBox buttonBox = new HBox(5, presentBtn, absentBtn);
                    setGraphic(buttonBox);
                }
            }
        };
    }

    private void updateAttendance(Student student, String status) {
        String courseCode = courseId.getText().trim();
        int weekNumber = Integer.parseInt(selectWeek.getValue().replace("Week ", ""));

        try {
            String checkQuery = "SELECT 1 FROM attendance WHERE student_id = ? AND course_code = ? AND week = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, student.getStudentId());
                checkStmt.setString(2, courseCode);
                checkStmt.setInt(3, weekNumber);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        String updateQuery = "UPDATE attendance SET status = ? WHERE student_id = ? AND course_code = ? AND week = ?";
                        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                            updateStmt.setString(1, status);
                            updateStmt.setString(2, student.getStudentId());
                            updateStmt.setString(3, courseCode);
                            updateStmt.setInt(4, weekNumber);
                            updateStmt.executeUpdate();
                        }
                    } else {
                        String insertQuery = "INSERT INTO attendance (student_id, course_code, week, status) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                            insertStmt.setString(1, student.getStudentId());
                            insertStmt.setString(2, courseCode);
                            insertStmt.setInt(3, weekNumber);
                            insertStmt.setString(4, status);
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }

            student.setAttendanceStatus(status);
            attendanceTable.refresh();

        } catch (SQLException e) {
            showAlert("Database Error", "Error updating attendance: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class Student {
        private final String studentId;
        private final String studentName;
        private String attendanceStatus;

        public Student(String studentId, String studentName, String attendanceStatus) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.attendanceStatus = attendanceStatus;
        }

        public String getStudentId() {
            return studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public String getAttendanceStatus() {
            return attendanceStatus;
        }

        public void setAttendanceStatus(String attendanceStatus) {
            this.attendanceStatus = attendanceStatus;
        }
    }
}