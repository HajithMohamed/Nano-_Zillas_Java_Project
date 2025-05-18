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

import java.sql.SQLException;

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
    private void handleLoginButtonAction() throws SQLException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        System.out.println("Attempting login with: " + username);

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Login Error", "Please enter both username and password.");
            return;
        }

        Users user = Users.login(username, password);
        if (user != null) {
            // Set the logged-in user globally
            Model.setLoggedInUser(user);
            navigateToRoleBasedView(user);
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
        }
    }

    private void navigateToRoleBasedView(Users user) {
        String role = user.getRole().toLowerCase();
        System.out.println("Logged in as: " + role);

        switch (role) {
            case "admin":
                Model.getInstance().getViewFactory().showAdminWindow();
                break;
            case "student":
                Model.getInstance().getViewFactory().showUndergraduateWindow();
                break;
            case "technical officer":
                Model.getInstance().getViewFactory().showTechnicalOfficerWindow();
                break;
            case "lecturer":
                Model.getInstance().getViewFactory().showLecturerWindow();
                break;
            default:
                showAlert(Alert.AlertType.ERROR, "Login Error", "Unknown role: " + user.getRole());
                return;
        }

        // Close the login window
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