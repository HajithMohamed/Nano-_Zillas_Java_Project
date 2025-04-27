package org.example.mini_project_java.Controllers.Undergraduate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.example.mini_project_java.Models.Courses;
import org.example.mini_project_java.Models.Student;

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

    private ObservableList<Courses> courseList = FXCollections.observableArrayList();

    private Student student; // create a student object

    @FXML
    public void initialize() {
        student = new Student(); // or pass the logged-in student if you have
        setupTable();
        loadCourses();
    }

    private void setupTable() {
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseTitle"));
        lecturerIdColumn.setCellValueFactory(new PropertyValueFactory<>("lecturerId"));
    }

    private void loadCourses() {
        List<Courses> courses = student.viewCourseDetails();
        courseList.setAll(courses);
        courseDetailsTable.setItems(courseList);
    }
}
