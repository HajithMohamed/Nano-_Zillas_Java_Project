package org.example.mini_project_java.Controllers.Lecturer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.example.mini_project_java.Models.CourseMaterials;
import java.io.File;
import java.util.List;

public class ModifyCourseController {

    @FXML
    private ComboBox<String> courseComboBox;

    @FXML
    private ComboBox<String> weekComboBox;

    @FXML
    private TextField materialTextField;

    @FXML
    private Button addMaterialButton;

    @FXML
    private Button browseButton;

    @FXML
    private TableView<CourseMaterials> materialTableView;

    @FXML
    private TableColumn<CourseMaterials, String> weekColumn;

    @FXML
    private TableColumn<CourseMaterials, String> materialColumn;

    private final String loggedInLecturer = "LEC/RUH/TEC/001"; // Replace with dynamic session

    @FXML
    public void initialize() {
        // Set week options
        ObservableList<String> weeks = FXCollections.observableArrayList();
        for (int i = 1; i <= 15; i++) {
            weeks.add("Week " + i);
        }
        weekComboBox.setItems(weeks);
        weekComboBox.setValue("Week 1");

        // Load courses for lecturer
        List<String> courses = CourseMaterials.fetchCoursesByLecturer(loggedInLecturer);
        courseComboBox.setItems(FXCollections.observableArrayList(courses));
        if (!courses.isEmpty()) {
            courseComboBox.setValue(courses.get(0));
            loadMaterials(courses.get(0));
        }

        // Configure table columns
        weekColumn.setCellValueFactory(new PropertyValueFactory<>("week"));
        materialColumn.setCellValueFactory(new PropertyValueFactory<>("courseMaterial"));

        materialColumn.setCellFactory(column -> new TableCell<CourseMaterials, String>() {
            private final Hyperlink link = new Hyperlink();
            {
                link.setOnAction(event -> {
                    CourseMaterials material = getTableView().getItems().get(getIndex());
                    try {
                        java.awt.Desktop.getDesktop().open(new File(material.getCourseMaterial()));
                    } catch (Exception e) {
                        showAlert("Error", "Failed to open PDF: " + e.getMessage());
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    link.setText(new File(item).getName());
                    setGraphic(link);
                }
            }
        });

        courseComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadMaterials(newVal);
            }
        });
    }

    private void loadMaterials(String courseCode) {
        List<CourseMaterials> materials = CourseMaterials.fetchMaterialsByCourseCode(courseCode);
        materialTableView.setItems(FXCollections.observableArrayList(materials));
    }

    @FXML
    private void handleAddMaterialAction(ActionEvent event) {
        String courseCode = courseComboBox.getValue();
        String week = weekComboBox.getValue();
        String materialPath = materialTextField.getText().trim();

        if (courseCode == null || week == null || materialPath.isEmpty()) {
            showAlert("Validation Error", "Please select a course, week, and browse a PDF file.");
            return;
        }

        if (!materialPath.toLowerCase().endsWith(".pdf")) {
            showAlert("Validation Error", "Only PDF files are allowed.");
            return;
        }

        CourseMaterials material = new CourseMaterials(courseCode, week, materialPath, "PDF");
        material.save();
        loadMaterials(courseCode);
        materialTextField.clear();
        showAlert("Success", "Material added successfully.");
    }

    @FXML
    private void handleBrowseAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            materialTextField.setText(file.getAbsolutePath());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
