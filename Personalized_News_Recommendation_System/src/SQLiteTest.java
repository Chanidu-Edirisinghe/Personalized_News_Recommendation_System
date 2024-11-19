import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class SQLiteTest {
    public static void main(String[] args) {
        // Define the database URL (SQLite will create the file if it doesn't exist)
        String url = "jdbc:sqlite:sample.db";

        // Connect to the database
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("Connection to SQLite has been established.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
