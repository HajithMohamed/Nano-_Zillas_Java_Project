package org.example.mini_project_java.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ViewFactory {

    // Admin Views
    private AnchorPane adminDashboardView;
    private AnchorPane adminProfileView;
    private Node adminUsersView;
    private Node adminReportsView;

    // Undergraduate Views
    private AnchorPane undergraduateDashboardView;
    private AnchorPane undergraduateProfileView;
    private Node undergraduateCoursesView;
    private Node undergraduateNoticesView;
    private Node undergraduateTimetableView;

    // Method to create a new stage
    private void createStage(FXMLLoader loader) {
        try {
            Stage stage = new Stage();
            stage.setScene(new javafx.scene.Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create stage.");
        }
    }

    // Admin Methods
    public void showAdminWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/Admin.fxml"));
        createStage(loader);
    }

    public Node getAdminDashboardView() {
        if (adminDashboardView == null) {
            try {
                adminDashboardView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Fxml/Admin/AdminDashboard.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to load AdminDashboard.fxml");
            }
        }
        return adminDashboardView;
    }

    public AnchorPane getAdminProfileView() {
        if (adminProfileView == null) {
            try {
                adminProfileView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Fxml/Admin/AdminProfile.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to load AdminProfile.fxml");
            }
        }
        return adminProfileView;
    }

    public Node getAdminUsersView() {
        if (adminUsersView == null) {
            try {
                adminUsersView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Fxml/Admin/AdminUsers.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to load AdminUsers.fxml");
            }
        }
        return adminUsersView;
    }

    public Node getAdminReportsView() {
        if (adminReportsView == null) {
            try {
                adminReportsView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Fxml/Admin/AdminReports.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to load AdminReports.fxml");
            }
        }
        return adminReportsView;
    }

    // Undergraduate Methods
    public void showUndergraduateWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Undergraduate/Undergraduate.fxml"));
        createStage(loader);
    }

    public Node getUndergraduateDashboardView() {
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
}