package org.example.mini_project_java.Controllers.Undergraduate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.example.mini_project_java.Models.Attaendance;
import org.example.mini_project_java.Models.Courses;
import org.example.mini_project_java.Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class View_Attendance_controller {

    @FXML
    public AnchorPane studentAttandance;
    @FXML
    public AnchorPane studentAttendance;
    @FXML
    public Button seeAttendanceButton;
    @FXML
    public TableView<Attaendance> attendanceTable;
    @FXML
    public TableColumn<Attaendance, String> courseCodeColumn;
    @FXML
    public TableColumn<Attaendance, String> courseNameColumn;
    @FXML
    public TableColumn<Attaendance, String> statusColumn;
    @FXML
    public TableColumn<Attaendance, String> dateColumn;
    @FXML
    public ComboBox<String> courseId;
    @FXML
    public ProgressBar attendancePercentage;

    private ObservableList<Courses> coursesList;
    private ObservableList<Attaendance> attendanceRecords;
    private final String currentStudentId = "TG/2022/1414"; // Replace with actual student ID (e.g., from session)
    private final Attaendance attendanceModel = new Attaendance();

    public void initialize() {
        // Initialize lists
        coursesList = FXCollections.observableArrayList();
        attendanceRecords = FXCollections.observableArrayList();

        // Populate courses from database
        loadCourses();

        // Populate ComboBox with course codes
        ObservableList<String> courseCodes = FXCollections.observableArrayList();
        for (Courses course : coursesList) {
            courseCodes.add(course.getCourseCode());
        }
        courseId.setItems(courseCodes);

        // Set up table columns
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        courseNameColumn.setCellValueFactory(cellData -> {
            String courseCode = cellData.getValue().getCourseCode();
            for (Courses course : coursesList) {
                if (course.getCourseCode().equals(courseCode)) {
                    return new javafx.beans.property.SimpleStringProperty(course.getCourseTitle());
                }
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty("Week " + cellData.getValue().getWeek()));

        // Set up button action
        seeAttendanceButton.setOnAction(event -> loadAttendance());
    }

    private void loadCourses() {
        String query = "SELECT DISTINCT c.course_code, c.course_title " +
                "FROM COURSE c JOIN attendance a ON c.course_code = a.course_code " +
                "WHERE a.student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, currentStudentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                coursesList.add(new Courses(
                        rs.getString("course_code"),
                        rs.getString("course_title"),
                        "", // lecturerId not needed
                        0,  // courseCredit not needed
                        "", // courseType not needed
                        0   // creditHours not needed
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle error (e.g., show alert)
        }
    }

    private void loadAttendance() {
        String selectedCourseCode = courseId.getSelectionModel().getSelectedItem();
        if (selectedCourseCode == null) {
            attendancePercentage.setProgress(0.0);
            return;
        }

        attendanceRecords.clear();
        attendancePercentage.setProgress(0.0);

        // Fetch attendance from database
        String query = "SELECT * FROM attendance WHERE student_id = ? AND course_code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, currentStudentId);
            stmt.setString(2, selectedCourseCode);
            ResultSet rs = stmt.executeQuery();

            int presentCount = 0;
            int postedCount = 0;

            if (rs.next()) {
                for (int week = 1; week <= 15; week++) {
                    String status;
                    if (rs.getObject("week" + week) == null) {
                        status = "Not Posted";
                    } else {
                        boolean isPresent = rs.getBoolean("week" + week);
                        status = isPresent ? "Present" : "Absent";
                        postedCount++;
                        if (isPresent) {
                            presentCount++;
                        }
                    }
                    Attaendance record = new Attaendance(
                            currentStudentId,
                            selectedCourseCode,
                            week,
                            status
                    );
                    attendanceRecords.add(record);
                }
                // Calculate and set progress bar value (0.0 to 1.0)
                double percentage = postedCount == 0 ? 0.0 : (presentCount * 100.0) / postedCount;
                attendancePercentage.setProgress(percentage / 100.0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle error (e.g., show alert)
        }

        attendanceTable.setItems(attendanceRecords);
    }
}