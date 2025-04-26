package org.example.mini_project_java.Controllers.Technical_Officer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.mini_project_java.Models.Model;
import org.example.mini_project_java.Utils.techOfficerMenuItems; // <- We will create this Enum too.

import java.net.URL;
import java.util.ResourceBundle;

public class Technical_Officer_Menu_controller implements Initializable {

    @FXML
    private VBox TechOffPan;

    @FXML
    private Button techAttandence;

    @FXML
    private Button techMedical;

    @FXML
    private Button techDashboard;

    @FXML
    private Button techLogout;



    private void onMenuItemSelected(String menuItem) {
        Model.getInstance().getViewFactory().technicalOfficerSelectedMenuItemProperty().set(menuItem);
        System.out.println(menuItem + " selected");
    }

    private void handleLogout() {
        // Show login window again
        Model.getInstance().getViewFactory().showLoginWindow();

        // Close current window
        Stage stage = (Stage) TechOffPan.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (techDashboard != null)
            techDashboard.setOnAction(e -> onMenuItemSelected(techOfficerMenuItems.DASHBOARD));
        if (techAttandence != null)
            techAttandence.setOnAction(e -> onMenuItemSelected(techOfficerMenuItems.ATTENDANCE));
        if (techMedical != null)
            techMedical.setOnAction(e -> onMenuItemSelected(techOfficerMenuItems.MEDICAL));
        if (techLogout != null)
            techLogout.setOnAction(e -> handleLogout());
    }
}
