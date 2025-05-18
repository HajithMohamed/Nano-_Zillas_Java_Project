package org.example.mini_project_java.Models;

import org.example.mini_project_java.Views.ViewFactory;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.util.ArrayList;
import java.util.List;

public class Model {
    private static Model model;
    private final ViewFactory viewFactory;

    // Add static field for logged-in user
    private static Users loggedInUser;

    public Model() {
        this.viewFactory = new ViewFactory();
    }

    public static synchronized Model getInstance() {
        if (model == null) {
            model = new Model();
        }
        return model;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }

    // Set the logged-in user
    public static void setLoggedInUser(Users user) {
        loggedInUser = user;
    }

    // Get the logged-in user
    public static Users getLoggedInUser() {
        return loggedInUser;
    }

    // Optionally, get username for compatibility
    public String getLoggedInUsername() {
        return loggedInUser != null ? loggedInUser.getUsername() : null;
    }

    // Centralized logout functionality
    public static void logout() {
        loggedInUser = null;
        // Close all open stages except the login window
        Platform.runLater(() -> {
            List<Stage> stages = new ArrayList<>();
            for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
                if (window instanceof Stage stage && stage.isShowing()) {
                    stages.add(stage);
                }
            }
            // Show login window first
            getInstance().getViewFactory().showLoginWindow();
            // Close all other stages (including the one that triggered logout)
            for (Stage stage : stages) {
                stage.close();
            }
        });
    }
}
