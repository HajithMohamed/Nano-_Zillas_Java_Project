package org.example.mini_project_java.Controllers.Admin;

import com.gluonhq.charm.glisten.control.Icon;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.mini_project_java.Models.Admin;
import org.example.mini_project_java.Models.Model;
import org.example.mini_project_java.Utils.MenuItems;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class Admin_Controller implements Initializable {

    @FXML private Text welcomeText;
    @FXML private Text profileHeading;
    @FXML private Button userProfile;
    @FXML private Button course;
    @FXML private Button notice;
    @FXML private Button timetable;
    @FXML private Button dashboard;
    @FXML private TextField admin_username;
    @FXML private TextField adminFullName;
    @FXML private TextField adminPassword;
    @FXML private TextField email;
    @FXML private TextField mobileNo;
    @FXML private Button update_btn;
    @FXML private Button profileImageChangeButton;
    @FXML private ImageView profileImage;
    @FXML private Icon adminNortification;

    private Admin adminModel;
    private File selectedImageFile;
    private String currentProfilePicturePath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addListeners();
        adminModel = new Admin();

        checkFXMLBindings();

        if (profileImageChangeButton != null) {
            profileImageChangeButton.setOnAction(event -> changeProfileImage());
        }
        if (update_btn != null) {
            update_btn.setOnAction(event -> updateProfile());
        }

        String username = getLoggedInUsername();
        loadAdminDetails(username);
    }

    private void addListeners() {
        if (dashboard != null) dashboard.setOnAction(event -> onMenuItemSelected(MenuItems.DASHBOARD));
        if (userProfile != null) userProfile.setOnAction(event -> onMenuItemSelected(MenuItems.USER_PROFILE));
        if (course != null) course.setOnAction(event -> onMenuItemSelected(MenuItems.COURSE));
        if (notice != null) notice.setOnAction(event -> onMenuItemSelected(MenuItems.NOTICE));
        if (timetable != null) timetable.setOnAction(event -> onMenuItemSelected(MenuItems.TIMETABLE));
    }

    private void onMenuItemSelected(String menuItem) {
        Model.getInstance().getViewFactory().getAdminSelectedMenuItem().set(menuItem);
        System.out.println(menuItem + " selected");
    }

    private String getLoggedInUsername() {
        String username = Model.getInstance().getLoggedInUsername();
        if (username == null) {
            System.err.println("No logged-in user found. Using fallback username.");
            return "ADMIN/RUH/TEC/001";
        }
        return username;
    }

    private void loadAdminDetails(String username) {
        if (username == null || username.trim().isEmpty()) {
            System.err.println("Provided username is null or empty.");
            showAlert("Error", "Invalid username provided.");
            return;
        }

        System.out.println("Attempting to load admin details for username: " + username);

        Admin admin = adminModel.getAdminDetails(username);
        if (admin != null) {
            if (admin_username != null) admin_username.setText(admin.getUsername());
            if (adminFullName != null) adminFullName.setText(admin.getName());
            if (adminPassword != null) adminPassword.setText(admin.getPassword());
            if (email != null) email.setText(admin.getEmail());
            if (mobileNo != null) mobileNo.setText(admin.getMobileNo());

            currentProfilePicturePath = admin.getProfilePicture();
            System.out.println("Admin details loaded: " + admin.getUsername() + ", " + admin.getName());

            // ✨ Update welcome message and heading
            if (welcomeText != null) {
                welcomeText.setText("Welcome " + admin.getName() + "!");
            }
            if (profileHeading != null) {
                profileHeading.setText(admin.getName() + "'s Profile Details");
            }

            if (currentProfilePicturePath != null && !currentProfilePicturePath.isEmpty() && profileImage != null) {
                try {
                    File imageFile = new File(currentProfilePicturePath);
                    if (imageFile.exists()) {
                        Image image = new Image(imageFile.toURI().toString());
                        profileImage.setImage(image);
                        System.out.println("Profile picture loaded successfully.");
                    } else {
                        System.err.println("Profile picture file does not exist: " + currentProfilePicturePath);
                        setDefaultProfileImage();
                    }
                } catch (Exception e) {
                    System.err.println("Failed to load profile picture: " + e.getMessage());
                    setDefaultProfileImage();
                }
            } else {
                setDefaultProfileImage();
            }
        } else {
            System.err.println("No admin found with username: " + username);
            showAlert("Error", "Failed to load admin profile details for username: " + username);
        }
    }

    private void setDefaultProfileImage() {
        if (profileImage != null) {
            try {
                profileImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/default_profile.png"))));
            } catch (Exception e) {
                System.err.println("Failed to load default profile image: " + e.getMessage());
            }
        }
    }

    private void changeProfileImage() {
        if (profileImageChangeButton == null || profileImage == null) {
            showAlert("Error", "Profile image change button or image view is not initialized.");
            return;
        }

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
                System.err.println("Failed to load selected profile picture");
                showAlert("Error", "Failed to load selected profile picture.");
            }
        }
    }

    private void updateProfile() {
        if (admin_username == null || adminFullName == null || adminPassword == null || email == null || mobileNo == null) {
            showAlert("Error", "One or more input fields are not initialized.");
            return;
        }

        try {
            String username = admin_username.getText();
            String fullName = adminFullName.getText();
            String password = adminPassword.getText();
            String emailText = email.getText();
            String mobile = mobileNo.getText();
            String role = "Admin";
            String profilePicture = selectedImageFile != null ? selectedImageFile.getAbsolutePath() : currentProfilePicturePath;

            if (username.isEmpty() || fullName.trim().isEmpty() || emailText.isEmpty()) {
                showAlert("Validation Error", "Username, full name, and email cannot be empty.");
                return;
            }

            if (!emailText.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                showAlert("Validation Error", "Invalid email format.");
                return;
            }

            adminModel.updateProfile();
            currentProfilePicturePath = profilePicture;
            selectedImageFile = null;

            showAlert("Success", "Profile updated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update profile: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void checkFXMLBindings() {
        if (welcomeText == null) System.err.println("welcomeText is null. Check fx:id='welcomeText' in Admin.fxml.");
        if (profileHeading == null) System.err.println("profileHeading is null. Check fx:id='profileHeading' in Admin.fxml.");
        if (admin_username == null) System.err.println("admin_username is null.");
        if (adminFullName == null) System.err.println("adminFullName is null.");
        if (adminPassword == null) System.err.println("adminPassword is null.");
        if (email == null) System.err.println("email is null.");
        if (mobileNo == null) System.err.println("mobileNo is null.");
        if (profileImageChangeButton == null) System.err.println("profileImageChangeButton is null.");
        if (update_btn == null) System.err.println("update_btn is null.");
        if (profileImage == null) System.err.println("profileImage is null.");
        if (adminNortification == null) System.err.println("adminNortification is null.");
    }
}
