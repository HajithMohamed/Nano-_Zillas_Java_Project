package org.example.mini_project_java.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.mini_project_java.Models.Model;
import org.example.mini_project_java.Models.Users;

public class Login_Controller {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private void initialize() {
        System.out.println("Login controller initialized.");
    }

    @FXML
    private void handleLoginButtonAction() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        System.out.println(username + " " + password);

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Login Error", "Please enter both username and password.");
            return;
        }

        try {
            Users user = Users.login(username, password);
            if (user != null) {
                navigateToRoleBasedView(user);
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "An error occurred while logging in: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateToRoleBasedView(Users user) {
        System.out.println("Navigate to " + user.getRole() + " Window");

        switch (user.getRole().toLowerCase()) {
            case "admin":
                Model.getInstance().getViewFactory().showAdminWindow();
                break;
            case "student":
                Model.getInstance().getViewFactory().showUndergraduateWindow();
                break;
            case "technical_officer":
                // Implement as needed
                System.out.println("Navigate to Technical Officer Window");
                break;
            case "lecture":
                // Implement as needed
                System.out.println("Navigate to Lecture Window");
                break;
            default:
                showAlert(Alert.AlertType.ERROR, "Login Error", "Unknown role: " + user.getRole());
                return;
        }

        // Close the login window after showing the next stage
        Platform.runLater(() -> {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
        });
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
