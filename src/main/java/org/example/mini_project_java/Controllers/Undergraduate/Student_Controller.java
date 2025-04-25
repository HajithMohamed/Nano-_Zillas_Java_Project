package org.example.mini_project_java.Controllers.Undergraduate;

import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.example.mini_project_java.Models.Model;
import org.example.mini_project_java.Utils.studentMenuItem;

import java.net.URL;
import java.util.ResourceBundle;

public class Student_Controller implements Initializable {
    public BorderPane student_parent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Model.getInstance().getViewFactory().getAdminSelectedMenuItem().addListener((obs, oldVal, newVal) -> {
            Node view = null;
            switch (newVal) {
                case studentMenuItem.DASHBOARD:
                    view = Model.getInstance().getViewFactory().getUndergraduateDashboardeView();
                    break;
                case studentMenuItem.COURSE:
                    view = Model.getInstance().getViewFactory().getUndergraduateCoursesView();
                    break;
                case studentMenuItem.GRADES:
                    view = Model.getInstance().getViewFactory().getUndergraduateGradesView();
                    break;
                case studentMenuItem.ATTENDANCE:
                    view = Model.getInstance().getViewFactory().getUndergraduateAttendanceView();
                    break;
                case studentMenuItem.TIMETABLE:
                    view = Model.getInstance().getViewFactory().getUndergraduateTimeTableView();
                    break;
                case studentMenuItem.MEDICAL:
                    view = Model.getInstance().getViewFactory().getUndergraduateMedicalView();
                    break;
            }
            if (view != null) {
                student_parent.setCenter(view);
            } else {
                System.err.println("View for " + newVal + " is not available.");
            }
        });
    }
}
