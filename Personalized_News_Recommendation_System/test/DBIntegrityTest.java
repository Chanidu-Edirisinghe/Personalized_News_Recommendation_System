import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.sql.*;

public class DBIntegrityTest {

    @Test
    void DatabaseIntegrityTest() {
        // Path to the SQLite database file
        String url = "jdbc:sqlite:news_recommendation_system.db"; // Replace with your database path

        // Attempt to connect to the database and perform an integrity check
        try (Connection conn = DriverManager.getConnection(url)) {
            // Check if the connection was established successfully
            if (conn != null) {
                // Create a statement to execute SQL queries
                Statement stmt = conn.createStatement();
                // Execute the PRAGMA integrity_check command to verify database integrity
                ResultSet rs = stmt.executeQuery("PRAGMA integrity_check;");

                // Check if a result was returned
                if (rs.next()) {
                    // Retrieve the result of the integrity check
                    String result = rs.getString(1);
                    // Assert that the result is "ok", indicating no integrity issues
                    Assertions.assertEquals("ok", result, "Database integrity check failed!");
                } else {
                    // Fail the test if no result was returned from the integrity check
                    Assertions.fail("No result returned from integrity check!");
                }
            } else {
                // Fail the test if the connection to the database could not be established
                Assertions.fail("Failed to connect to the database.");
            }
        } catch (SQLException e) {
            // Fail the test if an SQL exception occurred during the integrity check
            Assertions.fail("An error occurred during the integrity check: " + e.getMessage());
        }
    }

}
