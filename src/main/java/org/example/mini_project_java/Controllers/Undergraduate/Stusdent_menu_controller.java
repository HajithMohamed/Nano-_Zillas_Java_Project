package org.example.mini_project_java.Controllers.Undergraduate;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.mini_project_java.Models.Model;
import org.example.mini_project_java.Utils.studentMenuItem;

import java.net.URL;
import java.util.ResourceBundle;

public class Stusdent_menu_controller implements Initializable {

    @FXML
    private BorderPane student_parent;

    @FXML
    private Button studentProfile;

    @FXML
    private Button studentAttendance;

    @FXML
    private Button studentCourse;

    @FXML
    private Button studentMedical;

    @FXML
    private Button studentGrade;

    @FXML
    private Button studentTimeTable;

    @FXML
    private Button studentLogout;




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
        // Set the selected menu item so Student_Controller listens and loads the view
        Model.getInstance().getViewFactory().studentSelectedMenuItemProperty().set(menuItem);
        System.out.println(menuItem + " selected");
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addListeners();
    }
}
