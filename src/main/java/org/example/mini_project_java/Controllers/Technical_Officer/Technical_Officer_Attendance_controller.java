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
import javafx.util.Callback;
import org.example.mini_project_java.Database.DatabaseConnection;
import org.example.mini_project_java.Models.Attaendance;
import org.example.mini_project_java.Models.Undergratuate;

public class Technical_Officer_Attendance_controller {
    public AnchorPane techAttendance;
    public TextField courseId;
    public Button getStudentbtn;

    public TableView attendanceTable;
    public TableColumn studentIdColumn;
    public TableColumn studentNameColumn;
    public TableColumn attendanceColumn;
    public ComboBox selectWeek;

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

        // Setup attendance column with buttons
        attendanceColumn.setCellFactory(createAttendanceButtonCellFactory());

        // Set action for get students button
        getStudentbtn.setOnAction(this::handleGetStudents);
    }

    private void handleGetStudents(ActionEvent event) {
        String courseCod = courseId.getText();
        String week = (String) selectWeek.getValue();

        if (courseCod.isEmpty() || week == null) {
            showAlert("Input Error", "Please enter course code and select week");
            return;
        }

        loadStudents(courseCod, week);
    }

    private void loadStudents(String courseCode, String week) {
        studentList.clear();

        try {
            // Query to get all students with role "student" from USERS table
            String query = "SELECT u.id, u.name FROM USERS u WHERE u.role = 'student'";

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String studentId = resultSet.getString("id");
                String studentName = resultSet.getString("name");

                // Check if attendance record exists
                String existingStatus = checkExistingAttendance(studentId, courseCode, week);

                Student student = new Student(studentId, studentName, existingStatus);
                studentList.add(student);
            }

            attendanceTable.setItems(studentList);

        } catch (SQLException e) {
            showAlert("Database Error", "Error loading students: " + e.getMessage());
        }
    }

    private String checkExistingAttendance(String studentId, String courseCode, String week) {
        try {
            String query = "SELECT status FROM attaendance WHERE student_id = ? AND Course_code = ? AND week = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, studentId);
            statement.setString(2, courseCode);
            statement.setString(3, week);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("status");
            }
        } catch (SQLException e) {
            System.out.println("Error checking attendance: " + e.getMessage());
        }

        return null; // No existing record
    }

    private Callback<TableColumn<Student, String>, TableCell<Student, String>> createAttendanceButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<Student, String> call(TableColumn<Student, String> param) {
                return new TableCell<>() {
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
                            if ("Present".equals(student.getAttendanceStatus())) {
                                presentBtn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                                absentBtn.setStyle("");
                            } else if ("Absent".equals(student.getAttendanceStatus())) {
                                absentBtn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                                presentBtn.setStyle("");
                            } else {
                                presentBtn.setStyle("");
                                absentBtn.setStyle("");
                            }

                            // Create a container for the buttons
                            javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(5);
                            buttonBox.getChildren().addAll(presentBtn, absentBtn);
                            setGraphic(buttonBox);
                        }
                    }
                };
            }
        };
    }

    private void updateAttendance(Student student, String status) {
        try {
            String courseCode = courseId.getText();
            String week = (String) selectWeek.getValue();

            Attaendance attendance = new Attaendance();

            // First check if record exists
            String checkQuery = "SELECT * FROM attaendance WHERE student_id = ? AND Course_code = ? AND week = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setString(1, student.getStudentId());
            checkStmt.setString(2, courseCode);
            checkStmt.setString(3, week);

            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Update existing record
                String updateQuery = "UPDATE attaendance SET status = ? WHERE student_id = ? AND Course_code = ? AND week = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                updateStmt.setString(1, status);
                updateStmt.setString(2, student.getStudentId());
                updateStmt.setString(3, courseCode);
                updateStmt.setString(4, week);

                updateStmt.executeUpdate();
                updateStmt.close();
            } else {
                // Insert new record
                String insertQuery = "INSERT INTO attaendance (student_id, Course_code, week, status) VALUES (?, ?, ?, ?)";
                PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
                insertStmt.setString(1, student.getStudentId());
                insertStmt.setString(2, courseCode);
                insertStmt.setString(3, week);
                insertStmt.setString(4, status);

                insertStmt.executeUpdate();
                insertStmt.close();
            }

            // Update the student's status in the table
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

    // Inner class for Student model with attendance status
    public static class Student {
        private String studentId;
        private String studentName;
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