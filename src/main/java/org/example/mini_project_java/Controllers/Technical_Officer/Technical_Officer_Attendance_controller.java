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
import org.example.mini_project_java.Models.Attaendance;
import org.example.mini_project_java.Models.Courses;
import org.example.mini_project_java.Models.Undergratuate;

public class Technical_Officer_Attendance_controller {
    @FXML
    public AnchorPane techAttendance;
    @FXML
    public ComboBox<Courses> courseId;
    @FXML
    public Button getStudentbtn;
    @FXML
    public TableView<Undergratuate> attendanceTable;
    @FXML
    public TableColumn<Undergratuate, String> studentIdColumn;
    @FXML
    public TableColumn<Undergratuate, String> studentNameColumn;
    @FXML
    public TableColumn<Undergratuate, String> attendanceColumn;
    @FXML
    public TableColumn<Undergratuate, Double> percentageColumn; // New column for percentage
    @FXML
    public ComboBox<String> selectWeek;

    private Connection connection;
    private ObservableList<Undergratuate> studentList = FXCollections.observableArrayList();
    private ObservableList<Courses> courseList = FXCollections.observableArrayList();
    private Attaendance attendanceModel = new Attaendance();

    @FXML
    public void initialize() {
        // Initialize database connection
        connection = DatabaseConnection.getConnection();

        // Populate course ComboBox
        loadCourses();

        // Setup weeks in combobox (1-15 to match table)
        ObservableList<String> weeks = FXCollections.observableArrayList();
        for (int i = 1; i <= 15; i++) {
            weeks.add("Week " + i);
        }
        selectWeek.setItems(weeks);
        selectWeek.getSelectionModel().selectFirst();

        // Setup table columns
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        percentageColumn.setCellValueFactory(new PropertyValueFactory<>("attendancePercentage")); // New column
        attendanceColumn.setCellFactory(createAttendanceButtonCellFactory());

        // Set action for get students button
        getStudentbtn.setOnAction(this::handleGetStudents);
    }

    private void loadCourses() {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT course_code, course_title FROM COURSE")) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String courseCode = rs.getString("course_code");
                    String courseTitle = rs.getString("course_title");
                    courseList.add(new Courses(courseCode, courseTitle, null, 0, null, 0));
                }
                courseId.setItems(courseList);
                courseId.setCellFactory(param -> new ListCell<Courses>() {
                    @Override
                    protected void updateItem(Courses item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getCourseCode() + " - " + item.getCourseTitle());
                        }
                    }
                });
                courseId.setButtonCell(new ListCell<Courses>() {
                    @Override
                    protected void updateItem(Courses item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText("Select Course");
                        } else {
                            setText(item.getCourseCode() + " - " + item.getCourseTitle());
                        }
                    }
                });
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading courses: " + e.getMessage());
        }
    }

    private void handleGetStudents(ActionEvent event) {
        Courses selectedCourse = courseId.getValue();
        String courseCode = (selectedCourse != null) ? selectedCourse.getCourseCode() : "";
        String week = selectWeek.getValue();

        if (courseCode.isEmpty() || week == null) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please select a course and a week.");
            return;
        }

        if (!attendanceModel.isValidCourse(courseCode)) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid course code.");
            return;
        }

        loadStudents();
    }

    private void loadStudents() {
        studentList.clear();
        int weekNumber = Integer.parseInt(selectWeek.getValue().replace("Week ", ""));
        String courseCode = courseId.getValue().getCourseCode();

        studentList.addAll(attendanceModel.loadStudents(courseCode, weekNumber));
        attendanceTable.setItems(studentList);
    }

    private Callback<TableColumn<Undergratuate, String>, TableCell<Undergratuate, String>> createAttendanceButtonCellFactory() {
        return param -> new TableCell<>() {
            private final Button presentBtn = new Button("Present");
            private final Button absentBtn = new Button("Absent");

            {
                presentBtn.setOnAction(event -> {
                    Undergratuate student = getTableView().getItems().get(getIndex());
                    updateAttendance(student, true);
                });
                absentBtn.setOnAction(event -> {
                    Undergratuate student = getTableView().getItems().get(getIndex());
                    updateAttendance(student, false);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Undergratuate student = getTableView().getItems().get(getIndex());
                    String status = student.getAttendanceStatus();
                    presentBtn.setStyle("Present".equals(status) ?
                            "-fx-background-color: green; -fx-text-fill: white;" : "");
                    absentBtn.setStyle("Absent".equals(status) ?
                            "-fx-background-color: red; -fx-text-fill: white;" : "");

                    HBox buttonBox = new HBox(5, presentBtn, absentBtn);
                    setGraphic(buttonBox);
                }
            }
        };
    }

    private void updateAttendance(Undergratuate student, boolean isPresent) {
        String courseCode = courseId.getValue().getCourseCode();
        int weekNumber;
        try {
            weekNumber = Integer.parseInt(selectWeek.getValue().replace("Week ", ""));
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid week number selected.");
            return;
        }

        if (courseCode.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Course code cannot be empty.");
            return;
        }
        if (weekNumber < 1 || weekNumber > 15) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Week number must be between 1 and 15.");
            return;
        }

        if (!attendanceModel.isValidStudent(student.getUsername())) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Student ID does not exist.");
            return;
        }

        try {
            attendanceModel.updateAttendance(student.getUsername(), courseCode, weekNumber, isPresent);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Attendance updated successfully for " + student.getName() + ".");
            student.setAttendanceStatus(isPresent ? "Present" : "Absent");
            // Recalculate percentage after updating attendance
            double percentage = attendanceModel.calculateAttendancePercentage(student.getUsername(), courseCode);
            student.setAttendancePercentage(percentage);
            attendanceTable.refresh();
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}