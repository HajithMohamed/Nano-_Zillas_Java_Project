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


    public AnchorPane studentDashbord;
    public TextField userName;
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

}
