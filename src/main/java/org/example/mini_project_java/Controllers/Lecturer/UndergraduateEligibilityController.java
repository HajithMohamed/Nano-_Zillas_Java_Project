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
import org.example.mini_project_java.Models.Eligibility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class UndergraduateEligibilityController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(UndergraduateEligibilityController.class);
    private static final String DEFAULT_LECTURER_ID = "LEC/RUH/TEC/001";

    @FXML
    private TableView<Eligibility> eligibilityTable;

    @FXML
    private TableColumn<Eligibility, String> studentIdColumn;

    @FXML
    private TableColumn<Eligibility, String> courseCodeColumn;

    @FXML
    private TableColumn<Eligibility, String> courseNameColumn;

    @FXML
    private TableColumn<Eligibility, Double> caFinalColumn;

    @FXML
    private TableColumn<Eligibility, Double> attendanceColumn;

    @FXML
    private TableColumn<Eligibility, String> eligibilityColumn;

    @FXML
    private TextField searchField;

    private String loggedInLecturerId;
    private ObservableList<Eligibility> eligibilityList = FXCollections.observableArrayList();
    private Map<String, String> courseNameMap = new HashMap<>();

    public void setLoggedInLecturerId(String lecturerId) {
        if (lecturerId != null && !lecturerId.trim().isEmpty()) {
            this.loggedInLecturerId = lecturerId;
        } else {
            logger.warn("Invalid lecturer ID provided; using default ID: {}", DEFAULT_LECTURER_ID);
            this.loggedInLecturerId = DEFAULT_LECTURER_ID;
        }
        calculateAndStoreEligibility();
        loadEligibilityData();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTableColumns();
        loadCourseNames();
        if (loggedInLecturerId == null) {
            logger.info("No lecturer ID set; using default ID: {}", DEFAULT_LECTURER_ID);
            loggedInLecturerId = DEFAULT_LECTURER_ID;
            calculateAndStoreEligibility();
            loadEligibilityData();
        }
        setupSearch();
    }

    private void initializeTableColumns() {
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("student_id"));
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("course_code"));
        courseNameColumn.setCellValueFactory(cellData -> {
            String courseCode = cellData.getValue().getCourse_code();
            String courseName = courseNameMap.getOrDefault(courseCode, "Unknown Course");
            return javafx.beans.binding.Bindings.createStringBinding(() -> courseName);
        });
        caFinalColumn.setCellValueFactory(new PropertyValueFactory<>("caFinal"));
        attendanceColumn.setCellValueFactory(new PropertyValueFactory<>("attendancePercentage"));
        eligibilityColumn.setCellValueFactory(new PropertyValueFactory<>("eligibilityStatus"));
    }

    private void loadCourseNames() {
        Task<Map<String, String>> task = new Task<>() {
            @Override
            protected Map<String, String> call() throws Exception {
                logger.debug("Loading course names");
                Map<String, String> map = new HashMap<>();
                try (Connection conn = DatabaseConnection.getConnection()) {
                    if (conn == null) {
                        throw new SQLException("Database connection is null");
                    }
                    String query = "SELECT course_code, course_title FROM COURSE";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        try (ResultSet rs = stmt.executeQuery()) {
                            while (rs.next()) {
                                map.put(rs.getString("course_code"), rs.getString("course_title"));
                            }
                        }
                    }
                }
                logger.debug("Loaded {} course names", map.size());
                return map;
            }
        };
        task.setOnSucceeded(event -> courseNameMap.putAll(task.getValue()));
        task.setOnFailed(event -> {
            logger.error("Failed to load course names", task.getException());
            showAlert("Database Error", "Error loading course names: " + task.getException().getMessage());
        });
        new Thread(task).start();
    }

    private void calculateAndStoreEligibility() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                logger.debug("Calculating eligibility for lecturer {}", loggedInLecturerId);
                try (Connection conn = DatabaseConnection.getConnection()) {
                    if (conn == null) {
                        throw new SQLException("Database connection is null");
                    }
                    String courseQuery = "SELECT course_code FROM COURSE WHERE lecturer_id = ?";
                    try (PreparedStatement courseStmt = conn.prepareStatement(courseQuery)) {
                        courseStmt.setString(1, loggedInLecturerId);
                        try (ResultSet courseRs = courseStmt.executeQuery()) {
                            while (courseRs.next()) {
                                String courseCode = courseRs.getString("course_code");
                                Eligibility.calculateEligibilityForCourse(courseCode);
                            }
                        }
                    }
                }
                return null;
            }
        };
        task.setOnFailed(event -> {
            logger.error("Failed to calculate eligibility", task.getException());
            showAlert("Database Error", "Error calculating eligibility: " + task.getException().getMessage());
        });
        new Thread(task).start();
    }

    private void loadEligibilityData() {
        Task<ObservableList<Eligibility>> task = new Task<>() {
            @Override
            protected ObservableList<Eligibility> call() throws Exception {
                logger.debug("Loading all eligibility data");
                ObservableList<Eligibility> list = FXCollections.observableArrayList();
                try (Connection conn = DatabaseConnection.getConnection()) {
                    if (conn == null) {
                        throw new SQLException("Database connection is null");
                    }
                    String query = "SELECT * FROM Eligibility WHERE course_code IN (SELECT course_code FROM COURSE WHERE lecturer_id = ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setString(1, loggedInLecturerId);
                        try (ResultSet rs = stmt.executeQuery()) {
                            while (rs.next()) {
                                list.add(new Eligibility(
                                        rs.getString("student_id"),
                                        rs.getString("course_code"),
                                        rs.getDouble("caFinal"),
                                        rs.getDouble("attendancePercentage"),
                                        rs.getString("eligibilityStatus")
                                ));
                            }
                        }
                    }
                }
                logger.debug("Loaded {} eligibility records", list.size());
                return list;
            }
        };
        task.setOnSucceeded(event -> {
            eligibilityList.setAll(task.getValue());
            eligibilityTable.setItems(eligibilityList);
        });
        task.setOnFailed(event -> {
            logger.error("Failed to load eligibility data", task.getException());
            showAlert("Database Error", "Error loading eligibility data: " + task.getException().getMessage());
        });
        new Thread(task).start();
    }

    private void setupSearch() {
        FilteredList<Eligibility> filteredData = new FilteredList<>(eligibilityList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(eligibility -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (eligibility.getStudent_id().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                String courseName = courseNameMap.getOrDefault(eligibility.getCourse_code(), "");
                return courseName.toLowerCase().contains(lowerCaseFilter);
            });
        });
        SortedList<Eligibility> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(eligibilityTable.comparatorProperty());
        eligibilityTable.setItems(sortedData);
    }

    @FXML
    private void refreshData() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                logger.debug("Processing all marks for lecturer {}", loggedInLecturerId);
                try (Connection conn = DatabaseConnection.getConnection()) {
                    if (conn == null) {
                        throw new SQLException("Database connection is null");
                    }
                    String courseQuery = "SELECT course_code FROM COURSE WHERE lecturer_id = ?";
                    try (PreparedStatement courseStmt = conn.prepareStatement(courseQuery)) {
                        courseStmt.setString(1, loggedInLecturerId);
                        try (ResultSet courseRs = courseStmt.executeQuery()) {
                            while (courseRs.next()) {
                                String courseCode = courseRs.getString("course_code");
                                logger.debug("Calculating eligibility for course {}", courseCode);
                                Eligibility.calculateEligibilityForCourse(courseCode);
                            }
                        }
                    }
                }
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            loadCourseNames();
            loadEligibilityData();
        });
        task.setOnFailed(event -> {
            logger.error("Failed to refresh eligibility data", task.getException());
            showAlert("Database Error", "Error refreshing eligibility data: " + task.getException().getMessage());
        });
        new Thread(task).start();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}