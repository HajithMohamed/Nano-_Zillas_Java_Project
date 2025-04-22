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

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Admin_User_Profiles_Controller implements Initializable {

    @FXML public TableView<Users> User_Profile_Table_View;
    @FXML public TableColumn<Users, String> User_Name;
    @FXML public TableColumn<Users, String> Password;
    @FXML public TableColumn<Users, String> Name;
    @FXML public TableColumn<Users, String> Role;
    @FXML public TableColumn<Users, String> Email;
    @FXML public TableColumn<Users, String> Mobile;
    @FXML public TableColumn<Users, Void> userAction;

    @FXML public TextField userName;
    @FXML public TextField firstName;
    @FXML public TextField lastName;
    @FXML public TextField userRole;
    @FXML public TextField userEmail;
    @FXML public TextField userMobileNo;
    @FXML public Button addUserButton;

    private final ObservableList<Users> userList = FXCollections.observableArrayList();
    public PasswordField userPassword;
    private Users userBeingEdited = null;

    private void loadUserDataFromDatabase() {
        userList.clear();
        try (Connection connectDB = DatabaseConnection.getConnection();
             Statement statement = connectDB.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM USERS")) {

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String name = resultSet.getString("full_name");
                String email = resultSet.getString("email");
                String role = resultSet.getString("role");
                String mobileNo = resultSet.getString("contact_number");
                String password = resultSet.getString("password");

                switch (role) {
                    case "Student" -> userList.add(new Undergratuate(username, password, name, email, role, mobileNo));
                    case "Admin" -> userList.add(new Admin(username, password, name, email, role, mobileNo));
                    case "Lecture" -> userList.add(new Lecture(username, password, name, email, role, mobileNo));
                    case "Technical officer" ->
                            userList.add(new Tecninical_Officer(username, password, name, email, role, mobileNo));
                }
            }

        } catch (SQLException e) {
            showAlert("DB Error", "Error loading data: " + e.getMessage());
        }
    }

    public void btnAddOrEdit(ActionEvent event) {
        String fullName = firstName.getText().trim() + " " + lastName.getText().trim();
        String username = userName.getText().trim();
        String email = userEmail.getText().trim();
        String role = userRole.getText().trim();
        String mobile = userMobileNo.getText().trim();
        String password = "1234"; // placeholder

        try (Connection connectDB = DatabaseConnection.getConnection()) {

            if (userBeingEdited == null) {
                String insertSQL = "INSERT INTO USERS (username, full_name, email, role, contact_number, password) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connectDB.prepareStatement(insertSQL)) {
                    ps.setString(1, username);
                    ps.setString(2, fullName);
                    ps.setString(3, email);
                    ps.setString(4, role);
                    ps.setString(5, mobile);
                    ps.setString(6, password);
                    ps.executeUpdate();
                }

                showAlert("User Added", "User added successfully!");
            } else {
                String updateSQL = "UPDATE USERS SET full_name = ?, email = ?, role = ?, contact_number = ? WHERE username = ?";
                try (PreparedStatement ps = connectDB.prepareStatement(updateSQL)) {
                    ps.setString(1, fullName);
                    ps.setString(2, email);
                    ps.setString(3, role);
                    ps.setString(4, mobile);
                    ps.setString(5, username);
                    ps.executeUpdate();
                }

                showAlert("User Edited", "User edited successfully!");
                userBeingEdited = null;
                addUserButton.setText("Add User");
            }

            loadUserDataFromDatabase();
            clearFields();

        } catch (SQLException e) {
            showAlert("DB Error", e.getMessage());
        }
    }

    private void clearFields() {
        userName.clear();
        firstName.clear();
        lastName.clear();
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

    private void addActionButtonsToTable() {
        userAction.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox pane = new HBox(10, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    userBeingEdited = getTableView().getItems().get(getIndex());
                    userName.setText(userBeingEdited.getUsername());

                    String[] nameParts = userBeingEdited.getName().split(" ", 2);
                    firstName.setText(nameParts.length > 0 ? nameParts[0] : "");
                    lastName.setText(nameParts.length > 1 ? nameParts[1] : "");

                    userEmail.setText(userBeingEdited.getEmail());
                    userRole.setText(userBeingEdited.getRole());
                    userMobileNo.setText(userBeingEdited.getMobile_no());

                    addUserButton.setText("Edit User");
                });

                deleteButton.setOnAction(event -> {
                    Users selectedUser = getTableView().getItems().get(getIndex());

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Delete User");
                    confirm.setHeaderText(null);
                    confirm.setContentText("Delete this user?");

                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try (Connection connectDB = DatabaseConnection.getConnection();
                                 PreparedStatement ps = connectDB.prepareStatement("DELETE FROM USERS WHERE username = ?")) {
                                ps.setString(1, selectedUser.getUsername());
                                ps.executeUpdate();
                                userList.remove(selectedUser);
                            } catch (SQLException e) {
                                showAlert("DB Error", "Failed to delete: " + e.getMessage());
                            }
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Is User_Name null? " + (User_Name == null)); // DEBUG

        User_Name.setCellValueFactory(new PropertyValueFactory<>("username"));
        Name.setCellValueFactory(new PropertyValueFactory<>("name"));
        Role.setCellValueFactory(new PropertyValueFactory<>("role"));
        Email.setCellValueFactory(new PropertyValueFactory<>("email"));
        Mobile.setCellValueFactory(new PropertyValueFactory<>("mobile_no"));

        loadUserDataFromDatabase();
        User_Profile_Table_View.setItems(userList);

        addActionButtonsToTable();
        addUserButton.setOnAction(this::btnAddOrEdit);
    }
}
