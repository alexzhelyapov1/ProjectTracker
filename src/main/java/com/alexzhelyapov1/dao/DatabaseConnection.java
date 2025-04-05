package com.alexzhelyapov1.dao;

// DatabaseConnection.java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:sqlite:projects.db");
        }
        return connection;
    }

    public static void initializeDatabase() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS projects (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "tags TEXT," +
                    "deadline DATE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS tracking (" +
                    "project_id INTEGER," +
                    "date DATE," +
                    "done BOOLEAN," +
                    "PRIMARY KEY (project_id, date)," +
                    "FOREIGN KEY (project_id) REFERENCES projects(id))");
        }
    }
}
