import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class SQLiteTest1 {

    public static void main(String[] args) {
        // Define the SQLite database URL
        String url = "jdbc:sqlite:articles.db";

        // SQL query to create the articles table
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Articles ("
                + "article_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT NOT NULL, "
                + "content TEXT NOT NULL, "
                + "category TEXT NOT NULL)";

        // SQL query to insert two articles
        String insertSQL1 = "INSERT INTO Articles (title, content, category) VALUES (?, ?, ?)";
        String insertSQL2 = "INSERT INTO Articles (title, content, category) VALUES (?, ?, ?)";

        // SQL query to retrieve all articles
        String selectSQL = "SELECT article_id, title, content, category FROM Articles";

        // Connect to the SQLite database and perform operations
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                // Step 1: Create table
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createTableSQL);
                }

                // Step 2: Insert two articles
                try (PreparedStatement pstmt1 = conn.prepareStatement(insertSQL1);
                     PreparedStatement pstmt2 = conn.prepareStatement(insertSQL2)) {

                    pstmt1.setString(1, "Article 1 Title");
                    pstmt1.setString(2, "This is the content of the first article.");
                    pstmt1.setString(3, "Technology");
                    pstmt1.executeUpdate();

                    pstmt2.setString(1, "Article 2 Title");
                    pstmt2.setString(2, "This is the content of the second article.");
                    pstmt2.setString(3, "Health");
                    pstmt2.executeUpdate();
                }

                // Step 3: Retrieve and print all articles
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(selectSQL)) {

                    while (rs.next()) {
                        int id = rs.getInt("article_id");
                        String title = rs.getString("title");
                        String content = rs.getString("content");
                        String category = rs.getString("category");
                        System.out.println("Article ID: " + id);
                        System.out.println("Title: " + title);
                        System.out.println("Content: " + content);
                        System.out.println("Category: " + category);
                        System.out.println("------------------------------");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
