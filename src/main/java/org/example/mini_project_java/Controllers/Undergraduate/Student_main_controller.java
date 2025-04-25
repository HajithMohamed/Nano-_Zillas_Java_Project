package org.example.mini_project_java.Controllers.Undergraduate;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.example.mini_project_java.Models.Model;

import java.net.URL;
import java.util.ResourceBundle;

public class Student_main_controller implements Initializable {

    @FXML
    private BorderPane undergraduate_parent;

    @FXML
    private void handleDashboard() {
        undergraduate_parent.setCenter(Model.getInstance().getViewFactory().getUndergraduateDashboardView());
    }

    @FXML
    private void handleProfile() {
        undergraduate_parent.setCenter(Model.getInstance().getViewFactory().getUndergraduateProfileView());
    }

    @FXML
    private void handleCourses() {
        undergraduate_parent.setCenter(Model.getInstance().getViewFactory().getUndergraduateCoursesView());
    }

    @FXML
    private void handleNotices() {
        undergraduate_parent.setCenter(Model.getInstance().getViewFactory().getUndergraduateNoticesView());
    }

    @FXML
    private void handleTimetable() {
        undergraduate_parent.setCenter(Model.getInstance().getViewFactory().getUndergraduateTimetableView());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        handleDashboard(); // Default view
    }
}