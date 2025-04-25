package org.example.mini_project_java.Controllers.Undergraduate;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.mini_project_java.Models.Model;
import org.example.mini_project_java.Utils.studentMenuItem;

public class Stusdent_Menue_Controller {

    @FXML private Button studentProfile;
    @FXML private Button studentAttendance;
    @FXML private Button studentCourse;
    @FXML private Button studentMedical;
    @FXML private Button studentGrade;
    @FXML private Button studentTimeTable;
    @FXML private Button studentLogout;

    @FXML
    private void initialize() {
        addListeners();
    }

    private void addListeners() {
        if (studentProfile != null)
            studentProfile.setOnAction(e -> onMenuItemSelected(studentMenuItem.DASHBOARD));
        if (studentGrade != null)
            studentGrade.setOnAction(e -> onMenuItemSelected(studentMenuItem.GRADES));
        if (studentAttendance != null)
            studentAttendance.setOnAction(e -> onMenuItemSelected(studentMenuItem.ATTENDANCE));
        if (studentCourse != null)
            studentCourse.setOnAction(e -> onMenuItemSelected(studentMenuItem.COURSE));
        if (studentMedical != null)
            studentMedical.setOnAction(e -> onMenuItemSelected(studentMenuItem.MEDICAL));
        if (studentTimeTable != null)
            studentTimeTable.setOnAction(e -> onMenuItemSelected(studentMenuItem.TIMETABLE));
    }

    private void onMenuItemSelected(String menuItem) {
        Model.getInstance().getViewFactory().getAdminSelectedMenuItem().set(menuItem);
        System.out.println(menuItem + " selected");
    }
}
