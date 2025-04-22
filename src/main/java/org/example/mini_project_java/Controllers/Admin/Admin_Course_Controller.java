package org.example.mini_project_java.Controllers.Admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.example.mini_project_java.Database.DatabaseConnection;
import org.example.mini_project_java.Models.*;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Admin_Course_Controller implements Initializable {


    @FXML public TextField courseTitle;
    @FXML public TextField lecId;
    @FXML public TextField courseCredit;
    @FXML public TextField courseType;
    @FXML public TextField courseCreditHours;
    @FXML public Button addCourse;
    public TextField courseCode;

    public TableView <Courses> courseTable;
    public TableColumn <Courses,String>course_Title;
    public TableColumn <Courses,String> course_Code;
    public TableColumn <Courses,String> lec_Id;
    public TableColumn <Courses,String> course_Credit;
    public TableColumn <Courses,String> course_Type;
    public TableColumn <Courses,String> course_Credit_Hours;
    public TableColumn <Courses,String> Course_Action;

    private boolean isEditMode = false;
    private String courseBeingEdited = null;

    private final ObservableList<Courses> courseList = FXCollections.observableArrayList();

    private void loadCourseDataFromDatabase() {
        courseList.clear();
        try (Connection connectDB = DatabaseConnection.getConnection();
             Statement statement = connectDB.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM COURSE")) {

            while (resultSet.next()) {
                String courseCode = resultSet.getString("course_code");
                String courseTitle = resultSet.getString("course_title");
                String lecId = resultSet.getString("lecturer_id");
                int courseCredit = resultSet.getInt("course_credit");
                String courseType = resultSet.getString("course_type");
                int courseCreditHours = resultSet.getInt("credit_hours");

                courseList.add(new Courses(courseCode,courseTitle, lecId, courseCredit, courseType, courseCreditHours));
            }

        } catch (SQLException e) {
            showAlert("DB Error", "Error loading course data: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddOrEditCourse(ActionEvent event) {
        String courseCodeValue = courseCode.getText().trim();
        String courseTitleValue = courseTitle.getText().trim();
        String lecIdValue = lecId.getText().trim();
        String courseCreditValue = courseCredit.getText().trim();
        String courseTypeValue = courseType.getText().trim();
        String courseCreditHoursValue = courseCreditHours.getText().trim();

        // Validate input fields
        if (courseCodeValue.isEmpty() || courseTitleValue.isEmpty() || lecIdValue.isEmpty() || courseCreditValue.isEmpty() || courseTypeValue.isEmpty() || courseCreditHoursValue.isEmpty()) {
            showAlert("Validation Error", "Please fill in all fields.");
            return;
        }

        int courseCreditInt;
        int courseCreditHoursInt;
        try {
            courseCreditInt = Integer.parseInt(courseCreditValue);
            courseCreditHoursInt = Integer.parseInt(courseCreditHoursValue);
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Course Credit and Credit Hours must be numeric.");
            return;
        }

        Admin admin = new Admin();

        try {
            if (!isEditMode) {
                // Add new course
                admin.addOrEditCourse(courseCodeValue, courseTitleValue, lecIdValue, courseCreditInt, courseTypeValue, courseCreditHoursInt, false);
                showAlert("Success", "Course added successfully!");
            } else {
                // Edit existing course
                admin.addOrEditCourse(courseCodeValue, courseTitleValue, lecIdValue, courseCreditInt, courseTypeValue, courseCreditHoursInt, true);
                showAlert("Success", "Course updated successfully!");
                isEditMode = false;
                courseBeingEdited = null;
                addCourse.setText("Add Course");
            }

            // Clear the form
            clearCourseFields();
        } catch (Exception e) {
            if (e.getMessage().contains("Course code already exists")) {
                showAlert("Error", "The course code already exists. Please use a different course code.");
            } else {
                showAlert("Error", "An error occurred: " + e.getMessage());
            }
            e.printStackTrace();
        }
    }

    private void clearCourseFields() {
        courseCode.clear();
        courseTitle.clear();
        lecId.clear();
        courseCredit.clear();
        courseType.clear();
        courseCreditHours.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        course_Code.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        course_Title.setCellValueFactory(new PropertyValueFactory<>("courseTitle"));
        lec_Id.setCellValueFactory(new PropertyValueFactory<>("lectureId"));
        course_Credit.setCellValueFactory(new PropertyValueFactory<>("courseCredit"));
        course_Type.setCellValueFactory(new PropertyValueFactory<>("courseType"));
        course_Credit_Hours.setCellValueFactory(new PropertyValueFactory<>("courseCreditHours"));


        loadCourseDataFromDatabase();
        courseTable.setItems(courseList);

    }
}