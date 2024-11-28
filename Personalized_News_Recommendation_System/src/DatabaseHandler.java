import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private static final String url = "jdbc:sqlite:news_recommendation_system.db";

    public DatabaseHandler(){
    }

    private static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(url);
    }

//    public void connect() {
//        try {
//            this.conn = DriverManager.getConnection("jdbc:sqlite:news_recommendation_system.db");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

//    public void closeConnection(){
//        try {
//            this.conn.close();
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }


    public boolean login(String username, String password){
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
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

    public int register(String username, String password, String firstname, String lastname) {
        String sql = "INSERT INTO Users (username, password, firstname, lastname, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, firstname);
            pst.setString(4, lastname);
            pst.setString(5, "USER");

            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Return the generated user ID
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Indicate failure
    }


    public void savePreference(int userID, Preference preference){
        String sql = "INSERT INTO Preferences (user_id, category, interest_level) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)){
            pst.setInt(1, userID);
            pst.setString(2, String.valueOf(preference.getCategory()));
            pst.setInt(3, preference.getInterestLevel());
            pst.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void saveInteraction(Interaction interaction){
        String sql = "INSERT INTO Interactions (interaction_id, userID, article_id, " +
                "interaction_type, interaction_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)){
            pst.setInt(1, interaction.getInteraction_id());
            pst.setInt(2, interaction.getUser().getUserID());
            pst.setInt(3, interaction.getArticle().getArticleID());
            pst.setString(4, interaction.getInteractionType());
            pst.setString(5, String.valueOf(interaction.getInteractionDate()));
            pst.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        //        String createInteractionsTableSQL = "CREATE TABLE Interactions ("
//                + "interaction_id INTEGER PRIMARY KEY AUTOINCREMENT, "
//                + "user_id INTEGER NOT NULL, "
//                + "article_id INTEGER NOT NULL, "
//                + "interaction_type TEXT CHECK(interaction_type IN ('Read', 'Like', 'Skip')) NOT NULL, "
//                + "interaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
//                + "FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE, "
//                + "FOREIGN KEY (article_id) REFERENCES Articles(article_id) ON DELETE CASCADE"
//                + ");";
    }

    public boolean saveNewArticle(Article article) {
        String sql = "INSERT INTO Articles (title, content, category) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, article.getTitle());
            pst.setString(2, article.getContent());
            pst.setString(3, String.valueOf(article.getCategory()));

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Article added successfully!");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean saveUpdatedArticle(Article article){
        String sql = "UPDATE Articles SET title = ?, content = ? WHERE article_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)){
            pst.setString(1, article.getTitle());
            pst.setString(2, article.getContent());
            pst.setInt(3, article.getArticleID());
            return true;
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeDeletedArticle(int article_id){
        String sql = "DELETE FROM Articles WHERE article_id = ?";
        try(Connection conn = getConnection();
            PreparedStatement pst = conn.prepareStatement(sql)){
            pst.setInt(1, article_id);
            return true;
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkUsernameAvailability(String username) {
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ? AND role = 'USER'";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
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

    public void removeUser(int user_id) {
        // SQL query to delete the user based on their unique identifier (e.g., user_id or username)
        String deleteSQL = "DELETE FROM Users WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(deleteSQL)) {

            // Set the user_id parameter
            pst.setInt(1, user_id);

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

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

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

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

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

    public void updateUserDetails(User user){
        String sql = "UPDATE Users SET username = ?, password = ?, firstname = ?, lastname = ? " +
                "WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)){
            pst.setString(1, user.getUsername());
            pst.setString(2, user.getPassword());
            pst.setString(3, user.getFirstName());
            pst.setString(4, user.getLastName());
            pst.setInt(5, user.getUserID());
            pst.executeUpdate();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public List<List<String>> fetchFilteredArticles(Category category){
        List<List<String>> articleList = new ArrayList<>();
        String sql = "SELECT article_id, title, content FROM Articles WHERE category = ?";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                List<String> articleDetails = new ArrayList<>();
                articleDetails.add(rs.getString("article_id"));
                articleDetails.add(rs.getString("title"));
                articleDetails.add(rs.getString("content"));
                articleList.add(articleDetails);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articleList; // Return the list of user details
    }

    public void updatePreference(int user_id, Preference preference){
        String sql = "UPDATE Preferences SET interest_level = ? WHERE user_id = ? AND category = ?";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)){
            pst.setInt(1, preference.getInterestLevel());
            pst.setInt(2, user_id);
            pst.setString(3, preference.getCategory().toString());
            pst.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public List<List<String>> fetchRegisteredUsers(){
        List<List<String>> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password, firstname, lastname, role FROM Users";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                List<String> user = new ArrayList<>();
                user.add(rs.getString("user_id"));
                user.add(rs.getString("username"));
                user.add(rs.getString("password"));
                user.add(rs.getString("firstname"));
                user.add(rs.getString("lastname"));
                user.add(rs.getString("role"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }


}
