package org.example.mini_project_java.Controllers.Admin;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.example.mini_project_java.Models.Model;
import org.example.mini_project_java.Utils.MenuItems;

import java.net.URL;
import java.util.ResourceBundle;

public class Admin_Controller implements Initializable {

    @FXML
    private Button userProfile;

    @FXML
    private Button course;

    @FXML
    private Button notice;

    @FXML
    private Button timetable;

    @FXML
    private Button dashboard;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addListeners();
    }

    private void addListeners() {
        if (dashboard != null)
            dashboard.setOnAction(event -> onMenuItemSelected(MenuItems.DASHBOARD));
        if (userProfile != null)
            userProfile.setOnAction(event -> onMenuItemSelected(MenuItems.USER_PROFILE));
        if (course != null)
            course.setOnAction(event -> onMenuItemSelected(MenuItems.COURSE));
        if (notice != null)
            notice.setOnAction(event -> onMenuItemSelected(MenuItems.NOTICE));
        if (timetable != null)
            timetable.setOnAction(event -> onMenuItemSelected(MenuItems.TIMETABLE));
    }

    private void onMenuItemSelected(String menuItem) {
        Model.getInstance().getViewFactory().getAdminSelectedMenueItem().set(menuItem);
        System.out.println(menuItem + " selected");
    }
}