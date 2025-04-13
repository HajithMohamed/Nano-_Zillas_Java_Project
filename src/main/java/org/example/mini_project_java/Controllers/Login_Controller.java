package org.example.mini_project_java.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Login_Controller {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private void initialize() {
        // Optional: Add listeners or validation
        System.out.println("Login controller initialized.");
    }

    @FXML
    private void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        System.out.println("Login button clicked"); // Debug line

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please enter both username and password.");
            return;
        }

        if (username.equals("admin") && password.equals("1234")) {
            showAlert(Alert.AlertType.INFORMATION, "Login successful!");
            // TODO: Navigate to dashboard or next scene
        } else {
            showAlert(Alert.AlertType.ERROR, "Invalid username or password.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Login Status");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
