package org.example.mini_project_java.Views;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ViewFactory {

    private final StringProperty adminSelectedMenuItem;
    private final StringProperty studentSelectedMenuItem;

    // Admin views
    private AnchorPane dashboardView;
    private AnchorPane userProfileView;
    private AnchorPane timeTableView;
    private AnchorPane noticeView;
    private AnchorPane courseView;

    // Undergraduate views
    private AnchorPane undergraduateCoursesView;
    private AnchorPane undergraduateGradesView;
    private AnchorPane undergraduateAttendanceView;
    private AnchorPane undergraduateDashboardView;
    private AnchorPane undergraduateMedicalView;
    private AnchorPane UndergraduateTimeTableView;

    public ViewFactory() {
        this.studentSelectedMenuItem = new SimpleStringProperty("");
        this.adminSelectedMenuItem = new SimpleStringProperty("");
    }

    // -------- Student MenuItem Methods --------

    public StringProperty studentSelectedMenuItemProperty() {
        return studentSelectedMenuItem;
    }

    // -------- Admin MenuItem Methods --------
    public StringProperty getAdminSelectedMenuItem() {
        return adminSelectedMenuItem;
    }



    // -------- Admin Views --------
    public AnchorPane getDashboardView() {
        return getDashboardView(false);
    }

    public AnchorPane getDashboardView(boolean reload) {
        if (dashboardView == null || reload) {
            dashboardView = loadView("/Fxml/Admin/AdminDashbord.fxml", "Dashboard view could not be loaded.");
        }
        return dashboardView;
    }

    public AnchorPane getUserProfileView() {
        return getUserProfileView(false);
    }

    public AnchorPane getUserProfileView(boolean reload) {
        if (userProfileView == null || reload) {
            userProfileView = loadView("/Fxml/Admin/User_Profiles.fxml", "User profile view could not be loaded.");
        }
        return userProfileView;
    }

    public AnchorPane getNoticeView() {
        return getNoticeView(false);
    }

    public AnchorPane getNoticeView(boolean reload) {
        if (noticeView == null || reload) {
            noticeView = loadView("/Fxml/Admin/Admin_notice.fxml", "Notice view could not be loaded.");
        }
        return noticeView;
    }

    public AnchorPane getTimeTableView() {
        return getTimeTableView(false);
    }

    public AnchorPane getTimeTableView(boolean reload) {
        if (timeTableView == null || reload) {
            timeTableView = loadView("/Fxml/Admin/Admin_Time_Table.fxml", "Time Table view could not be loaded.");
        }
        return timeTableView;
    }

    public AnchorPane getCourseView() {
        return getCourseView(false);
    }

    public AnchorPane getCourseView(boolean reload) {
        if (courseView == null || reload) {
            courseView = loadView("/Fxml/Admin/Corses.fxml", "Courses view could not be loaded.");
        }
        return courseView;
    }

    // -------- Undergraduate Views --------
    public AnchorPane getUndergraduateCoursesView() {
        return getUndergraduateCoursesView(false);
    }

    public AnchorPane getUndergraduateCoursesView(boolean reload) {
        if (undergraduateCoursesView == null || reload) {
            undergraduateCoursesView = loadView("/Fxml/Student/Viewcourse.fxml", "Courses view could not be loaded.");
        }
        return undergraduateCoursesView;
    }

    public AnchorPane getUndergraduateGradesView() {
        return getUndergraduateGradesView(false);
    }

    public AnchorPane getUndergraduateGradesView(boolean reload) {
        if (undergraduateGradesView == null || reload) {
            undergraduateGradesView = loadView("/Fxml/Student/Viewgrade.fxml", "Grades view could not be loaded.");
        }
        return undergraduateGradesView;
    }

    public AnchorPane getUndergraduateAttendanceView() {
        return getUndergraduateAttendanceView(false);
    }

    public AnchorPane getUndergraduateAttendanceView(boolean reload) {
        if (undergraduateAttendanceView == null || reload) {
            undergraduateAttendanceView = loadView("/Fxml/Student/Attendance.fxml", "Attendance view could not be loaded.");
        }
        return undergraduateAttendanceView;
    }

    public AnchorPane getUndergraduateDashboardView() {
        return getUndergraduateDashboardView(false);
    }

    public AnchorPane getUndergraduateDashboardView(boolean reload) {
        if (undergraduateDashboardView == null || reload) {
            undergraduateDashboardView = loadView("/Fxml/Student/StudentDAshboard.fxml", "Dashboard view could not be loaded.");
        }
        return undergraduateDashboardView;
    }

    public AnchorPane getUndergraduateMedicalView() {
        return getUndergraduateMedicalView(false);
    }

    public AnchorPane getUndergraduateMedicalView(boolean reload) {
        if (undergraduateMedicalView == null || reload) {
            undergraduateMedicalView = loadView("/Fxml/Student/viewMedical.fxml", "Medical view could not be loaded.");
        }
        return undergraduateMedicalView;
    }

    public AnchorPane getUndergraduateTimeTableView() {
        return getUndergraduateTimeTableView(false);
    }

    public AnchorPane getUndergraduateTimeTableView(boolean reload) {
        if (UndergraduateTimeTableView == null || reload) {
            UndergraduateTimeTableView = loadView("/Fxml/Student/Viewtimetable.fxml", "Time Table view could not be loaded.");
        }
        return UndergraduateTimeTableView;
    }

    // -------- Window Display Methods --------
    public void showLoginWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
        createStage(loader);
    }

    public void showAdminWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/Admin.fxml"));
        createStage(loader);
    }

    public void showUndergraduateWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Student/student.fxml"));
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
        if (stage != null) {
            stage.close();
        }
    }

    // -------- Helper Methods --------
    private AnchorPane loadView(String fxmlPath, String errorMessage) {
        try {
            return FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
        } catch (IOException e) {
            e.printStackTrace();
            showError(errorMessage);
            return new AnchorPane(); // Return empty fallback view
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("View Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
