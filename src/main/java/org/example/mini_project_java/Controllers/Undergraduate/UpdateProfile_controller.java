package org.example.mini_project_java.Controllers.Undergraduate;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.mini_project_java.Database.DatabaseConnection;
import org.example.mini_project_java.Models.Model;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class UpdateProfile_controller implements Initializable {

    @FXML
    public AnchorPane studentDashbord;

    @FXML
    public TextField userName;

    @FXML
    public TextField fullName;

    @FXML
    public TextField password;

    @FXML
    public TextField email;

    @FXML
    public TextField mobileNo;

    @FXML
    public Button update_btn;

    @FXML
    public ImageView profileImage;

    @FXML
    public Button profileChangeButton;

    @FXML
    public Text welcomeText;

    @FXML
    public Text headerText;

    private File selectedImageFile;
    private String currentProfilePicturePath;
    private String studentFullName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up event handlers
        profileChangeButton.setOnAction(event -> changeProfileImage());
        update_btn.setOnAction(event -> updateStudentProfile());

        // Load student details
        String username = getLoggedInUsername();
        loadStudentDetails(username);

        // Make non-editable fields disabled
        setNonEditableFields();
    }

    private void setNonEditableFields() {
        // Disable fields that students shouldn't be able to edit
        userName.setEditable(false);
        fullName.setEditable(false);
        email.setEditable(false);
        password.setEditable(false);

        // Keep only mobile number editable
        mobileNo.setEditable(true);
    }

    private String getLoggedInUsername() {
        String username = Model.getInstance().getLoggedInUsername();
        if (username == null) {
            System.err.println("No logged-in user found.");
            return "TG/2022/1414"; // Default for testing
        }
        return username;
    }

    private void loadStudentDetails(String username) {
        if (username == null || username.trim().isEmpty()) {
            System.err.println("Provided username is null or empty.");
            showAlert("Error", "Invalid username provided.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM USERS WHERE username = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // Populate the fields with data from the database
                        studentFullName = rs.getString("full_name");

                        userName.setText(rs.getString("username"));
                        fullName.setText(studentFullName);
                        email.setText(rs.getString("email"));
                        mobileNo.setText(rs.getString("contact_number"));
                        password.setText("********"); // Masked for security

                        // Update welcome message and header with student's name
                        // Extract first name from full_name for personalized messages
                        String firstName = studentFullName.split(" ")[0];
                        updateWelcomeAndHeader(firstName);

                        // Load profile picture
                        currentProfilePicturePath = rs.getString("profile_picture");
                        loadProfileImage(currentProfilePicturePath);
                    } else {
                        System.err.println("No student found with username: " + username);
                        showAlert("Error", "Student profile not found.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load student details: " + e.getMessage());
        }
    }

    private void updateWelcomeAndHeader(String firstName) {
        // Find and update the welcome text and header text in the FXML
        if (welcomeText != null) {
            welcomeText.setText("Welcome " + firstName + "!");
        }

        if (headerText != null) {
            headerText.setText(firstName + "'s Profile Details");
        }
    }

    private void loadProfileImage(String imagePath) {
        if (profileImage == null) return;

        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    profileImage.setImage(image);
                } else {
                    setDefaultProfileImage();
                }
            } catch (Exception e) {
                System.err.println("Failed to load profile picture: " + e.getMessage());
                setDefaultProfileImage();
            }
        } else {
            setDefaultProfileImage();
        }
    }

    private void setDefaultProfileImage() {
        if (profileImage != null) {
            try {
                profileImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Images/admin.png"))));
            } catch (Exception e) {
                System.err.println("Failed to load default profile image: " + e.getMessage());
            }
        }
    }

    private void changeProfileImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) profileChangeButton.getScene().getWindow();
        selectedImageFile = fileChooser.showOpenDialog(stage);

        if (selectedImageFile != null) {
            try {
                String imageUrl = selectedImageFile.toURI().toString();
                Image newImage = new Image(imageUrl);
                profileImage.setImage(newImage);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load selected profile picture.");
            }
        }
    }

    @FXML
    private void updateStudentProfile() {
        String username = getLoggedInUsername();
        if (username.isEmpty()) {
            showAlert("Error", "No user is currently logged in.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Update only contact number and profile picture
            String updateSQL = "UPDATE USERS SET contact_number = ?, profile_picture = ? WHERE username = ?";
            try (PreparedStatement ps = connection.prepareStatement(updateSQL)) {
                ps.setString(1, mobileNo.getText());

                // Use new profile picture path if selected, otherwise keep the current one
                String profilePicturePath = selectedImageFile != null ?
                        selectedImageFile.getAbsolutePath() : currentProfilePicturePath;
                ps.setString(2, profilePicturePath);

                ps.setString(3, username);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    currentProfilePicturePath = profilePicturePath;
                    selectedImageFile = null;
                    showAlert("Success", "Profile updated successfully!");
                } else {
                    showAlert("Error", "Failed to update profile. No records were affected.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to update profile: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}