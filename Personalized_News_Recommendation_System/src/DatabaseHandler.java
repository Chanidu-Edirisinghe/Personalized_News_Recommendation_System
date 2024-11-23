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

    public boolean saveNewUser(SystemUser user) {
        String sql = "INSERT INTO Users (user_id, password, username, firstname, lastname, registration_date, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUserID());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getUsername());
            pstmt.setString(4, user.getFirstName());
            pstmt.setString(5, user.getLastName());
            pstmt.setDate(6, java.sql.Date.valueOf(user.getRegistrationDate()));
            pstmt.setString(7, user.getRole().toString());
            pstmt.executeUpdate();
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
