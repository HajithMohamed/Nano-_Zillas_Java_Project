package org.example.mini_project_java.Views;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ViewFactory {
    private final StringProperty adminSelectedMenuItem;
    private AnchorPane dashboardView;
    private AnchorPane userProfileView;
    private Node courseView;
    private Node timetableView;
    private Node noticeView;

    public ViewFactory() {
        this.adminSelectedMenuItem = new SimpleStringProperty("");
    }

    public StringProperty getAdminSelectedMenueItem() {
        return adminSelectedMenuItem;
    }


    public Node getDashboardView() {
        if (dashboardView == null) {
            try {
                dashboardView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Fxml/Admin/AdminDashbord.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to load AdminDashbord.fxml");
            }
        }
        return dashboardView;
    }


    public AnchorPane getUserProfileView() {

        if (userProfileView == null) {
            try {
                userProfileView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Fxml/Admin/User_Profiles.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
                showError("User profile view could not be loaded.");
                userProfileView = new AnchorPane();
            }
        }
        return userProfileView;
    }


    public Node getCourseView() {
        if (courseView == null) {
            try {
                courseView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Fxml/Admin/Corses.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to load Corses.fxml");
            }
        }
        return courseView;
    }


    public Node getTimetableView() {
        if (timetableView == null) {
            try {
                timetableView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Fxml/Admin/Admin_Time_Table.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to load Timetable.fxml");
            }
        }
        return timetableView;
    }

    public Node getNoticeView() {
        if (noticeView == null) {
            try {
                noticeView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Fxml/Admin/Admin_notice.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to load Notice.fxml");
            }
        }
        return noticeView;
    }
    public void showLoginWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
        createStage(loader);
    }

    public void showAdminWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/Admin.fxml"));
        createStage(loader);
    }

    private void createStage(FXMLLoader loader) {
        try {
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Window could not be loaded.");
        }
    }

    public void closeStage(Stage stage) {
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("View Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
