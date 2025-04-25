package org.example.mini_project_java.Controllers.Undergraduate;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateProfile_controller {

    public AnchorPane studentProfile;
    @FXML
    private TextField fullName; // Full Name TextField
    @FXML
    private TextField address; // Address TextField
    @FXML
    private TextField phone; // Phone TextField
    @FXML
    private ImageView profileImageView; // ImageView for Profile Picture
    @FXML
    private Button chooseImageBtn; // Choose Image Button
    @FXML
    private Button updateProfile; // Save Changes Button
    @FXML
    private Button viewProfileBtn; // View Profile Button

    private Connection connection; // Database connection
    private String stid; // Student ID
    private String profileImagePath; // Path to the selected profile image

    // Method to initialize the controller
    @FXML
    private void initialize() {
        chooseImageBtn.setOnAction(event -> chooseImage());
        updateProfile.setOnAction(event -> updateProfile());
        viewProfileBtn.setOnAction(event -> viewProfile()); // Add action for viewing profile
    }

    // Method to choose an image for the profile picture
    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        // Show open file dialog
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            profileImagePath = selectedFile.getAbsolutePath(); // Store the path of the selected image
            profileImageView.setImage(new Image(selectedFile.toURI().toString())); // Display the image
        }
    }

    // Method to view the profile
    private void viewProfile() {
        // Replace this with actual input method for student ID
        stid = "12345"; // For testing purposes, replace with actual input

        String selectSql = "SELECT ContactNumber, ProfilePath, Email, FullName, Address FROM undergraduate WHERE StudentId=?";
        try {
            PreparedStatement selectPst = connection.prepareStatement(selectSql);
            selectPst.setString(1, stid);
            ResultSet rs = selectPst.executeQuery();

            if (rs.next()) {
                // Populate the fields in the UI
                fullName.setText(rs.getString("FullName"));
                address.setText(rs.getString("Address"));
                phone.setText(String.valueOf(rs.getInt("ContactNumber")));
                String profilePath = rs.getString("ProfilePath");
                profileImageView.setImage(new Image(new File(profilePath).toURI().toString()));
                System.out.println("Profile loaded successfully");
            } else {
                System.out.println("No profile found for the given student id.");
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error loading profile: " + e.getMessage());
        }
    }

    // Method to update the profile
    public void updateProfile() {
        String newFullName = fullName.getText();
        String newAddress = address.getText();
        String newContact = phone.getText();

        String updateSql = "UPDATE undergraduate SET FullName=?, Address=?, ContactNumber=?, ProfilePath=? WHERE StudentId=?";
        try {
            PreparedStatement updatePst = connection.prepareStatement(updateSql);
            updatePst.setString(1, newFullName);
            updatePst.setString(2, newAddress);
            updatePst.setInt(3, Integer.parseInt(newContact)); // Ensure this is an integer
            updatePst.setString(4, profileImagePath); // Use the selected image path
            updatePst.setString(5, stid);

            int rowsAffected = updatePst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Profile updated successfully");
            } else {
                System.out.println("Profile update failed");
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid contact number format.");
        } catch (Exception e) {
            System.out.println("Error updating profile: " + e.getMessage());
        }
    }
}
