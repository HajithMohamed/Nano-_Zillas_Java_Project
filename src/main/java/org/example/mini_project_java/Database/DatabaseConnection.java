package org.example.mini_project_java.Database;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection;

    public static Connection getConnection() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("src/main/resources")
                    .ignoreIfMissing()
                    .load();

            String host = dotenv.get("DB_HOST", "localhost");
            String port = dotenv.get("DB_PORT", "3306");
            String dbName = dotenv.get("DB_NAME", "testdb");
            String username = dotenv.get("DB_USER", "root");
            String password = dotenv.get("DB_PASS", "");

            String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?useSSL=false";

            return DriverManager.getConnection(url, username, password); // No caching here
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed!");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

}