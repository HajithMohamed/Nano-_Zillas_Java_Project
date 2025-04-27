package org.example.mini_project_java.Controllers.Undergraduate;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
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
    public TextField fName;

    @FXML
    public TextField lName;

    @FXML
    public TextField email;

    @FXML
    public TextField mobileNo;

    @FXML
    public Button update_btn;

    @FXML
    private TextField fullName;

    @FXML
    private TextField address;

    @FXML
    private TextField phone;

    @FXML
    private ImageView profileImageView;

    @FXML
    private Button chooseImageBtn;

    @FXML
    private Button updateProfile;

    @FXML
    private Button viewProfileBtn;

    private File selectedImageFile;
    private String currentProfilePicturePath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up event handlers
        if (chooseImageBtn != null) {
            chooseImageBtn.setOnAction(event -> changeProfileImage());
        }

        if (updateProfile != null) {
            updateProfile.setOnAction(event -> updateStudentProfile());
        }

        if (viewProfileBtn != null) {
            viewProfileBtn.setOnAction(event -> viewProfile());
        }

        // Load student details
        String username = getLoggedInUsername();
        loadStudentDetails(username);

        // Make non-editable fields disabled
        setNonEditableFields();
    }

    private void setNonEditableFields() {
        // Disable fields that students shouldn't be able to edit
        if (userName != null) userName.setEditable(false);
        if (fName != null) fName.setEditable(false);
        if (lName != null) lName.setEditable(false);
        if (email != null) email.setEditable(false);
        if (fullName != null) fullName.setEditable(false);
        if (address != null) address.setEditable(false);

        // Keep phone/mobileNo editable
        if (phone != null) phone.setEditable(true);
        if (mobileNo != null) mobileNo.setEditable(true);
    }

    private String getLoggedInUsername() {
        String username = Model.getInstance().getLoggedInUsername();
        if (username == null) {
            System.err.println("No logged-in user found.");
            return "";
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
                        if (userName != null) userName.setText(rs.getString("username"));
                        if (fName != null) fName.setText(rs.getString("first_name"));
                        if (lName != null) lName.setText(rs.getString("last_name"));
                        if (email != null) email.setText(rs.getString("email"));
                        if (mobileNo != null) mobileNo.setText(rs.getString("contact_number"));
                        if (fullName != null) fullName.setText(rs.getString("first_name") + " " + rs.getString("last_name"));
                        if (address != null) address.setText(rs.getString("address"));
                        if (phone != null) phone.setText(rs.getString("contact_number"));

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

    private void loadProfileImage(String imagePath) {
        if (profileImageView == null) return;

        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    profileImageView.setImage(image);
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
        if (profileImageView != null) {
            try {
                profileImageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/default_profile.png"))));
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

        Stage stage = (Stage) chooseImageBtn.getScene().getWindow();
        selectedImageFile = fileChooser.showOpenDialog(stage);

        if (selectedImageFile != null) {
            try {
                String imageUrl = selectedImageFile.toURI().toString();
                Image newImage = new Image(imageUrl);
                profileImageView.setImage(newImage);
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
                // Use phone or mobileNo depending on which one is populated
                String contactNumber = (phone != null && !phone.getText().isEmpty()) ?
                        phone.getText() : (mobileNo != null) ? mobileNo.getText() : "";

                ps.setString(1, contactNumber);

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

    private void viewProfile() {
        // This method would navigate to the view profile screen
        // Implementation depends on your application's navigation structure
        System.out.println("View Profile button clicked");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}