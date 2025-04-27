package org.example.mini_project_java.Controllers.Technical_Officer;

import com.gluonhq.charm.glisten.control.Icon;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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

public class Technical_Officer_Deshboard_controller implements Initializable {

    @FXML
    public AnchorPane profDetai;

    @FXML
    public Icon lecturerNotification;

    @FXML
    public TextField username;

    @FXML
    public TextField FullName;

    @FXML
    public TextField Password;

    @FXML
    public TextField email;

    @FXML
    public TextField mobileNo;

    @FXML
    public Button update_btn;

    @FXML
    public ImageView profileImage;

    @FXML
    public Button profileImageChangeButton;

    @FXML
    public Text welcomeText; // The Text in the FXML for "Welcome Mr. Lecturer!"

    @FXML
    public Text headerText; // The Text in the FXML for "Mr. Heshan's Profile Details"

    private File selectedImageFile;
    private String currentProfilePicturePath;
    private String officerFullName;
    private String officerFirstName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up event handlers
        profileImageChangeButton.setOnAction(event -> changeProfileImage());
        update_btn.setOnAction(event -> updateTechOfficerProfile());

        // Load technical officer details
        String username = getLoggedInUsername();
        loadTechOfficerDetails(username);

        // Make non-editable fields disabled
        setNonEditableFields();
    }

    private void setNonEditableFields() {
        // Disable fields that technical officers shouldn't be able to edit
        username.setEditable(false);
        Password.setEditable(false);

        // Keep these fields editable
        FullName.setEditable(true);
        email.setEditable(true);
        mobileNo.setEditable(true);
    }

    private String getLoggedInUsername() {
        String username = Model.getInstance().getLoggedInUsername();
        if (username == null) {
            System.err.println("No logged-in user found.");
            return "techOff/0001"; // Default for testing
        }
        return username;
    }

    private void loadTechOfficerDetails(String username) {
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
                        officerFullName = rs.getString("full_name");

                        // Extract first name for personalized messages
                        if (officerFullName != null && !officerFullName.isEmpty()) {
                            officerFirstName = officerFullName.split(" ")[0];
                        } else {
                            officerFirstName = "Officer"; // Default if name not found
                        }

                        this.username.setText(rs.getString("username"));
                        FullName.setText(officerFullName);
                        email.setText(rs.getString("email"));
                        mobileNo.setText(rs.getString("contact_number"));
                        Password.setText("********"); // Masked for security

                        // Update welcome message and header with officer's name
                        updateWelcomeAndHeader();

                        // Load profile picture
                        currentProfilePicturePath = rs.getString("profile_picture");
                        loadProfileImage(currentProfilePicturePath);
                    } else {
                        System.err.println("No technical officer found with username: " + username);
                        showAlert("Error", "Technical officer profile not found.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load technical officer details: " + e.getMessage());
        }
    }

    private void updateWelcomeAndHeader() {
        // Use lookup to find the welcome text and header text in the FXML
        // Note: In your FXML, these need to have fx:id assigned to them
        Text welcomeTextElement = (Text) profDetai.lookup(".admin_dashboard_header");
        if (welcomeTextElement != null) {
            welcomeTextElement.setText("Welcome Mr. " + officerFirstName + "!");
        }

        Text headerTextElement = (Text) profDetai.lookup("VBox .admin_dashboard_header");
        if (headerTextElement != null) {
            headerTextElement.setText("Mr. " + officerFirstName + "'s Profile Details");
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

        Stage stage = (Stage) profileImageChangeButton.getScene().getWindow();
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
    private void updateTechOfficerProfile() {
        String usernameValue = getLoggedInUsername();
        if (usernameValue.isEmpty()) {
            showAlert("Error", "No user is currently logged in.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Update full name, email, contact number and profile picture
            String updateSQL = "UPDATE USERS SET full_name = ?, email = ?, contact_number = ?, profile_picture = ? WHERE username = ?";
            try (PreparedStatement ps = connection.prepareStatement(updateSQL)) {
                ps.setString(1, FullName.getText());
                ps.setString(2, email.getText());
                ps.setString(3, mobileNo.getText());

                // Use new profile picture path if selected, otherwise keep the current one
                String profilePicturePath = selectedImageFile != null ?
                        selectedImageFile.getAbsolutePath() : currentProfilePicturePath;
                ps.setString(4, profilePicturePath);

                ps.setString(5, usernameValue);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    currentProfilePicturePath = profilePicturePath;
                    selectedImageFile = null;

                    // Update officer name for display
                    officerFullName = FullName.getText();
                    if (officerFullName != null && !officerFullName.isEmpty()) {
                        officerFirstName = officerFullName.split(" ")[0];
                    }

                    // Update welcome and header texts
                    updateWelcomeAndHeader();

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