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
import org.example.mini_project_java.Models.Admin;
import org.example.mini_project_java.Models.Courses;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Admin_Course_Controller implements Initializable {

    @FXML
    public TextField courseCode;
    @FXML
    public TextField courseTitle;
    @FXML
    public TextField lecId;
    @FXML
    public TextField courseCredit;
    @FXML
    public TextField courseType;
    @FXML
    public TextField courseCreditHours;
    @FXML
    public Button addCourse;

    @FXML
    private TableView<Courses> course_Table;

    @FXML
    private TableColumn<Courses, String> Course_Code;

    @FXML
    private TableColumn<Courses, String> Course_Title;

    @FXML
    private TableColumn<Courses, Void> Course_Action;
    @FXML
    public TableColumn<Courses, String> Lecture_Id;
    @FXML
    public TableColumn<Courses, Integer> Course_Credit;
    @FXML
    public TableColumn<Courses, String> Course_Type;
    @FXML
    public TableColumn<Courses, Integer> Course_Credit_Hours;

    private final ObservableList<Courses> courseData = FXCollections.observableArrayList();
    private Courses courseBeingEdited = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Course_Code.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        Course_Title.setCellValueFactory(new PropertyValueFactory<>("courseTitle"));
        Lecture_Id.setCellValueFactory(new PropertyValueFactory<>("lectureId"));
        Course_Credit.setCellValueFactory(new PropertyValueFactory<>("courseCredit"));
        Course_Type.setCellValueFactory(new PropertyValueFactory<>("courseType"));
        Course_Credit_Hours.setCellValueFactory(new PropertyValueFactory<>("courseCreditHours"));

        loadCourseData();
        addDeleteButtonToTable();
        course_Table.setItems(courseData);
        addCourse.setOnAction(this::handleAddOrUpdateCourse);
    }

    private void handleAddOrUpdateCourse(ActionEvent event) {
        String courseCodeText = courseCode.getText().trim();
        String courseTitleText = courseTitle.getText().trim();
        String lecturerId = lecId.getText().trim();
        String creditText = courseCredit.getText().trim();
        String type = courseType.getText().trim();
        String creditHoursText = courseCreditHours.getText().trim();

        if (courseCodeText.isEmpty() || courseTitleText.isEmpty() || lecturerId.isEmpty()
                || creditText.isEmpty() || type.isEmpty() || creditHoursText.isEmpty()) {
            showAlert("Validation Error", "Please fill all fields.");
            return;
        }

        int credit, creditHours;
        try {
            credit = Integer.parseInt(creditText);
            creditHours = Integer.parseInt(creditHoursText);
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Credit and Credit Hours must be numbers.");
            return;
        }

        Admin admin = new Admin();

        try {
            if (courseBeingEdited == null) {
                if (!admin.courseExists(courseCodeText)) {
                    admin.addOrEditCourse(courseCodeText, courseTitleText, lecturerId, credit, type, creditHours, false);
                    showAlert("Success", "Course added successfully.");
                } else {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Course already exists. Update it?", ButtonType.YES, ButtonType.NO);
                    confirm.setTitle("Confirm Update");
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            try {
                                admin.addOrEditCourse(courseCodeText, courseTitleText, lecturerId, credit, type, creditHours, true);
                                showAlert("Success", "Course updated successfully.");
                                loadCourseData();
                            } catch (SQLException e) {
                                showAlert("Error", "Failed to update course: " + e.getMessage());
                            }
                        }
                    });
                    return;
                }
            } else {
                admin.addOrEditCourse(courseCodeText, courseTitleText, lecturerId, credit, type, creditHours, true);
                showAlert("Success", "Course updated successfully.");
                courseBeingEdited = null;
                addCourse.setText("Add Course");
            }

            loadCourseData();
            clearFields();

        } catch (SQLException e) {
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    private void loadCourseData() {
        courseData.clear();
        try (Connection connectDB = DatabaseConnection.getConnection();
             PreparedStatement statement = connectDB.prepareStatement("SELECT * FROM COURSE");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                courseData.add(new Courses(
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
    }



    private void clearFields() {
        courseCode.clear();
        courseTitle.clear();
        lecId.clear();
        courseCredit.clear();
        courseType.clear();
        courseCreditHours.clear();
    }

    private void addDeleteButtonToTable() {
        Course_Action.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            private final Button editButton = new Button("Edit");
            private final HBox actionButtons = new HBox(10, editButton, deleteButton);

            {
                deleteButton.setOnAction(event -> {
                    Courses course = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure you want to delete course: " + course.getCourseCode() + "?",
                            ButtonType.YES, ButtonType.NO);
                    confirm.setTitle("Confirm Delete");

                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            try {
                                new Admin().deleteCourse(course.getCourseCode());
                                showAlert("Success", "Course deleted successfully.");
                                loadCourseData();
                            } catch (SQLException e) {
                                showAlert("Error", "Failed to delete course: " + e.getMessage());
                            }
                        }
                    });
                });

                editButton.setOnAction(event -> {
                    Courses course = getTableView().getItems().get(getIndex());
                    courseCode.setText(course.getCourseCode());
                    courseTitle.setText(course.getCourseTitle());
                    lecId.setText(course.getLectureId());
                    courseCredit.setText(String.valueOf(course.getCourseCredit()));
                    courseType.setText(course.getCourseType());
                    courseCreditHours.setText(String.valueOf(course.getCourseCreditHours()));
                    courseBeingEdited = course;
                    addCourse.setText("Update Course");
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionButtons);
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
