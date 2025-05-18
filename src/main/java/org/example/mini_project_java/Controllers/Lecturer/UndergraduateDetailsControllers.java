
package org.example.mini_project_java.Controllers.Lecturer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.mini_project_java.Database.DatabaseConnection;
import org.example.mini_project_java.Models.Undergratuate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UndergraduateDetailsControllers {

    @FXML
    private TableView<Undergratuate> studentTable;

    @FXML
    private TableColumn<Undergratuate, String> studentIdColumn;

    @FXML
    private TableColumn<Undergratuate, String> studentNameColumn;

    @FXML
    private TableColumn<Undergratuate, String> emailColumn;

    @FXML
    private TableColumn<Undergratuate, String> courseColumn;

    @FXML
    private TextField stu_id_field;

    @FXML
    private Button searchbtn;

    private ObservableList<Undergratuate> studentList;

    @FXML
    public void initialize() {
        // Initialize TableView columns
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("mobileNo")); // This will display mobile number

        // Initialize student list
        studentList = FXCollections.observableArrayList();
        studentTable.setItems(studentList);

        // Load all students initially
        loadAllStudents();

        // Set up search button action
        searchbtn.setOnAction(event -> searchStudent());
    }

    private void loadAllStudents() {
        studentList.clear();
        String query = "SELECT username, full_name, email, contact_number FROM USERS WHERE role = 'Student'";

        try (Connection connectDB = DatabaseConnection.getConnection();
             PreparedStatement statement = connectDB.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Undergratuate student = new Undergratuate();
                student.setUsername(resultSet.getString("username"));
                student.setName(resultSet.getString("full_name"));
                student.setEmail(resultSet.getString("email"));
                student.setMobileNo(resultSet.getString("contact_number"));
                studentList.add(student);
            }

        } catch (SQLException e) {
            System.out.printf("SQLException: %s%n", e.getMessage());
            showAlert("Error", "Failed to load student details: " + e.getMessage());
        }
    }

    private void searchStudent() {
        String studentId = stu_id_field.getText().trim();

        if (studentId.isEmpty()) {
            loadAllStudents(); // If search field is empty, load all students
            return;
        }

        studentList.clear();
        String query = "SELECT username, full_name, email, contact_number FROM USERS WHERE role = 'Student' AND username LIKE ?";

        try (Connection connectDB = DatabaseConnection.getConnection();
             PreparedStatement statement = connectDB.prepareStatement(query)) {

            statement.setString(1, "%" + studentId + "%"); // Using LIKE for partial matching
            ResultSet resultSet = statement.executeQuery();

            boolean found = false;
            while (resultSet.next()) {
                found = true;
                Undergratuate student = new Undergratuate();
                student.setUsername(resultSet.getString("username"));
                student.setName(resultSet.getString("full_name"));
                student.setEmail(resultSet.getString("email"));
                student.setMobileNo(resultSet.getString("contact_number"));
                studentList.add(student);
            }

            if (!found) {
                showAlert("Info", "No student found with ID containing: " + studentId);
            }

        } catch (SQLException e) {
            System.out.printf("SQLException: %s%n", e.getMessage());
            showAlert("Error", "Failed to search student: " + e.getMessage());
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