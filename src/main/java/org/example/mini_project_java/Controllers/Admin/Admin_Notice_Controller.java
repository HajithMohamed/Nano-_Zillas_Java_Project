package org.example.mini_project_java.Controllers.Admin;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class Admin_Notice_Controller implements Initializable {

    public TableColumn noticeId;
    public TableColumn noticeRole;
    public TableColumn noticeTitle;
    public TableColumn noticeContent;
    public TableColumn noticeAction;


    public TextField notice_Id;
    public TextField noticeTopic;
    public Button addNotice;
    public TextArea notice_Content;
    public TextField notice_Role;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
