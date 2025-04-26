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
import org.example.mini_project_java.Models.Notice;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Admin_Notice_Controller implements Initializable {

    @FXML
    public TableColumn<Notice, Integer> noticeId;
    @FXML
    public TableColumn<Notice, String> noticeTitle;
    @FXML
    public TableColumn<Notice, String> noticeContent;
    @FXML
    public TableColumn<Notice, String> noticeRole;
    @FXML
    public TableColumn<Notice, Void> noticeAction;

    @FXML
    public TextField notice_Id;
    @FXML
    public TextField noticeTopic;
    @FXML
    public TextArea notice_Content;
    @FXML
    public TextField notice_Role;
    @FXML
    public Button addNotice;

    @FXML
    private TableView<Notice> notice_Table;

    private final ObservableList<Notice> noticeData = FXCollections.observableArrayList();
    private Notice noticeBeingEdited = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        noticeId.setCellValueFactory(new PropertyValueFactory<>("notice_id"));
        noticeTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        noticeContent.setCellValueFactory(new PropertyValueFactory<>("content"));
        noticeRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        loadNoticeData();
        addEditDeleteButtons();
        notice_Table.setItems(noticeData);

        addNotice.setOnAction(this::handleAddOrUpdateNotice);
    }

    private void handleAddOrUpdateNotice(ActionEvent event) {
        String title = noticeTopic.getText().trim();
        String content = notice_Content.getText().trim();
        String role = notice_Role.getText().trim();

        if (title.isEmpty() || content.isEmpty() || role.isEmpty()) {
            showAlert("Validation Error", "Please fill all fields.");
            return;
        }

        Notice noticeModel = new Notice(0, title, content, role);

        try {
            if (noticeBeingEdited == null) {
                noticeModel.createOrEditNotice(title, content, role, false, 0);
                showAlert("Success", "Notice added successfully.");
            } else {
                noticeModel.createOrEditNotice(title, content, role, true, noticeBeingEdited.getNotice_id());
                showAlert("Success", "Notice updated successfully.");
                noticeBeingEdited = null;
                addNotice.setText("Add Notice");
            }
            loadNoticeData();
            clearFields();
        } catch (Exception e) {
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    private void loadNoticeData() {
        noticeData.clear();
        try (Connection connectDB = DatabaseConnection.getConnection();
             PreparedStatement statement = connectDB.prepareStatement("SELECT notice_id, title, content, role FROM NOTICES");
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                noticeData.add(new Notice(
                        rs.getInt("notice_id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load notices: " + e.getMessage());
        }
    }

    private void clearFields() {
        notice_Id.clear();
        noticeTopic.clear();
        notice_Content.clear();
        notice_Role.clear();
    }

    private void addEditDeleteButtons() {
        noticeAction.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox actionButtons = new HBox(10, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    Notice notice = getTableView().getItems().get(getIndex());
                    notice_Id.setText(String.valueOf(notice.getNotice_id()));
                    noticeTopic.setText(notice.getTitle());
                    notice_Content.setText(notice.getContent());
                    notice_Role.setText(notice.getRole());
                    noticeBeingEdited = notice;
                    addNotice.setText("Update Notice");
                });

                deleteButton.setOnAction(event -> {
                    Notice notice = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure you want to delete this notice?", ButtonType.YES, ButtonType.NO);
                    confirm.setTitle("Confirm Delete");
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            try {
                                new Notice(0, null, null, null).deleteNotice(notice.getNotice_id());
                                showAlert("Success", "Notice deleted successfully.");
                                loadNoticeData();
                            } catch (Exception e) {
                                showAlert("Error", "Failed to delete notice: " + e.getMessage());
                            }
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionButtons);
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
