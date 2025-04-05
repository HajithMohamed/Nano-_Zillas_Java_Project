module org.example.mini_project_java {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens org.example.mini_project_java to javafx.fxml;
    exports org.example.mini_project_java;
}