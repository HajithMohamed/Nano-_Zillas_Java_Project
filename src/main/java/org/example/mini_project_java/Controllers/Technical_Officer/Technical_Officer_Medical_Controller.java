package org.example.mini_project_java.Controllers.Technical_Officer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.example.mini_project_java.Database.DatabaseConnection;
import org.example.mini_project_java.Models.Medical;
import org.example.mini_project_java.Models.Attaendance;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Technical_Officer_Medical_Controller implements Initializable {
    @FXML
    public AnchorPane AttupdMedSub;
    @FXML
    public TextField addStudentIdField;
    @FXML
    public ComboBox<String> addCourseComboBox;
    @FXML
    public ComboBox<Integer> startWeekComboBox;
    @FXML
    public ComboBox<Integer> endWeekComboBox;
    @FXML
    public TextField medicalNoField;
    @FXML
    public TextField medicalFilePathField;
    @FXML
    public Button browseButton;
    @FXML
    public DatePicker submissionDatePicker;
    @FXML
    public Button addMedicalButton;
    @FXML
    public ComboBox<String> subjectComboBox;
    @FXML
    public ComboBox<Integer> weekComboBox;
    @FXML
    public Button filterButton;
    @FXML
    public TableView<MedicalSubmission> studentTable;
    @FXML
    public TableColumn<MedicalSubmission, String> studentIdColumn;
    @FXML
    public TableColumn<MedicalSubmission, String> studentNameColumn;
    @FXML
    public TableColumn<MedicalSubmission, String> medicalIdColumn;
    @FXML
    public TableColumn<MedicalSubmission, String> weekRangeColumn;
    @FXML
    public TableColumn<MedicalSubmission, Date> submissionDateColumn;
    @FXML
    public TableColumn<MedicalSubmission, String> statusColumn;
    @FXML
    public TableColumn<MedicalSubmission, Void> actionColumn;
    @FXML
    public TableColumn<MedicalSubmission, Void> viewFileColumn;

    private File selectedFile;
    private final String UPLOAD_DIR = "Uploads/medical_reports/";
    private final Attaendance attendance = new Attaendance();

    public static class MedicalSubmission {
        private final int medicalId;
        private final String studentId;
        private final String studentName;
        private final String courseCode;
        private final String weekRange;
        private final Date submissionDate;
        private String status;
        private final String filePath;
        private final String medicalNo;

        public MedicalSubmission(int medicalId, String studentId, String studentName, String courseCode,
                                 String weekRange, Date submissionDate, String status, String filePath, String medicalNo) {
            this.medicalId = medicalId;
            this.studentId = studentId;
            this.studentName = studentName;
            this.courseCode = courseCode;
            this.weekRange = weekRange;
            this.submissionDate = submissionDate;
            this.status = status;
            this.filePath = filePath;
            this.medicalNo = medicalNo;
        }

        public int getMedicalId() { return medicalId; }
        public String getStudentId() { return studentId; }
        public String getStudentName() { return studentName; }
        public String getCourseCode() { return courseCode; }
        public String getWeekRange() { return weekRange; }
        public Date getSubmissionDate() { return submissionDate; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getFilePath() { return filePath; }
        public String getMedicalNo() { return medicalNo; }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeUploadDirectory();
        setupWeekComboBoxes();
        loadCourses();
        setupFileChooser();
        setupAddMedicalButton();
        setupTableColumns();
        setupActionColumns();
        setupFilterButton();
        studentTable.setItems(FXCollections.observableArrayList());
    }

    private void initializeUploadDirectory() {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Created upload directory: " + uploadPath.toAbsolutePath());
            } else {
                System.out.println("Upload directory already exists: " + uploadPath.toAbsolutePath());
            }
            if (!Files.isWritable(uploadPath)) {
                throw new IOException("Upload directory is not writable: " + uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Directory Error", "Failed to initialize upload directory: " + e.getMessage());
        }
    }

    private void setupWeekComboBoxes() {
        ObservableList<Integer> weeks = FXCollections.observableArrayList();
        for (int i = 1; i <= 15; i++) weeks.add(i);
        startWeekComboBox.setItems(weeks);
        endWeekComboBox.setItems(weeks);
        weekComboBox.setItems(weeks);
    }

    private void loadCourses() {
        List<String> courses = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT course_code FROM COURSE");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) courses.add(rs.getString("course_code"));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load courses: " + e.getMessage());
        }
        ObservableList<String> courseList = FXCollections.observableArrayList(courses);
        addCourseComboBox.setItems(courseList);
        subjectComboBox.setItems(courseList);
    }

    private void setupFileChooser() {
        browseButton.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select Medical Report");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            selectedFile = fc.showOpenDialog(AttupdMedSub.getScene().getWindow());
            if (selectedFile != null) {
                medicalFilePathField.setText(selectedFile.getAbsolutePath());
            }
        });
    }

    private void setupAddMedicalButton() {
        addMedicalButton.setOnAction(e -> {
            if (validateMedicalForm()) addMedicalRecord();
        });
    }

    private boolean validateMedicalForm() {
        if (addStudentIdField.getText().isEmpty() || addCourseComboBox.getValue() == null ||
                startWeekComboBox.getValue() == null || endWeekComboBox.getValue() == null ||
                medicalNoField.getText().isEmpty() || selectedFile == null || submissionDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please fill in all required fields.");
            return false;
        }
        if (!attendance.isValidStudent(addStudentIdField.getText())) {
            showAlert(Alert.AlertType.WARNING, "Invalid Student", "Please enter a valid student ID.");
            return false;
        }
        if (!attendance.isValidCourse(addCourseComboBox.getValue())) {
            showAlert(Alert.AlertType.WARNING, "Invalid Course", "Please select a valid course.");
            return false;
        }
        if (startWeekComboBox.getValue() > endWeekComboBox.getValue()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Week Range", "Start week must be less than or equal to end week.");
            return false;
        }
        if (!selectedFile.exists() || !selectedFile.canRead()) {
            showAlert(Alert.AlertType.WARNING, "File Error", "The selected file does not exist or cannot be read.");
            return false;
        }
        return true;
    }

    private void addMedicalRecord() {
        try {
            // Sanitize the filename to avoid issues with special characters
            String sanitizedStudentId = addStudentIdField.getText().replaceAll("[^a-zA-Z0-9]", "_");
            String sanitizedMedicalNo = medicalNoField.getText().replaceAll("[^a-zA-Z0-9]", "_");
            String uniqueFilename = System.currentTimeMillis() + "_" + sanitizedStudentId + "_" + sanitizedMedicalNo + ".pdf";
            Path targetPath = Paths.get(UPLOAD_DIR, uniqueFilename);

            // Log the source and target paths for debugging
            System.out.println("Copying file from: " + selectedFile.getAbsolutePath());
            System.out.println("Copying file to: " + targetPath.toAbsolutePath());

            // Verify the source file exists and is readable
            if (!selectedFile.exists()) {
                throw new IOException("Source file does not exist: " + selectedFile.getAbsolutePath());
            }
            if (!selectedFile.canRead()) {
                throw new IOException("Cannot read source file: " + selectedFile.getAbsolutePath());
            }

            // Perform the file copy
            Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Verify the target file was created
            if (!Files.exists(targetPath)) {
                throw new IOException("File copy failed: Target file does not exist after copy: " + targetPath.toAbsolutePath());
            }

            String weekRange = startWeekComboBox.getValue().equals(endWeekComboBox.getValue()) ?
                    startWeekComboBox.getValue().toString() : startWeekComboBox.getValue() + "-" + endWeekComboBox.getValue();

            Medical medical = new Medical(0, addCourseComboBox.getValue(), addStudentIdField.getText(), weekRange, uniqueFilename, medicalNoField.getText(), "pending", Date.valueOf(submissionDatePicker.getValue()));
            if (medical.createMedicalReport()) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Medical record added successfully.");
                clearAddMedicalForm();
                if (subjectComboBox.getValue() != null && weekComboBox.getValue() != null) {
                    loadMedicalSubmissions();
                }
            } else {
                throw new Exception("Failed to insert record into database.");
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "File Error", "Failed to copy medical report file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add medical record: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearAddMedicalForm() {
        addStudentIdField.clear();
        addCourseComboBox.setValue(null);
        startWeekComboBox.setValue(null);
        endWeekComboBox.setValue(null);
        medicalNoField.clear();
        medicalFilePathField.clear();
        selectedFile = null;
        submissionDatePicker.setValue(null);
    }

    private void setupTableColumns() {
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        medicalIdColumn.setCellValueFactory(new PropertyValueFactory<>("medicalNo"));
        weekRangeColumn.setCellValueFactory(new PropertyValueFactory<>("weekRange"));
        submissionDateColumn.setCellValueFactory(new PropertyValueFactory<>("submissionDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setupActionColumns() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button acceptButton = new Button("Accept");
            private final Button rejectButton = new Button("Reject");
            private final HBox buttonBox = new HBox(10, acceptButton, rejectButton);

            {
                acceptButton.getStyleClass().add("accept-button");
                rejectButton.getStyleClass().add("reject-button");

                acceptButton.setOnAction(e -> {
                    MedicalSubmission submission = getTableView().getItems().get(getIndex());
                    Medical medical = new Medical(submission.getMedicalId(), submission.getCourseCode(), submission.getStudentId(),
                            submission.getWeekRange(), submission.getFilePath(), submission.getMedicalNo(), "accepted", submission.getSubmissionDate());
                    if (medical.updateMedicalStatus("accepted")) {
                        submission.setStatus("accepted");
                        updateAttendanceForWeekRange(submission.getStudentId(), submission.getCourseCode(), submission.getWeekRange(), true);
                        studentTable.refresh();
                        loadMedicalSubmissions();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to accept medical record.");
                    }
                });

                rejectButton.setOnAction(e -> {
                    MedicalSubmission submission = getTableView().getItems().get(getIndex());
                    Medical medical = new Medical(submission.getMedicalId(), submission.getCourseCode(), submission.getStudentId(),
                            submission.getWeekRange(), submission.getFilePath(), submission.getMedicalNo(), "rejected", submission.getSubmissionDate());
                    if (medical.updateMedicalStatus("rejected")) {
                        submission.setStatus("rejected");
                        studentTable.refresh();
                        loadMedicalSubmissions();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to reject medical record.");
                    }
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
                acceptButton.setDisable("accepted".equals(submission.getStatus()));
                rejectButton.setDisable("rejected".equals(submission.getStatus()));
                setGraphic("pending".equals(submission.getStatus()) ? buttonBox : null);
            }
        });

        viewFileColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewButton = new Button("View");
            {
                viewButton.setOnAction(e -> openMedicalFile(getTableView().getItems().get(getIndex()).getFilePath()));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewButton);
            }
        });
    }

    private void updateAttendanceForWeekRange(String studentId, String courseCode, String weekRange, boolean isPresent) {
        String[] weeks = weekRange.contains("-") ? weekRange.split("-") : new String[]{weekRange};
        int startWeek = Integer.parseInt(weeks[0]);
        int endWeek = weeks.length > 1 ? Integer.parseInt(weeks[1]) : startWeek;

        for (int week = startWeek; week <= endWeek; week++) {
            attendance.updateAttendance(studentId, courseCode, week, isPresent);
        }
    }

    private void openMedicalFile(String filePath) {
        try {
            File file = new File(UPLOAD_DIR + filePath);
            if (!file.exists()) {
                showAlert(Alert.AlertType.ERROR, "File Error", "File does not exist: " + file.getAbsolutePath());
                return;
            }
            java.awt.Desktop.getDesktop().open(file);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupFilterButton() {
        filterButton.setOnAction(e -> {
            if (subjectComboBox.getValue() == null || weekComboBox.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Missing Selection", "Please select both a course and week.");
                studentTable.setItems(FXCollections.observableArrayList());
                return;
            }
            loadMedicalSubmissions();
        });
    }

    private void loadMedicalSubmissions() {
        if (subjectComboBox.getValue() == null || weekComboBox.getValue() == null) {
            studentTable.setItems(FXCollections.observableArrayList());
            return;
        }
        String course = subjectComboBox.getValue();
        int week = weekComboBox.getValue();
        ObservableList<MedicalSubmission> submissions = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT m.medicalId, m.courseCode, m.studentId, m.week, m.medicalReport, m.medicalNo, m.status, m.submissionDate, u.full_name " +
                             "FROM MEDICAL_REPORTS m JOIN USERS u ON m.studentId = u.username WHERE m.courseCode = ?")) {
            ps.setString(1, course);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String weekRange = rs.getString("week");
                    if (weekRange.contains("-")) {
                        String[] weeks = weekRange.split("-");
                        int start = Integer.parseInt(weeks[0]);
                        int end = Integer.parseInt(weeks[1]);
                        if (week >= start && week <= end) {
                            submissions.add(new MedicalSubmission(
                                    rs.getInt("medicalId"),
                                    rs.getString("studentId"),
                                    rs.getString("full_name"),
                                    rs.getString("courseCode"),
                                    weekRange,
                                    rs.getDate("submissionDate"),
                                    rs.getString("status"),
                                    rs.getString("medicalReport"),
                                    rs.getString("medicalNo")
                            ));
                        }
                    } else if (Integer.parseInt(weekRange) == week) {
                        submissions.add(new MedicalSubmission(
                                rs.getInt("medicalId"),
                                rs.getString("studentId"),
                                rs.getString("full_name"),
                                rs.getString("courseCode"),
                                weekRange,
                                rs.getDate("submissionDate"),
                                rs.getString("status"),
                                rs.getString("medicalReport"),
                                rs.getString("medicalNo")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load medical submissions: " + e.getMessage());
            studentTable.setItems(FXCollections.observableArrayList());
        }
        studentTable.setItems(submissions);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}