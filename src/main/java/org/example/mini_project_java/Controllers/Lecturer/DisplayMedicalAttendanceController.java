package org.example.mini_project_java.Controllers.Lecturer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.mini_project_java.Models.Attaendance;
import org.example.mini_project_java.Models.Medical;
import org.example.mini_project_java.Models.Undergratuate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DisplayMedicalAttendanceController {

    @FXML
    private TabPane contentPane;
    @FXML
    private Tab attendanceTab;
    @FXML
    private Tab medicalTab;
    @FXML
    private TextField attendanceSearchField;
    @FXML
    private TextField medicalSearchField;
    @FXML
    private TableView<Undergratuate> attendanceTable;
    @FXML
    private TableColumn<Undergratuate, String> attendanceStudentIdColumn;
    @FXML
    private TableColumn<Undergratuate, String> attendanceStudentNameColumn;
    @FXML
    private TableColumn<Undergratuate, String> attendanceStatusColumn;
    @FXML
    private TableColumn<Undergratuate, Double> attendancePercentageColumn;
    @FXML
    private TableView<Medical> medicalTable;
    @FXML
    private TableColumn<Medical, String> medicalStudentIdColumn;
    @FXML
    private TableColumn<Medical, String> medicalStudentNameColumn;
    @FXML
    private TableColumn<Medical, String> medicalWeekColumn;
    @FXML
    private TableColumn<Medical, String> medicalReportColumn;
    @FXML
    private TableColumn<Medical, String> medicalStatusColumn;

    private final String COURSE_CODE = "LEC/RUH/TEC/001";
    private ObservableList<Undergratuate> attendanceData = FXCollections.observableArrayList();
    private ObservableList<Medical> medicalData = FXCollections.observableArrayList();
    private List<Undergratuate> fullAttendanceData = new ArrayList<>();
    private List<Medical> fullMedicalData = new ArrayList<>();

    @FXML
    public void initialize() {
        // Set up table columns
        attendanceStudentIdColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        attendanceStudentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        attendanceStatusColumn.setCellValueFactory(new PropertyValueFactory<>("attendanceStatus"));
        attendancePercentageColumn.setCellValueFactory(new PropertyValueFactory<>("attendancePercentage"));

        medicalStudentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        medicalStudentNameColumn.setCellValueFactory(cellData -> {
            String studentId = cellData.getValue().getStudentId();
            Undergratuate student = getStudentById(studentId);
            return new javafx.beans.property.SimpleStringProperty(student != null ? student.getName() : "Unknown");
        });
        medicalWeekColumn.setCellValueFactory(new PropertyValueFactory<>("week"));
        medicalReportColumn.setCellValueFactory(new PropertyValueFactory<>("medicalReport"));
        medicalStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Load data and set default tab
        loadAttendanceData();
        loadMedicalData();
        contentPane.getSelectionModel().select(attendanceTab);

        // Set up search listeners
        attendanceSearchField.textProperty().addListener((obs, oldValue, newValue) -> searchAttendance());
        medicalSearchField.textProperty().addListener((obs, oldValue, newValue) -> searchMedical());
    }

    @FXML
    public void searchAttendance() {
        String query = attendanceSearchField.getText().toLowerCase().trim();
        if (query.isEmpty()) {
            attendanceData.setAll(fullAttendanceData);
        } else {
            List<Undergratuate> filtered = fullAttendanceData.stream()
                    .filter(student -> student.getUsername().toLowerCase().contains(query) ||
                            student.getName().toLowerCase().contains(query))
                    .collect(Collectors.toList());
            attendanceData.setAll(filtered);
        }
        attendanceTable.setItems(attendanceData);
    }

    @FXML
    public void searchMedical() {
        String query = medicalSearchField.getText().toLowerCase().trim();
        if (query.isEmpty()) {
            medicalData.setAll(fullMedicalData);
        } else {
            List<Medical> filtered = fullMedicalData.stream()
                    .filter(record -> {
                        String studentId = record.getStudentId();
                        Undergratuate student = getStudentById(studentId);
                        String studentName = student != null ? student.getName().toLowerCase() : "";
                        return studentId.toLowerCase().contains(query) || studentName.contains(query);
                    })
                    .collect(Collectors.toList());
            medicalData.setAll(filtered);
        }
        medicalTable.setItems(medicalData);
    }

    @FXML
    public void browseAttendance() {
        // Placeholder for browsing attendance records
        // This could open a file chooser, fetch additional data, etc.
        System.out.println("Browse attendance records functionality to be implemented.");
    }

    @FXML
    public void browseMedical() {
        // Placeholder for browsing medical records
        // This could open a file chooser, fetch additional data, etc.
        System.out.println("Browse medical records functionality to be implemented.");
    }

    private void loadAttendanceData() {
        Attaendance attendance = new Attaendance();
        List<Undergratuate> students = attendance.loadStudents(COURSE_CODE, 1);
        fullAttendanceData = new ArrayList<>();

        for (Undergratuate student : students) {
            Undergratuate record = new Undergratuate(
                    student.getUsername(),
                    null,
                    student.getName(),
                    null,
                    "student",
                    null
            );
            record.setAttendanceStatus(student.getAttendanceStatus());
            record.setAttendancePercentage(student.getAttendancePercentage());
            fullAttendanceData.add(record);
        }
        attendanceData.setAll(fullAttendanceData);
        attendanceTable.setItems(attendanceData);
    }

    private void loadMedicalData() {
        Medical medical = new Medical(0, COURSE_CODE, null, null, null, null);
        List<Medical> records = medical.viewMedicalReport(COURSE_CODE);
        fullMedicalData = new ArrayList<>(records);
        medicalData.setAll(fullMedicalData);
        medicalTable.setItems(medicalData);
    }

    private String getAttendanceStatus(String studentId, String courseCode, int week) {
        Attaendance attendance = new Attaendance();
        String query = "SELECT week" + week + " FROM attendance WHERE student_id = ? AND course_code = ?";
        try (java.sql.Connection conn = org.example.mini_project_java.Database.DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentId);
            stmt.setString(2, courseCode);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getObject(1) != null) {
                    return rs.getBoolean(1) ? "Present" : "Absent";
                }
                return "Not Posted";
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error fetching attendance status: " + e.getMessage());
            return "Error";
        }
    }

    private Undergratuate getStudentById(String studentId) {
        try (java.sql.Connection conn = org.example.mini_project_java.Database.DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT full_name FROM USERS WHERE username = ? AND role = 'student'")) {
            stmt.setString(1, studentId);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Undergratuate(studentId, null, rs.getString("full_name"), null, "student", null);
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error fetching student name: " + e.getMessage());
        }
        return null;
    }
}