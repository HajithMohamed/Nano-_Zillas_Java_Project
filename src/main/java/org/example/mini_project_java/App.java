package org.example.mini_project_java;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.mini_project_java.Models.Model;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Model.getInstance().getViewFactory().showLoginWindow();
    }
}
