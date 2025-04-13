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
            // You can add listeners or initialization logic here if needed
        }

        @FXML
        private void handleLoginButtonAction() {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Please enter both username and password.");
                return;
            }

            // Dummy check (replace with DB or authentication logic)
            if (username.equals("admin") && password.equals("1234")) {
                showAlert(Alert.AlertType.INFORMATION, "Login successful!");
                // Add navigation logic here
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


