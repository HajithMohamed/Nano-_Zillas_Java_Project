package org.example.mini_project_java.Controllers.Undergraduate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.AnchorPane;
import org.example.mini_project_java.Models.Medical;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class viewMedical_controller {

    @FXML
    private AnchorPane studentMedical;

    @FXML
    private TableView<Medical> medicalRecordsTable;

    @FXML
    private TableColumn<Medical, String> courseCodeColumn;

    @FXML
    private TableColumn<Medical, String> studentIdColumn;

    @FXML
    private TableColumn<Medical, String> submissionDateColumn;

    @FXML
    private TableColumn<Medical, String> descriptionColumn;

    @FXML
    private TableColumn<Medical, String> weekColumn;

    @FXML
    private TableColumn<Medical, String> medicalNoColumn;

    @FXML
    private TableColumn<Medical, Hyperlink> medicalReportColumn;

    private final String loggedInStudentId = "TG/2022/1414"; // Hardcoded student ID
    private static final String BASE_PDF_PATH = "uploads/medical_reports/"; // Relative path from project root

    @FXML
    public void initialize() {
        // Configure table columns
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        submissionDateColumn.setCellValueFactory(new PropertyValueFactory<>("submissionDate"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        weekColumn.setCellValueFactory(new PropertyValueFactory<>("week"));
        medicalNoColumn.setCellValueFactory(new PropertyValueFactory<>("medicalNo"));

        // Configure medicalReportColumn to display clickable Hyperlink
        medicalReportColumn.setCellValueFactory(cellData -> {
            String fileName = cellData.getValue().getMedicalReport();
            Hyperlink link = new Hyperlink("View PDF");
            link.setUserData(fileName);
            link.setOnAction(event -> openPdfFile(fileName));
            return new javafx.beans.property.SimpleObjectProperty<>(link);
        });

        // Initialize table with medical records
        initializeTable();
    }

    private void initializeTable() {
        Medical medical = new Medical(0, "", loggedInStudentId, "", "", "", "");
        List<Medical> medicalReports = medical.viewMedicalReportByStudentId();
        ObservableList<Medical> data = FXCollections.observableArrayList(medicalReports);
        medicalRecordsTable.setItems(data);
    }

    private void openPdfFile(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            try {
                // Get the absolute path based on the project root
                Path basePath = Paths.get("").toAbsolutePath().resolve(BASE_PDF_PATH);
                String fullPath = basePath.resolve(fileName).toString();
                System.out.println("Attempting to open PDF at: " + fullPath); // Debug log
                File pdfFile = new File(fullPath);
                if (pdfFile.exists()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    System.err.println("PDF file not found: " + fullPath);
                    showAlert("File Not Found", "The PDF file at " + fullPath + " could not be found.");
                }
            } catch (IOException e) {
                System.err.println("Error opening PDF file: " + e.getMessage());
                e.printStackTrace();
                showAlert("Error", "Failed to open PDF file: " + e.getMessage());
            }
        } else {
            System.err.println("Invalid PDF file name");
            showAlert("Invalid Path", "The PDF file name is invalid or empty.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}