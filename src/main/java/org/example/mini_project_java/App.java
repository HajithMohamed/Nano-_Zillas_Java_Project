package org.example.mini_project_java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
        primaryStage.setTitle("Login");
        primaryStage.setScene(loader.load());
        primaryStage.show();
    }
}
