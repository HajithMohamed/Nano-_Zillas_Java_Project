package org.example.mini_project_java.Controllers.Undergraduate;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class viewAttendance {

    // === Model Class for Attendance Records ===
    public static class AttendanceRecord {
        private final StringProperty courseCode;
        private final StringProperty courseName;
        private final StringProperty status;
        private final StringProperty date;

        public AttendanceRecord(String courseCode, String courseName, String status, String date) {
            this.courseCode = new SimpleStringProperty(courseCode);
            this.courseName = new SimpleStringProperty(courseName);
            this.status = new SimpleStringProperty(status);
            this.date = new SimpleStringProperty(date);
        }

        public StringProperty courseCodeProperty() { return courseCode; }
        public StringProperty courseNameProperty() { return courseName; }
        public StringProperty statusProperty() { return status; }
        public StringProperty dateProperty() { return date; }
    }

    // === FXML References ===
    @FXML
    private TextField studentIdField;

    @FXML
    private TableView<AttendanceRecord> attendanceTable;

    @FXML
    private TableColumn<AttendanceRecord, String> courseCodeColumn;

    @FXML
    private TableColumn<AttendanceRecord, String> courseNameColumn;

    @FXML
    private TableColumn<AttendanceRecord, String> statusColumn;

    @FXML
    private TableColumn<AttendanceRecord, String> dateColumn;

    @FXML
    private Button seeAttendanceButton;

    private ObservableList<AttendanceRecord> attendanceList = FXCollections.observableArrayList();

    // === Initialization ===
    @FXML
    private void initialize() {
        courseCodeColumn.setCellValueFactory(cellData -> cellData.getValue().courseCodeProperty());
        courseNameColumn.setCellValueFactory(cellData -> cellData.getValue().courseNameProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        seeAttendanceButton.setOnAction(this::handleSeeAttendance);
    }

    // === Handle Button Action ===
    private void handleSeeAttendance(ActionEvent event) {
        String studentId = studentIdField.getText();

        if (studentId == null || studentId.trim().isEmpty()) {
            System.out.println("Student ID is empty!");
            return;
        }

        // Get connection (adjust to your actual DB connection class or logic)
        org.example.mini_project_java.Database.DbConnection db = new org.example.mini_project_java.Database.DbConnection();
        Connection con = db.fetchConnection();

        String sql = "SELECT course_code, c_name, status, a_date FROM attendance WHERE Student_id=?";
        attendanceList.clear();

        try {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, studentId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                attendanceList.add(new AttendanceRecord(
                        rs.getString("course_code"),
                        rs.getString("c_name"),
                        rs.getString("status"),
                        rs.getString("a_date")
                ));
            }

            attendanceTable.setItems(attendanceList);

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
