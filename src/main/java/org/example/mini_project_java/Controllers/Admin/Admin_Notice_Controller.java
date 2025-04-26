package org.example.mini_project_java.Controllers.Admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.example.mini_project_java.Models.Notice;
import org.example.mini_project_java.Database.DatabaseConnection;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Admin_Notice_Controller implements Initializable {

    public TableView <Notice>noticeTable;
    @FXML
    private TableColumn<Notice, Integer> noticeId;
    @FXML
    private TableColumn<Notice, String> noticeRole;
    @FXML
    private TableColumn<Notice, String> noticeTitle;
    @FXML
    private TableColumn<Notice, String> noticeContent;
    @FXML
    private TableColumn<Notice, Void> noticeAction;

    @FXML
    private TextField notice_Id;
    @FXML
    private TextField noticeTopic;
    @FXML
    private TextArea notice_Content;
    @FXML
    private TextField notice_Role;
    @FXML
    private Button addNotice;
    @FXML


    private final ObservableList<Notice> noticeList = FXCollections.observableArrayList();

    private boolean isEditMode = false;
    private int editNoticeId = -1;



    private void loadNotices() {
        noticeList.clear();
        String query = "SELECT * FROM NOTICES";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("notice_id");
                String title = rs.getString("title");
                String content = rs.getString("content");
                String role = rs.getString("role");

                Notice notice = new Notice(id, title, content, role);
                noticeList.add(notice);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        noticeTable.setItems(noticeList);
    }

    private void handleAddOrUpdateNotice() {
        String title = noticeTopic.getText();
        String content = notice_Content.getText();
        String role = notice_Role.getText();

        if (title.isEmpty() || content.isEmpty() || role.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill in all fields.");
            return;
        }

        Notice notice = new Notice(0, title, content, role);

        if (!isEditMode) {
            // Create new notice
            notice.createOrEditNotice(title, content, role, false, 0);
            showAlert(Alert.AlertType.INFORMATION, "Notice added successfully!");
        } else {
            // Update existing notice
            notice.createOrEditNotice(title, content, role, true, editNoticeId);
            showAlert(Alert.AlertType.INFORMATION, "Notice updated successfully!");
            isEditMode = false;
            addNotice.setText("Add Notice");
        }

        clearForm();
        loadNotices();
    }

    private void addActionButtonsToTable() {
        Callback<TableColumn<Notice, Void>, TableCell<Notice, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Notice, Void> call(final TableColumn<Notice, Void> param) {
                return new TableCell<>() {

                    private final Button editBtn = new Button("Edit");
                    private final Button deleteBtn = new Button("Delete");
                    private final HBox managebtn = new HBox(editBtn, deleteBtn);

                    {
                        managebtn.setSpacing(10);

                        editBtn.setOnAction(event -> {
                            Notice selectedNotice = getTableView().getItems().get(getIndex());
                            populateFormForEdit(selectedNotice);
                        });

                        deleteBtn.setOnAction(event -> {
                            Notice selectedNotice = getTableView().getItems().get(getIndex());
                            deleteNotice(selectedNotice.getNotice_id());
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(managebtn);
                        }
                    }
                };
            }
        };

        noticeAction.setCellFactory(cellFactory);
    }

    private void populateFormForEdit(Notice notice) {
        notice_Id.setText(String.valueOf(notice.getNotice_id()));
        notice_Role.setText(notice.getRole());
        noticeTopic.setText(notice.getTitle());
        notice_Content.setText(notice.getContent());

        isEditMode = true;
        editNoticeId = notice.getNotice_id();
        addNotice.setText("Update Notice");
    }

    private void deleteNotice(int id) {
        Notice notice = new Notice(0, "", "", "");
        notice.deleteNotice(id);
        showAlert(Alert.AlertType.INFORMATION, "Notice deleted successfully!");

        loadNotices();
    }

    private void clearForm() {
        notice_Id.clear();
        notice_Role.clear();
        noticeTopic.clear();
        notice_Content.clear();
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up table columns
        noticeId.setCellValueFactory(new PropertyValueFactory<>("notice_id"));
        noticeRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        noticeTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        noticeContent.setCellValueFactory(new PropertyValueFactory<>("content"));

        addActionButtonsToTable();

        loadNotices();

        addNotice.setOnAction(e -> handleAddOrUpdateNotice());
    }
}
