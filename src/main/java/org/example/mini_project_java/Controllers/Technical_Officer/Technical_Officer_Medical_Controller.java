package org.example.mini_project_java.Controllers.Technical_Officer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import org.example.mini_project_java.Database.DatabaseConnection;
import org.example.mini_project_java.Models.Medical;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Technical_Officer_Medical_Controller implements Initializable {
    @FXML
    public AnchorPane AttupdMedSub;

    @FXML
    public ComboBox<String> subjectComboBox;

    @FXML
    public ComboBox<String> weekComboBox;

    @FXML
    public Button filterButton;

    @FXML
    public TableView<MedicalSubmission> studentTable;

    @FXML
    public TableColumn<MedicalSubmission, String> studentIdColumn;

    @FXML
    public TableColumn<MedicalSubmission, String> studentNameColumn;

    @FXML
    public TableColumn<MedicalSubmission, Void> actionColumn;

    // Class to represent a medical submission in the TableView
    public static class MedicalSubmission {
        private final String studentId;
        private final String studentName;
        private final int medicalId;
        private String status;

        public MedicalSubmission(String studentId, String studentName, int medicalId, String status) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.medicalId = medicalId;
            this.status = status;
        }

        public String getStudentId() {
            return studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public int getMedicalId() {
            return medicalId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up the columns
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));

        // Set up the action column with accept/reject buttons
        setupActionColumn();

        // Load course codes into subjectComboBox
        loadCourses();

        // Load weeks into weekComboBox
        loadWeeks();

        // Set up filter button action
        filterButton.setOnAction(event -> loadMedicalSubmissions());
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button acceptButton = new Button("Accept");
            private final Button rejectButton = new Button("Reject");
            private final HBox buttonBox = new HBox(10, acceptButton, rejectButton);

            {
                // Style the buttons
                acceptButton.getStyleClass().add("accept-button");
                rejectButton.getStyleClass().add("reject-button");

                // Set action for accept button
                acceptButton.setOnAction(event -> {
                    MedicalSubmission submission = getTableView().getItems().get(getIndex());
                    updateMedicalStatus(submission.getMedicalId(), "accepted");
                    submission.setStatus("accepted");
                    studentTable.refresh();
                });

                // Set action for reject button
                rejectButton.setOnAction(event -> {
                    MedicalSubmission submission = getTableView().getItems().get(getIndex());
                    updateMedicalStatus(submission.getMedicalId(), "rejected");
                    submission.setStatus("rejected");
                    studentTable.refresh();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                MedicalSubmission submission = getTableView().getItems().get(getIndex());

                // Disable buttons based on current status
                if ("accepted".equals(submission.getStatus())) {
                    acceptButton.setDisable(true);
                    rejectButton.setDisable(false);
                } else if ("rejected".equals(submission.getStatus())) {
                    acceptButton.setDisable(false);
                    rejectButton.setDisable(true);
                } else {
                    acceptButton.setDisable(false);
                    rejectButton.setDisable(false);
                }

                setGraphic(buttonBox);
            }
        });
    }

    private void loadCourses() {
        List<String> courses = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT DISTINCT courseCode FROM COURSES";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        courses.add(rs.getString("courseCode"));
                    }
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load courses: " + e.getMessage());
            e.printStackTrace();
        }

        subjectComboBox.setItems(FXCollections.observableArrayList(courses));
    }

    private void loadWeeks() {
        List<String> weeks = new ArrayList<>();
        for (int i = 1; i <= 14; i++) {
            weeks.add("Week " + i);
        }

        weekComboBox.setItems(FXCollections.observableArrayList(weeks));

        // Set converter to extract week number when needed
        weekComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(String week) {
                return week;
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        });
    }

    private void loadMedicalSubmissions() {
        String selectedCourse = subjectComboBox.getValue();
        String selectedWeek = weekComboBox.getValue();

        if (selectedCourse == null || selectedWeek == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Selection", "Please select both a course and week.");
            return;
        }

        // Extract week number from "Week X"
        String weekNumber = selectedWeek.replace("Week ", "");

        ObservableList<MedicalSubmission> medicalSubmissions = FXCollections.observableArrayList();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT m.medicalId, m.studentId, u.full_name, m.status " +
                    "FROM MEDICAL_REPORTS m " +
                    "JOIN USERS u ON m.studentId = u.username " +
                    "WHERE m.courseCode = ? AND m.week = ?";

            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, selectedCourse);
                ps.setString(2, weekNumber);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int medicalId = rs.getInt("medicalId");
                        String studentId = rs.getString("studentId");
                        String studentName = rs.getString("full_name");
                        String status = rs.getString("status");

                        medicalSubmissions.add(new MedicalSubmission(studentId, studentName, medicalId, status));
                    }
                }
            }

            studentTable.setItems(medicalSubmissions);

            if (medicalSubmissions.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Records",
                        "No medical submissions found for " + selectedCourse + " in " + selectedWeek);
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Failed to load medical submissions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean updateMedicalStatus(int medicalId, String newStatus) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String updateSQL = "UPDATE MEDICAL_REPORTS SET status = ? WHERE medicalId = ?";

            try (PreparedStatement ps = connection.prepareStatement(updateSQL)) {
                ps.setString(1, newStatus);
                ps.setInt(2, medicalId);

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Update Successful",
                            "Medical submission has been " + newStatus);
                    return true;
                } else {
                    showAlert(Alert.AlertType.ERROR, "Update Failed",
                            "Failed to update medical submission status.");
                    return false;
                }
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Failed to update medical status: " + e.getMessage());
            e.printStackTrace();
            return false;
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