package org.example.mini_project_java.Controllers.Lecturer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.mini_project_java.Models.Model;
import org.example.mini_project_java.Utils.LectureMenuItem;

import java.net.URL;
import java.util.ResourceBundle;

public class LecturerPannelController implements Initializable {

    @FXML
    private BorderPane lecture_parent;

    @FXML
    private Button lecturerProfile;

    @FXML
    private Button manageCourses;

    @FXML
    private Button uploadMaterials;

    @FXML
    private Button uploadMarks;

    @FXML
    private Button undergraduateDetails;

    @FXML
    private Button eligibility;

    @FXML
    private Button marksGradesGPA;

    @FXML
    private Button attendanceMedical;

    @FXML
    private Button logout;

    private void addListeners() {
        if (lecturerProfile != null)
            lecturerProfile.setOnAction(e -> onMenuItemSelected(LectureMenuItem.DASHBOARD));
        if (manageCourses != null)
            manageCourses.setOnAction(e -> onMenuItemSelected(LectureMenuItem.COURSES));
        if (uploadMaterials != null)
            uploadMaterials.setOnAction(e -> onMenuItemSelected(LectureMenuItem.MATERIALS));
        if (uploadMarks != null)
            uploadMarks.setOnAction(e -> onMenuItemSelected(LectureMenuItem.MARKS));
        if (undergraduateDetails != null)
            undergraduateDetails.setOnAction(e -> onMenuItemSelected(LectureMenuItem.UNDERGRADUATE_DETAILS));
        if (eligibility != null)
            eligibility.setOnAction(e -> onMenuItemSelected(LectureMenuItem.ELIGIBILITY));
        if (marksGradesGPA != null)
            marksGradesGPA.setOnAction(e -> onMenuItemSelected(LectureMenuItem.GRADES));
        if (attendanceMedical != null)
            attendanceMedical.setOnAction(e -> onMenuItemSelected(LectureMenuItem.MEDICAL));
        if (logout != null)
            logout.setOnAction(e -> handleLogout());
    }

    private void onMenuItemSelected(String menuItem) {
        Model.getInstance().getViewFactory().lectureSelectedMenuItemProperty().set(menuItem);
        System.out.println(menuItem + " selected");
    }

    private void handleLogout() {
        Model.logout();
        javafx.stage.Stage stage = (javafx.stage.Stage) lecture_parent.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addListeners();
    }
}
