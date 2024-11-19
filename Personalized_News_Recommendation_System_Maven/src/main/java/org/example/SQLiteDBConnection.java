package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class SQLiteDBConnection {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:news_recommendation_system.db"; // SQLite DB file location
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                Statement stmt = conn.createStatement();
                String sql = "CREATE TABLE IF NOT EXISTS Articles (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "title TEXT NOT NULL," +
                        "content TEXT NOT NULL," +
                        "category TEXT NOT NULL);";
                stmt.execute(sql);
                System.out.println("Database and table created successfully.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
