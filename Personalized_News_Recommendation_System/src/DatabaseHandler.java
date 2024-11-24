import java.sql.*;
import java.util.Map;

public class DatabaseHandler {

    private Connection conn;

    public DatabaseHandler(){
    }

    public void connect() {
        try {
            this.conn = DriverManager.getConnection("jdbc:sqlite:news_recommendation_system.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean authenticate(String username, String password){
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();
            // If a match is found, return true
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if no match is found or an error occurs
    }

    public void closeConnection(){
        try {
            this.conn.close();
            System.out.println("Database connection closed.");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean saveNewUser(User user) {
        String sql = "INSERT INTO Users (user_id, password, username, firstname, lastname, registration_date, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, user.getUserID());
            pst.setString(2, user.getPassword());
            pst.setString(3, user.getUsername());
            pst.setString(4, user.getFirstName());
            pst.setString(5, user.getLastName());
            pst.setDate(6, java.sql.Date.valueOf(user.getRegistrationDate()));
            pst.setString(7, user.getRole().toString());
            pst.executeUpdate();
            System.out.println("User added successfully!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean saveNewArticle(Article article) {
        String sql = "INSERT INTO Articles (title, content, category) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, article.getTitle());
            pstmt.setString(2, article.getContent());
            pstmt.setString(3, String.valueOf(article.getCategory()));

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Article added successfully!");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkUsernameAvailability(String username) {
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ? AND role = 'USER'";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // If the count is greater than 0, the username is already taken
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Return false in case of an error (indicates username is unavailable)
        return false;
    }

    public void removeDeletedUser(User user) {
        // SQL query to delete the user based on their unique identifier (e.g., user_id or username)
        String deleteSQL = "DELETE FROM Users WHERE user_id = ?";

        try (PreparedStatement pst = conn.prepareStatement(deleteSQL)) {

            // Set the user_id parameter
            pst.setInt(1, user.getUserID());

            // Execute the deletion
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User successfully deleted.");
            } else {
                System.out.println("No user found with the given ID.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error occurred while deleting the user: " + e.getMessage());
        }
    }



//    public boolean savePreferences(String userID, Map<Category, Integer> categories){
//        String sql = "INSERT INTO Preferences (userID, category, interest_level) VALUES (?, ?, ?)";
//        try (PreparedStatement pst = conn.prepareStatement(sql)){
//
//        }
//        catch (SQLException e){
//            e.printStackTrace();
//        }
//    }


}
