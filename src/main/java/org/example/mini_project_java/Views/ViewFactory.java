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
    private final StringProperty lectureSelectedMenuItem;
    private final StringProperty technicalOfficerSelectedMenuItem;

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

    // Technical Officer views
    private AnchorPane technicalOfficerAttendanceView;
    private AnchorPane technicalOfficerMedicalView;
    private AnchorPane technicalOfficerDashboardView;

    // Lecture Views
    private AnchorPane lectureDashboardView;
    private AnchorPane LectureCourseMaterialView;
    private AnchorPane lectureCourseView;
    private AnchorPane lectureMarksView;
    private AnchorPane lectureUndergraduateDetailsView;
    private AnchorPane lectureEligibilityView;
    private AnchorPane lectureGradeView;
    private AnchorPane lectureMedicalView;

    public ViewFactory() {
        this.lectureSelectedMenuItem = new SimpleStringProperty("");
        this.technicalOfficerSelectedMenuItem = new SimpleStringProperty("");
        this.studentSelectedMenuItem = new SimpleStringProperty("");
        this.adminSelectedMenuItem = new SimpleStringProperty("");
    }

    public String getTechnicalOfficerSelectedMenuItem() {
        return technicalOfficerSelectedMenuItem.get();
    }

    public StringProperty technicalOfficerSelectedMenuItemProperty() {
        return technicalOfficerSelectedMenuItem;
    }

    public String getLectureSelectedMenuItem() {
        return lectureSelectedMenuItem.get();
    }

    public StringProperty lectureSelectedMenuItemProperty() {
        return lectureSelectedMenuItem;
    }

    public StringProperty studentSelectedMenuItemProperty() {
        return studentSelectedMenuItem;
    }

    public StringProperty getAdminSelectedMenuItem() {
        return adminSelectedMenuItem;
    }

    // -------- Lecturer Views --------
    public AnchorPane getLectureDashboardView() {
        return getLectureDashboardView(false);
    }

    public AnchorPane getLectureDashboardView(boolean reload) {
        if (lectureDashboardView == null || reload) {
            lectureDashboardView = loadView("/Fxml/Lecturer/LecturerDashboard.fxml", "Lecturer Dashboard view could not be loaded.");
        }
        return lectureDashboardView;
    }

    public AnchorPane getLectureCourseMaterialView() {
        return getLectureCourseMaterialView(false);
    }

    public AnchorPane getLectureCourseMaterialView(boolean reload) {
        if (LectureCourseMaterialView == null || reload) {
            LectureCourseMaterialView = loadView("/Fxml/Lecturer/LectureCourseMaterial.fxml", "Course Material view could not be loaded.");
        }
        return LectureCourseMaterialView;
    }

    public AnchorPane getLectureCourseView() {
        return getLectureCourseView(false);
    }

    public AnchorPane getLectureCourseView(boolean reload) {
        if (lectureCourseView == null || reload) {
            lectureCourseView = loadView("/Fxml/Lecturer/ModifyCourse.fxml", "Courses view could not be loaded.");
        }
        return lectureCourseView;
    }

    public AnchorPane getLectureMarksView() {
        return getLectureMarksView(false);
    }

    public AnchorPane getLectureMarksView(boolean reload) {
        if (lectureMarksView == null || reload) {
            lectureMarksView = loadView("/Fxml/Lecturer/UploadMark.fxml", "Marks view could not be loaded.");
        }
        return lectureMarksView;
    }

    public AnchorPane getLectureUndergraduateDetailsView() {
        return getLectureUndergraduateDetailsView(false);
    }

    public AnchorPane getLectureUndergraduateDetailsView(boolean reload) {
        if (lectureUndergraduateDetailsView == null || reload) {
            lectureUndergraduateDetailsView = loadView("/Fxml/Lecturer/UndergraduateDetails.fxml", "Undergraduate Details view could not be loaded.");
        }
        return lectureUndergraduateDetailsView;
    }

    public AnchorPane getLectureEligibilityView() {
        return getLectureEligibilityView(false);
    }

    public AnchorPane getLectureEligibilityView(boolean reload) {
        if (lectureEligibilityView == null || reload) {
            lectureEligibilityView = loadView("/Fxml/Lecturer/UndergraduateEligibility.fxml", "Eligibility view could not be loaded.");
        }
        return lectureEligibilityView;
    }

    public AnchorPane getLectureGradeView() {
        return getLectureGradeView(false);
    }

    public AnchorPane getLectureGradeView(boolean reload) {
        if (lectureGradeView == null || reload) {
            lectureGradeView = loadView("/Fxml/Lecturer/UndergraduateMarksGpa.fxml", "Grades view could not be loaded.");
        }
        return lectureGradeView;
    }

    public AnchorPane getLectureMedicalView() {
        return getLectureMedicalView(false);
    }

    public AnchorPane getLectureMedicalView(boolean reload) {
        if (lectureMedicalView == null || reload) {
            lectureMedicalView = loadView("/Fxml/Lecturer/DisplayMedicalAttendance.fxml", "Medical view could not be loaded.");
        }
        return lectureMedicalView;
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

    public void showTechnicalOfficerWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Technical_Officer/Technical_Officer.fxml"));
        createStage(loader);
    }

    public void showLecturerWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Lecturer/Lecture.fxml"));
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
            undergraduateDashboardView = loadView("/Fxml/Student/StudentDashboard.fxml", "Dashboard view could not be loaded.");
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

    // -------- Technical Officer Views --------
    public AnchorPane getTechnicalOfficerAttendanceView() {
        return getTechnicalOfficerAttendanceView(false);
    }

    public AnchorPane getTechnicalOfficerAttendanceView(boolean reload) {
        if (technicalOfficerAttendanceView == null || reload) {
            technicalOfficerAttendanceView = loadView("/Fxml/Technical_Officer/Technical_Officer_Attendance.fxml", "Technical Officer Attendance view could not be loaded.");
        }
        return technicalOfficerAttendanceView;
    }

    public AnchorPane getTechnicalOfficerMedicalView() {
        return getTechnicalOfficerMedicalView(false);
    }

    public AnchorPane getTechnicalOfficerMedicalView(boolean reload) {
        if (technicalOfficerMedicalView == null || reload) {
            technicalOfficerMedicalView = loadView("/Fxml/Technical_Officer/Technical_Officer_Medical.fxml", "Technical Officer Medical view could not be loaded.");
        }
        return technicalOfficerMedicalView;
    }

    public AnchorPane getTechnicalOfficerDashboardView() {
        return getTechnicalOfficerDashboardView(false);
    }

    public AnchorPane getTechnicalOfficerDashboardView(boolean reload) {
        if (technicalOfficerDashboardView == null || reload) {
            technicalOfficerDashboardView = loadView("/Fxml/Technical_Officer/Technical_Officer_Dashboard.fxml", "Technical Officer Dashboard view could not be loaded.");
        }
        return technicalOfficerDashboardView;
    }

    // -------- Helper Methods --------
    private AnchorPane loadView(String fxmlPath, String errorMessage) {
        try {
            return FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
        } catch (IOException e) {
            e.printStackTrace();
            showError(errorMessage);
            return new AnchorPane();
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