package org.example.mini_project_java.Controllers.Technical_Officer;

import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.example.mini_project_java.Models.Model;
import org.example.mini_project_java.Utils.techOfficerMenuItems;

import java.net.URL;
import java.util.ResourceBundle;

public class Technical_officer_controller implements Initializable {

    public BorderPane technical_officer_parent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Model.getInstance().getViewFactory().technicalOfficerSelectedMenuItemProperty().addListener((obs, oldVal, newVal) -> {
            Node view = null;
            switch (newVal) {
                case techOfficerMenuItems.DASHBOARD:
                    view = Model.getInstance().getViewFactory().getTechnicalOfficerDashboardView();
                    break;
                case techOfficerMenuItems.ATTENDANCE:
                    view = Model.getInstance().getViewFactory().getTechnicalOfficerAttendanceView();
                    break;
                case techOfficerMenuItems.MEDICAL:
                    view = Model.getInstance().getViewFactory().getTechnicalOfficerMedicalView();
                    break;
            }
            if (view != null) {
                technical_officer_parent.setCenter(view);
            } else {
                System.err.println("View for " + newVal + " is not available.");
            }
        });
    }
}
