package org.example.mini_project_java.Controllers.Lecturer;

import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.example.mini_project_java.Models.Model;
import org.example.mini_project_java.Utils.LectureMenuItem;

import java.net.URL;
import java.util.ResourceBundle;

public class LectureController implements Initializable {
    public BorderPane lecture_parent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Model.getInstance().getViewFactory().lectureSelectedMenuItemProperty().addListener((obs, oldVal, newVal) -> {
            Node view = null;
            switch (newVal) {
                case LectureMenuItem.DASHBOARD:
                    view = Model.getInstance().getViewFactory().getLectureDashboardView();
                    break;
                case LectureMenuItem.COURSES:
                    view = Model.getInstance().getViewFactory().getLectureCourseView();
                    break;
                case LectureMenuItem.MATERIALS:
                    view = Model.getInstance().getViewFactory().getLectureCourseMaterialView();
                    break;
                case LectureMenuItem.MARKS:
                    view = Model.getInstance().getViewFactory().getLectureMarksView();
                    break;
                case LectureMenuItem.UNDERGRADUATE_DETAILS:
                    view = Model.getInstance().getViewFactory().getLectureUndergraduateDetailsView();
                    break;
                case LectureMenuItem.ELIGIBILITY:
                    view = Model.getInstance().getViewFactory().getLectureEligibilityView();
                    break;
                case LectureMenuItem.GRADES:
                    view = Model.getInstance().getViewFactory().getLectureGradeView();
                    break;
                case LectureMenuItem.MEDICAL:
                    view = Model.getInstance().getViewFactory().getLectureMedicalView();
                    break;
            }
            if (view != null) {
                lecture_parent.setCenter(view);
            } else {
                System.err.println("View for " + newVal + " is not available.");
            }
        });
    }
}