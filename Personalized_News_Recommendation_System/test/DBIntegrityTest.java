import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.sql.*;

public class DBIntegrityTest {

    @Test
    void DatabaseIntegrityTest() {
        String url = "jdbc:sqlite:news_recommendation_system.db"; // Replace with your database path

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("PRAGMA integrity_check;");

                if (rs.next()) {
                    String result = rs.getString(1);
                    Assertions.assertEquals("ok", result, "Database integrity check failed!");
                } else {
                    Assertions.fail("No result returned from integrity check!");
                }
            } else {
                Assertions.fail("Failed to connect to the database.");
            }
        } catch (SQLException e) {
            Assertions.fail("An error occurred during the integrity check: " + e.getMessage());
        }
    }
}
