package org.example.mini_project_java.Database;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class DatabaseConnection {
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("src/main/resources")
            .ignoreIfMissing()
            .load();

    private static final String HOST = dotenv.get("DB_HOST");
    private static final int PORT = Integer.parseInt(Objects.requireNonNull(dotenv.get("DB_PORT")));
    private static final String DB_NAME = dotenv.get("DB_NAME");
    private static final String USERNAME = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASS");

    public static Connection connection;

    static {
        try {
            String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME;
            connection = DriverManager.getConnection(url, USERNAME, PASSWORD);
            System.out.println("✅ Database connected successfully.");
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed!");
            e.printStackTrace();
        }
    }
}
