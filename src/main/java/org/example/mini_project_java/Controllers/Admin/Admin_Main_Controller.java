package org.example.mini_project_java.Controllers.Admin;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.example.mini_project_java.Models.Model;
import org.example.mini_project_java.Utils.MenuItems;

import java.net.URL;
import java.util.ResourceBundle;

public class Admin_Main_Controller implements Initializable {

    @FXML
    private BorderPane admin_parent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Model.getInstance().getViewFactory().getAdminSelectedMenueItem().addListener((obs, oldVal, newVal) -> {
            Node view = null;
            switch (newVal) {
                case MenuItems.DASHBOARD:
                    view = Model.getInstance().getViewFactory().getDashboardView();
                    break;
                case MenuItems.USER_PROFILE:
                    view = Model.getInstance().getViewFactory().getUserProfileView();
                    break;
                case MenuItems.COURSE:
                    view = Model.getInstance().getViewFactory().getCourseView();
                    break;
                case MenuItems.NOTICE:
                    view = Model.getInstance().getViewFactory().getNoticeView();
                    break;
                case MenuItems.TIMETABLE:
                    view = Model.getInstance().getViewFactory().getTimetableView();
                    break;
            }
            if (view != null) {
                admin_parent.setCenter(view);
            } else {
                System.err.println("View for " + newVal + " is not available.");
            }
        });
    }
}
