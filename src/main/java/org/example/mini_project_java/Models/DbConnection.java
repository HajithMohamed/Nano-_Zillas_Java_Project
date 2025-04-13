package org.example.mini_project_java.Models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private String url = "jdbc:mysql://localhost:3306/Undergraduate";
    private String user = "root";
    private String password = "1234";
    private Connection con = null;

    private void registerMyConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connecting to database...");
        } catch (ClassNotFoundException e) {
            System.out.println("Couldn't find database driver: " + e.getMessage());
        }
    }

    public Connection getMyConnection() {
        registerMyConnection();
        try {
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Connected");
        } catch (SQLException e) {
            System.out.println("Couldn't connect to database: " + e.getMessage());
        }
        return con;
    }
}
