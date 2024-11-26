import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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

    public void closeConnection(){
        try {
            this.conn.close();
        }
        catch (SQLException e) {
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


    public boolean saveNewUser(User user) {
        String sql = "INSERT INTO Users (user_id, password, username, firstname, lastname, registration_date, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
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


    public boolean savePreferences(String userID, List<Preference> preferences){
        String sql = "INSERT INTO Preferences (userID, category, interest_level) VALUES (?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)){
            for(int i = 0; i < preferences.size(); i++) {
                pst.setString(1, userID);
                pst.setString(2, String.valueOf(preferences.get(i).getCategory()));
                pst.setInt(3, preferences.get(i).getInterestLevel());
                pst.executeUpdate();
            }
            return true;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

//    public boolean saveInteraction(Interaction interaction){
////        String createInteractionsTableSQL = "CREATE TABLE Interactions ("
////                + "interaction_id INTEGER PRIMARY KEY AUTOINCREMENT, "
////                + "user_id INTEGER NOT NULL, "
////                + "article_id INTEGER NOT NULL, "
////                + "interaction_type TEXT CHECK(interaction_type IN ('Read', 'Like', 'Skip')) NOT NULL, "
////                + "interaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
////                + "FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE, "
////                + "FOREIGN KEY (article_id) REFERENCES Articles(article_id) ON DELETE CASCADE"
////                + ");";
//    }

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

//    public boolean saveUpdatedArticle(){
//
//    }
//
//    public boolean removeDeletedArticle(){
//
//    }

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

    public void removeUser(User user) {
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

    public List<String> getUserDetails(String username) {
        List<String> userDetailsList = new ArrayList<>();
        String sql = "SELECT user_id, username, password, firstname, lastname, registration_date, role FROM Users WHERE username LIKE ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, username); // Support partial matches
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                userDetailsList.add(rs.getString("user_id"));
                userDetailsList.add(rs.getString("username"));
                userDetailsList.add(rs.getString("password"));
                userDetailsList.add(rs.getString("firstname"));
                userDetailsList.add(rs.getString("lastname"));
                userDetailsList.add(rs.getString("registration_date"));
                userDetailsList.add(rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userDetailsList; // Return the list of user details
    }

    public List<List<String>> fetchArticles() {
        List<List<String>> articleList = new ArrayList<>();
        String sql = "SELECT article_id, title FROM Articles";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                List<String> articleDetails = new ArrayList<>();
                articleDetails.add(rs.getString("article_id"));
                articleDetails.add(rs.getString("title"));
                // Add the single user's details to the main list
                articleList.add(articleDetails);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articleList; // Return the list of user details
    }

    public void getUserDetails(){

    }

    public void updateUserDetails(){

    }

    public void fetchFilteredArticles(){

    }

    public void updatePreferences(){

    }





}
