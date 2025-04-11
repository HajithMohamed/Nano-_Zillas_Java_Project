module org.example.mini_project_java {
    requires javafx.controls;
    requires javafx.fxml;
    requires de.jensd.fx.glyphs.fontawesome;
    requires java.sql;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens org.example.mini_project_java to javafx.fxml;
    exports org.example.mini_project_java;
    exports org.example.mini_project_java.Controllers;
    exports org.example.mini_project_java.Models;

}