package org.example.mini_project_java.Controllers.Undergraduate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.example.mini_project_java.Models.Courses;
import org.example.mini_project_java.Models.Undergraduate;

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

    private Undergraduate student; // Correct spelling

    @FXML
    public void initialize() {
        setupTable();
    }

    private void setupTable() {
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseTitle"));
        lecturerIdColumn.setCellValueFactory(new PropertyValueFactory<>("lecturerId"));
    }

    private void loadCourses() {
        if (student != null) {
            List<Courses> courses = student.viewCourseDetails();
            courseList.setAll(courses);
            courseDetailsTable.setItems(courseList);
        }
    }

    public void setStudent(Undergraduate student) {
        this.student = student;
        loadCourses();
    }
}
