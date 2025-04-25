package org.example.mini_project_java.Views;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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

    // Undergraduate views
    private AnchorPane undergraduateDashboardView;
    private AnchorPane undergraduateProfileView;
    private Node undergraduateCoursesView;
    private Node undergraduateNoticesView;
    private Node undergraduateTimetableView;

    public ViewFactory() {
        this.adminSelectedMenuItem = new SimpleStringProperty("");
    }

    private void createStage(FXMLLoader loader) {
        try {
            Stage stage = new Stage();
            stage.setScene(new javafx.scene.Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Window could not be loaded.");
        }
    }

    public StringProperty getAdminSelectedMenueItem() {
        return adminSelectedMenuItem;
    }

    // --- Admin Views ---
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

    public void showUndergraduateWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Undergraduate/Undergraduate.fxml"));
        createStage(loader);
    }

    // --- Undergraduate Views ---
    public AnchorPane getUndergraduateDashboardView() {
        if (undergraduateDashboardView == null) {
            try {
                undergraduateDashboardView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Fxml/Undergraduate/UndergraduateDashboard.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to load UndergraduateDashboard.fxml");
            }
        }
        return undergraduateDashboardView;
    }

    public AnchorPane getUndergraduateProfileView() {
        if (undergraduateProfileView == null) {
            try {
                undergraduateProfileView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Fxml/Undergraduate/UndergraduateProfile.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to load UndergraduateProfile.fxml");
            }
        }
        return undergraduateProfileView;
    }

    public Node getUndergraduateCoursesView() {
        if (undergraduateCoursesView == null) {
            try {
                undergraduateCoursesView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Fxml/Undergraduate/UndergraduateCourses.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to load UndergraduateCourses.fxml");
            }
        }
        return undergraduateCoursesView;
    }

    public Node getUndergraduateNoticesView() {
        if (undergraduateNoticesView == null) {
            try {
                undergraduateNoticesView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Fxml/Undergraduate/UndergraduateNotices.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to load UndergraduateNotices.fxml");
            }
        }
        return undergraduateNoticesView;
    }

    public Node getUndergraduateTimetableView() {
        if (undergraduateTimetableView == null) {
            try {
                undergraduateTimetableView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Fxml/Undergraduate/UndergraduateTimetable.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to load UndergraduateTimetable.fxml");
            }
        }
        return undergraduateTimetableView;
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
