package org.example.mini_project_java.Controllers.Admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.example.mini_project_java.Database.DatabaseConnection;
import org.example.mini_project_java.Models.*;
import org.mindrot.jbcrypt.BCrypt;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Admin_User_Profiles_Controller implements Initializable {

    @FXML public TableView<Users> User_Profile_Table_View;
    @FXML public TableColumn<Users, String> User_Name;
    @FXML public TableColumn<Users, String> Name;
    @FXML public TableColumn<Users, String> Role;
    @FXML public TableColumn<Users, String> Email;
    @FXML public TableColumn<Users, String> mobile_no;
    @FXML public TableColumn<Users, String> user_Password;
    @FXML public TableColumn<Users, Void> userAction;

    @FXML public TextField userName;
    @FXML public TextField fullName;
    @FXML public TextField userPassword;
    @FXML public TextField userRole;
    @FXML public TextField userEmail;
    @FXML public TextField userMobileNo;
    @FXML public Button addUserButton;

    private final ObservableList<Users> userList = FXCollections.observableArrayList();
    private Users userBeingEdited = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Fix: Use property names that match the Users class
        User_Name.setCellValueFactory(new PropertyValueFactory<>("username"));
        Name.setCellValueFactory(new PropertyValueFactory<>("name"));
        Role.setCellValueFactory(new PropertyValueFactory<>("role"));
        Email.setCellValueFactory(new PropertyValueFactory<>("email"));
        // Fix: Changed from "contact_number" to "mobileNo" to match getMobileNo()
        mobile_no.setCellValueFactory(new PropertyValueFactory<>("mobileNo"));
        user_Password.setCellValueFactory(new PropertyValueFactory<>("password"));

        loadUserDataFromDatabase();
        User_Profile_Table_View.setItems(userList);
        addButtonToTable();
        addUserButton.setOnAction(this::btnAddOrEdit);
    }

    private void loadUserDataFromDatabase() {
        userList.clear();
        try (Connection connectDB = DatabaseConnection.getConnection();
             Statement statement = connectDB.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT username, full_name, email, role, contact_number, password FROM USERS")) {

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String name = resultSet.getString("full_name");
                String email = resultSet.getString("email");
                String role = resultSet.getString("role");
                String mobileNo = resultSet.getString("contact_number");
                String password = resultSet.getString("password");

                // Fix: Corrected class names and role cases
                switch (role.toLowerCase()) {
                    case "student" -> userList.add(new Undergratuate(username, password, name, email, role, mobileNo));
                    case "admin" -> userList.add(new Admin(username, password, name, email, role, mobileNo));
                    case "lecturer" -> userList.add(new Lecture(username, password, name, email, role, mobileNo));
                    case "technical officer" -> userList.add(new Tecninical_Officer(username, password, name, email, role, mobileNo));
                    default -> System.out.println("Unknown role: " + role);
                }
            }

        } catch (SQLException e) {
            showAlert("DB Error", "Error loading data: " + e.getMessage());
            e.printStackTrace(); // Added for better debugging
        }
    }

    public void btnAddOrEdit(ActionEvent event) {
        String FullName = fullName.getText().trim();
        String username = userName.getText().trim();
        String email = userEmail.getText().trim();
        String role = userRole.getText().trim();
        String mobile = userMobileNo.getText().trim();
        String password = userPassword.getText().trim();

        if (username.isEmpty() || FullName.isEmpty() || email.isEmpty() || role.isEmpty() || mobile.isEmpty()) {
            showAlert("Validation Error", "Please fill in all fields.");
            return;
        }

        Admin admin = new Admin();

        try {
            if (userBeingEdited == null) {
                // Hash the password before saving
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                admin.createUserProfiles(username, FullName, email, role, mobile, hashedPassword, false);
                showAlert("Success", "User added successfully!");
            } else {
                // Update the user without changing the password if password field is empty
                String updatedPassword = password.isEmpty() ? null : BCrypt.hashpw(password, BCrypt.gensalt());
                admin.createUserProfiles(username, FullName, email, role, mobile, updatedPassword, true);
                showAlert("Success", "User updated successfully!");
                userBeingEdited = null;
                addUserButton.setText("Add User");
            }

            loadUserDataFromDatabase(); // Reload data after changes
            clearFields();
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addButtonToTable() {
        userAction.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox hbox = new HBox(10, editBtn, deleteBtn);

            {
                editBtn.setOnAction(event -> {
                    Users user = getTableView().getItems().get(getIndex());
                    fillFormWithUser(user);
                    userBeingEdited = user;
                    addUserButton.setText("Update User");
                });

                deleteBtn.setOnAction(event -> {
                    Users user = getTableView().getItems().get(getIndex());
                    deleteUser(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(hbox);
                }
            }
        });
    }

    private void fillFormWithUser(Users user) {
        fullName.setText(user.getName());
        // Don't display password for security
        userName.setText(user.getUsername());
        userEmail.setText(user.getEmail());
        userRole.setText(user.getRole());
        userMobileNo.setText(user.getMobileNo());
        userPassword.clear(); // do not fill password for security
    }

    private void deleteUser(Users user) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete User");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete user: " + user.getUsername() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection connectDB = DatabaseConnection.getConnection();
                     PreparedStatement ps = connectDB.prepareStatement("DELETE FROM USERS WHERE username = ?")) {
                    ps.setString(1, user.getUsername());
                    int rowsAffected = ps.executeUpdate();

                    if (rowsAffected > 0) {
                        showAlert("Success", "User deleted successfully.");
                        loadUserDataFromDatabase();
                    } else {
                        showAlert("Warning", "No user was deleted. User may no longer exist in database.");
                    }
                } catch (SQLException e) {
                    showAlert("DB Error", "Failed to delete user: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void clearFields() {
        userName.clear();
        fullName.clear();
        userPassword.clear();
        userEmail.clear();
        userRole.clear();
        userMobileNo.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}