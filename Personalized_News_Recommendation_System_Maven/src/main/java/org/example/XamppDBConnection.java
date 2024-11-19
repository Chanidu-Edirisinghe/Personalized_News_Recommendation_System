package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class XamppDBConnection {

    public static void main(String[] args) {
        // Database connection details
        String url = "jdbc:mariadb://localhost:3306/news_recommendation_system"; // Replace with your database name
        String username = "root"; // Default username in XAMPP
        String password = ""; // Default password is empty in XAMPP unless changed

        // Establish connection
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Connected to MariaDB successfully!");

            // Example query
            String query = "SELECT * FROM Articles"; // Replace with your table name

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                // Process the result set
                while (rs.next()) {
                    System.out.println("Article Title: " + rs.getString("title"));
                    System.out.println("Article Content: " + rs.getString("content"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
