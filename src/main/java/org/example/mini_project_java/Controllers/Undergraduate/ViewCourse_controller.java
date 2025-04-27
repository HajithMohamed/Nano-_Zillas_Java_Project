package org.example.mini_project_java.Controllers.Undergraduate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.example.mini_project_java.Database.DatabaseConnection;
import org.example.mini_project_java.Models.Courses;
import org.example.mini_project_java.Models.Undergratuate;

import java.sql.*;
import java.util.List;

public class ViewCourse_controller {
    @FXML
    public AnchorPane studentCourse;

    @FXML
    public TableView<Courses> courseDetailsTable;

    @FXML
    public TableColumn<Courses, String> courseCodeColumn;

    @FXML
    public TableColumn<Courses, String> courseNameColumn;

    @FXML
    public TableColumn<Courses, String> lecturerIdColumn;
    public TableColumn courseNameColumn1;
    public TableColumn courseNameColumn11;

    private ObservableList<Courses> courseList = FXCollections.observableArrayList();

    private Undergratuate student;  // Correct spelling

    @FXML
    public void initialize() {
        setupTable();
        loadCourseData();  // Load course data when initialized
    }

    private void setupTable() {
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        courseNameColumn1.setCellValueFactory(new PropertyValueFactory<>("courseTitle"));
        courseNameColumn11.setCellValueFactory(new PropertyValueFactory<>("lecturerId"));
    }

    // Using your updated loadCourseData method
    private void loadCourseData() {
        courseList.clear();  // Clear any previous data

        try (Connection connectDB = DatabaseConnection.getConnection();
             PreparedStatement statement = connectDB.prepareStatement("SELECT * FROM COURSE");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                // Add each course into the observable list
                courseList.add(new Courses(
                        resultSet.getString("course_code"),
                        resultSet.getString("course_title"),
                        resultSet.getString("lecturer_id"),
                        resultSet.getInt("course_credit"),
                        resultSet.getString("course_type"),
                        resultSet.getInt("credit_hours")
                ));
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load course data: " + e.getMessage());
        }

        // Now set the course data into the TableView
        courseDetailsTable.setItems(courseList);
    }

    // If you want to show alerts in case of errors
    private void showAlert(String title, String message) {
        // You can use JavaFX Alert class here to show error messages
        System.out.println(title + ": " + message);
    }

    // This method sets the student for the view if required, or you can pass the student through the constructor
    public void setStudent(Undergratuate student) {
        this.student = student;
    }
}
