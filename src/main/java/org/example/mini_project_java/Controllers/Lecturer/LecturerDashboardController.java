package org.example.mini_project_java.Controllers.Lecturer;

import com.gluonhq.charm.glisten.control.Icon;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import org.example.mini_project_java.Models.Lecture;
import javafx.scene.control.Alert;
import java.io.File;
import javafx.stage.FileChooser;
import org.example.mini_project_java.Models.Users;

public class LecturerDashboardController {
    @FXML
    public Icon lecturerNotification;
    @FXML
    public ImageView profileImage;
    @FXML
    public Button profileImageChangeButton;
    @FXML
    public TextField lecturer_username;
    @FXML
    public TextField lecturerFullName;
    @FXML
    public TextField lecturerPassword;
    @FXML
    public TextField email;
    @FXML
    public TextField mobileNo;
    @FXML
    public Button update_btn;

    private Lecture lecturer;
    private String profilePicturePath;

    @FXML
    public void initialize() {
        // Disable username and password fields to prevent updates
        lecturer_username.setEditable(false);
        lecturerPassword.setEditable(false);
        Users user = org.example.mini_project_java.Models.Model.getLoggedInUser();
        String loggedInUsername = (user != null) ? user.getUsername() : "LEC/RUH/TEC/001";
        try {
            lecturer = Lecture.fetchLecturerByUsername(loggedInUsername);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load lecturer details: " + e.getMessage());
            return;
        }

        // Load initial profile data
        loadProfileData();

        // Set up button actions
        update_btn.setOnAction(event -> handleUpdateProfile());
        profileImageChangeButton.setOnAction(event -> handleChangeProfileImage());
    }

    private void loadProfileData() {
        // Load data from lecturer object to text fields
        lecturer_username.setText(lecturer.getUsername());
        lecturerFullName.setText(lecturer.getName());
        lecturerPassword.setText("********");
        email.setText(lecturer.getEmail());
        mobileNo.setText(lecturer.getMobileNo());
        // Load profile image if available
        if (lecturer.getProfilePicture() != null && !lecturer.getProfilePicture().isEmpty()) {
            profileImage.setImage(new javafx.scene.image.Image("file:" + lecturer.getProfilePicture()));
        }
    }

    private void handleUpdateProfile() {
        try {
            // Validate inputs
            String fullName = lecturerFullName.getText().trim();
            String emailText = email.getText().trim();
            String mobile = mobileNo.getText().trim();

            if (fullName.isEmpty() || emailText.isEmpty() || mobile.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields must be filled.");
                return;
            }
            if (!emailText.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Invalid email format.");
                return;
            }
            if (!mobile.matches("\\d{10}")) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Mobile number must be 10 digits.");
                return;
            }

            // Update lecturer object with new values
            lecturer.setName(fullName);
            lecturer.setEmail(emailText);
            lecturer.setMobileNo(mobile);
            if (profilePicturePath != null) {
                lecturer.setProfilePicture(profilePicturePath);
            }

            // Call updateProfile to save changes to database
            lecturer.updateProfile();

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully.");
        } catch (Exception e) {
            // Show error message
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update profile: " + e.getMessage());
        }
    }

    private void handleChangeProfileImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null && selectedFile.exists()) {
            try {
                profilePicturePath = selectedFile.getAbsolutePath();
                profileImage.setImage(new javafx.scene.image.Image("file:" + profilePicturePath));
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load image: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Warning", "No valid image file selected.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}